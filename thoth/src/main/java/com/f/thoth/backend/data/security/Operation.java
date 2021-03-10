package com.f.thoth.backend.data.security;

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
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa una operacion que puede ser ejecutada
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = Operation.BRIEF,
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
         name = Operation.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("owner"),
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
@Table(name = "OPERATION", indexes = { @Index(columnList = "code") })
public class Operation extends BaseEntity implements NeedsProtection, HierarchicalEntity<Operation>, Comparable<Operation>
{
   public static final String BRIEF = "Operation.brief";
   public static final String FULL  = "Operation.full";

   @NotNull  (message = "{evidentia.name.required}")
   @NotBlank (message = "{evidentia.name.required}")
   @NotEmpty (message = "{evidentia.name.required}")
   @Size(max = 255)
   @Column(unique = true)
   protected String          name;       // Operation name

   @ManyToOne
   protected Operation       owner;      // Operation group to which this operation belongs

   @NotNull(message = "{evidentia.objectToProtect.required}")
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;  // Associated security object

   // --------------------- Construccion -------------------------
   public Operation()
   {
      super();
      objectToProtect = new ObjectToProtect();
      name = "[name]";
      init();
      buildCode();
   }//Operation constructor

   public Operation( String name, ObjectToProtect objectToProtect, Operation owner)
   {
      super();

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es invalido");

      if ( objectToProtect == null )
         throw new IllegalArgumentException( "Objeto a proteger asociado a la operacion no puede ser nulo");

      init();
      this.name             = TextUtil.nameTidy(name);
      this.owner            = owner;
      this.objectToProtect = objectToProtect;
      buildCode();
   }//Operation constructor

   private void init()
   {
      owner     = null;
   }//init


   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name     =  TextUtil.nameTidy(name).toLowerCase();
      buildCode();
   }//prepareData

   @Override protected void buildCode()
   {
      this.code = (tenant == null? "[tenant]": tenant.getCode())+"[EXE]"+
                  (owner == null? "": owner.getOwnerCode())+
                   ">"+ (name == null? "[name]" : name);
   }//buildCode

   public String isValid()
   {
      StringBuilder msg = new StringBuilder();
      if ( !TextUtil.isValidName(name))
         msg.append("Nombre de la Operation["+ name+ "] es inválido\n");

      return msg.toString();
   }//isValid

   // ------------------------ Getters && Setters ---------------------------

   public void                  setName(String name)      { this.name  = (name != null ? name.trim() : "Anonima");}
   public void                  setOwner(Operation owner) { this.owner = owner;}
   public void                  setObjectToProtect( ObjectToProtect objectToProtect) {this.objectToProtect = objectToProtect;}

   public Integer               getCategory() {return objectToProtect.getCategory();}
   public void                  setCategory(Integer category) {objectToProtect.setCategory(category);}

   public User            getUserOwner() {return objectToProtect.getUserOwner();}
   public void                  setUserOwner(User userOwner) {objectToProtect.setUserOwner(userOwner);}

   public Role                  getRoleOwner() {return objectToProtect.getRoleOwner();}
   public void                  setRoleOwner(Role roleOwner) {objectToProtect.setRoleOwner(roleOwner);}

   public UserGroup             getRestrictedTo() {return objectToProtect.getRestrictedTo();}
   public void                  setRestrictedTo(UserGroup restrictedTo) {objectToProtect.setRestrictedTo(restrictedTo);}

   // --------------------------- Implements HierarchicalEntity ---------------------------------------
   @Override public String      getName()   { return name;}

   @Override public Operation   getOwner()  { return owner;}
   
   @Override public String      formatCode() { return getCode();}

   private String getOwnerCode(){ return (owner == null ? "" : owner.getOwnerCode())+ ":"+ name; }

   // -----------------  Implements NeedsProtection ----------------

   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( User user)           { return objectToProtect.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

   @Override public void            grant( Permission  permission)        { objectToProtect.grant(permission);}

   @Override public void            revoke(Permission permission)         { objectToProtect.revoke(permission);}

   // ---------------------- Object -----------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Operation ))
         return false;

      Operation that = (Operation) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 7: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" Operacion{"+ super.toString())
       .append(" name["+ name+ "]")
       .append(" owner["+ (owner     == null? "---": owner.getCode()))
       .append(" \n   [").append(objectToProtect.toString()).append("   ]}\n");

      return s.toString();
   }//toString

   @Override  public int compareTo(Operation that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.getCode().compareTo(that.getCode());

   }// compareTo

}//Operation
