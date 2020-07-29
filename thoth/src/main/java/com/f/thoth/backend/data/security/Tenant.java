package com.f.thoth.backend.data.security;

import java.util.Set;
import java.util.TreeSet;

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
	private Set<Role>     roles;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 100)
	@Valid
	private Set<SingleUser>  singleUsers;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 100)
	@Valid
	private Set<UserGroup>  userGroups;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@OrderColumn
	@JoinColumn
	@BatchSize(size = 100)
	@Valid
	private Set<DocType>  docTypes;

	// ------------- Constructors ----------------------

	public Tenant()
	{
		super();
		allocate();
		buildCode();
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

	@Override protected void buildCode(){ this.code = (name == null? "[name]":name);}

	private void allocate()
	{
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

	public String         getAdministrator() { return administrator;}
	public void           setAdministrator( String administrator) { this.administrator = administrator;}

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

		if (o == null || getClass() != o.getClass())
			return false;

		Tenant that = (Tenant) o;

		return this.name.equals(that.name);

	}// equals

	@Override
	public int hashCode() { return name.hashCode(); }

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