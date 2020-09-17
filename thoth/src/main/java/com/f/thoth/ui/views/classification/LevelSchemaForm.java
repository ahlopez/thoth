package com.f.thoth.ui.views.classification;

import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaToVaadinExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

public class LevelSchemaForm extends FormLayout
{

   private Level  level  = null;
   private Button save   = new Button("Guardar campos");
   private Button close  = new Button("Cancelar");

   private Schema.Exporter schemaExporter = new SchemaToVaadinExporter();

   public LevelSchemaForm(Schema schema) 
   {  
      addClassName("field-form");
      setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3));

      H3  title = new H3("Campos a actualizar");
      title.getElement().setAttribute("colspan", "2");

      schemaExporter = new SchemaToVaadinExporter();

   }//LevelForm
   
   public void setLevel( Level level)
   {
      if (level == null)
         return;
      
      this.level = level;
      Component schemaFields = (Component)level.getSchema().export(schemaExporter);

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

      save.  setWidth("40%");
      close. setWidth("20%");

      save.addClickListener  (click -> validateAndSave());
      close.addClickListener (click -> fireEvent(new CloseEvent(this)));
      
      Label space = new Label(" ");
      space.setWidthFull();
      HorizontalLayout buttons = new HorizontalLayout(space, save, close);
      buttons.getElement().setAttribute("colspan", "3");

      return buttons; 
   }//createButtonsLayout

   private void validateAndSave() 
   {
         fireEvent(new SaveEvent(this, level));
   }//validateAndSave

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
      SaveEvent(LevelSchemaForm source, Level level) 
      {
         super(source, level);
      }
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
