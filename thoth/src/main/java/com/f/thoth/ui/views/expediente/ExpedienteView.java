package com.f.thoth.ui.views.expediente;


import static com.f.thoth.ui.utils.Constant.PAGE_EXPEDIENTES;
import static com.f.thoth.ui.utils.Constant.TITLE_EXPEDIENTES;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.LevelService;
import com.f.thoth.backend.service.RetentionService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.components.Notifier;
import com.f.thoth.ui.views.classification.ClassificationForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_EXPEDIENTES, layout = MainView.class)
@PageTitle(TITLE_EXPEDIENTES)
@Secured(Role.ADMIN)
public class ExpedienteView extends VerticalLayout
{
   private ClassificationForm    expedienteForm;
   private ClassificationService classificationService;
   private ExpedienteService     expedienteService;
   private User                  currentUser;

   private VerticalLayout        leftSection;
   private VerticalLayout        content;
   private VerticalLayout        rightSection;
   
   private HierarchicalSelector<Classification, HasValue.ValueChangeEvent<Classification>> ownerClass;
   private Classification        currentClass= null;
   
   private Button add      = new Button("+ Nuevo Expediente");
   private Button save     = new Button("Guardar expediente");
   private Button delete   = new Button("Eliminar expediente");
   private Button close    = new Button("Cancelar");
   
   private Level[] levels;
   private List<Retention>  retentionSchedules;


   @Autowired
   public ClassificationView(ClassificationService classificationService, ExpedienteService expedienteService)
   {
	  this.classificationService = classificationService;
      this.expedienteService     = expedienteService;
      this.currentUser           = ThothSession.getCurrentUser();
      
      addClassName("main-view");
      setSizeFull();

      leftSection  = new VerticalLayout();
      leftSection.addClassName  ("left-section");
      leftSection.add(new H3 ("Clasificación del expediente"));
      leftSection.add( configureClassSelector());

      rightSection = new VerticalLayout();
      rightSection.addClassName ("right-section");

      content      = new VerticalLayout();
      content.addClassName ("content");
      content.setSizeFull();
      content.add(new H3("Expedientes, subexpedientes, volúmenes"));
      content.add(configureExpedienteSelector(), configureExpedienteActions());

      rightSection.add(configureForm(expedienteForm ));
      updateSelector();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout(leftSection, content, rightSection);
      panel.setSizeFull();
      add( panel);

   }//ClassificationView

   protected String getBasePage() { return PAGE_ESQUEMAS_CLASIFICACION; }

   
   private Level[] getAllLevels( LevelService levelService)
   {       
      List<Level> allLevels      = levelService.findAll();
      int nLevels    = allLevels.size();
      Level[] levels = new Level[nLevels];
      for( int i=0; i < nLevels; i++)
         levels[i] = allLevels.get(i);
      
      return levels;
   }//getAllLevels

   private Component configureGrid()
   {
      ownerClass = new HierarchicalSelector<>(
                           expedienteService, 
                           Grid.SelectionMode.SINGLE, 
                           "Seleccione la clase padre", 
                           this::editOwner
                           );     
      ownerClass.getElement().setAttribute("colspan", "4");

      FormLayout form = new FormLayout(ownerClass);
      form.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4));

      BeanValidationBinder<Classification> binder = new BeanValidationBinder<>(Classification.class);
      binder.forField(ownerClass)
            .bind("owner");
      
      return ownerClass;

   }//configureGrid
     

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

   
   private void editOwner(Classification owner)
   {
      this.currentClass = owner;
      editClass(currentClass);
   }//editOwner
   

   private void addClass()
   {
      currentClass = new Classification();
      Classification owner = ownerClass.getValue();
      currentClass.setOwner(owner);
      Level level = getCurrentLevel(owner);
      currentClass.setLevel(level);
      editClass(currentClass);
   }//addClass
   
   private Level  getCurrentLevel( Classification owner)
   {
      Level level      = null;
      int currentLevel = owner.getLevel().getOrden()+ 1;
      if ( currentLevel >= levels.length)
         Notifier.error("La clase del último nivel no puede tener hijos");       
      else
         level = levels[currentLevel];

      return level;
      
   }//getCurrentLevel
   
   
   private void saveClass( Classification expediente)
   {
      expedienteService.save(currentUser, expediente);
      closeEditor();
      currentClass = null;
     
   }//saveClass


   private void deleteClass(Classification expediente)
   {
      try
      {
         if( expediente != null && expediente.isPersisted())
             expedienteService.delete(currentUser, expediente);
      } catch (Exception e)
      {
         Notifier.error("Clase["+ expediente.getName()+ "] tiene referencias. No puede ser borrada");
      }
      updateSelector();
      closeEditor();
   }//deleteClass
   
   
   private void editClass(Classification expediente)
   {
      if (expediente == null)
      {
         closeEditor();
      } else
      {
         if( expediente.isPersisted())
            expediente = expedienteService.load(expediente.getId());

         expedienteForm.setVisible(true);
         expedienteForm.setClassification(expediente);
         rightSection.setVisible(true);
         expedienteForm.addClassName("selected-item-form");
      }
   }//editClass
   

   private void closeEditor()
   {
      expedienteForm.setClassification(null);
      expedienteForm.setVisible(false);
      expedienteForm.removeClassName("selected-item-form");

   }//closeEditor
   
   
   private void closeAll()
   {
      closeEditor();
      currentClass = null;
      ownerClass.resetSelector();      
   }//closeAll
   

   private void updateSelector()
   {
     ownerClass.refresh();
   }//updateSelector


   private void saveClassification(ClassificationForm.SaveEvent event)
   {
      Classification expediente = event.getClassification();
      expedienteService.save(currentUser, expediente);
      updateSelector();
      closeEditor();
   }//saveClassification

}//ClassificationView
