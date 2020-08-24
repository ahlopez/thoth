package com.f.thoth.backend.data.security;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.ui.utils.Constant;


/**
 * Representa un objeto que requiere protección
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = ObjectToProtect.BRIEF,
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
         name = ObjectToProtect.FULL,
         attributeNodes = {
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
public class ObjectToProtect extends BaseEntity  implements NeedsProtection, HierarchicalEntity<ObjectToProtect>, Comparable<ObjectToProtect>
{
   public static final String BRIEF = "ObjectToProtect.brief";
   public static final String FULL  = "ObjectToProtect.full";

   @NotNull  (message = "{evidentia.name.required}")
   @NotBlank (message = "{evidentia.name.required}")
   @NotEmpty (message = "{evidentia.name.required}")
   @Size(max = 255)
   @Column(unique = true)
   protected String          name;    // Object name

   @NotNull     (message= "{evidentia.category.required}")
   @Min(value=0, message= "{evidentia.category.minvalue}")
   @Max(value=5, message= "{evidentia.category.maxvalue}")
   protected Integer         category; // Security category

   @ManyToOne
   protected ObjectToProtect owner;    // Object group to which this object belongs

   @ManyToOne
   protected SingleUser      userOwner;  // User that owns this object

   @ManyToOne
   protected Role            roleOwner;  // Role that owns this object

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 20)
   protected Set<Role>       acl;   // Access control list

   // -------------- Constructors -------------
   public ObjectToProtect()
   {
      super();
      name = "";
      init();
      buildCode();
   }//ObjectToProtect

   public ObjectToProtect( String name)
   {
      super();
      if ( TextUtil.isEmpty(name))
         throw new IllegalArgumentException("Nombre del objeto no puede ser nulo ni vacío");

      this.name = name;
      init();
      buildCode();
   }//ObjectToProtect

   private void init()
   {
      category  = Constant.DEFAULT_CATEGORY;
      owner     = null;
      userOwner = null;
      roleOwner = null;
      acl       = new TreeSet<>();
   }//init


   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name     =  TextUtil.nameTidy(name).toLowerCase();
      buildCode();
   }//prepareData

   @Override public void buildCode()
   {
      this.code = (tenant == null? "[Tenant]": tenant.getCode())+
            ">"+
            (name == null? "[name]" : name);
   }//buildCode

   // ----------------- Getters & Setters ----------------

   public void            setName(String name) { this.name = name;}

   public void            setOwner(ObjectToProtect owner) {this.owner = owner;}

   public SingleUser      getUserOwner() {return userOwner;}
   public void            setUserOwner(SingleUser userOwner) {this.userOwner = userOwner;}

   public Role            getRoleOwner() {return roleOwner;}
   public void            setRoleOwner(Role roleOwner) {this.roleOwner = roleOwner;}

   public Integer         getCategory() {return category;}
   public void            setCategory(Integer category) {this.category = category;}

   public Set<Role>       getAcl() {return acl;}
   public void            setAcl(Set<Role> acl) {this.acl = acl;}

   // Implements HierarchicalEntity
   @Override public Long            getId()     { return super.getId();}
   @Override public String          getCode()   { return super.getCode();}
   @Override public String          getName()   { return name;}
   @Override public ObjectToProtect getOwner()  { return owner;}

   // ---------------------- Object -----------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ObjectToProtect ))
         return false;

      ObjectToProtect that = (ObjectToProtect) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 7: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" ObjectToProtect{"+ super.toString())
       .append(" name["+ name+ "]")
       .append(" category["+ category+ "]")
       .append(" owner["+ (owner.getName() == null? "--": owner.getName())+ "]")
       .append(" userOwner["+ (userOwner == null? "-NO-": userOwner.getCode())+ "]")
       .append(" roleOwner["+ (roleOwner == null? "-NO-": roleOwner.getCode())+ "]}\n\tAcl{");

      int i = 1;
      for( Role r: acl)
      {
         s.append((i % 10 == 0? "\n": " "))
          .append(r.getCode());
         i++;
      }
      s.append("\n\t}\n");

      return s.toString();
   }//toString

   @Override public int compareTo(ObjectToProtect that)
   {
      return this.equals(that)?  0 :
         that ==  null    ?  1 :
         this.code == null  && that.code == null?  0 :
         this.code != null  && that.code == null?  1 :
         this.code == null  && that.code != null? -1 :
         this.code.compareTo(that.code);

   }//compareTo

   // -----------------  Logic ----------------
   @Override public String  getKey() { return code;}

   @Override public boolean canBeAccessedBy(Integer userCategory) { return userCategory != null && category.compareTo(userCategory) <= 0;}

   @Override public boolean isOwnedBy( SingleUser user) { return userOwner != null && user != null && userOwner.equals(user);}

   @Override public boolean isOwnedBy( Role role) { return roleOwner != null && role != null && roleOwner.equals(role);}
   
   @Override public boolean admits( Role role) { return acl.contains(role); }
   
   @Override public void grant( Role role) { acl.add(role);}
   
   @Override public void revoke( Role role) { acl.remove(role);}

}//ObjectToProtect
