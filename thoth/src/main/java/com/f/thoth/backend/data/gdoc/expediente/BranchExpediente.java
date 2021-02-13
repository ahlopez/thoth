package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.SingleUser;

/**
 * Representa un nodo de la jerarquia de expedientes (expediente/sub-expediente/volumen
 */
@NamedEntityGraphs({
     @NamedEntityGraph(
         name = Expediente.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),           // DB human id. Includes [tenant, type, path+]
            @NamedAttributeNode("expedienteCode"), // Business id unique inside the owner (class or expediente), vg 001,002, etc
            @NamedAttributeNode("name"),
            @NamedAttributeNode("path"),
            @NamedAttributeNode("classificationClass"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("open"),
            @NamedAttributeNode("admissibleTypes"),
            @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.BRIEF)
         },
         subgraphs = @NamedSubgraph(name = ObjectToProtect.BRIEF,
            attributeNodes = {
               @NamedAttributeNode("category"),
               @NamedAttributeNode("userOwner"),
               @NamedAttributeNode("roleOwner"),
               @NamedAttributeNode("restrictedTo")
            })
         ),
     @NamedEntityGraph(
         name = Expediente.FULL,
         attributeNodes = {
            @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.BRIEF),
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("expedienteCode"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("classificationClass"),
            @NamedAttributeNode("createdBy"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("path"),
            @NamedAttributeNode("dateOpened"),
            @NamedAttributeNode("dateClosed"),
            @NamedAttributeNode("metadata"),
            @NamedAttributeNode("open"),
            @NamedAttributeNode("admissibleTypes"),
            @NamedAttributeNode("keywords"),
            @NamedAttributeNode("entries"),
            @NamedAttributeNode("mac"),
            @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.FULL)
         },
         subgraphs = @NamedSubgraph(name = ObjectToProtect.FULL,
            attributeNodes = {
               @NamedAttributeNode("category"),
               @NamedAttributeNode("userOwner"),
               @NamedAttributeNode("roleOwner"),
               @NamedAttributeNode("restrictedTo"),
               @NamedAttributeNode("acl")
            })
         )
    })

@Entity
@Table(name = "BRANCH_EXPEDIENTE", indexes = { @Index(columnList = "code"), @Index(columnList = "tenant,expedienteCode"), @Index(columnList= "tenant,keywords")})
public class BranchExpediente extends AbstractExpediente
{
   public static final String BRIEF = "BranchExpediente.brief";
   public static final String FULL  = "BranchExpediente.full";

   @ManyToMany
   protected Set<AbstractExpediente>  children;                   // Children of this expediente

   // ------------- Constructors ------------------
   public BranchExpediente()
   {
      super();
      this.children = new TreeSet<>();
   }//BranchExpediente null constructor

   public BranchExpediente( String expedienteCode, String path, String name, SingleUser createdBy, Classification classificationClass,
                            SchemaValues metadata, LocalDateTime dateOpened, LocalDateTime dateClosed, Expediente owner, Boolean open,
                            Set<IndexEntry> entries, Set<DocumentType> admissibleTypes, Set<String> keywords, String location,
                            Set<AbstractExpediente> children, String mac)
   {
      super( expedienteCode, path, name, createdBy,  classificationClass, metadata, dateOpened, dateClosed,
             owner, open, entries, admissibleTypes, keywords, location, mac);

      this.children = (children == null? new TreeSet<>(): children);
   }//BranchExpediente constructor

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      super.prepareData();
   }//prepareData


   // -------------- Getters & Setters ----------------
   public Set<AbstractExpediente>  getChildren() { return children;}
   public void                     setChildren( Set<AbstractExpediente> children) { this.children = children;}

   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof BranchExpediente ))
         return false;

      BranchExpediente that = (BranchExpediente) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 40277: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "BranchExpediente{")
       .append( super.toString()+"]\n children[");

      for ( AbstractExpediente child: children )
         s.append( child.toString()+ "\n");

      s.append("]\n     }\n");

      return s.toString();
   }//toString

   // --------------- Logic ------------------------------

   public boolean isOpen()
   {
      LocalDateTime now = LocalDateTime.now();
      return open &&
      ((now.equals(dateOpened) || now.equals(dateClosed)) ||
       (now.isAfter(dateOpened) && now.isBefore(dateClosed))) ;
   }//isOpen


   @Override public void openExpediente()
   {
      if ( !isOpen())
      {
         super.openExpediente();
         for( AbstractExpediente child: children)
            child.openExpediente();
      }
   }//openExpediente


   @Override public void closeExpediente()
   {
      if( isOpen())
      {
         super.closeExpediente();
         for( AbstractExpediente child: children)
            child.closeExpediente();
      }
   }//closeExpediente


   public boolean addChild(AbstractExpediente child)
   {
      return children.add(child);
   }//addChild

   public Iterator<AbstractExpediente> childIterator()
   {
      return children.iterator();
   }//childIterator


}//BranchExpediente
