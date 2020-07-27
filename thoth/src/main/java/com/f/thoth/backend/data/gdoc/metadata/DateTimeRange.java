package com.f.thoth.backend.data.gdoc.metadata;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un rango de valores de Fecha-Hora
 */
public class DateTimeRange implements Range
{
   private LocalDateTime minTime;
   private LocalDateTime maxTime;

   // ------------- Constructors ------------------
   public DateTimeRange()
   {
   }

   public DateTimeRange( LocalDateTime minTime, LocalDateTime maxTime)
   {
      if (minTime ==  null || maxTime == null)
         throw new IllegalArgumentException("Valores m�nimo y m�ximo del rango no pueden ser nulos");

      if ( minTime.isAfter( maxTime))
         throw new IllegalArgumentException("Fecha-hora m�nima no puede ser mayor que Fecha-hora m�xima");

      this.minTime   = minTime;
      this.maxTime   = maxTime;

   }//DateTimeRange

   // -------------- Getters & Setters ----------------

   public LocalDateTime getMinTime(){ return minTime;}
   public void          setMinTime( LocalDateTime minTime){ this.minTime = minTime;}

   public LocalDateTime getMaxTime(){ return maxTime;}
   public void          setMaxTime( LocalDateTime maxTime){ this.maxTime = maxTime;}

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

      DateTimeRange that = (DateTimeRange) o;

      return  this.minTime.equals(that.minTime) && this.maxTime.equals(that.maxTime);

   }// equals

   @Override
   public int hashCode() { return  Objects.hash(minTime,maxTime); }

   @Override
   public String toString()
   {
      return "DateTimeRange{ minTime["+ minTime+ "] maxTime["+ maxTime+ "]}";
   }//toString

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
      if (value == null ||  !(value instanceof LocalDateTime))
         return false;

      LocalDateTime that = (LocalDateTime) value;
      return (that.isEqual(minTime) || that.isAfter(minTime))  &&
             (that.isEqual(maxTime) || that.isBefore(maxTime));

   }//in


}//DateTimeRange