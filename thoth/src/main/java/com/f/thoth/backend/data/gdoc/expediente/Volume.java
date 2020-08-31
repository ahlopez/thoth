package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.f.thoth.backend.data.gdoc.document.Document;
import com.f.thoth.backend.data.gdoc.metadata.DocType;

/**
 * Representa un volumen documental (segun Moreq)
 */
@Entity
@Table(name = "VOLUME_EXPEDIENTE", indexes = { @Index(columnList = "code") })
public class Volume extends Expediente
{
   public Expediente    parent;
   public Set<Document> documents;
   public Set<DocType > admissibleTypes;

   public Volume()
   {
      super();
      documents       = new TreeSet<>();
      admissibleTypes = new TreeSet<>();
   }//Volume


   public Volume( Set<Document> documents, Set<DocType> admissibleTypes, Expediente parent)
   {
      super();
      if ( admissibleTypes == null || admissibleTypes.size() == 0)
         throw new IllegalArgumentException("Un volumen no puede tener sus tipos documentales permitidos nulos ni vacios");

      this.documents       = documents == null? new TreeSet<>() : documents;
      this.admissibleTypes = admissibleTypes;
      this.parent          = parent;
   }//Volume

   // ------------------ Getters & Setters ----------------------


   public Expediente getParent() {return parent;}
   public void setParent(Expediente parent) {this.parent = parent;}

   public Set<Document> getDocuments() {return documents;}
   public void setDocuments(Set<Document> documents) {   this.documents = documents;}

   public Set<DocType> getAdmissibleTypes() {return admissibleTypes;}
   public void setAdmissibleTypes(Set<DocType> admissibleTypes) {this.admissibleTypes = admissibleTypes;}

   // ------------------- Object ---------------------------------

   public boolean equals( Object other) { return super.equals(other);}

   public int hashCode()  { return super.hashCode();}

   public String toString()
   {
      return "Volume{"+ super.toString()+ " n documents["+ documents.size()+ "]}";
   }

   // ------------------- Logic  -------------------------------
   public Iterator<Document> iterator(){ return documents.iterator();}

   public boolean isBranch(){ return false;}

   public boolean isLeaf(){ return false;}

   public boolean isVolume(){ return true;}

}//Volume