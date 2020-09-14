package com.f.thoth.backend.data.gdoc.metadata;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.vaadin.SchemaValuesImporter;

@Entity
@Table(name = "PROPERTY_VALUES")
public class PropertyValues extends BaseEntity implements SchemaValuesImporter
{

   @NotNull(message="{evientia.schema.required}")
   @ManyToOne
   @JoinColumn(name="esquema_id")
   private Schema    schema;


   @NotNull (message = "{evidentia.valores.required}")
   @NotEmpty(message = "{evidentia.valores.required}")
   private String    valores;

   public PropertyValues()
   {
      super();
      valores = "<valores><value/></valores>";
   }

   public PropertyValues( PropertyValues.ImporterDirector importerDirector)
   {
      super();
      valores = "";
      importerDirector.dirija( this);
      String ok = isValid();
      if ( ok != null)
         throw new IllegalStateException("Importación de valores construyó un esquema inconsistente. Razón\n"+ ok );

      buildCode();
   }//SchemaValues

   private String isValid()
   {
      StringBuilder msg = new StringBuilder();
      if ( TextUtil.isEmpty(valores))
         msg.append("Valores del esquema no pueden ser nulos ni vacíos\n");

      return msg.toString();
   }//isValid


   @Override public void buildCode(){ this.code = (tenant == null? "[Tenant]": tenant.getCode())+ "[SCV]"+ (id == null? "---": id);}

   // ------------------------   Getters && Setters ----------------------------
   
   public Schema         getSchema() { return schema; }
   @Override public void setSchema(Schema schema) { this.schema = schema; }
   
   public String         getValues() { return valores; }
   public void           setValues(String valores) { this.valores = valores; }
   @Override public void addValue( String value) {  this.valores = this.valores+ ";"+ value; }

   // ------------------------   Builders ----------------------------

   public interface Exporter
   {
      public void   initExport();
      public void   exportSchema( Schema schema);
      public void   exportValues( String valores);
      public void   endExport();
      public Object getProduct();
   }//Exporter

   public Object export( PropertyValues.Exporter exporter)
   {
      exporter.initExport();
      exporter.exportSchema(schema);
      exporter.exportValues(valores);
      exporter.endExport();
      return exporter.getProduct();
   }//export

   public interface ImporterDirector
   {
      public void dirija( SchemaValuesImporter importer);
   }

}//PropertyValues