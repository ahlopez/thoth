package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.User;

/**
 * Representa una entrada de un indice de expedientes
 */
@Entity
@Table(name = "INDEX_ENTRY", indexes = { @Index(columnList = "code") })
public class IndexEntry extends BaseEntity implements Comparable<IndexEntry>
{
   public enum Action {CREATE, ADD, READ, COPY, TRANSFER, REMOVE, OPEN, CLOSE, REOPEN}
   
   @NotNull     (message= "{evidentia.action.required}")
   @Enumerated(EnumType.STRING)
   private Action               entryType;                            // Tipo de operación realizada sobre el expediente
   
   @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
   private ExpedienteIndex     index;                                 // Indice al que pertenece esta entrada 
   
   @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
   private SchemaValues        attributes;                            // Metadatos que describen la operación realizada
   
   @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
   private BaseExpediente      expediente;                            // Expediente afectado por la operación           
   
   @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
   private User                user;                                  // Usuario que realizó la operación   
   
   @NotNull     (message= "{evidentia.date.required}")
   private LocalDate           date;                                  // Fecha en que se realizó la operación
   
   private String              reference;                             // Nota de referencia asociada con la operación


   // TODO: Considerar implementar esto con Metadatos
   //--------------- Constructors --------------------
   public IndexEntry()
   {
      buildCode();
   }

   public IndexEntry(Action entryType, ExpedienteIndex index, BaseExpediente expediente, User user, LocalDate date, String reference)
   {
      if( entryType == null)
         throw new IllegalArgumentException("Tipo de entrada del índice no puede ser nula");

      if( index == null)
         throw new IllegalArgumentException("Índice al que pertenece la entrada no puede ser nulo");

      if( expediente == null)
         throw new IllegalArgumentException("Expediente del índice no puede ser nulo");

      if( date == null)
         throw new IllegalArgumentException("Fecha de la entrada del índice no puede ser nula");

      this.entryType = entryType;
      this.index     = index;
      this.expediente= expediente;
      this.user      = user;
      this.date      = date;
      this.reference = reference;
      
      buildCode();

   }//IndexEntry
   
   @Override public void buildCode() { this.code = (tenant == null? "[Tenant]": tenant.getCode())+ "[IXE]"+ (id == null? "---": id.toString());}

   // ------------------ Getters & Setters ----------------------------

   public Action           getEntryType() { return entryType; }
   public void             setEntryType(Action entryType) { this.entryType = entryType; }

   public BaseExpediente   getExpediente() {  return expediente;}
   public void             setExpediente(BaseExpediente expediente) {this.expediente = expediente;}

   public User             getUser() {return user;}
   public void             setUser(User user) {this.user = user;}

   public LocalDate        getDate() {return date;}
   public void             setDate(LocalDate date) {this.date = date;}

   public String           getReference() {return reference;}
   public void             setReference(String reference) {this.reference = reference;}

   // -------------------------- Object ----------------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      if (!super.equals(o))
         return false;

      IndexEntry that = (IndexEntry) o;
      return expediente.equals(that.expediente) && id.equals(that.id);

   }//equals

   @Override public int hashCode() { return expediente.hashCode()* 1023 + id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append("IndexEntry{")
       .append(" tipo["+ entryType+ "]")
       .append(" id["+ id+ "]")
       .append(" expediente["+ expediente.getCode()+ "]")
       .append(" usuario["+ user.getCode()+ "]")
       .append(" fecha["+ TextUtil.formatDate(date)+ "]")
       .append(" referencia["+ reference+ "]}");

      return s.toString();

   }//toString

   @Override public int compareTo(IndexEntry other)
   {
      if ( this.equals(other))
         return 0;

      if ( other == null)
         return 1;

      int dateOrder = this.date.compareTo(other.date);
      if (dateOrder != 0)
         return dateOrder;

      return this.id.compareTo(other.id);

   }//compareTo


}//IndexEntry