package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.AbstractEntity;

@Entity
@Table(name = "VOLUME")
public class Volume extends AbstractEntity implements  Comparable<Volume>
{
   @NotNull  (message = "{evidentia.expediente.required}")
   protected LeafExpediente       expediente;                              // Leaf expediente associated to the volume

   @NotNull  (message = "{evidentia.expediente.required}")
   protected Integer              currentInstance;                         // Current instace of this volume

   @NotNull  (message = "{evidentia.volume_instances.required}")
   protected Set<VolumeInstance>  instances;                               // Set of volume instances

   public Volume()
   {
      this.expediente      = null;
      this.currentInstance = 0;
      this.instances       = new TreeSet<>();
   }//Volume constructor


   public Volume( LeafExpediente expediente, Integer currentInstance, Set<VolumeInstance>  instances)
   {
      if (expediente == null)
         throw new IllegalArgumentException("Expediente-Hoja que define el volumen no puede ser nulo");

      this.expediente      = expediente;
      this.currentInstance = (currentInstance == null? 0: currentInstance);
      this.instances       = (instances       == null? new TreeSet<>(): instances);
   }//Volume constructor


   // ---------------------- getters & setters ---------------------
   public LeafExpediente       getExpediente() { return expediente;}
   public void                 setExpediente(LeafExpediente expediente){ this.expediente = expediente;}

   public Integer              getCurrentInstance() { return currentInstance;}
   public void                 setCurrentInstance ( Integer currentInstance) { this.currentInstance = currentInstance;}

   public Set<VolumeInstance>  getInstances() {  return instances;}
   public void                 setInstances(Set<VolumeInstance> instances) { this.instances = instances;}


   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Volume))
         return false;

      Volume that = (Volume) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 924027: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "Volume{")
       .append( super.toString())
       .append( " expediente["+ expediente.toString()+ "]")
       .append( " cuurent Instance["+ currentInstance+ "]}\n");

      return s.toString();
   }//toString

   @Override public int compareTo(Volume other)
   {
      if (other == null)
         return 1;

      LeafExpediente that = other.getExpediente();
      return this.expediente.compareTo(that);
   }//compareTo

}//Volume
