package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

/**
 * Representa un indice de expediente
 */
@NamedEntityGraphs({
   @NamedEntityGraph(
   name = ExpedienteIndex.BRIEF,
   attributeNodes = {
      @NamedAttributeNode("tenant"),
      @NamedAttributeNode("code"),
      @NamedAttributeNode("name"),
      @NamedAttributeNode("type"),
      @NamedAttributeNode("owner"),
      @NamedAttributeNode("expedienteCode"),
      @NamedAttributeNode("dateOpened"),
      @NamedAttributeNode("dateClosed"),
      @NamedAttributeNode("path"),
      @NamedAttributeNode("open"),
      @NamedAttributeNode("path"),
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
   name = ExpedienteIndex.FULL,
   attributeNodes = {
      @NamedAttributeNode("tenant"),
      @NamedAttributeNode("code"),
      @NamedAttributeNode("name"),
      @NamedAttributeNode("type"),
      @NamedAttributeNode("owner"),
      @NamedAttributeNode("expedienteCode"),
      @NamedAttributeNode("open"),
      @NamedAttributeNode("createdBy"),
      @NamedAttributeNode("dateOpened"),
      @NamedAttributeNode("dateClosed"),
      @NamedAttributeNode("path"),
      @NamedAttributeNode("metadata"),
      @NamedAttributeNode("keywords"),
      @NamedAttributeNode("location"),
      @NamedAttributeNode("mac"),
      //@NamedAttributeNode("entries"),
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
   // TODO:  Ojo, cargar el subgraph del Set<IndexEntry> entries
   )
})

@Entity
@Table(name = "EXPEDIENTE_INDEX", indexes = {@Index(columnList= "keywords")})
public class ExpedienteIndex extends BaseEntity implements  NeedsProtection, HierarchicalEntity<String>, Comparable<ExpedienteIndex>
{
   public static final String BRIEF = "ExpedienteIndex.brief";
   public static final String FULL  = "ExpedienteIndex.full";

   @NotNull  (message = "{evidentia.name.required}")
   @NotBlank (message = "{evidentia.name.required}")
   @NotEmpty (message = "{evidentia.name.required}")
   @Size(max = 255)
   protected String             name;                       // Expediente name

   @NotNull(message = "{evidentia.objectToProtect.required}")
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   protected ObjectToProtect    objectToProtect;            // Associated security object

   @NotNull(message = "{evidentia.type.required}")
   @Enumerated(EnumType.STRING)
   protected NodeType           type;                       // Node type: {EXPEDIENTE}

   @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
   protected User               createdBy;                  // User that created this expediente

 //  @NotNull(message = "evidentia.metadata.required")
   @OneToOne(cascade= CascadeType.MERGE, fetch = FetchType.LAZY, orphanRemoval = true)
   protected SchemaValues       metadata;                   // Metadata values of the associated expediente

   @NotNull(message = "{evidentia.dateopened.required}")
   protected LocalDateTime      dateOpened;                 // Date expediente was opened

   @NotNull(message = "{evidentia.dateclosed.required}")
   protected LocalDateTime      dateClosed;                 // Date expediente was closed

   protected String             owner;                      // Business Id(code) of Group/Expediente/Volume to which the index belongs

   @NotNull(message = "{evidentia.expedientecode.required}")
   protected String             expedienteCode;             // Expediente code

   protected String             path;                       // Node path in document repository
/*
   @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, orphanRemoval = true)
   @JoinColumn(name="entry_id")
   @BatchSize(size = 50)
   protected Set<IndexEntry>    entries;                    // Entries in the index
*/
   @NotNull(message = "{evidentia.open.required}")
   protected boolean            open;                       // Is the expediente currently open?

   protected String             keywords;                   // Search keywords

   protected String             location;                   // Signatura topografica

   @NotNull(message = "{evidentia.mac.required}")
   protected String             mac;                        // Message authentication code


   // ------------- Constructors ------------------
   public ExpedienteIndex()
   {
   }//ExpedienteIndex null constructor


   public ExpedienteIndex(Tenant tenant, String owner)
   {
      super();
      init(tenant, owner);
   }//ExpedienteIndex constructor


   private void init(Tenant tenant, String owner)
   {
      if (tenant == null)
      {  throw new IllegalArgumentException("Tenant dueÃ±o del índice de expediente no puede ser nulo");
      }
      if (owner == null)
      {  throw new IllegalArgumentException("Expediente dueÃ±o del índice de expediente no puede ser nulo");
      }
      LocalDateTime now        = LocalDateTime.now();
      this.tenant              = tenant;
      this.code                = null;
      this.name                = "[EXPEDIENTE_INDEX]";
      this.type                = NodeType.EXPEDIENTE_INDEX;
      this.objectToProtect     = new ObjectToProtect();
      this.dateOpened          = now;
      this.dateClosed          = LocalDateTime.MAX;
      this.owner               = owner;
      this.metadata            = SchemaValues.EMPTY;
      this.expedienteCode      = obtainExpedienteCode(code);
      this.path                = Parm.PATH_SEPARATOR;
//      this.entries             = new TreeSet<>();
      this.mac                 = "";

   }//init


   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      objectToProtect.prepareData();
      buildCode();
   }//prepareData


   @Override protected void buildCode()
   {
      if ( this.code == null)
      {  this.path = (tenant    == null? "/[tenant]": tenant.getWorkspace())+ Parm.PATH_SEPARATOR+ 
                  NodeType.EXPEDIENTE_INDEX.getCode()+ Parm.PATH_SEPARATOR+
                  (owner == null? "" : owner)+ Parm.PATH_SEPARATOR+ (expedienteCode == null? "[expedienteCode]" : expedienteCode);
                  //TODO: Cambiar el owner Id por el owner code
         this.code = this.path;
      }
   }//buildCode


   private String  obtainExpedienteCode( String code)
   {
      int i = code == null? -1 : code.lastIndexOf(Parm.PATH_SEPARATOR);
      String expedienteCode = i < 0? code: code.substring(i);
      return expedienteCode;
   }//obtainExpedienteCode


   // -------------- Getters & Setters ----------------
   public void             setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect;}

   public void             setOwner(String owner)                     { this.owner = owner;}

   public void             setName( String name)                      { this.name = name;}

   public NodeType         getType()                                  { return type;}
   public void             setType( NodeType type)                    { this.type = type;}

   public User             getCreatedBy()                             { return createdBy;}
   public void             setCreatedBy( User createdBy)              { this.createdBy = createdBy;}

   public LocalDateTime    getDateOpened()                            { return dateOpened;}
   public void             setDateOpened( LocalDateTime dateOpened)   { this.dateOpened = dateOpened;}

   public LocalDateTime    getDateClosed()                            { return dateClosed;}
   public void             setDateClosed( LocalDateTime dateClosed)   { this.dateClosed = dateClosed;}

   public SchemaValues     getMetadata()                              { return metadata;}
   public void             setMetadata ( SchemaValues metadata)       { this.metadata = metadata;}

   public String           getExpedienteCode()                        { return expedienteCode;}
   public void             setExpedienteCode ( String expedienteCode) { this.expedienteCode = expedienteCode;}

   public String           getPath()                                  { return path;}
   public void             setPath ( String path)                     { this.path = path;}

   /*
   public Set<IndexEntry>  getEntries(){ return entries;}
   public void             setEntries(Set<IndexEntry> entries){ this.entries = entries;}
   public int              size() { return entries.size();}
   */

   public Boolean          getOpen()                                  { return open;}
   public Boolean          isOpen()                                   { return open;}
   public void             setOpen( Boolean open)                     { this.open = open; }

   public String           getKeywords()                              { return keywords;}
   public void             setKeywords( String keywords)              { this.keywords = keywords;}

   public String           getLocation()                              { return location;}
   public void             setLocation(String location)               { this.location = location;}

   public String           getMac()                                   { return mac;}
   public void             setMac(String mac)                         { this.mac = mac;}


   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ExpedienteIndex ))
         return false;

      ExpedienteIndex that = (ExpedienteIndex) o;
      return this.id != null && this.id.equals(that.id);

   }//equals


   @Override public int hashCode() { return id == null? 4027: id.hashCode();}


   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "ExpedienteIndex{")
       .append(  super.toString())
       .append(  "name["+ name+ "]")
       .append( " expedienteCode["+ expedienteCode+ "]")
       .append( " open["+ open+ "]")
       .append( " type["+ type.getCode()+ "]")
       .append( " createdBy["+ createdBy.getEmail()+ "]")
       .append( " path["+ path+ "]")
       .append( " dateOpened["+ TextUtil.formatDateTime(dateOpened)+ "]")
       .append( " dateClosed["+ TextUtil.formatDateTime(dateClosed)+ "]\n")
       .append( " objectToProtect["+ objectToProtect.toString()+ "]")
       .append( " owner["+ owner+ "]")
       .append( " path="+ path)
//       .append( " nEntries["+ entries.size()+ "]")
       .append( " mac=["+ mac+ "]")
       .append( " metadata["+ metadata.toString()+ "]")
       .append( " keywords["+ keywords+ "]")
       .append( " location["+ location+ "]")
       .append("\n     }\n");

      return s.toString();
   }//toString


   @Override  public int compareTo(ExpedienteIndex that)
   {
      return this.equals(that)?  0 :
      that == null?       1 :
      this.getCode().compareTo(that.getCode());

   }// compareTo


   // --------------- Implements NeedsProtection ------------------------------

   public Integer       getCategory() {return objectToProtect.getCategory();}
   public void          setCategory(Integer category) {objectToProtect.setCategory(category);}

   public User          getUserOwner() {return objectToProtect.getUserOwner();}
   public void          setUserOwner(User userOwner) {objectToProtect.setUserOwner(userOwner);}

   public Role          getRoleOwner() {return objectToProtect.getRoleOwner();}
   public void          setRoleOwner(Role roleOwner) {objectToProtect.setRoleOwner(roleOwner);}

   public UserGroup     getRestrictedTo() {return objectToProtect.getRestrictedTo();}
   public void          setRestrictedTo(UserGroup restrictedTo) {objectToProtect.setRestrictedTo(restrictedTo);}


        // --------------------------- Implements HierarchicalEntity ---------------------------------------

        @Override public String            getName()           { return name;}

        @Override public String            getCode()           { return code;}

        @Override public String            getOwner()          { return owner.toString();}

        @Override public String         formatCode()
        {
           int i = code.lastIndexOf(Parm.PATH_SEPARATOR);
           String id = code.substring(i);
           id = TextUtil.replace(id, Parm.PATH_SEPARATOR, "-");
           return id;
        }//formatCode


   // -----------------  Implements NeedsProtection ----------------

   @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

   @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( User user)                 { return objectToProtect.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

   @Override public void            grant( Permission  permission)        { objectToProtect.grant(permission);}

   @Override public void            revoke(Permission permission)         { objectToProtect.revoke(permission);}


   // --------------- Logic ------------------------------


}//ExpedienteIndex