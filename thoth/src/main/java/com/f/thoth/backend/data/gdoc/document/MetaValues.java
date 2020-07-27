package com.f.thoth.backend.data.gdoc.document;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.f.thoth.backend.data.gdoc.metadata.Value;

/**
 * Representa los valores de los metadatos de un tipo documental
 */
public class MetaValues
{
   List<Map.Entry<String,Value<?>>>  values;


   // ------------- Constructors ------------------

   public MetaValues()
   {
      values = new ArrayList<>();
   }

   public MetaValues( List<Map.Entry<String,Value<?>>> values)
   {
      this.values = values == null? new ArrayList<>()  : values;
   }


   // -------------- Getters & Setters ----------------

   public List<Map.Entry<String,Value<?>>> getValues() { return values;}
   public void        setValues(List<Map.Entry<String,Value<?>>> values){ this.values = values;}

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

      MetaValues that = (MetaValues) o;

      return  this.values.equals(that.values);

   }// equals

   @Override
   public int hashCode()
   {
      return Objects.hashCode(values);
   }

   @Override
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      for ( Map.Entry<String,Value<?>> v:  values)
      {
         String key =  v.getKey();
         Value<?> val =  v.getValue();
         s.append( key.toString()).append("=").append(val.toString()).append(" ");
      }

      return s.toString();
   }//toString


   // --------------- Logic ------------------------------

   public Iterator<Map.Entry<String,Value<?>>> iterator() { return values.iterator();}

   public void addValue( String property, Value<?> value) { values.add(new AbstractMap.SimpleEntry<String,Value<?>>(property, value) );}

}//MetaValues
