package com.f.thoth.backend.data.gdoc.metadata;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Representa un rango de valores enteros
 */
public class IntegerRange implements Range<BigInteger>
{
   private BigInteger minValue;
   private BigInteger maxValue;

   // ------------- Constructors ------------------
   public IntegerRange()
   {
   }

   public IntegerRange( BigInteger minValue, BigInteger maxValue)
   {
      if (minValue ==  null || maxValue == null)
         throw new IllegalArgumentException("Valores minimo y maximo del rango no pueden ser nulos");

      if ( minValue.compareTo( maxValue) > 0)
         throw new IllegalArgumentException("Valor m�nimo no puede ser mayor que valor m�ximo");

      this.minValue   = minValue;
      this.maxValue   = maxValue;
   }//IntegerRange

	public IntegerRange( String range)
	{
		//TODO: IntegerRange( String range) constructor
	}


   // -------------- Getters & Setters ----------------

   public BigInteger  getMinValue(){ return minValue;}
   public void        setMinValue( BigInteger minValue){ this.minValue = minValue;}

   public BigInteger  getMaxValue(){ return maxValue;}
   public void        setMaxValue( BigInteger maxValue){ this.maxValue = maxValue;}


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

      IntegerRange that = (IntegerRange) o;

      return  this.minValue.equals(that.minValue) && this.maxValue.equals(that.maxValue);

   }// equals

   @Override
   public int hashCode()
   {
      return Objects.hash( minValue, maxValue);
   }

   @Override
   public String toString()
   {
      return "IntegerRange{ minValue["+ minValue+ "] maxValue["+ maxValue+ "]}";
   }//toString


   // --------------- Logic ------------------------------

   public boolean in(BigInteger value)
   {
       return value != null &&
              value.compareTo( minValue) >= 0  && 
              value.compareTo( maxValue) <= 0;

   }//in

}//IntegerRange