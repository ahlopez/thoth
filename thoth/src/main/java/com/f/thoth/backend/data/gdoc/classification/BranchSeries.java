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
 * Representa una rama de la estructura jer�rquica de series documentales
 */
@Entity
@Table(name = "BRANCH_SERIES", indexes = { @Index(columnList = "code") })
public class BranchSeries extends Series
{
   public static final String BRIEF = "BranchSeries.brief";
   public static final String FULL  = "BranchSeries.full";

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 10)
   public Set<Series> subSeries;

   // ------------- Constructors ------------------
   public BranchSeries()
   {
      super();
      subSeries = new TreeSet<>();
   }

   public BranchSeries( String name, Schema schema, Integer category, Role roleOwner, Series parent,
                     LocalDateTime dateOpened, LocalDateTime dateClosed, RetentionSchedule retentionSchedule)
   {
      super( name, schema, category, roleOwner, parent, dateOpened, dateClosed, retentionSchedule);

      subSeries = new TreeSet<>();

   }//BranchSeries

   // -------------- Getters & Setters ----------------
   public Set<Series> getSubSeries() { return subSeries;}
   public void        setSubSeries( Set<Series> subSeries){ this.subSeries = subSeries;}

   // --------------- Object methods ---------------------

   @Override  public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" {BranchSeries "+ super.toString()+ " subSeries[");
      for ( Series ss : subSeries)
      {
         s.append(ss.getCode() + " ");
      }
      s.append("]}\n");
      return s.toString();
   }//toString

   // ----------------   Logic ---------------------
   public Iterator<Series> iterator() { return subSeries.iterator();}

}//BranchSeries