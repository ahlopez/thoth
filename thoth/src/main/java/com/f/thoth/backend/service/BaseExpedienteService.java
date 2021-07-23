package com.f.thoth.backend.service;

import static com.f.thoth.Parm.CURRENT_USER;
import static com.f.thoth.Parm.PATH_SEPARATOR;
import static com.f.thoth.Parm.TENANT;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.BaseExpedienteRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class BaseExpedienteService implements FilterableCrudService<BaseExpediente>, PermissionService<BaseExpediente>
{
  private final BaseExpedienteRepository     baseExpedienteRepository;
  private final PermissionRepository         permissionRepository;
  private final ObjectToProtectRepository    objectToProtectRepository;

  @Autowired
  public BaseExpedienteService(  BaseExpedienteRepository   baseExpedienteRepository,
      PermissionRepository       permissionRepository,
      ObjectToProtectRepository  objectToProtectRepository)
  {
    this.baseExpedienteRepository    = baseExpedienteRepository;
    this.permissionRepository        = permissionRepository;
    this.objectToProtectRepository   = objectToProtectRepository;
  }//BaseExpedienteService constructor


  @Override public Page<BaseExpediente> findAnyMatching(Optional<String> filter, Pageable pageable)
  {
    if (filter.isPresent())
    {
      String repositoryFilter = "%" + filter.get() + "%";
      return baseExpedienteRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
    }
    else
    {
      return find(pageable);
    }
  }//findAnyMatching


  @Override public long countAnyMatching(Optional<String> filter)
  {
    if (filter.isPresent())
    {
      String repositoryFilter = "%" + filter.get() + "%";
      return baseExpedienteRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
    }
    else
    {
      long n = baseExpedienteRepository.countAll(tenant());
      return n;
    }
  }//countAnyMatching


  public Page<BaseExpediente> find(Pageable pageable)
  { return baseExpedienteRepository.findBy(tenant(), pageable);}

  @Override public JpaRepository<BaseExpediente, Long> getRepository()
  { return baseExpedienteRepository;}

  @Override public BaseExpediente createNew(User currentUser)
  {
    BaseExpediente   baseExpediente   = new BaseExpediente();
    baseExpediente.setTenant(tenant());
    baseExpediente.setCreatedBy(null /*TODO: currentUser*/);
    return baseExpediente;

  }//createNew

  @Override public BaseExpediente save(User currentUser, BaseExpediente expediente)
  {
    try
    {
      ObjectToProtect associatedObject = expediente.getObjectToProtect();
      if ( !associatedObject.isPersisted())
        objectToProtectRepository.saveAndFlush( associatedObject);

      return FilterableCrudService.super.save(currentUser, expediente);
    }
    catch (DataIntegrityViolationException e)
    {
      throw new UserFriendlyDataException("Ya hay un expediente con esa identificación. Por favor escoja un identificador único para el expediente");
    }

  }//save


  //  ----- implements HierarchicalService ------
  @Override public List<BaseExpediente>     findAll()                           {return baseExpedienteRepository.findAll(tenant());}
  @Override public Optional<BaseExpediente> findById(Long id)                   {return baseExpedienteRepository.findById( id);}

  @Override public List<BaseExpediente>     findByParent( BaseExpediente owner) {return baseExpedienteRepository.findByParent(owner.getId()); }
  @Override public int                      countByParent(BaseExpediente owner) {return baseExpedienteRepository.countByParent(owner.getId()); }
  @Override public boolean               hasChildren( BaseExpediente expediente){return baseExpedienteRepository.countByChildren(expediente.getId()) > 0; }
  public boolean                         hasChildren( Classification clase)     {return baseExpedienteRepository.countByClass(clase) > 0; }
  public boolean   hasChildren( BaseExpediente expediente, Classification clase){return expediente != null?  hasChildren(expediente) : hasChildren(clase);}

  @Override public List<BaseExpediente> findByNameLikeIgnoreCase (Tenant tenant, String name)
  { return baseExpedienteRepository.findByNameLikeIgnoreCase (tenant, name);}

  @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
  { return baseExpedienteRepository.countByNameLikeIgnoreCase(tenant, name);}

  public List<BaseExpediente>   findByClass (Classification clase){ return baseExpedienteRepository.findByClass(clase);}

  public int                    countByClass(Classification clase){ return baseExpedienteRepository.countByClass(clase); }

  public List<BaseExpediente>   findByNameLikeIgnoreCase (Tenant tenant, String name, Classification clase)
  { return baseExpedienteRepository.findByNameLikeIgnoreCase (tenant, name, clase);}

  public long  countByNameLikeIgnoreCase(Tenant tenant, String name, Classification clase)
  { return baseExpedienteRepository.countByNameLikeIgnoreCase(tenant, name, clase);}

  public Optional<BaseExpediente> findByPath( String path)                 {return baseExpedienteRepository.findByPath(tenant(), path);}

  //  --------  Permission handling ---------------------

  @Override public List<Permission> findGrants( Role role)
  {
    List<BaseExpediente>  expedientes = baseExpedienteRepository.findExpedientesGranted(role);
    List<ObjectToProtect>     objects = new ArrayList<>();
    expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));
    return  permissionRepository.findByObjects(objects);
  }//findGrants

  @Override public List<BaseExpediente> findObjectsGranted( Role role)
  {  return baseExpedienteRepository.findExpedientesGranted(role);}

  public void grantRevoke( User currentUser, Role role, Set<Permission> newGrants, Set<Permission> newRevokes)
  {
    grant ( currentUser, role, newGrants);
    revoke( currentUser, role, newRevokes);

  }//grantRevoke

  public void grant( User currentUser, Role role, Set<Permission> newGrants)
  {
    newGrants.forEach( newGrant->
    {
      ObjectToProtect objectOfClass= newGrant.getObjectToProtect();
      if ( !newGrant.isPersisted())
        permissionRepository.saveAndFlush(newGrant);

      objectOfClass.grant(newGrant);
      objectToProtectRepository.saveAndFlush(objectOfClass);
    });
  }//grant


  public void revoke( User currentUser, Role role, Set<Permission> newRevokes)
  {
    newRevokes.forEach( newRevoke->
    {
      ObjectToProtect objectOfClass= newRevoke.getObjectToProtect();
      Permission toRevoke = permissionRepository.findByRoleObject(newRevoke.getRole(),objectOfClass);
      if ( toRevoke != null)
      {
        objectOfClass.revoke(toRevoke);
        objectToProtectRepository.saveAndFlush(objectOfClass);
        permissionRepository.delete(toRevoke);
      }
    });

  }//revoke

  private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }
  
  // ------------------------ JCR ------------------------------

  public void createClassRoot(String expedienteRootPath, Classification rootClass)
      throws RepositoryException, UnknownHostException
  {
     VaadinSession vSession = VaadinSession.getCurrent();
     User         user      = (User)vSession.getAttribute(CURRENT_USER);
     String       code      = ""+ rootClass.getId();
     String       clazzPath =  expedienteRootPath+ PATH_SEPARATOR+ code;
     Repo.getInstance().addNode( clazzPath, code, user.getEmail());
  }//createClassRoot
  
  
  
  public Node createJCRGroup( BaseExpediente base)
        throws RepositoryException, UnknownHostException
  {
     Node jcrGroup = createJCRExpediente(base, null);
     jcrGroup.setProperty("jcr:nodeTypeName", Nature.GRUPO.toString());
     return jcrGroup;
  }//createJCRGroup
  
  
  
  public void  createJCRVolume(Node jcrVol, Volume volume)
        throws RepositoryException, UnknownHostException
  {
     VaadinSession vSession = VaadinSession.getCurrent();
     Tenant          tenant = (Tenant)vSession.getAttribute(TENANT);
     String       namespace = tenant.getName()+ ":";
     String       volNature = Nature.VOLUMEN.toString();
     jcrVol.setProperty("jcr:nodeTypeName", volNature);
     jcrVol.setProperty(namespace+ "currentInstance", ""+ volume.getCurrentInstance());
  }//createJCRVolume
  

  
  public void createJCRInstance(VolumeInstance instance, Node jcrVol)
     throws RepositoryException, UnknownHostException
  {
     VaadinSession vSession = VaadinSession.getCurrent();
     Tenant          tenant = (Tenant)vSession.getAttribute(TENANT);
     User              user = (User)vSession.getAttribute(CURRENT_USER);
     String       namespace = tenant.getName()+ ":";
     Volume          volume = instance.getVolume();
     Integer instanceNumber = instance.getInstance();
     String            path = volume.getPath()+ Parm.PATH_SEPARATOR+ instanceNumber;
     Node       jcrInstance = Repo.getInstance().addNode(path, volume.getName()+ " instance "+ instanceNumber, user.getEmail());
     jcrInstance.setProperty(namespace+ "instance", instanceNumber);
     jcrInstance.setProperty(namespace+ "open",     instance.getOpen());
     jcrInstance.setProperty(namespace+ "opened",   TextUtil.formatDateTime(instance.getDateOpened()));
     jcrInstance.setProperty(namespace+ "closed",   TextUtil.formatDateTime(instance.getDateClosed()));
     //TODO:   Simular  - FCN:location            ( STRING    )                     // Physical archive location (topographic signature)
  }//createJCRInstance
  


  private Node createJCRExpediente(BaseExpediente base, Set<DocumentType> admissibleTypes)
        throws RepositoryException, UnknownHostException
  {
     Tenant tenant    = base.getTenant();
     String namespace = tenant.getName()+ ":";
     Node node = Repo.getInstance().addNode( base.getPath(), base.getName(), base.getCreatedBy().getEmail());
     node.addMixin   ( "mix:referenceable");
     node.setProperty(namespace+ "tenant",         tenant.getId());
     node.setProperty(namespace+ "expedienteCode", base.formatCode());
     Node clase = Repo.getInstance().findNode( base.getClassificationClass().getPath());
     node.setProperty(namespace+ "classification", clase.getIdentifier());
     if (admissibleTypes != null)
     {  for( DocumentType admissibleType: admissibleTypes)
        {  node.setProperty(namespace+ "admissibleTypes", admissibleType.getCode());
        }
     }
     node.setProperty(namespace+ "open",           base.isOpen());
     if ( base.isOpen())
     {  node.setProperty(namespace+ "dateOpened",  TextUtil.formatDateTime(base.getDateOpened()));
     }  else
     {  node.setProperty(namespace+ "dateClosed",  TextUtil.formatDateTime(base.getDateClosed()));
     }
     if (base.getLocation() != null)
     {  node.setProperty(namespace+ "location",    base.getLocation());
     }
     if (TextUtil.isNotEmpty( base.getKeywords()))
     {  String[] keywords = base.getKeywords().split(Parm.VALUE_SEPARATOR);
        for( String keyword: keywords)
        {  node.setProperty(namespace+ "keywords", keyword);
        }
     }
     if ( base.getMac() != null )
     {  node.setProperty(namespace+ "mac", base.getMac());
     }
     Schema schema = base.getMetadataSchema();
     if ( schema != null)
     {  Repo.getInstance().updateMixin( node, namespace, schema, base.getMetadata());
     }
     Repo.getInstance().save();
     return node;

  }//createJCRExpediente
  

}//BaseExpedienteService
