package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues.Exporter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class SchemaValuesToVaadinExporter implements Exporter
{
   private SchemaToVaadinExporter schemaExporter;
   private Component              exportedSchema;
   
   public SchemaValuesToVaadinExporter()
   {
      schemaExporter = new SchemaToVaadinExporter();
   }//SchemaValuesToVaadinExporter constructor

   @Override
   public void initExport()
   {
      exportedSchema = null;
   }//initExport

   @Override
   public void exportSchema(Schema schema)
   {
      if (schema == null)
         throw new IllegalArgumentException("Esquema asociado a los valores a exportar no puede ser nulo");
      
      exportedSchema = (Component)schema.export(schemaExporter);
   }//exportSchema

   @Override
   public void exportValues(String valores)
   {
      if ( valores == null)
         return;
      
      List<HasValue<?,?>> fields = new ArrayList<>();
      exportedSchema.getChildren().forEach( fld -> 
      {
         if ( fld instanceof HasValue)
            fields.add( (HasValue<?,?>)fld);            
      });
      
      String[] vals = valores == null || valores.length()== 0
                    ? new String[0]
                    : valores.split(Parm.VALUE_SEPARATOR);
      if (vals.length != fields.size())
         throw new IllegalStateException("Valores a exportar["+ vals.length+ "] diferentes a campos a recibirlos["+ fields.size()+ "]");

      int i= 0;
      for ( HasValue<?,?> field: fields)
      {  String value = vals[i++];
         if ( !Parm.NULL_VALUE.equals(value))
            setValue( field, value);
      }
      
   }//exportValues

   @Override
   public void endExport() { }

   @Override
   public Object getProduct() { return exportedSchema; }
   
   
   @SuppressWarnings("unchecked")
   private void setValue( HasValue<?,?> field, String value)
   {
      if ( field instanceof TextField)
         ((TextField)field).setValue(value);
      else if (field instanceof DateTimePicker)
      {
         String      stringDate = value.trim().toLowerCase();
         LocalDateTime dateTime = (TextUtil.isEmpty(value) || stringDate.equals("now"))
                                ? LocalDateTime.now() 
                                : stringDate.equals("endOfTimes")
                                ? Parm.END_OF_TIMES
                                : LocalDateTime.parse(value);
        ((DateTimePicker)field).setValue(dateTime);
      }
      else if( field instanceof Checkbox)
      {
         boolean boolValue = TextUtil.boolValue(value);
         ((Checkbox)field).setValue(boolValue);
      }
      else if( field instanceof TextArea)
         ((TextArea)field).setValue(value);
      else if (field instanceof ComboBox<?> )
         ((ComboBox<String>)field).setValue(value);
      
   }//setValue

}//SchemaValuesToVaadinExporter
