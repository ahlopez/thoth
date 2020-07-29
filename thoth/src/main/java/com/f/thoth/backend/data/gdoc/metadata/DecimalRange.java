package com.f.thoth.backend.data.gdoc.metadata;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa un rango de valores decimales
 */
public class DecimalRange implements Range<BigDecimal>
{
   private BigDecimal minValue;
   private BigDecimal maxValue;

   // ------------- Constructors ------------------
   public DecimalRange()
   {
   }

   public DecimalRange( BigDecimal minValue, BigDecimal maxValue)
   {
      if (minValue ==  null || maxValue == null)
         throw new IllegalArgumentException("Valores m�nimo y m�ximo del rango no pueden ser nulos");

      if ( minValue.compareTo( maxValue) > 0)
         throw new IllegalArgumentException("Valor m�nimo no puede ser mayor que valor m�ximo");

      this.minValue   = minValue;
      this.maxValue   = maxValue;
   }//DecimalRange
   
   
   public DecimalRange( String range)
   {
	   //TODO: DecimalRange( String range) constructor
   }


   // -------------- Getters & Setters ----------------

   public BigDecimal  getMinValue(){ return minValue;}
   public void        setMinValue( BigDecimal minValue){ this.minValue = minValue;}

   public BigDecimal  getMaxValue(){ return maxValue;}
   public void        setMaxValue( BigDecimal maxValue){ this.maxValue = maxValue;}


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

      DecimalRange that = (DecimalRange) o;

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
      return "DecimalRange{ minValue["+ minValue+ "] maxValue["+ maxValue+ "]}";
   }//toString


   // --------------- Logic ------------------------------

   public boolean in(BigDecimal value)
   {
       return value != null &&
              value.compareTo( minValue) >= 0  && 
              value.compareTo( maxValue) <= 0;

   }//in
}//DecimalRange