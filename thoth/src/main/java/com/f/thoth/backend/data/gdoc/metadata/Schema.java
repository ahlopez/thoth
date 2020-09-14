package com.f.thoth.backend.data.gdoc.metadata;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un esquema de metadatos
 */
@Entity
@Table(name = "ESQUEMA", indexes = { @Index(columnList = "code")}) 
public class Schema extends BaseEntity implements Comparable<Schema>
{  
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @NotEmpty(message = "{evidentia.name.required}")
   private String         name;

   /*
   @NotNull (message = "{evidentia.fields.required}")
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @JoinColumn(name="field_id")
   @BatchSize(size = 30)
   */
   @NotNull (message = "{evidentia.fields.required}")
   @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @JoinTable(name="SCHEMA_FIELDS", joinColumns=@JoinColumn(name="schema_id"), inverseJoinColumns=@JoinColumn(name="field_id"))
   @BatchSize(size = 30)
   private Set<Field>  fields;
   
   public static final Schema EMPTY = new Schema("EMPTY", new TreeSet<>());


   // ------------- Constructors ------------------
   public Schema()
   {
      super();
      fields = new TreeSet<>();
      buildCode();
   }

   public Schema( String name, Set<Field> fields)
   {
      super();
      if( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre inválido");

      if(fields == null )
         throw new IllegalArgumentException("Conjunto de campos del esquema no puede ser nulo ni vacío");

      this.name   = name;
      this.fields = fields;
      buildCode();
   }//Schema

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  (name != null ? name.trim() : "[schema]");
      buildCode();
   }//prepareData

   @Override public void buildCode(){ this.code = (tenant == null? "[Tenant]": tenant.getCode())+ "[SCM]"+ (name == null? "[Name]": name);}

   // -------------- Getters & Setters ----------------

   public String    getName(){ return name;}
   public void      setName( String name)
   {
      this.name = name;
      buildCode();
   }//setName

   public Set<Field>  getFields() { return fields;}
   public void        setFields( Set<Field> fields){ this.fields = fields;}
   
   // --------------- Builders ---------------------
   
   public interface Exporter
   {
      public void initExport();
      public void exportName(String name);
      public void exportField(Field field);
      public void endExport();
      public Object getProduct();
      
   }//Exporter
   
   public Object export( Schema.Exporter exporter, Field.Exporter fieldExporter)
   {
      exporter.initExport();
      exporter.exportName( name);
      fields.forEach( field-> field.export(fieldExporter));
      exporter.endExport();
      return exporter.getProduct();
   }//export


   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (o == null || getClass() != o.getClass())
         return false;

      if (!super.equals(o))
         return false;

      Schema that = (Schema) o;

      return  this.tenant.equals(that.tenant) && this.code.equals(that.code);

   }// equals

   @Override
   public int hashCode()
   {
      return Objects.hash( tenant, code);
   }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "Schema{").
      append( super.toString()).
      append( " name["+ name+ "]").append("\n\t\t").
      append( " fields["+ fieldNames()).
      append( "]}");

      return s.toString();

   }//toString

   private String fieldNames()
   {
      StringBuilder s = new StringBuilder();
      for(Field f: fields)
         s.append( f.getName()).append(" ");

      return s.toString();
   }//fieldNames

   @Override
   public int compareTo(Schema other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }
   
   // -------------------------- Logic ------------------------------

   public Iterator<Field> iterator()    { return fields.iterator(); }
   
   public void deleteField(Field field) { fields.remove(field); }
   
   public void addField( Field field)   { fields.add(field); }

}//Schema