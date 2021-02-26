package com.f.thoth.backend.data.gdoc.numerator;

import java.util.Observable;
import java.util.Observer;


/**
 * Singleton que implementa la persistencia de las secuencias
 */
public class PersistProducer implements Observer
{
   /**
    *  N_THREADS     - Numero de hilos que ejecutan la persistencia
    *  MAX_QUEUE     - Maximo tamano del buffer de comunicacion
    *  commandBuffer - Acciones sobre la imagen persistente de la secuencia
    *  INSTANCE      - La N instancia del singleton
    */
   private static final int N_THREADS = 3;
   private static final int MAX_QUEUE = 500;
   private static CommBuffer<Instruction> commandBuffer = null;
   private static PersistProducer              INSTANCE = null;

   /** Obtiene una instancia del encargado de persistir las secuencias */
   private PersistProducer( )
   {
      commandBuffer =  new CommBuffer<>(MAX_QUEUE);
      for ( int hilo = 1; hilo <= N_THREADS; hilo++)
      {
          Thread persist = new Thread( new PersistConsumer( commandBuffer, hilo));
          persist.start();
      }
      INSTANCE   = this;
   }//PersistProducer

   /**
    * Obtiene una instancia del productor
    * @return PersistProducer La instancia solicitada del productor
    */
   public static PersistProducer   getInstance()
   {
      if( INSTANCE == null )
         INSTANCE = new PersistProducer();

      return INSTANCE;
   }//getInstance

   /**
    * Crea o actualiza una secuencia
    * @param o La secuencia siendo observada
    * @param arg  Comando que identifica la naturaleza del cambio en la secuencia
    */
   public void update(Observable obs, Object arg)
   {
      commandBuffer.put( (Instruction)arg);
   }//update


}//PersistProducer
