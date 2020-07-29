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
 * Representa una hoja de la estructura jer�rquica de oficinas productoras
 */
@Entity
@Table(name = "LEAF_OFFICE", indexes = { @Index(columnList = "code") })
public class LeafOffice extends Office
{
   public static final String BRIEF = "LeafOffice.brief";
   public static final String FULL  = "LeafOffice.full";

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 40)
   private Set<Series> series;

   // ------------- Constructors ------------------
   public LeafOffice()
   {
      super();
      series = new TreeSet<>();
   }


   public LeafOffice( String name, Schema schema, Integer category, Role roleOwner, Office parent,
                 LocalDateTime dateOpened, LocalDateTime dateClosed, RetentionSchedule retentionSchedule)
   {
      super( name, schema, category, roleOwner, parent, dateOpened, dateClosed, retentionSchedule);

      series = new TreeSet<>();
   }//LeafOffice

   // -------------- Getters & Setters ----------------
   public Set<Series> getSeries() { return series;}
   public void        setSeries( Set<Series> series) { this.series = series;}

   // --------------- Object methods ---------------------
   @Override  public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" {LeafOffice "+ super.toString()).
        append(" series[");

      for ( Series ser : series)
      {
         s.append(ser.getCode() + " ");
      }
      s.append("]}\n");
      return s.toString();

   }//toString

   // ----------------   Logic ---------------------
   public Iterator<Series> iterator() { return series.iterator();}

}//LeafOffice