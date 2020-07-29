package com.f.thoth.backend.data.gdoc.metadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.BaseEntity;
import com.f.thoth.backend.data.entity.util.TextUtil;

/**
 * Representa la definicion de un metadato
 */
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = Metadata.BRIEF,
        attributeNodes = {
            @NamedAttributeNode("parms")
        }),
    @NamedEntityGraph(
        name = Metadata.FULL,
        attributeNodes = {
            @NamedAttributeNode("parms"),
            @NamedAttributeNode("history")
        }) })
@Entity
@Table(name = "METADATA", indexes = { @Index(columnList = "code") })
public class Metadata extends BaseEntity implements Comparable<Metadata>
{
   public static final String BRIEF = "Metadata.brief";
   public static final String FULL  = "Metadata.full";

   @NotBlank(message = "{evidentia.name.required}")
   @NotNull (message = "{evidentia.name.required}")
   @Size(min= 2, max = 50, message= "{evidentia.name.length}")
   private String    name;

   @NotNull (message = "{evidentia.type.required}")
   private Type      type;

   private boolean   required = true;

   private boolean   editable = false;

   @NotNull (message = "{evidentia.range.required}")
   @NotEmpty(message = "{evidentia.range.required}")
   private String    range;


   // ------------- Constructors ------------------
   public Metadata()
   {
      super();
   }

   public Metadata( String name, Type type, boolean required, boolean editable)
   {
      super();
      if ( TextUtil.isEmpty(name))
         throw new IllegalArgumentException("Nombre del metadato no puede ser nulo ni vacío");

      if ( type == null)
         throw new IllegalArgumentException("Tipo del metadato no puede ser nulo");

      this.name     = name;
      this.type     = type;
      this.required = required;
      this.editable = editable;
      buildCode();

   }//Metadata

   @Override protected void buildCode() { this.code = tenant.getCode()+ ":M:"+ this.name;}

   // -------------- Getters & Setters ----------------

   public String  getName(){ return name;}
   public void    setName( String name)
   {
      this.name = name;
      buildCode();
   }

   public Type    getType(){ return type;}
   public void    setType( Type type){ this.type = type;}

   public boolean isRequired() { return required;}
   public void    setRequired( boolean required){ this.required = required;}

   public boolean isEditable() { return editable;}
   public void    setEditable( boolean editable){ this.editable = editable;}

   public String  getRange(){ return range;}
   public void    setRange(String range) { this.range = range;}

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

      Metadata that = (Metadata) o;

      return  this.code.equals(that.code);

   }// equals

   @Override
   public int hashCode()
   {
      return code.hashCode();
   }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( super.toString()).append("\n\t\t").
        append( " name["+ name+ "]").
        append( " type["+ type+ "]").
        append( " required["+ required+ "]").
        append( " editable["+ editable+ "]").
        append( " range["+ range.toString()+ "]");

      return s.toString();
   }//toString

   @Override
   public int compareTo(Metadata other)
   {
      return other == null?  1 :  this.equals(other)? 0:  this.code.compareTo( other.code);
   }

   // --------------- Logic ------------------------------

   public boolean in(Object value)
   {
     switch ( type)
     {
      case STRING   :
      {
         StringRange rng =new StringRange( range);
         return rng.in( (String)value);
      }
      case ENUM     :
      {
         EnumRange rng =new EnumRange( range);
         return rng.in( (String)value);
      }
      case BINARY   :
      {
         return value != null;
      }
      case BOOLEAN  :
      {
         BooleanRange rng =new BooleanRange( );
         return rng.in( (Boolean)value);
      }
      case DECIMAL  :
      {
         DecimalRange rng =new DecimalRange( range);
         return rng.in( (BigDecimal)value);
      }
      case INTEGER  :
      {
         IntegerRange rng =new IntegerRange( range);
         return rng.in( (BigInteger)value);
      }
      case DATETIME :
      {
         DateTimeRange rng =new DateTimeRange( range);
         return rng.in( (LocalDateTime)value);
      }
      case REFERENCE:
      {
         IdRange rng =new IdRange( range);
         return rng.in( (String)value);
      }
      case URI      :
      {
         UriRange rng =new UriRange( );
         return rng.in( (String)value);
      }
      case ID       :
      {
         IdRange rng =new IdRange(range);
         return rng.in( (String)value);
      }
      case PATH     :
      {
         StringRange rng =new StringRange( range);
         return rng.in( (String)value);
      }
      case HTML     :
      {
         HtmlRange rng =new HtmlRange( );
         return rng.in( (String)value);
      }
     }//switch
      return false;
   }//in


}//Metadata