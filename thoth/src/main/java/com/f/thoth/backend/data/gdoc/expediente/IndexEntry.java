package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;

import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa una entrada de un indice de expedientes
 */
public class IndexEntry implements Comparable<IndexEntry>
{
   private String entryType;
   private String id;
   private String expediente;
   private String user;
   private String date;
   private String action;
   private String reference;


   // TODO: Considerar implementar esto con Metadatos
   //--------------- Constructors --------------------
   public IndexEntry()
   {
   }

   public IndexEntry(String entryType, String id, String expediente, String user, String date, String action, String reference)
   {
      if( TextUtil.isEmpty(entryType))
         throw new IllegalArgumentException("Tipo de entrada del índice no puede ser nula ni vacio");

      if( TextUtil.isEmpty(id))
         throw new IllegalArgumentException("Id de la entrada del índice no puede ser nulo ni vacio");

      if( TextUtil.isEmpty(expediente))
         throw new IllegalArgumentException("Id del expediente del índice no puede ser nulo ni vacio");

      if( TextUtil.isEmpty(date))
         throw new IllegalArgumentException("Fecha de la entrada del índice no puede ser nula ni vacía");

      if( TextUtil.isEmpty(action))
         throw new IllegalArgumentException("Accion registrada en la entrada del indice no puede ser nula ni vacía");

      this.entryType = entryType;
      this.id        = id;
      this.expediente= expediente;
      this.user      = user;
      this.date      = date;
      this.action    = action;
      this.reference = reference;

   }//IndexEntry

   // ------------------ Getters & Setters ----------------------------

   public String getEntryType() { return entryType; }
   public void setEntryType(String entryType) { this.entryType = entryType; }

   public String getId() { return id;}
   public void setId(String id) {this.id = id;}

   public String getExpediente() {  return expediente;}
   public void setExpediente(String expediente) {this.expediente = expediente;}

   public String getUser() {return user;}
   public void setUser(String user) {this.user = user;}

   public String getDate() {return date;}
   public void setDate(String date) {this.date = date;}

   public String getAction() {return action;}
   public void setAction(String action) {this.action = action;}

   public String getReference() {return reference;}
   public void setReference(String reference) {this.reference = reference;}

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
       .append(" expediente["+ expediente+ "]")
       .append(" usuario["+ user+ "]")
       .append(" fecha["+ date+ "]")
       .append(" accion["+ action+ "]")
       .append(" referencia["+ reference+ "]");

      return s.toString();

   }//toString

   @Override public int compareTo(IndexEntry other)
   {
      if ( this.equals(other))
         return 0;

      if ( other == null)
         return 1;

      LocalDateTime fecha1 = LocalDateTime.parse(date);
      LocalDateTime fecha2 = LocalDateTime.parse(other.date);

      int dateOrder = fecha1.compareTo(fecha2);
      if (dateOrder != 0)
         return dateOrder;

      return this.id.compareTo(other.id);

   }//compareTo


}//IndexEntry