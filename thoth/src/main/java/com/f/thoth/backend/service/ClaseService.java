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
import com.f.thoth.backend.data.gdoc.classification.Clase;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ClaseRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class ClaseService implements FilterableCrudService<Clase>, PermissionService<Clase>
{

   private final ClaseRepository             claseRepository;
   private final PermissionRepository<Clase> permissionRepository;

   @Autowired
   public ClaseService(ClaseRepository claseRepository, PermissionRepository<Clase> permissionRepository)
   {
      this.claseRepository = claseRepository;
      this.permissionRepository      = permissionRepository;
   }

   @Override public Page<Clase> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return claseRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return claseRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = claseRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<Clase> find(Pageable pageable)
   {
      return claseRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override public JpaRepository<Clase, Long> getRepository()
   {
      return claseRepository;
   }

   @Override public Clase createNew(User currentUser)
   {
      Clase objectToProtect = new Clase();
      objectToProtect.setTenant(ThothSession.getCurrentTenant()); 
      return objectToProtect;
   }

   @Override public Clase save(User currentUser, Clase entity)
   {
      try {
         return FilterableCrudService.super.save(currentUser, entity);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un Objeto con esa llave. Por favor escoja una llave única para el objeto");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<Clase> findAll() { return claseRepository.findAll(ThothSession.getCurrentTenant()); }

   @Override public Optional<Clase> findById(Long id)              { return claseRepository.findById( id);}

   @Override public List<Clase> findByParent  ( Clase owner) { return claseRepository.findByParent  (owner); }
   @Override public int         countByParent ( Clase owner) { return claseRepository.countByParent (owner); }
   @Override public boolean     hasChildren   ( Clase object){ return claseRepository.countByChildren(object) > 0; }

   @Override public List<Clase> findByNameLikeIgnoreCase (Tenant tenant, String name) { return claseRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long        countByNameLikeIgnoreCase(Tenant tenant, String name) { return claseRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   { 
      List<Clase> objects = claseRepository.findClasesGranted(role);
      return  permissionRepository.findByObjects(objects);
   }
   
   @Override public List<Clase> findObjectsGranted( Role role)
   {
      return claseRepository.findClasesGranted(role);
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
         Optional<Clase> optObject= claseRepository.findById(newGrant.getObjectId());
         if ( optObject.isPresent())
         {
            permissionRepository.saveAndFlush(newGrant);
            Clase object = optObject.get();
            object.grant(newGrant);
            claseRepository.saveAndFlush(object);
        }
      });
   }//grant

   
   public void revoke( User currentUser, Role role, Set<Permission> newRevokes)
   {
      newRevokes.forEach( newRevoke-> 
      {
         Optional<Clase> optObject= claseRepository.findById(newRevoke.getObjectId());
         if ( optObject.isPresent())
         {
            Clase object = optObject.get();
            Permission toRevoke = permissionRepository.findByRoleObject(newRevoke.getRole(),newRevoke.getObjectToProtect());
            object.revoke(toRevoke);
            claseRepository.saveAndFlush(object);
            permissionRepository.delete(toRevoke);
        }
      });

   }//grant



}//ClaseService
