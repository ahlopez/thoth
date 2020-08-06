package com.f.thoth.backend.data.entity;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;

@MappedSuperclass
public abstract class BaseEntity extends AbstractEntity
{
	@ManyToOne
	@NotNull (message = "{evidentia.tenant.required}")
	protected Tenant tenant;
	
	public BaseEntity()
	{
		super();
		this.tenant = ThothSession.getCurrentTenant();
	}

	public Tenant  getTenant() { return tenant;}
	public void    setTenant( Tenant tenant) { this.tenant = tenant;}  

	@Override public boolean equals( Object other)
	{
		return super.equals(other);
	}

	@Override public int hashCode() { return super.hashCode();}

	@Override public String toString() { return super.toString()+ " tenant["+ tenant.getName()+ "]";}

}//BaseEntity
