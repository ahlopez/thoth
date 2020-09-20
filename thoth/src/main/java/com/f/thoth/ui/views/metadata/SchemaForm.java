package com.f.thoth.ui.views.metadata;

import java.util.List;
import java.util.Set;

import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class SchemaForm extends VerticalLayout
{
   private Schema        schema;
   private Grid<Field>   fieldGrid;
   private FieldForm     fieldForm;
   private TextField     schemaName  = new TextField();


   private Button save     = new Button("Guardar esquema");
   private Button delete   = new Button("Eliminar esquema");
   private Button close    = new Button("Cancelar");
   private Button newField = new Button("+ Nuevo campo", click -> addField());


   public SchemaForm(List<Metadata> availableMetadata)
   {
      setVisible(false);
      H3 title = new H3("Esquema");
      HorizontalLayout titles =  new HorizontalLayout(title, schemaName, newField);
      titles.setWidthFull();
      titles.setPadding(true);
      titles.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
      
      schemaName.addValueChangeListener(e-> schema.setName(e.getValue()));
      schemaName.getElement().getStyle().set("margin-right", "auto");
      add( titles);
      add( configureFieldGrid());
      add( configureButtons());
      add( configureFieldForm(availableMetadata));
      updateList();
      closeEditor();

   }//SchemaForm

   public void setSchema( Schema schema)
   {
      this.schema = schema;
      updateList();
   }//setSchema

   private Component configureFieldGrid()
   {
      fieldGrid = new Grid<>();
      fieldGrid.setWidthFull();
      fieldGrid.addColumn(Field::getName)     .setHeader("Nombre")      .setFlexGrow(40);
      fieldGrid.addColumn(Field::isVisible)   .setHeader("Visible")     .setFlexGrow(12);
      fieldGrid.addColumn(Field::isReadOnly)  .setHeader("Solo lectura").setFlexGrow(12);
      fieldGrid.addColumn(Field::isRequired)  .setHeader("Requerido")   .setFlexGrow(12);
      fieldGrid.addColumn(Field::getSortOrder).setHeader("Orden")       .setFlexGrow(12);
      fieldGrid.addColumn(Field::getSortOrder).setHeader("Columnas")    .setFlexGrow(12);
      fieldGrid.asSingleSelect().addValueChangeListener(e-> editField(e.getValue()));
      return fieldGrid;
   }//configureFieldGrid

   private Component configureFieldForm(List<Metadata> availableMetadata)
   {
      fieldForm = new FieldForm(availableMetadata);
      fieldForm.addListener(FieldForm.SaveEvent.class,   this::saveField);
      fieldForm.addListener(FieldForm.DeleteEvent.class, this::deleteField);
      fieldForm.addListener(FieldForm.CloseEvent.class,  e -> closeEditor());
      return fieldForm;
   }//configureFieldForm


   private void addField()
   {
      Field nuevo = new Field();
      nuevo.setName("Nombre");
      editField(nuevo);
   }//addField


   private void deleteField(FieldForm.DeleteEvent event)
   {
      schema.deleteField(event.getField());
      closeEditor();
      updateList();
   }//deleteSchema

   private void saveField(FieldForm.SaveEvent event)
   {
      schema.addField(event.getField());
      closeEditor();
      updateList();
   }//saveSchema

   private void editField(Field field)
   {
      if (field == null)
      {
         closeEditor();
      } else
      {
         fieldForm.setField(field);
         fieldForm.setVisible(true);
         addClassName("editing");
      }
   }//editField

   private void closeEditor()
   {
      fieldForm.setField(null);
      fieldForm.setVisible(false);
      removeClassName("editing");
   }//closeEditor

   private void updateList()
   {
      if (schema != null)
      {
      //    setVisible(true);
          Set<Field> fields = schema.getFields();
          fieldGrid.setItems(fields);
          schemaName.setValue(schema.getName());
      }
   }//updateList

   private Component configureButtons()
   {
      save.    addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      delete.  addThemeVariants(ButtonVariant.LUMO_ERROR);
      close.   addThemeVariants(ButtonVariant.LUMO_TERTIARY);
      newField.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      
      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      save.addClickListener  (click -> fireEvent(new SaveEvent  (this, schema)));
      delete.addClickListener(click -> fireEvent(new DeleteEvent(this, schema)));
      close.addClickListener (click -> fireEvent(new CloseEvent (this)));
      
      save.getElement().getStyle().set("margin-left", "auto");

      HorizontalLayout buttons = new HorizontalLayout();
      buttons.setWidthFull();
      buttons.setPadding(true);
      buttons.add( save, delete, close);
      return buttons;
   }//configureButtons



   // --------------------- Events -----------------------
   public static abstract class SchemaFormEvent extends ComponentEvent<SchemaForm>
   {
      private Schema schema;

      protected SchemaFormEvent(SchemaForm source, Schema schema)
      {
         super(source, false);
         this.schema = schema;
      }//SchemaFormEvent

      public Schema getSchema()
      {
         return schema;
      }
   }//SchemaFormEvent

   public static class SaveEvent extends SchemaFormEvent
   {
      SaveEvent(SchemaForm source, Schema schema)
      {
         super(source, schema);
      }
   }//SaveEvent

   public static class DeleteEvent extends SchemaFormEvent
   {
      DeleteEvent(SchemaForm source, Schema schema)
      {
         super(source, schema);
      }
   }//DeleteEvent

   public static class CloseEvent extends SchemaFormEvent
   {
      CloseEvent(SchemaForm source)
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener


}//SchemaForm
