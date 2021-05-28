package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

/**
 * Representa un nodo terminal de la jerarquia de expedientes (expediente/sub-expediente/volumen)
 */
@MappedSuperclass
public class LeafExpediente extends AbstractEntity implements  NeedsProtection, ExpedienteType
{
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @NotNull  (message = "{evidentia.expediente.required}")
   protected BaseExpediente    expediente;                 // Expediente that describes this leaf

   @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
   @NotNull  (message = "{evidentia.types.required}")
   protected Set<DocumentType> admissibleTypes;            // Admisible document types that can be included in the expediente

   // ------------- Constructors ------------------
   public LeafExpediente()
   {
      super();
      this.expediente           = new BaseExpediente();
      this.admissibleTypes      = new TreeSet<>();
   }//LeafExpediente null constructor


   // ------------- LeafExpediente ------------------

   public LeafExpediente( BaseExpediente expediente, Set<DocumentType>admissibleTypes)
   {
      super();

      if ( expediente == null )
         throw new IllegalArgumentException("Expediente asociado a la rama no puede ser nulo");

      this.expediente       = expediente;
      this.admissibleTypes  = (admissibleTypes  == null? new TreeSet<>(): admissibleTypes);

   }//LeafExpediente constructor


   // -------------- Getters & Setters ----------------

   public BaseExpediente    getExpediente()        { return expediente;}
   public void              setExpediente(BaseExpediente expediente){ this.expediente = expediente;}

   public Set<DocumentType> getAdmissibleTypes()   { return admissibleTypes;}
   public void              setAdmissibleTypes(Set<DocumentType> admissibleTypes) { this.admissibleTypes = admissibleTypes;}

  // ------------------------ Hereda de BaseExpediente -------------------------

  public void              setName ( String name)                    { expediente.setName(name);}
  
  public String            getCode()                                 { return expediente.getCode();}

  @Override public Nature  getType()                                 { return expediente == null? null: expediente.getType();}
  @Override public boolean isOfType( Nature type)                    { return expediente != null && expediente.isOfType(type);}
  public void              setType ( Nature type)                    { expediente.setType(Nature.GRUPO);}

  public Boolean           getOpen()                                 { return expediente.getOpen();}
  public void              setOpen ( Boolean open)                   { expediente.setOpen(open);}

  public void              setObjectToProtect(ObjectToProtect objectToProtect) { expediente.setObjectToProtect(objectToProtect);}

  public Long              getOwnerId()                              { return expediente.getOwnerId();}
  public void              setOwnerId(Long ownerId)                  { expediente.setOwnerId(ownerId);}
  public String            getOwner()                                { return expediente.getOwner();}

  public Classification    getClassificationClass()                  { return expediente.getClassificationClass();}
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

  public String            getLocation()                              { return expediente.getLocation();}
  public void              setLocation(String location)               { expediente.setLocation(location);}

  public String            getKeywords()                              { return expediente.getKeywords();}
  public void              setKeywords( String keywords)              { expediente.setKeywords(keywords);}

  public String            getMac()                                   { return expediente.getMac();}
  public void              setMac(String mac)                         { expediente.setMac(mac);}


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
       .append( " expediente["+ expediente.getCode())
       .append( "]\nAdmissibleTypes[\n");

      for ( DocumentType docType: admissibleTypes )
         s.append( " "+ docType.getName());

      s.append("]\n     }\n");

      return s.toString();
   }//toString


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

   // --------------- Logic ------------------------------

}//LeafExpediente
