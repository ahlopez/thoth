package com.f.thoth.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class ObjectToProtectService implements FilterableCrudService<ObjectToProtect>, PermissionService<ObjectToProtect>
{

   private final ObjectToProtectRepository objectToProtectRepository;
   private final PermissionRepository      permissionRepository;

   @Autowired
   public ObjectToProtectService(ObjectToProtectRepository objectToProtectRepository, PermissionRepository permissionRepository)
   {
      this.objectToProtectRepository = objectToProtectRepository;
      this.permissionRepository      = permissionRepository;
   }

   @Override
   public Page<ObjectToProtect> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return objectToProtectRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return objectToProtectRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = objectToProtectRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<ObjectToProtect> find(Pageable pageable)
   {
      return objectToProtectRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<ObjectToProtect, Long> getRepository()
   {
      return objectToProtectRepository;
   }

   @Override
   public ObjectToProtect createNew(User currentUser)
   {
      ObjectToProtect objectToProtect = new ObjectToProtect();
      objectToProtect.setTenant(ThothSession.getCurrentTenant()); 
      return objectToProtect;
   }

   @Override
   public ObjectToProtect save(User currentUser, ObjectToProtect entity)
   {
      try {
         return FilterableCrudService.super.save(currentUser, entity);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un Objeto con esa llave. Por favor escoja una llave única para el objeto");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<ObjectToProtect> findAll() { return objectToProtectRepository.findAll(ThothSession.getCurrentTenant()); }

   @Override public Optional<ObjectToProtect> findById(Long id)              { return objectToProtectRepository.findById( id);}

   @Override public List<ObjectToProtect> findByParent  ( ObjectToProtect owner) { return objectToProtectRepository.findByParent  (owner); }
   @Override public int                   countByParent ( ObjectToProtect owner) { return objectToProtectRepository.countByParent (owner); }
   @Override public boolean               hasChildren   ( ObjectToProtect object){ return objectToProtectRepository.countByChildren(object) > 0; }

   @Override public List<ObjectToProtect> findByNameLikeIgnoreCase (Tenant tenant, String name) { return objectToProtectRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long                  countByNameLikeIgnoreCase(Tenant tenant, String name) { return objectToProtectRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   { 
      List<ObjectToProtect> objects = objectToProtectRepository.findObjectsGranted(role);
      return  permissionRepository.findByObjects(objects);
   }
   
   @Override public List<ObjectToProtect> findObjectsGranted( Role role)
   {
      return objectToProtectRepository.findObjectsGranted(role);
   }

   public void grantRevoke( User currentUser, Role role, Set<Permission> newGrants, Set<Permission> newRevokes)
   {
      grant ( currentUser, role, newGrants);
      revoke( currentUser, role, newRevokes);

   }//grantRevoke
   
   public void grant( User currentUser, Role role, Set<Permission> newGrants)
   {
      newGrants.forEach( newGrant-> 
      {
         List<ObjectToProtect> allObj;
         allObj = objectToProtectRepository.findAll();
         Optional<ObjectToProtect> optObject= objectToProtectRepository.findById(newGrant.getObjectToProtect().getId());
         if ( optObject.isPresent())
         {
            permissionRepository.saveAndFlush(newGrant);
            ObjectToProtect object = optObject.get();
            object.grant(newGrant);
            allObj = objectToProtectRepository.findAll();
            objectToProtectRepository.saveAndFlush(object);
            allObj = objectToProtectRepository.findAll();
            List<Permission> allGrants = permissionRepository.findAll();
            int x = 1;
        }
      });
   }//grant

   
   public void revoke( User currentUser, Role role, Set<Permission> newRevokes)
   {
      newRevokes.forEach( newRevoke-> 
      {
         List<ObjectToProtect> allObj;
         allObj = objectToProtectRepository.findAll();
         Optional<ObjectToProtect> optObject= objectToProtectRepository.findById(newRevoke.getObjectToProtect().getId());
         if ( optObject.isPresent())
         {
            ObjectToProtect object = optObject.get();
            object.revoke(newRevoke);
            allObj = objectToProtectRepository.findAll();
            objectToProtectRepository.saveAndFlush(object);
            permissionRepository.delete(newRevoke);
            allObj = objectToProtectRepository.findAll();
            List<Permission> allGrants = permissionRepository.findAll();
            int x = 1;
        }
      });

   }//grant


}//ObjectToProtectService
