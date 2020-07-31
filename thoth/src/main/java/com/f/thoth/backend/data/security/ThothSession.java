package com.f.thoth.backend.data.security;

import static com.f.thoth.ui.utils.BakeryConst.TENANT;

import com.vaadin.flow.server.VaadinSession;

public class ThothSession
{
   public ThothSession()
   {
   }

   public static Tenant getCurrentTenant()
   {
      VaadinSession session = VaadinSession.getCurrent();
      return  (Tenant)session.getAttribute(TENANT);
   }//getCurrentTenant

   public static SingleUser getCurrentUser()
   {
      Tenant       tenant   = getCurrentTenant();
      VaadinSession session = VaadinSession.getCurrent();
      String       userId   = (String)session.getAttribute("user");
      return       tenant.getSingleUserById( userId);
   }//getCurrentUser
   
}//ThothSession
