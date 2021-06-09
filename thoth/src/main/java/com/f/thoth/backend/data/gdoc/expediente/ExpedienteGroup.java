package com.f.thoth.backend.data.gdoc.expediente;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.HierarchicalEntity;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.NeedsProtection;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;

/**
 * Representa un nodo de la jerarquia de expedientes (expediente/sub-expediente/volumen
 */
@Entity
@Table(name = "BRANCH_EXPEDIENTE")
public class ExpedienteGroup extends AbstractEntity implements  NeedsProtection, HierarchicalEntity<String>, Comparable<ExpedienteGroup>, ExpedienteType
{
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @NotNull  (message = "{evidentia.expediente.required}")
  protected BaseExpediente       expediente;                 // Expediente that describes this branch

  // ------------- Constructors ------------------
  public ExpedienteGroup()
  {
    super();
    this.expediente = new BaseExpediente();
    setType();
  }//ExpedienteGroup null constructor



  public ExpedienteGroup( BaseExpediente expediente)// , Set<BaseExpediente> children)
  {
    super();

    if ( expediente == null )
      throw new IllegalArgumentException("Expediente base del grupo de expedientes no puede ser nulo");

    this.expediente = expediente;
    setType();

  }//ExpedienteGroup constructor



  // -------------- Getters & Setters ----------------

  public BaseExpediente       getExpediente()      { return expediente;}
  public void                 setExpediente(BaseExpediente expediente){ this.expediente = expediente; setType();}

  private void                setType()
  {
    if( expediente != null)
      expediente.setType(Nature.GRUPO);
  }

  // ------------------------ Hereda de BaseExpediente -------------------------

  public void              setName ( String name)                    { expediente.setName(name);}

  @Override public Nature  getType()                                 { return expediente.getType();}
  @Override public boolean isOfType( Nature type)                    { return expediente.isOfType(type);}
  public void              setType ( Nature type)                    { expediente.setType(Nature.GRUPO);}

  public Boolean           getOpen()                                 { return expediente.getOpen();}
  public void              setOpen ( Boolean open)                   { expediente.setOpen(open);}

  public void              setObjectToProtect(ObjectToProtect objectToProtect) { expediente.setObjectToProtect(objectToProtect);}

  public Long              getOwnerId()                              { return expediente.getOwnerId();}
  public void              setOwnerId(Long ownerId)                  { expediente.setOwnerId(ownerId);}

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

  public String            getKeywords()                              { return expediente.getKeywords();}
  public void              setKeywords( String keywords)              { expediente.setKeywords(keywords);}

  public String            getMac()                                   { return expediente.getMac();}
  public void              setMac(String mac)                         { expediente.setMac(mac);}


  // --------------------------- Implements HierarchicalEntity ---------------------------------------

  @Override public String            getName()           { return expediente.getName();}

  @Override public String            getCode()           { return expediente.getCode();}

  @Override public String            getOwner()          { return expediente.getOwner();}

  @Override public String            formatCode()        { return expediente.formatCode();}


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

    if (!(o instanceof ExpedienteGroup ))
      return false;

    ExpedienteGroup that = (ExpedienteGroup) o;
    return this.id != null && this.id.equals(that.id);

  }//equals

  @Override public int hashCode() { return id == null? 490277: id.hashCode();}

  @Override public String toString()
  {
    StringBuilder s = new StringBuilder();
    s.append( "ExpedienteGroup{")
     .append( super.toString())
     .append( "expediente["+ expediente.getCode())
     .append("]\n     }\n");

    return s.toString();
  }//toString

  @Override  public int compareTo(ExpedienteGroup that) { return that == null? 1: expediente.compareTo(that.getExpediente());}


  // --------------- Logic ------------------------------

  public void openExpediente()
  {
    if ( !expediente.isOpen())
    {  expediente.openExpediente();
      }
  }//openExpediente


  public void closeExpediente()
  {
    if( expediente.isOpen())
    {  expediente.closeExpediente();
    }
  }//closeExpediente

}//ExpedienteGroup
