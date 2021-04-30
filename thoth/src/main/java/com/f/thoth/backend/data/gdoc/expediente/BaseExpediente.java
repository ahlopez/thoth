package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;

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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

/**
 * Representa un nodo de la jerarquia de expedientes (expediente/sub-expediente/volumen
 */
@NamedEntityGraphs({
	@NamedEntityGraph(
			name = BaseExpediente.BRIEF,
			attributeNodes = {
					@NamedAttributeNode("tenant"),
					@NamedAttributeNode("code"),           // DB human id. Includes [tenant, type, path+]
					@NamedAttributeNode("expedienteCode"), // Business id unique inside the owner (class or expediente), vg 001,002, etc
					@NamedAttributeNode("name"),
					@NamedAttributeNode("path"),
					@NamedAttributeNode("classificationClass"),
					@NamedAttributeNode("ownerPath"),
					@NamedAttributeNode("open"),
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
			name = BaseExpediente.FULL,
			attributeNodes = {
					@NamedAttributeNode(value="objectToProtect", subgraph = ObjectToProtect.BRIEF),
					@NamedAttributeNode("tenant"),
					@NamedAttributeNode("code"),
					@NamedAttributeNode("expedienteCode"),
					@NamedAttributeNode("name"),
					@NamedAttributeNode("classificationClass"),
					@NamedAttributeNode("createdBy"),
					@NamedAttributeNode("ownerPath"),
					@NamedAttributeNode("path"),
					@NamedAttributeNode("dateOpened"),
					@NamedAttributeNode("dateClosed"),
					@NamedAttributeNode("metadata"),
					@NamedAttributeNode("open"),
					@NamedAttributeNode("keywords"),
					@NamedAttributeNode("location"),
					//@NamedAttributeNode("expedienteIndex"),
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
			)
})

@Entity
@Table(name = "BASE_EXPEDIENTE", indexes = { @Index(columnList = "code"), @Index(columnList ="tenant_id, expedienteCode")}) 
public class BaseExpediente extends BaseEntity implements  NeedsProtection, Comparable<BaseExpediente>
{
	public static final String BRIEF = "BaseExpediente.brief";
	public static final String FULL  = "BaseExpediente.full";

	@NotNull  (message = "{evidentia.code.required}")
	@NotBlank (message = "{evidentia.code.required}")
	@NotEmpty (message = "{evidentia.code.required}")
	@Size(max = 255)
	protected String            expedienteCode;              // Business id unique inside the owner (class or expediente), vg 001,002, etc

	@NotNull  (message = "{evidentia.repopath.required}")
	@NotBlank (message = "{evidentia.repopath.required}")
	@NotEmpty (message = "{evidentia.repopath.required}")
	@Size(max = 255)
	protected String            path;                        // Node path in document repository

	@NotNull  (message = "{evidentia.name.required}")
	@NotBlank (message = "{evidentia.name.required}")
	@NotEmpty (message = "{evidentia.name.required}")
	@Size(max = 255)
	@Column(unique = true)
	protected String            name;                        // Expediente name

	@NotNull(message = "{evidentia.objectToProtect.required}")
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	protected ObjectToProtect   objectToProtect;             // Associated security object

	@NotNull  (message = "{evidentia.creator.required}")
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	protected User              createdBy;                   // User that created this expediente

	@NotNull(message = "{evidentia.class.required}")
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	protected Classification    classificationClass;         // Classification class to which this expediente belongs (Subserie si TRD)

	@OneToOne(cascade= CascadeType.MERGE, fetch = FetchType.LAZY, orphanRemoval = true)
	protected SchemaValues      metadata;                    // Metadata values of the associated expediente

	@NotNull(message = "{evidentia.dateopened.required}")
	protected LocalDateTime     dateOpened;                  // Date expediente was opened

	@NotNull(message = "{evidentia.dateclosed.required}")
	protected LocalDateTime     dateClosed;                  // Date expediente was closed

	//@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	//protected BranchExpediente  owner;                      // Expediente to which this Branch/Leaf/Volume belongs
	protected String             ownerPath;                   // Branch Expediente to which this Branch/Leaf/Volume belongs

	/*
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   @NotNull(message = "{evidentia.index.required}")
   protected ExpedienteIndex   expedienteIndex;            // Expediente index entries
	 */

	@NotNull(message = "{evidentia.open.required}")
	protected Boolean           open;                       // Is the expediente currently open?

	protected String            location;                   // Signatura topográfica

	protected String            keywords;                   // Search keywords

	@NotNull(message = "{evidentia.mac.required}")
	protected String            mac;                        // Message authentication code

	// ------------- Constructors ------------------
	public BaseExpediente()
	{
		super();
		this.expedienteCode       = "";
		this.path                 = "";
		this.name                 = "";
		this.objectToProtect      = new ObjectToProtect();
		this.createdBy            = ThothSession.getCurrentUser();
		this.classificationClass  = null;
		this.metadata             = null;
		this.dateOpened           = LocalDateTime.MAX;
		this.dateClosed           = LocalDateTime.MAX;
		this.ownerPath            = null;
		//  this.expedienteIndex      = null;
		this.open                 = false;
		this.keywords             = null;
		this.mac                  = "";

		buildCode();
	}//BaseExpediente null constructor


	public BaseExpediente( String expedienteCode, String path, String name, User createdBy, Classification classificationClass,
			SchemaValues metadata, LocalDateTime dateOpened, LocalDateTime dateClosed, String ownerPath,
			Boolean open,String keywords, String mac)
	{
		if ( TextUtil.isEmpty(expedienteCode))
			throw new IllegalArgumentException("Código del expediente no puede ser nulo ni vacío");

		if ( TextUtil.isEmpty(path))
			throw new IllegalArgumentException("Path del expediente en el repositorio no puede ser nulo ni vacío");

		if ( TextUtil.isEmpty(name))
			throw new IllegalArgumentException("Nombre del expediente no puede ser nulo ni vacío");

		if ( !TextUtil.isValidName(name))
			throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");

		if ( createdBy == null)
			throw new IllegalArgumentException("Creador del expediente no puede ser nulo");

		if ( classificationClass == null)
			throw new IllegalArgumentException("Clase del expediente no puede ser nula ni vacía");

		if ( dateOpened == null)
			throw new IllegalArgumentException("Estado de apertura del expediente es nulo. Debe ser true/false");

		this.objectToProtect     = new ObjectToProtect();
		this.expedienteCode      = expedienteCode;
		this.path                = path;
		this.name                = name;
		this.createdBy           = createdBy;
		this.classificationClass = classificationClass;
		this.metadata            = metadata;
		this.dateOpened          = dateOpened;
		this.dateClosed          = dateClosed;
		this.ownerPath           = ownerPath;
		this.open                = (open == null? false : open);
		this.keywords            = keywords;
		// this.expedienteIndex     = null;
		this.mac                 = mac;

		buildCode();
	}//BaseExpediente constructor


	public void prepareData()
	{
		objectToProtect.prepareData();
		buildCode();
	}//prepareData


	@Override protected void buildCode()
	{	  
		if (code == null)
		{
			this.path = (tenant    == null? "/[tenant]": tenant.getWorkspace())+ "/"+ NodeType.EXPEDIENTE.getCode()+ "/"+
					(ownerPath == null ? "[ROOT]/": ownerPath)+ (expedienteCode == null? "[expedienteCode]" : expedienteCode);
			this.code = this.path;
		}
	}//buildCode


	// -------------- Getters & Setters ----------------

	public String            getName() { return name;}
	public void              setName ( String name) { this.name = name;}

	public Boolean           getOpen() { return open;}
	public void              setOpen ( Boolean open) { this.open = open;}

	public void              setObjectToProtect(ObjectToProtect objectToProtect) { this.objectToProtect = objectToProtect;}

	public String            getOwnerPath() { return ownerPath;}
	public void              setOwnerPath(String ownerPath){ this.ownerPath = ownerPath;}

	public Classification    getClassificationClass() { return classificationClass;}
	public void              setClassificationClass( Classification classificationClass) { this.classificationClass = classificationClass;}

	public User              getCreatedBy() { return createdBy;}
	public void              setCreatedBy( User createdBy){ this.createdBy = createdBy;}

	public LocalDateTime     getDateOpened() { return dateOpened;}
	public void              setDateOpened( LocalDateTime dateOpened) { this.dateOpened = dateOpened;}

	public LocalDateTime     getDateClosed() { return dateClosed;}
	public void              setDateClosed( LocalDateTime dateClosed){ this.dateClosed = dateClosed;}

	public SchemaValues      getMetadata() { return metadata;}
	public void              setMetadata ( SchemaValues metadata) { this.metadata = metadata;}

	public String            getExpedienteCode() { return expedienteCode;}
	public void              setExpedienteCode ( String expedienteCode) { this.expedienteCode = expedienteCode;}

	public String            getPath() { return path;}
	public void              setPath ( String path) { this.path = path;}
	/*
   public ExpedienteIndex   getExpedienteIndex(){ return expedienteIndex;}
   public void              setExpedienteIndex(ExpedienteIndex expedienteIndex){ this.expedienteIndex = expedienteIndex;}
	 */
	public String            getKeywords() { return keywords;}
	public void              setKeywords( String keywords) { this.keywords = keywords;}

	public String            getMac() { return mac;}
	public void              setMac(String mac) { this.mac = mac;}


	// --------------- Object methods ---------------------

	@Override public boolean equals( Object o)
	{
		if (this == o)
			return true;

		if (!(o instanceof BaseExpediente ))
			return false;

		BaseExpediente that = (BaseExpediente) o;
		return this.id != null && this.id.equals(that.id);

	}//equals

	@Override public int hashCode() { return id == null? 4027: id.hashCode();}

	@Override public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append( "BaseExpediente{")
		.append( super.toString())
		.append( " name["+ name+ "]")
		.append( " open["+ open+ "]")
		.append( " user owner["+ objectToProtect.getUserOwner().getEmail()+ "]")
		.append( " createdBy["+ createdBy.getEmail()+ "]")
		.append( " classCode["+ classificationClass.formatCode()+ "]")
		.append( " expedienteCode["+ expedienteCode+ "]")
		.append( " path["+ path+ "]")
		.append( " dateOpened["+ TextUtil.formatDateTime(dateOpened)+ "]")
		.append( " dateClosed["+ TextUtil.formatDateTime(dateClosed)+ "]\n")
		.append( " objectToProtect["+ objectToProtect.toString()+ "]\n")
		.append( " expediente owner["+ (ownerPath == null? "[ROOT]": ownerPath)+ "]")
		//       .append( " n index-entries["+ expedienteIndex.size()+ "]")
		.append( " path["+ path+ "]")
		.append( " mac=["+ mac+ "]")
		.append( " metadata["+ metadata.toString()+ "]")
		.append( " keywords["+ keywords+ "]")
		.append("     }\n");

		return s.toString();
	}//toString


	@Override  public int compareTo(BaseExpediente that)
	{
		return this.equals(that)?  0 :
			that == null?       1 :
				this.code.compareTo(that.code);
	}// compareTo


	// ------------------ code & path -------------------------
	public String            formatCode()
	{
		int i = TextUtil.indexOf(code, "/", 3);
		String formattedCode = code.substring(i);
		formattedCode = TextUtil.replace(formattedCode, "/", "-");
		return formattedCode;
	}//formatCode

	/*
   private String getOwnerPath(BranchExpediente owner)
   {
      String path = "";
      while (owner != null)
      {
         path = owner.getExpedienteCode()+ "/"+ path;
         owner = owner.getOwner();
      }
      return  path;
   }//getOwnerPath
	 */

	// -----------------  Implements NeedsProtection ----------------

	public Integer                   getCategory()                           {return objectToProtect.getCategory();}
	public void                      setCategory(Integer category)           {objectToProtect.setCategory(category);}

	public User                      getUserOwner()                          {return objectToProtect.getUserOwner();}
	public void                      setUserOwner(User userOwner)            {objectToProtect.setUserOwner(userOwner);}

	public Role                      getRoleOwner()                          {return objectToProtect.getRoleOwner();}
	public void                      setRoleOwner(Role roleOwner)            {objectToProtect.setRoleOwner(roleOwner);}

	public UserGroup                 getRestrictedTo()                       {return objectToProtect.getRestrictedTo();}
	public void                      setRestrictedTo(UserGroup restrictedTo) {objectToProtect.setRestrictedTo(restrictedTo);}

	@Override public ObjectToProtect getObjectToProtect()                    { return objectToProtect;}

	@Override public boolean         canBeAccessedBy(Integer userCategory)   { return objectToProtect.canBeAccessedBy(userCategory);}

	@Override public boolean         isOwnedBy( User user)                   { return objectToProtect.isOwnedBy(user);}

	@Override public boolean         isOwnedBy( Role role)                   { return objectToProtect.isOwnedBy(role);}

	@Override public boolean         isRestrictedTo( UserGroup userGroup)    { return objectToProtect.isRestrictedTo(userGroup);}

	@Override public boolean         admits( Role role)                      { return objectToProtect.admits(role);}

	@Override public void            grant( Permission  permission)          { objectToProtect.grant(permission);}

	@Override public void            revoke(Permission permission)           { objectToProtect.revoke(permission);}


	// --------------- Logic ------------------------------
	public boolean isOpen()
	{
		LocalDateTime now = LocalDateTime.now();
		return open &&
				((now.equals(dateOpened) || now.equals(dateClosed)) ||
						(now.isAfter(dateOpened) && now.isBefore(dateClosed))) ;
	}//isOpen


	public void openExpediente()
	{
		if ( !isOpen() )
		{
			setOpen(true);
			setDateOpened(LocalDateTime.now());
		}
	}//openExpediente


	public void closeExpediente()
	{
		if(  isOpen() )
		{
			setOpen(false);
			setDateClosed(LocalDateTime.now());
			closeIndex();
		}
	}//closeExpediente


	public ExpedienteIndex  createIndex()
	{
		//TODO:  Iniciar el blockchain del indice
		ExpedienteIndex expedienteIndex    = new ExpedienteIndex();
		ObjectToProtect idxObjectToProtect = new ObjectToProtect();
		idxObjectToProtect.setRoleOwner(getRoleOwner()); //TODO: El rol de acceso debe ser ADMIN
		expedienteIndex.setObjectToProtect( idxObjectToProtect);
		expedienteIndex.setName(name);
		expedienteIndex.setType(NodeType.EXPEDIENTE_INDEX);
		expedienteIndex.setCreatedBy(createdBy);
		expedienteIndex.setMetadata(metadata);
		expedienteIndex.setDateOpened(dateOpened);
		expedienteIndex.setDateClosed(dateClosed);
		expedienteIndex.setOwnerPath(ownerPath);
		expedienteIndex.setExpedienteCode(expedienteCode);
		//       expedienteIndex.setEntries( new TreeSet<>());
		expedienteIndex.setOpen(open);
		expedienteIndex.setKeywords(keywords);
		expedienteIndex.setLocation(location);
		expedienteIndex.buildCode();
		expedienteIndex.setMac("");  // TODO: El mac debe ser calculado internamente
		return expedienteIndex;
	}//createIndex


	protected void closeIndex()
	{
		//TODO: Cerrar el blockchain del indice
	}//closeIndex

}//BaseExpediente
