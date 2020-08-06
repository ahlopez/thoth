package com.f.thoth.backend.data.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un objeto que requiere protección
 */
@Entity
@Table(name = "object_to_protect", indexes = { @Index(columnList = "code") })
public class ObjectToProtect extends BaseEntity  implements NeedsProtection, Comparable<ObjectToProtect>
{
   @NotNull  (message = "{evidentia.name.required}")
   @NotBlank (message = "{evidentia.name.required}")
   @Size(max = 255)
   @Column(unique = true)
   protected String     name;

   @NotNull (message = "{evidentia.category.required}")
   protected Integer    category;

   @ManyToOne
   protected SingleUser    userOwner;

   @ManyToOne
   protected Role          roleOwner;

   // -------------- Constructors -------------
   public ObjectToProtect()
   {
      super();
      buildCode();
   }//ObjectToProtect

   public ObjectToProtect( String name)
   {
      super();
      if ( TextUtil.isEmpty(name))
         throw new IllegalArgumentException("Nombre del objeto no puede ser nulo ni vacío");

      this.name = name;
      buildCode();
   }//ObjectToProtect


    @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name     =  TextUtil.nameTidy(name).toLowerCase();
      buildCode();
   }//prepareData

   @Override public void buildCode()
   {
      this.code = (tenant == null? "[Tenant]": tenant.getCode())+
                 ">"+
                   (name == null? "[name]" : name);
   }

   // ----------------- Getters & Setters ----------------

   public String  getName() { return name;}
   public void    setName(String name) { this.name = name;}

   public SingleUser getUserOwner() {return userOwner;}
   public void setUserOwner(SingleUser userOwner) {this.userOwner = userOwner;}

   public Role getRoleOwner() {return roleOwner;}
   public void setRoleOwner(Role roleOwner) {this.roleOwner = roleOwner;}

   public Integer getCategory() {return category;}
   public void setCategory(Integer category) {this.category = category;}

   // ---------------------- Object -----------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      ObjectToProtect that = (ObjectToProtect) o;
      return this.tenant.equals(that.tenant) && this.name.equals(that.name);
   }//equals

   @Override public int hashCode() { return tenant.hashCode()* 1023+ name.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" ObjectToProtect{"+ super.toString())
      .append(" name["+ name+ "]")
      .append(" category["+ category+ "]")
      .append(" userOwner["+ userOwner == null? "-NO-": userOwner.getCode()+ "]")
      .append(" roleOwner["+ roleOwner == null? "-NO-": roleOwner.getCode()+ "]}");

      return s.toString();
   }//toString

   @Override public int compareTo(ObjectToProtect other){ return other == null? 1 : this.code.compareTo(other.code); }

   // -----------------  Logic ----------------
   @Override public String  getKey() { return code;}

   @Override public boolean canBeAccessedBy(Integer userCategory) { return userCategory != null && category.compareTo(userCategory) <= 0;}

   @Override public boolean isOwnedBy( SingleUser user) { return userOwner != null && user != null && userOwner.equals(user);}

   @Override public boolean isOwnedBy( Role role) { return roleOwner != null && role != null && roleOwner.equals(role);}

}//ObjectToProtect
