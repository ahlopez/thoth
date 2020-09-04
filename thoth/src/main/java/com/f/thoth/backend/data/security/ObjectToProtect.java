package com.f.thoth.backend.data.security;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.ui.utils.Constant;


/**
 * Representa un objeto que requiere protección
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = ObjectToProtect.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("id"),
            @NamedAttributeNode("category"),
            @NamedAttributeNode("userOwner"),
            @NamedAttributeNode("roleOwner"),
            @NamedAttributeNode("restrictedTo")
         }),
   @NamedEntityGraph(
         name = ObjectToProtect.FULL,
         attributeNodes = {
            @NamedAttributeNode("id"),
            @NamedAttributeNode("category"),
            @NamedAttributeNode("userOwner"),
            @NamedAttributeNode("roleOwner"),
            @NamedAttributeNode("restrictedTo"),
            @NamedAttributeNode("acl")
         }) })

@Entity
@Table(name = "OBJECT_TO_PROTECT")
public class ObjectToProtect
{
   public static final String BRIEF = "ObjectToProtect.brief";
   public static final String FULL  = "ObjectToProtect.full";
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   protected Long            id;
   
   @NotNull     (message= "{evidentia.category.required}")
   @Min(value=0, message= "{evidentia.category.minvalue}")
   @Max(value=5, message= "{evidentia.category.maxvalue}")
   protected Integer         category;      // Security category

   @ManyToOne
   protected SingleUser      userOwner;     // User that owns this object

   @ManyToOne
   protected Role            roleOwner;     // Role that owns this object

   @ManyToOne
   protected UserGroup       restrictedTo;  // UserGroup that owns this object

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @JoinColumn(name="role_id")
   @BatchSize(size = 20)
   protected Set<Role>  acl;               // Access control list

   // -------------- Constructors -------------
   public ObjectToProtect()
   {
      super();
      init();
   }//ObjectToProtect

   private void init()
   {
      id           = null;
      category     = Constant.DEFAULT_CATEGORY;
      userOwner    = null;
      roleOwner    = null;
      restrictedTo = null;
      acl          = new TreeSet<>();
   }//init


   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      if( category == null)
         category = Constant.DEFAULT_CATEGORY;
      
   }//prepareData

   // ----------------- Getters & Setters ----------------
   public Long            getId() { return id;}
   
   public Integer         getCategory() {return category;}
   public void            setCategory(Integer category) {this.category = category;}

   public SingleUser      getUserOwner() {return userOwner;}
   public void            setUserOwner(SingleUser userOwner) {this.userOwner = userOwner;}

   public Role            getRoleOwner() {return roleOwner;}
   public void            setRoleOwner(Role roleOwner) {this.roleOwner = roleOwner;}

   public UserGroup       getRestrictedTo() {return restrictedTo;}
   public void            setRestrictedTo(UserGroup restrictedTo) {this.restrictedTo = restrictedTo;}

   public Set<Role> getAcl() {return acl;}
   public void            setAcl( Set<Role> acl) {this.acl = acl;}

   // ---------------------- Object -----------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ObjectToProtect ))
         return false;

      ObjectToProtect that = (ObjectToProtect) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 7: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" id["+            id+ "]")
       .append(" category["+      category+ "]")
       .append(" userOwner["+    (userOwner    == null? "---": userOwner.getCode())+ "]")
       .append(" roleOwner["+    (roleOwner    == null? "---": roleOwner.getCode())+ "]")
       .append(" restrictedTo["+ (restrictedTo == null? "---": restrictedTo.getCode())+ "]}\n\tAcl{");

      int i = 1;
      for( Role r: acl)
      {
         s.append((i % 5 == 0? "\n\t   ": " "))
          .append(r.getCode());
         i++;
      }
      s.append("\n\t   }\n");

      return s.toString();
   }//toString

   // -----------------  Logic ----------------
   public boolean isPersisted() { return id != null;}
   
   public boolean canBeAccessedBy(Integer userCategory) { return userCategory != null && category.compareTo(userCategory) <= 0;}

   public boolean isOwnedBy( SingleUser user)           { return userOwner    != null && userOwner.equals(user);}

   public boolean isOwnedBy( Role role)                 { return role         != null && roleOwner.equals(role);}
   
   public boolean isRestrictedTo( UserGroup userGroup)  { return restrictedTo != null && restrictedTo.equals(userGroup);}

   public void grant( Role role)                        { acl.add(role); }

   public void revoke( Role role)                       { acl.remove(role);}

   public boolean admits( Role role)
   {
      for( Role r: acl)
      {
         if ( r.equals( role) )
            return true;
      }
      return false;
   }

}//ObjectToProtect
