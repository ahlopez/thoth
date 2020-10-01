package com.f.thoth.ui.views.metadata;

import java.util.List;

import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
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
import com.vaadin.flow.shared.Registration;

public class DocumentTypeForm extends FormLayout
{
   private Button save   = new Button("Guardar Tipo");
   private Button close  = new Button("Cancelar");

   Binder<DocumentType> binder       = new BeanValidationBinder<>(DocumentType.class);

   public DocumentTypeForm(List<Schema> availableSchemas) 
   {  
      setWidthFull();
      setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3));

      H3  title = new H3("Tipo a actualizar");
      title.getElement().setAttribute("colspan", "2");

      TextField  tipo    = new TextField("Nombre");
      tipo.setRequired(true);
      tipo.setRequiredIndicatorVisible(true);
      tipo.getElement().setAttribute("colspan", "2");
      
      Checkbox requiresContent = new Checkbox("Requiere contenido");
      requiresContent.setRequiredIndicatorVisible(true);
      requiresContent.setWidth("40%");
      requiresContent.getElement().setAttribute("colspan", "1");
      
      ComboBox<Schema> schema = new ComboBox<>("Esquema");
      schema.setItems(availableSchemas);
      schema.setItemLabelGenerator(Schema::getName);
      schema.setRequired(true);
      schema.getElement().setAttribute("colspan", "2");
   
      add(
            title,
            tipo,
            new Label(" "),
            requiresContent,
            schema,
            createButtonsLayout()
            );

      binder.forField(tipo).bind("name");
      binder.forField(requiresContent).bind("requiresContent");
      binder.forField(schema).bind("schema");

   }//DocumentTypeForm
   

   public void setDocumentType(DocumentType tipo) 
   {
      binder.setBean(tipo);
   }//setDocumentType

   private Component createButtonsLayout() 
   {
      save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
      close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);

      save.addClickShortcut (Key.ENTER);
      close.addClickShortcut(Key.ESCAPE);

      save.  setWidth("20%");
      save.getElement().getStyle().set("margin-left", "auto");
      close. setWidth("20%");

      save.addClickListener  (click -> validateAndSave(binder.getBean()));
      close.addClickListener (click -> { fireEvent(new CloseEvent(this));});

      binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));
      HorizontalLayout buttons = new HorizontalLayout(close, save);
      buttons.getElement().setAttribute("colspan", "3");

      return buttons; 
   }//createButtonsLayout

   private void validateAndSave(DocumentType tipo) 
   {
      if (binder.isValid()) 
         fireEvent(new SaveEvent(this, tipo));

   }//validateAndSave

   // --------------------- Events -----------------------
   public static abstract class DocumentTypeFormEvent extends ComponentEvent<DocumentTypeForm> 
   {
      private DocumentType tipo;

      protected DocumentTypeFormEvent(DocumentTypeForm source, DocumentType tipo) 
      {
         super(source, false);
         this.tipo = tipo;
      }//DocumentTypeFormEvent

      public DocumentType getDocumentType() 
      {
         return tipo;
      }
   }//DocumentTypeFormEvent

   public static class SaveEvent extends DocumentTypeFormEvent 
   {
      SaveEvent(DocumentTypeForm source, DocumentType tipo) 
      {
         super(source, tipo);
      }
   }//SaveEvent

   public static class CloseEvent extends DocumentTypeFormEvent 
   {
      CloseEvent(DocumentTypeForm source) 
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) 
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener

}//DocumentTypeForm
