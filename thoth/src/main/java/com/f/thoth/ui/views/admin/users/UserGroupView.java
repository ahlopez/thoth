package com.f.thoth.ui.views.admin.users;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;
import static com.f.thoth.ui.utils.BakeryConst.PAGE_USER_GROUPS;
import static com.f.thoth.ui.utils.BakeryConst.TENANT;

import java.time.LocalDate;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.backend.service.UserGroupService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractBakeryCrudView;
import com.f.thoth.ui.utils.BakeryConst;
import com.f.thoth.ui.utils.converters.LocalDateToLocalDate;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = PAGE_USER_GROUPS, layout = MainView.class)
@PageTitle(BakeryConst.TITLE_USER_GROUPS)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class UserGroupView extends AbstractBakeryCrudView<UserGroup>
{
   private static final Converter<String, Integer> CATEGORY_CONVERTER = 
		                 new StringToIntegerConverter( BakeryConst.DEFAULT_CATEGORY, "Número inválido");
   
   private static final Converter<LocalDate, LocalDate> DATE_CONVERTER = new LocalDateToLocalDate();
   
   @Autowired
   public UserGroupView(UserGroupService service, CurrentUser currentUser)
   {
      super(UserGroup.class, service, new Grid<>(), createForm(), currentUser);
   }

   @Override
   protected void setupGrid(Grid<UserGroup> grid)
   {
      grid.addColumn(group -> group.getFirstName().toLowerCase()).setHeader("Grupo").setFlexGrow(15);
      grid.addColumn(group -> group.isLocked() ? "SI" : "--").setHeader("Bloqueado?").setFlexGrow(8);
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

      TextField category = new TextField("Categoría");
      category.setValue(BakeryConst.DEFAULT_CATEGORY.toString());
      category.setRequiredIndicatorVisible(true);
      category.getElement().setAttribute("colspan", "1");
    
      LocalDate now = LocalDate.now();
      LocalDate yearStart =now.minusDays(now.getDayOfYear());
      DatePicker fromDate = new DatePicker("Fecha desde");
      fromDate.setValue(yearStart);
      fromDate.setRequiredIndicatorVisible(true);
      fromDate.getElement().setAttribute("colspan", "2");

      DatePicker toDate   = new DatePicker("Fecha hasta");
      toDate.setValue(yearStart.plusYears(1));
      toDate.setRequiredIndicatorVisible(true);
      toDate.getElement().setAttribute("colspan", "2");

      ComboBox<UserGroup> parentGroup = new ComboBox<>();
      parentGroup.getElement().setAttribute("colspan", "6");
      parentGroup.setLabel("Grupo Padre");
      parentGroup.setDataProvider(getTenantGroups());
      parentGroup.setItemLabelGenerator(createItemLabelGenerator(UserGroup::getFirstName));
      parentGroup.setAllowCustomValue(false);
      parentGroup.setRequired(false);
      parentGroup.setRequiredIndicatorVisible(false);
      parentGroup.setClearButtonVisible(true);
      parentGroup.setPageSize(20);

      FormLayout form = new FormLayout(name, blocked, category, fromDate, toDate, parentGroup);

      BeanValidationBinder<UserGroup> binder = new BeanValidationBinder<>(UserGroup.class);
 
      binder.forField(name)
                .withValidator(text -> TextUtil.isAlphaNumeric(text), "El nombre debe ser alfanumérico")
                .bind("firstName");
      
      binder.bind(blocked, "locked");
      binder.forField(category)      
                .withValidator(text -> text.length() == 1, "Categorías solo tienen un dígito") //Validación del texto
                .withConverter(CATEGORY_CONVERTER)
                .withValidator(cat -> cat >= BakeryConst.MIN_CATEGORY && cat <= BakeryConst.MAX_CATEGORY, 
                     "La categoría debe estar entre "+ BakeryConst.MIN_CATEGORY+ " y "+ BakeryConst.MAX_CATEGORY) // Validación del número
                .bind("category");
      
      binder.forField(fromDate)            
                .withConverter(DATE_CONVERTER)
                .withValidator( date -> date.compareTo(LocalDate.now()) <= 0, "Fecha desde no puede ser futura")
                .bind("fromDate");
      
      binder.forField(toDate)            
                .withConverter(DATE_CONVERTER)
                .withValidator( date -> date.compareTo(LocalDate.now()) > 0, "Fecha hasta debe ser futura")
                .bind("toDate");
      
      binder.bind(parentGroup, "parentGroup");

      return new BinderCrudEditor<UserGroup>(binder, form);
   }//BinderCrudEditor
   

	private static ListDataProvider<UserGroup> getTenantGroups()
	{
		VaadinSession currentSession = VaadinSession.getCurrent();
		Tenant tenant = (Tenant)currentSession.getAttribute(TENANT);
		return new ListDataProvider<UserGroup>( tenant == null? new TreeSet<>() : tenant.getUserGroups());
	}//getTenantRoles


}//UserGroupView
