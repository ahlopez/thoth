package com.f.thoth.backend.data.gdoc.metadata.vaadin;

import com.f.thoth.backend.data.gdoc.metadata.Schema;

public interface SchemaValuesImporter
{
   public void setSchema(Schema schema);
   
   public void addValue( String value);

}//SchemaValuesImporter
