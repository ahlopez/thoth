package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.SingleUser;

/**
 * Representa una entrada de un indice de expedientes
 */
@Entity
@Table(name = "INDEX_ENTRY", indexes = { @Index(columnList = "code") })
public class IndexEntry extends BaseEntity implements Comparable<IndexEntry>
{
   public enum Action { ADD, COPY, TRANSFER, REMOVE}
   
   @NotNull     (message= "{evidentia.action.required}")
   @Enumerated(EnumType.STRING)
   private Action entryType;
   
   @ManyToOne
   private FileIndex     index;
   
   @ManyToOne
   private SchemaValues  attributes;
   
   @ManyToOne
   private FileIndex     expediente;
   
   @ManyToOne
   private SingleUser    user;
   
   private LocalDate     date;
   
   private String        reference;


   // TODO: Considerar implementar esto con Metadatos
   //--------------- Constructors --------------------
   public IndexEntry()
   {
      buildCode();
   }

   public IndexEntry(Action entryType, FileIndex expediente, SingleUser user, LocalDate date, String reference)
   {
      if( entryType == null)
         throw new IllegalArgumentException("Tipo de entrada del índice no puede ser nula");

      if( expediente == null)
         throw new IllegalArgumentException("Expediente del índice no puede ser nulo");

      if( date == null)
         throw new IllegalArgumentException("Fecha de la entrada del índice no puede ser nula");

      this.entryType = entryType;
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

   public FileIndex        getExpediente() {  return expediente;}
   public void             setExpediente(FileIndex expediente) {this.expediente = expediente;}

   public SingleUser       getUser() {return user;}
   public void             setUser(SingleUser user) {this.user = user;}

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