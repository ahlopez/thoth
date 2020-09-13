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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class FieldForm extends FormLayout 
{
    private TextField          campo    = new TextField("Campo");
    private ComboBox<Metadata> metadata = new ComboBox<>("Metadato");

    private Button save   = new Button("Guardar");
    private Button delete = new Button("Eliminar");
    private Button close  = new Button("Cancelar");

    Binder<Field> binder = new BeanValidationBinder<>(Field.class);

    public FieldForm(List<Metadata> availableMetadata) 
    {
        addClassName("field-form");
        metadata.setItems(availableMetadata);
        metadata.setItemLabelGenerator(Metadata::getName);

        binder.bindInstanceFields(this);

        add(
            campo,
            metadata,
            createButtonsLayout()
        );
    }//FieldForm

    public void setField(Field field) 
    {
        binder.setBean(field);
    }

    private Component createButtonsLayout() 
    {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut (Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener  (click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener (click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
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

