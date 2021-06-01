package com.f.thoth.backend.data.gdoc.metadata;

import javax.persistence.CascadeType;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

/**
 * Representa un tipo documental
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = DocumentType.BRIEF,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("owner"),
               @NamedAttributeNode("schema"),
               @NamedAttributeNode("requiresContent"),
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
         name = DocumentType.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("code"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("owner"),
               @NamedAttributeNode("schema"),
               @NamedAttributeNode("requiresContent"),
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
@Table(name = "DOCUMENT_TYPE", indexes = { @Index(columnList = "code") })
public class DocumentType extends BaseEntity implements NeedsProtection, HierarchicalEntity<DocumentType>, Comparable<DocumentType>
{
   public static final String BRIEF = "DocType.brief";
   public static final String FULL  = "DocType.full";

   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   protected String           name;

   @NotNull(message = "{evidentia.objectToProtect.required}")
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;

   @ManyToOne
   protected DocumentType     owner;

   @ManyToOne
   @NotNull (message = "{evidentia.schema.required}")
   protected Schema           schema;

   protected boolean          requiresContent;

   // Considerar definirlo como "Abstract"    boolean according to JCR pag 39 &3.7.1.3
   // Considerar definirlo como "Mixin"       boolean according to JCR pag 39 &3.7.1.4
   // Considerar definirlo como "Queryable"   boolean according to JCR pag 39 &3.7.1.5
   // Considerar definirlo como "Orderable"   boolean according to JCR pag 39 &3.7.1.6 (para expedientes)
   // Considerar definirlo como "Autocreated" boolean according to JCR pag 42 &3.7.2.3
   // Considerar definirlo como "Mandatory"   boolean according to JCR pag 42 &3.7.2.4

   // Properties can be defined as "Full-text searchable"  boolean according to JCR pag 44 &3.7.3.4
   // Properties can be defined as "Query-orderable"       boolean according to JCR pag 44 &3.7.3.5 Requires node be "Queryable"  &3.7.1.5
   // Properties have value constraints (array of Strings)


   // ------------- Constructors ------------------
   public DocumentType()
   {
      super();
      name = "[name]";
      init();
   }// DocType constructor

   public DocumentType( String name, Schema schema, DocumentType owner, boolean requiresContent)
   {
      super();
      if( TextUtil.isEmpty(name))
         throw new IllegalArgumentException( "Nombre["+ name+ "] del tipo documental es invalido");

      if(schema == null)
         throw new IllegalArgumentException( "El esquema de metadatos del tipo documental no puede ser nulo");

      if( owner != null && !owner.isPersisted())
         throw new IllegalArgumentException( "El tipo padre debe definirse antes que el tipo hijo");

      this.name     = name;
      this.owner    = owner;
      this.schema   = schema;
      this.requiresContent = requiresContent;
      this.objectToProtect = new ObjectToProtect();

   }//DocType

   private void init()
   {
      this.name            = "[nombre]";
      this.owner           = null;
      this.schema          = Schema.EMPTY;
      this.requiresContent = false;
      this.objectToProtect = new ObjectToProtect();
   }//init

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name     =  TextUtil.nameTidy(name).toLowerCase();
      buildCode();
   }


   @Override protected void buildCode()
   {
      if ( this.code == null)
      {
         this.code = (tenant == null? "[tenant]": tenant.getCode())+"[DTP]"+
               (owner == null? ":": owner.getOwnerCode())+ ">"+
               (name == null? "[name]" : name);
      }
   }//buildCode


   // -------------- Getters & Setters ----------------

   public void        setName( String name) { this.name = name;}

   public void        setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }

   public void        setOwner(DocumentType owner) { this.owner = owner;}

   public Schema      getSchema() { return schema;}
   public void        setSchema( Schema schema){ this.schema = schema;}

   public boolean     isRequiresContent() { return requiresContent;}
   public void        setRequiresContent( boolean requiresContent){ this.requiresContent = requiresContent;}

   // --------------------------- Implements HierarchicalEntity ---------------------------------------
   @Override public String      getName()   { return name;}

   @Override public DocumentType     getOwner()  { return owner;}

   @Override public String      formatCode()
   {
      int i = TextUtil.indexOf(code, "/", 3);
      String id = code.substring(i);
      id = TextUtil.replace(id, "/", "-");
      return id;
   }//formatCode

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

      if (!(o instanceof DocumentType ))
         return false;

      DocumentType that = (DocumentType) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 7: id.hashCode();}


   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "DocType{").
      append( super.toString()).append("\n\t\t").
      append( " name["+ name+ "]").
      append( " owner["+ owner == null? "---" : owner.getCode()+ "]").
      append( " requiresContent["+ requiresContent+ "]").
      append( " schema["+ schema.toString()+ "]").append("\n\t\t").
      append( " {"+ objectToProtect.toString()+ "}}\n");

      return s.toString();

   }//toString

   @Override  public int compareTo(DocumentType that)
   {
      return this.equals(that)?  0 :
         that == null?       1 :
            this.getCode().compareTo(that.getCode());

   }// compareTo


}//DocType