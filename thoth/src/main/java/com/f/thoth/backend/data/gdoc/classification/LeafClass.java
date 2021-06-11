package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.Role;

/**
 * Representa una hoja del arbol de clasificacion
 */
@Entity
@Table(name = "LEAF_CLASS")
public class LeafClass extends Clazz
{

   public static final String BRIEF = "LeafClass.brief";
   public static final String FULL  = "LeafClass.full";

   // ------------- Constructors ------------------
   public LeafClass()
   {
      super();
   }//LeafClass

   public LeafClass( String name, Schema schema, Integer category, Role roleOwner, BranchClass owner,
                     LocalDate dateOpened, LocalDate dateClosed, Retention retentionSchedule)
   {
      super( name, schema, category, roleOwner, owner, dateOpened, dateClosed, retentionSchedule);

   }//LeafClass

   // -------------- Getters & Setters -------------------

   // --------------- Object methods ---------------------

   @Override  public String toString() { return " {LeafClass "+ super.toString()+  "]}"; }

   // ----------------   Logic ---------------------

}//LeafClass