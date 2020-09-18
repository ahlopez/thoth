package com.f.thoth.ui.views.classification;

import static com.f.thoth.ui.utils.Constant.PAGE_NIVELES;
import static com.f.thoth.ui.utils.Constant.TITLE_NIVELES;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.LevelService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_NIVELES, layout = MainView.class)
@PageTitle(TITLE_NIVELES)
@Secured(Role.ADMIN)
public class LevelsView extends VerticalLayout
{
   private LevelForm  levelForm;
   private Grid<Level>     grid  = new Grid<>(Level.class);
   private TextField  filterText = new TextField();

   private LevelService   levelService;
   private User            currentUser;

   private VerticalLayout   leftSection;
   private VerticalLayout   content;
   private VerticalLayout   rightSection;


   @Autowired
   public LevelsView(LevelService levelService, SchemaService schemaService)
   {
      this.levelService   = levelService;
      this.currentUser    = ThothSession.getCurrentUser();

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

      content.add(getToolBar(), configureGrid());
      rightSection.add(configureForm(schemaService.findAll()));
      updateList();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout(leftSection, content, rightSection);
      panel.setSizeFull();
      add( panel);

   }//LevelsView


   protected String getBasePage() { return PAGE_NIVELES; }

   private HorizontalLayout getToolBar()
   {
      filterText.setPlaceholder("Filtro...");
      filterText.setWidthFull();
      filterText.setClearButtonVisible(true);
      filterText.setValueChangeMode(ValueChangeMode.LAZY);
      filterText.addValueChangeListener(e -> updateList());

      Button addLevelButton = new Button("+ Nuevo nivel", click -> addLevel());
      addLevelButton.setWidth("40%");
      addLevelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


      HorizontalLayout toolbar = new HorizontalLayout(filterText, addLevelButton);
      toolbar.setWidthFull();
      toolbar.addClassName("toolbar");
      return toolbar;
   }//getToolBar

   private void addLevel()
   {
      editLevel(new Level());
   }//addLevel

   private Grid<Level> configureGrid()
   {
      grid.addClassName("metadata-grid");
      grid.setSizeFull();
      grid.addColumn(level -> level.getName() == null? "---" : level.getName()) .setHeader("Nombre").setFlexGrow(20);
      grid.addColumn(level -> level.getOrden()== null? "---" : level.getOrden()).setHeader("Orden") .setFlexGrow(10);
      grid.getColumns().forEach(col -> col.setAutoWidth(true));
      grid.asSingleSelect().addValueChangeListener(event -> editLevel(event.getValue()));
      return grid;

   }//configureGrid

   private LevelForm configureForm(List<Schema>availableSchemas)
   {
      levelForm = new LevelForm(availableSchemas);
      levelForm.addListener(LevelForm.SaveEvent.class,   this::saveLevel);
      levelForm.addListener(LevelForm.DeleteEvent.class, this::deleteLevel);
      levelForm.addListener(LevelForm.CloseEvent.class,  e -> closeEditor());
      return levelForm;

   }//configureForm

   private void editLevel(Level level)
   {
      if (level == null)
      {
         closeEditor();
      } else
      {
         if( level.isPersisted())
            level = levelService.load(level.getId());

         levelForm.setVisible(true);
         levelForm.setLevel(level);
         rightSection.setVisible(true);
         levelForm.addClassName("selected-item-form");
      }
   }//editLevel

   private void closeEditor()
   {
      levelForm.setLevel(null);
      levelForm.setVisible(false);
      levelForm.removeClassName("selected-item-form");

   }//closeEditor

   private void updateList()
   {
      Optional<String> filter = Optional.of(filterText.getValue());
      List<Level>      levels = levelService.findAnyMatching(filter);
      grid.setItems(levels);
   }//updateList


   private void deleteLevel(LevelForm.DeleteEvent event)
   {
      levelService.delete(currentUser, event.getLevel());
      updateList();
      closeEditor();
   }//deleteLevel

   private void saveLevel(LevelForm.SaveEvent event)
   {
      levelService.save(currentUser, event.getLevel());
      updateList();
      closeEditor();
   }//saveLevel

}//LevelView




