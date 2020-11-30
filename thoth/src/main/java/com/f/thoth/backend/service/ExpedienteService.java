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
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteIndex;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ExpedienteRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class ExpedienteService implements FilterableCrudService<ExpedienteIndex>, PermissionService<ExpedienteIndex>
{
   private final ExpedienteRepository           expedienteRepository;
   private final PermissionRepository           permissionRepository;
   private final ObjectToProtectRepository      objectToProtectRepository;

   @Autowired
   public ExpedienteService(ExpedienteRepository       expedienteRepository,
                            PermissionRepository       permissionRepository,
                            ObjectToProtectRepository  objectToProtectRepository)
   {
      this.expedienteRepository        = expedienteRepository;
      this.permissionRepository        = permissionRepository;
      this.objectToProtectRepository   = objectToProtectRepository;
   }

   @Override public Page<ExpedienteIndex> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return expedienteRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return expedienteRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = expedienteRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<ExpedienteIndex> find(Pageable pageable)
   {
      return expedienteRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override public JpaRepository<ExpedienteIndex, Long> getRepository()
   {
      return expedienteRepository;
   }

   @Override public ExpedienteIndex createNew(User currentUser)
   {
      ExpedienteIndex expedienteIndex = new ExpedienteIndex();
      expedienteIndex.setTenant(ThothSession.getCurrentTenant());
      return expedienteIndex;
   }//createNew

   @Override public ExpedienteIndex save(User currentUser, ExpedienteIndex expedienteIndex)
   {
      try {
         ObjectToProtect associatedObject = expedienteIndex.getObjectToProtect();
         if ( !associatedObject.isPersisted())
            objectToProtectRepository.saveAndFlush( associatedObject);

         return FilterableCrudService.super.save(currentUser, expedienteIndex);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un expediente con esa identificación. Por favor escoja un identificador único para el expediente");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<ExpedienteIndex> findAll() { return expedienteRepository.findAll(ThothSession.getCurrentTenant()); }

   @Override public Optional<ExpedienteIndex> findById(Long id)  { return expedienteRepository.findById( id);}

   @Override public List<ExpedienteIndex>  findByParent ( ExpedienteIndex owner) { return expedienteRepository.findByParent(owner); }
   @Override public int         countByParent ( ExpedienteIndex owner) { return expedienteRepository.countByParent (owner); }
   @Override public boolean     hasChildren   ( ExpedienteIndex expedienteIndex){ return expedienteRepository.countByChildren(expedienteIndex) > 0; }

   @Override public List<ExpedienteIndex> findByNameLikeIgnoreCase (Tenant tenant, String name)
                          { return expedienteRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
                          { return expedienteRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<ExpedienteIndex> expedientes = expedienteRepository.findExpedientesGranted(role);
      List<ObjectToProtect>     objects = new ArrayList<>();
      expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants

   @Override public List<ExpedienteIndex> findObjectsGranted( Role role)
   {
      return expedienteRepository.findExpedientesGranted(role);
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

   }//revoke

}//ExpedienteService
