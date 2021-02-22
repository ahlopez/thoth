package com.f.thoth.backend.data.gdoc.numerator;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.f.thoth.backend.data.entity.util.TextUtil;

import db.EmFactory;


/**
 * Representa la acción de avanzar una secuencia
 */
public class AdvanceSequence implements Instruction
{
   /**
    * nombre     - Nombre externo de la secuencia
    * prefijo    - Prefijo del número
    * sufijo     - Sufijo del número
    * valor      - Valor actual de la secuencia
    * emf        - Entity manager factory
    * MAX_TRIES  - Número de intentos para conseguir la secuencia antes de reportar falla
    */
   private String nombre;
   private String prefijo;
   private String sufijo;
   private long   valor;
   private static EmFactory emf;

   /**
    * Constantes
    * MAX_TRIES   -  Máximo número de pruebas de espera para encontrar el registro persistente de la secuencia
    * TIMEOUT     -  Tiempo de espera entre pruebas, cuando el avance es anterior a la creación del registro
    */
   private static final int  MAX_TRIES = 30;
   private static final long TIMEOUT   = 700;


   /**
    * Obtiene un comando para actualizar una secuencia
    * @param nombre Identificador externo de la secuencia
    * @param prefijo Prefijo del número
    * @param sufijo  Sufijo del número
    * @param valor Nuevo valor de la secuencia
    */
   public AdvanceSequence(String nombre, String prefijo, String sufijo, long valor)
   {
      reset(nombre, prefijo, sufijo, valor);

   }//AdvanceSequence

   /**
    * Inicializa un comando para actualizar una secuencia
    * @param nombre Identificador externo de la secuencia
    * @param prefijo Prefijo del número
    * @param sufijo  Sufijo del número
    * @param valor Valor actual de la secuencia
    */
   public void reset(String nombre, String prefijo, String sufijo, long valor)
   {
      if (TextUtil.isEmpty(nombre))
         throw new IllegalArgumentException("Identificador de la secuencia no puede ser nulo ni vacío");

      if (valor <= 0)
         throw new IllegalArgumentException("Valor de la secuencia debe ser un número entero positivo");

      this.nombre   = nombre;
      this.prefijo  = prefijo == null? "": prefijo;
      this.sufijo   = sufijo  == null? "": sufijo;
      this.valor    = valor;
      emf           = EmFactory.getInstance();

   }//reset

   /**
    * Actualiza la secuencia con su nuevo valor
    */
   @SuppressWarnings("unchecked")
   public void execute()
   {
      EntityManager     em = emf.getManager();
      EntityTransaction tx = null;
      try
      {
         tx = em.getTransaction();
         tx.begin();
         Query query = em.createNamedQuery("selectSequence" );
         query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
         query.setParameter("nombre",  nombre);
         query.setParameter("prefijo", prefijo);
         query.setParameter("sufijo",  sufijo);

         List<DBSequence> sequences = (List<DBSequence>) query.getResultList();
         for (int i = 0; sequences.size() == 0 && i < MAX_TRIES; i++)
         {
            Thread.currentThread().wait( TIMEOUT);
            sequences = (List<DBSequence>) query.getResultList();
         }

         DBSequence sequence = sequences.get(0);
         if ( !sequence.isActive())
            throw new IllegalStateException("Secuencia ["+ nombre+ ":"+ prefijo+ ":"+ sufijo+ "] no está activa");

         if ( sequence.isLower(valor))
         {
            em.merge(sequence);
            sequence.setValue(valor);
         }else
            System.out.println(">>> Secuencia con valor menor que "+ valor);
         tx.commit();
      }
      catch ( Throwable t)
      {
         throw new IllegalStateException("\nNo pudo avanzar secuencia["+ nombre+ "] "+
                                         "prefijo["+ prefijo+ "] valor["+ valor+ "] sufijo["+ sufijo+ "].Razón\n"+ t);
      }finally
      {
         if (tx != null && tx.isActive())
            em.getTransaction().rollback();

         em.close();
      }

   }//execute



   /**
    * Combina instrucciones en una sola, si es posible
    * @param  other La instrucción que será combinada con esta
    * @return boolean true si esta instruccion se actualizó con la presentada
    */
   public boolean merge ( Instruction other)
   {
      boolean merged = false;
      if ( other instanceof AdvanceSequence )
      {

         AdvanceSequence that = (AdvanceSequence) other;
         if ( this.nombre.equals ( that.nombre)  &&
              this.prefijo.equals( that.prefijo) &&
              this.sufijo.equals ( that.sufijo))
         {
            synchronized(this)
            {
               merged = (this.valor < that.valor);
               if (merged)
               {
                 this.valor = that.valor;
       //          System.out.println(">>> N merged["+ nombre+ "]="+ ++nMerged);
               }
            }
         }
      }
      return merged;
   }// merge


   /**
    * Obtiene el identificador de negocio para la secuencia
    * @return Id solicitado
    */
   public String fullName()
   {
      return nombre+ "::"+ prefijo+ "::"+ sufijo;
   }// fullName

}//AdvanceSequence
