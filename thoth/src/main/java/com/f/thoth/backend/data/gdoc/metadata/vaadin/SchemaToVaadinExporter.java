package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SchemaToVaadinExporter implements Schema.Exporter
{
   VerticalLayout layout;
   Field.Exporter fieldExporter;
   
   public SchemaToVaadinExporter()
   {      
      layout        = new VerticalLayout(); 
      fieldExporter = new FieldToComponentExporter();
   }//SchemaToVaadinExporter
   
   @Override  public void initExport() { }

   @Override  public void exportName(String name) { layout.add( new Label(name));}

   @Override  public void exportField(Field field) { layout.add( (Component)field.export(fieldExporter));}

   @Override  public void endExport() { }

   @Override  public Object getProduct() { return layout; }

}//SchemaToVaadinExporter
