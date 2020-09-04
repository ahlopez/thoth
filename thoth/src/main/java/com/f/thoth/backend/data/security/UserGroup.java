package com.f.thoth.backend.data.security;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
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
            @NamedAttributeNode("code"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("userCategory"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("fromDate"),
            @NamedAttributeNode("toDate"),
            @NamedAttributeNode("locked"),
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
         name = UserGroup.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("userCategory"),
               @NamedAttributeNode("owner"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("locked"),
               @NamedAttributeNode("roles"),
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
@Table(name = "USER_GROUP", indexes = { @Index(columnList = "code")})
public class UserGroup extends Usuario implements HierarchicalEntity<UserGroup>
{
   public static final String BRIEF = "UserGroup.brief";
   public static final String FULL  = "UserGroup.full";

   @ManyToOne
   protected UserGroup   owner;   // Owner Group to which this group belongs

   // ----------------- Constructor -----------------
   public UserGroup()
   {
      super();
      owner = null;
      buildCode();
   }//UserGroup

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      super.prepareData();
      buildCode();
   }//prepareData


   @Override protected void buildCode()
   {
      this.code = (tenant == null? "[tenant]": tenant.getCode())+"[UGR]"+ getOwnerCode()+ ">"+ (name == null? "[name]" : name);
   }//buildCode

   // --------------- Getters & Setters -----------------
   @Override
   public void   setName(String name) { this.name = name;}

   public void   setOwner(UserGroup owner)
   {
      if ( owner == null || owner.canBeOwnerOf( this))
           this.owner = owner;
      else
           throw new IllegalArgumentException(owner.getName()+ " no puede ser padre de este grupo");
   }//setOwnerGroup

   // --------------------------- Implements HierarchicalEntity ---------------------------------------
   @Override public String      getName()   { return name;}

   @Override public UserGroup   getOwner()  { return owner;}

   private String getOwnerCode(){ return owner == null ? "" : owner.getOwnerCode()+ ":"+ name; }

   // -----------------  Implements NeedsProtection ----------------

   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( SingleUser user)           { return objectToProtect.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

   @Override public void            grant( Role  role)         { objectToProtect.grant(role);}

   @Override public void            revoke(Role role)        { objectToProtect.revoke(role);}

   // ---------------------- Object -----------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof UserGroup ))
         return false;

      UserGroup that = (UserGroup) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 64597: id.hashCode();}

   @Override
   public String toString()
   {
      return "UserGroup{" + super.toString() + " owner[" + (owner == null? "---": owner.getName()) + "]}";
   }

   // --------------- Logic ---------------------

   public boolean canBeOwnerOf( UserGroup child)
   {
      if (this.equals(child))
         return false;

      return  owner == null || owner.canBeOwnerOf(child);

   }//canBeOwnerOf


   @Override public boolean canAccess( NeedsProtection object)
   {
      //TODO: Implementar la restricción de grupo de usuarios
      if ( ! object.canBeAccessedBy( this.userCategory))
         return false;

      for( Role r : this.roles)
      {
         if ( r.canAccess( object))
            return true;
      }

      return owner == null? false : owner.canAccess( object);

   }//canAccess

}//UserGroup