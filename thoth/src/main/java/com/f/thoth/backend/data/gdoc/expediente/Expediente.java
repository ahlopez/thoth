package com.f.thoth.backend.data.gdoc.expediente;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

@Entity
@Table(name = "EXPEDIENTE")
public class Expediente  extends AbstractEntity implements  NeedsProtection, Comparable<Expediente>, ExpedienteType
{
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
      @NotNull  (message = "{evidentia.expediente.required}")
      protected LeafExpediente       expediente;                              // Leaf expediente associated to the expediente

      @NotNull  (message = "{evidentia.repopath.required}")
      @NotBlank (message = "{evidentia.repopath.required}")
      @NotEmpty (message = "{evidentia.repopath.required}")
      @Size(max = 255)
      protected String               path;                                    // Node path in document repository

      protected String               location;                                // Physical archive location (topographic signature)

      // ------------------ Construction -----------------------

      public Expediente()
      {
         this.expediente = new LeafExpediente();
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
         setType();

      }//Expediente constructor

      // ---------------------- getters & setters ---------------------
      public LeafExpediente    getExpediente()              { return expediente;}
      public void              setExpediente(LeafExpediente expediente){ this.expediente = expediente;}
  	  	
      @Override public Type    getType()                    { return expediente == null? null: expediente.getType();}
      @Override public boolean isOfType( Type type)         { return expediente != null && expediente.isOfType(type);}

      public String            getPath()                    { return path;}
      public void              setPath ( String path)       { this.path = path;}

      public String            getLocation()                { return location;}
      public void              setLocation(String location) { this.location = location;}
  	
      private void             setType()
      {
  		if( expediente != null && !isOfType(Type.EXPEDIENTE))
  			expediente.setType(Type.EXPEDIENTE);
  	  }//setType


      // -----------------  Implements NeedsProtection ----------------

      public Integer                   getCategory()                           {return expediente.getCategory();}
      public void                      setCategory(Integer category)           {expediente.setCategory(category);}

      public User                      getUserOwner()                          {return expediente.getUserOwner();}
      public void                      setUserOwner(User userOwner)            {expediente.setUserOwner(userOwner);}

      public Role                      getRoleOwner()                          {return expediente.getRoleOwner();}
      public void                      setRoleOwner(Role roleOwner)            {expediente.setRoleOwner(roleOwner);}

      public UserGroup                 getRestrictedTo()                       {return expediente.getRestrictedTo();}
      public void                      setRestrictedTo(UserGroup restrictedTo) {expediente.setRestrictedTo(restrictedTo);}

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

