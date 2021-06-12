package com.f.thoth.backend.service;

import static com.f.thoth.Parm.EXPEDIENTE_ROOT;
import static com.f.thoth.Parm.TENANT;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

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
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.f.thoth.backend.repositories.VolumeRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class VolumeService implements FilterableCrudService<Volume>, PermissionService<Volume>
{
   private final VolumeRepository             volumeRepository;
   private final PermissionRepository         permissionRepository;
   private final ObjectToProtectRepository    objectToProtectRepository;

   @Autowired
   public VolumeService(VolumeRepository           volumeRepository,
                        PermissionRepository       permissionRepository,
                        ObjectToProtectRepository  objectToProtectRepository)
   {
      this.volumeRepository            = volumeRepository;
      this.permissionRepository        = permissionRepository;
      this.objectToProtectRepository   = objectToProtectRepository;
   }//VolumeService constructor

   @Override public Page<Volume> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return volumeRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else
      {
         return find(pageable);
      }
   }//findAnyMatching


   @Override public long countAnyMatching(Optional<String> filter)
   {  long n = filter.isPresent()
             ? volumeRepository.countByNameLikeIgnoreCase(tenant(), "%" + filter.get() + "%")
             : volumeRepository.countAll(tenant());
      
      return n;
                   
   }//countAnyMatching


   public Page<Volume> find(Pageable pageable) { return volumeRepository.findAll(tenant(), pageable); }
   public Volume  findByCode(String code)      { return volumeRepository.findByCode(code);}

   @Override public JpaRepository<Volume, Long> getRepository() { return volumeRepository; }

   @Override public Volume createNew(User currentUser)
   {
      BaseExpediente baseExpediente = new BaseExpediente();
      baseExpediente.setType(Nature.VOLUMEN);
      baseExpediente.setTenant(tenant());
      baseExpediente.setCreatedBy(null/*TODO: currentUser*/);

      Volume volume = new Volume(baseExpediente, Nature.VOLUMEN, 0, new TreeSet<DocumentType>());
      return volume;
   }//createNew

   @Override public Volume save(User currentUser, Volume Volume)
   {
      try
      {  Volume volume = FilterableCrudService.super.save(currentUser, Volume);
         saveJCRVolume(currentUser, volume);
         return volume;
      } catch (DataIntegrityViolationException e)
      {  throw new UserFriendlyDataException("Ya hay un volumen con esa identificación. Por favor escoja un identificador único para el volumen");
      }

   }//save
   
   
   private void saveJCRVolume(User currentUser, Volume volume)
   {
      try
      {
         VaadinSession vSession   = VaadinSession.getCurrent();
         String      parentPath   = (String)vSession.getAttribute(EXPEDIENTE_ROOT);
         Long          parentId   = volume.getOwnerId();
         if (parentId != null)
         {   Optional<Volume> parent = volumeRepository.findById(parentId);
             if (parent.isPresent()) 
             {  parentPath =  parent.get().getPath();
             }
         }
         Node groupJCR = addJCRChild( currentUser, parentPath, volume);
         updateJCRVolume(groupJCR, volume);
      } catch(Exception e)
      {
         throw new IllegalStateException("*** No pudo guardar estructura de clasificación en el repositorio. Razón\n"+ e.getLocalizedMessage());
      }
   }//saveJCRVolume


   private Node addJCRChild(User currentUser, String parentPath, Volume volume)
         throws RepositoryException, UnknownHostException
   {
      String volumeCode = volume.getExpedienteCode();
      String  childPath = parentPath+ Parm.PATH_SEPARATOR+ volumeCode;
      Node        child = Repo.getInstance().addNode(childPath, volume.getName(), currentUser.getEmail());
      child.setProperty("jcr:nodeType", NodeType.VOLUMEN.getCode());
      child.setProperty("evid:code",    volumeCode);
      return child;
   }//addJCRChild
   
   
   private void updateJCRVolume(Node groupJCR, Volume volume)
   {
      try
      {
         groupJCR.setProperty("evid:type",           Nature.VOLUMEN.toString());
         groupJCR.setProperty("evid:class",          volume.getClassificationClass().formatCode());
         groupJCR.setProperty("evid:schema",         volume.getMetadataSchema().getCode());
         groupJCR.setProperty("evid:opened",         TextUtil.formatDateTime(volume.getDateOpened()));
         groupJCR.setProperty("evid:closed",         TextUtil.formatDateTime(volume.getDateClosed()));
         groupJCR.setProperty("evid:open",           volume.getOpen().toString());
         groupJCR.setProperty("evid:location",       volume.getLocation());
         groupJCR.setProperty("evid:keywords",       volume.getKeywords());
         groupJCR.setProperty("evid:curentInstance", volume.getCurrentInstance().toString());
         
         // TODO: Revisar como incorporar los campos objectToProtect, metadata, expedienteIndex, mac, admissibleTypes en el repositorio
         // protected ObjectToProtect   objectToProtect;            // Associated security object
         // protected SchemaValues      metadata;                   // Metadata values of the associated expediente
         // protected ExpedienteIndex   expedienteIndex;            // Expediente index entries
         // protected String            mac;                        // Message authentication code
         // protected Set<DocumentType> admissibleTypes;            // Admissible document types in this volume

      } catch(Exception e)
      {   throw new IllegalStateException("No pudo actualizar volumen["+ volume.formatCode()+ "] en el repositorio. Razón\n"+ e.getMessage());
      }
     
   }//updateJCRVolume


   //TODO: Falta implementar el delete en el repositorio JCR


   //  ----- implements HierarchicalService ------
   @Override public List<Volume>      findAll()                     { return volumeRepository.findAll(tenant());}
   @Override public Optional<Volume>  findById(Long id)             { return volumeRepository.findById( id);}
   @Override public List<Volume>      findByParent ( Volume owner)  { return volumeRepository.findByParent(owner.getId());}
   @Override public int               countByParent( Volume owner)  { return volumeRepository.countByParent (owner.getId());}
   @Override public boolean           hasChildren  ( Volume volume)
   { return volume.getCurrentInstance() > 0 || volumeRepository.countByChildren(volume.getId()) > 0;
   }

   @Override public List<Volume> findByNameLikeIgnoreCase (Tenant tenant, String name)
             { return volumeRepository.findByNameLikeIgnoreCase (tenant, name);}
   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
             { return volumeRepository.countByNameLikeIgnoreCase(tenant, name);}

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<Volume>       expedientes = volumeRepository.findExpedientesGranted(role);
      List<ObjectToProtect>  objects = new ArrayList<>();
      expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));

      return  permissionRepository.findByObjects(objects);

   }//findGrants

   @Override public List<Volume> findObjectsGranted( Role role)
             { return volumeRepository.findExpedientesGranted(role);}

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

}//VolumeService
