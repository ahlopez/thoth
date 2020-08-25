package com.f.thoth.ui.views.security.permission;

import static com.f.thoth.ui.dataproviders.DataProviderUtil.createItemLabelGenerator;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.PermissionService;
import com.f.thoth.ui.components.TreeGridSelector;
import com.f.thoth.ui.utils.TemplateUtil;
import com.f.thoth.ui.views.HasNotifications;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public abstract class      AbstractPermissionView<E extends HierarchicalEntity<E>> 
                extends    VerticalLayout 
                implements HasNotifications
{

   // private TreeDataProvider<ObjectToProtect>   dataProvider;
   private PermissionPresenter<E>   permissionPresenter;
   private Role                     role;
   private CurrentUser              currentUser;
   
   private VerticalLayout           permissionLayout;
   private ComboBox<Role>           roleSelector = new ComboBox<>(); 
   private Button                   save         = new Button("Guardar");
   private Button                   close        = new Button("Cancelar");
   private TreeGridSelector<E, HasValue.ValueChangeEvent<E>> permissionSelector;

   @Autowired
   public AbstractPermissionView(Class<E> beanType, PermissionService<E> service, CurrentUser currentUser, String name)
   {
      role                = null;
      this.currentUser    = currentUser;
      permissionPresenter = new PermissionPresenter<>(service, currentUser, this);
      setWidthFull();
      
      roleSelector.getElement().setAttribute("colspan", "2");
      roleSelector.setLabel("Rol");
      roleSelector.setDataProvider(ThothSession.getTenantRoles());
      roleSelector.setItemLabelGenerator(createItemLabelGenerator(Role::getName));
      roleSelector.setRequired(false);
      roleSelector.setRequiredIndicatorVisible(false);
      roleSelector.setClearButtonVisible(true);
      roleSelector.setAllowCustomValue(true);
      roleSelector.setPageSize(20);
      add( roleSelector);
      
      permissionLayout   = new VerticalLayout();
      permissionLayout.setVisible(false);
      permissionSelector = new TreeGridSelector<>(service, Grid.SelectionMode.MULTI, name);
      permissionSelector.getElement().setAttribute("colspan", "4");
      permissionSelector.init( permissionPresenter.loadGrants(role));

      FormLayout form = new FormLayout(permissionSelector);
      form.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4)
            );
      permissionLayout.add(form);
      
      HorizontalLayout actions = new HorizontalLayout();
      actions.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
      actions.setWidth("100%");

      save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      save.addClickShortcut(Key.ENTER);

      close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);      
      close.addClickShortcut(Key.ESCAPE);

      actions.add(close,save);
      setupEventListeners(permissionPresenter);
      permissionLayout.add(actions);
      add(permissionLayout);

   }//AbstractPermissionView constructor


   protected abstract String getBasePage(); 


   protected void setupEventListeners( PermissionPresenter<E> permissionPresenter)
   {
      // Consumer<ObjectToProtect> onSuccess = entity -> navigateToEntity(null);
      // Consumer<ObjectToProtect> onFail    = entity -> {throw new RuntimeException("La operación no pudo ser ejecutada."); };

      roleSelector.addValueChangeListener((event)-> 
      {
         role = event.getValue();
         permissionSelector.init( permissionPresenter.loadGrants(role));
         permissionLayout.setVisible(true);
      });
      
      save.addClickListener (event -> fireEvent(new GrantRevokeEvent<>(this, permissionSelector.getValues(), role)));
      close.addClickListener(event -> fireEvent(new CloseEvent<>(this)));
      
      addListener(GrantRevokeEvent.class, this::saveGrants); 
      //addListener(CloseEvent.class, e -> closeEditor());
      addListener(CloseEvent.class, this::close);

   }//setupEventListeners
   
   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) 
   { 
      return getEventBus().addListener(eventType, listener);
   }//addListener


   protected void navigateToEntity(String id)
   {
      getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(getBasePage(), id)));
   }//navigateToEntity
   
   private void saveGrants( GrantRevokeEvent<E> event)
   {
      permissionPresenter.grantRevoke( event.getGrants(), event.getRole(), currentUser);
      clear();
      
   }//saveGrants

   
   private void close( CloseEvent<E> event)
   {
      clear();
   }
   
   private void clear()
   {
      roleSelector.clear();
      permissionSelector.clear();
      permissionLayout.setVisible(false);      
   }//clear



}//AbstractPermitView
