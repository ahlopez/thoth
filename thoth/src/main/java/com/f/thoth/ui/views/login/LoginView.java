package com.f.thoth.ui.views.login;

import static com.f.thoth.ui.utils.BakeryConst.TENANT;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.app.security.SecurityUtils;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.TenantRepository;
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
	@Autowired TenantRepository tenantRepository;

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
	  VaadinSession session = VaadinSession.getCurrent();
	  session.setAttribute(TENANT, ThothSession.getCurrentTenant());//TODO: Salvar aquí también el currrent user
      setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
   }//afterNavigation


}//LoginView
