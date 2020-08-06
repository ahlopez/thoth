package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;

import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.Usuario;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa un expediente documental
 */
public abstract class Expediente implements NeedsProtection, Comparable<Expediente>
{
   private Tenant        tenant;
   private String        id;
   private FileIndex     index;
   private DocType       attributes;
   private Usuario       userOwner;
   private Role          roleOwner;
   private Integer       category;
   private LocalDateTime openingDate;
   private LocalDateTime closingDate;
   private boolean       open;

   // --------------- Constructors --------------------
   public Expediente()
   {
      this.tenant = ThothSession.getCurrentTenant();
   }

   // ---------------- Getters & Setters --------------

   public Tenant getTenant() { return tenant; }
   //public void setTenant(Tenant tenant) {this.tenant = tenant;}

   public String getId() {return id;}
   public void setId(String id) {this.id = id;}

   public FileIndex getIndex() {return index;}
   public void setIndex(FileIndex index) {this.index = index;}

   public DocType getAttributes() {return attributes;}
   public void setAttributes(DocType attributes) {this.attributes = attributes;}

   public Usuario getUserOwner() {return userOwner;}
   public void setUserOwner(Usuario userOwner) {this.userOwner = userOwner;}

   public Role getRoleOwner() {return roleOwner;}
   public void setRoleOwner(Role roleOwner) {this.roleOwner = roleOwner;}

   public Integer getCategory() {return category;}
   public void setCategory(Integer category) {this.category = category;}

   public LocalDateTime getOpeningDate() {return openingDate;}
   public void setOpeningDate(LocalDateTime openingDate) {this.openingDate = openingDate;}

   public LocalDateTime getClosingDate() {return closingDate;}
   public void setClosingDate(LocalDateTime closingDate) {this.closingDate = closingDate;}

   public void setOpen(boolean open) {this.open = open;}


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


}//Expediente