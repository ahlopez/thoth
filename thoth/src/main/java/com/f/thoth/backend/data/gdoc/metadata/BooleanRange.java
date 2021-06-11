package com.f.thoth.backend.data.gdoc.metadata;

/**
 * Representa un rango de valores booleano
 */
public class BooleanRange implements Range<Boolean>
{
   // ------------- Constructors ------------------
   public BooleanRange()
   {
   }

   // --------------- Logic ------------------------------

   public boolean in(Boolean value)
   {
      return  value != null && (value.equals(Boolean.TRUE) || value.equals(Boolean.FALSE));

   }//in

}//BooleanRange