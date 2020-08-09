package com.f.thoth.backend.data.security;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un usuario autenticado del sistema
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = SingleUser.BRIEF,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("lastName"),
               @NamedAttributeNode("email"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("locked")
         }),
   @NamedEntityGraph(
         name = SingleUser.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("lastName"),
               @NamedAttributeNode("email"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("locked"),
               @NamedAttributeNode("roles"),
               @NamedAttributeNode("groups")
         }) })
@Entity
@Table(name = "SINGLE_USER", indexes = { @Index(columnList = "email"), @Index(columnList = "lastName, name") })
public class SingleUser extends Usuario implements Comparable<SingleUser>
{

   public static final String BRIEF = "SingleUser.brief";
   public static final String FULL  = "SingleUser.full";

   @NotNull(message  = "{evidentia.email.required}")
   @NotEmpty(message = "{evidentia.email.required}")
   @Email
   @Size(min=3, max = 255, message="{evidentia.email.length}")
   @Column(unique = true)
   protected String email;        // user email

   @NotNull
   @Size(min = 4, max = 255)
   protected String passwordHash; // user password

   @NotNull (message = "{evidentia.lastname.required}")
   @NotBlank(message = "{evidentia.lastname.required}")
   @NotEmpty(message = "{evidentia.lastname.required}")
   @Size(min= 2, max = 255, message= "{evidentia.lastname.length}")
   protected String lastName;  // User last name

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 10)
   @Valid
   protected Set<UserGroup>  groups; // groups it belongs


   // ----------------- Constructor -----------------
   public SingleUser()
   {
      super();
      email     = "";
      lastName  = "";
      groups    = new TreeSet<>();
      buildCode();
   }

   public SingleUser( String email, String passwordHash, String lastName)
   {
      super();

      if ( !TextUtil.isValidEmail(email))
         throw new IllegalArgumentException("Email["+ email+ "] inválido");

      if (passwordHash ==  null)
         throw new IllegalArgumentException("Password inválido");

      if (TextUtil.isEmpty(lastName))
         throw new IllegalArgumentException("Apellido["+ lastName+ "] inválido");

      this.email        = email;
      this.passwordHash = passwordHash;
      this.lastName     = lastName;
      buildCode();

   }//SingleUser

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      super.prepareData();
      this.email     =  email     != null ? email.trim().toLowerCase(): null ;
      this.lastName  =  TextUtil.nameTidy(lastName);
      buildCode();

   }//prepareData

   @Override protected void buildCode(){this.code = (tenant == null? "[Tenant]": tenant.getCode())+ ">"+ (email==null? "[email]": email);}

   // --------------- Getters & Setters -----------------
   public String getPasswordHash() { return passwordHash;}
   public void   setPasswordHash(String passwordHash) { this.passwordHash = passwordHash;}

   public String getLastName() { return lastName;}
   public void   setLastName(String lastName) { this.lastName = lastName;}
   public String getFullName() { return lastName+ " "+ name;}

   public String getEmail() { return email;}
   public void   setEmail(String email)
   {
      this.email = email;
      buildCode();
   }// setEmail

   public Set<UserGroup> getGroups() { return groups;}
   public void           setGroups( Set<UserGroup> groups) { this.groups = groups;}


   // --------------- Object ------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof SingleUser ))
         return false;

      SingleUser that = (SingleUser) o;
        return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return 257;}

   @Override
   public String toString() { return "SingleUser{" + super.toString() + " lastName[" + lastName + "] email[" + email + "]}";}

   @Override
   public int compareTo(SingleUser that)
   {
      return this.equals(that)? 0 :
         that == null ?     1 :
            (this.lastName + ":" + this.name).compareTo(that.lastName + ":" + that.name);

   }// compareTo


   // --------------- Logic ---------------------

   public void addToGroup( UserGroup group) { groups.add( group); }

   @Override public boolean canAccess( NeedsProtection object)
   {
      if ( object.isOwnedBy( this))
         return true;

      if ( ! object.canBeAccessedBy( this.category))
         return false;

      for ( Role r : roles)
      {
         if ( r.canAccess( object))
            return true;
      }

      for (UserGroup ug: groups)
      {
         if (ug.canAccess(object))
            return true;
      }

      return false;

   }//canAccess

}//SingleUser