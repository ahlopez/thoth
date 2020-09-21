package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.gdoc.metadata.DocType;

/**
 * Representa un sub-Expediente hoja
 */
@Entity
@Table(name = "LEAF_EXPEDIENTE", indexes = { @Index(columnList = "code") })
public class LeafExpediente extends Expediente
{
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @JoinColumn(name="doctype_id")
   @BatchSize(size = 20)
   public Set<DocType>  admissibleTypes;

   // --------------- Constructors --------------------
   public LeafExpediente()
   {
      super();
      admissibleTypes = new TreeSet<>();
   }//LeafExpediente


   public LeafExpediente( String name, BranchExpediente owner, Set<DocType> admissibleTypes)
   {
      super(name, owner);
      if ( admissibleTypes == null || admissibleTypes.size() == 0)
         throw new IllegalArgumentException("Un expediente hoja no puede tener sus tipos documentales permitidos nulos ni vacios");

      this.admissibleTypes = admissibleTypes;

   }//LeafExpediente constructor

   // ---------------- Getters & Setters ---------------

   public Set<DocType> getAdmissibleTypes(){ return admissibleTypes;}
   public void         setAdmissibleTypes(Set<DocType> admissibleTypes){ this.admissibleTypes = admissibleTypes;}

   // ---------------- Object Methods ------------------

   public String toString()
   {
      return "LeafExpediente{"+ super.toString()+ " n docTypes["+ admissibleTypes.size()+ "]}";
   }

   // ------------------ Logic -------------------------

   public boolean isBranch(){ return false;}

   public boolean isLeaf(){ return true;}

   public boolean isVolume(){ return false;}

}//LeafExpediente