package com.f.thoth.backend.data.security;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un rol que tiene permisos sobre los objetos que requieren protección
 */
@Entity
@Table(name = "ROLE", indexes = { @Index(columnList = "code") })
public class Role extends BaseEntity implements Comparable<Role>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotEmpty(message = "{evidentia.name.required}")
   @Size(max = 50)
   private String name;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 50)
   @Valid
   private Set<Permission> permissions;

   public Role()
   {
      super();
      allocate();
      buildCode();
   }

   public Role( String name)
   {
      super();
      if ( !TextUtil.isValidName( name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");

      this.name = TextUtil.nameTidy(name);
      allocate();
      buildCode();
   }//Role

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name   = name != null ? name.trim() : "Anonimo";
      buildCode();
   }//prepareData

   @Override protected void buildCode(){ this.code = (tenant == null? "[Tenant]": tenant.getCode())+ ">"+ this.name; }

   private void allocate() { this.permissions = new TreeSet<>(); }

   // -------------- Getters & Setters ----------------

   public String       getName()  { return name;}
   public void         setName( String name)
   {
      this.name = name;
      buildCode();
   }//setName

   public Set<Permission> getPermissions() { return permissions;}
   public void setPermissions( Set<Permission> permissions) { this.permissions = permissions;}

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if ( !(o instanceof Role))
         return false;

      Role that = (Role) o;

      return Objects.equals(this.tenant, that.tenant) &&
             Objects.equals(this.name,   that.name);

   }// equals

   @Override
   public int hashCode() { return Objects.hash(this.tenant, this.name); }

   @Override
   public String toString() { return "Role{"+ super.toString()+ " name["+ name+ "] permissions["+ permissions.size()+ "]}";}

   @Override
   public int compareTo(Role that)
   {
     return that == null? 1 :
            this.equals(that)? 0 :
            (this.tenant.toString()+ ":"+ this.name).compareTo(that.tenant.toString()+ ":"+ that.name);

   }// compareTo

   // --------------- Logic ---------------------

   /**
    * Determina si el rol puede acceder a un objeto protegido
    * @param object Objeto que requiere protección
    */
   public boolean canAccess(NeedsProtection object)
   {
      if (object == null)
         return false;

      if( object.isOwnedBy(this))
         return true;

      for(Permission p: permissions)
      {
         if ( p.getObjectToProtect().getCode().equals(object.getKey()) && p.isCurrent())
          return true;
      }

      return  false;
   }//canAccess


}//Role

