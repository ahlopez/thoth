package com.f.thoth.backend.data.gdoc.classification;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.f.thoth.backend.data.entity.BaseEntity;

/**
 * Enumeracion con las duraciones de retencion
 * en unidades de annos, para las tres fases
 * de archivo
 */
@Entity
@Table(name = "RETENCION", indexes = { @Index(columnList = "code") })
public class Retencion extends BaseEntity implements Comparable<Retencion>
{
   private static int retSequence = 0;
   
   public static int GESTION    = 0;
   public static int CENTRAL    = 1;
   public static int INTERMEDIO = 2;

   private int periodo[] = new int[3];

   // ------------- Constructors ------------------

   public Retencion()
   {
      super();
      periodo[0] = periodo[1] = periodo[2] = 5;
      buildCode();
   }

   public Retencion( int gestion, int central, int intermedio)
   {
      super();
      if (gestion <= 0 || central <= 0 || intermedio <= 0)
         throw new IllegalArgumentException("Periodos de retencion deben ser mayores que 0");

      periodo[0] = gestion;
      periodo[1] = central;
      periodo[2] = intermedio;
      buildCode();
   }//Retencion
   
   @Override protected void buildCode() { this.code = tenant.getCode()+ ":"+ (++retSequence);}

   // -------------- Getters & Setters ----------------

   public int[] getPeriodo(){ return periodo;}
   public void  setPeriodo( int[] periodo){ this.periodo = periodo;}

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

      Retencion that = (Retencion) o;

      return   this.tenant.equals(that.tenant) && this.code.equals(that.code);

   }// equals

   @Override
   public int hashCode()
   {
      return Objects.hash( tenant, code);
   }

   @Override
   public String toString()
   {
      return " Retencion{"+ super.toString()+ " periodos["+ periodo[GESTION]+ ", "+ periodo[CENTRAL]+ ", "+ periodo[INTERMEDIO]+ "]}";
   }//toString

   @Override
   public int compareTo(Retencion other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }

   // --------------- Logic ------------------------------

   public int getRetencion( int fase) { return periodo[ fase];}

}//Retencion