package com.f.thoth.backend.data.gdoc.numerator;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;

import org.h2.command.ddl.CreateSequence;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;

public class Sequence extends Observable
{
      public enum Status { OPEN, CLOSED};

      protected Tenant      tenant;                   // Tenant dueño de la secuencia
      protected String      nombre;                   // Nombre de la secuencia
      protected String      prefijo;                  // Prefijo del numero
      protected String      sufijo;                   // Sufijo  del numero
      protected AtomicLong  value;                    // Valor actual de la secuencia
      protected long        increment;                // Delta entre dos números consecutivos de la secuencia
      protected int         longitud;                 // Longitud obligatoria del secuencial. Asegurarla con padding de ceros por la izquierda
      protected Status      status;                   // Estado de la secuencia OPEN/CLOSED

      /** persistor - Observador de persistencia de la secuencia */
      private static final Observer persistor   =  PersistProducer.getInstance();


      // ================================================================================
      // Creación e inicialización
      //

      /** Obtiene una instancia vacía de la secuencia */
      public Sequence()
      {
         this.nombre = null;
      }// Sequence

      /**
       *  Crea una secuencia de numeración con valor inicial e incremento especificados
       *  @param nombre Identificador externo de la secuencia
       *  @param prefijo Prefijo del número
       *  @param sufijo  Sufijo del número
       *  @param initialValue El valor inferior del rango
       *  @param increment Delta entre dos números consecutivos de la secuencia
       *  @param longitud Longitud del secuencial
       */
      public Sequence( Tenant tenant, String nombre, String prefijo, String sufijo, final long initialValue, final long increment, int longitud)
      {
         assert TextUtil.isNotEmpty( nombre);
         assert prefijo != null;
         assert sufijo  != null;
         assert initialValue >= 0L;
         assert increment > 0L;
         assert longitud > 0;

         this.tenant     = tenant;
         this.nombre     = nombre;
         this.prefijo    = prefijo;
         this.sufijo     = sufijo;
         this.value      = new AtomicLong( initialValue);
         this.increment  = increment;
         this.status     = Status.OPEN;
         this.longitud   = longitud;

         addObserver( persistor);
         setChanged( );
         notifyObservers( new CreateSequence( this.tenant, this.nombre, this.prefijo, this.sufijo,  value.get(), increment, longitud));

      }//Sequence

      // ----------------------- Logica ---------------------------

      public  boolean     isEmpty(){return nombre == null;}

      /**
       * Retorna el siguiente valor de la secuencia.
       * En la primera llamada retorna el valor inicial de la secuencia
       * @return long Siguiente valor de la secuencia.  Thread-safe.
       */
      public synchronized String next()
      {
         if( status != Status.OPEN )
            throw new IllegalStateException("Secuencia ["+ nombre+ ","+ prefijo+ ","+ sufijo+ "] está cerrada. No puede avanzar");

         long n =  value.addAndGet(increment);
         setChanged( );
         notifyObservers( new AdvanceSequence( nombre, prefijo, sufijo, n));
         return prefijo+ TextUtil.pad(n, longitud)+ sufijo;

      }//next


      /**
       *  Obtiene el valor actual de la secuencia
       *  @return Valor actual de la secuencia
       */
      public String get() { return prefijo+ value.get()+ sufijo;}



      /** Cierra la secuencia e impide su avance posterior */
      public synchronized void close()
      {
         if( status == Status.OPEN )
         {
            status = Status.CLOSED;
            setChanged();
            notifyObservers( new CloseSequence( nombre, prefijo, sufijo));
            deleteObserver(persistor);
         }

      }//close

   }//Sequence
