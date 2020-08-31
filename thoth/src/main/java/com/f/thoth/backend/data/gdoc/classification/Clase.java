package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ObjectToProtect;

/**
 * Representa un objeto que requiere protección
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = Clase.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("category"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("userOwner"),
            @NamedAttributeNode("roleOwner")
         }),
   @NamedEntityGraph(
         name = Clase.FULL,
         attributeNodes = {
            @NamedAttributeNode("id"),
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("category"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("userOwner"),
            @NamedAttributeNode("roleOwner"),
            @NamedAttributeNode("acl")
         }) })

@Entity
@Table(name = "OBJECT_TO_PROTECT", indexes = { @Index(columnList = "code") })

public class Clase extends ObjectToProtect
{
   public static final String BRIEF = "Clase.brief";
   public static final String FULL  = "Clase.full";

   @NotNull(message = "{evidentia.level.required") 
   protected Integer    level;
   
   @NotNull(message = "{evidentia.schema.required}")
   protected Schema     schema;

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDate  dateOpened;

   protected LocalDate  dateClosed;

   @NotNull(message = "{remun.status.required}")
   protected RetentionSchedule retentionSchedule;

   // ------------- Constructors ------------------
   public Clase()
   {
      super();
      init();
      buildCode();
   }

   public Clase( String name, Clase owner)
   {
      super(name, owner);

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");

      if ( schema == null)
         throw new IllegalArgumentException("Esquema de metadatos no puede ser nulo");

      if ( category < 0 || category > 5)
         throw new IllegalArgumentException("Categoría de seguridad inválida");

      if (dateOpened ==  null || dateClosed == null || dateOpened.isAfter(dateClosed))
         throw new IllegalArgumentException("Fechas de apertura["+ dateOpened+ "] y de cierre["+ dateClosed+ "] inconsistentes");

      init();
      buildCode();
   }//Clazz
   
   private void init()
   {
      LocalDate now = LocalDate.now();
      this.schema            = null;
      this.dateOpened        = now;
      this.dateClosed        = LocalDate.MAX;
      this.retentionSchedule = null;
      
   }

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Anonima";
      buildCode();
   }

   @Override protected void buildCode()
   {
      this.code =   owner != null? owner.getCode() + "-"+ name :
                   (tenant == null? "[Tenant]" : tenant.getCode())+ "[CLS]>"+ (name == null? "[name]" : name);
   }//buildCode

   // -------------- Getters & Setters ----------------

   public Integer    getLevel(){ return level;}
   public void       setLevel(Integer level){ this.level = level;}

   public Schema     getSchema(){ return this.schema;}
   public void       setSchema( Schema schema){ this.schema = schema;}

   public LocalDate  getDateOpened() { return dateOpened;}
   public void       setDateOpened( LocalDate dateOpened) { this.dateOpened = dateOpened;}

   public LocalDate  getDateClosed() { return dateClosed;}
   public void       setDateClosed( LocalDate dateClosed){ this.dateClosed = dateClosed;}

   public RetentionSchedule getRetentionSchedule() { return retentionSchedule;}
   public void              setRetentionSchedule( RetentionSchedule retentionSchedule) {this.retentionSchedule = retentionSchedule;}

   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ObjectToProtect ))
         return false;

      Clase that = (Clase) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 4027: id.hashCode();}

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "Clase{").
        append( " level["+ level+ "]").
        append( " schema["+ schema.getCode()+ "]").
        append( " dateOpened["+ TextUtil.formatDate(dateOpened)+ "]").
        append( " dateClosed["+ TextUtil.formatDate(dateClosed)+ "]").
        append( " retentionSchedule["+ retentionSchedule == null? "---" :  retentionSchedule.getCode()+ "]").
        append( super.toString()).
        append("\n     }\n");

      return s.toString();
   }//toString

   // --------------- Logic ------------------------------

   public boolean isOpen()
   {
      LocalDate now = LocalDate.now();
      return now.compareTo(dateOpened) >= 0 && now.compareTo(dateClosed) <= 0;
   }//isOpen

}//Clase
