package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa una clase del esquema de clasificacion
 */
@MappedSuperclass
public class Clazz extends BaseEntity implements NeedsProtection, HierarchicalEntity<BranchClass>, Comparable<Clazz>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   protected String     name;

   @NotNull(message = "{evidentia.objectToProtect.required") 
   @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;

   @NotNull(message = "{evidentia.schema.required}")
   @ManyToOne
   protected Schema     schema;

   @NotNull (message = "{evidentia.category.required}")
   protected Integer    category;

   @ManyToOne
   protected Role       roleOwner;

   @ManyToOne
   protected BranchClass  owner;

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDate  dateOpened;

   protected LocalDate  dateClosed;

   @NotNull(message = "{remun.status.required}")
   @ManyToOne
   protected RetentionSchedule retentionSchedule;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn(name="class_id")
   @BatchSize(size = 20)
   protected Set<Permission>  acl;   // Access control list

   // ------------- Constructors ------------------
   public Clazz()
   {
      super();
      buildCode();
   }

   public Clazz( String name, Schema schema, Integer category, Role roleOwner, BranchClass owner,
                 LocalDate dateOpened, LocalDate dateClosed, RetentionSchedule retentionSchedule)
   {
      super();

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es inv�lido");

      if ( schema == null)
         throw new IllegalArgumentException("Esquema de metadatos no puede ser nulo");

      if ( category < 0 || category > 5)
         throw new IllegalArgumentException("Categor�a de seguridad inv�lida");

      if (dateOpened ==  null || dateClosed == null || dateOpened.isAfter(dateClosed))
         throw new IllegalArgumentException("Fechas de apertura["+ dateOpened+ "] y de cierre["+ dateClosed+ "] inconsistentes");

      this.name              = TextUtil.nameTidy(name);
      this.schema            = schema;
      this.category          = category;
      this.roleOwner         = roleOwner;
      this.owner             = owner;
      this.dateOpened        = dateOpened;
      this.dateClosed        = dateClosed;
      this.retentionSchedule = retentionSchedule;
      buildCode();
   }//Clazz

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Anonima";
      buildCode();
   }


   @Override protected void buildCode()
   {
      this.code =   owner != null? owner.code + ">"+ name :
                   (tenant == null? "[Tenant]" : tenant.getCode())+  "[CLS]>"+ (name == null? "[name]" : name);
   }//buildCode

   // -------------- Getters & Setters ----------------

   public void       setName( String name){ this.name = name;}

   public void setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }

   public Schema     getSchema(){ return this.schema;}
   public void       setSchema( Schema schema){ this.schema = schema;}

   public Integer    getCategory() { return category;}
   public void       setCategory( Integer category){ this.category = category;}

   public Role       getRoleOwner() { return roleOwner;}
   public void       setRoleOwner( Role roleOwner) { this.roleOwner = roleOwner;}

   public void         setOwner(BranchClass owner) { this.owner = owner;}

   public LocalDate  getDateOpened() { return dateOpened;}
   public void       setDateOpened( LocalDate dateOpened) { this.dateOpened = dateOpened;}

   public LocalDate  getDateClosed() { return dateClosed;}
   public void       setDateClosed( LocalDate dateClosed){ this.dateClosed = dateClosed;}

   public RetentionSchedule getRetentionSchedule() { return retentionSchedule;}
   public void              setRetentionSchedule( RetentionSchedule retentionSchedule) {this.retentionSchedule = retentionSchedule;}

   public Set<Permission>   getAcl() {return acl;}
   public void              setAcl(Set<Permission> acl) {this.acl = acl;}
   
   // ------------------- implements HierarchicalEntity<BranchOffice> -----------
   
   @Override public ObjectToProtect getObjectToProtect(){ return objectToProtect;}
   
   @Override public String          getName(){ return name;}
   
   @Override public BranchClass     getOwner() { return owner;}
   

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      if (!super.equals(o))
         return false;

      Clazz that = (Clazz) o;

      return  this.tenant.equals(that.tenant) && this.code.equals(that.code) &&
              ((this.owner == null   && that.owner == null) || (this.owner != null && this.owner.equals(that.owner)));


   }// equals

   @Override
   public int hashCode()
   {
      return Objects.hash( tenant, code);
   }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( super.toString()).append("\n\t\t").
        append( " name["+ name+ "]").
        append( " category["+ category+ "]").
        append( " schema["+ schema.getCode()+ "]").
        append( " roleOwner["+ (roleOwner == null? "-NO-" : roleOwner.getCode())+ "]\n\t\t").append("\n\t\t").
        append( " owner["+ owner == null? "---" : owner.getCode()+ "]").
        append( " dateOpened["+ dateOpened.format( FormattingUtils.FULL_DATE_FORMATTER)+ "]").
        append( " dateClosed["+ dateClosed.format( FormattingUtils.FULL_DATE_FORMATTER)+ "]").
        append( " retentionSchedule["+ retentionSchedule == null? "-NO-" :  retentionSchedule.getCode()+ "]");

      return s.toString();
   }//toString

   @Override
   public int compareTo(Clazz other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }

   // --------------- Logic ------------------------------

   public boolean isOpen()
   {
      LocalDate now = LocalDate.now();
      return now.compareTo(dateOpened) >= 0 && now.compareTo(dateClosed) <= 0;
   }//isOpen

   @Override public boolean canBeAccessedBy (Integer userCategory) { return this.category != null && userCategory != null && this.category.compareTo( userCategory) <= 0; }

   @Override public boolean isOwnedBy( SingleUser user)            { return false;}

   @Override public boolean isOwnedBy( Role role)                  { return roleOwner != null && roleOwner.equals(role); }
   
   @Override public boolean isRestrictedTo( UserGroup userGroup)   { return objectToProtect.isRestrictedTo(userGroup);}
   
   @Override public boolean admits( Role role)
   { 
      for( Permission p: acl)
      {
         if ( p.grants( role, objectToProtect) )
            return true;
      }
      return false; 
      
   }//admits

   @Override public void grant( Permission permission) { acl.add(permission);}

   @Override public void revoke( Permission permission) { acl.remove(permission);}

}//Clazz