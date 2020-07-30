package com.f.thoth.ui.views.admin.objects;

import static com.f.thoth.ui.utils.BakeryConst.PAGE_OBJECT_TO_PROTECT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.service.ObjectToProtectService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractBakeryCrudView;
import com.f.thoth.ui.utils.BakeryConst;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_OBJECT_TO_PROTECT, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_OBJECT_TO_PROTECT)
@Secured(Role.ADMIN)
public class ObjectToProtectView extends AbstractBakeryCrudView<ObjectToProtect>
{
   @Autowired
   public ObjectToProtectView(ObjectToProtectService service, CurrentUser currentUser) {
      super(ObjectToProtect.class, service, new Grid<>(), createForm(), currentUser);
   }

   @Override
   protected void setupGrid(Grid<ObjectToProtect> grid)
   {
      grid.addColumn(ObjectToProtect::getName).setHeader("LLave").setFlexGrow(30);
      grid.addColumn(ObjectToProtect::getCategory).setHeader("Categoría").setFlexGrow(8);
      grid.addColumn(ObjectToProtect::getUserOwner).setHeader("Usuario dueño").setFlexGrow(15);
      grid.addColumn(ObjectToProtect::getRoleOwner).setHeader("Rol dueño").setFlexGrow(15);

   }//setupGrid

   @Override
   protected String getBasePage() {
      return PAGE_OBJECT_TO_PROTECT;
   }

   private static BinderCrudEditor<ObjectToProtect> createForm() {
      TextField name = new TextField("LLave del objeto");
      name.getElement().setAttribute("colspan", "4");
      IntegerField category = new IntegerField("Categoría");
      category.setValue( new Integer(0));
      category.getElement().setAttribute("colspan", "4");
      
      TextField userOwner = new TextField("Usuario dueño");
      userOwner.getElement().setAttribute("colspan", "2");
      TextField roleOwner = new TextField("Rol dueño");
      roleOwner.getElement().setAttribute("colspan", "2");
      

      FormLayout form = new FormLayout(name, category, userOwner, roleOwner);

      BeanValidationBinder<ObjectToProtect> binder = new BeanValidationBinder<>(ObjectToProtect.class);

      binder.bind(name, "name");
      binder.bind(category, "category");
      binder.bind(userOwner, "userOwner");
      binder.bind(roleOwner, "roleOwner");


      return new BinderCrudEditor<ObjectToProtect>(binder, form);
   }//BinderCrudEditor

}//ObjectToProtectView
