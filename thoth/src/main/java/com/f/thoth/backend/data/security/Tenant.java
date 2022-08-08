package com.f.thoth.backend.data.security;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;


/**
 *  Representa una instancia del sistema,
 *  dueña de sus propias definiciones y datos
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
         name = Tenant.BRIEF,
         attributeNodes = {
               @NamedAttributeNode("name"),
               @NamedAttributeNode("administrator"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("locked"),
               @NamedAttributeNode("workspace")
         }),
   @NamedEntityGraph(
         name = Tenant.FULL,
         attributeNodes = {
               @NamedAttributeNode("name"),
               @NamedAttributeNode("administrator"),
               @NamedAttributeNode("fromDate"),
               @NamedAttributeNode("toDate"),
               @NamedAttributeNode("locked"),
               @NamedAttributeNode("workspace")
               /*
               @NamedAttributeNode("roles"),
               @NamedAttributeNode("singleUsers"),
               @NamedAttributeNode("userGroups"),
               */
         }) })
@Entity
@Table(name = "TENANT", indexes = { @Index(columnList = "name") })
public class Tenant extends AbstractEntity implements Comparable<Tenant>
{
   public static final String BRIEF = "Tenant.brief";
   public static final String FULL  = "Tenant.full";

   @NotBlank(message = "{evidentia.name.required}")
   @NotEmpty(message = "{evidentia.name.required}")
   @Size(min = 2, max = 255, message="{evidentia.name.minmaxlength}")
   @Column(unique = true)
   private String       name;                        // Nombre del Tenant

   @NotNull (message = "{evidentia.code.required}")
   @NotEmpty(message = "{evidentia.code.required}")
   @Size(max = 255, message="{evidentia.code.maxlength}")
   @Column(unique = true)
   protected String code;                            // Código del Tenant

   @NotEmpty(message = "{evidentia.email.required}")
   @Email
   @Size(min=3, max = 255, message="{evidentia.email.length}")
   private String       administrator;               // Administrador general del Tenant

   @NotNull(message = "{evidentia.date.required}")
   @PastOrPresent(message="{evidentia.date.pastorpresent}")
   protected LocalDate  fromDate;                    // Fecha desde la cual puede usar el sistema (inclusive)

   @NotNull(message = "{evidentia.date.required}")
   protected LocalDate  toDate;                      // Fecha hasta la cual puede usar el sistema (inclusive)

   protected String  workspace;                      // Ruta de la raíz del workspace del repositorio asignado al Tenant

   protected boolean locked = false;                 // Está el Tenant bloqueado? (No puede usar el sistema)


   /*
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @OrderColumn
   @JoinColumn(name="role_id")
   @BatchSize(size = 50)
   @Valid

   private Set<Role>     roles;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn(name="user_id")
   @BatchSize(size = 100)
   @Valid
   private Set<User>  singleUsers;

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @OrderColumn
   @JoinColumn(name="group_id")
   @BatchSize(size = 50)
   @Valid
   private Set<UserGroup>  userGroups;
   */
   //TODO: Tenant debe ser Singleton, cargado cuando se carga su primer usuario.
   //TODO: Precargar los roles, users y groups crea el problema que al administrar dichos roles, users, groups
   //      se deberían actualizar en RAM los roles, users, groups  creados/editados/borrados.
   //      Procurar borrar esta lógica y cargar los objetos correspondientes cuando se requieran

   @Transient
   private Set<Role>             roles;

   @Transient
   private Set<User>             singleUsers;

   @Transient
   private Set<UserGroup>        userGroups;


   // ------------- Constructors ----------------------

   public Tenant()
   {
      super();
      init();
   }//Tenant null constructor

   public Tenant( String name)
   {
      super();

      if ( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");

      init();
      this.name = name;
   }//Tenant constructor


   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      buildCode();
   }//prepareData


   protected void buildCode()
   {
      if ( this.code == null)
      {
        this.code      = Parm.PATH_SEPARATOR+ name;
        this.workspace = code;
      }
   }//buildCode

   private void init()
   {
      LocalDate now = LocalDate.now();

      administrator= "";
      name         = "[name]";
      code         = null;
      fromDate     = now;
      toDate       = now.plusYears(1);
      workspace    = code;
      roles        = new TreeSet<>();
      singleUsers  = new TreeSet<>();
      userGroups   = new TreeSet<>();
   }//allocate

   // -------------- Getters & Setters ----------------

   public boolean      isPersisted() { return id != null; }

   public int          getVersion() { return version; }

   public String       getCode() { return code; }
   public String       quickCode()  {   return "["+ id+ "]"; }
   public void         setCode(String code) { this.code = code; }

   public String       getName()  { return name;}
   public void         setName( String name) { this.name = name; }

   public void         setLocked(boolean locked) { this.locked = locked;}
   public boolean      isLocked()
   {
      if( locked)
         return true;
      else
      {
         LocalDate now = LocalDate.now();
         return (fromDate != null && now.compareTo(fromDate) < 0) || (toDate != null && now.compareTo(toDate) > 0);
      }
   }//isLocked

   public String             getAdministrator() { return administrator;}
   public void               setAdministrator( String administrator) { this.administrator = administrator;}

   public LocalDate          getFromDate() {  return fromDate;}
   public void               setFromDate(LocalDate fromDate) { this.fromDate = fromDate;}

   public LocalDate          getToDate() { return toDate; }
   public void               setToDate(LocalDate toDate) { this.toDate = toDate; }

   public String             getWorkspace() { return workspace; }
   public void               setWorkspace(String workspace) { this.workspace = workspace; }

   public Set<Role>          getRoles() { return roles;}
   public void               setRoles( Set<Role> roles) { this.roles = roles;}

   public Set<User>          getSingleUsers() { return singleUsers;}
   public void               setUsers( Set<User> singleUsers){ this.singleUsers = singleUsers;}

   public Set<UserGroup>     getUserGroups() { return userGroups;}
   public void               setUserGroups( Set<UserGroup> userGroups){ this.userGroups = userGroups;}

   // --------------- Object methods ------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Tenant ))
         return false;

      Tenant that = (Tenant) o;
        return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return id == null? 1023: id.hashCode(); }

   @Override
   public String toString()
   {
      return "Tenant{ id["+ id+ "] version["+ version+ "] name["+ name+ "] "+
             "code["+  code+ "] workspace["+ workspace+ "] "+
             "roles["+  roles.size()+ "] singleUsers["+ singleUsers.size()+ "] "+
             "userGroups["+ userGroups.size()+ "]}";
   }//toString

   @Override
   public int compareTo(Tenant that)
   {
      return this.equals(that)?  0 :
         this.name == null  && that.name == null?  0 :
         this.name != null  && that.name == null?  1 :
         this.name == null  && that.name != null? -1 :
         this.name.compareTo(that.name);

   }// compareTo


   // --------------- Logic ---------------------

   public void addRole( Role role) { roles.add(role);}

   public void addUserGroup( UserGroup group) { userGroups.add(group); }

   public User getSingleUserById( String userCode)
   {
      for ( User s: singleUsers )
      {
         if ( s.getCode().equals(userCode))
            return s;
      }
      return null;
   }//getSingleUserById

   public UserGroup getUserGroupById( String groupCode)
   {
      for ( UserGroup ug: userGroups )
      {
         if ( ug.getCode().equals(groupCode))
            return ug;
      }
      return null;
   }//getUserGroupById



}//Tenant