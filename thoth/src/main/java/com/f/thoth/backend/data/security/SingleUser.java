package com.f.thoth.backend.data.security;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un usuario autenticado del sistema
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = SingleUser.BRIEF,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("firstName"),
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
               @NamedAttributeNode("firstName"),
               @NamedAttributeNode("lastName"),
               @NamedAttributeNode("email"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("locked"),
               @NamedAttributeNode("roles"),
               @NamedAttributeNode("groups")
         }) })
@Entity
@Table(name = "SINGLE_USER", indexes = { @Index(columnList = "email"), @Index(columnList = "lastName,firstName") })
public class SingleUser extends Usuario implements Comparable<SingleUser>
{

   public static final String BRIEF = "SingleUser.brief";
   public static final String FULL  = "SingleUser.full";

   @NotEmpty(message = "{evidentia.email.required}")
   @Email
   @Size(min=3, max = 255, message="{evidentia.email.length}")
   @Column(unique = true)
   private String email;

   @NotNull
   @Size(min = 4, max = 255)
   private String passwordHash;

   @NotBlank(message = "{evidentia.lastname.required}")
   @NotEmpty(message = "{evidentia.lastname.required}")
   @Size(min= 2, max = 255, message= "{evidentia.lastname.length}")
   private String lastName;


   // ----------------- Constructor -----------------
   public SingleUser()
   {
      super();
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
   public String getFullName() { return lastName+ " "+ firstName;}

   public String getEmail() { return email;}
   public void   setEmail(String email)
   {
      this.email = email;
      buildCode();
   }// setEmail

   // --------------- Object ------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      if (!super.equals(o))
         return false;

      SingleUser that = (SingleUser) o;
      return Objects.equals(email, that.email) && Objects.equals(lastName, that.lastName);

   }// equals

   @Override
   public int hashCode() { return Objects.hash(super.hashCode(), email, lastName);}

   @Override
   public String toString() { return "SingleUser{" + super.toString() + " lastName[" + lastName + "] email[" + email + "]}";}

   @Override
   public int compareTo(SingleUser that)
   {
      return this.equals(that)? 0 :
         that == null ?     1 :
            (this.lastName + ":" + this.firstName).compareTo(that.lastName + ":" + that.firstName);

   }// compareTo


   // --------------- Logic ---------------------
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