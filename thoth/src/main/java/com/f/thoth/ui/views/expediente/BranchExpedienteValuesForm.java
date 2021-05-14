package com.f.thoth.ui.views.expediente;

import java.util.ArrayList;
import java.util.List;

import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaToVaadinExporter;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaValuesToVaadinExporter;
import com.f.thoth.ui.utils.Constant;
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

public class BranchExpedienteValuesForm extends VerticalLayout
{
   private BranchExpediente      branch         = null;
   private SchemaValues          schemaValues   = null;
   private Schema                schema         = null;

   private Component             schemaFields   = null;
   private Schema.Exporter       schemaExporter = new SchemaToVaadinExporter();
   private SchemaValues.Exporter valuesExporter = new SchemaValuesToVaadinExporter();

   private Button          save ;
   private Button          close;

   public BranchExpedienteValuesForm()
   {
      schemaExporter = new SchemaToVaadinExporter();
      valuesExporter = new SchemaValuesToVaadinExporter();
      setWidthFull();
   }//BranchExpedienteValuesForm


   public void setBranchExpediente( BranchExpediente branch)
   {
      if (branch == null)
         return;

      removeAll();
      this.branch          = branch;
      this.schema          = branch.getMetadata().getSchema();
      this.schemaFields    = getFields( branch);

      if (schemaFields != null)
         add( schemaFields);

      add(createButtonsLayout());
      startEditing();
   }//setBranchExpediente


   private Component getFields( BranchExpediente branch)
   {
	  Schema schema = branch.getMetadataSchema();
      this.schemaValues    = branch.getMetadata();
      return  schemaValues == null || SchemaValues.EMPTY.equals(schemaValues)?
    		  schema == null?  new FormLayout()                              :  	      
    	                       (Component)schema.export(schemaExporter)      :
    	                       (Component)schemaValues.export(valuesExporter);
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
       branch.setMetadata(vals);
       endEditing();
       fireEvent(new SaveEvent(this, branch));
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
            values.append(Constant.VALUE_SEPARATOR);

         values.append(val == null? Constant.NULL_VALUE: val.toString());
      }

      return values == null? null: values.toString();

   }//getValuesFromFields



   private Component createButtonsLayout()
   {
      close=  new Button("Cancelar");
      save =  new Button("Guardar expediente");

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
   public static abstract class BranchExpedienteValuesEvent extends ComponentEvent<BranchExpedienteValuesForm>
   {
      private BranchExpediente branch;

      protected BranchExpedienteValuesEvent(BranchExpedienteValuesForm source, BranchExpediente branch)
      {
         super(source, false);
         this.branch = branch;
      }//BranchExpedienteValuesEvent

      public BranchExpediente getBranchExpediente()
      {
         return branch;
      }
   }//BranchExpedienteValuesEvent

   public static class SaveEvent extends BranchExpedienteValuesEvent
   {
      SaveEvent(BranchExpedienteValuesForm source, BranchExpediente branch)
      {
         super(source, branch);
      }
   }//SaveEvent

   public static class CloseEvent extends BranchExpedienteValuesEvent
   {
      CloseEvent(BranchExpedienteValuesForm source)
      {
         super(source, null);
      }
   }//CloseEvent

   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
   {
      return getEventBus().addListener(eventType, listener);
   }//addListener


}//BranchExpedienteValuesForm
