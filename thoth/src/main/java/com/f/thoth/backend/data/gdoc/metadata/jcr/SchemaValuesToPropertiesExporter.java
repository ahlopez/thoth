package com.f.thoth.backend.data.gdoc.metadata.jcr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Property;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;

public class SchemaValuesToPropertiesExporter implements SchemaValues.Exporter
{
   Set<Field>  fields;
   String[]    values;
   List<Property> properties;
   
   public SchemaValuesToPropertiesExporter () { }
   
   @Override   public void initExport() { }

   @Override   public void exportSchema(Schema schema) { fields = schema.getFields();}

   @Override   public void exportValues(String valores){ values = valores == null? null: valores.split(Parm.VALUE_SEPARATOR); }

   @Override   public void endExport() 
   {
      if (values != null)
      {
         if ( fields.size() != values.length)
         {   throw new IllegalStateException("Número de valores a exportar["+ values.length+ "] diferente de número de propiedades a crear["+ fields.size()+ "]");
         }
         properties = new ArrayList<>();
         int      i = 0;
         for(Field field: fields)
         {   properties.add(new Property(field, i < values.length? values[i++]: null));
         }
      } else
      {  properties = new ArrayList<>();
      }
   }//endExport

   @Override   public Object getProduct() { return properties; }

}//SchemaValuesToPropertiesExporter
