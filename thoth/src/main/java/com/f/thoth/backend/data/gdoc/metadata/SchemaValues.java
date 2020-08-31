package com.f.thoth.backend.data.gdoc.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaValuesImporter;

@Entity
@Table(name = "SCHEMA_VALUES", indexes = { @Index(columnList = "code")})
public class SchemaValues extends BaseEntity implements SchemaValuesImporter
{
   @ManyToOne
   private Schema       schema;
   
   private List<String> values;
   
   public SchemaValues()
   {     
      values = new ArrayList<>();
   }
   
   public SchemaValues( SchemaValues.ImporterDirector importerDirector)
   {
      values = new ArrayList<>();
      importerDirector.dirija( this);  
      buildCode();
   }//SchemaValues
   

   @Override public void buildCode(){ this.code = (tenant == null? "[Tenant]": tenant.getCode())+ "[SCM]"+ (id == null? "---": id);}
   
   // ------------------------   Getters && Setters ----------------------------
   public Schema getSchema() { return schema; }
   @Override public void setSchema(Schema schema) { this.schema = schema; }

   public List<String>   getValues() { return values; }
   public void           setValues(List<String> values) { this.values = values; }
   @Override public void addValue( String value) {  this.values.add( value); }
   
   // ------------------------   Builders ----------------------------
   
   public interface Exporter
   {
      public void initExport();
      public void exportSchema( Schema schema);
      public void exportValue( String value);
      public void endExport();
      public Object getProduct();
   }//Exporter
   
   public Object export( SchemaValues.Exporter exporter)
   {
      exporter.initExport();
      exporter.exportSchema(schema);
      values.forEach( value-> exporter.exportValue(value));
      exporter.endExport();
      return exporter.getProduct();
   }//export
   
   public interface ImporterDirector
   {
      public void dirija( SchemaValuesImporter importer);
   }

}//SchemaValues
