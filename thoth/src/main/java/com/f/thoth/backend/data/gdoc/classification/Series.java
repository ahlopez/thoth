package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa una serie documental
 */
@MappedSuperclass
public class Series extends BaseEntity implements NeedsProtection, HierarchicalEntity<BranchSeries>, Comparable<Series>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   protected String     name;

   @NotNull(message = "{evidentia.objectToProtect.required}")
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;

   @ManyToOne
   protected BranchSeries     owner;

   @NotNull(message = "{evidentia.schema.required}")
   @ManyToOne
   protected Schema     schema;

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDate  dateOpened;

   protected LocalDate  dateClosed;
/*
   @NotNull(message = "{remun.status.required}")
   @ManyToOne
   protected RetentionSchedule retentionSchedule;
*/

   // ------------- Constructors ------------------
   public Series()
   {
      super();
      buildCode();
   }


   public Series( String name, Schema schema, BranchSeries owner, LocalDate dateOpened, LocalDate dateClosed) //, RetentionSchedule retentionSchedule)
   {
      super();

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es invalido");

      if ( schema == null)
         throw new IllegalArgumentException("Esquema de metadatos no puede ser nulo");

      /*
      if ( retentionSchedule == null)
         throw new IllegalArgumentException("Reglas de retención no pueden ser nulas");
      */

      if (dateOpened ==  null || dateClosed == null || dateOpened.isAfter(dateClosed))
         throw new IllegalArgumentException("Fechas de apertura["+ dateOpened+ "] y de cierre["+ dateClosed+ "] inconsistentes");

      this.name              = TextUtil.nameTidy(name);
      this.schema            = schema;
      this.owner             = owner;
      this.dateOpened        = dateOpened;
      this.dateClosed        = dateClosed;
     // this.retentionSchedule = retentionSchedule;
      buildCode();
   }//Office

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Anonima";
      buildCode();
   }//prepareData



   @Override protected void buildCode()
   {
      this.code = (tenant == null? "[tenant]": tenant.getCode())+"[SER]"+
                  (owner == null? ":": owner.getOwnerCode())+ ">"+
                  (name == null? "[name]" : name);
   }//buildCode

   protected String getOwnerCode(){ return (owner == null ? "" : owner.getOwnerCode())+ ":"+ name; }

   // -------------- Getters & Setters ----------------

   public void              setName( String name) { this.name = name;}

   public void              setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }

   public void              setOwner(BranchSeries owner) { this.owner = owner;}

   public Schema            getSchema(){ return this.schema;}
   public void              setSchema( Schema schema){ this.schema = schema;}

   public LocalDate         getDateOpened() { return dateOpened;}
   public void              setDateOpened( LocalDate dateOpened) { this.dateOpened = dateOpened;}

   public LocalDate         getDateClosed() { return dateClosed;}
   public void              setDateClosed( LocalDate dateClosed){ this.dateClosed = dateClosed;}
/*
   public RetentionSchedule getRetentionSchedule() { return retentionSchedule;}
   public void              setRetentionSchedule( RetentionSchedule retentionSchedule){ this.retentionSchedule= retentionSchedule;}
*/

   // --------------------------- Implements HierarchicalEntity ---------------------------------------
   @Override public String       getName()   { return name;}

   @Override public BranchSeries getOwner()  { return owner;}

   @Override public String       formatCode() { return code;}

   // -----------------  Implements NeedsProtection ----------------

   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( User user)           { return objectToProtect.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

   @Override public void            grant( Permission  permission)        { objectToProtect.grant(permission);}

   @Override public void            revoke(Permission permission)         { objectToProtect.revoke(permission);}

   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Office ))
         return false;

      Series that = (Series) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 9341: id.hashCode();}

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( super.toString()).append("\n\t\t").
        append( " name["+ name+ "]").
        append( " owner["+ owner == null? "-NO-" : owner.getCode()+ "]").
        append( " schema["+ schema.getCode()+ "]").
        append( " dateOpened["+ dateOpened.format( FormattingUtils.FULL_DATE_FORMATTER)+ "]").
        append( " dateClosed["+ dateClosed.format( FormattingUtils.FULL_DATE_FORMATTER)+ "]\n").
        append( "{"+ objectToProtect.toString()+ "}\n");
      //  append( " retentionSchedule["+ retentionSchedule.getCode()+ "]");

      return s.toString();
   }//toString

   @Override
   public int compareTo(Series other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }


   // --------------- Logic ------------------------------

   public boolean isOpen()
   {
      LocalDate now = LocalDate.now();
      return now.compareTo(dateOpened) >= 0 && now.compareTo(dateClosed) <= 0;
   }//isOpen


}//Series