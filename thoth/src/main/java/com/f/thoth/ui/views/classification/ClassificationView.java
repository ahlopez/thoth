package com.f.thoth.ui.views.classification;


import static com.f.thoth.ui.utils.Constant.PAGE_ESQUEMAS_CLASIFICACION;
import static com.f.thoth.ui.utils.Constant.TITLE_ESQUEMAS_CLASIFICACION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.components.HierarchicalSelector;
import com.f.thoth.ui.components.Notifier;
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

@Route(value = PAGE_ESQUEMAS_CLASIFICACION, layout = MainView.class)
@PageTitle(TITLE_ESQUEMAS_CLASIFICACION)
@Secured(Role.ADMIN)
public class ClassificationView extends VerticalLayout
{
   private ClassificationForm    classificationForm;
   private ClassificationService classificationService;
   private User                  currentUser;

   private VerticalLayout        leftSection;
   private VerticalLayout        content;
   private VerticalLayout        rightSection;
   
   private HierarchicalSelector<Classification, HasValue.ValueChangeEvent<Classification>> ownerClass;
   private Classification        currentClass= null;
   
   private Button add      = new Button("+ Nueva Clase");
   private Button save     = new Button("Guardar clase");
   private Button delete   = new Button("Eliminar clase");
   private Button close    = new Button("Cancelar");


   @Autowired
   public ClassificationView(ClassificationService classificationService)
   {
      this.classificationService = classificationService;
      this.currentUser           = ThothSession.getCurrentUser();

      addClassName("main-view");
      setSizeFull();

      leftSection  = new VerticalLayout();
      leftSection.addClassName  ("left-section");
      leftSection.add(new Label (" "));

      rightSection = new VerticalLayout();
      rightSection.addClassName ("right-section");

      content      = new VerticalLayout();
      content.addClassName      ("content");
      content.setSizeFull();
      content.add(new H3("Clases registradas"));

      content.add( configureGrid(), configureButtons());
      rightSection.add(configureForm());
      updateSelector();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout(leftSection, content, rightSection);
      panel.setSizeFull();
      add( panel);

   }//ClassificationView

   protected String getBasePage() { return PAGE_ESQUEMAS_CLASIFICACION; }

   private Component configureGrid()
   {
      ownerClass = new HierarchicalSelector<>(
                           classificationService, 
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


   private ClassificationForm configureForm()
   {
      classificationForm = new ClassificationForm();
      classificationForm.addListener(ClassificationForm.SaveEvent.class,   this::saveClassification);
      classificationForm.addListener(ClassificationForm.CloseEvent.class,  e -> closeEditor());
      return classificationForm;

   }//configureForm

   
   private void editOwner(Classification owner)
   {
      this.currentClass = owner;
      editClass(currentClass);
   }//editOwner
   

   private void addClass()
   {
      currentClass = new Classification();
      currentClass.setOwner(ownerClass.getValue());
      editClass(currentClass);
   }//addClass
   
   
   private void saveClass( Classification classification)
   {
      classificationService.save(currentUser, classification);
      closeEditor();
      currentClass = null;
     
   }//saveClass


   private void deleteClass(Classification classification)
   {
      try
      {
         if( classification != null && classification.isPersisted())
             classificationService.delete(currentUser, classification);
      } catch (Exception e)
      {
         Notifier.error("Clase["+ classification.getName()+ "] tiene referencias. No puede ser borrada");
      }
      updateSelector();
      closeEditor();
   }//deleteClass
   
   
   private void editClass(Classification classification)
   {
      if (classification == null)
      {
         closeEditor();
      } else
      {
         if( classification.isPersisted())
            classification = classificationService.load(classification.getId());

         classificationForm.setVisible(true);
         classificationForm.setClassification(classification);
         rightSection.setVisible(true);
         classificationForm.addClassName("selected-item-form");
      }
   }//editClass
   

   private void closeEditor()
   {
      classificationForm.setClassification(null);
      classificationForm.setVisible(false);
      classificationForm.removeClassName("selected-item-form");

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
      Classification classification = event.getClassification();
      classificationService.save(currentUser, classification);
      updateSelector();
      closeEditor();
   }//saveClassification

}//ClassificationView
