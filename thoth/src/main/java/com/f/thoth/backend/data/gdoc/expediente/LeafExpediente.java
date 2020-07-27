package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.f.thoth.backend.data.gdoc.document.Document;
import com.f.thoth.backend.data.gdoc.metadata.DocType;

/**
 * Representa un sub-Expediente hoja
 */
public class LeafExpediente extends Expediente
{
   public Expediente    parent;
   public Set<DocType>  admissibleTypes;
   public Set<Document> documents;

   // --------------- Constructors --------------------
   public LeafExpediente()
   {
      super();
      documents       = new TreeSet<>();
      admissibleTypes = new TreeSet<>();
   }//LeafExpediente

   public LeafExpediente( Set<Document> documents, Set<DocType> admissibleTypes, Expediente parent)
   {
      super();
      if ( admissibleTypes == null || admissibleTypes.size() == 0)
         throw new IllegalArgumentException("Un expediente hoja no puede tener sus tipos documentales permitidos nulos ni vac�os");

      this.documents       = documents == null? new TreeSet<>() : documents;
      this.admissibleTypes = admissibleTypes;
      this.parent          = parent;
   }//LeafExpediente

   // ---------------- Getters & Setters ---------------
   public Expediente getParent() {return parent;}
   public void setParent(Expediente parent) {this.parent = parent;}

   public Set<DocType> getAdmissibleTypes(){ return admissibleTypes;}
   public void         setAdmissibleTypes(Set<DocType> admissibleTypes){ this.admissibleTypes = admissibleTypes;}

   public Set<Document> getDocuments(){ return documents;}
   public void          setDocuments( Set<Document> documents){ this.documents = documents;}

   // ---------------- Object Methods ------------------

   public boolean equals( Object other) { return super.equals(other);}

   public int hashCode()  { return super.hashCode();}

   public String toString()
   {
      return "LeafExpediente{"+ super.toString()+ " n documents["+ documents.size()+ "]}";
   }

   // ------------------ Logic -------------------------

   public Iterator<Document> iterator(){ return documents.iterator();}

   public boolean isBranch(){ return false;}

   public boolean isLeaf(){ return true;}

   public boolean isVolume(){ return false;}

}//LeafExpediente