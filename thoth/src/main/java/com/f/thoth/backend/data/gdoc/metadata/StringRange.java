package com.f.thoth.backend.data.gdoc.metadata;

import java.util.Objects;

/**
 * Representa un rango de valores de Strings
 */
public class StringRange implements Range
{
   public static int   MAX_LENGTH = 500000;

   private int     minLength  = 0;
   private int     maxLength  = 255;
   private boolean canBeEmpty = true;


   // ------------- Constructors ------------------
   public StringRange()
   {
   }

   public StringRange( int minLength, int maxLength, boolean canBeEmpty)
   {
      if ( minLength > maxLength)
         throw new IllegalArgumentException("Longitud minimma no puede ser mayor que longitud maxima");

      if ( minLength < 0 || maxLength > MAX_LENGTH)
         throw new IllegalArgumentException("Longitud m�nima o m�xima inv�lidas");

      this.minLength   = minLength;
      this.maxLength   = maxLength;
      this.canBeEmpty  = canBeEmpty;

   }//StringRange

   // -------------- Getters & Setters ----------------

   public int     getMinLength(){ return minLength;}
   public void    setMinLength( int minLength){ this.minLength = minLength;}

   public int     getMaxLength(){ return maxLength;}
   public void    setMaxLength( int maxLength){ this.maxLength = maxLength;}

   public boolean getcanBeEmpty() { return this.canBeEmpty;}
   public void    setcanBeEmpty( boolean canBeEmpty){ this.canBeEmpty = canBeEmpty;}

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

      StringRange that = (StringRange) o;

      return  this.minLength == that.minLength && this.maxLength == that.maxLength && this.canBeEmpty == that.canBeEmpty;

   }// equals

   @Override
   public int hashCode()
   {
      return Objects.hash( minLength, maxLength, canBeEmpty);
   }

   @Override
   public String toString()
   {
      return "StringRange{ minLength["+ minLength+ "] maxLength["+ maxLength+ "] canBeEmpty["+ canBeEmpty+ "]}";
   }//toString

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
      if (value == null || !(value instanceof String))
         return false;

      String val = ((String)value).trim();
      if (!canBeEmpty)
      {
         val = ((String)value).trim();
         if (val.equals(""))
             return false;
      }
      return val.length() >= minLength && val.length() <= maxLength;

   }//in


}//StringRange