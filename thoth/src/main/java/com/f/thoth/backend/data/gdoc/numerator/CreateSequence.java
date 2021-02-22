package com.f.thoth.backend.data.gdoc.numerator;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.f.thoth.backend.data.security.Tenant;

import db.EmFactory;

/**
 * Representa la acción de crear una secuencia
 */
class CreateSequence implements Instruction
{
   /**
    * sequence - La secuencia a crear
    * emf      - Entity manager factory
    */
   private DBSequence sequence;
   private static EmFactory emf;

   /**
    * Obtiene un comando para crear una secuencia
    *
    * @param nombre Identificador externo de la secuencia
    * @param prefijo Prefijo del número
    * @param sufijo  Sufijo del número
    * @param inicial Valor inicial de la secuencia
    * @param incremento Delta entre dos números consecutivos de la secuencia
    * @param longitud Longitud del secuencial
    */
   public CreateSequence(Tenant tenant,  final String nombre, final String prefijo, final String sufijo, final long inicial, final long incremento, final int longitud)
   {
      assert nombre != null && nombre.trim().length() > 0;
      assert prefijo != null;
      assert sufijo  != null;
      assert inicial >= 0;
      assert incremento > 0;
      assert longitud   > 0;

      emf = EmFactory.getInstance();
      sequence = new DBSequence(nombre, prefijo, sufijo, inicial, incremento, DBSequence.Status.ACTIVA, longitud);

   }//CreateSequence

   /**
    * Aquí crear o actualizar la secuencia en su medio externo
    */
   public void execute()
   {
      EntityManager     em = emf.getManager();
      EntityTransaction tx = null;
      try
      {
         tx = em.getTransaction();
         tx.begin();
         em.persist(sequence);
         tx.commit();
      }catch( Throwable t)
      {
         throw new IllegalStateException("\nNo pudo crear secuencia "+ sequence+ ".Razón\n"+ t);
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
         merged = this.sequence.esIgual( that.fullName());
         if ( merged )
         {
            this.sequence.advance();
            System.out.println("Merge create-add"+ sequence.getNombre()+ " valor="+ this.sequence.getValue());
         }
      }
      return merged;
   }// merge

}//CreateSequence
