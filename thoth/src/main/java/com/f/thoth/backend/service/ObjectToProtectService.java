package com.f.thoth.backend.service;

import java.util.Collection;
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
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;

@Service
public class ObjectToProtectService implements FilterableCrudService<ObjectToProtect>, PermissionService<ObjectToProtect>
{

   private final ObjectToProtectRepository objectToProtectRepository;

   @Autowired
   public ObjectToProtectService(ObjectToProtectRepository objectToProtectRepository)
   {
      this.objectToProtectRepository = objectToProtectRepository;
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
         // return objectToProtectRepository.saveAndFlush(entity);
         Optional<ObjectToProtect> opt2 = objectToProtectRepository.findById( entity.getId());
         if ( opt2.isPresent())
         {
            ObjectToProtect obj2 = opt2.get();
            System.out.println(obj2.toString());
         }
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
   @Override public boolean               hasChildren   ( ObjectToProtect object) { return objectToProtectRepository.countByChildren(object) > 0; }

   @Override public List<ObjectToProtect> findByNameLikeIgnoreCase (Tenant tenant, String name) { return objectToProtectRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long                  countByNameLikeIgnoreCase(Tenant tenant, String name) { return objectToProtectRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<ObjectToProtect> findGrants( Role role){ return objectToProtectRepository.findGrants(role); }

   public void grantRevoke( User currentUser, Role role, Set<ObjectToProtect> newGrants, Set<ObjectToProtect> newRevokes)
   {
      newGrants.forEach( grant-> 
      {
         ObjectToProtect x = null;
         Optional<ObjectToProtect> optObject= objectToProtectRepository.findById(grant.getId());
         if ( optObject.isPresent())
         {
            ObjectToProtect object = optObject.get();
            object.grant(role);
            objectToProtectRepository.saveAndFlush(object);
            optObject= objectToProtectRepository.findById(object.getId());
            if ( optObject.isPresent())
            {
               x  = optObject.get();       
               System.out.println(x.toString());
            }
         }
      });
      List<ObjectToProtect> g = findGrants(role);
      
      newRevokes.forEach( revoke->
      {
         Optional<ObjectToProtect> optObject= objectToProtectRepository.findById(revoke.getId());
         if ( optObject.isPresent())
         {
            ObjectToProtect object = optObject.get();
            object.revoke(role);
            objectToProtectRepository.saveAndFlush(object);
         }
      });
   }//grantRevoke
   
   public void grant(User currentUser, Role role, Collection<ObjectToProtect>newGrants)
   {
      newGrants.forEach( objectToProtect->  
      {
         objectToProtect.grant(role);
         save(currentUser, objectToProtect);
      });
   }//grant


   public void revoke(User currentUser, Role role, Collection<ObjectToProtect> revokedGrants)
   {
      revokedGrants.forEach( objectToProtect->  
      {
         objectToProtect.revoke(role);
         save(currentUser, objectToProtect);
      });
   }//revoke


}//ObjectToProtectService
