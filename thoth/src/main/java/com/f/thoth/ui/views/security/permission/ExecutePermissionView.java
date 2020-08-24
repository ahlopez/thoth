package com.f.thoth.ui.views.security.permission;

import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.service.PermissionService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = Constant.PAGE_PERMISOS_EJECUCION, layout = MainView.class)
@PageTitle(Constant.TITLE_PERMISOS_EJECUCION)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class ExecutePermissionView extends AbstractPermissionView<ObjectToProtect>
{
   public ExecutePermissionView( PermissionService<ObjectToProtect> service, CurrentUser currentUser)
   {
      super(ObjectToProtect.class, service, currentUser, Constant.TITLE_PERMISOS_EJECUCION);
   }
   
   @Override 
   protected String getBasePage() { return Constant.PAGE_PERMISOS_EJECUCION;}

}//ExecutePermitView
