package com.f.thoth.backend.data.gdoc.expediente;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "BRANCH_EXPEDIENTE", indexes = { @Index(columnList = "code") })public class BranchExpediente extends Expediente
{

   // --------------- Constructors --------------------
   public BranchExpediente()
   {
      super();
   }
   
   public BranchExpediente( String name, BranchExpediente owner)
   {
      super(name, owner);
   }

   // ---------------- Object Methods ------------------
   @Override public String toString(){ return "BranchExpediente{"+ super.toString()+"}";}

   // ------------------ abstract Expediente -------------------------

   @Override public boolean isBranch(){ return true;}

   @Override public boolean isLeaf(){ return false;}

   @Override public boolean isVolume(){ return false;}


}//BranchExpediente