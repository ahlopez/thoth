package com.f.thoth.backend.data.gdoc.classification;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.gdoc.metadata.Schema;

/**
 * Representa un nivel del esquema de clasificacion
 */
@Entity
@Table(name = "CLASSIFICATION_LEVEL")
public class ClassificationLevel extends BaseEntity implements Comparable<ClassificationLevel>
{
   @NotNull(message = "{evidentia.level.required}")
   protected Integer    level;

   @NotNull(message = "{evidentia.schema.required}")
   @ManyToOne
   protected Schema     schema;

   // ------------------- Construction ---------------------
   public ClassificationLevel()
   {
      init();
      buildCode();
   }

   public ClassificationLevel( Integer level, Schema schema)
   {
      if ( level == null)
         throw new IllegalArgumentException("Nivel de los nodos de clasificación no puede ser nulo");

      if ( schema == null)
         throw new IllegalArgumentException("Esquema de metadatos de los nodos de clasificación no puede ser nulo");

      this.level  = level;
      this.schema = schema;
      buildCode();
   }//ClassificationLevel

   private void init()
   {
      level = 1;
      schema = Schema.EMPTY;
   }

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      buildCode();
   }//prepareData

   @Override
   protected void buildCode()
   {
      this.code = (tenant == null? "[tenant]": tenant.getCode())+"[LEV]"+ (level == null? "[level]" : level);
   }

   // ------------------- Getters && Setters ---------------

   public Integer getLevel() { return level; }
   public void setLevel(Integer level) { this.level = level;}

   public Schema getSchema() { return schema; }
   public void setSchema(Schema schema) { this.schema = schema; }

   // ---------------------- Object -----------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ClassificationLevel ))
         return false;

      ClassificationLevel that = (ClassificationLevel) o;
        return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 779: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( super.toString())
       .append(" level[" + level+ "]")
       .append(" schema["+ (schema == null? "---": schema.getName())+ "]");

      return s.toString();
   }//toString


   @Override
   public int compareTo(ClassificationLevel that)
   {
     return  this.equals(that)?                         0:
             that == null || that.level == null?        1:
             this.level == null && that.level == null?  0:
             this.level == null?                       -1:
             this.level.compareTo(that.level);

   }//compareTo

}//ClassificationLevel
