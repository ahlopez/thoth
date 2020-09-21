package com.f.thoth.ui.views.admin.users;

import static com.f.thoth.ui.utils.Constant.PAGE_SINGLE_USERS;
import static com.f.thoth.ui.utils.Constant.TITLE_SINGLE_USERS;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.backend.service.RoleService;
import com.f.thoth.backend.service.SingleUserService;
import com.f.thoth.backend.service.UserGroupService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.crud.AbstractEvidentiaCrudView;
import com.f.thoth.ui.crud.CrudEntityPresenter;
import com.f.thoth.ui.utils.Constant;
import com.f.thoth.ui.utils.converters.LocalDateToLocalDate;
import com.f.thoth.ui.utils.converters.StringToString;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_SINGLE_USERS, layout = MainView.class)
@PageTitle(TITLE_SINGLE_USERS)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class SingleUserView extends AbstractEvidentiaCrudView<SingleUser>
{
   private static final Converter<LocalDate, LocalDate> DATE_CONVERTER   = new LocalDateToLocalDate();
   private static final Converter<String, String>       STRING_CONVERTER = new StringToString("");
   private static final Converter<String, Integer>    CATEGORY_CONVERTER =
                    new StringToIntegerConverter( Constant.DEFAULT_CATEGORY, "Categoría inválida");

   private static SingleUser       singleUser;
   
   private static Button           groups = new Button("Grupos");
   private static Dialog           groupsDialog;
   private static Set<UserGroup>   userGroups = new TreeSet<>();
   private static UserGroupService userGService;
   private static HierarchicalSelector<UserGroup,HasValue.ValueChangeEvent<UserGroup>> groupsSelector;
   
   private static Button roles  = new Button("Roles");
   private static Dialog           rolesDialog;
   private static Set<Role>        userRoles = new TreeSet<>();
   private static RoleService      roleSvice;
   private static Grid<Role>       rolesSelector;


   /*
   private static ComboBox<UserGroup> groupsItBelongs;
   private static ComboBox<Role>      rolesItBelongs;
    */


   @Autowired
   public SingleUserView(SingleUserService userGService, UserGroupService userGroupService, RoleService roleService, CurrentUser currentUser)
   {
      super(SingleUser.class, userGService, new Grid<>(), createForm(userGroupService, roleService), currentUser);
   }

   @Override
   protected void setupGrid(Grid<SingleUser> grid)
   {
      grid.addColumn(user -> user.getName().toLowerCase()).setHeader("Nombre").setFlexGrow(13);
      grid.addColumn(user -> user.getLastName().toLowerCase()).setHeader("Apellido").setFlexGrow(13);
      grid.addColumn(user -> user.getEmail().toLowerCase()).setHeader("Correo").setFlexGrow(15);
      grid.addColumn(user -> user.isLocked() ? "SI" : "--").setHeader("Bloqueado?").setFlexGrow(8);
      grid.addColumn(user -> user.getCategory() == null? "0" : user.getCategory().toString()).setHeader("Categoría").setFlexGrow(8);
      grid.addColumn(SingleUser::getFromDate).setHeader("Fecha Desde").setFlexGrow(6);
      grid.addColumn(SingleUser::getToDate).setHeader("Fecha Hasta").setFlexGrow(6);

   }//setupGrid

   @Override
   protected String getBasePage() { return PAGE_SINGLE_USERS;}

   private static BinderCrudEditor<SingleUser> createForm(UserGroupService userGroupService, RoleService roleService)
   {
      userGService = userGroupService;
      roleSvice    = roleService;
      TextField name = new TextField("Nombre");
      name.setRequired(true);
      name.setPlaceholder("--nombre--");
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "3");

      TextField lastName = new TextField("Apellido");
      lastName.setRequired(true);
      lastName.setPlaceholder("--apellido--");
      lastName.setRequiredIndicatorVisible(true);
      lastName.getElement().setAttribute("colspan", "3");

      PasswordField password = new PasswordField("Palabra Clave");
      password.setRequired(true);
      password.setRequiredIndicatorVisible(true);
      password.getElement().setAttribute("colspan", "3");

      EmailField email = new EmailField("Correo electrónico");
      email.setPlaceholder("--dirección de correo--");
      email.getElement().setAttribute("required", true);
      email.setRequiredIndicatorVisible(true);
      email.getElement().setAttribute("colspan", "3");
      email.setClearButtonVisible(true);
      email.setErrorMessage("Por favor ingrese una dirección válida de correo");


      Checkbox   blocked    = new Checkbox("Bloqueado?");
      blocked.setRequiredIndicatorVisible(true);
      blocked.setValue(false);
      blocked.getElement().setAttribute("colspan", "1");

      TextField category = new TextField("Categoría");
      category.setRequired(true);
      category.setValue(Constant.DEFAULT_CATEGORY.toString());
      category.setRequiredIndicatorVisible(true);
      category.getElement().setAttribute("colspan", "1");

      LocalDate now = LocalDate.now();
      LocalDate yearStart =now.minusDays(now.getDayOfYear());
      DatePicker fromDate = new DatePicker("Desde");
      fromDate.setRequired(true);
      fromDate.setValue(yearStart);
      fromDate.setRequiredIndicatorVisible(true);
      fromDate.getElement().setAttribute("colspan", "2");

      DatePicker toDate   = new DatePicker("Hasta");
      toDate.setRequired(true);
      toDate.setValue(yearStart.plusYears(1));
      toDate.setRequiredIndicatorVisible(true);
      toDate.getElement().setAttribute("colspan", "2");
      
      Component buttonsComponent = createButtons();

      FormLayout form = new FormLayout(name, lastName, password, email,  blocked, category, fromDate, toDate, buttonsComponent);

      BeanValidationBinder<SingleUser> binder = new BeanValidationBinder<>(SingleUser.class);

      binder.forField(name)
            .withConverter(STRING_CONVERTER)
            .withValidator(text -> TextUtil.isAlphaNumeric(text), "El nombre debe ser alfanumérico")
            .bind("name");

      binder.forField(lastName)
            .withConverter(STRING_CONVERTER)
            .withValidator(text -> TextUtil.isAlphaNumeric(text), "El apellido debe ser alfanumérico")
            .bind("lastName");

      binder.forField(password)
            .withConverter(STRING_CONVERTER)
            .bind("passwordHash");

      binder.forField(email)
            .withConverter(STRING_CONVERTER)
            .withValidator(new EmailValidator("Ingrese un correo electrónico válido"))
            .bind("email");

      binder.bind(blocked, "locked");
      binder.forField(category)
            .withValidator(text -> text.length() == 1, "Categorías solo tienen un dígito") //Validación del texto
            .withConverter(CATEGORY_CONVERTER)
            .withValidator(cat -> cat >= Constant.MIN_CATEGORY && cat <= Constant.MAX_CATEGORY,
                  "La categoría debe estar entre "+ Constant.MIN_CATEGORY+ " y "+ Constant.MAX_CATEGORY) // Validación del número
            .bind("category");

      binder.forField(fromDate)
            .withConverter(DATE_CONVERTER)
            .withValidator( date -> date.compareTo(LocalDate.now()) <= 0, "Fecha desde no puede ser futura")
            .bind("fromDate");

      binder.forField(toDate)
            .withConverter(DATE_CONVERTER)
            .withValidator( date -> date.compareTo(LocalDate.now()) > 0, "Fecha hasta debe ser futura")
            .bind("toDate");
      
      groupsDialog = createGroupsSelector(userGService);
      rolesDialog  = createRolesSelector(roleSvice);
      singleUser = binder.getBean();

      return new BinderCrudEditor<SingleUser>(binder, form);
   }//BinderCrudEditor


   protected void setupCrudEventListeners(CrudEntityPresenter<SingleUser> entityPresenter)
   {
      Consumer<SingleUser> onSuccess = entity -> navigateToEntity(null);
      Consumer<SingleUser> onFail = entity -> {
         throw new RuntimeException("La operación no pudo ser ejecutada.");
      };

      addEditListener(e ->
      {
         entityPresenter.loadEntity(e.getItem().getId(), entity -> navigateToEntity(entity.getId().toString()));
      });

      addCancelListener(e -> navigateToEntity(null));

      addSaveListener(e -> 
           {
              singleUser = e.getItem();
              singleUser.setGroups(userGroups);
              singleUser.setRoles (userRoles);
              entityPresenter.save(singleUser, onSuccess, onFail);
           });

      addDeleteListener(e -> entityPresenter.delete(e.getItem(), onSuccess, onFail));

   }//setupCrudEventListeners


   private static Component createButtons() 
   {
      groups.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      groups.addClickListener(e -> groupsDialog.open());

      roles.addThemeVariants (ButtonVariant.LUMO_PRIMARY);

      groups.addClickListener(click -> selectGroups());
      roles.addClickListener(click ->  selectRoles());

      return new HorizontalLayout(groups, roles);

   }//createButtonsLayout
   
   private static Dialog createGroupsSelector(UserGroupService userGroupService)
   {
      groupsDialog   = new Dialog();
      groupsDialog.setModal(true);
      groupsDialog.setDraggable(true);
      groupsDialog.setResizable(true);
      groupsDialog.setWidth("1000px");
      groupsDialog.setHeight("1300px");
      
      groupsSelector = new HierarchicalSelector<>( userGroupService, Grid.SelectionMode.MULTI, "Seleccione grupos", null);
      Button close = new Button("Cerrar");     
      close.addClickListener(e-> 
           {
              userGroups = groupsSelector.getValues();
              if (singleUser != null)
                  singleUser.setGroups(userGroups);
              
              groupsDialog.close();
           });
      groupsDialog.add( groupsSelector, close );

      return groupsDialog;
      
   }//createGroupsSelector

   
   private static Dialog createRolesSelector( RoleService  roleService)
   {
      rolesDialog   = new Dialog();
      rolesDialog.setModal(true);
      rolesDialog.setDraggable(true);
      rolesDialog.setResizable(true);
      rolesDialog.setWidth ("400px");
      rolesDialog.setHeight("700px");
      
      rolesSelector = new Grid<>();
      rolesSelector.addColumn(Role::getName).setHeader("Rol");
      rolesSelector.setSelectionMode(SelectionMode.MULTI);
      rolesSelector.setItems(roleService.findAll());
      
      Button close = new Button("Cerrar");     
      close.addClickListener(e-> 
           {
              userRoles = rolesSelector.asMultiSelect().getValue();
              if( singleUser != null)
                  singleUser.setRoles(userRoles);
              
              rolesDialog.close();
           });
      H3 title = new H3("Seleccione los roles");
      rolesDialog.add( title, rolesSelector, close );

      return rolesDialog;
      
   }//createRolesSelector
   
   private static void selectGroups()
   {
      groupsSelector.init(userGroups);
      groupsDialog.open();
   }//selectGroups
   
   private static void selectRoles()
   {     
      rolesSelector.deselectAll();
      userRoles = (singleUser == null? new TreeSet<>() : singleUser.getRoles());
      rolesSelector.asMultiSelect().select(userRoles);
      rolesDialog.open();
   }//selectRoles

}//SingleUserView
