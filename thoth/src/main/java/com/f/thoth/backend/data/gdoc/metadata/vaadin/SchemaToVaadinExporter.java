package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H3;

/**
 * Builder de exportación de un esquema de metadatos a un componente Vaadin
 */
public class SchemaToVaadinExporter implements Schema.Exporter
{
   FormLayout layout;
   Field.Exporter fieldExporter;
   
   public SchemaToVaadinExporter()
   {      
      layout  = new FormLayout(); 
      layout.setWidthFull();
      layout.setResponsiveSteps(
            new ResponsiveStep("80em", 1),
            new ResponsiveStep("80em", 2),
            new ResponsiveStep("80em", 3),
            new ResponsiveStep("80em", 4)
            );

      fieldExporter = new FieldToComponentExporter();
   }//SchemaToVaadinExporter
   
   @Override  public void initExport() { }

   @Override  public void exportName(String name) { layout.add( new H3(name));}

   @Override  public void exportField(Field field){ layout.add( (Component)field.export(fieldExporter));}

   @Override  public void endExport() { }

   @Override  public Object getProduct() { return layout; }

}//SchemaToVaadinExporter
