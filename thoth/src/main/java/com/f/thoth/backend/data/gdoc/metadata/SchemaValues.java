package com.f.thoth.backend.data.gdoc.metadata;

import static com.f.thoth.Parm.TENANT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.security.Tenant;
import com.vaadin.flow.server.VaadinSession;

@Entity
@Table(name = "FIELD_VALUES")
public class SchemaValues extends AbstractEntity implements SchemaValuesImporter, Comparable<SchemaValues>
{
   public static final SchemaValues  EMPTY = new SchemaValues();

   @ManyToOne
   @NotNull (message = "{evidentia.tenant.required}")
   protected Tenant tenant;

   @NotNull(message="{evientia.schema.required}")
   @ManyToOne
   @JoinColumn(name="esquema_id")
   private Schema    schema;                    // Schema to which the values apply

   private String    valores;                   // Value for each field of the schema separated by Constant.VALUE_SEPARATOR

   public SchemaValues()
   {
      super();
      init( Schema.EMPTY, null);
      
   }//SchemaValues constructor


   public SchemaValues( Schema schema, String values)
   {
      super();
      init(schema, values);

   }//SchemaValues constructor
   
   private void init(Schema schema, String values)
   {
      if (schema == null)
         throw new IllegalArgumentException("Esquema de los valores no puede ser nulo");

      VaadinSession vSession = VaadinSession.getCurrent();
      this.tenant = vSession == null? null: (Tenant)vSession.getAttribute(TENANT);
      this.schema  = schema;
      this.valores = values;
   }//init


   public SchemaValues( SchemaValues.ImporterDirector importerDirector)
   {
      super();

      schema  = null;
      valores = null;
      importerDirector.dirija( this);
      String ok = isValid();
      if ( ok != null)
         throw new IllegalStateException("Importación de valores construyó un esquema inconsistente. Razón\n"+ ok );

   }//SchemaValues constructor


   private String isValid()
   {
      StringBuilder msg = new StringBuilder();
      if ( schema == null)
         msg.append("Esquema asociado a los valores no puede ser nulo\n");

      return msg.toString();
   }//isValid


   // ------------------------   Getters && Setters ----------------------------

   public String        getName()    { return schema.getName();}
   
   public Tenant         getTenant() { return tenant; }
   public void           setTenant(Tenant tenant) { this.tenant = tenant; }

   public Schema         getSchema() { return schema; }
   @Override public void setSchema(Schema schema) { this.schema = schema; }

   public String         getValues() { return valores; }
   public void           setValues(String valores) { this.valores = valores; }
   @Override public void addValue( String value)
   {
     valores = valores == null
                       ?  value
                       :  valores+ Parm.VALUE_SEPARATOR+ value;
   }//addValue

   // ------------------------   Builders ----------------------------

   public interface Exporter
   {
      public void   initExport();
      public void   exportSchema( Schema schema);
      public void   exportValues( String valores);
      public void   endExport();
      public Object getProduct();
   }//Exporter

   public Object export( SchemaValues.Exporter exporter)
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


   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof SchemaValues ))
         return false;

      SchemaValues that = (SchemaValues) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 40247: id.hashCode();}

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "SchemaValues{"+super.toString())
       .append( "tenant["+ tenant.getCode()+ "] ")
       .append( "schema["+ schema.getName()+ "] ")
       .append( "value["+ valores+ "]}\n");

      return s.toString();
   }//toString


   @Override  public int compareTo(SchemaValues that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.getId().compareTo(that.getId());

   }// compareTo

   // ----------------------------- Logic --------------------------------
   public Iterator<String> iterator()
   {
      if ( valores == null )
         return new ArrayList<String>().iterator();

      String vals[] = valores.split(Parm.VALUE_SEPARATOR);
      return Arrays.asList(vals).iterator();
   }//iterator

}//PropertyValues
