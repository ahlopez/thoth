package com.f.thoth.backend.data.gdoc.metadata;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;

/**
 * Representa un tipo documental
 */

@NamedEntityGraphs({
   @NamedEntityGraph(
         name = DocType.BRIEF,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("category"),
               @NamedAttributeNode("userOwner"),
               @NamedAttributeNode("roleOwner"),
               @NamedAttributeNode("parent"),
               @NamedAttributeNode("requiresContent")
         }),
   @NamedEntityGraph(
         name = DocType.FULL,
         attributeNodes = {
               @NamedAttributeNode("tenant"),
               @NamedAttributeNode("id"),
               @NamedAttributeNode("name"),
               @NamedAttributeNode("category"),
               @NamedAttributeNode("userOwner"),
               @NamedAttributeNode("roleOwner"),
               @NamedAttributeNode("parent"),
               @NamedAttributeNode("requiresContent"),
               @NamedAttributeNode("children"),
               @NamedAttributeNode("acl")

         }) })
@Entity
@Table(name = "DOC_TYPE", indexes = { @Index(columnList = "code") })
public class DocType extends BaseEntity implements NeedsProtection, Comparable<DocType>
{
   public static final String BRIEF = "DocType.brief";
   public static final String FULL  = "DocType.full";

   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   protected String      name;

   @NotNull(message = "{evidentia.objectToProtect.required") 
   @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect  objectToProtect;

   @ManyToOne
   @NotNull (message = "{evidentia.schema.required}")
   protected Schema      schema;

   @NotNull (message = "{evidentia.category.required}")
   protected Integer    category;

   @ManyToOne
   protected SingleUser userOwner;

   @ManyToOne
   protected Role       roleOwner;

   @ManyToOne
   protected DocType    parent;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 10)
   protected Set<DocType>  children;

   protected boolean   requiresContent;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn(name="doctype_id")
   @BatchSize(size = 20)
   protected Set<Permission>       acl;   // Access control list
   
   

   // ------------- Constructors ------------------
   public DocType()
   {
      children = new TreeSet<DocType>();
   }

   public DocType( String name, Schema schema, DocType parent, boolean requiresContent)
   {
      if( name == null || name.trim().length() == 0 || name.trim().equals(" "))
         throw new IllegalArgumentException( "Nombre["+ name+ "] del tipo documental es invalido");

      if(schema == null)
         throw new IllegalArgumentException( "El esquema de metadatos del tipo documental no puede ser nulo");

      if( !this.tenant.contains( parent))
         throw new IllegalArgumentException( "El tipo padre debe definirse antes que el tipo hijo");

      if( parent.name.equals(name) || inParents(parent,name))
         throw new IllegalArgumentException( "Tipo documental ya existe en alguno de sus padres");

      this.children = new TreeSet<DocType>();
      this.name     = name;
      this.schema   = schema;
      this.requiresContent = requiresContent;
      this.parent   = parent;
      this.parent.children.add(this);
      buildCode();

   }//DocType

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Generico";
      buildCode();
   }

   @Override protected void buildCode() { this.code =  parent == null? tenant.getCode()+ ":"+ name : parent.code + "-"+ name; }

   private boolean inParents(DocType parent, String name)
   {
      if (parent == null)
         return false;

      for (DocType child: parent.children)
      {
         if (child.name.equals(name))
            return true;
      }
      return inParents( parent.parent, name);

   }//inParents


   // -------------- Getters & Setters ----------------

   public String     getName(){ return name;}
   public void       setName( String name)
   {
      this.name = name;
      buildCode();
   }//setName

   @Override public ObjectToProtect getObjectToProtect(){ return objectToProtect;}
   public void setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }

   public Schema      getSchema() { return schema;}
   public void        setSchema( Schema schema){ this.schema = schema;}

   public Integer     getCategory() { return category;}
   public void        setCategory(Integer category){ this.category = category;}

   public SingleUser  getUuserOwner(){ return userOwner;}
   public void        setUserOwner( SingleUser userOwner){ this.userOwner = userOwner;}

   public Role        getRoleOwner() { return roleOwner;}
   public void        setRoleOwner( Role roleOwner) { this.roleOwner = roleOwner;}

   public boolean     isRequiresContent() { return requiresContent;}
   public void        setRequiresContent( boolean requiresContent){ this.requiresContent = requiresContent;}

   public DocType     getParent() { return parent;}
   public void        setParent(DocType parent) { this.parent = parent;}

   public Set<DocType> getChildren(){ return children;}
   public void         setChildren( Set<DocType> children){ this.children = children;}

   public Set<Permission>  getAcl() {return acl;}
   public void             setAcl(Set<Permission> acl) {this.acl = acl;}

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

      DocType that = (DocType) o;

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
      s.append( "DocType{").
      append( super.toString()).append("\n\t\t").
      append( " name["+ name+ "]").
      append( " requiresContent["+ requiresContent+ "]").
      append( " schema["+ schema.toString()+ "]").append("\n\t\t").
      append( " category["+ category+ "]").
      append( " userOwner["+ (userOwner == null? "-NO-" : userOwner.getCode())+ "]").
      append( " roleOwner["+ (roleOwner == null? "-NO-" : roleOwner.getCode())+ "]").append("\n\t\t").
      append( " parent["+ parent == null? "-NO-" : parent.getCode()+ "]").
      append( " children[");

      for(DocType child: children)
         s.append( child.name).append(" ");

      s.append( "]\n\t acl[");
      
      int i = 1;
      for( Permission p: acl)
      {
         s.append((i % 10 == 0? "\n\t   ": ", "))
          .append(p.getRole().getCode());
         i++;
      }
      
      s.append("\n\t    ]}\n");

      return s.toString();

   }//toString

   @Override
   public int compareTo(DocType other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }

   // --------------- Logic ------------------------------
   public boolean hasName( String name) { return this.name.equals( name);}


   @Override public String  getKey() { return this.code;}

   @Override public boolean canBeAccessedBy(Integer userCategory)
   {
      return this.category != null && userCategory != null && this.category <= userCategory;
   }

   @Override public boolean isOwnedBy( SingleUser user) { return userOwner != null && userOwner.equals(user);}

   @Override public boolean isOwnedBy( Role role) { return roleOwner != null && roleOwner.equals(role);}

   @Override public boolean admits( Role role)
   { 
      for( Permission p: acl)
      {
         if ( p.grants( role, this) )
            return true;
      }
      return false; 
   }

   @Override public void grant( Permission permission) { acl.add(permission);}

   @Override public void revoke( Permission permission) { acl.remove(permission);}



   public Set<Metadata> getFields( DocType type)
   {
      Set<Metadata> meta = new TreeSet<>();
      if (parent != null)
         meta.addAll( getFields(parent));

      meta.addAll(schema.getFields());
      return meta;
   }//getFields


   public Iterator<Metadata> iterator()
   {
      Set<Metadata> meta = getFields(this);
      return meta.iterator();
   }//iterator


}//DocType