package com.f.thoth.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.f.thoth.backend.repositories.VolumeInstanceRepository;

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
         return volumeInstanceRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else
      { return find(pageable);
      }
   }//findAnyMatching


   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return volumeInstanceRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else
      {  long n = volumeInstanceRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching


   public Page<VolumeInstance> find(Pageable pageable)                    { return volumeInstanceRepository.findAll(ThothSession.getCurrentTenant(), pageable); }

   public VolumeInstance  findByInstanceCode(Volume volume, Integer code) { return volumeInstanceRepository.findByInstanceCode(volume, code);}

   @Override public JpaRepository<VolumeInstance, Long> getRepository()   { return volumeInstanceRepository; }

   @Override public VolumeInstance createNew(User currentUser)
   {
      Volume volume = new Volume();
      volume.getExpediente().setTenant(ThothSession.getCurrentTenant());
      volume.setCreatedBy(null/*TODO: currentUser*/);
      
      LocalDateTime now = LocalDateTime.now();
      VolumeInstance volInstance = new VolumeInstance(volume, 0, "", now, now.plusYears(1000L));
      return volInstance;
   }//createNew

   @Override public VolumeInstance save(User currentUser, VolumeInstance VolumeInstance)
   {
      try
      { return FilterableCrudService.super.save(currentUser, VolumeInstance);
      } catch (DataIntegrityViolationException e)
      { 
         throw new UserFriendlyDataException("Ya hay una instancia de volumen con esa identificación. Por favor escoja un identificador único para la instancia");
      }
   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<VolumeInstance>     findAll()                     { return volumeInstanceRepository.findAll(ThothSession.getCurrentTenant());}
   
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

}//VolumeInstanceService
