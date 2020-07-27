package com.f.thoth.backend.data.gdoc.metadata;

import java.util.Iterator;
import java.util.Set;

/**
 * Representa un rango de valores de enumeraci�n
 */
public class EnumRange implements Range
{
   private Set<String> values;

   // ------------- Constructors ------------------
   public EnumRange()
   {
   }

   public EnumRange( Set<String> values)
   {
      if( values == null)
         throw new IllegalArgumentException("Rango de valores no puede ser nulo");

      this.values = values;
   }

   // -------------- Getters & Setters ----------------

   public Set<String> getValues(){ return values;}
   public void        setValues(Set<String> values) { this.values = values;}


   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      if (!super.equals(o))
         return false;

      EnumRange that = (EnumRange) o;

      if( this.values.size() != that.values.size())
         return false;

      Iterator<String> thatValues = that.values.iterator();
      for( String v : values)
      {
         if( ! v.equals( thatValues.next()))
             return false;
      }
      return  true;

   }// equals

   @Override
   public int hashCode()
   {
      return values.hashCode();
   }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "EnumRange{");
      for(String v: values)
      {
         s.append( v+ " ");
      }
      s.append("}");
      return s.toString();
   }//toString

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
      return value != null && value instanceof String && values.contains( value);
   }

}//EnumRange