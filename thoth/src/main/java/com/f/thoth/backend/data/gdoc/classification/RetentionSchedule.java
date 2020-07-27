package com.f.thoth.backend.data.gdoc.classification;

import java.util.Objects;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BasicEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa una entrada de la  TRD
 * (Tabla de retenci�n documental, o tambi�n
 *  Calendario de conservaci�n)
 */
@Table(name = "RETENTION_SCHEDULE", indexes = { @Index(columnList = "code") })
public class RetentionSchedule extends BasicEntity implements Comparable<RetentionSchedule>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   private String              name;

   @NotNull (message = "{evidentia.retention.required}")
   private Retencion           retention;

   @NotNull(message = "{evidentia.disposition.required}")
   @Enumerated(EnumType.STRING)
   private Disposicion         disposition;

   @NotNull(message = "{evidentia.disposition.required}")
   @Enumerated(EnumType.STRING)
   private TradicionDocumental tradicionDocumental;

   // ------------- Constructors ------------------
   public RetentionSchedule()
   {
      super();
   }

   public RetentionSchedule( String name, Retencion retention, Disposicion disposition, TradicionDocumental tradicionDocumental)
   {
      super();

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es invalido");

      if ( retention == null)
         throw new IllegalArgumentException("Periodos de retencion no puede ser nulos");

      if ( disposition == null)
         throw new IllegalArgumentException("Accion de disposicion final no puede ser nula");

      if (tradicionDocumental == null)
         throw new IllegalArgumentException("Tradicion documental no puede ser nula");

      this.name = name;
      this.retention = retention;
      this.disposition = disposition;
      this.tradicionDocumental = tradicionDocumental;
      buildCode();

   }//RetentionSchedule

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Anonimo";
      buildCode();
   }//prepareData


   private void buildCode() { this.code =  tenant.toString()+ ":"+ name; }

   // -------------- Getters & Setters ----------------

   public String    getName() { return name;}
   public void      setName( String name)
   {
      this.name =  name;
      buildCode();
   }//setName

   public Retencion           getRetencion() { return retention;}
   public void                setRetencion( Retencion retention){ this.retention = retention;}

   public Disposicion         getDisposition(){ return disposition;}
   public void                setDisposition( Disposicion disposition){ this.disposition = disposition;}

   public TradicionDocumental getTradicionDocumental() { return tradicionDocumental;}
   public void                setTradicionDocumental( TradicionDocumental tradicionDocumental){ this.tradicionDocumental = tradicionDocumental;}

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

      RetentionSchedule that = (RetentionSchedule) o;

      return  this.code.equals(that.code);

   }// equals

   @Override
   public int hashCode()
   {
      return Objects.hash( tenant, code);
   }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "RetentionSchedule {").
        append( super.toString()).append("\n\t\t").
        append( " name["+ name+ "]").
        append( retention.toString()).
        append( " disposicion["+ disposition+ "]").
        append( " tradicionDocumental["+ tradicionDocumental+ "]}");

      return s.toString();
   }//toString

   @Override
   public int compareTo(RetentionSchedule other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }

   // --------------- Logic ------------------------------


}//RetentionSchedule