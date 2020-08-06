package com.f.thoth.backend.data.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import static com.f.thoth.ui.utils.BakeryConst.TENANT;
import com.f.thoth.backend.service.TenantService;
import com.vaadin.flow.server.VaadinSession;

public class ThothSession
{
	private static TenantService tenantService;	

	private static Tenant tenant; 
	public  static Tenant getTenant() { return tenant;}
	public  static void   setTenant(Tenant currentTenant) { tenant = currentTenant;}

	public ThothSession( @Autowired TenantService tService)
	{
		if (tService == null)
			throw new IllegalArgumentException("Servicio de Tenant no puede ser nulo");

		tenantService = tService;
	}//ThothSession

	public static Tenant getCurrentTenant()
	{
		Tenant t = null;
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null)
			t = (Tenant)session.getAttribute(TENANT);			
		 	
		return t == null? tenant: t;
	}//getCurrentTenant

	public static SingleUser getCurrentUser()
	{
		Tenant       tenant   = getCurrentTenant();
		VaadinSession session = VaadinSession.getCurrent();
		String       userId   = (String)session.getAttribute("user");
		return       tenant.getSingleUserById( userId);
	}//getCurrentUser
	
	public static void updateSession()
	{
		if ( tenant != null && tenantService != null)
		{
			Optional<Tenant> currentTenant = tenantService.findById( tenant.getId());
			if ( currentTenant.isPresent())
			{
				tenant = currentTenant.get();
				VaadinSession session = VaadinSession.getCurrent();
				if ( session != null)
					session.setAttribute(TENANT, tenant);
			}
		}
	}//updateSession

}//ThothSession
