package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;

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
   protected Set<VolumeInstance>  instance;                                // Set of volume instances
   
   @Override public int compareTo(Volume that)
   {
	   if (that == null)
		   return 1;
	   
	   return expediente.compareTo(that.expediente);
   }//compareTo
   
  

}//Volume
