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
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa un esquema de metadatos
 */
@Entity
@Table(name = "SCHEMA", indexes = { @Index(columnList = "code")})
public class Schema extends BaseEntity implements Comparable<Schema>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   private String         name;

   @NotNull (message = "{evidentia.fields.required}")
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @OrderColumn
   @JoinColumn
   @BatchSize(size = 30)
   private Set<Metadata>  fields;


   // ------------- Constructors ------------------
   public Schema()
   {
      fields = new TreeSet<>();
   }

   public Schema( String name, Set<Metadata> fields)
   {
      if( !TextUtil.isValidName(name))
         throw new IllegalArgumentException("Nombre inválido");

      if(fields == null || fields.size() == 0)
         throw new IllegalArgumentException("Conjunto de metadatos del esquema no puede ser nulo ni vacío");

      this.name   = name;
      this.fields = fields;
      buildCode();
   }//Schema

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name =  name != null ? name.trim() : "Generico";
      buildCode();
   }//prepareData

   @Override protected void buildCode() { this.code =  tenant.toString()+ ">"+ name; }

   // -------------- Getters & Setters ----------------

   public String    getName(){ return name;}
   public void      setName( String name)
   {
      this.name = name;
      buildCode();
   }//setName

   public Set<Metadata>  getFields() { return fields;}
   public void           setFields( Set<Metadata> fields){ this.fields = fields;}
   
   // --------------- Builders ---------------------
   
   public interface Exporter
   {
      public void initExport();
      public void exportName(String name);
      public void exportField(Metadata field);
      public void endExport();
      public Object getProduct();
      
   }//Exporter
   
   public Object export( Schema.Exporter exporter)
   {
      exporter.initExport();
      exporter.exportName( name);
      fields.forEach( field-> exporter.exportField(field));
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
      for(Metadata m: fields)
         s.append( m.getName()).append(" ");

      return s.toString();
   }//fieldNames

   @Override
   public int compareTo(Schema other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }

   public Iterator<Metadata> iterator()
   {
      return fields.iterator();
   }

}//Schema