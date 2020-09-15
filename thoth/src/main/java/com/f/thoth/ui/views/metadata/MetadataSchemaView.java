package com.f.thoth.ui.views.metadata;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.MetadataService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@CssImport("./styles/shared-styles.css")
@PageTitle("Metadatos | Evidentia")
@Route(value = Constant.PAGE_ESQUEMAS_METADATA, layout=MainView.class)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class MetadataSchemaView extends VerticalLayout
{
   private SchemaForm schemaForm;
   private Grid<Schema>     grid  = new Grid<>(Schema.class);
   private TextField  filterText  = new TextField();

   private SchemaService   schemaService;
   private User            currentUser;

   private VerticalLayout   leftSection;
   private VerticalLayout   content;
   private VerticalLayout   rightSection;


   @Autowired
   public MetadataSchemaView(SchemaService schemaService, MetadataService metadataService)
   {
      this.schemaService   = schemaService;
      this.currentUser     = ThothSession.getCurrentUser();

      addClassName("schema-view");
      setSizeFull();

      leftSection         = new VerticalLayout();
      leftSection.addClassName  ("left-section");
      leftSection.add(new Label (" "));

      rightSection        = new VerticalLayout();
      rightSection.addClassName ("right-section");

      content             = new VerticalLayout();
      content.addClassName      ("content");
      content.setSizeFull();
      content.add(new H3("Esquemas registrados"));

      content.add(getToolBar(), configureGrid());
      rightSection.add(configureForm(metadataService));
      updateList();
      closeEditor();

      HorizontalLayout panel=  new HorizontalLayout(leftSection, content, rightSection);
      panel.setSizeFull();
      add( panel);

   }//MetadataSchemaView


   protected String getBasePage() { return Constant.PAGE_ESQUEMAS_METADATA; }

   private HorizontalLayout getToolBar()
   {
      filterText.setPlaceholder("[Filtro]");
      filterText.setWidthFull();
      filterText.setClearButtonVisible(true);
      filterText.setValueChangeMode(ValueChangeMode.LAZY);
      filterText.addValueChangeListener(e -> updateList());

      Button addSchemaButton = new Button("+Nuevo esquema", click -> addSchema());
      addSchemaButton.setWidth("40%");
      addSchemaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


      HorizontalLayout toolbar = new HorizontalLayout(filterText, addSchemaButton);
      toolbar.setWidthFull();
      toolbar.addClassName("toolbar");
      return toolbar;
   }//getToolBar

   private void addSchema()
   {
      editSchema(new Schema("Nombre", new TreeSet<>()));
   }//addSchema

   private Grid<Schema> configureGrid()
   {
      grid.addClassName("metadata-grid");
      grid.setSizeFull();
      grid.setColumns("name");
      grid.getColumns().forEach(col -> col.setAutoWidth(true));
      grid.asSingleSelect().addValueChangeListener(event -> editSchema(event.getValue()));
      return grid;

   }//configureGrid

   private SchemaForm configureForm(MetadataService metadataService)
   {
      schemaForm = new SchemaForm(metadataService.findAll());
      schemaForm.addListener(SchemaForm.SaveEvent.class,   this::saveSchema);
      schemaForm.addListener(SchemaForm.DeleteEvent.class, this::deleteSchema);
      schemaForm.addListener(SchemaForm.CloseEvent.class,  e -> closeEditor());
      return schemaForm;

   }//configureForm

   private void editSchema(Schema schema)
   {
      if (schema == null)
      {
         closeEditor();
      } else
      {
         if( schema.isPersisted())
            schema = schemaService.load(schema.getId());

         schemaForm.setVisible(true);
         schemaForm.setSchema(schema);
         rightSection.setVisible(true);
         addClassName("editing");
      }
   }//editSchema

   private void closeEditor()
   {
      schemaForm.setSchema(null);
      schemaForm.setVisible(false);
      removeClassName("editing");

   }//closeEditor

   private void updateList()
   {
      Optional<String> filter = Optional.of(filterText.getValue());
      List<Schema>    schemas = schemaService.findAnyMatching(filter);
      grid.setItems(schemas);
   }//updateList


   private void deleteSchema(SchemaForm.DeleteEvent event)
   {
      schemaService.delete(currentUser, event.getSchema());
      updateList();
      closeEditor();
   }//deleteSchema

   private void saveSchema(SchemaForm.SaveEvent event)
   {
      schemaService.save(currentUser, event.getSchema());
      updateList();
      closeEditor();
   }//saveSchema

}//MetadataSchemaView
