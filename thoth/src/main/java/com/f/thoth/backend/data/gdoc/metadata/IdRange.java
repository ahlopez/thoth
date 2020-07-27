package com.f.thoth.backend.data.gdoc.metadata;

/**
 * Representa un rango de valores de Identificadores
 */
public class IdRange implements Range
{
   public static int   MAX_LENGTH = 255;

   private int     minLength  = 0;
   private int     maxLength  = 255;


   // ------------- Constructors ------------------
   public IdRange()
   {
   }

   public IdRange( int minLength, int maxLength)
   {
      if ( minLength > maxLength)
         throw new IllegalArgumentException("Longitud m�nimma no puede ser mayor que longitud m�xima");

      if (minLength < 0 || maxLength > MAX_LENGTH)
         throw new IllegalArgumentException("Longitud m�nima o m�xima inv�lidas");

      this.minLength   = minLength;
      this.maxLength   = maxLength;

   }//IdRange

   // -------------- Getters & Setters ----------------

   public int     getMinLength(){ return minLength;}
   public void    setMinLength( int minLength){ this.minLength = minLength;}

   public int     getMaxLength(){ return maxLength;}
   public void    setMaxLength( int maxLength){ this.maxLength = maxLength;}

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

      IdRange that = (IdRange) o;

      return  this.minLength == that.minLength && this.maxLength == that.maxLength;

   }// equals

   @Override
   public int hashCode() { return minLength * 1023 + maxLength; }

   @Override
   public String toString()
   {
      return "IdRange{ minLength["+ minLength+ "] maxLength["+ maxLength+ "]}";
   }//toString

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
      if (value == null || !(value instanceof String))
         return false;

      String val = ((String)value);
      return val.length() >= minLength && val.length() <= maxLength;
   }//val

}//IdRange