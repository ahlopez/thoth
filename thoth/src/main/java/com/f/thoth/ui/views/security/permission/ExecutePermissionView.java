package com.f.thoth.ui.views.security.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.security.Operation;
import com.f.thoth.backend.service.PermissionService;
import com.f.thoth.backend.service.RoleService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = Constant.PAGE_PERMISOS_EJECUCION, layout = MainView.class)
@PageTitle(Constant.TITLE_PERMISOS_EJECUCION)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class ExecutePermissionView extends AbstractPermissionView<Operation>
{
   @Autowired
   public ExecutePermissionView( PermissionService<Operation> service, RoleService roleService, CurrentUser currentUser)
   {
      super(Operation.class, service, roleService, currentUser, Constant.TITLE_PERMISOS_EJECUCION);
   }
   
   @Override 
   protected String getBasePage() { return Constant.PAGE_PERMISOS_EJECUCION;}

}//ExecutePermitView
