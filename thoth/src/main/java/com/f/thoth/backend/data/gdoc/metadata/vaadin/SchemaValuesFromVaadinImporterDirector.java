package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import java.util.stream.Stream;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValuesImporter;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;

public class SchemaValuesFromVaadinImporterDirector implements SchemaValues.ImporterDirector
{
   Schema    schema;
   Component component;
   
   public SchemaValuesFromVaadinImporterDirector( Component component, Schema schema)
   {
      if ( component == null)
         throw new IllegalArgumentException("Componente a importar no puede ser nula");
      
      if ( schema == null)
         throw new IllegalArgumentException("Esquema asociado a los valores no puede ser nulo");
      
      this.component = component;
      this.schema    = schema;
   }//SchemaValuesFromVaadinImporterDirector constructor

   @Override public void dirija(SchemaValuesImporter importer)
   {
     importer.setSchema(schema);
     Stream<Component> children = component.getChildren();
     children.forEach( comp->
     {
        if ( comp instanceof AbstractField<?,?>)
        {
           @SuppressWarnings("unchecked")
           String value = ((AbstractField<?,String>) comp).getValue();
           importer.addValue(value);
        }
     });

   }//dirija

}//SchemaValuesFromVaadinImporterDirector
