package com.f.thoth.backend.data.gdoc.metadata;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.security.Tenant;

/**
 * Representa un campo (metadato) que tiene visualización
 */
@Entity
@Table(name = "FIELD", indexes = { @Index(columnList = "code")})
public class Field extends BaseEntity implements Comparable<Field>
{
   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   private String   name;                        // Nombre del campo
   
   @ManyToOne
   private Metadata metadata;                    // Metadato asociado al campo
   
   private boolean  visible;                     // Es el campo visible?
   
   private boolean  readOnly;                    // Es el campo solo de lectura?
   
   private boolean  required;                    // Es el campo requerido
   
   @NotNull (message = "{evidentia.order.required}")
   @Positive(message = "{evidentia.order.positive}")
   private Integer  sortOrder;                   // Orden del campo en la lista de campos
   
   private Integer  columns;                     // Número de columnas que ocupa el campo


   // --------------------- Construction -------------------------
   public Field()
   {  
      super();
      this.name     = "[name]";
      this.metadata = null;
      this.visible  = true;
      this.readOnly = false;
      this.required = false;
      this.sortOrder= 0;
      this.columns  = 1;
   }//Field

   public Field( Tenant tenant, String name, Metadata metadata, boolean visible, boolean readOnly, boolean required, int sortOrder, int columns)
   {
      super();
      if (tenant == null)
         throw new IllegalArgumentException("Tenant dueño del campo no puede ser nulo");
         
      if ( !TextUtil.isIdentifier(name))
         throw new IllegalArgumentException("Nombre inválido para el campo");

      if ( metadata == null)
         throw new IllegalArgumentException("Metadato asociado al campo no puede ser nulo");

      this.tenant   = tenant;
      this.name     = name;
      this.metadata = metadata;
      this.visible  = visible;
      this.readOnly = readOnly;
      this.required = required;
      this.sortOrder= (sortOrder <= 0? 0: sortOrder);
      this.columns  = (columns <= 0? 1: columns);

   }//Field
   

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name     =  TextUtil.nameTidy(name).toLowerCase();
      buildCode();
   }//prepareData

   @Override protected void buildCode()
   {
      this.code = (tenant == null? "[tenant]": tenant.getCode())+"[FLD]>"+
                   (name == null? "[name]" : name);
   }//buildCode

   public String isValid()
   {
      StringBuilder msg = new StringBuilder();
      if ( !TextUtil.isValidName(name))
         msg.append("Nombre del Campo["+ name+ "] es inválido\n");

      return msg.toString();
   }//isValid


   // ------------------------ Getters && Setters ---------------------------
   public String     getName() { return name;}
   public void       setName( String name) { this.name = name;}

   public Metadata   getMetadata() { return metadata;}
   public void       setMetadata( Metadata metadata) { this.metadata = metadata;}

   public boolean    isVisible() { return visible;}
   public void       setVisible( boolean visible) { this.visible = visible;}

   public boolean    isReadOnly() { return readOnly;}
   public void       setReadOnly(boolean readOnly) { this.readOnly = readOnly;}

   public boolean    isRequired() { return required;}
   public void       setRequired( boolean required) { this.required = required;}
   
   public Integer    getSortOrder() { return sortOrder;}
   public void       setSortOrder( Integer sortOrder) { this.sortOrder = sortOrder;}
   
   public Integer    getColumns() { return columns;}
   public void       setColumns( Integer columns) { this.columns = columns;}
   
   // ---------------------- Builders ---------------------
   public interface Exporter
   { 
      public void initExport();
      public void exportBasic( String name, Metadata metadata);
      public void exportFlags( boolean visible, boolean readOnly, boolean required);
      public void exportNumbers( Integer sortOrder, Integer columns);
      public void endExport();
      public Object getProduct();
   }//Exporter
   
   public Object export( Field.Exporter exporter)
   {
      exporter.initExport();
      exporter.exportBasic(name, metadata);
      exporter.exportFlags( visible, readOnly, required);
      exporter.exportNumbers( sortOrder, columns);
      exporter.endExport();
      return exporter.getProduct();
   }//export


   // ---------------------- Object -----------------------
   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Field ))
         return false;

      Field that = (Field) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 8087: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append(" Field{"+ super.toString())
       .append(" name["+      name+ "]")
       .append(" metadata["+ metadata.toString()+ "]")
       .append(" visible["+   visible+ "]")
       .append(" readOnly["+  readOnly+ "]")
       .append(" required["+  required+ "]")
       .append(" sortOrder["+ sortOrder+ "]")
       .append("}\n");

      return s.toString();
   }//toString

   @Override
   public int compareTo(Field that)
   {
      return this.equals(that)?  0 :
             that == null?       1 :
             this.sortOrder.compareTo(that.sortOrder);
   }//compareTo

}//Field
