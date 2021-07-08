package com.f.thoth.backend.data.gdoc.metadata.jcr;

import java.util.ArrayList;
import java.util.List;

import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.gdoc.metadata.Schema;

public class SchemaToMixinExporter implements Schema.Exporter
{
   String       mixin;
   List<Field>  fields;
   
   public SchemaToMixinExporter() { };

   @Override  public void initExport() { fields = new ArrayList<Field>(); }

   @Override  public void exportName(String name) { mixin = name; }

   @Override  public void exportField(Field field) { fields.add(field); }

   @Override  public void endExport()  { }

   @Override  public Object getProduct() { return fields; }

}//SchemaToMixinExporter
