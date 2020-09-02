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

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.classification.ClassificationClass;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ClassificationClassRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class ClassificationClassService implements FilterableCrudService<ClassificationClass>, PermissionService<ClassificationClass>
{
   private final ClassificationClassRepository  claseRepository;
   private final PermissionRepository           permissionRepository;
   private final ObjectToProtectRepository      objectToProtectRepository;

   @Autowired
   public ClassificationClassService(ClassificationClassRepository claseRepository, 
                                     PermissionRepository permissionRepository,
                                     ObjectToProtectRepository objectToProtectRepository)
   {
      this.claseRepository           = claseRepository;
      this.permissionRepository      = permissionRepository;
      this.objectToProtectRepository = objectToProtectRepository;
   }

   @Override public Page<ClassificationClass> findAnyMatching(Optional<String> filter, Pageable pageable)
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

   public Page<ClassificationClass> find(Pageable pageable)
   {
      return claseRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override public JpaRepository<ClassificationClass, Long> getRepository()
   {
      return claseRepository;
   }

   @Override public ClassificationClass createNew(User currentUser)
   {
      ClassificationClass clase = new ClassificationClass();
      clase.setTenant(ThothSession.getCurrentTenant()); 
      return clase;
   }//createNew

   @Override public ClassificationClass save(User currentUser, ClassificationClass entity)
   {
      try {
         objectToProtectRepository.save( entity.getObjectToProtect());
         return FilterableCrudService.super.save(currentUser, entity);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay una Clase con esa llave. Por favor escoja una llave única para la clase");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<ClassificationClass> findAll() { return claseRepository.findAll(ThothSession.getCurrentTenant()); }

   @Override public Optional<ClassificationClass> findById(Long id)  { return claseRepository.findById( id);}

   @Override public List<ClassificationClass>  findByParent  ( ClassificationClass owner) { return claseRepository.findByParent(owner); }
   @Override public int         countByParent ( ClassificationClass owner) { return claseRepository.countByParent (owner); }
   @Override public boolean     hasChildren   ( ClassificationClass clase){ return claseRepository.countByChildren(clase) > 0; }

   @Override public List<ClassificationClass> findByNameLikeIgnoreCase (Tenant tenant, String name) 
       { return claseRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name) 
       { return claseRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   { 
      List<ClassificationClass> clases = claseRepository.findClasesGranted(role);
      List<ObjectToProtect>    objects = new ArrayList<>();
      clases.forEach( clas-> objects.add(clas.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants
   
   @Override public List<ClassificationClass> findObjectsGranted( Role role)
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

   }//grant



}//ClaseService
