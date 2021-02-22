package com.f.thoth.backend.data.gdoc.numerator;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import db.EmFactory;

/**
 * Representa la acción de cerrar una secuencia
 */
class CloseSequence implements Instruction
{
   /**
    * nombre     - Nombre externo de la secuencia
    * prefijo    - Prefijo del número
    * sufijo     - Sufijo del número
    * emf        - Entity manager factory
    */
   private final  String    nombre;
   private final  String    prefijo;
   private final  String    sufijo;
   private static EmFactory emf;

   /**
    * Obtiene un comando para cerrar una secuencia
    * @param nombre Identificador externo de la secuencia
    * @param prefijo Prefijo del número
    * @param sufijo  Sufijo del número
    */
   public CloseSequence(  String nombre, String prefijo, String sufijo)
   {
      assert nombre  != null && nombre.trim().length() > 0;
      assert prefijo != null;
      assert sufijo  != null;

      this.nombre   = nombre;
      this.prefijo  = prefijo;
      this.sufijo   = sufijo;
      emf           = EmFactory.getInstance();

   }//CreateSequence

   /**
    * Aquí cerrar la secuencia en su medio externo
    */
   public void execute()
   {
      EntityManager     em = emf.getManager();
      EntityTransaction tx = null;
      try
      {
         tx = em.getTransaction();
         Query query = em.createNamedQuery("selectSequence");
         query.setParameter("nombre",  nombre);
         query.setParameter("prefijo", prefijo);
         query.setParameter("sufijo",  sufijo);
         DBSequence sequence = (DBSequence) query.getSingleResult();

         tx.begin();
         em.merge(sequence);
         sequence.setActive(DBSequence.Status.CERRADA);
         tx.commit();
      }catch( Throwable t)
      {
         throw new IllegalStateException("No pudo cerrar secuencia["+ nombre+ "] "+
               "prefijo["+ prefijo+ "] sufijo["+ sufijo+ "].Razón\n"+ t);
      }finally
      {
         if ( tx != null && tx.isActive())
            tx.rollback();

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
      return false;
   }// merge

}//CloseSequence
