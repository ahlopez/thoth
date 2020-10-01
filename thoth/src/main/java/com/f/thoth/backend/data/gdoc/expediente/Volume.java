package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.gdoc.document.Document;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;

/**
 * Representa un volumen documental (segun Moreq)
 */
@Entity
@Table(name = "VOLUME_EXPEDIENTE")
public class Volume extends Expediente
{
   @ManyToOne
   public BranchExpediente    parent;

   @Transient
   public Set<Document> documents;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @JoinColumn(name="doctype_id")
   @BatchSize(size = 20)
   public Set<DocumentType > admissibleTypes;

   public Volume()
   {
      super();
      documents       = new TreeSet<>();
      admissibleTypes = new TreeSet<>();
   }//Volume


   public Volume( Set<Document> documents, Set<DocumentType> admissibleTypes, BranchExpediente parent)
   {
      super();
      if ( admissibleTypes == null || admissibleTypes.size() == 0)
         throw new IllegalArgumentException("Un volumen no puede tener sus tipos documentales permitidos nulos ni vacios");

      this.documents       = documents == null? new TreeSet<>() : documents;
      this.admissibleTypes = admissibleTypes;
      this.parent          = parent;
   }//Volume

   // ------------------ Getters & Setters ----------------------


   public BranchExpediente getParent() {return parent;}
   public void setParent(BranchExpediente parent) {this.parent = parent;}

   public Set<Document> getDocuments() {return documents;}
   public void setDocuments(Set<Document> documents) {   this.documents = documents;}

   public Set<DocumentType> getAdmissibleTypes() {return admissibleTypes;}
   public void setAdmissibleTypes(Set<DocumentType> admissibleTypes) {this.admissibleTypes = admissibleTypes;}

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