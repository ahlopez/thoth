package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.Role;

/**
 * Representa una rama del �rbol de clasificaci�n
 */
@Entity
@Table(name = "BRANCH_CLASS", indexes = { @Index(columnList = "code") })
public class BranchClass extends Clazz
{

   public static final String BRIEF = "BranchClass.brief";
   public static final String FULL  = "BranchClass.full";

   // ------------- Constructors ------------------
   public BranchClass()
   {
   }

   public BranchClass( String name, Schema schema, Integer category, Role roleOwner, BranchClass owner,
                     LocalDate dateOpened, LocalDate dateClosed, RetentionSchedule retentionSchedule)
   {
      super( name, schema, category, roleOwner, owner, dateOpened, dateClosed, retentionSchedule);
   }//BranchClass

   // -------------- Getters & Setters ----------------

   // --------------- Object methods ---------------------

   @Override  public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" {BranchClass "+ super.toString()+ "}\n");
      return s.toString();
   }//toString

   // ----------------   Logic ---------------------

}//BranchClass