package com.f.thoth.ui.views.admin.roles;

import static com.f.thoth.ui.utils.Constant.PAGE_ROLES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.service.RoleService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractBakeryCrudView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_ROLES, layout = MainView.class)
@PageTitle(Constant.TITLE_ROLES)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class RoleView extends AbstractBakeryCrudView<Role>
{
   @Autowired
   public RoleView(RoleService service, CurrentUser currentUser) 
   {
      super(Role.class, service, new Grid<>(), createForm(), currentUser);
   }

   @Override
   protected void setupGrid(Grid<Role> grid)
   {
      grid.addColumn(Role::getName).setHeader("Identificador del rol").setFlexGrow(20);
   }//setupGrid

   @Override
   protected String getBasePage() {
      return PAGE_ROLES;
   }

   private static BinderCrudEditor<Role> createForm() 
   {
      TextField name = new TextField("Nombre del rol");
      name.getElement().setAttribute("colspan", "3");

      FormLayout form = new FormLayout(name);

      BeanValidationBinder<Role> binder = new BeanValidationBinder<>(Role.class);

      binder.bind(name, "name");


      return new BinderCrudEditor<Role>(binder, form);
   }//BinderCrudEditor

}//RoleView
