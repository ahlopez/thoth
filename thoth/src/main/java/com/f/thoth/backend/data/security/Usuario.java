package com.f.thoth.backend.data.security;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.BaseEntity;

/**
 *  Representa un usuario sencillo o compuesto (grupo de usuarios) del sistema
 */
@MappedSuperclass
public abstract class Usuario extends BaseEntity implements NeedsProtection, Comparable<Usuario>
{
   private static final long DEFAULT_TO_DATE = 90L;

   @NotNull(message  = "{evidentia.name.required}")
   @NotEmpty(message = "{evidentia.name.required}")
   @NotBlank(message = "{evidentia.name.required}")
   @Size(min = 1, max = 255, message="{evidentia.name.min.max.length}")
   protected String            name;                 // user first name

   @NotNull     (message= "{evidentia.category.required}")
   @Min(value=0, message= "{evidentia.category.minvalue}")
   @Max(value=5, message= "{evidentia.category.maxvalue}")
   protected Integer           userCategory;         // Security category (User level)

   @NotNull(message = "{evidentia.objectToProtect.required}")
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect   objectToProtect;      // Associated security object that protects the Usuario

   @NotNull(message = "{evidentia.date.required}")
   @PastOrPresent(message="{evidentia.date.pastorpresent}")
   protected LocalDate         fromDate;             // Initial date it can be used. default = now

   @NotNull(message = "{evidentia.date.required}")
   protected LocalDate         toDate;               // Final date it can be used. default end of year

   @OneToMany( cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  // @JoinColumn(name="role_id")
   @BatchSize(size = 20)
   @Valid
   protected Set<Role>         roles;                // Roles assigned to it

   protected boolean           locked;               // Is the user locked?

   // ----------------- Constructor -----------------
   public Usuario()
   {
      super();

      name            = "[name]";
      locked          = false;
      fromDate        = yearStart();
      userCategory    = Parm.DEFAULT_CATEGORY;          // The default category of this Usuario
      toDate          = yearStart().plusYears(1);
      roles           = new TreeSet<>();
      objectToProtect = new ObjectToProtect();
      objectToProtect.setCategory(Parm.ADMIN_CATEGORY); // The min category of a user to be allowed to operate on this Usuario
   }//Usuario

   
   public void prepareData()
   {
      this.fromDate  =  fromDate  != null ? fromDate : LocalDate.MIN;
      this.toDate    =  toDate    != null ? toDate   : LocalDate.now().plusDays(DEFAULT_TO_DATE);
      this.locked    =  isLocked();
   }//prepareData
   

   private LocalDate yearStart()
   {
      LocalDate now = LocalDate.now();
      return now.minusDays(now.getDayOfYear());
   }//yearStart


   // --------------- Getters & Setters -----------------

   public String     getName()   { return name;}
   public void       setName(String name) { this.name = name;}

   public void       setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect= objectToProtect;}

   public Integer    getCategory() {return userCategory;}
   public void       setCategory(Integer userCategory) {this.userCategory = userCategory;}

   public LocalDate  getFromDate() {   return fromDate;}
   public void       setFromDate(LocalDate fromDate) { this.fromDate = fromDate;}

   public LocalDate  getToDate() { return toDate; }
   public void       setToDate(LocalDate toDate) { this.toDate = toDate; }

   public Set<Role>  getRoles() { return roles;}
   public void       setRoles(Set<Role> roles) { this.roles = roles;}

   public boolean    isLocked()
   {
      if (locked)
         return true;

      if (fromDate != null && toDate != null)
      {
         LocalDate now = LocalDate.now();
         return now.compareTo(fromDate) < 0 || now.compareTo(toDate) > 0;
      }
      return false;
   }
   public void       setLocked(boolean locked) { this.locked = locked;}

   // -----------------  Implements NeedsProtection ----------------

   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( User user)                 { return objectToProtect.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

   @Override public void            grant( Permission  permission)        { objectToProtect.grant(permission);}

   @Override public void            revoke(Permission permission)         { objectToProtect.revoke(permission);}

   // --------------- Object ------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Usuario ))
         return false;

      Usuario that = (Usuario) o;
        return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return 70007; }

   @Override
   public String toString() { return " Usuario{" + super.toString()+ " tenant["+ tenant.getName()+ "] locked["+ isLocked()+ "]"+ "] name[" + name+ "]}" ; }

   @Override
   public int compareTo(Usuario that)
   {
      return this.equals(that)?  0 :
         that ==  null        ?  1 :
         this.code == null  && that.code == null?  0 :
         this.code != null  && that.code == null?  1 :
         this.code == null  && that.code != null? -1 :
         this.code.compareTo(that.code);

   }// compareTo

   // --------------- function ----------------

   public void grantRole( Role role)
   {
      if ( role != null)
         roles.add(role);
   }//addToRole

   public void revokeRole( Role role)
   {
      roles.remove(role);
   }

   public abstract boolean canAccess( NeedsProtection object);

   /*
   public String getRole()
   {
      StringBuilder s = new StringBuilder();
      for ( Role r: roles)
      {   s.append(s.length() > 0? ",": "")
         .append(r.getName());
      }
      return s.toString();
   }//getRole
   */

   //TODO: Implement getRole() as defined above
   public String getRole() {
      return "admin";
   }


   public void setRole(String roleName)
   {
      roles.add(new Role(roleName));
   }//setRole


}//grantRole
