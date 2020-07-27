package com.f.thoth.backend.data.security;

import java.util.Map;
import java.util.TreeMap;

import com.vaadin.flow.server.VaadinSession;

public class ThothSession
{
   private static Map<String,Tenant> tenants = new TreeMap<>();

   public ThothSession()
   {
   }

   public static Tenant getCurrentTenant()
   {
      VaadinSession session = VaadinSession.getCurrent();
      String       tenantId = (String)session.getAttribute("tenant");
      return   tenants.get( tenantId);
   }//getTenant

   public static Usuario getCurrentUser()
   {
      Tenant       tenant   = getCurrentTenant();
      VaadinSession session = VaadinSession.getCurrent();
      String       userId   = (String)session.getAttribute("user");
      return       tenant.getUserById( userId);
   }//getCurrentUser
}//ThothSession
