package com.f.thoth.backend.service;

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

import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.f.thoth.backend.repositories.VolumeRepository;

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
         return volumeRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else
      {
         return find(pageable);
      }
   }//findAnyMatching


   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return volumeRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else
      {
         long n = volumeRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching


   public Page<Volume> find(Pageable pageable) { return volumeRepository.findAll(ThothSession.getCurrentTenant(), pageable); }
   public Volume  findByCode(String code)      { return volumeRepository.findByCode(ThothSession.getCurrentTenant(), code);}

   @Override public JpaRepository<Volume, Long> getRepository() { return volumeRepository; }

   @Override public Volume createNew(User currentUser)
   {
      BaseExpediente baseExpediente = new BaseExpediente();
      baseExpediente.setTenant(ThothSession.getCurrentTenant());
      baseExpediente.setCreatedBy(null/*TODO: currentUser*/);

      LeafExpediente leafExpediente = new LeafExpediente();
      leafExpediente.setExpediente(baseExpediente);

      Volume expediente = new Volume();
      expediente.setExpediente(leafExpediente);
      return expediente;
   }//createNew

   @Override public Volume save(User currentUser, Volume Volume)
   {
      try
      {
         ObjectToProtect associatedObject = Volume.getObjectToProtect();
         if ( !associatedObject.isPersisted())
            objectToProtectRepository.saveAndFlush( associatedObject);

         return FilterableCrudService.super.save(currentUser, Volume);
      } catch (DataIntegrityViolationException e)
      {
         throw new UserFriendlyDataException("Ya hay un volumen con esa identificación. Por favor escoja un identificador único para el volumen");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<Volume>      findAll()                     { return volumeRepository.findAll(ThothSession.getCurrentTenant());}
   @Override public Optional<Volume>  findById(Long id)             { return volumeRepository.findById( id);}
   @Override public List<Volume>      findByParent ( Volume owner)  { return volumeRepository.findByParent(owner.getId());}
   @Override public int               countByParent( Volume owner)  { return volumeRepository.countByParent (owner.getId());}
   @Override public boolean           hasChildren  ( Volume volume) { return volumeRepository.countByChildren(volume.getId()) > 0;}

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

}//VolumeService
