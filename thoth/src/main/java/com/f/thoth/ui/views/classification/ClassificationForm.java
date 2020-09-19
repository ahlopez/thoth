package com.f.thoth.ui.views.classification;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaToVaadinExporter;
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

public class ClassificationForm extends VerticalLayout
{

   private Classification  classification  = null;
   private Button save   = new Button("Guardar campos");
   private Button close  = new Button("Cancelar");

   private Component       schemaFields;
   private Schema.Exporter schemaExporter = new SchemaToVaadinExporter();

   public ClassificationForm(Schema schema) 
   {  
      schemaExporter = new SchemaToVaadinExporter();
   }//ClassificationForm
   
   public void setClassification( Classification classification)
   {
      if (classification == null)
         return;
      
      removeAll();
      this.classification   = classification;
      SchemaValues schemaValues = classification.getMetadata();
      Schema       classificationSchema = schemaValues.getSchema();
      this.schemaFields = (Component)classificationSchema.export(schemaExporter);
      add(
            schemaFields,
            createButtonsLayout()
            );
   }//setClassification

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
   
   private void validateAndSave() 
   {
       StringBuilder values = new StringBuilder();
       schemaFields.getChildren().forEach( c->  
       {
          if (c instanceof HasValue<?,?>)
          {  
             Object val = ((HasValue<?,?>)c).getValue();
             values.append(val == null? " ": val.toString());
             values.append(Constant.VALUE_SEPARATOR);
          }
       });
       fireEvent(new SaveEvent(this, classification, values.toString()));
   }//validateAndSave
   
   private void close()
   {
      fireEvent(new CloseEvent(this));
   }//close   
   

   // --------------------- Events -----------------------
   public static abstract class ClassificationFormEvent extends ComponentEvent<ClassificationForm> 
   {
      private Classification classification;

      protected ClassificationFormEvent(ClassificationForm source, Classification classification) 
      {
         super(source, false);
         this.classification = classification;
      }//ClassificationFormEvent

      public Classification getClassification() 
      {
         return classification;
      }
   }//ClassificationFormEvent

   public static class SaveEvent extends ClassificationFormEvent 
   {
      private String values;
      SaveEvent(ClassificationForm source, Classification classification, String values) 
      {
         super(source, classification);
         this.values = values;
      }
      public String getValues() { return values;}
   }//SaveEvent

   public static class CloseEvent extends ClassificationFormEvent 
   {
      CloseEvent(ClassificationForm source) 
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) 
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener

}//ClassificationForm