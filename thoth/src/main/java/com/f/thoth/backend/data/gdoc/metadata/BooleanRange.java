package com.f.thoth.backend.data.gdoc.metadata;

/**
 * Representa un rango de valores booleano
 */
public class BooleanRange implements Range
{
   // ------------- Constructors ------------------
   public BooleanRange()
   {
   }

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
      if (value == null ||  !(value instanceof Boolean))
         return false;

      Boolean that = (Boolean) value;
      return that.equals(Boolean.TRUE) || that.equals(Boolean.FALSE);

   }//in

}//BooleanRange