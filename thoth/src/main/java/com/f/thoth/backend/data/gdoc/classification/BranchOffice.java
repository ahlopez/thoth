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
 * Representa una rama de la estructura jer�rquica de oficinas productoras
 */
@Entity
@Table(name = "BRANCH_OFFICE", indexes = { @Index(columnList = "code") })
public class BranchOffice extends Office
{
   public static final String BRIEF = "BranchOffice.brief";
   public static final String FULL  = "BranchOffice.full";

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 10)
   public Set<Office> subOffices;

   // ------------- Constructors ------------------
   public BranchOffice()
   {
      super();
      subOffices = new TreeSet<>();
   }//BranchOffice


   public BranchOffice( String name, Schema schema, Integer category, Role roleOwner, Office parent,
                     LocalDateTime dateOpened, LocalDateTime dateClosed, RetentionSchedule retentionSchedule)
   {
      super( name, schema, category, roleOwner, parent, dateOpened, dateClosed, retentionSchedule);
      subOffices = new TreeSet<>();

   }//BranchOffice

   // -------------- Getters & Setters ----------------
   public Set<Office> getSubOffices() { return subOffices;}
   public void        setSubOffices( Set<Office> subOffices){ this.subOffices = subOffices;}

   // --------------- Object methods ---------------------

   @Override  public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" {BranchOffice "+ super.toString()+ " subOffices[");
      for ( Office so : subOffices)
      {
         s.append(so.getCode() + " ");
      }
      s.append("]}\n");
      return s.toString();
   }//toString

   // ----------------   Logic ---------------------
   public Iterator<Office> iterator() { return subOffices.iterator();}

}//BranchOffice