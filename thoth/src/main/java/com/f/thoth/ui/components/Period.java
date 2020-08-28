package com.f.thoth.ui.components;

import java.time.LocalDate;

import com.f.thoth.ui.utils.FormattingUtils;

public class Period implements Comparable<Period>
{
   private LocalDate  fromDate;
   private LocalDate  toDate;
   
   public Period()
   {
      LocalDate now = LocalDate.now();
      this.fromDate = now;
      this.toDate   = now;      
   }//Period
   
   public Period( LocalDate fromDate, LocalDate toDate)
   {
      this.fromDate = fromDate;
      this.toDate   = toDate;
   }//Period
   
   
   // -------------  Getters & Setters ---------------
   public LocalDate getFromDate(){ return fromDate; }
   public void      setFromDate(LocalDate fromDate){ this.fromDate = fromDate; }
   
   public LocalDate getToDate() { return toDate; }
   public void      setToDate(LocalDate toDate) { this.toDate = toDate; }
   
   // -------------  Object ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Period ))
         return false;

      Period that = (Period) o;
      return this.fromDate.equals(that.fromDate) && this.toDate.equals(that.toDate);

   }//equals

   @Override public int hashCode() { return fromDate.hashCode()* 1027 + toDate.hashCode();}

   @Override public String toString()
   {
       return " Period{"+ fromDate.format(FormattingUtils.FULL_DATE_FORMATTER)+ " : "+ toDate.format(FormattingUtils.FULL_DATE_FORMATTER);
   }//toString

   @Override public int compareTo(Period that)
   {
      return this.equals(that)?  0 :
             that ==  null    ?  1 :
         this.toDate.isBefore(that.fromDate)?  -1 :
         this.fromDate.isAfter(that.toDate) ?   1 :
         this.toDate.isBefore(that.toDate)  ?  -1 :
         this.toDate.equals(that.toDate)?       1 :
         this.fromDate.equals(that.fromDate)?  -1 :
         this.toDate.isAfter(that.toDate)?      1 :
                                               -1 ;

   }//compareTo
   
   //  ---------------   Logic ------------------
   public  boolean isValid()
   {
      return  fromDate != null && toDate != null && 
            ( fromDate.equals(toDate) || fromDate.isBefore(toDate));
   }

}//Period
