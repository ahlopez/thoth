package com.f.thoth.backend.data.gdoc.expediente;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.AbstractEntity;

@Entity
@Table(name = "EXPEDIENTE")
public class Expediente  extends AbstractEntity implements  Comparable<Expediente>
{
      @NotNull  (message = "{evidentia.expediente.required}")
      protected LeafExpediente       expediente;                              // Leaf expediente associated to the expediente

      @NotNull  (message = "{evidentia.repopath.required}")
      @NotBlank (message = "{evidentia.repopath.required}")
      @NotEmpty (message = "{evidentia.repopath.required}")
      @Size(max = 255)
      protected String         path;                                          //  Node path in document repository

      protected String               location;                                // Physical archive location (topographic signature)

      // ------------------ Construction -----------------------

      public Expediente()
      {
         this.expediente = null;
         this.path       = "";
         this.location   = "";
      }//Expediente constructor

      public Expediente( LeafExpediente expediente, String path, String location)
      {
         if (expediente == null)
            throw new IllegalArgumentException("Expediente-Hoja que define el expediente no puede ser nulo");

         if ( path == null)
            throw new IllegalArgumentException("Path del expediente en el repositorio no puede ser nulo");

         this.expediente = expediente;
         this.path       = path;
         this.location   = (location   == null? "" : location);

      }//Expediente constructor

      // ---------------------- getters & setters ---------------------
      public LeafExpediente    getExpediente() { return expediente;}
      public void              setExpediente(LeafExpediente expediente){ this.expediente = expediente;}

      public String            getPath() { return path;}
      public void              setPath ( String path) { this.path = path;}

      public String            getLocation() {  return location;}
      public void              setLocation(String location) { this.location = location;}

      // --------------- Object methods ---------------------

      @Override public boolean equals( Object o)
      {
         if (this == o)
            return true;

         if (!(o instanceof Expediente))
            return false;

         Expediente that = (Expediente) o;
         return this.id != null && this.id.equals(that.id);

      }//equals

      @Override public int hashCode() { return id == null? 94027: id.hashCode();}

      @Override public String toString()
      {
         StringBuilder s = new StringBuilder();
         s.append( "Expediente{")
          .append( super.toString())
          .append( " expediente["+ expediente.toString()+ "]")
          .append( " path["+ path+ "]")
          .append( " location["  + location+ "]}\n");

         return s.toString();
      }//toString


      @Override public int compareTo(Expediente other)
      {
         if (other == null)
            return 1;

         LeafExpediente that = other.getExpediente();
         return expediente.compareTo(that);
      }//compareTo

}//Expediente
