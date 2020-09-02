package com.f.thoth.backend.data.entity;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;

@MappedSuperclass
public abstract class BaseEntity extends AbstractEntity
{
	@ManyToOne
	@NotNull (message = "{evidentia.tenant.required}")
	protected Tenant tenant;

   @NotNull (message = "{evidentia.code.required}")
   @NotEmpty(message = "{evidentia.code.required}")
   @Size(max = 255, message="{evidentia.code.maxlength}")
   @Column(unique = true)
   protected String code;
	
   // -------------------  Construction ------------------
	public BaseEntity()
	{
		super();
		this.tenant = ThothSession.getCurrentTenant();
	}
 
   protected abstract void buildCode();
	
	// ------------------ Getters && Setters
	public Tenant  getTenant() { return tenant;}
	public void    setTenant( Tenant tenant) { this.tenant = tenant;}  

   public void    setCode(String code) { this.code = code; }
   public String  getCode()
   {
      if ( code == null )
         buildCode();

      return code;
   }//getCode

   // ---------------- Object -----------------------
	@Override public boolean equals( Object other)
	{
		return super.equals(other);
	}

	@Override public int    hashCode() { return super.hashCode();}

	@Override public String toString() 
	{ 
	   return super.toString()+ " tenant["+ tenant==null?"[tenant]": tenant.getName()+ "] code["+ (code==null? "---": code)+ "]";
	}

}//BaseEntity
