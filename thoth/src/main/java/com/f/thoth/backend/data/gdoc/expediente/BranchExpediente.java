package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class BranchExpediente extends Expediente
{
   public Expediente      parent;
   public Set<Expediente> subExpedientes;

   // --------------- Constructors --------------------
   public BranchExpediente()
   {
      super();
      subExpedientes = new TreeSet<>();
   }

   public BranchExpediente (Set<Expediente> subExpedientes)
   {
      super();
      if (subExpedientes == null || subExpedientes.size() == 0)
         throw new IllegalArgumentException("Conjunto de subExpedientes no puede ser nulo ni vacio");

      this.subExpedientes = subExpedientes;
   }//BranchExpediente

   // ---------------- Getters & Setters ---------------

   public Expediente getParent() {return parent;}
   public void setParent(Expediente parent) {this.parent = parent;}

   public Set<Expediente> getSubExpedientes(){ return subExpedientes;}
   public void            setSubExpedientes( Set<Expediente> subExpedientes){ this.subExpedientes = subExpedientes;}

   // ---------------- Object Methods ------------------

   @Override public boolean equals( Object other) { return super.equals(other);}

   @Override public int hashCode()  { return super.hashCode();}

   @Override public String toString(){ return "BranchExpediente{"+ super.toString()+ " n subExpedientes["+ subExpedientes.size()+ "]}";}

   // ------------------ Logic -------------------------
   public boolean addExpediente( Expediente expediente) { return subExpedientes.add(expediente);}

   public Iterator<Expediente> iterator(){ return subExpedientes.iterator(); }

   public boolean isBranch(){ return true;}

   public boolean isLeaf(){ return false;}

   public boolean isVolume(){ return false;}


}//BranchExpediente