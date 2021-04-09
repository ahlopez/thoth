package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;


/**
 * Representa una clase del esquema de clasificaciOn documental
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = Classification.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("level"),
            @NamedAttributeNode("dateOpened"),
            @NamedAttributeNode("dateClosed"),
            @NamedAttributeNode("classCode"),
            @NamedAttributeNode("path"),
            @NamedAttributeNode("currentExpedienteNumber"),
            @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.BRIEF)
         },
         subgraphs = @NamedSubgraph(name = ObjectToProtect.BRIEF,
               attributeNodes = {
                 @NamedAttributeNode("category"),
                 @NamedAttributeNode("userOwner"),
                 @NamedAttributeNode("roleOwner"),
                 @NamedAttributeNode("restrictedTo")
               })
         ),
   @NamedEntityGraph(
         name = Classification.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("owner"),
               @NamedAttributeNode("level"),
               @NamedAttributeNode("dateOpened"),
               @NamedAttributeNode("dateClosed"),
               @NamedAttributeNode("classCode"),
               @NamedAttributeNode("path"),
               @NamedAttributeNode("currentExpedienteNumber"),
               @NamedAttributeNode("metadata"),
               @NamedAttributeNode("retentionSchedule"),
               @NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.FULL)
            },
            subgraphs = @NamedSubgraph(name = ObjectToProtect.FULL,
                  attributeNodes = {
                    @NamedAttributeNode("category"),
                    @NamedAttributeNode("userOwner"),
                    @NamedAttributeNode("roleOwner"),
                    @NamedAttributeNode("restrictedTo"),
                    @NamedAttributeNode("acl")
                  })
            )
         })
@Entity
@Table(name = "CLASSIFICATION", indexes = { @Index(columnList = "code") })
public class Classification extends BaseEntity implements  NeedsProtection, HierarchicalEntity<Classification>, Comparable<Classification>
{
   public static final String BRIEF = "Classification.brief";
   public static final String FULL  = "Classification.full";

   @NotNull  (message = "{evidentia.name.required}")
   @NotBlank (message = "{evidentia.name.required}")
   @NotEmpty (message = "{evidentia.name.required}")
   @Size(max = 255)
   @Column(unique = true)
   protected String          name;                         // Classification class name

   @NotNull(message = "{evidentia.objectToProtect.required}")
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;             // Associated security object

   @NotNull(message = "{evidentia.level.required}")
   @ManyToOne
   protected Level    level;                               // Classification level in the classification tree

   @NotNull(message = "evidentia.metadata.required")
   @OneToOne(cascade= CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   protected SchemaValues metadata;                        // Metadata values of the associated classification.level

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDate  dateOpened;                        // Date classification class was opened

   @NotNull(message = "{evidentia.dateclosed.required}")
   protected LocalDate  dateClosed;                        // Date classification class was closed

   @NotNull(message = "{evidentia.retention.required}")
   @ManyToOne
   protected Retention retentionSchedule;                  // Retention Calendar associated to the class

   @ManyToOne
   protected Classification owner;                         //  Classification node to which this class belongs

   @NotNull(message = "{evidentia.classcode.required}")
   protected String    classCode;                          //  Unique business code of the classification node (includes level codes+ class code)

   @NotNull  (message = "{evidentia.path.required}")
   @NotBlank (message = "{evidentia.path.required}")
   @NotEmpty (message = "{evidentia.path.required}")
   protected String    path;                               //  Classification node path in document repository

   @PositiveOrZero
   protected Integer   currentExpedienteNumber;            //  Number of the last expediente created for this class

   // ------------- Constructors ------------------
   public Classification()
   {
      super();
      init();
      objectToProtect = new ObjectToProtect();
      buildCode();
   }

   public Classification( Level level, String name, String classCode, Classification owner, ObjectToProtect objectToProtect)
   {
      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es invalido");

      if ( level == null)
         throw new IllegalArgumentException("Nivel de la clase del esquema de clasificaciOn no puede ser nulo");

      if ( TextUtil.isEmpty(name))
         throw new IllegalArgumentException("Nombre de la clase del esquema de clasificaciOn no puede ser nulo");

      if ( objectToProtect == null)
         throw new IllegalArgumentException("Objeto de seguridad de la clase del esquema de clasificaciOn no puede ser nulo");

      init();
      this.level            = level;
      this.name             = TextUtil.nameTidy(name);
      this.classCode        = classCode;
      this.owner            = owner;
      this.objectToProtect  = objectToProtect;
      buildCode();
   }//Classification constructor

   private void init()
   {
      LocalDate now                = LocalDate.now();
      this.dateOpened              = now;
      this.dateClosed              = LocalDate.MAX;
      this.owner                   = null;
      this.retentionSchedule       = Retention.DEFAULT;
      this.metadata                = null;
      this.classCode               = null;
      this.path                    = "/";
      this.currentExpedienteNumber = 0;

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
      this.path = (tenant    == null? "/[tenant]": tenant.getWorkspace())+ "/"+ NodeType.CLASSIFICATION.getCode()+ "/"+
                      getOwnerPath(owner)+ (classCode == null? "[classCode]" : classCode);
      this.code = this.path;
   }//buildCode

   // -------------- Getters & Setters ----------------
   public void         setName(String name)      { this.name  = (name != null ? name.trim() : "Anonima");}
   public void         setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }
   public void         setOwner(Classification owner){ this.owner = owner; }

   public Level        getLevel(){ return level;}
   public void         setLevel(Level level){ this.level = level;}

   public LocalDate    getDateOpened() { return dateOpened;}
   public void         setDateOpened( LocalDate dateOpened) { this.dateOpened = dateOpened;}

   public LocalDate    getDateClosed() { return dateClosed;}
   public void         setDateClosed( LocalDate dateClosed){ this.dateClosed = dateClosed;}

   public Retention    getRetentionSchedule() { return retentionSchedule;}
   public void         setRetentionSchedule( Retention retentionSchedule) {this.retentionSchedule = retentionSchedule;}

   public SchemaValues getMetadata() { return metadata;}
   public void         setMetadata ( SchemaValues metadata) { this.metadata = metadata;}

   public String       getClassCode() { return classCode;}
   public void         setClassCode ( String classCode) { this.classCode = classCode;}

   public String       getPath() { return path;}
   public void         setPath ( String path) { this.path = path;}

   public Integer      getCurrentExpedienteNumber() { return currentExpedienteNumber;}
   public void         setCurrentExpedienteNumber ( Integer currentExpedienteNumber) { this.currentExpedienteNumber = currentExpedienteNumber;}


   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Classification ))
         return false;

      Classification that = (Classification) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 4027: id.hashCode();}

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "Classification{")
       .append( super.toString())
       .append( "name["+ name+ "]")
       .append( " ["+ level.toString()+ "]")
       .append( " classCode["+ classCode+ "]")
       .append( " path["+ path+ "]")
       .append( " currentExpedienteNumber["+ currentExpedienteNumber+ "]")
       .append( " dateOpened["+ TextUtil.formatDate(dateOpened)+ "]")
       .append( " dateClosed["+ TextUtil.formatDate(dateClosed)+ "]\n")
       .append( " retentionSchedule["+ retentionSchedule == null? "---" :  retentionSchedule.getCode()+ "]\n")
       .append( " objectToProtect["+ objectToProtect.toString()+ "]")
       .append("\n     }\n");

      return s.toString();
   }//toString


   @Override  public int compareTo(Classification that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.getCode().compareTo(that.getCode());

   }// compareTo


   // --------------------------- Implements HierarchicalEntity ---------------------------------------
   @Override public String           getName()   { return name;}

   @Override public Classification   getOwner()  { return owner;}

   @Override public String      formatCode()
   {
       int i = TextUtil.indexOf(code, "/", 3);
       String id = i >= 0? code.substring(i): "";
       id = TextUtil.replace(id, "/", "-");
       return id;
   }//formatCode

   private String getOwnerPath(Classification owner)
   {
           String path = "";
           while (owner != null)
           {
                   path = owner.classCode+ "/"+ path;
                   owner = owner.owner;
           }
           return  path;
   }//getOwnerPath


   // -----------------  Implements NeedsProtection ----------------

   public Integer                   getCategory() {return objectToProtect.getCategory();}
   public void                      setCategory(Integer category) {objectToProtect.setCategory(category);}

   public User                      getUserOwner() {return objectToProtect.getUserOwner();}
   public void                      setUserOwner(User userOwner) {objectToProtect.setUserOwner(userOwner);}

   public Role                      getRoleOwner() {return objectToProtect.getRoleOwner();}
   public void                      setRoleOwner(Role roleOwner) {objectToProtect.setRoleOwner(roleOwner);}

   public UserGroup                 getRestrictedTo() {return objectToProtect.getRestrictedTo();}
   public void                      setRestrictedTo(UserGroup restrictedTo) {objectToProtect.setRestrictedTo(restrictedTo);}

   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( User user)                 { return objectToProtect.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

   @Override public void            grant( Permission  permission)        { objectToProtect.grant(permission);}

   @Override public void            revoke(Permission permission)         { objectToProtect.revoke(permission);}


   // --------------- Logic ------------------------------

   public boolean isOpen()
   {
      LocalDate now = LocalDate.now();
      return now.compareTo(dateOpened) >= 0 && now.compareTo(dateClosed) <= 0;
   }//isOpen
   
   public boolean isRoot()
   {
	   return level.isRoot();
   }//isRoot
   
   
   public String getRootCode()
   {
       Classification parent  = this;	   	   	   
	   while (!parent.isRoot())
	   {   parent  = parent.owner;		   
	   }
	   return TextUtil.pad(parent.getClassCode(),3);

   }//getRootCode
   
   
   public String getTenantCode()
   {
	   return "["+ tenant.getId()+ "]";
   }//getTenantCode
   

   protected synchronized Integer nextExpedienteNumber()
   {
       currentExpedienteNumber ++;
       return currentExpedienteNumber;
   }//nextExpedienteNumber

   public String nextExpedienteCode()
   {
       return formatCode()+ "-"+ nextExpedienteNumber();
   }//nextExpedienteCode

}//Classification
