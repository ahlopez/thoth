package com.f.thoth.backend.data.security;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.Valid;

import org.hibernate.annotations.BatchSize;

/**
 * Representa un Grupo de Usuarios
 */
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = UserGroup.BRIEF,
        attributeNodes = {
            @NamedAttributeNode("parms")
        }),
    @NamedEntityGraph(
        name = UserGroup.FULL,
        attributeNodes = {
            @NamedAttributeNode("parms"),
            @NamedAttributeNode("history")
        }) })
@Entity
@Table(name = "USER_GROUP", indexes = { @Index(columnList = "code")})
public class UserGroup extends Usuario implements Comparable<UserGroup>
{
   public static final String BRIEF = "UserGroup.brief";
   public static final String FULL  = "UserGroup.full";

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 50)
   @Valid
   private Set<Usuario> members;

   // ----------------- Constructor -----------------
   public UserGroup()
   {
      super();
      members = new TreeSet<>();
   }

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      super.prepareData();
      this.code     = tenant.toString()+ ":G:"+ firstName;
   }//prepareData

   // --------------- Getters & Setters -----------------
   @Override
   public void   setFirstName(String firstName)
   {
      this.firstName = firstName;
      this.setCode( tenant.toString()+ ":G:"+ firstName);
   }//setFirstName

   public Set<Usuario> getMembers() { return members;}
   public void         setMembers( Set<Usuario> members) { this.members = members;}

    @Override
   public int compareTo(UserGroup that)
   {
      return this.equals(that)? 0 :
             that == null ?     1 :
             (this.code).compareTo(that.code);

   }// compareTo


   // --------------- Logic ---------------------

   public void addMember( Usuario member) { member.addToGroup(this); }

   @Override public boolean canAccess( NeedsProtection object)
   {
      if ( ! object.canBeAccessedBy( this.category))
         return false;

      for( Role r : this.roles)
      {
         if ( r.canAccess( object))
            return true;
      }

      for (UserGroup ug: groups)
      {
         if (ug.hasAccess(object, this.category))
            return true;
      }

      return false;

   }//canAccess


   public boolean hasAccess( NeedsProtection object, Integer category)
   {
      Integer cat = Math.min(this.category, category);
      if ( ! object.canBeAccessedBy( cat))
         return false;

      if (hasPermission( object))
         return true;

      return parentHasAccess( object, cat);

   }//hasAccess


   private boolean hasPermission( NeedsProtection object)
   {
      for( Role r : roles)
      {
         if ( r.canAccess( object))
            return true;
      }
      return false;

   }//hasPermission

   private boolean parentHasAccess( NeedsProtection object, Integer category)
   {
      for (UserGroup g: groups)
      {
         if (g.hasAccess(object, category))
            return true;
      }

      return false;
   }//parentHasAccess

}//UserGroup