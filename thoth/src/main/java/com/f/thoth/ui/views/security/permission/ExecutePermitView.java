package com.f.thoth.ui.views.security.permission;

import org.springframework.security.access.annotation.Secured;

import com.f.thoth.ui.MainView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = Constant.PAGE_PERMISOS_EJECUCION, layout = MainView.class)
@PageTitle(Constant.TITLE_PERMISOS_EJECUCION)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class ExecutePermitView
{


}//ExecutePermitView
