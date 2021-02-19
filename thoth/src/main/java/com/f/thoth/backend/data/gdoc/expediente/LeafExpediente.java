package com.f.thoth.backend.data.gdoc.expediente;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.UserGroup;

/**
 * Representa un nodo terminal de la jerarquia de expedientes (expediente/sub-expediente/volumen)
 */
@Entity
@Table(name = "LEAF_EXPEDIENTE")
public class LeafExpediente extends AbstractEntity implements  NeedsProtection, Comparable<LeafExpediente>
{

   @OneToOne
   @NotNull  (message = "{evidentia.expediente.required}")
   protected BaseExpediente    expediente;                 // Expediente that describes this leaf

   @OneToMany
   @NotNull  (message = "{evidentia.types.required}")
   protected Set<DocumentType> admissibleTypes;            // Admisible document types that can be included in the expediente

   // ------------- Constructors ------------------
   public LeafExpediente()
   {
      super();
      this.expediente           = null;
      this.admissibleTypes      = new TreeSet<>();
   }//LeafExpediente null constructor


   // ------------- LeafExpediente ------------------

   public LeafExpediente( BaseExpediente expediente, Set<DocumentType>admissibleTypes)
   {
      super();

      if ( expediente == null )
         throw new IllegalArgumentException("Expediente asociado a la rama no puede ser nulo");

      this.admissibleTypes  = (admissibleTypes  == null? new TreeSet<>(): admissibleTypes);

   }//LeafExpediente constructor


   // -------------- Getters & Setters ----------------

   public BaseExpediente    getExpediente() { return expediente;}
   public void              setExpediente(BaseExpediente expediente){ this.expediente = expediente;}

   public Set<DocumentType> getAdmissibleTypes() {  return admissibleTypes;}
   public void              setAdmissibleTypes(Set<DocumentType> admissibleTypes) { this.admissibleTypes = admissibleTypes;}

   // --------------- Object methods ---------------------

   @Override public boolean equals( Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof LeafExpediente ))
         return false;

      LeafExpediente that = (LeafExpediente) o;
      return this.id != null && this.id.equals(that.id);

   }//equals

   @Override public int hashCode() { return id == null? 74027: id.hashCode();}

   @Override public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append( "LeafExpediente{")
       .append( super.toString())
       .append( " expediente["+ expediente.getCode()+ "]\nAdmissibleTypes[\n");

      for ( DocumentType docType: admissibleTypes )
         s.append( " "+ docType.getName());

      s.append("]\n     }\n");

      return s.toString();
   }//toString


   @Override  public int compareTo(LeafExpediente other)
   {
     if ( other == null)
        return 1;

     BaseExpediente that = other.getExpediente();
     return this.expediente.compareTo(that);
   }// compareTo


   // -----------------  Implements NeedsProtection ----------------

   public Integer                   getCategory()                           {return expediente.getCategory();}
   public void                      setCategory(Integer category)           {expediente.setCategory(category);}

   public SingleUser                getUserOwner()                          {return expediente.getUserOwner();}
   public void                      setUserOwner(SingleUser userOwner)      {expediente.setUserOwner(userOwner);}

   public Role                      getRoleOwner()                          {return expediente.getRoleOwner();}
   public void                      setRoleOwner(Role roleOwner)            {expediente.setRoleOwner(roleOwner);}

   public UserGroup                 getRestrictedTo()                       {return expediente.getRestrictedTo();}
   public void                      setRestrictedTo(UserGroup restrictedTo) {expediente.setRestrictedTo(restrictedTo);}

   @Override public ObjectToProtect getObjectToProtect()                    { return expediente.getObjectToProtect();}

   @Override public boolean         canBeAccessedBy(Integer userCategory)   { return expediente.canBeAccessedBy(userCategory);}

   @Override public boolean         isOwnedBy( SingleUser user)             { return expediente.isOwnedBy(user);}

   @Override public boolean         isOwnedBy( Role role)                   { return expediente.isOwnedBy(role);}

   @Override public boolean         isRestrictedTo( UserGroup userGroup)    { return expediente.isRestrictedTo(userGroup);}

   @Override public boolean         admits( Role role)                      { return expediente.admits(role);}

   @Override public void            grant( Permission  permission)          { expediente.grant(permission);}

   @Override public void            revoke(Permission permission)           { expediente.revoke(permission);}

   // --------------- Logic ------------------------------


}//LeafExpediente
