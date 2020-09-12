package com.f.thoth.ui.views.metadata;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.MetadataService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = Constant.PAGE_ESQUEMAS_METADATA, layout=MainView.class)
@PageTitle("Metadatos | Evidentia")
@CssImport("./styles/shared-styles.css")
public class MetadataSchemaView extends VerticalLayout 
{
    private final SchemaForm   form;
    private Grid<Schema>       grid  = new Grid<>(Schema.class);
    private TextField    filterText  = new TextField();

    private SchemaService   schemaService;
    private User            currentUser;

    @Autowired
    public MetadataSchemaView(SchemaService schemaService, MetadataService metadataService) 
    {
        this.schemaService   = schemaService;
        this.currentUser     = ThothSession.getCurrentUser();
        
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new SchemaForm(metadataService.findAll());
        form.addListener(SchemaForm.SaveEvent.class,   this::saveSchema);
        form.addListener(SchemaForm.DeleteEvent.class, this::deleteSchema);
        form.addListener(SchemaForm.CloseEvent.class,  e -> closeEditor());

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
        
    }//MetadataSchemaView


    protected String getBasePage() {
       return Constant.PAGE_ESQUEMAS_METADATA;
    }

    
    private void deleteSchema(SchemaForm.DeleteEvent evt) 
    {
        schemaService.delete(currentUser, evt.getSchema());
        updateList();
        closeEditor();
    }//deleteSchema

    private void saveSchema(SchemaForm.SaveEvent evt) 
    {
        schemaService.save(currentUser, evt.getSchema());
        updateList();
        closeEditor();
    }//saveSchema

    private HorizontalLayout getToolBar() 
    {
        filterText.setPlaceholder("Filtrar según nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addMetadataButton = new Button("Nuevo esquema", click -> addSchema());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addMetadataButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }//getToolBar

    private void addSchema() 
    {
        grid.asSingleSelect().clear();
        editSchema(new Schema());
    }

    private void configureGrid() 
    {
        grid.addClassName("metadata-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("esquema");
        grid.setColumns("Nombre", "Campos");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> editSchema(evt.getValue()));
    }//configureGrid

    private void editSchema(Schema schema) 
    {
        if (schema == null) 
        {
            closeEditor();
        } else 
        {
            form.setSchema(schema);
            form.setVisible(true);
            addClassName("editing");
        }
    }//editSchema

    private void closeEditor() 
    {
        form.setSchema(null);
        form.setVisible(false);
        removeClassName("editing");
    }//closeEditor

    private void updateList() 
    {
       Optional<String> filter = Optional.of(filterText.getValue());      
       List<Schema>    schemas = schemaService.findAnyMatching(filter);
       grid.setItems(schemas);
    }

}//MetadataSchemaView
