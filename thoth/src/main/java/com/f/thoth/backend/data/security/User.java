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
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
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
         name = User.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("lastName"),
            @NamedAttributeNode("email"),
            @NamedAttributeNode("passwordHash"),
            @NamedAttributeNode("userCategory"),
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
         name = User.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("lastName"),
               @NamedAttributeNode("email"),
               @NamedAttributeNode("passwordHash"),
               @NamedAttributeNode("userCategory"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("locked"),
               @NamedAttributeNode("roles"),
               @NamedAttributeNode("groups"),
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
@Table(name = "SINGLE_USER", indexes = { @Index(columnList = "tenant_id, email"), @Index(columnList = "tenant_id, lastName, name") })
public class User extends Usuario
{
   public static final String BRIEF = "User.brief";
   public static final String FULL  = "User.full";

   @NotNull(message  = "{evidentia.email.required}")
   @NotEmpty(message = "{evidentia.email.required}")
   @Email
   @Size(min=3, max = 255, message="{evidentia.email.length}")
   protected String email;                                          // user email

   @NotNull
   @Size(min = 4, max = 255)
   protected String passwordHash;                                   // user password

   @NotNull (message = "{evidentia.lastname.required}")
   @NotBlank(message = "{evidentia.lastname.required}")
   @NotEmpty(message = "{evidentia.lastname.required}")
   @Size(min= 2, max = 255, message= "{evidentia.lastname.length}")
   protected String lastName;                                       // User last name

   @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @JoinColumn(name="group_id")
   @BatchSize(size = 10)
   @Valid
   protected Set<UserGroup>  groups;                                // groups it belongs


   // ----------------- Constructor -----------------
   public User()
   {
      super();
      this.email     = "";
      this.lastName  = "";
      this.groups    = new TreeSet<>();
   }//User

   public User( Tenant tenant, String email, String passwordHash, String lastName)
   {
      super();
      
      if ( tenant == null)
          throw new IllegalArgumentException("Tenant del nuevo usuario no puede ser nulo");

      if ( !TextUtil.isValidEmail(email))
         throw new IllegalArgumentException("Email["+ email+ "] inválido");

      if (passwordHash ==  null)
         throw new IllegalArgumentException("Password inválido");

      if (TextUtil.isEmpty(lastName))
         throw new IllegalArgumentException("Apellido["+ lastName+ "] inválido");

      this.tenant       = tenant;
      this.email        = email;
      this.passwordHash = passwordHash;
      this.lastName     = lastName;

   }//User
   

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      super.prepareData();
      this.email     =  email     != null ? email.trim().toLowerCase(): null ;
      this.lastName  =  TextUtil.nameTidy(lastName);
      buildCode();

   }//prepareData
   

   @Override public void buildCode()
   {
      if( this.code == null)
      {  this.code = (tenant == null? "[Tenant]": tenant.getCode())+ ">"+ (email==null? "[email]": email);
      }
   }//buildCode
   

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

      if (!(o instanceof User ))
         return false;

      User that = (User) o;
      return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return id == null? 25777: id.hashCode();}

   @Override
   public String toString() 
   {   
      return "User{" + super.toString() +
              " lastName[" + lastName + "]"+  
              " email[" + email + "]"+
              " groups["+ groups.size()+ "]"+
              " roles["+ roles.size()+ "]}\n";
   }//toString


   // --------------- Logic ---------------------

   public void addToGroup( UserGroup group) { groups.add( group); }

   @Override public boolean canAccess( NeedsProtection object)
   {
      if ( object.isOwnedBy( this))
         return true;

      if ( ! object.canBeAccessedBy( this.userCategory))
         return false;

      boolean roleCan= false;
      for ( Role r : roles)
      {
         if ( r.canAccess( object))
         {  roleCan = true;
            break;
         }
      }
      if ( !roleCan)
         return false;

      if (groups.size() == 0)
         return true;
      
      for (UserGroup ug: groups)
      {
         if (ug.canAccess(object))
            return true;
      }

      return false;

   }//canAccess

}//SingleUser