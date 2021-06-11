package com.f.thoth.ui.views.security.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.service.PermissionService;
import com.f.thoth.backend.service.RoleService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = Constant.PAGE_PERMISOS_ACCESO, layout = MainView.class)
@PageTitle(Constant.TITLE_PERMISOS_ACCESO)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class AccessPermissionView extends AbstractPermissionView<Classification>
{
   @Autowired
   public AccessPermissionView( PermissionService<Classification> service, RoleService roleService, CurrentUser currentUser)
   {
      super(Classification.class, service, roleService, currentUser, Constant.TITLE_PERMISOS_ACCESO);
   }
   
   @Override 
   protected String getBasePage() { return Constant.PAGE_PERMISOS_ACCESO;}


}//AccessPermitView
