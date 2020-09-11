package com.f.thoth.ui.views.properties;

import java.util.List;

import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
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

public class SchemaForm extends FormLayout 
{
    private TextField          name     = new TextField("Nombre");
    private ComboBox<Metadata> metadata = new ComboBox<>("Metadatos");

    Button save   = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close  = new Button("Cancelar");

    Binder<Schema> binder = new BeanValidationBinder<>(Schema.class);

    public SchemaForm(List<Metadata> availableMetadata) 
    {
        addClassName("metadata-form");

        binder.bindInstanceFields(this);
        metadata.setItems(availableMetadata);
        metadata.setItemLabelGenerator(Metadata::getName);

        add(
            name,
            metadata,
            createButtonsLayout()
        );
    }//SchemaForm

    public void setSchema(Schema metadata) 
    {
        binder.setBean(metadata);
    }

    private Component createButtonsLayout() 
    {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click   -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(click  -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }//createButtonsLayout

    private void validateAndSave() 
    {
        if (binder.isValid()) 
        {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }//SchemaFormEvent

    // Events
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

