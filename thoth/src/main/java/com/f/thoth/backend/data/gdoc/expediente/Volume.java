package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

@Entity
@Table(name = "VOLUME")
public class Volume extends AbstractEntity implements  NeedsProtection, Comparable<Volume>, ExpedienteType
{
   @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @NotNull  (message = "{evidentia.expediente.required}")
   protected LeafExpediente       expediente;                              // Leaf expediente associated to the volume

   @NotNull  (message = "{evidentia.expediente.required}")
   protected Integer              currentInstance;                         // Current instace of this volume

   @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
   @NotNull  (message = "{evidentia.volume_instances.required}")
   protected Set<VolumeInstance>  instances;                               // Set of volume instances

   public Volume()
   {
      this.expediente      = new LeafExpediente();
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
      setType();
   }//Volume constructor


   // ---------------------- getters & setters ---------------------
   public LeafExpediente            getExpediente()      { return expediente;}
   public void                      setExpediente(LeafExpediente expediente){ this.expediente = expediente;} 	
	
   @Override public Nature            getType()            { setType(); return expediente.getType();}
   @Override public boolean         isOfType( Nature type) { return expediente != null && expediente.isOfType(type);}

   public Integer                   getCurrentInstance() { return currentInstance;}
   public void                      setCurrentInstance ( Integer currentInstance) { this.currentInstance = currentInstance;}

   public Set<VolumeInstance>       getInstances()       {  return instances;}
   public void                      setInstances(Set<VolumeInstance> instances) { this.instances = instances;}
 	
   private void                     setType()
   {
		if( expediente != null && !isOfType(Nature.VOLUMEN))
			expediente.setType(Nature.VOLUMEN);
   }//setType


   // -----------------  Implements NeedsProtection ----------------

   public Integer                   getCategory()                           { return expediente.getCategory();}
   public void                      setCategory(Integer category)           { expediente.setCategory(category);}

   public User                      getUserOwner()                          { return expediente.getUserOwner();}
   public void                      setUserOwner(User userOwner)            { expediente.setUserOwner(userOwner);}

   public Role                      getRoleOwner()                          { return expediente.getRoleOwner();}
   public void                      setRoleOwner(Role roleOwner)            { expediente.setRoleOwner(roleOwner);}

   public UserGroup                 getRestrictedTo()                       { return expediente.getRestrictedTo();}
   public void                      setRestrictedTo(UserGroup restrictedTo) { expediente.setRestrictedTo(restrictedTo);}

   @Override public ObjectToProtect getObjectToProtect()                    { return expediente.getObjectToProtect();}

   @Override public boolean         canBeAccessedBy(Integer userCategory)   { return expediente.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( User user)                   { return expediente.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                   { return expediente.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)    { return expediente.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                      { return expediente.admits(role);}

   @Override public void            grant( Permission  permission)          { expediente.grant(permission);}

   @Override public void            revoke(Permission permission)           { expediente.revoke(permission);}


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

   // ----------------------- Logic --------------------------
   public void  addInstance( VolumeInstance instance) { instances.add(instance); }

   public String getPath() { return expediente.getPath();}


}//Volume

