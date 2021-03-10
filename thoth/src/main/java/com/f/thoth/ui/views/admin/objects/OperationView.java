package com.f.thoth.ui.views.admin.objects;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;
import static com.f.thoth.ui.utils.Constant.PAGE_OPERATIONS;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Operation;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.OperationService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.crud.AbstractEvidentiaCrudView;
import com.f.thoth.ui.crud.CrudEntityPresenter;
import com.f.thoth.ui.utils.Constant;
import com.f.thoth.ui.utils.converters.StringToString;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_OPERATIONS, layout = MainView.class)
@PageTitle(Constant.TITLE_OPERATIONS)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class OperationView extends AbstractEvidentiaCrudView<Operation>
{
   private static final Converter<String, String>  STRING_CONVERTER  = new StringToString("");
   private static final Converter<String, Integer> CATEGORY_CONVERTER= new StringToIntegerConverter( Constant.DEFAULT_CATEGORY, "Número inválido");

   private static HierarchicalSelector<Operation, HasValue.ValueChangeEvent<Operation>> parentObject;

   @Autowired
   public OperationView(OperationService service, CurrentUser currentUser)
   {
      super(Operation.class, service, new Grid<>(), createForm(service), currentUser);
   }

   @Override
   protected void setupGrid(Grid<Operation> grid)
   {
      grid.addColumn(operation -> operation.getName()).setHeader("LLave").setFlexGrow(30);
      grid.addColumn(operation -> operation.getOwner()    == null? "---" : operation.getOwner().getName())     .setHeader("Grupo Operaciones").setFlexGrow(20);
      grid.addColumn(operation -> operation.getCategory() == null? "0"   : operation.getCategory().toString()) .setHeader("Categoría")    .setFlexGrow(5);
      grid.addColumn(operation -> operation.getUserOwner()== null? "---" : operation.getUserOwner().getEmail()).setHeader("Usuario dueño").setFlexGrow(30);
      grid.addColumn(operation -> operation.getRoleOwner()== null? "---" : operation.getRoleOwner().getName()) .setHeader("Rol dueño")    .setFlexGrow(15);

   }//setupGrid

   @Override
   protected String getBasePage() { return PAGE_OPERATIONS;}

   private static BinderCrudEditor<Operation> createForm(OperationService service)
   {
      TextField name = new TextField("Operación");
      name.setRequired(true);
      name.setPlaceholder("--llave--");
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "3");

      TextField category = new TextField("Categoría");
      category.setRequired(true);
      category.setValue(Constant.DEFAULT_CATEGORY.toString());
      category.setRequiredIndicatorVisible(true);
      category.getElement().setAttribute("colspan", "1");

      ComboBox<Role> roleOwner = new ComboBox<>();
      roleOwner.getElement().setAttribute("colspan", "2");
      roleOwner.setLabel("Rol dueño");
      roleOwner.setDataProvider(ThothSession.getTenantRoles());
      roleOwner.setItemLabelGenerator(createItemLabelGenerator(Role::getName));
      roleOwner.setRequired(false);
      roleOwner.setRequiredIndicatorVisible(false);
      roleOwner.setClearButtonVisible(true);
      roleOwner.setAllowCustomValue(true);
      roleOwner.setPageSize(20);

      ComboBox<User> userOwner = new ComboBox<>();  // TODO: Considerar el uso del HierarchicalSelector
      userOwner.setLabel("Usuario dueño");
      userOwner.getElement().setAttribute("colspan", "2");
      userOwner.setRequired(false);
      userOwner.setRequiredIndicatorVisible(false);
      userOwner.setClearButtonVisible(true);
      userOwner.setAllowCustomValue(true);
      userOwner.setPageSize(20);

      parentObject = new HierarchicalSelector<>(service, Grid.SelectionMode.SINGLE, "Objeto padre", false, false, null);
      parentObject.getElement().setAttribute("colspan", "4");

      FormLayout form = new FormLayout( name, category, userOwner, roleOwner, parentObject);
      form.setResponsiveSteps(
             new ResponsiveStep("30em", 1),
             new ResponsiveStep("30em", 2),
             new ResponsiveStep("30em", 3),
             new ResponsiveStep("30em", 4));

      BeanValidationBinder<Operation> binder = new BeanValidationBinder<>(Operation.class);

      binder.forField(name)
            .withConverter(STRING_CONVERTER)
            .withValidator(text -> TextUtil.isAlphaNumeric(text), "El nombre debe ser alfanumérico")
            .bind("name");

      binder.forField(category)
            .withValidator(text -> text.length() == 1, "Categorías solo tienen un dígito") //Validación del texto
            .withConverter(CATEGORY_CONVERTER)
            .withValidator(cat -> cat >= Constant.MIN_CATEGORY && cat <= Constant.MAX_CATEGORY,
                           "La categoría debe estar entre "+ Constant.MIN_CATEGORY+ " y "+ Constant.MAX_CATEGORY) // Validación del número
            .bind("category");

      binder.bind(userOwner, "userOwner");
      binder.bind(roleOwner, "roleOwner");

      binder.forField(parentObject)
            .bind("owner");

      return new BinderCrudEditor<Operation>(binder, form);

   }//BinderCrudEditor

   @Override protected void setupCrudEventListeners(CrudEntityPresenter<Operation> entityPresenter)
   {
      Consumer<Operation> onSuccess = entity -> navigateToEntity(null);
      Consumer<Operation> onFail = entity ->
      {
         throw new RuntimeException("La operación no pudo ser ejecutada.");
      };

      addEditListener(e ->  entityPresenter.loadEntity(e.getItem().getId(), entity -> navigateToEntity(entity.getId().toString())));
      addCancelListener(e -> navigateToEntity(null));
      addSaveListener(e ->
      {
         Operation newObject = e.getItem();
         entityPresenter.save(newObject, onSuccess, onFail);
         parentObject.refresh();
      });

      addDeleteListener(e -> entityPresenter.delete(e.getItem(), onSuccess, onFail));
   }//setupCrudEventListeners

}//ObjectToProtectView
