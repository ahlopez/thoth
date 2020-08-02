package com.f.thoth.ui.views.admin.objects;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;
import static com.f.thoth.ui.utils.BakeryConst.PAGE_OBJECT_TO_PROTECT;
import static com.f.thoth.ui.utils.BakeryConst.TENANT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.service.ObjectToProtectService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractBakeryCrudView;
import com.f.thoth.ui.utils.BakeryConst;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = PAGE_OBJECT_TO_PROTECT, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_OBJECT_TO_PROTECT)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class ObjectToProtectView extends AbstractBakeryCrudView<ObjectToProtect>
{
	@Autowired
	public ObjectToProtectView(ObjectToProtectService service, CurrentUser currentUser) {
		super(ObjectToProtect.class, service, new Grid<>(), createForm(), currentUser);
	}

	@Override
	protected void setupGrid(Grid<ObjectToProtect> grid)
	{
		grid.addColumn(object -> object.getName()).setHeader("LLave").setFlexGrow(30);
		grid.addColumn(object -> object.getCategory() == null? "0" : object.getCategory().toString()).setHeader("Categoría").setFlexGrow(8);
 		grid.addColumn(object -> object.getUserOwner()== null? "-NINGUNO-" : object.getUserOwner().getFullName()).setHeader("Usuario dueño").setFlexGrow(15);
  		grid.addColumn(object -> object.getRoleOwner()== null? "-NINGUNO-" : object.getRoleOwner().getName()).setHeader("Rol dueño").setFlexGrow(15);

	}//setupGrid

	@Override
	protected String getBasePage() {
		return PAGE_OBJECT_TO_PROTECT;
	}

	private static BinderCrudEditor<ObjectToProtect> createForm() 
	{
		TextField name = new TextField("LLave del objeto");
		name.setRequired(true);
		name.setRequiredIndicatorVisible(true);
		name.getElement().setAttribute("colspan", "10");
		
		IntegerField category = new IntegerField("Categoría");
		category.setValue( new Integer(0));
		category.setRequiredIndicatorVisible(true);
		category.getElement().setAttribute("colspan", "2");

		//TextField userOwner = new TextField("Usuario dueño");
		//userOwner.setItemLabelGenerator(s -> s != null ? s.getFullName() : "-NINGUNO-");
		ComboBox<SingleUser> userOwner = new ComboBox<>();
		userOwner.setLabel("Usuario dueño");      
		userOwner.getElement().setAttribute("colspan", "4");
		userOwner.setRequired(false);
		userOwner.setRequiredIndicatorVisible(false);
		userOwner.setClearButtonVisible(true);
		userOwner.setAllowCustomValue(true);
		userOwner.setPageSize(20);
		
		ComboBox<Role> roleOwner = new ComboBox<>();
		roleOwner.getElement().setAttribute("colspan", "4");
		roleOwner.setLabel("Rol dueño");      
		roleOwner.setDataProvider(getTenantRoles());
		roleOwner.setItemLabelGenerator(createItemLabelGenerator(Role::getName));
		roleOwner.setRequired(false);
		roleOwner.setRequiredIndicatorVisible(false);
		roleOwner.setClearButtonVisible(true);
		roleOwner.setPageSize(20);

		FormLayout form = new FormLayout( name, category, userOwner, roleOwner);

		BeanValidationBinder<ObjectToProtect> binder = new BeanValidationBinder<>(ObjectToProtect.class);

		//ListDataProvider<String> roleProvider = DataProvider.ofCollection(roles());
		//

		binder.bind(name,      "name");
		binder.bind(category,  "category");
		binder.bind(userOwner, "userOwner");
		binder.bind(roleOwner, "roleOwner");


		return new BinderCrudEditor<ObjectToProtect>(binder, form);
	}//BinderCrudEditor

	private static ListDataProvider<Role> getTenantRoles()
	{
		VaadinSession currentSession = VaadinSession.getCurrent();
		Tenant tenant = (Tenant)currentSession.getAttribute(TENANT);
		return new ListDataProvider<Role>( tenant.getRoles());
	}//getTenantRoles

}//ObjectToProtectView
