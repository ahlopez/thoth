package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

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

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 10)
   public Set<Clazz> subClasses;


   // ------------- Constructors ------------------
   public BranchClass()
   {
      subClasses = new TreeSet<>();
   }

   public BranchClass( String name, Schema schema, Integer category, Role roleOwner, Clazz parent,
                     LocalDateTime dateOpened, LocalDateTime dateClosed, RetentionSchedule retentionSchedule)
   {
      super( name, schema, category, roleOwner, parent, dateOpened, dateClosed, retentionSchedule);
      subClasses = new TreeSet<>();

   }//BranchClass

   // -------------- Getters & Setters ----------------
   public Set<Clazz> getSubClasses() { return subClasses;}
   public void       setSubClasses( Set<Clazz> subClasses){ this.subClasses = subClasses;}

   // --------------- Object methods ---------------------

   @Override  public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" {BranchClass "+ super.toString()+ " subClasses[");
      for ( Clazz sc : subClasses)
      {
         s.append(sc.getCode() + " ");
      }
      s.append("]}\n");
      return s.toString();
   }//toString

   // ----------------   Logic ---------------------
   public Iterator<Clazz> iterator() { return subClasses.iterator();}

}//BranchClass