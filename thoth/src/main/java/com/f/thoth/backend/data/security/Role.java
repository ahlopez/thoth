package com.f.thoth.backend.data.security;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
   @Size(max = 80)
   private String name;

   public Role()
   {
      super();
      name = "";
      buildCode();
   }

   public Role( String name)
   {
      super();
      if ( !TextUtil.isValidName( name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");

      this.name = TextUtil.nameTidy(name);
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

   // -------------- Getters & Setters ----------------

   public String       getName()  { return name;}
   public void         setName( String name)
   {
      this.name = name;
      buildCode();
   }//setName

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Role ))
         return false;

      Role that = (Role) o;
        return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return id == null? 13: id.hashCode(); }

   @Override
   public String toString() { return "Role{"+ super.toString()+ " name["+ name+ "]}\n";}

   @Override
   public int compareTo(Role that)
   {
      return this.equals(that)?  0 :
         this.code == null  && that.code == null?  0 :
         this.code != null  && that.code == null?  1 :
         this.code == null  && that.code != null? -1 :
         this.code.compareTo(that.code);

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

      return object.admits(this);
   }//canAccess


}//Role

