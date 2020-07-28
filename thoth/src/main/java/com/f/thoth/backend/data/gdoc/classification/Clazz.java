package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.ui.utils.FormattingUtils;

/**
 * Representa una clase del esquema de clasificaci�n
 */
public class Clazz extends BaseEntity implements NeedsProtection, Comparable<Clazz>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   protected String     name;

   @NotNull(message = "{evidentia.schema.required}")
   protected Schema     schema;

   @NotNull (message = "{evidentia.category.required}")
   protected Integer    category;

   protected Role       roleOwner;

   protected Clazz      parent;

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDateTime dateOpened;

   protected LocalDateTime dateClosed;

   @NotNull(message = "{remun.status.required}")
   protected RetentionSchedule retentionSchedule;

   // ------------- Constructors ------------------
   public Clazz()
   {
      super();
   }

   public Clazz( String name, Schema schema, Integer category, Role roleOwner, Clazz parent,
                 LocalDateTime dateOpened, LocalDateTime dateClosed, RetentionSchedule retentionSchedule)
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
      this.parent            = parent;
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

   @Override protected void buildCode() { this.code =  parent == null? tenant.toString()+ ":"+ name : parent.code + "-"+ name; }

   // -------------- Getters & Setters ----------------

   public String     getName(){ return name;}
   public void       setName( String name)
   {
      this.name = name;
      buildCode();
   }

   public Schema     getSchema(){ return this.schema;}
   public void       setSchema( Schema schema){ this.schema = schema;}

   public Integer    getCategory() { return category;}
   public void       setCategory( Integer category){ this.category = category;}

   public Role       getRoleOwner() { return roleOwner;}
   public void       setRoleOwner( Role roleOwner) { this.roleOwner = roleOwner;}

   public Clazz      getParent() { return parent;}
   public void       setParent(Clazz parent) { this.parent = parent;}

   public LocalDateTime getDateOpened() { return dateOpened;}
   public void       setDateOpened( LocalDateTime dateOpened) { this.dateOpened = dateOpened;}

   public LocalDateTime getDateClosed() { return dateClosed;}
   public void       setDateClosed( LocalDateTime dateClosed){ this.dateClosed = dateClosed;}

   public RetentionSchedule getRetentionSchedule() { return retentionSchedule;}
   public void              setRetentionSchedule( RetentionSchedule retentionSchedule) {this.retentionSchedule = retentionSchedule;}

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
              ((this.parent == null   && that.parent == null) || (this.parent != null && this.parent.equals(that.parent)));


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
        append( " parent["+ parent == null? "-NO-" : parent.getCode()+ "]").
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
      LocalDateTime now = LocalDateTime.now();
      return now.compareTo(dateOpened) >= 0 && now.compareTo(dateClosed) <= 0;
   }//isOpen

   @Override public String getKey() { return code;}

   @Override public boolean canBeAccessedBy (Integer userCategory) { return this.category != null && userCategory != null && this.category.compareTo( userCategory) <= 0; }

   @Override public boolean isOwnedBy( SingleUser user) { return false;}

   @Override public boolean isOwnedBy( Role role){ return roleOwner != null && roleOwner.equals(role); }

}//Clazz