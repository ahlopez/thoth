package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
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
                                        @NamedAttributeNode("dateOpened"),
                                        @NamedAttributeNode("dateClosed"),
                                        @NamedAttributeNode("classificationClass"),
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
                                        @NamedAttributeNode("createdBy"),
                                        @NamedAttributeNode("dateOpened"),
                                        @NamedAttributeNode("dateClosed"),
                                        @NamedAttributeNode("classificationClass"),
                                        @NamedAttributeNode("path"),
                                        @NamedAttributeNode("metadata"),
                                        @NamedAttributeNode("location"),
                                        @NamedAttributeNode("mac"),
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
                        // TODO:  Ojo, cargar el subraph del Set<String> keywords
                        )
})

@Entity
@Table(name = "EXPEDIENTE_INDEX", indexes = { @Index(columnList = "code"), @Index(columnList= "keywords") })
public class ExpedienteIndex extends BaseEntity implements  NeedsProtection, HierarchicalEntity<ExpedienteIndex>, Comparable<ExpedienteIndex>
{
        public static final String BRIEF = "ExpedienteIndex.brief";
        public static final String FULL  = "ExpedienteIndex.full";

        @NotNull  (message = "{evidentia.name.required}")
        @NotBlank (message = "{evidentia.name.required}")
        @NotEmpty (message = "{evidentia.name.required}")
        @Size(max = 255)
        @Column(unique = true)
        protected String            name;                       // Expediente name

        @NotNull(message = "{evidentia.objectToProtect.required}")
        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
        protected ObjectToProtect  objectToProtect;             // Associated security object

        @NotNull(message = "{evidentia.level.required}")
        @Enumerated(EnumType.STRING)
        protected NodeType         type;                        // Node type: {EXPEDIENTE}

        @ManyToOne
        protected User             createdBy;                   // User that created this expediente

        @NotNull(message = "{evidentia.class.required}")
        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        protected Classification    classificationClass;        // Classification class to which this expediente belongs

        @NotNull(message = "evidentia.metadata.required")
        @OneToOne(cascade= CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        protected SchemaValues      metadata;                   // Metadata values of the associated expediente

        @NotNull(message = "{evidentia.dateopened.required}")
        protected LocalDateTime     dateOpened;                 // Date expediente was opened

        @NotNull(message = "{evidentia.dateclosed.required}")
        protected LocalDateTime     dateClosed;                 // Date expediente was closed

        @ManyToOne
        protected ExpedienteIndex    owner;                     // Expediente to which this SUBEXPEDIENTE/VOLUMEN belongs

        @NotNull(message = "{evidentia.expedientecode.required}")
        protected String             expedienteCode;            // Expediente code

        protected String             path;                      // Node path in document repository

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        @JoinColumn(name="entry_id")
        @BatchSize(size = 50)
        public Set<IndexEntry>       entries;                   // Entries in the index

        @NotNull(message = "{evidentia.open.required}")
        protected boolean            open;                      // Is the expediente currently open?

        protected String             keywords;                  // Search keywords

        protected String             location;                  // Signatura topográfica

        @NotNull(message = "{evidentia.mac.required}")
        public String                mac;                       // Message authentication code

        // ------------- Constructors ------------------
        public ExpedienteIndex()
        {
                super();
                init();
                objectToProtect = new ObjectToProtect();
                buildCode();
        }//ExpedienteIndex null constructor

        public ExpedienteIndex( String name, Classification classificationClass, String expedienteCode, ExpedienteIndex owner, ObjectToProtect objectToProtect)
        {
                if ( !TextUtil.isValidName(name))
                        throw new IllegalArgumentException("Nombre["+ name+ "] es invalido");

                if ( TextUtil.isEmpty(name))
                        throw new IllegalArgumentException("Nombre del expediente no puede ser nulo ni vacío");

                if ( objectToProtect == null)
                        throw new IllegalArgumentException("Objeto de seguridad del expediente no puede ser nulo");

                init();
                this.name                = TextUtil.nameTidy(name);
                this.classificationClass = classificationClass;
                this.expedienteCode      = expedienteCode;
                this.owner               = owner;
                this.objectToProtect     = objectToProtect;
                buildCode();
        }//ExpedienteIndex constructor

        private void init()
        {
                LocalDateTime now        = LocalDateTime.now();
                this.dateOpened          = now;
                this.dateClosed          = LocalDateTime.MAX;
                this.owner               = null;
                this.metadata            = null;
                this.classificationClass = null;
                this.expedienteCode      = null;
                this.path                = "/";

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
                this.path = (tenant    == null? "/[tenant]": tenant.getWorkspace())+ "/"+ NodeType.EXPEDIENTE.getCode()+ "/"+
                                getOwnerPath(owner)+ (expedienteCode == null? "[expedienteCode]" : expedienteCode);
                this.code = this.path;
        }//buildCode


        // -------------- Getters & Setters ----------------
        public void             setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect; }
        public void             setOwner(ExpedienteIndex owner){ this.owner = owner; }

        public Classification   getClassificationClass() { return classificationClass;}
        public void             setClassificationClass( Classification classificationClass) { this.classificationClass = classificationClass;}

        public User       getCreatedBy() { return createdBy;}
        public void             setCreatedBy( User createdBy){ this.createdBy = createdBy;}

        public LocalDateTime    getDateOpened() { return dateOpened;}
        public void             setDateOpened( LocalDateTime dateOpened) { this.dateOpened = dateOpened;}

        public LocalDateTime    getDateClosed() { return dateClosed;}
        public void             setDateClosed( LocalDateTime dateClosed){ this.dateClosed = dateClosed;}

        public SchemaValues     getMetadata() { return metadata;}
        public void             setMetadata ( SchemaValues metadata) { this.metadata = metadata;}

        public Classification   getClassification() { return classificationClass;}
        public void             setClassification ( Classification classificationClass) { this.classificationClass = classificationClass;}

        public String           getExpedienteCode() { return expedienteCode;}
        public void             setExpedienteCode ( String expedienteCode) { this.expedienteCode = expedienteCode;}

        public String           getPath() { return path;}
        public void             setPath ( String path) { this.path = path;}

        public Set<IndexEntry>  getEntries(){ return entries;}
        public void             setEntries(Set<IndexEntry> entries){ this.entries = entries;}
        public int              size() { return entries.size();}

        public String           getKeywords() { return keywords;}
        public void             setKeywords( String keywords) { this.keywords = keywords;}

        public String           getLocation() { return location;}
        public void             setLocation(String location) { this.location = location;}

        public String           getMac() { return mac;}
        public void             setMac(String mac) { this.mac = mac;}

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
                .append( " classCode["+ classificationClass.formatCode()+ "]")
                .append( " expedienteCode["+ expedienteCode+ "]")
                .append( " path["+ path+ "]")
                .append( " dateOpened["+ TextUtil.formatDate(dateOpened.toLocalDate())+ "]")
                .append( " dateClosed["+ TextUtil.formatDate(dateClosed.toLocalDate())+ "]\n")
                .append( " objectToProtect["+ objectToProtect.toString()+ "]")
                .append( " path="+ path)
                .append( " mac=["+ mac+ "]")
                .append("\n     }\n");
                //TODO:  Revisar que estén todos los campos

                return s.toString();
        }//toString


        @Override  public int compareTo(ExpedienteIndex that)
        {
                return this.equals(that)?  0 :
                        that == null?       1 :
                                this.getCode().compareTo(that.getCode());

        }// compareTo


        // --------------- Implements NeedsProtection ------------------------------

        public Integer               getCategory() {return objectToProtect.getCategory();}
        public void                  setCategory(Integer category) {objectToProtect.setCategory(category);}

        public User            getUserOwner() {return objectToProtect.getUserOwner();}
        public void                  setUserOwner(User userOwner) {objectToProtect.setUserOwner(userOwner);}

        public Role                  getRoleOwner() {return objectToProtect.getRoleOwner();}
        public void                  setRoleOwner(Role roleOwner) {objectToProtect.setRoleOwner(roleOwner);}

        public UserGroup             getRestrictedTo() {return objectToProtect.getRestrictedTo();}
        public void                  setRestrictedTo(UserGroup restrictedTo) {objectToProtect.setRestrictedTo(restrictedTo);}

        // --------------------------- Implements HierarchicalEntity ---------------------------------------
        @Override public String           getName()   { return name;}

        @Override public ExpedienteIndex   getOwner()  { return owner;}

        @Override public String      formatCode()
        {
                int i = TextUtil.indexOf(code, "/", 3);
                String id = code.substring(i);
                id = TextUtil.replace(id, "/", "-");
                return id;
        }//formatCode


        private String getOwnerPath(ExpedienteIndex owner)
        {
                String path = "";
                while (owner != null)
                {
                        path = owner.getClassificationClass().getClassCode()+ "/"+ path;
                        owner = owner.owner;
                }
                return  path;
        }//getOwnerPath

        // -----------------  Implements NeedsProtection ----------------

        @Override public ObjectToProtect getObjectToProtect()                  { return objectToProtect;}

        @Override public boolean         canBeAccessedBy(Integer userCategory) { return objectToProtect.canBeAccessedBy(userCategory);}

        @Override public boolean         isOwnedBy( User user)           { return objectToProtect.isOwnedBy(user);}

        @Override public boolean         isOwnedBy( Role role)                 { return objectToProtect.isOwnedBy(role);}

        @Override public boolean         isRestrictedTo( UserGroup userGroup)  { return objectToProtect.isRestrictedTo(userGroup);}

        @Override public boolean         admits( Role role)                    { return objectToProtect.admits(role);}

        @Override public void            grant( Permission  permission)        { objectToProtect.grant(permission);}

        @Override public void            revoke(Permission permission)         { objectToProtect.revoke(permission);}


        // --------------- Logic ------------------------------
        public boolean isOpen()
        {
          LocalDateTime now = LocalDateTime.now();
          return open &&
              ((now.equals(dateOpened) || now.equals(dateClosed)) ||
                  (now.isAfter(dateOpened) && now.isBefore(dateClosed))) ;
        }//isOpen

}//ExpedienteIndex