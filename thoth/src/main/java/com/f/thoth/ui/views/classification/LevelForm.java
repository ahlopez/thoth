package com.f.thoth.ui.views.classification;

import java.util.List;

import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.shared.Registration;

public class LevelForm extends VerticalLayout
{
   private Level           level;
   private LevelSchemaForm levelSchemaForm;
   
   private TextField       name;
   private TextField       orden;
   private Schema          levelSchema;

   Binder<Level> binder = new BeanValidationBinder<>(Level.class);
   
   private Button save     = new Button("Guardar nivel");
   private Button delete   = new Button("Eliminar nivel");
   private Button close    = new Button("Cancelar");

   private static final Converter<String, Integer>    LEVEL_ORDER_CONVERTER =
                  new StringToIntegerConverter( 0, "Orden inválido");

   public LevelForm(List<Schema> availableSchemas)
   {
      addClassName("field-form");

      setVisible(false);
      add( new H3("Definición del nivel"));
      
      name  = new TextField("Nombre");
      name.setRequired(true);
      name.setRequiredIndicatorVisible(true);
      name.getElement().setAttribute("colspan", "2");
      name.addValueChangeListener(v-> { if(level != null) level.setName(v.getValue());});
      
      orden = new TextField("Orden");
      orden.setRequired(true);
      orden.setRequiredIndicatorVisible(true);
      orden.getElement().setAttribute("colspan", "1");
      orden.addValueChangeListener(v-> { if(level != null) level.setOrden( Integer.valueOf(v.getValue()));});
      
      ComboBox<Schema> schema = new ComboBox<>("Esquema de metadatos");
      schema.setItems(availableSchemas);
      schema.setItemLabelGenerator(Schema::getName);
      schema.setRequired(true);
      schema.getElement().setAttribute("colspan", "1");
      schema.addValueChangeListener(e ->  
             { 
                level.setSchema(e.getValue());
                editLevel(level);
             });
      
      add(name,
          orden,
          schema);
      
      add( configureButtons());
      add( configureLevelSchemaForm());
      updateLevel();
      closeEditor();

      binder.forField(name) .bind("name");
      binder.forField(orden)
            .withValidator(text -> text.length() > 0, "Orden es un número positivo") //Validación del texto
            .withConverter(LEVEL_ORDER_CONVERTER)
            .withValidator(o -> o > 0, "El orden es un número positivo")             // Validación del número
            .bind("orden");

   }//LevelForm

   public void setLevel( Level level)
   {
      this.level = level;
      updateLevel();
   }//setSchema

   private Component configureLevelSchemaForm()
   {
      levelSchemaForm = new LevelSchemaForm(levelSchema);
      levelSchemaForm.addListener(LevelSchemaForm.SaveEvent.class,   this::saveSchema);
      levelSchemaForm.addListener(LevelSchemaForm.CloseEvent.class,  e -> closeEditor());
      return levelSchemaForm;
   }//configureLevelSchemaForm


   private void saveSchema(LevelSchemaForm.SaveEvent event)
   {
      closeEditor();
      updateLevel();
   }//saveSchema

   private void editLevel(Level level)
   {
      if (level == null)
      {
         closeEditor();
      } else
      {
         levelSchemaForm.setLevel(level);
         levelSchemaForm.setVisible(true);
         addClassName("editing");
      }
   }//editLevel

   private void closeEditor()
   {
      levelSchemaForm.setLevel(null);
      levelSchemaForm.setVisible(false);
      removeClassName("editing");
   }//closeEditor

   private void updateLevel()
   {
      if (level != null)
      {
          name.setValue(level.getName());
          orden.setValue(level.getOrden().toString());
      }
   }//updateLevel

   private Component configureButtons()
   {
      save.    addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      delete.  addThemeVariants(ButtonVariant.LUMO_ERROR);
      close.   addThemeVariants(ButtonVariant.LUMO_TERTIARY);
      
      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      save.addClickListener  (click -> fireEvent(new SaveEvent  (this, level)));
      delete.addClickListener(click -> fireEvent(new DeleteEvent(this, level)));
      close.addClickListener (click -> fireEvent(new CloseEvent (this)));
      
      save.getElement().getStyle().set("margin-left", "auto");

      HorizontalLayout buttons = new HorizontalLayout();
      buttons.setWidthFull();
      buttons.setPadding(true);
      buttons.add( save, delete, close);
      return buttons;
   }//configureButtons



   // --------------------- Events -----------------------
   public static abstract class LevelFormEvent extends ComponentEvent<LevelForm>
   {
      private Level level;

      protected LevelFormEvent(LevelForm source, Level level)
      {
         super(source, false);
         this.level = level;
      }//LevelFormEvent

      public Level getLevel()
      {
         return level;
      }
   }//LevelFormEvent

   public static class SaveEvent extends LevelFormEvent
   {
      SaveEvent(LevelForm source, Level level)
      {
         super(source, level);
      }
   }//SaveEvent

   public static class DeleteEvent extends LevelFormEvent
   {
      DeleteEvent(LevelForm source, Level level)
      {
         super(source, level);
      }
   }//DeleteEvent

   public static class CloseEvent extends LevelFormEvent
   {
      CloseEvent(LevelForm source)
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener


}//LevelForm
