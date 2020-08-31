package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;


/**
 * Representa un objeto que requiere protección
 */

@NamedEntityGraphs({
   @NamedEntityGraph(
         name = ClassificationClass.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode(value="name",      subgraph="brief"),
            @NamedAttributeNode(value="category",  subgraph="brief"),
            @NamedAttributeNode(value="userOwner", subgraph="brief"),
            @NamedAttributeNode(value="roleOwner", subgraph="brief")
         },
         subgraphs = @NamedSubgraph(name = ObjectToProtect.BRIEF, 
               attributeNodes = {
                 @NamedAttributeNode("name"),
                 @NamedAttributeNode("category"),
                 @NamedAttributeNode("userOwner"),
                 @NamedAttributeNode("roleOwner")
               })
         ),
   @NamedEntityGraph(
         name = ClassificationClass.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("owner"),
               @NamedAttributeNode(value="name",      subgraph="full"),
               @NamedAttributeNode(value="category",  subgraph="full"),
               @NamedAttributeNode(value="userOwner", subgraph="full"),
               @NamedAttributeNode(value="roleOwner", subgraph="full"),
               @NamedAttributeNode(value="acl",       subgraph="full")
            },
            subgraphs = @NamedSubgraph(name = ObjectToProtect.FULL, 
                  attributeNodes = {
                    @NamedAttributeNode("name"),
                    @NamedAttributeNode("category"),
                    @NamedAttributeNode("userOwner"),
                    @NamedAttributeNode("roleOwner"),
                    @NamedAttributeNode("acl")
                  })
            )
         })

@Entity
@Table(name = "CLASSIFICATION_CLASS", indexes = { @Index(columnList = "code") })
public class ClassificationClass extends BaseEntity implements  NeedsProtection, HierarchicalEntity<ClassificationClass>, Comparable<ClassificationClass>
{
   public static final String BRIEF = "ClassificationClass.brief";
   public static final String FULL  = "ClassificationClass.full";

   @NotNull(message = "{evidentia.objectToProtect.required") 
   @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
   ObjectToProtect  objectToProtect;
   
   @NotNull(message = "{evidentia.level.required") 
   protected Integer    level;
   
   @NotNull(message = "{evidentia.schema.required}")
   protected Schema     schema;

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDate  dateOpened;

   protected LocalDate  dateClosed;

   @NotNull(message = "{remun.status.required}")
   protected RetentionSchedule retentionSchedule;

   @ManyToOne
   protected ClassificationClass owner;      // Object group to which this object belongs

   // ------------- Constructors ------------------
   public ClassificationClass()
   {
      super();
      init();
      objectToProtect = new ObjectToProtect("",null);
      buildCode();
   }

   public ClassificationClass( Integer level, String name, ClassificationClass owner)
   {
      init();
      objectToProtect = new ObjectToProtect(name, null);
      this.level      = level;
      this.owner      = owner;
      buildCode();
   }//Clazz
   
   private void init()
   {
      LocalDate now          = LocalDate.now();
      this.level             = 0;
      this.schema            = null;
      this.dateOpened        = now;
      this.dateClosed        = LocalDate.MAX;
      this.retentionSchedule = null;
      this.owner             = null;
      
   }//init

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      objectToProtect.prepareData();
      buildCode();
   }

   @Override protected void buildCode()
   {
      ObjectToProtect owner = objectToProtect.getOwner();
      String          name  = objectToProtect.getName();
      this.code =   owner != null? owner.getCode() + "-"+ name :
                   (tenant == null? "[Tenant]" : tenant.getCode())+ "[CLS]>"+ (name == null? "[name]" : name);
   }//buildCode
   
   public String isValid()
   {
      StringBuilder msg = new StringBuilder();

      if ( objectToProtect ==  null)
         msg.append("Objeto a proteger asociado a la clase no puede ser nulo");
      
      msg.append( objectToProtect.isValid());

      if (dateOpened ==  null || dateClosed == null || dateOpened.isAfter(dateClosed))
         msg.append("Fechas de apertura["+ dateOpened+ "] y de cierre["+ dateClosed+ "] inconsistentes");
      
      return msg.toString();

   }//isValid

   // -------------- Getters & Setters ----------------

   public ObjectToProtect getObjectToProtect(){ return objectToProtect;}
   public void setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }

   public void setOwner(ClassificationClass owner){ this.owner = owner; }

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
   
   // --------------- implements HierarchicalEntity<ClassificationClass>
   @Override public Long                getId()   { return super.getId();}
   @Override public String              getName() { return objectToProtect.getName();}
   @Override public ClassificationClass getOwner(){ return owner;}
   @Override public String              getKey()  { return "[CLS]"+id; }
   @Override public String              getCode() 
   {
      return (tenant == null? "[tenant]": tenant.getCode())+"[CLS]"+ objectToProtect.getOwnerCode()+ ">"+  getName();
   }//getCode


   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ObjectToProtect ))
         return false;

      ClassificationClass that = (ClassificationClass) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 4027: id.hashCode();}

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "ClassificationClass{").
        append( " level["+ level+ "]").
        append( " schema["+ schema.getCode()+ "]").
        append( " dateOpened["+ TextUtil.formatDate(dateOpened)+ "]").
        append( " dateClosed["+ TextUtil.formatDate(dateClosed)+ "]").
        append( " retentionSchedule["+ retentionSchedule == null? "---" :  retentionSchedule.getCode()+ "]").
        append( super.toString()).
        append("\n     }\n");

      return s.toString();
   }//toString
   

   @Override
   public int compareTo(ClassificationClass that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.objectToProtect.compareTo(that.objectToProtect); 
   }// compareTo


   // --------------- Implements NeedsProtection ------------------------------   
   public static Long getId( String key)
   {
      if( key == null)
         return Long.MIN_VALUE;
      
      Long id = Long.valueOf(key.substring(key.indexOf(']')+1));
      return id;
   }//getId
   

   @Override public boolean canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}
   @Override public boolean isOwnedBy( SingleUser user)           { return objectToProtect.isOwnedBy(user);}
   @Override public boolean isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}   
   @Override public boolean admits( Role role)                    { return objectToProtect.admits(role);}   
   @Override public void    grant( Permission permission)         { objectToProtect.grant(permission);}  
   @Override public void    revoke( Permission permission)        { objectToProtect.revoke(permission);}



   // --------------- Logic ------------------------------

   public boolean isOpen()
   {
      LocalDate now = LocalDate.now();
      return now.compareTo(dateOpened) >= 0 && now.compareTo(dateClosed) <= 0;
   }//isOpen

}//ClassificationClass
