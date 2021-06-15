package com.f.thoth.ui.views.classification;

import static com.f.thoth.Parm.CLASS_CODE_SIZE;
import static com.f.thoth.Parm.CURRENT_USER;
import static com.f.thoth.Parm.TENANT;
import static com.f.thoth.ui.utils.Constant.PAGE_ESQUEMAS_CLASIFICACION;
import static com.f.thoth.ui.utils.Constant.TITLE_ESQUEMAS_CLASIFICACION;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.LevelService;
import com.f.thoth.backend.service.RetentionService;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route(value = PAGE_ESQUEMAS_CLASIFICACION, layout = MainView.class)
@PageTitle(TITLE_ESQUEMAS_CLASIFICACION)
@Secured(Role.ADMIN)
public class ClassificationView extends VerticalLayout
{
   private ClassificationForm    classificationForm;
   private ClassificationService classificationService;
   private User                  currentUser;
   private Tenant                tenant;

   private VerticalLayout        content;
   private VerticalLayout        rightSection;

   private HierarchicalSelector<Classification, HasValue.ValueChangeEvent<Classification>> ownerClass;
   private Classification        currentClass= null;
   private Notifier              notifier    = new Notifier();

   private Level[] levels;
   private List<Retention>  retentionSchedules;


   @Autowired
   public ClassificationView(ClassificationService classificationService, LevelService levelService, RetentionService retentionService)
   {
      this.classificationService = classificationService;
      VaadinSession vSession     = VaadinSession.getCurrent();
      this.currentUser           = (User)vSession.getAttribute(CURRENT_USER);
      this.tenant                = (Tenant)vSession.getAttribute(TENANT);

      levels = getAllLevels( levelService);
      retentionSchedules = retentionService.findAll();

      addClassName("main-view");
      setSizeFull();

      rightSection = new VerticalLayout();
      rightSection.addClassName ("right-section");

      content      = new VerticalLayout();
      content.addClassName ("selector");
      content.add(new H3("Clases registradas"));

      content.add( configureGrid(), configureButtons());
      rightSection.add(configureForm(retentionSchedules));
      updateSelector();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout(content, rightSection);
      panel.setSizeFull();
      add( panel);

   }//ClassificationView

   protected String getBasePage() { return PAGE_ESQUEMAS_CLASIFICACION; }


   private Level[] getAllLevels( LevelService levelService)
   {
      List<Level> allLevels  = levelService.findAll();
      int         nLevels    = allLevels.size();
      Level[]     levels     = new Level[nLevels];
      for( int i=0; i < nLevels; i++)
      { levels[i] = allLevels.get(i);
      }
      return levels;
   }//getAllLevels

   private Component configureGrid()
   {
      ownerClass = new HierarchicalSelector<>(
                           classificationService,
                           Grid.SelectionMode.SINGLE,
                           "Seleccione la clase padre",
                           true,
                           false,
                           this::editOwner
                           );
      ownerClass.getElement().setAttribute("colspan", "3");

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
      Button add      = new Button("+ Nueva Clase");
      add.   addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      add.   getElement().getStyle().set("margin-left", "auto");
      add.   addClickShortcut(Key.ENTER);
      add.   addClickListener  (click -> addClass());

      Button delete = new Button("Eliminar Clase");
      delete.addClickShortcut (Key.DELETE);
      delete.addThemeVariants  (ButtonVariant.LUMO_ERROR);
      delete.getElement().getStyle().set("margin-left", "auto");
      delete.addClickListener  (click -> deleteClass(currentClass));
      
      Button close    = new Button("Cancelar");
      close. addThemeVariants(ButtonVariant.LUMO_TERTIARY);
      close. addClickShortcut(Key.ESCAPE);
      close. addClickListener (click -> closeAll());

      add .getElement().getStyle().set("margin-left", "auto");

      HorizontalLayout buttons = new HorizontalLayout();
      buttons.setWidthFull();
      buttons.setPadding(true);
      buttons.add(close, delete, add);
      return buttons;
   }//configureButtons


   private ClassificationForm configureForm(List<Retention >retentionSchedules)
   {
      classificationForm = new ClassificationForm(retentionSchedules);
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
      Classification owner = ownerClass.getValue();
      Level          level = getCurrentLevel(owner);
      currentClass         =  new Classification();
      currentClass.setTenant(tenant);
      currentClass.setLevel (level);
      currentClass.setOwner (owner);
      assignClassCode( currentClass);
      editClass(currentClass);
   }//addClass


   private void deleteClass(Classification classification)
   {
     if (classification != null  && classification.isPersisted())
     {
       if (!classificationService.hasChildren(currentClass))
       {  Classification parentClass = currentClass.getOwner();
          classificationService.delete(currentUser, currentClass);
          ownerClass.selectInGrid(parentClass);
          notifier.accept( "Clasificador "+ classification.formatCode()+ " eliminado");
       }else
       {  notifier.error("Clasificador "+ classification.formatCode()+ " no puede ser eliminado pues contiene hijos");
       }
     }
     closeEditor();
   }//deleteClass
   


   private Level  getCurrentLevel( Classification owner)
   {
      Level level      = null;
      int currentLevel = owner == null? 0: owner.getLevel().getOrden()+ 1;
      if ( currentLevel >= levels.length)
         notifier.error("La clase del último nivel no puede tener hijos");
      else
         level = levels[currentLevel];

      return level;

   }//getCurrentLevel


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
      boolean isNew = !classification.isPersisted();
      classificationService.save(currentUser, classification);
      ownerClass.selectInGrid(classification);
      closeEditor();
      notifier.accept("Clasificador "+ classification.formatCode()+ (isNew? " creado" : " actualizado"));

   }//saveClassification
   
   
   
   private  void assignClassCode( Classification currentClass)
   {
      Classification parent = currentClass.getOwner();
      int nChildren = classificationService.countByParent(parent);
      currentClass.setClassCode(TextUtil.pad(""+ (nChildren+1), CLASS_CODE_SIZE));
   }

}//ClassificationView
