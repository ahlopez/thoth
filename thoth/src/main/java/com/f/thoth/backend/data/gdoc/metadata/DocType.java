package com.f.thoth.backend.data.gdoc.metadata;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;

/**
 * Representa un tipo documental
 */
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = DocType.BRIEF,
        attributeNodes = {
            @NamedAttributeNode("parms")
        }),
    @NamedEntityGraph(
        name = DocType.FULL,
        attributeNodes = {
            @NamedAttributeNode("parms"),
            @NamedAttributeNode("history")
        }) })
@Entity
@Table(name = "DOC_TYPE", indexes = { @Index(columnList = "code") })
public class DocType extends AbstractEntity implements NeedsProtection, Comparable<DocType>
{
   public static final String BRIEF = "DocType.brief";
   public static final String FULL  = "DocType.full";

   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   protected String      name;

   @NotNull (message = "{evidentia.schema.required}")
   protected Schema      schema;

   @NotNull (message = "{evidentia.category.required}")
   protected Integer    category;

   protected SingleUser userOwner;

   protected Role       roleOwner;

   protected DocType    parent;

   @NotNull (message = "{evidentia.children.required}")
   protected Set<String> children;

   protected boolean   requiresContent;

   // ------------- Constructors ------------------
   public DocType()
   {
      children = new TreeSet<String>();
      this.tenant.addType( this);
   }

   public DocType( String name, Schema schema, DocType parent, boolean requiresContent)
   {
      if( name == null || name.trim().length() == 0 || name.trim().equals(" "))
         throw new IllegalArgumentException( "Nombre["+ name+ "] del tipo documental es inv�lido");

      if(schema == null)
         throw new IllegalArgumentException( "El esquema de metadatos del tipo documental no puede ser nulo");

      if( !this.tenant.contains( parent))
         throw new IllegalArgumentException( "El tipo padre debe definirse antes que el tipo hijo");

      if( parent.name.equals(name) || children.contains(name))
         throw new IllegalArgumentException( "El padre no puede ser el mismo tipo documental, y tampoco ninguno de sus hijos");

      this.children = new TreeSet<String>();
      this.name     = name;
      this.schema   = schema;
      this.requiresContent = requiresContent;
      this.parent   = parent;
      this.parent.children.add(name);
      this.tenant.addType( this);
      buildCode();

   }//DocType

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Generico";
      buildCode();
   }

   private void buildCode() { this.code =  parent == null? tenant.toString()+ ":"+ name : parent.code + "-"+ name; }

   // -------------- Getters & Setters ----------------

   public String     getName(){ return name;}
   public void       setName( String name)
   {
      this.name = name;
      buildCode();
   }//setName

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

   public Set<String> getChildren(){ return children;}
   public void        setChildren( Set<String> children){ this.children = children;}

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

      for(String child: children)
           s.append( child).append(" ");

      s.append( "]}");
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