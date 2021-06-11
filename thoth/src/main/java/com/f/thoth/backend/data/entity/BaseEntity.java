package com.f.thoth.backend.data.entity;

import static com.f.thoth.Parm.TENANT;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.security.Tenant;
import com.vaadin.flow.server.VaadinSession;

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
    VaadinSession vSession = VaadinSession.getCurrent();
    this.tenant = vSession == null? null: (Tenant)vSession.getAttribute(TENANT);
  }//BaseEntity
  
  
  public BaseEntity( Tenant tenant)
  {
     super();
     this.tenant = tenant;
  }//BaseEntity
  

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
     return super.toString()+
            " tenant["+ (tenant==null?"[tenant]": tenant.getName())+ "]"+
            " code["+ (code==null? "---": code)+ "]";
  }

}//BaseEntity
