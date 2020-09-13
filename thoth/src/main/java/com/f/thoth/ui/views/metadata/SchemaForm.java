package com.f.thoth.ui.views.metadata;

import java.util.List;

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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class SchemaForm extends VerticalLayout
{
   private Schema        schema;
   private Grid<Field>   fieldGrid;
   private FieldForm     fieldForm;

   private Button save   = new Button("Guardar esquema");
   private Button delete = new Button("Eliminar esquema");
   private Button close  = new Button("Cancelar");


   public SchemaForm(List<Metadata> availableMetadata)
   {
      add( configureFieldGrid());
      add( configureFieldForm(availableMetadata));
      add( configureButtons());
      updateList();
      closeEditor();

   }//SchemaForm

   public void setSchema( Schema schema) { this.schema = schema;}

   private Component configureFieldGrid()
   {
      fieldGrid = new Grid<>();
      fieldGrid.addClassName("selector-list");
      fieldGrid.setVisible(false);
      fieldGrid.setWidthFull();
      fieldGrid.addColumn(Field::getName)     .setHeader("Nombre")      .setFlexGrow(40);
      fieldGrid.addColumn(Field::isVisible)   .setHeader("Visible")     .setFlexGrow(15);
      fieldGrid.addColumn(Field::isReadOnly)  .setHeader("Solo lectura").setFlexGrow(15);
      fieldGrid.addColumn(Field::isRequired)  .setHeader("Requerido")   .setFlexGrow(15);
      fieldGrid.addColumn(Field::getSortOrder).setHeader("Orden")       .setFlexGrow(15);
      fieldGrid.setSelectionMode(Grid.SelectionMode.SINGLE);   
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


   private void deleteField(FieldForm.DeleteEvent event) 
   {
      schema.deleteField(event.getField());
      updateList();
      closeEditor();
   }//deleteSchema

   private void saveField(FieldForm.SaveEvent event) 
   {
      schema.addField(event.getField());
      updateList();
      closeEditor();
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
   }//editSchema

   private void closeEditor() 
   {
      fieldForm.setField(null);
      fieldForm.setVisible(false);
      removeClassName("editing");
   }//closeEditor

   private void updateList() 
   {
      if (schema != null)
          fieldGrid.setItems(schema.getMetadata());
   }

   private Component configureButtons() 
   {
      save.  addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
      close. addThemeVariants(ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      save.addClickListener  (click -> fireEvent(new SaveEvent  (this, schema)));
      delete.addClickListener(click -> fireEvent(new DeleteEvent(this, schema)));
      close.addClickListener (click -> fireEvent(new CloseEvent (this)));

      return new HorizontalLayout(save, delete, close);
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
