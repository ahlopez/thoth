package com.f.thoth.ui.views.classification;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaToVaadinExporter;
import com.f.thoth.ui.components.Notifier;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class ClassificationValuesForm extends VerticalLayout
{
   private Classification  classification  = null;
   private SchemaValues    schemaValues    = null; 
   private Button save   = new Button("Guardar campos");
   private Button close  = new Button("Cancelar");

   private Component       schemaFields;
   private Schema.Exporter schemaExporter = new SchemaToVaadinExporter();

   public ClassificationValuesForm() 
   {  
      schemaExporter = new SchemaToVaadinExporter();
      setWidthFull();
   }//ClassificationValuesForm

   
   public void setClassification( Classification classification)
   {
      if (classification == null)
         return;
      
      removeAll();
      this.classification  = classification;
      this.schemaValues    = classification.getMetadata();
      Level level          = classification.getLevel();
      if ( level ==  null)
      {
         Notifier.error("No hay un nivel definido");
         return;
      }
      this.schemaFields = (Component)level.getSchema().export(schemaExporter);
      add(
            schemaFields,
            createButtonsLayout()
            );
      startEditing();
   }//setClassification
   
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
       StringBuilder values = new StringBuilder();
       schemaFields.getChildren().forEach( c->  
       {
          int i =0;
          if (c instanceof HasValue<?,?>)
          {  
             Object val = ((HasValue<?,?>)c).getValue();
             if(i++ > 0)
                values.append(Constant.VALUE_SEPARATOR);
             
            values.append(val == null? Constant.NULL_VALUE: val.toString());
          }
       });
       schemaValues.setValues(values.toString());
       classification.setSchemaValues(schemaValues);
       endEditing();
       fireEvent(new SaveEvent(this, classification));
   }//validateAndSave

   
   private Component createButtonsLayout() 
   {
      save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
      close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      save.  setWidth("20%");
      close. setWidth("20%");

      save.addClickListener  (click -> validateAndSave());
      close.addClickListener (click -> close());
      
      HorizontalLayout buttons = new HorizontalLayout( save, close);
      save.getElement().getStyle().set("margin-left", "auto");
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
