package com.f.thoth.backend.data.gdoc.numerator;


/**
 * Representa el buffer de comunicacion entre dos grupos de hilos
 * un grupo productor y otro consumidor.
 * El buffer esta configurado para almacenar instrucciones
 */
final class CommBuffer<E extends Instruction>
{
   private E[] B;               // Buffer circular
   private int P_in;            // Los productores escribiran aquí
   private int P_out;           // El ultimo consumido por los consumidores
   private final int Buff_size; // Tamaño del buffer
   private boolean stop;        // Si true, pare la comunicacion cuando el buffer está vacio
                                // Tambien para a los productores de incluir elementos en el buffer


   // ---------------------------------------------------------------------------
   /**
    * Construye un buffer de comunicaciones
    * @param tamano Número de elementos en el buffer de comunicaciones
    */
   @SuppressWarnings("unchecked")
   public CommBuffer(final int tamano)
   {
      if ( tamano <= 2 )
         throw new IllegalArgumentException("Tamaño de buffer inválido["+ tamano+ "]");

      B         = (E[])new Instruction[ tamano];
      P_in      = 0;
      P_out     = tamano - 1;
      Buff_size = tamano;
      stop      = false;
   }//CommBuffer constructor


   /**
    * Obtiene el siguiente objeto de comunicacion.
    * La comunicacion continúa hasta cuando se ordene
    * parar la comunicacion o el buffer de comunicacion está vacio
    * @param consumer Identificacion del consumidor
    * @return Siguiente objeto de la comunicacion.
    */
   public synchronized E get(final int consumer)
   {
      while ( (P_out+1) % Buff_size == P_in )
      {
         // Buffer vacio Espere a que se escriba algo
         try
         {
            wait();
         } catch ( InterruptedException e )
         { }
      }

      // Finalmente hay algo que consumir
      P_out    = (++P_out) % Buff_size;
      E item   = B[P_out];
      B[P_out] = null;
      notifyAll();
      return item;

   }//get




   /**
    * Coloca un nuevo elemento en el buffer de comunicaciones
    * @param item Nuevo elemento a ser colocado en el buffer
    * @return true si acepto el objeto y lo coloco en el buffer;  false si no acepto objeto
    */
   public synchronized boolean put(final E item)
   {
      if ( stop )
         return false;

      // Combine las instrucciones compatibles
      for ( int k = (P_in > 0 ? P_in-1 : Buff_size-1); k > P_out; k = (k > 0 ? k-1 :Buff_size-1) )
      {
         if ( B[k].merge( (Instruction)item) )
            return true;
      }

      while ( P_in == P_out )
      {
         // Buffer lleno. Espere a que se consuma algo
         try
         {
            wait();
         } catch ( InterruptedException e )
         {
         }
      }

      // Hay espacio. Incluya el ítem y notifique a los consumidores
      B[P_in] = item;
      P_in    = (++P_in) % Buff_size;
      notifyAll();
      return true;

   }//put


   /**
    * Ordena la terminacion de la comunicacion
    * una vez que el buffer esta vacio
    */
   public synchronized void stop()
   {
      stop = true;
   }//stop

   /**
    * Evacua los elementos existentes en el buffer
    */
   public synchronized void flush()
   {
      // Impida que sigan incluyendo elementos en el buffer
      stop = true;

      while ( (P_out+1) % Buff_size != P_in )
      {
         // Bloquee mientras se vacia el buffer
         try
         {
            wait();
         } catch ( InterruptedException e )
         {
         }
      }

      // Finalmente el buffer está vacío Reanude la comunicacion
      stop = false;
      notifyAll();

   }//flush

}//CommBuffer
