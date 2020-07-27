package com.f.thoth.backend.data.security;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un rol que tiene permisos sobre los objetos que requieren protección
 */
@Entity
@Table(name = "ROLE", indexes = { @Index(columnList = "tenant,name") })
public class Role extends AbstractEntity implements Comparable<Role>
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
   private Map<NeedsProtection,Permission> permissions;

   public Role()
   {
      super();
      allocate();
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
      this.name = name != null ? name.trim() : "Anonimo";
      buildCode();
   }//prepareData

   private void buildCode(){ this.code = this.name; }

   private void allocate() { this.permissions = new TreeMap<>(); }

   // -------------- Getters & Setters ----------------

   public String       getName()  { return name;}
   public void         setName( String name)
   {
      this.name = name;
      buildCode();
   }//setName

   public Map<NeedsProtection,Permission> getPermissions() { return permissions;}
   public void setPermissions( Map<NeedsProtection,Permission> permissions) { this.permissions = permissions;}

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      Role that = (Role) o;

      return Objects.equals(tenant, that.tenant) &&
             Objects.equals(name,   that.name);

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
      if( object.isOwnedBy(this))
         return true;

      Permission permission = permissions.get(object);
      return  permission != null && permission.isCurrent();
   }//hasPermission

}//Role

