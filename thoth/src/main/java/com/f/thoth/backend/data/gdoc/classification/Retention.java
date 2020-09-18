package com.f.thoth.backend.data.gdoc.classification;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa una entrada de la  TRD
 * (Tabla de retencion documental, o tambien
 *  Calendario de conservacion)
 */
@Entity
@Table(name = "RETENTION")
public class Retention extends BaseEntity implements Comparable<Retention>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   private String              name;

   @NotNull (message = "{evidentia.gestion.required}")
   private int           gestion;

   @NotNull (message = "{evidentia.central.required}")
   private int           central;

   @NotNull (message = "{evidentia.intermedio.required}")
   private int           intermedio;

   @NotNull(message = "{evidentia.disposicion.required}")
   @Enumerated(EnumType.STRING)
   private Disposicion         disposicion;

   @NotNull(message = "{evidentia.disposicion.required}")
   @Enumerated(EnumType.STRING)
   private TradicionDocumental tradicion;

   // ------------- Constructors ------------------
   public Retention()
   {
      super();
      buildCode();
   }

   public Retention( String name, int gestion, int central, int intermedio, Disposicion disposicion, TradicionDocumental tradicion)
   {
      super();

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es invalido");

      if ( gestion < 0 || central < 0 || intermedio < 0)
         throw new IllegalArgumentException("Periodos de retencion no puede ser negativos");

      if ( disposicion == null)
         throw new IllegalArgumentException("Accion de disposicion final no puede ser nula");

      if (tradicion == null)
         throw new IllegalArgumentException("Tradicion documental no puede ser nula");

      this.name        = name;
      this.gestion     = gestion;
      this.central     = central;
      this.intermedio  = intermedio;
      this.disposicion = disposicion;
      this.tradicion = tradicion;
      buildCode();

   }//RetentionSchedule

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Anonimo";
      buildCode();
   }//prepareData


   @Override protected void buildCode()
   {
      this.code =  (tenant == null? "[Tenant]" : tenant.getCode())+ "[RET]>"+
                   (name == null? "[name]": name);
   }//buildCode

   // -------------- Getters & Setters ----------------

   public String              getName() { return name;}
   public void                setName( String name){ this.name =  name; }

   public int                 getGestion() { return gestion;}
   public void                setGestion( int gestion){ this.gestion = gestion;}

   public int                 getCentral() { return central;}
   public void                setCentral( int central){ this.central = central;}

   public int                 getIntermedio() { return intermedio;}
   public void                setIntermedio( int intermedio){ this.intermedio = intermedio;}

   public Disposicion         getDisposicion(){ return disposicion;}
   public void                setDisposicion( Disposicion disposicion){ this.disposicion = disposicion;}

   public TradicionDocumental getTradicion() { return tradicion;}
   public void                setTradicion( TradicionDocumental tradicion){ this.tradicion = tradicion;}

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Retention ))
         return false;

      Retention that = (Retention) o;
      return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return id == null? 13397: id.hashCode(); }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "RetentionSchedule {").
        append( super.toString()).append("\n\t\t").
        append( " name["+ name+ "]").
        append( " gestion["+ gestion+ "]").
        append( " central["+ central+ "]").
        append( " intermedio["+ intermedio+ "]").
        append( " disposicion["+ disposicion+ "]").
        append( " tradicion["+ tradicion+ "]}");

      return s.toString();
   }//toString

   @Override
   public int compareTo(Retention that)
   {
      return this.equals(that)?  0 :
         that ==  null        ?  1 :
         this.code == null  && that.code == null?  0 :
         this.code != null  && that.code == null?  1 :
         this.code == null  && that.code != null? -1 :
         this.code.compareTo(that.code);

   }// compareTo

   // --------------- Logic ------------------------------


}//RetentionSchedule