package com.f.thoth.ui.views.admin.users;

import static com.f.thoth.ui.utils.Constant.PAGE_USER_GROUPS;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.Parm;
import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.backend.service.UserGroupService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.crud.AbstractEvidentiaCrudView;
import com.f.thoth.ui.crud.CrudEntityPresenter;
import com.f.thoth.ui.utils.Constant;
import com.f.thoth.ui.utils.converters.LocalDateToLocalDate;
import com.f.thoth.ui.utils.converters.StringToString;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_USER_GROUPS, layout = MainView.class)
@PageTitle(Constant.TITLE_USER_GROUPS)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class UserGroupView extends AbstractEvidentiaCrudView<UserGroup>
{
   private static final Converter<LocalDate, LocalDate> DATE_CONVERTER   = new LocalDateToLocalDate();
   private static final Converter<String, String>       STRING_CONVERTER = new StringToString("");
   private static final Converter<String, Integer>    CATEGORY_CONVERTER =
                        new StringToIntegerConverter( Parm.DEFAULT_CATEGORY, "Número inválido");

   private static HierarchicalSelector<UserGroup, HasValue.ValueChangeEvent<UserGroup>> parentGroup;


   @Autowired
   public UserGroupView(UserGroupService service, CurrentUser currentUser)
   {
      super(UserGroup.class, service, new Grid<>(), createForm(service), currentUser);
   }

   @Override
   protected void setupGrid(Grid<UserGroup> grid)
   {
      grid.addColumn(group -> group.getName().toLowerCase()).setHeader("Grupo").setFlexGrow(60);
      grid.addColumn(group -> group.isLocked() ? "SI" : "--").setHeader("Bloqueado?").setFlexGrow(10);
      grid.addColumn(group -> group.getCategory() == null? "0" : group.getCategory().toString()).setHeader("Categoría").setFlexGrow(30);
      grid.addColumn(UserGroup::getFromDate).setHeader("Fecha Desde").setFlexGrow(50);
      grid.addColumn(UserGroup::getToDate).setHeader("Fecha Hasta").setFlexGrow(50);
      grid.addColumn(group -> group.getOwner()== null? "---" : group.getOwner().getName()).setHeader("Grupo padre").setFlexGrow(100);

   }//setupGrid

   @Override
   protected String getBasePage() { return PAGE_USER_GROUPS; }

   private static BinderCrudEditor<UserGroup> createForm(UserGroupService service)
   {
      TextField name = new TextField("Nombre");
      name.setRequired(true);
      name.setValue("--nombre--");
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "2");

      Checkbox   blocked    = new Checkbox("Bloqueado?");
      blocked.setRequiredIndicatorVisible(true);
      blocked.setValue(false);
      blocked.getElement().setAttribute("colspan", "1");

      TextField category = new TextField("Categoría");
      category.setRequired(true);
      category.setValue(Parm.DEFAULT_CATEGORY.toString());
      category.setRequiredIndicatorVisible(true);
      category.getElement().setAttribute("colspan", "1");

      LocalDate now = LocalDate.now();
      LocalDate yearStart =now.minusDays(now.getDayOfYear());
      DatePicker fromDate = new DatePicker("Fecha desde");
      fromDate.setRequired(true);
      fromDate.setValue(yearStart);
      fromDate.setRequiredIndicatorVisible(true);
      fromDate.getElement().setAttribute("colspan", "2");

      DatePicker toDate   = new DatePicker("Fecha hasta");
      toDate.setRequired(true);
      toDate.setValue(yearStart.plusYears(1));
      toDate.setRequiredIndicatorVisible(true);
      toDate.getElement().setAttribute("colspan", "2");

      parentGroup = new HierarchicalSelector<>(service, Grid.SelectionMode.SINGLE, "Grupo padre", false, false, null);
      parentGroup.getElement().setAttribute("colspan", "4");

      FormLayout form = new FormLayout(name, blocked, category, fromDate, toDate, parentGroup);
      form.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4));

      BeanValidationBinder<UserGroup> binder = new BeanValidationBinder<>(UserGroup.class);

      binder.forField(name)
            .withConverter(STRING_CONVERTER)
            .withValidator(text -> TextUtil.isAlphaNumeric(text), "El nombre debe ser alfanumérico")
            .bind("name");

      binder.bind(blocked, "locked");
      binder.forField(category)
            .withValidator(text -> text.length() == 1, "Categorías solo tienen un dígito") //Validación del texto
            .withConverter(CATEGORY_CONVERTER)
            .withValidator(cat -> cat >= Parm.MIN_CATEGORY && cat <= Parm.MAX_CATEGORY,
                 "La categoría debe estar entre "+ Parm.MIN_CATEGORY+ " y "+ Parm.MAX_CATEGORY) // Validación del número
            .bind("category");

      binder.forField(fromDate)
            .withConverter(DATE_CONVERTER)
            .withValidator( date -> date.compareTo(LocalDate.now()) <= 0, "Fecha desde no puede ser futura")
            .bind("fromDate");

      binder.forField(toDate)
            .withConverter(DATE_CONVERTER)
            .withValidator( date -> date.compareTo(LocalDate.now()) > 0, "Fecha hasta debe ser futura")
            .bind("toDate");

      binder.forField(parentGroup)
            .bind("owner");

      return new BinderCrudEditor<UserGroup>(binder, form);

   }//createForm

   protected void setupCrudEventListeners(CrudEntityPresenter<UserGroup> entityPresenter)
   {
       Consumer<UserGroup> onSuccess = entity -> navigateToEntity(null);
       Consumer<UserGroup> onFail = entity -> {
           throw new RuntimeException("La operación no pudo ser ejecutada.");
       };

       addEditListener(e ->  entityPresenter.loadEntity(e.getItem().getId(), entity -> navigateToEntity(entity.getId().toString())));
       addCancelListener(e -> navigateToEntity(null));
       addSaveListener(e ->
       {
          UserGroup newGroup = e.getItem();
          entityPresenter.save(newGroup, onSuccess, onFail);
          parentGroup.refresh();
       });
       addDeleteListener(e -> entityPresenter.delete(e.getItem(), onSuccess, onFail));
   }//setupCrudEventListeners


}//UserGroupView
