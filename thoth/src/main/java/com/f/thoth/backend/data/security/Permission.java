package com.f.thoth.backend.data.security;

import static com.f.thoth.Parm.CURRENT_USER;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.vaadin.flow.server.VaadinSession;

/**
 * Representa un permiso de acceso a un objeto que requiere protección
 */
@Entity
@Table(name = "PERMISSION", indexes = { @Index(columnList = "code") })
public class Permission extends BaseEntity implements Comparable<Permission>
{
   @NotNull(message = "{evidentia.role.required}")
   @ManyToOne
   private Role          role;                          // rol a quien se concede el permiso

   @NotNull (message = "{evidentia.object.required}")
   @ManyToOne
   public ObjectToProtect  objectToProtect;             // objeto sobre el que se concede permiso de acceso

   @NotNull(message = "{evidentia.date.required}")
   private LocalDate     fromDate;                      // fecha inicial del período de concesión (inclusive)

   @NotNull(message = "{evidentia.date.required}")
   private LocalDate     toDate;                        // fecha final del período de concesión (inclusive)

   @NotNull(message = "{evidentia.user.required}")
   @ManyToOne
   private User          grantedBy;                     // usuario que concede el permiso

   // ------------- Constructors ------------------
   public Permission()
   {  super();
      init();
   }

   public Permission( Tenant tenant, Role role, ObjectToProtect objectToProtect, LocalDate fromDate, LocalDate toDate)
   {
      super();

      if (tenant == null)
         throw new IllegalArgumentException("Tenant que concede el permiso no puede ser nulo");

      if (role == null)
         throw new IllegalArgumentException("Rol a quien se concede el permiso no puede ser nulo");

      if (objectToProtect == null)
         throw new IllegalArgumentException("Objeto del permiso no puede ser nulo");

      this.tenant            = tenant;
      this.role              = role;
      this.fromDate          = fromDate;
      this.toDate            = toDate;
      this.objectToProtect   = objectToProtect;
      this.grantedBy         = (User)VaadinSession.getCurrent().getAttribute(CURRENT_USER);
   }//Permission
   
   
   private void init()
   {
      if (this.grantedBy == null)
      {  VaadinSession vSession = VaadinSession.getCurrent();
         this.grantedBy = vSession == null? null: (User)vSession.getAttribute(CURRENT_USER);
      }
   }//init

   
   @PrePersist
   @PreUpdate
   public void prepareData()
   {  buildCode();
   }//prepareData

   
   @Override protected void buildCode()
   {
      if (this.code == null)
      {
          this.code = (tenant == null? "[Tenant]" : tenant.getId())+ ">"+
                      (role ==  null? "[role]": role.getId())+ ":"+
                      (objectToProtect == null? "[object]" : objectToProtect.getId());
      }
   }//buildCode

   // -------------- Getters & Setters ----------------

   public Role            getRole() { return role;}
   public void            setRole(Role role) { this.role = role;}

   public LocalDate       getFromDate() { return fromDate; }
   public void            setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

   public LocalDate       getToDate() { return toDate; }
   public void            setToDate(LocalDate toDate) { this.toDate = toDate; }

   public User            getGrantedBy() { return grantedBy;}
   public void            setGrantedBy( User grantedBy){ this.grantedBy = grantedBy;}

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
   public int hashCode() { return id == null? 11: id.hashCode(); }

   @Override
   public String toString() 
   { 
      return "Permission{ "+ super.toString()+ 
             " role["+ role.getName()+ "]\n\t"+
             " object["+ objectToProtect.toString()+ "]\n\t"+
             " from["+  fromDate+ "]"+
             " to["+ toDate+ "]"+
             " grantedBy["+ grantedBy.getEmail()+ "]}\n";
   }//toString

   @Override
   public int compareTo(Permission that)
   {
      return this.equals(that)?  0 :
         this.code == null  && that.code == null?  0 :
         this.code != null  && that.code == null?  1 :
         this.code == null  && that.code != null? -1 :
         this.code.compareTo(that.code);

   }// compareTo

   // --------------- Logic ---------------------

   public boolean grants( Role role, ObjectToProtect objectWanted)
   {
      LocalDate now = LocalDate.now();
      return this.role.equals(role) &&
             this.objectToProtect.equals(objectWanted) &&
             now.compareTo(fromDate) >= 0 && now.compareTo(toDate) <= 0;
   }//grants

}//Permission