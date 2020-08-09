package com.f.thoth.backend.data.security;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.f.thoth.backend.data.entity.HierarchicalEntity;

/**
 * Representa un Grupo de Usuarios
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = UserGroup.BRIEF,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate")
         }),
   @NamedEntityGraph(
         name = UserGroup.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("roles")
         }) })
@Entity
@Table(name = "USER_GROUP", indexes = { @Index(columnList = "code")})
public class UserGroup extends Usuario implements Comparable<UserGroup>, HierarchicalEntity
{
   public static final String BRIEF = "UserGroup.brief";
   public static final String FULL  = "UserGroup.full";

   @ManyToOne
   protected UserGroup   parent;

   // ----------------- Constructor -----------------
   public UserGroup()
   {
      super();
      parent = null;
      buildCode();
   }

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      super.prepareData();
      buildCode();
   }//prepareData

   @Override protected void buildCode()
   {
      this.code = (tenant == null?    "[Tenant]": tenant.getCode())+ ">"+
                  (name == null? "[name]": name);
   }//buildCode

   // --------------- Getters & Setters -----------------
   @Override
   public void   setName(String name)
   {
      this.name = name;
      buildCode();
   }//setFirstName

   public UserGroup       getParentGroup() { return parent; }
   public void            setParentGroup(UserGroup parent)
   {
      if ( parent == null || parent.canBeParentOf( this))
           this.parent = parent;
      else
           throw new IllegalArgumentException(parent.getName()+ " no puede ser padre de este grupo");
   }//setParentGroup

    // Implements HierarchicalEntity
    @Override public Long    getId()     { return super.getId();}
    @Override public String  getCode()   { return super.getCode();}
    @Override public String  getName()   { return name;}
    @Override public Long    getParent() { return parent == null? null: parent.getId();}


   // --------------- Object ------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof UserGroup ))
         return false;

      UserGroup that = (UserGroup) o;
       return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return 511;}

   @Override
   public String toString()
   {
      return "UserGroup{" + super.toString() + " parent[" + (parent == null? "-ninguno-": parent.getName()) + "]}";
   }

   @Override
   public int compareTo(UserGroup that)
   {
      return this.equals(that)? 0 :
         that == null ? 1 :   (this.code).compareTo(that.code);

   }// compareTo


   // --------------- Logic ---------------------

   public boolean canBeParentOf( UserGroup child)
   {
      if (this.equals(child))
         return false;

      return  parent == null || parent.canBeParentOf(child);

   }//canBeParentOf


   @Override public boolean canAccess( NeedsProtection object)
   {
      if ( ! object.canBeAccessedBy( this.category))
         return false;

      for( Role r : this.roles)
      {
         if ( r.canAccess( object))
            return true;
      }

      return parent == null? false : parent.canAccess( object);

   }//canAccess

}//UserGroup