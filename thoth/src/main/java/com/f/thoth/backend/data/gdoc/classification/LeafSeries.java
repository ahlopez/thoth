package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.Role;

/**
 * Representa una hoja de la estructura jer�rquica de series documentales
 */
@Entity
@Table(name = "LEAF_SERIES")
public class LeafSeries extends Series
{
   public static final String BRIEF = "LeafSeries.brief";
   public static final String FULL  = "LeafSeries.full";

   // ------------- Constructors ------------------
   public LeafSeries()
   {
      super();
   }

   public LeafSeries( String name, Schema schema, Integer category, Role roleOwner, Series parent,
                 LocalDate dateOpened, LocalDate dateClosed, RetentionSchedule retentionSchedule)
   {
      super( name, schema, category, roleOwner, parent, dateOpened, dateClosed, retentionSchedule);
   }//LeafSeries

   // --------------- Object methods ---------------------
   @Override  public String toString() { return " {LeafSeries "+ super.toString()+ "}"; }


}//LeafSeries