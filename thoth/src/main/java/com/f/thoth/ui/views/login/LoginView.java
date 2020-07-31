package com.f.thoth.ui.views.login;

import java.time.LocalDateTime;

import com.f.thoth.app.security.SecurityUtils;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.ui.utils.BakeryConst;
import com.f.thoth.ui.views.storefront.StorefrontView;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route
@PageTitle("Evidentia")
@JsModule("./styles/shared-styles.js")
@Viewport(BakeryConst.VIEWPORT)
public class LoginView extends LoginOverlay
   implements AfterNavigationObserver, BeforeEnterObserver 
{

   public LoginView() 
   {
      LoginI18n i18n = LoginI18n.createDefault();
      i18n.setHeader(new LoginI18n.Header());
      i18n.getHeader().setTitle("Evidentia"); 
      i18n.getHeader().setDescription("Gestión Documental");
      //   "admin@vaadin.com + admin\n" + "barista@vaadin.com + barista");
      i18n.setAdditionalInformation(null);
      i18n.setForm(new LoginI18n.Form());
      i18n.getForm().setSubmit("Ingresar");
      i18n.getForm().setTitle("Identificación");
      i18n.getForm().setUsername("Correo");
      i18n.getForm().setPassword("Clave");
      setI18n(i18n);
      setForgotPasswordButtonVisible(false);
      setAction("login");
   }//LoginView

   @Override
   public void beforeEnter(BeforeEnterEvent event) 
   {
      if (SecurityUtils.isUserLoggedIn()) {
         event.forwardTo(StorefrontView.class);
      } else {
         setOpened(true);
      }
   }//beforeEnter

   @Override
   public void afterNavigation(AfterNavigationEvent event) 
   {
	  loadTenant();
      setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
   }
   
   private void loadTenant()
   {
	    //TODO: Cuando esté el mantenimiento de usuarios, borrar este y createRole
	    //       y cambiarlos por la carga del Tenant y sus roles definidos
		Tenant tenant = new Tenant();
		tenant.setName("Tenant1");
		tenant.setLocked(false);
		tenant.setAdministrator("admin@vaadin.com");
		tenant.setFromDate(LocalDateTime.MIN);
		tenant.setToDate(LocalDateTime.MAX);
		VaadinSession session = VaadinSession.getCurrent();
		session.setAttribute("TENANT", tenant);
		Role role1 = createRole(tenant, "Gerente");
		Role role2 = createRole(tenant, "Admin");
		Role role3 = createRole(tenant, "Supervisor");
		Role role4 = createRole(tenant, "Operador");
		Role role5 = createRole(tenant, "Publico");
		tenant.addRole(role1); 
		tenant.addRole(role2); 
		tenant.addRole(role3);
		tenant.addRole(role4); 
		tenant.addRole(role5);

   }//loadTenant
   
	private Role createRole( Tenant tenant, String name)
	{
		com.f.thoth.backend.data.security.Role  role = new Role();
		role.setTenant(tenant);
		role.setName(name);
		return role;
	}


}//LoginView
