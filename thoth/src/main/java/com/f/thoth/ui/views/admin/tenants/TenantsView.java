package com.f.thoth.ui.views.admin.tenants;

import static com.f.thoth.ui.utils.BakeryConst.PAGE_TENANTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.service.TenantService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractBakeryCrudView;
import com.f.thoth.ui.utils.BakeryConst;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_TENANTS, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_TENANTS)
@Secured(Role.ADMIN)
public class TenantsView extends AbstractBakeryCrudView<Tenant>
{

   @Autowired
   public TenantsView(TenantService service, CurrentUser currentUser) {
      super(Tenant.class, service, new Grid<>(), createForm(), currentUser);
   }

   @Override
   protected void setupGrid(Grid<Tenant> grid) {
      grid.addColumn(Tenant::getName).setHeader("Cliente").setFlexGrow(10);
      grid.addColumn(Tenant::getAdministrator).setHeader("Administrador").setFlexGrow(10);
   }

   @Override
   protected String getBasePage() {
      return PAGE_TENANTS;
   }

   private static BinderCrudEditor<Tenant> createForm() {
      TextField name = new TextField("Nombre cliente");
      name.getElement().setAttribute("colspan", "2");
      TextField administrator = new TextField("Administrador");
      name.getElement().setAttribute("colspan", "2");

      FormLayout form = new FormLayout(name, administrator);

      BeanValidationBinder<Tenant> binder = new BeanValidationBinder<>(Tenant.class);

      binder.bind(name, "name");
      binder.bind(administrator, "administrator");


      return new BinderCrudEditor<Tenant>(binder, form);
   }//BinderCrudEditor

}//TenantsView
