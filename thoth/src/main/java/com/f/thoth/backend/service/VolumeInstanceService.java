package com.f.thoth.backend.service;

import static com.f.thoth.Parm.TENANT;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
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
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.f.thoth.backend.repositories.VolumeInstanceRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class VolumeInstanceService  implements FilterableCrudService<VolumeInstance>, PermissionService<VolumeInstance>
{
   private final VolumeInstanceRepository     volumeInstanceRepository;
   private final PermissionRepository         permissionRepository;
   private final ObjectToProtectRepository    objectToProtectRepository;

   @Autowired
   public VolumeInstanceService(VolumeInstanceRepository   volumeInstanceRepository,
                                PermissionRepository       permissionRepository,
                                ObjectToProtectRepository  objectToProtectRepository)
   {
      this.volumeInstanceRepository    = volumeInstanceRepository;
      this.permissionRepository        = permissionRepository;
      this.objectToProtectRepository   = objectToProtectRepository;
   }//VolumeService constructor


   @Override public Page<VolumeInstance> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return volumeInstanceRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else
      { return find(pageable);
      }
   }//findAnyMatching


   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return volumeInstanceRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else
      {  long n = volumeInstanceRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching


   public Page<VolumeInstance> find(Pageable pageable)                    { return volumeInstanceRepository.findAll(tenant(), pageable); }

   public VolumeInstance  findByInstanceCode(Volume volume, Integer code) { return volumeInstanceRepository.findByInstanceCode(volume, code);}

   @Override public JpaRepository<VolumeInstance, Long> getRepository()   { return volumeInstanceRepository; }

   @Override public VolumeInstance createNew(User currentUser)
   {
      Volume volume = new Volume();
      volume.getExpediente().setTenant(tenant());
      volume.setCreatedBy(null/*TODO: currentUser*/);

      LocalDateTime now = LocalDateTime.now();
      VolumeInstance volInstance = new VolumeInstance(volume, 0, "", now, now.plusYears(1000L));
      return volInstance;
   }//createNew

   @Override public VolumeInstance save(User currentUser, VolumeInstance VolumeInstance)
   {
      try
      { VolumeInstance instance = FilterableCrudService.super.save(currentUser, VolumeInstance);
        saveJCRInstance(currentUser, instance);
        return instance;
      } catch (DataIntegrityViolationException e)
      {
         throw new UserFriendlyDataException("Ya hay una instancia de volumen con esa identificación. Por favor escoja un identificador único para la instancia");
      }
   }//save



   private void saveJCRInstance(User currentUser, VolumeInstance instance)
   {
      try
      {
         String  parentPath  = instance.getVolume().getPath();
         Node    instanceJCR = addJCRChild( currentUser, parentPath, instance);
         updateJCRInstance(instanceJCR, instance);
      } catch(Exception e)
      {
         throw new IllegalStateException("*** No pudo guardar estructura de clasificación en el repositorio. Razón\n"+ e.getLocalizedMessage());
      }
   }//saveJCRVolume


   private Node addJCRChild(User currentUser, String parentPath, VolumeInstance instance)
         throws RepositoryException, UnknownHostException
   {
      String namespace    = currentUser.getTenant().getName()+ ":";
      Volume       volume = instance.getVolume();
      String instanceCode = instance.getInstance().toString();
      String    childPath = parentPath+ Parm.PATH_SEPARATOR+ ""+ instanceCode;
      Node          child = Repo.getInstance().addNode(childPath, "volume "+ volume.getName()+ " - instance "+ instanceCode, currentUser.getEmail());
      child.setProperty("jcr:nodeType", NodeType.VOLUME_INSTANCE.toString());
      child.setProperty(namespace+ "code",    instance.formatCode());
      return child;
   }//addJCRChild


   private void updateJCRInstance(Node instanceJCR, VolumeInstance instance)
   {
      try
      {
         String namespace    = instance.getTenant().getName()+ ":";
         instanceJCR.setProperty(namespace+ "type",     NodeType.VOLUME_INSTANCE.getCode());
         instanceJCR.setProperty(namespace+ "instance", instance.getInstance().toString());
         instanceJCR.setProperty(namespace+ "location", instance.getLocation());
         instanceJCR.setProperty(namespace+ "open",     instance.isOpen());
         instanceJCR.setProperty(namespace+ "opened",   TextUtil.formatDateTime(instance.getDateOpened()));
         instanceJCR.setProperty(namespace+ "closed",   TextUtil.formatDateTime(instance.getDateClosed()));
      } catch(Exception e)
      {   throw new IllegalStateException("No pudo actualizar instancia["+ instance.formatCode()+ "] en el repositorio. Razón\n"+ e.getMessage());
      }

   }//updateJCRInstance


   //TODO: Falta implementar el delete en el repositorio JCR


   //  ----- implements HierarchicalService ------
   @Override public List<VolumeInstance>     findAll()                     { return volumeInstanceRepository.findAll(tenant());}

   @Override public Optional<VolumeInstance> findById(Long id)             { return volumeInstanceRepository.findById( id);}

   @Override public List<VolumeInstance>     findByParent (VolumeInstance instance)
   { return instance == null? new ArrayList<>(): volumeInstanceRepository.findByParent(instance.getVolume().getId());
   }

   @Override public int                      countByParent(VolumeInstance instance)
   { return instance ==  null? 0: volumeInstanceRepository.countByParent(instance.getVolume().getId());
   }

   @Override public boolean           hasChildren  ( VolumeInstance volumeInstance)
   { return volumeInstance.getInstance() > 0 || volumeInstanceRepository.countByChildren(volumeInstance.getId()) > 0;
   }

   @Override public List<VolumeInstance> findByNameLikeIgnoreCase (Tenant tenant, String name)
             { return volumeInstanceRepository.findByNameLikeIgnoreCase (tenant, name);}

   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
             { return volumeInstanceRepository.countByNameLikeIgnoreCase(tenant, name);}

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<VolumeInstance>  volumeInstances = volumeInstanceRepository.findVolumeInstancesGranted(role);
      List<ObjectToProtect>  objects = new ArrayList<>();
      volumeInstances.forEach( instance-> objects.add(instance.getVolume().getObjectToProtect()));

      return  permissionRepository.findByObjects(objects);

   }//findGrants

   @Override public List<VolumeInstance> findObjectsGranted( Role role)
             { return volumeInstanceRepository.findVolumeInstancesGranted(role);}

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

}//VolumeInstanceService
