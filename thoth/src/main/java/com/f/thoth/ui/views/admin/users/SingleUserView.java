package com.f.thoth.ui.views.admin.users;

import static com.f.thoth.ui.utils.BakeryConst.PAGE_SINGLE_USERS;
import static com.f.thoth.ui.utils.BakeryConst.TITLE_SINGLE_USERS;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.service.SingleUserService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractBakeryCrudView;
import com.f.thoth.ui.crud.CrudEntityPresenter;
import com.f.thoth.ui.utils.BakeryConst;
import com.f.thoth.ui.utils.converters.LocalDateToLocalDate;
import com.f.thoth.ui.utils.converters.StringToString;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
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
public class SingleUserView extends AbstractBakeryCrudView<SingleUser>
{
   private static final Converter<LocalDate, LocalDate> DATE_CONVERTER   = new LocalDateToLocalDate();
   private static final Converter<String, String>       STRING_CONVERTER = new StringToString("");
   private static final Converter<String, Integer>    CATEGORY_CONVERTER =
         new StringToIntegerConverter( BakeryConst.DEFAULT_CATEGORY, "Categoría inválida");


   private static Button groups = new Button("Grupos");
   private static Button roles  = new Button("Roles");


   /*
   private static ComboBox<UserGroup> groupsItBelongs;
   private static ComboBox<Role>      rolesItBelongs;
    */


   @Autowired
   public SingleUserView(SingleUserService service, CurrentUser currentUser)
   {
      super(SingleUser.class, service, new Grid<>(), createForm(), currentUser);
   }

   @Override
   protected void setupGrid(Grid<SingleUser> grid)
   {
      /*
      protected Tenant          tenant;         // Tenant the user belongs to
      protected String          name;           // User first name
      protected String          lastName;       // User last name
      protected String          email;          // user email
      protected boolean         locked;         // Is the user blocked?
      protected Integer         category;       // security category
      protected LocalDate       fromDate;       // initial date it can be used. default = now
      protected LocalDate       toDate;         // final date it can be used. default = a year from now
      protected String          passwordHash;   // user password
      protected Set<Role>       roles;          // roles assigned to it
      protected Set<UserGroup>  groups;         // groups it belongs
       */

      grid.addColumn(user -> user.getName().toLowerCase()).setHeader("Nombre").setFlexGrow(13);
      grid.addColumn(user -> user.getLastName().toLowerCase()).setHeader("Apellido").setFlexGrow(13);
      grid.addColumn(user -> user.getEmail().toLowerCase()).setHeader("Correo").setFlexGrow(15);
      grid.addColumn(user -> user.isLocked() ? "SI" : "--").setHeader("Bloqueado?").setFlexGrow(8);
      grid.addColumn(user -> user.getCategory() == null? "0" : user.getCategory().toString()).setHeader("Categoría").setFlexGrow(8);
      grid.addColumn(SingleUser::getFromDate).setHeader("Fecha Desde").setFlexGrow(6);
      grid.addColumn(SingleUser::getToDate).setHeader("Fecha Hasta").setFlexGrow(6);
      //grid.addColumn(user -> user.getParentUser()== null? "---" : user.getParentUser().getName()).setHeader("Grupo padre").setFlexGrow(30);
      //grid.addColumn(user -> user.getParentUser()== null? "---" : user.getParentUser().getName()).setHeader("Grupo padre").setFlexGrow(30);

   }//setupGrid

   @Override
   protected String getBasePage() { return PAGE_SINGLE_USERS;}

   private static BinderCrudEditor<SingleUser> createForm()
   {
      TextField name = new TextField("Nombre");
      name.setRequired(true);
      name.setValue("--nombre--");
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "3");

      TextField lastName = new TextField("Apellido");
      lastName.setRequired(true);
      lastName.setValue("--apellido--");
      lastName.setRequiredIndicatorVisible(true);
      lastName.getElement().setAttribute("colspan", "3");

      PasswordField password = new PasswordField("Palabra Clave");
      password.setRequired(true);
      password.setRequiredIndicatorVisible(true);
      password.getElement().setAttribute("colspan", "3");

      EmailField email = new EmailField("Correo electrónico");
      //email.setRequired(true);
      email.setRequiredIndicatorVisible(true);
      email.getElement().setAttribute("colspan", "3");

      Checkbox   blocked    = new Checkbox("Bloqueado?");
      blocked.setRequiredIndicatorVisible(true);
      blocked.setValue(false);
      blocked.getElement().setAttribute("colspan", "1");

      TextField category = new TextField("Categoría");
      category.setRequired(true);
      category.setValue(BakeryConst.DEFAULT_CATEGORY.toString());
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

      /*
      ComboBox<SingleUser> parentGroup = new ComboBox<>();
      parentCombo = parentGroup;
      parentGroup.getElement().setAttribute("colspan", "6");
      parentGroup.setLabel("Grupo Padre");
      parentGroup.setDataProvider(getTenantGroups());
      parentGroup.setItemLabelGenerator(createItemLabelGenerator(SingleUser::getFullName));
      parentGroup.setAllowCustomValue(false);
      parentGroup.setRequired(false);
      parentGroup.setRequiredIndicatorVisible(false);
      parentGroup.setClearButtonVisible(true);
      parentGroup.setPageSize(20);
       */
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

      //  binder.bind(parentGroup, "parentGroup");

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

      addSaveListener(e -> entityPresenter.save(e.getItem(), onSuccess, onFail));

      addDeleteListener(e -> entityPresenter.delete(e.getItem(), onSuccess, onFail));

   }//setupCrudEventListeners


   private static Component createButtons() 
   {
      groups.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      roles.addThemeVariants (ButtonVariant.LUMO_PRIMARY);

      groups.addClickListener(click -> selectGroups());
      roles.addClickListener(click ->  selectRoles());

      return new HorizontalLayout(groups, roles);

   }//createButtonsLayout
   
   private static void selectGroups()
   {
      System.out.println("))) En selectGroups");
   }
   
   private static void selectRoles()
   {     
      System.out.println("))) En selectRoles");
   }

}//SingleUserView
