package com.f.thoth.ui.views.expediente;

import static com.f.thoth.ui.utils.Constant.PAGE_JERARQUIA_EXPEDIENTES;
import static com.f.thoth.ui.utils.Constant.TITLE_JERARQUIA_EXPEDIENTES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.ExpedienteService;
import com.f.thoth.ui.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * La gestión de expedientes procede por pasos:
 * [1] Obtiene la clase a la que pertenece el expediente
 * [2] Selecciona el expediente navegando la jerarquía de expedientes en la clase
 * [3] Crea, actualiza, elimina expedientes en el expediente seleccionado
 * Esta vista corresponde a los pasos [2], [3]. El paso [1] se ejeccuta en ExpedienteClassSelectorView
 */
@Route(value = PAGE_JERARQUIA_EXPEDIENTES, layout = MainView.class)
@PageTitle(TITLE_JERARQUIA_EXPEDIENTES)
@Secured(Role.ADMIN)
class ExpedienteHierarchyView extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver
{

      /**********
       *  1. Considerar utilizar solo dos páneles fijos y uno flotante Left-> Class selector right-> expediente selector  float-> edit expediente
       *  2. >>>DONE-> Adicionar la columna de id al hierarchicalSelector
       *  3. Ir por pasos. a) >>>DONE-> construir y probar el Class selector, b) construir el expediente selector c) Construir el expediente editor
       *  4. Crear la configuración de debug del proyecto ver https://www.baeldung.com/spring-debugging
       *
       */


   private ClassificationService classificationService;
   private ExpedienteService     expedienteService;
   private User                  currentUser;
   private String                classCode;

   private Button create   = new Button("+ Nuevo Expediente");
   private Button save     = new Button("Guardar expediente");
   private Button delete   = new Button("Eliminar expediente");
   private Button close    = new Button("Cancelar");


   private VerticalLayout        content;

   @Autowired
   public ExpedienteHierarchyView(ClassificationService classificationService, ExpedienteService expedienteService)
   {
      this.classificationService = classificationService;
      this.expedienteService     = expedienteService;
      this.currentUser           = ThothSession.getCurrentUser();

      addClassName("main-view");
      setSizeFull();

      content      = new VerticalLayout();
      content.addClassName ("selector");
      //content.add( configureClassSelector());

      // updateSelector();
      // closeEditor();

      content.setSizeFull();
      add( content);

       //Notification.show("LLequé a jerarquía de expedientes");


      /*
         leftSection  = new VerticalLayout();
         leftSection.addClassName  ("left-section");
         leftSection.add(new H3 ("Clasificación del expediente"));
         leftSection.add( configureClassSelector());

         rightSection = new VerticalLayout();
         rightSection.addClassName ("right-section");

         content      = new VerticalLayout();
         content.addClassName ("content");
         content.setSizeFull();
         content.add(new H3("Expedientes, Subexpedientes y Volúmenes"));
         //content.add(configureExpedienteSelector(), configureExpedienteActions());

         //rightSection.add(configureForm(expedienteForm ));
         updateSelector();
         closeEditor();

         HorizontalLayout panel=  new HorizontalLayout( content, rightSection);
         panel.setSizeFull();
         add( panel);
       */



      /*  de ExpedienteView
      leftSection  = new VerticalLayout();
      leftSection.addClassName  ("left-section");
      leftSection.add(new H3 ("Clasificación del expediente"));
      leftSection.add( configureClassSelector());

      rightSection = new VerticalLayout();
      rightSection.addClassName ("right-section");

      content      = new VerticalLayout();
      content.addClassName ("content");
      content.setSizeFull();
      content.add(new H3("Expedientes, Subexpedientes y Volúmenes"));
      //content.add(configureExpedienteSelector(), configureExpedienteActions());

      //rightSection.add(configureForm(expedienteForm ));
      updateSelector();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout( content, rightSection);
      panel.setSizeFull();
      add( panel);

   private Component configureButtons()
   {
      add.     addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      save.    addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      delete.  addThemeVariants(ButtonVariant.LUMO_ERROR);
      close.   addThemeVariants(ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      add .addClickListener  (click -> addClass());
      save.addClickListener  (click -> saveClass(currentClass));
      delete.addClickListener(click -> deleteClass(currentClass));
      close.addClickListener (click -> closeAll());

      save.getElement().getStyle().set("margin-left", "auto");
      add .getElement().getStyle().set("margin-left", "auto");

      HorizontalLayout buttons = new HorizontalLayout();
      buttons.setWidthFull();
      buttons.setPadding(true);
      buttons.add( delete, save, close, add);
      return buttons;
   }//configureButtons


   private ClassificationForm configureForm(List<Retention >retentionSchedules)
   {
      expedienteForm = new ClassificationForm(retentionSchedules);
      expedienteForm.addListener(ClassificationForm.SaveEvent.class,   this::saveClassification);
      expedienteForm.addListener(ClassificationForm.CloseEvent.class,  e -> closeEditor());
      return expedienteForm;

   }//configureForm
       */

   }//ExpedienteHierarchyView

   @Override
   public void afterNavigation(AfterNavigationEvent event)
   {
      content.add(new H3 ("Expedientes de la clase "+ classCode));
   }//afterNavigation

   @Override
   public void setParameter(BeforeEvent event, String parameter)
   {
      //Notification.show("Voy a navegar con parámetro["+ parameter+ "]");
      this.classCode = parameter;
   }

   /*
   if( expediente.isPersisted())
      expediente = expedienteService.load(expediente.getId());

   expedienteForm.setVisible(true);
   expedienteForm.setClassification(expediente);
   rightSection.setVisible(true);
   expedienteForm.addClassName("selected-item-form");
    */


}//ExpedienteHierarchyView
