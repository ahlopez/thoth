package com.f.thoth.ui.views.admin.users;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;
import static com.f.thoth.ui.utils.BakeryConst.PAGE_USER_GROUPS;
import static com.f.thoth.ui.utils.BakeryConst.TENANT;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.backend.service.UserGroupService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractBakeryCrudView;
import com.f.thoth.ui.utils.BakeryConst;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = PAGE_USER_GROUPS, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_USER_GROUPS)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class UserGroupView extends AbstractBakeryCrudView<UserGroup>
{
   private static Integer DEFAULT_CATEGORY = 1;
   
   @Autowired
   public UserGroupView(UserGroupService service, CurrentUser currentUser)
   {
      super(UserGroup.class, service, new Grid<>(), createForm(), currentUser);
   }

   @Override
   protected void setupGrid(Grid<UserGroup> grid)
   {
      grid.addColumn(UserGroup::getFirstName).setHeader("Nombre").setFlexGrow(20);
      grid.addColumn(UserGroup::isLocked).setHeader("Bloqueado").setFlexGrow(5);
      grid.addColumn(group -> group.getCategory() == null? "0" : group.getCategory().toString()).setHeader("Categoría").setFlexGrow(8);
      grid.addColumn(UserGroup::getFromDate).setHeader("Fecha Desde").setFlexGrow(10);
      grid.addColumn(UserGroup::getToDate).setHeader("Fecha Hasta").setFlexGrow(10);
      grid.addColumn(group -> group.getParentGroup()== null? "-NINGUNO-" : group.getParentGroup().getFirstName()).setHeader("Grupo padre").setFlexGrow(15);

   // protected Set<Role>       userGroups;
   // protected Set<UserGroup>  groups;

   }//setupGrid

   @Override
   protected String getBasePage() { return PAGE_USER_GROUPS; }

   private static BinderCrudEditor<UserGroup> createForm()
   {
      TextField name = new TextField("Nombre");
      name.setRequired(true);
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "6");

      Checkbox   blocked    = new Checkbox("Bloqueado?");
      blocked.setRequiredIndicatorVisible(true);
      blocked.setValue(false);
      blocked.getElement().setAttribute("colspan", "1");

      IntegerField category = new IntegerField("Categoría");
      category.setValue( DEFAULT_CATEGORY);
      category.setRequiredIndicatorVisible(true);
      category.getElement().setAttribute("colspan", "1");

      LocalDate now = LocalDate.now();
      LocalDate yearStart =now.minusDays(now.getDayOfYear());
      DateTimePicker fromDate = new DateTimePicker("Fecha desde", yearStart.atStartOfDay());
      fromDate.setRequiredIndicatorVisible(true);
      fromDate.getElement().setAttribute("colspan", "2");

      DateTimePicker toDate   = new DateTimePicker("Fecha hasta",yearStart.plusYears(1).atStartOfDay());
      toDate.setRequiredIndicatorVisible(true);
      toDate.getElement().setAttribute("colspan", "2");

      ComboBox<UserGroup> parentGroup = new ComboBox<>();
      parentGroup.getElement().setAttribute("colspan", "4");
      parentGroup.setLabel("Grupo Padre");
      parentGroup.setDataProvider(getTenantGroups());
      parentGroup.setItemLabelGenerator(createItemLabelGenerator(UserGroup::getFirstName));
      parentGroup.setRequired(false);
      parentGroup.setRequiredIndicatorVisible(false);
      parentGroup.setClearButtonVisible(true);
      parentGroup.setPageSize(20);

      FormLayout form = new FormLayout(name, blocked, category, fromDate, toDate, parentGroup);

      BeanValidationBinder<UserGroup> binder = new BeanValidationBinder<>(UserGroup.class);

      binder.bind(name,        "firstName");
      binder.bind(blocked,     "locked");
      binder.bind(category,    "category");
      binder.bind(fromDate,    "fromDate");
      binder.bind(toDate,      "toDate");
      binder.bind(parentGroup, "parentGroup");


      return new BinderCrudEditor<UserGroup>(binder, form);
   }//BinderCrudEditor
   

	private static ListDataProvider<UserGroup> getTenantGroups()
	{
		VaadinSession currentSession = VaadinSession.getCurrent();
		Tenant tenant = (Tenant)currentSession.getAttribute(TENANT);
		return new ListDataProvider<UserGroup>( tenant.getUserGroups());
	}//getTenantRoles


}//UserGroupView
