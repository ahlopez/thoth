package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.DocType;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa un expediente documental
 */
@MappedSuperclass
public abstract class Expediente extends BaseEntity implements NeedsProtection, HierarchicalEntity<BranchExpediente>, Comparable<Expediente>
{

   @NotNull  (message = "{evidentia.name.required}")
   @NotBlank (message = "{evidentia.name.required}")
   @NotEmpty (message = "{evidentia.name.required}")
   @Size(max = 255)
   @Column(unique = true)
   protected String           name;              // Expediente name
   
   @NotNull(message = "{evidentia.objectToProtect.required") 
   @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;  // Objeto asociado de seguridad

   @ManyToOne
   protected BranchExpediente  owner;            // Expediente al que pertenece este expediente
   
   @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
   private FileIndex          index;
   
   @ManyToOne
   private DocType            attributes;
   
   @NotNull(message = "{evidentia.dateopened.required}")
   private LocalDateTime      openingDate;
   
   private LocalDateTime      closingDate;
   
   private boolean            open;
   

   // --------------- Constructors --------------------
   public Expediente()
   {
      this.tenant = ThothSession.getCurrentTenant();
      buildCode();
   }
   
   public Expediente( String name, BranchExpediente owner)
   {
      if( TextUtil.isEmpty(name))
         throw new IllegalArgumentException("Nombre del expediente no puede ser nulo ni vacío");
      
      this.name  = name;
      this.owner = owner;
      
   }//Expediente constructor

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name     =  TextUtil.nameTidy(name).toLowerCase();
      buildCode();
   }//prepareData

   @Override protected void buildCode()
   {
      this.code = (tenant == null? "[tenant]": tenant.getCode())+"[XPE]"+ getOwnerCode()+ ">"+ (name == null? "[name]" : name);
   }//buildCode

   // ---------------- Getters & Setters --------------

   public void setName(String name){ this.name = name; }

   public void setOwner(BranchExpediente owner) { this.owner = owner; }

   public void setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }
   
   public FileIndex getIndex() {return index;}
   public void setIndex(FileIndex index) {this.index = index;}

   public DocType getAttributes() {return attributes;}
   public void setAttributes(DocType attributes) {this.attributes = attributes;}

   public LocalDateTime getOpeningDate() {return openingDate;}
   public void setOpeningDate(LocalDateTime openingDate) {this.openingDate = openingDate;}

   public LocalDateTime getClosingDate() {return closingDate;}
   public void setClosingDate(LocalDateTime closingDate) {this.closingDate = closingDate;}

   public void setOpen(boolean open) {this.open = open;}

   // --------------------------- Implements HierarchicalEntity ---------------------------------------
   @Override public String      getName()   { return name;}
   
   @Override public BranchExpediente getOwner()  { return owner;}
   
   protected String getOwnerCode(){ return owner == null ? "" : owner.getOwnerCode()+ ":"+ name; }

   // -----------------  Implements NeedsProtection ----------------
   
   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}
   
   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}
   
   @Override public boolean         isOwnedBy( SingleUser user)           { return objectToProtect.isOwnedBy(user);}
   
   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}
   
   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}
   
   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}
   
   @Override public void            grant( Permission permission)         { objectToProtect.grant(permission);}
   
   @Override public void            revoke( Permission permission)        { objectToProtect.revoke(permission);}

   // ---------------------- Object -----------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Expediente ))
         return false;

      Expediente that = (Expediente) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 83237: id.hashCode();}


   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" name["+ name+ "]")
       .append(" owner["+ owner.getCode()+ "]")
       .append(" n entries["+ index.size()+ "]")
       .append(" docType["+ attributes.getCode()+ "]")
       .append(" dateOpen["+ openingDate.format(FormattingUtils.FULL_DATE_FORMATTER)+ "]")
       .append(" dateCloses["+ closingDate.format(FormattingUtils.FULL_DATE_FORMATTER)+ "]")
       .append(" isOpen["+ isOpen()+ "]");

      return s.toString();
   }//toString

   @Override  public int compareTo(Expediente that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.getCode().compareTo(that.getCode());

   }// compareTo

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

}//Expediente