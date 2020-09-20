package com.f.thoth.ui.views.classification;

import static com.f.thoth.ui.utils.Constant.PAGE_ESQUEMAS_CLASIFICACION;
import static com.f.thoth.ui.utils.Constant.TITLE_ESQUEMAS_CLASIFICACION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.HierarchicalService;
import com.f.thoth.ui.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "oldClassification", layout = MainView.class)
@PageTitle(TITLE_ESQUEMAS_CLASIFICACION)
@Secured(Role.ADMIN)
public class OldClassificationView extends VerticalLayout
{
   private ClassificationForm    classificationForm;
   private TextField             filterText = new TextField();
   private Classification        owner;

   private ClassificationService classificationService;
   private User                  currentUser;

   private VerticalLayout        leftSection;
   private VerticalLayout        content;
   private VerticalLayout        rightSection;

   private TreeDataProvider<Classification>   dataProvider;
   private TreeGrid<Classification> tGrid = new TreeGrid<>(Classification.class);
   private List<Classification>   classNodes;

   @Autowired
   public OldClassificationView(ClassificationService classificationService)
   {
      this.classificationService = classificationService;
      this.currentUser           = ThothSession.getCurrentUser();
      this.owner                 = null;

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
      content.add(new H3("Niveles registrados"));

      content.add(configureToolBar(), configureGrid());
      rightSection.add(configureForm());
      updateTree();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout(leftSection, content, rightSection);
      panel.setSizeFull();
      add( panel);

   }//OldClassificationView

   protected String getBasePage() { return "oldClassification"; }

   private HorizontalLayout configureToolBar()
   {
      filterText.setPlaceholder("Filtro...");
      filterText.setWidthFull();
      filterText.setClearButtonVisible(true);
      filterText.setValueChangeMode(ValueChangeMode.LAZY);
      filterText.addValueChangeListener(e -> updateTree());

      Button addClassButton = new Button("+ Nueva clase", click -> addClass());
      addClassButton.setWidth("40%");
      addClassButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

      HorizontalLayout toolbar = new HorizontalLayout(filterText, addClassButton);
      toolbar.setWidthFull();
      toolbar.addClassName("toolbar");
      return toolbar;
   }//configureToolBar

   private void addClass()
   {
      Classification newClass = new Classification();
      newClass.setOwner(owner);
      editClass(newClass);
   }//addClass

   private TreeGrid<Classification> configureGrid()
   {
      tGrid.addClassName("metadata-tGrid");
      tGrid.setSizeFull();
      tGrid.addHierarchyColumn(Classification::getName).setHeader("Nombre");
      dataProvider = getDataProvider(classificationService);
      tGrid.setDataProvider(dataProvider);
      tGrid.asSingleSelect().addValueChangeListener(event ->
         {
            owner = event.getValue();
            editClass(event.getValue());
         });
      return tGrid;

   }//configureGrid


   private  TreeDataProvider<Classification>  getDataProvider(HierarchicalService<Classification> service )
   {
      classNodes = service.findAll();
      TreeData<Classification> treeData = new TreeData<Classification>();
      addChildrenOf(null, treeData);
      TreeDataProvider<Classification> dataProvider = new TreeDataProvider<>(treeData);
      return dataProvider;

   }//getDataProvider


   private void addChildrenOf(Classification parent, TreeData<Classification> treeData)
   {
      List<Classification> children = getChildrenOf( parent);
      children.forEach( child->
      {
         treeData.addItem(parent, child);
         addChildrenOf(child, treeData);
         classNodes.remove(child);
      });
   }//addChildrenOf


   private List<Classification>getChildrenOf( Classification owner)
   {
      List<Classification> children = new ArrayList<>();
      classNodes.forEach(item->
      {
         Classification parent =  item.getOwner();
         if (parent == null)
         {
            if (owner == null)
               children.add(item);
         }
         else if (parent.equals(owner))
            children.add(item);
      });
      return children;
   }//getChildrenOf


   private ClassificationForm configureForm()
   {
      classificationForm = new ClassificationForm();
      classificationForm.addListener(ClassificationForm.SaveEvent.class,   this::saveClassification);
      classificationForm.addListener(ClassificationForm.CloseEvent.class,  e -> closeEditor());
      return classificationForm;

   }//configureForm

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

   private void updateTree()
   {
      /*
      Optional<String> filter = Optional.of(filterText.getValue());
      List<Classification>      levels = classificationService.findAnyMatching(filter);
      tGrid.setItems(levels);
      */
   }//updateTree


   /*
   private void deleteClassification(Classification classification)
   {
      try
      {
         if( classification.isPersisted())
             classificationService.delete(currentUser, classification);
      } catch (Exception e)
      {
         Notifier.error("Clase["+ classification.getName()+ "] tiene referencias. No puede ser borrada");
      }
      updateTree();
      closeEditor();
   }//deleteClassification
   */

   private void saveClassification(ClassificationForm.SaveEvent event)
   {
      Classification classification = event.getClassification();
      classificationService.save(currentUser, classification);
      updateTree();
      closeEditor();
   }//saveClassification

}//OldClassificationView




