package com.f.thoth.ui.views.classification;

import java.util.ArrayList;
import java.util.List;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaToVaadinExporter;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaValuesToVaadinExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class ClassificationValuesForm extends VerticalLayout
{
   private Classification        classification = null;
   private SchemaValues          schemaValues   = null;
   private Schema                schema         = null;

   private Component             schemaFields   = null;
   private Schema.Exporter       schemaExporter = new SchemaToVaadinExporter();
   private SchemaValues.Exporter valuesExporter = new SchemaValuesToVaadinExporter();

   private Button          save ;
   private Button          close;

   public ClassificationValuesForm()
   {
      schemaExporter = new SchemaToVaadinExporter();
      valuesExporter = new SchemaValuesToVaadinExporter();
      setWidthFull();
   }//ClassificationValuesForm


   public void setClassification( Classification classification)
   {
      if (classification == null)
         return;

      removeAll();
      this.classification  = classification;
      this.schema          = classification.getLevel().getSchema();
      this.schemaFields    = getFields( classification);

      if (schemaFields != null)
         add( schemaFields);

      add(createButtonsLayout());
      startEditing();
   }//setClassification


   private Component getFields( Classification classification)
   {
      this.schemaValues    = classification.getMetadata();
      if ( schemaValues != null)
          return (Component)schemaValues.export(valuesExporter);

      Level level  = classification.getLevel();
      return ( level !=  null)? (Component)level.getSchema().export(schemaExporter): new FormLayout();

   }//getFields


   private void startEditing()
   {
      addClassName  ("field-form");
      setVisible(true);
   }//startEditing


   private void endEditing()
   {
      setVisible(false);
      removeClassName("field-form");
   }//endEditing


   private void validateAndSave()
   {
       String values = getValuesFromFields(schemaFields);
       SchemaValues vals = new SchemaValues(schema, values);
       classification.setMetadata(vals);
       endEditing();
       fireEvent(new SaveEvent(this, classification));
   }//validateAndSave

   private String getValuesFromFields( Component schemaFields)
   {
      List<Component> fields = new ArrayList<>();
      schemaFields.getChildren().forEach( c ->
      {
       if (c instanceof HasValue<?,?>)
          fields.add(c);
      });

      int i= 0;
      StringBuilder values = null;
      for (Component field: fields)
      {
         Object val = ((HasValue<?,?>)field).getValue();
         if(i++ == 0)
            values = new StringBuilder();
         else
            values.append(Parm.VALUE_SEPARATOR);

         values.append(val == null? Parm.NULL_VALUE: val.toString());
      }

      return values == null? null: values.toString();

   }//getValuesFromFields



   private Component createButtonsLayout()
   {
      close=  new Button("Cancelar");
      save =  new Button("Guardar clase");

      close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);
      save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);

      close.addClickShortcut(Key.ESCAPE);
      save.addClickShortcut (Key.ENTER);

      close. setWidth("20%");
      save.  setWidth("20%");

      close.addClickListener (click -> close());
      save .addClickListener (click -> validateAndSave());
      save.getElement().getStyle().set("margin-left", "auto");

      HorizontalLayout buttons = new HorizontalLayout( close, save);
      buttons.setWidthFull();

      return buttons;
   }//createButtonsLayout


   private void close()
   {
      endEditing();
      fireEvent(new CloseEvent(this));
   }//close


   // --------------------- Events -----------------------
   public static abstract class ClassificationValuesEvent extends ComponentEvent<ClassificationValuesForm>
   {
      private Classification classification;

      protected ClassificationValuesEvent(ClassificationValuesForm source, Classification classification)
      {
         super(source, false);
         this.classification = classification;
      }//ClassificationValuesEvent

      public Classification getClassification()
      {
         return classification;
      }
   }//ClassificationValuesEvent

   public static class SaveEvent extends ClassificationValuesEvent
   {
      SaveEvent(ClassificationValuesForm source, Classification classification)
      {
         super(source, classification);
      }
   }//SaveEvent

   public static class CloseEvent extends ClassificationValuesEvent
   {
      CloseEvent(ClassificationValuesForm source)
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener

}//ClassificationValuesForm
