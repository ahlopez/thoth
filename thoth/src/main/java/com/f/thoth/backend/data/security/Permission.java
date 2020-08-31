package com.f.thoth.backend.data.security;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.User;

/**
 * Representa un permiso de acceso a un objeto que requiere protección
 */
@Entity
@Table(name = "PERMISSION", indexes = { @Index(columnList = "code") })
public class Permission extends BaseEntity implements Comparable<Permission>
{
   @NotNull(message = "{evidentia.role.required}")
   @ManyToOne
   private Role          role;

   @NotNull (message = "{evidentia.object.required}")
   public String        objectToProtect;            

   @NotNull(message = "{evidentia.date.required}")
   private LocalDate     fromDate;

   @NotNull(message = "{evidentia.date.required}")
   private LocalDate     toDate;

   @NotNull(message = "{evidentia.user.required}")
   @ManyToOne
   private User  grantedBy;

   // ------------- Constructors ------------------
   public Permission()
   {
      super();
      this.grantedBy = ThothSession.getCurrentUser();
      buildCode();
   }

   public Permission( Role role, String objectToProtect, LocalDate fromDate, LocalDate toDate)
   {
      super();
      
      if (role == null)
         throw new IllegalArgumentException("Rol a quien se concede el permiso no puede ser nulo");

      if (objectToProtect == null)
         throw new IllegalArgumentException("Objeto del permiso no puede ser nulo");

      this.role              = role;
      this.fromDate          = fromDate;
      this.toDate            = toDate;
      this.objectToProtect   = objectToProtect;
      this.grantedBy         = ThothSession.getCurrentUser();
      buildCode();
   }//Permission

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      buildCode();
   }//prepareData

   @Override protected void buildCode()
   {
      this.code = (tenant == null? "[Tenant]" : tenant.getCode())+ "[PRM]>"+
                  (role ==  null? "[role]": role.getCode())+ ":"+ 
    		         (objectToProtect == null? "[object]" : objectToProtect);
   }//buildCode

   // -------------- Getters & Setters ----------------
   
   public Long getObjectId( )
   {
      if( objectToProtect == null)
         return Long.MIN_VALUE;
      
      Long id = Long.valueOf(objectToProtect.substring(objectToProtect.indexOf(']')+1));
      return id;
   }//getId

   public Role            getRole() { return role;}
   public void            setRole(Role role) { this.role = role;}

   public LocalDate       getFromDate() { return fromDate; }
   public void            setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

   public LocalDate       getToDate() { return toDate; }
   public void            setToDate(LocalDate toDate) { this.toDate = toDate; }

   public User            getGrantedBy() { return grantedBy;}
   public void            setGrantedBy( User grantedBy){ this.grantedBy = grantedBy;}

   public String          getObjectToProtect() { return objectToProtect;}
   public void            setObjectToProtect( String objectToProtect) { this.objectToProtect = objectToProtect;}

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
		if (this == o)
			return true;

		if (!(o instanceof Permission )) 
			return false;

		 Permission that = (Permission) o;
       return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return id == null? 11: id.hashCode(); }

   @Override
   public String toString() { return "Permission{ "+ super.toString()+ " role["+ role.getName()+ "]\n\t object["+ objectToProtect.toString()+ "]\n\t from["+  fromDate+ "] to["+ toDate+ "] grantedBy["+ grantedBy.getEmail()+ "]}";}

   @Override
   public int compareTo(Permission that)
   {
      return this.equals(that)?  0 :
         that ==  null        ?  1 :
         this.code == null  && that.code == null?  0 :   
         this.code != null  && that.code == null?  1 :
         this.code == null  && that.code != null? -1 :   
         this.code.compareTo(that.code);
      

   }// compareTo

   // --------------- Logic ---------------------

   public boolean isCurrent()
   {
      LocalDate now = LocalDate.now();
      return now.compareTo(fromDate) >= 0 && now.compareTo(toDate) <= 0;
   }//isCurrent
   
   public boolean grants( Role role, NeedsProtection object)
   {
      return this.role.equals(role) && this.objectToProtect.equals(object.getKey()) && isCurrent();
   }

}//Permission