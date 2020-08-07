package com.f.thoth.backend.data.security;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;

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
   @ManyToOne
   public ObjectToProtect objectToProtect;

   @NotNull(message = "{evidentia.date.required}")
   private LocalDateTime fromDate;

   @NotNull(message = "{evidentia.date.required}")
   private LocalDateTime toDate;

   @NotNull(message = "{evidentia.user.required}")
   @ManyToOne
   private SingleUser  grantedBy;

   // ------------- Constructors ------------------
   public Permission()
   {
      super();
      buildCode();
   }

   public Permission( Integer category, Role role, ObjectToProtect objectToProtect,
         LocalDateTime fromDate, LocalDateTime toDate, SingleUser grantedBy)
   {
      super();
      if (category < 0 || category > 5)
         throw new IllegalArgumentException("Categoría["+ category+ "] inválida");

      if (role == null)
         throw new IllegalArgumentException("Rol a quien se concede el permiso no puede ser nulo");

      if (objectToProtect == null)
         throw new IllegalArgumentException("Objeto del permiso no puede ser nulo");

      this.role     = role;
      this.fromDate = fromDate;
      this.toDate   = toDate;
      this.grantedBy= grantedBy;
      this.objectToProtect   = objectToProtect;
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
      this.code = (tenant == null? "[Tenant]" : tenant.getCode())+ ">"+
                  (role ==  null? "[role]": role.getCode())+ ":"+ 
    		      (objectToProtect == null? "[object]" : objectToProtect.getCode());
   }//buildCode

   // -------------- Getters & Setters ----------------

   public Role            getRole() { return role;}
   public void            setRole(Role role) { this.role = role;}

   public LocalDateTime   getFromDate() { return fromDate; }
   public void            setFromDate(LocalDateTime fromDate) { this.fromDate = fromDate; }

   public LocalDateTime   getToDate() { return toDate; }
   public void            setToDate(LocalDateTime toDate) { this.toDate = toDate; }

   public SingleUser      getGrantedBy() { return grantedBy;}
   public void            setGrantedBy( SingleUser grantedBy){ this.grantedBy = grantedBy;}

   public ObjectToProtect getObjectToProtect() { return objectToProtect;}
   public void            setObjectToProtect( ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect;}

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
   public int hashCode() { return 2047; }

   @Override
   public String toString() { return "Permission{ role["+ role.getName()+ "] object["+ objectToProtect.toString()+ "] from["+  fromDate+ "] to["+ toDate+ "] grantedBy["+ grantedBy.getEmail()+ "]}";}

   @Override
   public int compareTo(Permission that)
   {
     if (that == null)
        return 1;

     if (this.equals(that))
        return 0;

     String key1 = this.role.getName()+ ":"+ this.objectToProtect+ this.fromDate+ ":"+ this.toDate;
     String key2 = this.role.getName()+ ":"+ that.objectToProtect+ that.fromDate+ ":"+ that.toDate;
     return key1.compareTo(key2);

   }// compareTo

   // --------------- Logic ---------------------

   public boolean isCurrent()
   {
      LocalDateTime now = LocalDateTime.now();
      return now.compareTo(fromDate) >= 0 && now.compareTo(toDate) <= 0;
   }//isCurrent

}//Permission