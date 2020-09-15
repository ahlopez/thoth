package com.f.thoth.ui.views.metadata;

import java.util.List;

import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.shared.Registration;

public class FieldForm extends FormLayout 
{
   private static final Converter<String, Integer>    SORT_ORDER_CONVERTER =
         new StringToIntegerConverter( 0, "Orden inválido");

   private Button save   = new Button("Guardar Campo");
   private Button delete = new Button("Eliminar Campo");
   private Button close  = new Button("Cancelar");

   Binder<Field> binder = new BeanValidationBinder<>(Field.class);

   public FieldForm(List<Metadata> availableMetadata) 
   {  
      addClassName("field-form");
      setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3));
      
      H3  title = new H3("Campo a actualizar");
      title.getElement().setAttribute("colspan", "2");
      
      TextField  campo    = new TextField("Nombre");
      campo.setRequired(true);
      campo.setRequiredIndicatorVisible(true);
      campo.getElement().setAttribute("colspan", "2");
      
      ComboBox<Metadata> metadata = new ComboBox<>("Metadato");
      metadata.setItems(availableMetadata);
      metadata.setItemLabelGenerator(Metadata::getName);
      metadata.setRequired(true);
      metadata.getElement().setAttribute("colspan", "1");
      
      Checkbox visible    = new Checkbox("Visible");
      Checkbox readOnly   = new Checkbox("Solo lectura");
      Checkbox required   = new Checkbox("Requerido");
      TextField sortOrder = new TextField("Orden");
      sortOrder.setRequired(true);
      sortOrder.setRequiredIndicatorVisible(true);

       add(
            title,
            campo,
            metadata,
            visible,
            readOnly,
            required,
            sortOrder,
            createButtonsLayout()
            );
       
       binder.forField(campo)    .bind("name");
       binder.forField(metadata) .bind("metadata");
       binder.forField(visible)  .bind("visible");
       binder.forField(readOnly) .bind("readOnly");
       binder.forField(required) .bind("required");
       binder.forField(sortOrder)
             .withValidator(text -> text.length() > 0, "Orden es un número positivo") //Validación del texto
             .withConverter(SORT_ORDER_CONVERTER)
             .withValidator(order -> order >= 0, "El orden es un número positivo")    // Validación del número
             .bind("sortOrder");


   }//FieldForm

   public void setField(Field field) 
   {
      binder.setBean(field);
   }

   private Component createButtonsLayout() 
   {
      save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
      delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
      close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);
      
      save.  setWidth("40%");
      delete.setWidth("40%");
      close. setWidth("20%");

      save.addClickListener  (click -> validateAndSave());
      delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
      close.addClickListener (click -> fireEvent(new CloseEvent(this)));

      binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));
      Label space = new Label(" ");
      space.setWidthFull();
      HorizontalLayout buttons = new HorizontalLayout(space, save, delete, close);
      buttons.getElement().setAttribute("colspan", "3");

      return buttons; 
   }//createButtonsLayout

   private void validateAndSave() 
   {
      if (binder.isValid()) 
      {
         fireEvent(new SaveEvent(this, binder.getBean()));
      }
   }//validateAndSave

   // --------------------- Events -----------------------
   public static abstract class FieldFormEvent extends ComponentEvent<FieldForm> 
   {
      private Field field;

      protected FieldFormEvent(FieldForm source, Field field) 
      {
         super(source, false);
         this.field = field;
      }//FieldFormEvent

      public Field getField() 
      {
         return field;
      }
   }//FieldFormEvent

   public static class SaveEvent extends FieldFormEvent 
   {
      SaveEvent(FieldForm source, Field field) 
      {
         super(source, field);
      }
   }//SaveEvent

   public static class DeleteEvent extends FieldFormEvent 
   {
      DeleteEvent(FieldForm source, Field field) 
      {
         super(source, field);
      }
   }//DeleteEvent

   public static class CloseEvent extends FieldFormEvent 
   {
      CloseEvent(FieldForm source) 
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) 
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener

}//FieldForm

