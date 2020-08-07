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

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.DocType;


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
					@NamedAttributeNode("locked")
			}),
	@NamedEntityGraph(
			name = Tenant.FULL,
			attributeNodes = {
					@NamedAttributeNode("name"),
					@NamedAttributeNode("administrator"),
					@NamedAttributeNode("fromDate"),
					@NamedAttributeNode("toDate"),
					@NamedAttributeNode("locked")
					/*
					@NamedAttributeNode("roles"),
					@NamedAttributeNode("singleUsers"),
					@NamedAttributeNode("userGroups"),
					@NamedAttributeNode("docTypes")
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
	private String       name;

	@NotEmpty(message = "{evidentia.email.required}")
	@Email
	@Size(min=3, max = 255, message="{evidentia.email.length}")
	private String       administrator;

	@NotNull(message = "{evidentia.date.required}")
	@PastOrPresent(message="{evidentia.date.pastorpresent}")
	protected LocalDate  fromDate;

	@NotNull(message = "{evidentia.date.required}")
	protected LocalDate  toDate;

	protected boolean locked = false;

	/*
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 50)
	@Valid
	
	private Set<Role>     roles;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 100)
	@Valid
	private Set<SingleUser>  singleUsers;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 50)
	@Valid
	private Set<UserGroup>  userGroups;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 50)
	@Valid
	private Set<DocType>  docTypes;
	*/
	
	@Transient
	private Set<Role>        roles;
	@Transient
	private Set<SingleUser>  singleUsers;
	@Transient
	private Set<UserGroup>   userGroups;
	@Transient
	private Set<DocType>     docTypes;


	// ------------- Constructors ----------------------

	public Tenant()
	{
		super();
		init();
		buildCode();
	}

	public Tenant( String name)
	{
		super();

		if ( !TextUtil.isValidName(name))
			throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");

		this.name = TextUtil.nameTidy(name);
		init();
		buildCode();
	}//Tenant

	
	@PrePersist
	@PreUpdate
	public void prepareData()
	{
		this.name     =  TextUtil.nameTidy(name);
		buildCode();
	}//prepareData


	@Override protected void buildCode(){ this.code = (name == null? "[name]" : name);}

	private void init()
	{
		LocalDate now = LocalDate.now();
		LocalDate yearStart =now.minusDays(now.getDayOfYear());

		administrator= "";
		fromDate     = yearStart;
		toDate       = yearStart.plusYears(1);
		roles        = new TreeSet<>();
		singleUsers  = new TreeSet<>();
		userGroups   = new TreeSet<>();
		docTypes     = new TreeSet<>();
	}//allocate

	// -------------- Getters & Setters ----------------

	public boolean      isPersisted() { return id != null; }

	public int          getVersion() { return version; }

	public String       getCode() { return code; }
	public void         setCode(String code) { this.code = code; }


	public String       getName()  { return name;}
	public void         setName( String name)
	{
		this.name = name;
		buildCode();
	}

	public void           setLocked(boolean locked) { this.locked = locked;}
	public boolean        isLocked()
	{
		if( locked)
			return true;
		else
		{
			LocalDate now = LocalDate.now();
			return (fromDate != null && now.compareTo(fromDate) < 0) || (toDate != null && now.compareTo(toDate) > 0);
		}
	}//isLocked

	public String         getAdministrator() { return administrator;}
	public void           setAdministrator( String administrator) { this.administrator = administrator;}

	public LocalDate      getFromDate() {  return fromDate;}
	public void           setFromDate(LocalDate fromDate) { this.fromDate = fromDate;}

	public LocalDate      getToDate() { return toDate; }
	public void           setToDate(LocalDate toDate) { this.toDate = toDate; }

	public Set<Role>      getRoles() { return roles;}
	public void           setRoles( Set<Role> roles) { this.roles = roles;}

	public Set<SingleUser>  getSingleUsers() { return singleUsers;}
	public void             setUsers( Set<SingleUser> singleUsers){ this.singleUsers = singleUsers;}

	public Set<UserGroup>   getUserGroups() { return userGroups;}
	public void             setUserGroups( Set<UserGroup> userGroups){ this.userGroups = userGroups;}

	public Set<DocType>   getDocTypes() { return docTypes;}
	public void           setDocTypes( Set<DocType> docTypes){ this.docTypes = docTypes;}

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
	public int hashCode() { return 1023; }

	@Override
	public String toString()
	{
		return "Tenant{ id["+ id+ "] version["+ version+ "] name["+ name+ "] code["+  code+ "] roles["+  roles.size()+
				"] singleUsers["+ singleUsers.size()+ "userGroups["+ userGroups.size()+ "] docTypes["+ docTypes.size()+ "]}";
	}

	@Override
	public int compareTo(Tenant that) { return this.equals(that)?  0:  that == null? 1: this.name.compareTo(that.name); }

	// --------------- Logic ---------------------

	public boolean contains( DocType type) { return docTypes.contains(type);}

	public void addType( DocType type) { docTypes.add(type);}

	public void addRole( Role role) { roles.add(role);}
	
	public void addUserGroup( UserGroup group) { userGroups.add(group); }

	public SingleUser getSingleUserById( String userCode)
	{
		for ( SingleUser s: singleUsers )
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

	public DocType getTypeById( String code)
	{
		for (DocType dt: docTypes)
		{
			if( dt.getCode().equals(code))
				return dt;
		}
		return null;
	}//getTypeById



}//Tenant