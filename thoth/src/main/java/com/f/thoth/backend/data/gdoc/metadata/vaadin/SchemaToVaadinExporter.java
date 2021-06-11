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
   private FormLayout     layout;
   private Field.Exporter fieldExporter;
   
   public SchemaToVaadinExporter()
   {      
      layout  = new FormLayout(); 
      layout.setWidthFull();
      layout.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("30em", 2),
            new ResponsiveStep("30em", 3),
            new ResponsiveStep("30em", 4)
            );
      fieldExporter = new FieldToComponentExporter();
   }//SchemaToVaadinExporter
   
   @Override  public void initExport() { layout.removeAll(); }

   @Override  public void exportName(String name) 
   { 
      H3 nameLabel = new H3(name);
      layout.add(nameLabel);
      layout.setColspan(nameLabel, 4);
   }//exportName

   @Override  public void exportField(Field field) { layout.add((Component)field.export(fieldExporter));}

   @Override  public void endExport() { }

   @Override  public Object getProduct() { return layout; }

}//SchemaToVaadinExporter
