package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa un expediente documental
 */
@Entity
@Table(name = "EXPEDIENTE")
public abstract class Expediente implements NeedsProtection, Comparable<Expediente>
{
   @Id
   private String        id;   

   @NotNull(message = "{evidentia.objectToProtect.required") 
   @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;
   
   @ManyToOne
   private Tenant        tenant;
   
   @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
   private FileIndex     index;
   
   @ManyToOne
   private DocType       attributes;
   
   @ManyToOne
   private SingleUser    userOwner;
   
   @ManyToOne
   private Role          roleOwner;
   
   @NotNull(message = "{evidentia.category.required") 
   private Integer       category;
   
   @NotNull(message = "{evidentia.dateopened.required}")
   private LocalDateTime openingDate;
   
   private LocalDateTime closingDate;
   
   private boolean       open;
   
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 20)
   protected Set<Permission>       acl;   // Access control list
   

   // --------------- Constructors --------------------
   public Expediente()
   {
      this.tenant = ThothSession.getCurrentTenant();
   }

   // ---------------- Getters & Setters --------------

   public Tenant getTenant() { return tenant; }
   //public void setTenant(Tenant tenant) {this.tenant = tenant;}

   public FileIndex getIndex() {return index;}
   public void setIndex(FileIndex index) {this.index = index;}

   @Override public ObjectToProtect getObjectToProtect(){ return objectToProtect;}
   public void setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }

   public DocType getAttributes() {return attributes;}
   public void setAttributes(DocType attributes) {this.attributes = attributes;}

   public SingleUser getUserOwner() {return userOwner;}
   public void setUserOwner(SingleUser userOwner) {this.userOwner = userOwner;}

   public Role getRoleOwner() {return roleOwner;}
   public void setRoleOwner(Role roleOwner) {this.roleOwner = roleOwner;}

   public Integer getCategory() {return category;}
   public void setCategory(Integer category) {this.category = category;}

   public LocalDateTime getOpeningDate() {return openingDate;}
   public void setOpeningDate(LocalDateTime openingDate) {this.openingDate = openingDate;}

   public LocalDateTime getClosingDate() {return closingDate;}
   public void setClosingDate(LocalDateTime closingDate) {this.closingDate = closingDate;}

   public void setOpen(boolean open) {this.open = open;}

   public Set<Permission>  getAcl() {return acl;}
   public void             setAcl(Set<Permission> acl) {this.acl = acl;}


   // ------------------ Object ------------------------

   @Override public boolean equals( Object o)
   {
         if (this == o)
             return true;

          if (o == null || getClass() != o.getClass())
             return false;

          Expediente that = (Expediente) o;
          return this.tenant.equals(that.tenant) && this.id.equals(that.id);
   }//equals

   @Override public int hashCode() { return tenant.hashCode()* 1023+ id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" tenant["+ tenant.getCode()+ "]")
       .append(" id["+ id+ "]")
       .append(" n entries["+ index.size()+ "]")
       .append(" docType["+ attributes.getCode()+ "]")
       .append(" userOwner["+ (userOwner != null? userOwner.getCode(): "-NO-")+ "]")
       .append(" roleOwner["+ (roleOwner != null? roleOwner.getCode(): "-NO-")+ "]")
       .append(" category["+ category+ "]")
       .append(" dateOpen["+ openingDate.format(FormattingUtils.FULL_DATE_FORMATTER)+ "]")
       .append(" dateCloses["+ closingDate.format(FormattingUtils.FULL_DATE_FORMATTER)+ "]")
       .append(" isOpen["+ isOpen()+ "]");

      return s.toString();
   }//toString

   @Override public int compareTo(Expediente other){ return other == null? 1 : this.id.compareTo(other.id); }

   public abstract boolean isBranch();

   public abstract boolean isLeaf();

   public abstract boolean isVolume();

   public boolean isOpen()
   {
      LocalDateTime now = LocalDateTime.now();
      return open &&
            ((now.equals(openingDate) || now.equals(closingDate)) ||
                  (now.isAfter(openingDate) && now.isBefore(closingDate))) ;
   }//isOpen

   public boolean canBeAccessedBy(Role role){ return false;}

   public boolean canBeAccessedBy(int category){ return false;}

   @Override public String  getKey() { return tenant.getCode()+ ">"+ id;}

   @Override public boolean canBeAccessedBy(Integer userCategory) { return userCategory != null && category.compareTo(userCategory) <= 0;}

   @Override public boolean isOwnedBy( SingleUser user) { return userOwner != null && user != null && userOwner.equals(user);}

   @Override public boolean isOwnedBy( Role role) { return roleOwner != null && role != null && roleOwner.equals(role);}

   @Override public boolean admits( Role role)
   { 
      for( Permission p: acl)
      {
         if ( p.grants( role, this) )
            return true;
      }
      return false; 
   }

   @Override public void grant( Permission permission) { acl.add(permission);}

   @Override public void revoke( Permission permission) { acl.remove(permission);}


}//Expediente