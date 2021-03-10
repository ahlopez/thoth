package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

/**
 * Representa un nodo de la jerarquia de expedientes (expediente/sub-expediente/volumen
 */
@Entity
@Table(name = "BRANCH_EXPEDIENTE")
public class BranchExpediente extends AbstractEntity implements  NeedsProtection, HierarchicalEntity<BranchExpediente>, Comparable<BranchExpediente>
{
	@OneToOne
	@NotNull  (message = "{evidentia.expediente.required}")
	protected BaseExpediente       expediente;                 // Expediente that describes this branch

	@OneToMany
	protected Set<BaseExpediente>  children;                   // Children of this expediente

	// ------------- Constructors ------------------
	public BranchExpediente()
	{
		super();
		this.expediente = null;
		this.children   = new TreeSet<>();
	}//BranchExpediente null constructor

	public BranchExpediente( BaseExpediente expediente, Set<BaseExpediente> children)
	{
		super();

		if ( expediente == null )
			throw new IllegalArgumentException("Expediente asociado a la rama no puede ser nulo");

		this.expediente = expediente;
		this.children   = (children == null? new TreeSet<>(): children);

	}//BranchExpediente constructor

	// -------------- Getters & Setters ----------------

	public BaseExpediente       getExpediente() { return expediente;}
	public void                 setExpediente(BaseExpediente expediente){ this.expediente = expediente;}

	public Set<BaseExpediente>  getChildren() { return children;}
	public void                 setChildren( Set<BaseExpediente> children) { this.children = children;}

	public  String              getExpedienteCode() { return expediente.getExpedienteCode();}

	// --------------------------- Implements HierarchicalEntity ---------------------------------------

	@Override public String            getName()           { return expediente.getName();}

	@Override public String            getCode()           { return expediente.getCode();}

	@Override public BranchExpediente  getOwner()          { return expediente.getOwner();}

	@Override public String            formatCode()        { return expediente.formatCode();}

	// -----------------  Implements NeedsProtection ----------------

	public Integer                   getCategory()                           {return expediente.getCategory();}
	public void                      setCategory(Integer category)           {expediente.setCategory(category);}

	public User                getUserOwner()                          {return expediente.getUserOwner();}
	public void                      setUserOwner(User userOwner)      {expediente.setUserOwner(userOwner);}

	public Role                      getRoleOwner()                          {return expediente.getRoleOwner();}
	public void                      setRoleOwner(Role roleOwner)            {expediente.setRoleOwner(roleOwner);}

	public UserGroup                 getRestrictedTo()                       {return expediente.getRestrictedTo();}
	public void                      setRestrictedTo(UserGroup restrictedTo) {expediente.setRestrictedTo(restrictedTo);}

	@Override public ObjectToProtect getObjectToProtect()                    { return expediente.getObjectToProtect();}

	@Override public boolean         canBeAccessedBy(Integer userCategory)   { return expediente.canBeAccessedBy(userCategory);}

	@Override public boolean         isOwnedBy( User user)             { return expediente.isOwnedBy(user);}

	@Override public boolean         isOwnedBy( Role role)                   { return expediente.isOwnedBy(role);}

	@Override public boolean         isRestrictedTo( UserGroup userGroup)    { return expediente.isRestrictedTo(userGroup);}

	@Override public boolean         admits( Role role)                      { return expediente.admits(role);}

	@Override public void            grant( Permission  permission)          { expediente.grant(permission);}

	@Override public void            revoke(Permission permission)           { expediente.revoke(permission);}

	// --------------- Object methods ---------------------

	@Override public boolean equals( Object o)
	{
		if (this == o)
			return true;

		if (!(o instanceof BranchExpediente ))
			return false;

		BranchExpediente that = (BranchExpediente) o;
		return this.id != null && this.id.equals(that.id);

	}//equals

	@Override public int hashCode() { return id == null? 490277: id.hashCode();}

	@Override public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append( "BranchExpediente{")
		 .append( super.toString())
		 .append( "expediente["+ expediente.getCode()+ "]\n children[");

		for ( BaseExpediente child: children )
			s.append( child.toString()+ "\n");

		s.append("]\n     }\n");

		return s.toString();
	}//toString

	@Override  public int compareTo(BranchExpediente that) { return that == null? 1: expediente.compareTo(that.getExpediente());}

	// --------------- Logic ------------------------------

	public void openExpediente()
	{
		if ( !expediente.isOpen())
		{
			expediente.openExpediente();
			for( BaseExpediente child: children)
				child.openExpediente();
		}
	}//openExpediente


	public void closeExpediente()
	{
		if( expediente.isOpen())
		{
			expediente.closeExpediente();
			for( BaseExpediente child: children)
				child.closeExpediente();
		}
	}//closeExpediente


	public boolean addChild(BaseExpediente child)
	{
		return children.add(child);
	}//addChild

	public Iterator<BaseExpediente> childIterator()
	{
		return children.iterator();
	}//childIterator


}//BranchExpediente
