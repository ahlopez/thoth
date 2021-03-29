package com.f.thoth.backend.data.gdoc.numerator;


/**
 * Consumidor de comandos registrados en un buffer de comunicaciones
 */
final class PersistConsumer implements Runnable
{
   private CommBuffer<Instruction>  buff;   // Buffer de comunicacion entre productor y consumidor
   private int                      number; // Identificador del hilo

   /**
    * Construye una instancia de un hilo consumidor de comandos
    * @param cb   Buffer de communicaci�n entre el productor de comandos y este consumidor
    * @param number  Identificador del hilo
    */
   public PersistConsumer( CommBuffer<Instruction> cb, int number)
   {
      if (cb == null)
         throw new IllegalArgumentException("Buffer de comunicación no puede ser nulo");

      if (number <= 0)
         throw new IllegalArgumentException("Número del hilo["+ number+ "] debe ser positivo");

      buff           = cb;
      this.number    = number;
   }// PersistConsumer


   /**
    * Obtenga y ejecute los comandos registrados en
    * el buffer de comunicaciones.
    */
   public void run()
   {
      while (true)
      {
         Instruction command = (Instruction)buff.get(this.number);
         System.out.println(" Thread["+ number+ "] instruction["+ command.toString()+ "]");
         command.execute();
      }
   }//run
}//PersistConsumer
