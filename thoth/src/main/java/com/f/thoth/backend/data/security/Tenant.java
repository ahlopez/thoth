package com.f.thoth.backend.data.security;

import java.util.Map;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.DocType;


/**
 *  Representa una instancia del sistema,
 *  dueña de sus propias definiciones y datos
 */
@Entity
@Table(name = "TENANT", indexes = { @Index(columnList = "name") })
public class Tenant extends AbstractEntity implements Comparable<Tenant>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotEmpty(message = "{evidentia.name.required}")
   @Size(min = 2, max = 255, message="{evidentia.name.minmaxlength}")
   @Column(unique = true)
   private String         name;
 
   @NotEmpty(message = "{evidentia.email.required}")
   @Email
   @Size(min=3, max = 255, message="{evidentia.email.length}")
   private String         administrator;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 50)
   @Valid
   private Map<String,Role>     roles;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 100)
   @Valid
   private Map<String,Usuario>  users;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 100)
   @Valid
   private Map<String,DocType>  docTypes;

   // ------------- Constructors ----------------------

   public Tenant()
   {
      super();
      allocate();
   }

   public Tenant( String name)
   {
      super();

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");

      this.name = TextUtil.nameTidy(name);
      allocate();
      buildCode();
   }//Tenant

   private void buildCode(){ this.code = name;}

   private void allocate()
   {
      roles    = new TreeMap<>();
      users    = new TreeMap<>();
      docTypes = new TreeMap<>();
   }//allocate

   // -------------- Getters & Setters ----------------

   public String       getName()  { return name;}
   public void         setName( String name)
   {
      this.name = name;
      buildCode();
   }
   
   public String       getAdministrator() { return administrator;}
   public void         setAdministrator( String administrator) { this.administrator = administrator;}

   public Map<String,Role>    getRoles() { return roles;}
   public void                setRoles( Map<String,Role> roles) { this.roles = roles;}

   public Map<String,Usuario> getUsers() { return users;}
   public void                setUsers( Map<String,Usuario> users){ this.users = users;}

   public Map<String,DocType> getDocTypes() { return docTypes;}
   public void                setDocTypes( Map<String,DocType> docTypes){ this.docTypes = docTypes;}

   // --------------- Object methods ------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      Tenant that = (Tenant) o;

      return this.name.equals(that.name);

   }// equals

   @Override
   public int hashCode() { return name.hashCode(); }

   @Override
   public String toString() { return "Tenant{"+ super.toString()+ " name["+ name+ "] roles["+  roles.size()+ "] users["+ users.size()+ "] docTypes["+ docTypes.size()+ "]}";}

   @Override
   public int compareTo(Tenant that) { return this.equals(that)?  0:  that == null? 1: this.name.compareTo(that.name); }

   // --------------- Logic ---------------------

   public boolean contains( DocType type) { return docTypes.get(type.getCode()) != null;}

   public void addType( DocType type) { docTypes.put(type.getCode(), type);}

   public Usuario getUserById( String userId) { return users.get(userId);}

   public DocType getTypeById( String code){ return docTypes.get(code);}


}//Tenant