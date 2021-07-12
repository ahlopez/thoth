package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

@Entity
@Table(name = "VOLUME")
public class Volume extends AbstractEntity implements  NeedsProtection, Comparable<Volume>, ExpedienteType
{
   @OneToOne (cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @NotNull  (message = "{evidentia.expediente.required}")
   protected BaseExpediente       expediente;                      // Expediente that describes this volume

   @ManyToMany (fetch= FetchType.EAGER)
   @JoinTable (name="volume_docType",  indexes= @Index(name= "vol_docType", columnList= "volume_id", unique= false) )
   @NotNull   (message = "{evidentia.types.required}")
   protected Set<DocumentType>    admissibleTypes;                 // Admisible document types that can be included in the volume

   @NotNull  (message = "{evidentia.volumeinstance.required}")
   protected Integer              currentInstance;                 // Current instance of this volume. For EXPEDIENTE it is always zero (0)

   public Volume()
   {
      super();
      expediente = new BaseExpediente();
      setType(Nature.VOLUMEN);
      this.admissibleTypes = new TreeSet<>();
      this.currentInstance = 0;
  }//Volume constructor


   public Volume( BaseExpediente base, Nature type, Integer currentInstance, Set<DocumentType>  admissibleTypes)
   {
      if (base == null)
      {   throw new IllegalArgumentException("Expediente-Hoja que define el volumen no puede ser nulo");
      }
      if ( type == null)
      {   throw new IllegalArgumentException("Tipo que define si es EXPEDIENTE o VOLUMEN no puede ser nulo");        
      }
      this.expediente      = base;
      setType(type);
      this.admissibleTypes = admissibleTypes  == null? new TreeSet<>(): admissibleTypes;
      this.currentInstance = (currentInstance == null? 0: currentInstance);
   }//Volume constructor


   // ---------------------- getters & setters ---------------------
   public BaseExpediente            getExpediente()                                       { return expediente;}
   public void                      setExpediente(BaseExpediente expediente)              { this.expediente = expediente;} 	

   public Set<DocumentType>         getAdmissibleTypes()                                  { return admissibleTypes;}
   public void                      setAdmissibleTypes(Set<DocumentType> admissibleTypes) { this.admissibleTypes = admissibleTypes;}
   public void                      clearTypes()                                          { this.admissibleTypes.clear();}
   
   public Integer                   getCurrentInstance()                                  { return currentInstance;}
   public void                      setCurrentInstance ( Integer currentInstance)         { this.currentInstance = currentInstance;}
   
   public Tenant                    getTenant()                                           { return expediente.getTenant();}
   public boolean                   isOpen()                                              { return expediente.isOpen();}
 	
   // ------------------------ Hereda de LeafExpediente -------------------------

   public String            getName()                                  { return expediente.getName();}
   public void              setName ( String name)                     { expediente.setName(name);}
   
   public String            getCode()                                  { return expediente.getCode();}
   public String            formatCode()                               { return expediente.formatCode();}

   @Override public Nature  getType()                                  { return expediente == null? null: expediente.getType();}
   @Override public void    setType( Nature type)                      { this.expediente.setType(type);}
   @Override public boolean isOfType( Nature type)                     { return expediente != null && expediente.isOfType(type);}

   public Boolean           getOpen()                                  { return expediente.getOpen();}
   public void              setOpen ( Boolean open)                    { expediente.setOpen(open);}

   public void              setObjectToProtect(ObjectToProtect objectToProtect) { expediente.setObjectToProtect(objectToProtect);}

   public Long              getOwnerId()                               { return expediente.getOwnerId();}
   public void              setOwnerId(Long ownerId)                   { expediente.setOwnerId(ownerId);}

   public Classification    getClassificationClass()                   { return expediente.getClassificationClass();}
   public void              setClassificationClass( Classification classificationClass) { expediente.setClassificationClass(classificationClass);}

   public User              getCreatedBy()                             { return expediente.getCreatedBy();}
   public void              setCreatedBy( User createdBy)              { expediente.setCreatedBy(createdBy);}

   public LocalDateTime     getDateOpened()                            { return expediente.getDateOpened();}
   public void              setDateOpened( LocalDateTime dateOpened)   { expediente.setDateOpened(dateOpened);}

   public LocalDateTime     getDateClosed()                            { return expediente.getDateClosed();}
   public void              setDateClosed( LocalDateTime dateClosed)   { expediente.setDateClosed(dateClosed);}

   public Schema            getMetadataSchema()                        { return expediente.getMetadataSchema();}
   public void              setMetadataSchema ( Schema metadataSchema) { expediente.setMetadataSchema(metadataSchema);}

   public SchemaValues      getMetadata()                              { return expediente.getMetadata();}
   public void              setMetadata ( SchemaValues metadata)       { expediente.setMetadata(metadata);}

   public String            getExpedienteCode()                        { return expediente.getExpedienteCode();}
   public void              setExpedienteCode ( String expedienteCode) { expediente.setExpedienteCode(expedienteCode);}

   public String            getPath()                                  { return expediente.getPath();}
   public void              setPath ( String path)                     { expediente.setPath(path);}

   public String            getKeywords()                              { return expediente.getKeywords();}
   public void              setKeywords( String keywords)              { expediente.setKeywords(keywords);}

   public String            getMac()                                   { return expediente.getMac();}
   public void              setMac(String mac)                         { expediente.setMac(mac);}

   public String            getLocation()                              { return expediente.getLocation();}
   public void              setLocation(String location)               { expediente.setLocation(location);}

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

      BaseExpediente that = other.getExpediente();
      return this.expediente.compareTo(that);
   }//compareTo

   // ----------------------- Logic --------------------------


}//Volume

