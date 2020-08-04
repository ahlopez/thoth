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

/**
 * Representa un Grupo de Usuarios
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = UserGroup.BRIEF,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("firstName"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate")
         }),
   @NamedEntityGraph(
         name = UserGroup.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("firstName"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("roles"),
               @NamedAttributeNode("groups")
         }) })
@Entity
@Table(name = "USER_GROUP", indexes = { @Index(columnList = "code")})
public class UserGroup extends Usuario implements Comparable<UserGroup>
{
   public static final String BRIEF = "UserGroup.brief";
   public static final String FULL  = "UserGroup.full";

   @ManyToOne
   protected UserGroup   parentGroup;

   // ----------------- Constructor -----------------
   public UserGroup()
   {
      super();
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
                  (firstName == null? "[firstName]": firstName);
   }//buildCode

   // --------------- Getters & Setters -----------------
   @Override
   public void   setFirstName(String firstName)
   {
      this.firstName = firstName;
      buildCode();
   }//setFirstName

   public UserGroup       getParentGroup() { return parentGroup; }
   public void            setParentGroup(UserGroup parentGroup) { this.parentGroup = parentGroup; }

   @Override
   public int compareTo(UserGroup that)
   {
      return this.equals(that)? 0 :
         that == null ? 1 :   (this.code).compareTo(that.code);

   }// compareTo


   // --------------- Logic ---------------------

   public void addMember( Usuario member)
   {
      if ( canAdd(member))
          member.addToGroup(this);
   }//addMember

   public boolean canAdd( Usuario member)
   {
      if (this.equals(member))
         return false;

      return  parentGroup == null || parentGroup.canAdd(member);

   }//canAdd


   @Override public boolean canAccess( NeedsProtection object)
   {
      if ( ! object.canBeAccessedBy( this.category))
         return false;

      for( Role r : this.roles)
      {
         if ( r.canAccess( object))
            return true;
      }

      return parentGroup == null? false : parentGroup.canAccess( object);

   }//canAccess

}//UserGroup