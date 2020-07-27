package com.f.thoth.backend.data.security;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.AbstractEntity;

/**
 * Representa un permiso de acceso a un objeto que requiere protección
 */
@Entity
@Table(name = "PERMISSION", indexes = { @Index(columnList = "code") })
public class Permission extends AbstractEntity implements Comparable<Permission>
{

   @NotNull(message = "{evidentia.role.required}")
   private Role          role;

   @NotNull(message = "{evidentia.protected.required}")
   public NeedsProtection object;

   @NotNull(message = "{evidentia.date.required}")
   private LocalDateTime fromDate;

   @NotNull(message = "{evidentia.date.required}")
   private LocalDateTime toDate;

   @NotNull(message = "{evidentia.usuer.required}")
   private SingleUser  grantedBy;

   // ------------- Constructors ------------------
   public Permission()
   {
      super();
   }

   public Permission( Integer category, Role role, NeedsProtection object, LocalDateTime fromDate, LocalDateTime toDate, SingleUser grantedBy)
   {
      super();
      if (category < 0 || category > 5)
         throw new IllegalArgumentException("Categoría["+ category+ "] inválida");

      if (object == null)
         throw new IllegalArgumentException("Objeto del permiso no puede ser nulo");

      this.role     = role;
      this.object   = object;
      this.fromDate = fromDate;
      this.toDate   = toDate;
      this.grantedBy= grantedBy;
      buildCode();
   }//Permission

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      buildCode();
   }//prepareData

   private void buildCode(){ this.code = tenant.toString()+ ":"+ role.getCode()+ ">"+ object.getKey(); }

   // -------------- Getters & Setters ----------------

   public Role            getRole() { return role;}
   public void            setRole(Role role) { this.role = role;}

   public LocalDateTime   getFromDate() { return fromDate; }
   public void            setFromDate(LocalDateTime fromDate) { this.fromDate = fromDate; }

   public LocalDateTime   getToDate() { return toDate; }
   public void            setToDate(LocalDateTime toDate) { this.toDate = toDate; }

   public SingleUser      getGrantedBy() { return grantedBy;}
   public void            setGrantedBy( SingleUser grantedBy){ this.grantedBy = grantedBy;}

   public NeedsProtection getObject() { return object;}
   public void            setObject( NeedsProtection object) { this.object = object;}

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      Permission that = (Permission) o;

      return this.role.equals(that.role) && this.object.equals(that.object) && this.fromDate.equals(that.fromDate) && this.toDate.equals(that.toDate);

   }// equals

   @Override
   public int hashCode() { return Objects.hash(role.hashCode(), object.hashCode(), fromDate, toDate); }

   @Override
   public String toString() { return "Permission{ role["+ role.getName()+ "] object["+ object.toString()+ "] from["+  fromDate+ "] to["+ toDate+ "] grantedBy["+ grantedBy.getEmail()+ "]}";}

   @Override
   public int compareTo(Permission that)
   {
     if (that == null)
        return 1;

     if (this.equals(that))
        return 0;

     String key1 = this.role.getName()+ ":"+ this.object.getKey()+ this.fromDate+ ":"+ this.toDate;
     String key2 = this.role.getName()+ ":"+ that.object.getKey()+ that.fromDate+ ":"+ that.toDate;
     return key1.compareTo(key2);

   }// compareTo

   // --------------- Logic ---------------------

   public boolean isCurrent()
   {
      LocalDateTime now = LocalDateTime.now();
      return now.compareTo(fromDate) >= 0 && now.compareTo(toDate) <= 0;
   }//isCurrent

}//Permission