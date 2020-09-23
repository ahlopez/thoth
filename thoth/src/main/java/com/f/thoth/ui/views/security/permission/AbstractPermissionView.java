package com.f.thoth.ui.views.security.permission;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.PermissionService;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.components.Notifier;
import com.f.thoth.ui.components.Period;
import com.f.thoth.ui.utils.TemplateUtil;
import com.f.thoth.ui.views.HasNotifications;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

/**
 * Representa la vista de actualización de permisos de ejecución/acceso a datos
 */
@CssImport("./styles/shared-styles.css")
public abstract class      AbstractPermissionView<E extends HierarchicalEntity<E>>
                extends    VerticalLayout
                implements HasNotifications
{
   private PermissionPresenter<E>   permissionPresenter;
   private Role                     role;

   private VerticalLayout           leftSection;
   private VerticalLayout           content;
   private VerticalLayout           rightSection;

   private VerticalLayout           permissionLayout;
   private ComboBox<Role>           roleSelector;
   private DatePicker               permissionFrom;
   private DatePicker               permissionTo;
   private Period                   permissionPeriod;
   private Binder<Period>           binder;
   private Button                   save         = new Button("Guardar");
   private Button                   close        = new Button("Cancelar");
   private HierarchicalSelector<E, HasValue.ValueChangeEvent<E>> permissionSelector;

   protected abstract String getBasePage();


   @Autowired
   public AbstractPermissionView(Class<E> beanType, PermissionService<E> service, CurrentUser currentUser, String name)
   {
      role                = null;
      permissionPresenter = new PermissionPresenter<>(service, currentUser, this);
      addClassName("list-view");
      setSizeFull();

      leftSection         = new VerticalLayout();
      leftSection.addClassName  ("left-section");
      leftSection.add(new Label (" "));

      rightSection        = new VerticalLayout();
      rightSection.addClassName ("right-section");
      rightSection.add(new Label(" "));

      content             = new VerticalLayout();
      content.addClassName      ("selector");
      content.setSizeFull();

      HorizontalLayout roleLayout = setupRoleSelector(name);
      permissionLayout            = setupPermissionSelector(service, currentUser, name);
      content.add(roleLayout, permissionLayout);

      HorizontalLayout panel=  new HorizontalLayout(leftSection, content, rightSection);
      panel.setSizeFull();
      add( panel);

      setupEventListeners(permissionPresenter);

   }//AbstractPermissionView constructor

   private HorizontalLayout setupRoleSelector(String name)
   {
      HorizontalLayout roleLayout = new HorizontalLayout();
      roleSelector = new ComboBox<>();
      roleSelector.setLabel("Rol");
      roleSelector.setDataProvider(ThothSession.getTenantRoles());
      roleSelector.setItemLabelGenerator(createItemLabelGenerator(Role::getName));
      roleSelector.setRequired(false);
      roleSelector.setRequiredIndicatorVisible(false);
      roleSelector.setClearButtonVisible(true);
      roleSelector.setAllowCustomValue(true);
      roleSelector.setPageSize(20);

      roleSelector.addValueChangeListener((event)->
      {
         role = event.getValue();
         permissionSelector.init( permissionPresenter.loadGrants(role));
         permissionLayout.setVisible(true);
      });

      roleLayout.add(new H3(name), roleSelector);
      return roleLayout;

   }//setupRoleSelector

   private VerticalLayout setupPermissionSelector(PermissionService<E> service, CurrentUser currentUser, String name)
   {
      permissionLayout   = new VerticalLayout();
      permissionLayout.setVisible(false);
      permissionLayout.setSizeFull();

      setupPeriod();
      setupSelector(service, currentUser, name);
      setupActions();

      return permissionLayout;

   }//setupPermissionSelector

   private void setupPeriod()
   {

      HorizontalLayout periodLayout = new HorizontalLayout();

      permissionFrom = new DatePicker("Válidos desde");
      permissionFrom.setRequired(true);
      permissionTo   = new DatePicker("Válidos hasta");
      permissionTo.setRequired(true);

      FormLayout periodForm = new FormLayout(permissionFrom, permissionTo);

      periodForm.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4)
            );
      periodLayout.add(periodForm);
      permissionLayout.add(periodLayout);

      permissionPeriod = new Period();
      binder = new BeanValidationBinder<>(Period.class);

      binder.forField(permissionFrom)
            .asRequired()
            .withValidator( fromDate ->
            {
               LocalDate toDate = permissionTo.getValue();
               return toDate != null && fromDate.equals(toDate) || fromDate.isBefore(toDate);
            }, "Fecha final debe ser igual o posterior a fecha inicial")
            .bind("fromDate");

      binder.forField(permissionTo)
            .asRequired()
            .withValidator( toDate -> toDate.equals(permissionFrom.getValue()) || toDate.isAfter(permissionFrom.getValue()),
                       "Fecha final debe ser igual o posterior a fecha inicial")
            .bind("toDate");

      binder.readBean(permissionPeriod);
   }//setupPeriod

   private void setupSelector(PermissionService<E> service, CurrentUser currentUser, String name)
   {
      permissionSelector = new HierarchicalSelector<>(service, Grid.SelectionMode.MULTI, name, null);
      permissionSelector.setWidthFull();
      permissionLayout.add(permissionSelector);

   }//setupSelector

   private void setupActions()
   {
      HorizontalLayout actions = new HorizontalLayout();
      actions.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
      actions.setWidth("100%");

      close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
      close.addClickShortcut(Key.ESCAPE);
      close.addClickListener(event -> fireEvent(new CloseEvent<>(this)));

      save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      save.addClickShortcut(Key.ENTER);
      save.addClickListener(event -> validateAndSave());

      binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

      actions.add(close,save);
      permissionLayout.add(actions);


   }//setupActions


   protected void setupEventListeners( PermissionPresenter<E> permissionPresenter)
   {
       addListener(GrantRevokeEvent.class, this::saveGrants);
       addListener(CloseEvent.class,       this::close);

   }//setupEventListeners


   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener


   protected void navigateToEntity(String id)
   {
      getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(getBasePage(), id)));
   }//navigateToEntity


   private void validateAndSave()
   {
       try
       {
          binder.writeBean(permissionPeriod);
          fireEvent(new GrantRevokeEvent<>(this, permissionSelector.getValues(), role, permissionPeriod));
       } catch (ValidationException e)
       {
          Notifier.error( "Período inválido "+
                TextUtil.formatDate(permissionPeriod.getFromDate())+ " : "+
                TextUtil.formatDate(permissionPeriod.getToDate())+ "]");
          clear();
       }
   }//validateAndSave


   private void saveGrants( GrantRevokeEvent<E> event)
   {
      permissionPresenter.grantRevoke( event.getGrants(), event.getRole(), event.getPeriod());
      Notifier.accept("Permisos del rol "+ role.getName()+ " actualizados");
      clear();

   }//saveGrants


   private void close( CloseEvent<E> event)
   {
      clear();
   }

   private void clear()
   {
      roleSelector.clear();
      //permissionSelector.resetSelector();
      permissionSelector.refresh();
      permissionLayout.setVisible(false);
   }//clear

   public abstract class PeriodEvent extends ComponentEvent<AbstractPermissionView<E>>
   {
      private Period period;
      protected PeriodEvent(AbstractPermissionView<E> source, Period period)
      {
         super(source, false);
         this.period = period;
      }
      public Period getPeriod() { return period; }

   }//PeriodEvent

   public class SaveEvent extends PeriodEvent
   {
      SaveEvent(AbstractPermissionView<E> source, Period period)
      {
         super(source, period);
      }
   }//SaveEvent

}//AbstractPermissionView
