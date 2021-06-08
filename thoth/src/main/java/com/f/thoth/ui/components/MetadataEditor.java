package com.f.thoth.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaToVaadinExporter;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaValuesToVaadinExporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MetadataEditor extends VerticalLayout
{
   private Schema                schema         = null;
   private SchemaValues          schemaValues   = null;
   private Component             schemaFields   = null;
   private Schema.Exporter       schemaExporter = new SchemaToVaadinExporter();
   private SchemaValues.Exporter valuesExporter = new SchemaValuesToVaadinExporter();

   public MetadataEditor()
   {
      schemaExporter = new SchemaToVaadinExporter();
      valuesExporter = new SchemaValuesToVaadinExporter();
      setWidthFull();
   }//MetadataEditor


   public void editMetadata( Schema schema, SchemaValues values)
   {
      removeAll();
      add(getEditor( schema, values));
   }//editMetadata



   public Component getEditor( Schema schema, SchemaValues values)
   {
      this.schema       = schema;
      this.schemaValues = values;
      this.schemaFields = values != null && !SchemaValues.EMPTY.equals(values) 
                                 ? (Component)values.export(valuesExporter)
                                 :  schema == null
                                 ?  new FormLayout() 
                                 : (Component)schema.export(schemaExporter);

      return schemaFields;
   }//getEditor


   private void endEditing()
   {
      this.schema       = null;
      this.schemaFields = null;
      setVisible(false);
      removeClassName("field-form");
   }//endEditing


   public SchemaValues validateAndSave()
   {  
      String sVals =  schemaFields == null? null : getValuesFromFields(schemaFields);
      if (schemaValues == null)
      {  schemaValues = new SchemaValues(schema, sVals );
      }else
      {  schemaValues.setValues(sVals);
      }
      endEditing();
      return schemaValues;
   }//validateAndSave


   private String getValuesFromFields( Component schemaFields)
   {
      if ( schemaFields == null)
         return null;

      List<Component> fields = new ArrayList<>();
      schemaFields.getChildren().forEach( c ->
      {
         if (c instanceof HasValue<?,?>)
            fields.add(c);
      });

      int i= 0;
      StringBuilder values = new StringBuilder();
      for (Component field: fields)
      {
         if (i++ > 0)
         {  values.append(Parm.VALUE_SEPARATOR);
         }
         
         Object  val = ((HasValue<?,?>)field).getValue();
         values.append( val == null? Parm.NULL_VALUE: val.toString());
      }

      return values == null? null: values.toString();

   }//getValuesFromFields

}//MetadataEditor

