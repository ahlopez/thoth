package com.f.thoth.backend.data.security;

import static com.f.thoth.Parm.TENANT;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.backend.service.TenantService;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.VaadinSession;

public class ThothSession
{
   private static TenantService tenantService;	

   private static Tenant tenant; 
   public  static Tenant getTenant() { return tenant;}
   public  static void   setTenant(Tenant currentTenant) { tenant = currentTenant;}
   
   private static User   user;
   public  static User   getUser(){ return user;   }
   public  static void   setUser(User currentUser) { user = currentUser; }
   

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

   public static User getCurrentUser()
   {
      User u  = null;
      VaadinSession session = VaadinSession.getCurrent();
      if ( session != null)
         u = (User)session.getAttribute("user");
       
      return  u  == null? user: u;
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


   public static ListDataProvider<Role> getTenantRoles()
   {
      Tenant tenant = getCurrentTenant();
      return new ListDataProvider<Role>( tenant.getRoles());
   }//getTenantRoles


}//ThothSession
