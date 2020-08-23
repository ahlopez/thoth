package com.f.thoth.ui.views.security.permission;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.HierarchicalService;
import com.f.thoth.ui.components.TreeGridSelector;
import com.f.thoth.ui.presenters.PermissionPresenter;
import com.f.thoth.ui.utils.TemplateUtil;
import com.f.thoth.ui.views.HasNotifications;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.shared.Registration;

public abstract class      AbstractPermissionView<E extends HierarchicalEntity<E>> 
                extends    VerticalLayout 
                implements HasUrlParameter<Long>, HasNotifications
{
   
   // -----------------------------   Events -------------------------------   
   public abstract class PermitEvent extends ComponentEvent<AbstractPermissionView<E>> 
   {
      private E objectToProtect;
      protected PermitEvent( AbstractPermissionView<E> source, E objectToProtect) 
      { 
         super(source, false);
         this.objectToProtect = objectToProtect;
      }
      public E getObjectToProtect() { return objectToProtect; }

   }//PermitFormEvent

   public class GrantRevokeEvent extends PermitEvent 
   {
      GrantRevokeEvent(AbstractPermissionView<E> source, E objectToProtect) 
      {
         super(source, objectToProtect);
      }
      
   }//GrantRevokeEvent

   public class CloseEvent extends PermitEvent 
   {
      CloseEvent(AbstractPermissionView<E> source) 
      { 
         super(source, null); 
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) 
   { 
      return getEventBus().addListener(eventType, listener);
   }
   
   // -----------------------------   View -------------------------------

   // private TreeDataProvider<E>     dataProvider;
   private PermissionPresenter<E>      permitPresenter;
   private E                       objectToProtect;
   private TreeGridSelector<E, HasValue.ValueChangeEvent<E>> permit;


   @Autowired
   public AbstractPermissionView(Class<E> beanType, HierarchicalService<E> service, CurrentUser currentUser, String name)
   {
      permitPresenter = new PermissionPresenter<>(service, currentUser, this);

      ComboBox<Role> role = new ComboBox<>();
      role.getElement().setAttribute("colspan", "2");
      role.setLabel("Rol");
      role.setDataProvider(ThothSession.getTenantRoles());
      role.setItemLabelGenerator(createItemLabelGenerator(Role::getName));
      role.setRequired(false);
      role.setRequiredIndicatorVisible(false);
      role.setClearButtonVisible(true);
      role.setAllowCustomValue(true);
      role.setPageSize(20);

      add(role);
      add( new Label("Permisos"));

      add(selector( beanType, service, name));
      add(actions());
      setupEventListeners(permitPresenter);

   }//AbstractPermitView constructor


   private Component selector( Class<E> beanType,  HierarchicalService<E> service, String name)
   {
      permit = new TreeGridSelector<>(service, Grid.SelectionMode.MULTI, name);
      // TODO: Hay que inicializar el selector con los permisos que ya existen
      // TODO: Hay que adicionar al selector el addMultiSelectionListener, procesar los grants y revokes
      permit.getElement().setAttribute("colspan", "4");

      FormLayout form = new FormLayout(permit);
      form.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4));

      return form;

   }//selector

   protected abstract String getBasePage(); 

   protected Component actions()
   {
      HorizontalLayout actions = new HorizontalLayout();
      actions.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
      actions.setWidth("100%");

      Button save = new Button("Guardar");
      save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      save.addClickListener(event -> fireEvent(new GrantRevokeEvent(this, objectToProtect)));


      Button close = new Button("Cancelar");
      close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);      
      close.addClickListener(event -> fireEvent(new CloseEvent(this)));

      actions.add(close,save);
      return actions;
      
   }//actions

   protected void setupEventListeners( PermissionPresenter<E> permitPresenter)
   {
      // Consumer<E> onSuccess = entity -> navigateToEntity(null);
      // Consumer<E> onFail    = entity -> {throw new RuntimeException("La operación no pudo ser ejecutada."); };

      //addMultiSelectionListener ( (e) ->  permitPresenter.grant(e.getItem(), onSuccess, onFail));
      //

   }//setupEventListeners

   protected void navigateToEntity(String id)
   {
      getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(getBasePage(), id)));
   }//navigateToEntity



}//AbstractPermitView
