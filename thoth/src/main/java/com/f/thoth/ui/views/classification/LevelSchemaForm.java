package com.f.thoth.ui.views.classification;

import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
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

public class LevelSchemaForm extends VerticalLayout
{

   private Level  level  = null;
   private Button save   = new Button("Guardar campos");
   private Button close  = new Button("Cancelar");

   private Component       schemaFields;
   private Schema.Exporter schemaExporter = new SchemaToVaadinExporter();

   public LevelSchemaForm(Schema schema) 
   {  
      schemaExporter = new SchemaToVaadinExporter();
   }//LevelSchemaForm
   
   public void setLevel( Level level)
   {
      if (level == null)
         return;
      
      removeAll();
      this.level        = level;
      this.schemaFields = (Component)level.getSchema().export(schemaExporter);
      add(
            schemaFields,
            createButtonsLayout()
            );
   }//setLevel

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
       fireEvent(new SaveEvent(this, level, values.toString()));
   }//validateAndSave
   
   private void close()
   {
      fireEvent(new CloseEvent(this));
   }//close   
   

   // --------------------- Events -----------------------
   public static abstract class LevelSchemaFormEvent extends ComponentEvent<LevelSchemaForm> 
   {
      private Level level;

      protected LevelSchemaFormEvent(LevelSchemaForm source, Level level) 
      {
         super(source, false);
         this.level = level;
      }//LevelSchemaFormEvent

      public Level getLevel() 
      {
         return level;
      }
   }//LevelSchemaFormEvent

   public static class SaveEvent extends LevelSchemaFormEvent 
   {
      private String values;
      SaveEvent(LevelSchemaForm source, Level level, String values) 
      {
         super(source, level);
         this.values = values;
      }
      public String getValues() { return values;}
   }//SaveEvent

   public static class CloseEvent extends LevelSchemaFormEvent 
   {
      CloseEvent(LevelSchemaForm source) 
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) 
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener

}//LevelForm
