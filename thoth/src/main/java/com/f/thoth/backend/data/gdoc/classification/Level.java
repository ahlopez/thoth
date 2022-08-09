package com.f.thoth.backend.data.gdoc.classification;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.metadata.Schema;

/**
 * Representa la definición de un nivel del esquema de clasificación
 * Cada clase del esquema de clasificación está asociada con un nivel.
 */
@Entity
@Table(name = "NIVEL", indexes = { @Index(columnList = "code"), @Index(columnList= "name") })
public class Level extends BaseEntity implements Comparable<Level>
{
   public static final Level DEFAULT = new Level("default", 99, Schema.EMPTY);
   
   @NotBlank(message = "{evidentia.name.required}")
   @NotEmpty(message = "{evidentia.name.required}")
   @Size(max = 50)
   private String name;                                     // Nombre del nivel, vg oficina,  serie, subserie
   
   @NotNull (message="{evidentia.orden.required}")
   @PositiveOrZero(message="{evidentia.orden.positive}")
   private Integer orden;                                  // Nivel en el árbol de clasificación. Raíz = 0, sigte=1, ...
   
   @NotNull(message="{evidentia.schema.notnull}")
   @ManyToOne
   private Schema  schema;                                 // Esquema de metadatos asociado con el nivel

   public Level()
   {
      super();
      name   = "";
      orden  = 1;
      schema = new Schema();
      buildCode();
   }//Level constructor

   public Level( String name, Integer orden, Schema schema)
   {
      super();
      if ( !TextUtil.isValidName( name))
         throw new IllegalArgumentException("Nombre["+ name+ "] es inválido");
      
      if ( orden == null || orden < 0)
         throw new IllegalArgumentException("Número del nivel debe ser un número cero o positivo");
      
      if ( schema == null)
         throw new IllegalArgumentException("Esquema de metadatos del nivel no puede ser nulo");

      this.name   = TextUtil.nameTidy(name);
      this.orden  = orden;
      this.schema = schema;
      buildCode();
   }//Level constructor

   @PrePersist
   @PreUpdate
   public void prepareData()
   {
      this.name   = name != null ? name.trim() : "Anonimo";
      buildCode();
   }//prepareData

   @Override public void buildCode(){ this.code = (tenant == null? "[Tenant]": tenant.getCode())+ ">"+ this.orden; }

   // -------------- Getters & Setters ----------------

   public String       getName()  { return name;}
   public void         setName( String name) { this.name = name;}
   
   public Integer      getOrden() { return orden;}
   public void         setOrden( Integer orden){ this.orden = orden;}
   
   public Schema       getSchema() { return this.schema;}
   public void         setSchema( Schema schema) { this.schema = schema;}

   // --------------- Object methods ---------------------

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Level ))
         return false;

      Level that = (Level) o;
      return this.id != null && this.id.equals(that.id);

   }// equals

   @Override
   public int hashCode() { return id == null? 13793: id.hashCode(); }

   @Override
   public String toString() { return "Level{"+ super.toString()+ 
                                     " name["+ name+ "]"+ 
                                     " orden["+ orden+ "]\n"+
                                     "schema["+ schema.toString()+ "]}\n";
   }//toString

   @Override
   public int compareTo(Level that)
   {
      return this.equals(that)?  0 :
         that ==  null        ?  1 :
         this.code == null  && that.code == null?  0 :
         this.code != null  && that.code == null?  1 :
         this.code == null  && that.code != null? -1 :
         this.code.compareTo(that.code);

   }// compareTo

   // --------------- Logic ---------------------
   public boolean isRoot() { return orden == 1;}
   
   public boolean isDefault() { return this.name.toLowerCase().equals("default");}

}//Level
