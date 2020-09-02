package com.f.thoth.backend.data.gdoc.classification;

import java.time.LocalDate;
import java.util.Set;

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
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.UserGroup;


/**
 * Representa un nodo del esquema de clasificación documental
 */

/*
 * @NamedEntityGraph(name = "graph.module.projects.contractors",
        attributeNodes = @NamedAttributeNode(value = "projects", subgraph = "projects.contractors"),
        subgraphs = @NamedSubgraph(name = "projects.contractors",
                attributeNodes = @NamedAttributeNode(value = "contractors")))

 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = ClassificationClass.BRIEF,
         attributeNodes = {
            @NamedAttributeNode("tenant"),
            @NamedAttributeNode("code"),
            @NamedAttributeNode("name"),
            @NamedAttributeNode("owner"),
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
         name = ClassificationClass.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("owner"),
            //   @NamedAttributeNode("retentionSchedule"),
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
@Table(name = "CLASSIFICATION_CLASS", indexes = { @Index(columnList = "code") })
public class ClassificationClass extends BaseEntity implements  NeedsProtection, HierarchicalEntity<ClassificationClass>, Comparable<ClassificationClass>
{
   public static final String BRIEF = "ClassificationClass.brief";
   public static final String FULL  = "ClassificationClass.full";

   @NotNull  (message = "{evidentia.name.required}")
   @NotBlank (message = "{evidentia.name.required}")
   @NotEmpty (message = "{evidentia.name.required}")
   @Size(max = 255)
   @Column(unique = true)
   protected String          name;                         // Node name

   @NotNull(message = "{evidentia.objectToProtect.required") 
   @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;             // Associated security object

   @NotNull(message = "{evidentia.level.required") 
   @ManyToOne
   protected ClassificationLevel    level;                 // Classification level

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDate  dateOpened;                        // Date level was opened

   protected LocalDate  dateClosed;                        // Date level was closed

   /*
   @NotNull(message = "{evidentia.retention.required}")
   @ManyToOne
   protected RetentionSchedule retentionSchedule;
   */
   
   @ManyToOne
   protected ClassificationClass owner;                    //  Classification node to which this ClassificationClass belongs

   // ------------- Constructors ------------------
   public ClassificationClass()
   {
      super();
      init();
      objectToProtect = new ObjectToProtect();
      buildCode();
   }

   public ClassificationClass( ClassificationLevel level, String name, ClassificationClass owner, ObjectToProtect objectToProtect)
   {

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es invalido");

      if ( level == null)
         throw new IllegalArgumentException("Nivel de la clase del esquema de clasificación no puede ser nulo");
     
      if ( TextUtil.isEmpty(name))
         throw new IllegalArgumentException("Nombre de la clase del esquema de clasificación no puede ser nulo");
           
      if ( objectToProtect == null)
         throw new IllegalArgumentException("Objeto de seguridad de la clase del esquema de clasificación no puede ser nulo");
      
      init(); 
      this.level            = level;
      this.name             = TextUtil.nameTidy(name);
      this.owner            = owner;
      this.objectToProtect  = objectToProtect;
      buildCode();
   }//Clazz
   
   private void init()
   {
      LocalDate now          = LocalDate.now();
      this.dateOpened        = now;
      this.dateClosed        = LocalDate.MAX;
      this.owner             = null;
      // this.retentionSchedule = null;
      
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
      this.code = (tenant == null? "[Tenant]" : tenant.getCode())+ getOwnerCode()+ "[CLS]>"+ (name == null? "[name]" : name);
   }//buildCode

   // -------------- Getters & Setters ----------------
   public void                      setName(String name)      { this.name  = (name != null ? name.trim() : "Anonima");}
   public void                      setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }
   public void                      setOwner(ClassificationClass owner){ this.owner = owner; }

   public ClassificationLevel       getLevel(){ return level;}
   public void                      setLevel(ClassificationLevel level){ this.level = level;}

   public LocalDate                 getDateOpened() { return dateOpened;}
   public void                      setDateOpened( LocalDate dateOpened) { this.dateOpened = dateOpened;}

   public LocalDate                 getDateClosed() { return dateClosed;}
   public void                      setDateClosed( LocalDate dateClosed){ this.dateClosed = dateClosed;}

   /*
   public RetentionSchedule getRetentionSchedule() { return retentionSchedule;}
   public void              setRetentionSchedule( RetentionSchedule retentionSchedule) {this.retentionSchedule = retentionSchedule;}
   */

   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ClassificationClass ))
         return false;

      ClassificationClass that = (ClassificationClass) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 4027: id.hashCode();}

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "ClassificationClass{")
       .append(  super.toString())
       .append(  "name["+ name+ "]")
       .append( " ["+ level.toString()+ "]")
       .append( " dateOpened["+ TextUtil.formatDate(dateOpened)+ "]")
       .append( " dateClosed["+ TextUtil.formatDate(dateClosed)+ "]\n")
       // .append( " retentionSchedule["+ retentionSchedule == null? "---" :  retentionSchedule.getCode()+ "]\n")
       .append( " objectToProtect["+ objectToProtect.toString()+ "]")
       .append("\n     }\n");

      return s.toString();
   }//toString
   

   @Override  public int compareTo(ClassificationClass that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.getCode().compareTo(that.getCode());

   }// compareTo


   // --------------- Implements NeedsProtection ------------------------------   

   public Integer               getCategory() {return objectToProtect.getCategory();}
   public void                  setCategory(Integer category) {objectToProtect.setCategory(category);}

   public SingleUser            getUserOwner() {return objectToProtect.getUserOwner();}
   public void                  setUserOwner(SingleUser userOwner) {objectToProtect.setUserOwner(userOwner);}

   public Role                  getRoleOwner() {return objectToProtect.getRoleOwner();}
   public void                  setRoleOwner(Role roleOwner) {objectToProtect.setRoleOwner(roleOwner);}

   public UserGroup             getRestrictedTo() {return objectToProtect.getRestrictedTo();}
   public void                  setRestrictedTo(UserGroup restrictedTo) {objectToProtect.setRestrictedTo(restrictedTo);}

   public Set<Permission>       getAcl() {return objectToProtect.getAcl();}
   public void                  setAcl( Set<Permission> acl) {objectToProtect.setAcl(acl);}

   // --------------------------- Implements HierarchicalEntity ---------------------------------------
   @Override public String                getName()   { return name;}
   
   @Override public ClassificationClass   getOwner()  { return owner;}
   
   private String getOwnerCode(){ return owner == null ? "" : owner.getOwnerCode()+ ":"+ name; }

   // -----------------  Implements NeedsProtection ----------------
   
   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}
   
   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}
   
   @Override public boolean         isOwnedBy( SingleUser user)           { return objectToProtect.isOwnedBy(user);}
   
   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}
   
   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}
   
   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}
   
   @Override public void            grant( Permission permission)         { objectToProtect.grant(permission);}
   
   @Override public void            revoke( Permission permission)        { objectToProtect.revoke(permission);}


   // --------------- Logic ------------------------------

   public boolean isOpen()
   {
      LocalDate now = LocalDate.now();
      return now.compareTo(dateOpened) >= 0 && now.compareTo(dateClosed) <= 0;
   }//isOpen

}//ClassificationClass
