package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.vaadin.flow.component.Component;

public class FieldToComponentExporter implements Field.Exporter
{
   private Metadata           metadata;
   private String             name;
   private boolean            visible;
   private boolean            readOnly;
   private boolean            required;
   private Component          component;
   
   public FieldToComponentExporter()
   {
   }
   
   @Override
   public void initExport()
   {
   }

   @Override
   public void exportName(String name)
   {
     this.name = name;
   }

   @Override
   public void exportMetadata(Metadata metadata)
   {
      this.metadata = metadata;
   }

   @Override
   public void exportFlags(boolean visible, boolean readOnly, boolean required)
   {
      this.visible  = visible;
      this.readOnly = readOnly;
      this.required = required;
   }

   @Override
   public void endExport()
   {
      Metadata.Exporter  metaExporter = new MetadataToComponentExporter(name, visible, readOnly, required);
      component = (Component)metadata.export(metaExporter);
   }

   @Override
   public Object getProduct()
   {
      return component;
   }

}//FieldToComponentExporter
