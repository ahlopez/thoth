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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.Role;

/**
 * Representa una hoja del �rbol de clasificaci�n
 */
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = LeafClass.BRIEF,
        attributeNodes = {
            @NamedAttributeNode("parms")
        }),
    @NamedEntityGraph(
        name = LeafClass.FULL,
        attributeNodes = {
            @NamedAttributeNode("parms"),
            @NamedAttributeNode("history")
        }) })
@Entity
@Table(name = "LEAF_CLASS", indexes = { @Index(columnList = "code") })
public class LeafClass extends Clazz
{

   public static final String BRIEF = "LeafClass.brief";
   public static final String FULL  = "LeafClass.full";

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 40)
   private Set<Expediente> expedientes;

   // ------------- Constructors ------------------
   public LeafClass()
   {
      super();
      expedientes = new TreeSet<>();
   }//LeafClass

   public LeafClass( String name, Schema schema, Integer category, Role roleOwner, Clazz parent,
                     LocalDateTime dateOpened, LocalDateTime dateClosed, RetentionSchedule retentionSchedule)
   {
      super( name, schema, category, roleOwner, parent, dateOpened, dateClosed, retentionSchedule);
      expedientes = new TreeSet<>();

   }//LeafClass

   // -------------- Getters & Setters ----------------
   public Set<Expediente> getExpedientes() { return expedientes;}
   public void            setExpedientes( Set<Expediente> expedientes) { this.expedientes = expedientes;}

   // --------------- Object methods ---------------------

   @Override  public String toString() { return " {LeafClass "+ super.toString()+ " expedientes["+ expedientes.size()+ "]}"; }

   // ----------------   Logic ---------------------
   public Iterator<Expediente> iterator() { return expedientes.iterator();}

}//LeafClass