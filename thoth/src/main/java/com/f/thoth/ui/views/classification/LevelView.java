package com.f.thoth.ui.views.classification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.service.LevelService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.MainView;
import com.f.thoth.ui.crud.AbstractEvidentiaCrudView;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


/**
 * View para mantenimiento de los niveles del esquema de clasificación
 */
@Route(value = Constant.PAGE_NIVELES, layout = MainView.class)
@PageTitle(Constant.TITLE_NIVELES)
@Secured(com.f.thoth.backend.data.Role.ADMIN)
public class LevelView extends AbstractEvidentiaCrudView<Level>
{
   private static final Converter<String, Integer>    LEVEL_ORDER_CONVERTER =
         new StringToIntegerConverter( 0, "Orden inválido");

   @Autowired
   public LevelView(LevelService service, SchemaService schemaService, CurrentUser currentUser) 
   {
      super(Level.class, service, new Grid<>(), createForm(schemaService.findAll()), currentUser);    
      
   }//LevelView

   @Override
   protected void setupGrid(Grid<Level> grid)
   {
      grid.addColumn(level -> level.getName().toLowerCase()).setHeader("Nombre").setFlexGrow(40);
      grid.addColumn(level -> level.getOrden()).setHeader("Número de orden").setFlexGrow(20);
      grid.addColumn(level -> 
      {
         Schema schema = level.getSchema();
         return schema == null || schema.getName() == null? "---" : schema.getName().toLowerCase();         
      }).setHeader("Esquema de Metadatos").setFlexGrow(30);

   }//setupGrid

   @Override
   protected String getBasePage() { return Constant.PAGE_NIVELES; }

   private static BinderCrudEditor<Level> createForm(List<Schema>availableSchemas) 
   {
      TextField name = new TextField("Nombre del nivel");
      name.getElement().setAttribute("colspan", "3");
      name.setRequired(true);
      name.setRequiredIndicatorVisible(true);
      
      TextField orden = new TextField("Número de orden");
      orden.getElement().setAttribute("colspan", "1");
      orden.setRequired(true);
      orden.setRequiredIndicatorVisible(true);

      ComboBox<Schema> schema = new ComboBox<>("Esquema de metadatos");
      schema.setItems(availableSchemas);
      schema.setItemLabelGenerator(Schema::getName);
      schema.setRequired(true);
      schema.setRequiredIndicatorVisible(true);
      schema.getElement().setAttribute("colspan", "1");

      FormLayout form = new FormLayout(name, orden, schema);

      BeanValidationBinder<Level> binder = new BeanValidationBinder<>(Level.class);

      binder.bind(name, "name");
      binder.forField(orden)
            .withValidator(text -> text.length() > 0, "Orden es un número positivo") //Validación del texto
            .withConverter(LEVEL_ORDER_CONVERTER)
            .withValidator(o -> o > 0, "El orden es un número positivo")             // Validación del número
            .bind("orden");
      binder.bind(schema,"schema");

      return new BinderCrudEditor<Level>(binder, form);
   }//BinderCrudEditor

}//LevelView

