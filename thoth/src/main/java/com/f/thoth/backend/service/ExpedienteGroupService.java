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

import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ExpedienteGroupRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class ExpedienteGroupService implements FilterableCrudService<ExpedienteGroup>, PermissionService<ExpedienteGroup>
{
   private final ExpedienteGroupRepository   expedienteGroupRepository;
   private final PermissionRepository         permissionRepository;
   private final ObjectToProtectRepository    objectToProtectRepository;

   @Autowired
   public ExpedienteGroupService(ExpedienteGroupRepository   expedienteGroupRepository,
                                  PermissionRepository         permissionRepository,
                                  ObjectToProtectRepository    objectToProtectRepository)
   {
      this.expedienteGroupRepository  = expedienteGroupRepository;
      this.permissionRepository        = permissionRepository;
      this.objectToProtectRepository   = objectToProtectRepository;
   }//ExpedienteGroupService constructor


   @Override public Page<ExpedienteGroup> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return expedienteGroupRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching


   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return expedienteGroupRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = expedienteGroupRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching


   public Page<ExpedienteGroup> find(Pageable pageable)
       { return expedienteGroupRepository.findBy(ThothSession.getCurrentTenant(), pageable);}

   public ExpedienteGroup  findByCode(String code) { return expedienteGroupRepository.findByCode(code);}
/*
   public Optional<ExpedienteGroup>  findParent(String parentPath) { return expedienteGroupRepository.findParent(ThothSession.getCurrentTenant(),parentPath);}
*/
   @Override public JpaRepository<ExpedienteGroup, Long> getRepository()
       { return expedienteGroupRepository; }

   @Override public ExpedienteGroup createNew(User currentUser)
   {
      BaseExpediente   baseExpediente   = new BaseExpediente();
      baseExpediente.setTenant(ThothSession.getCurrentTenant());
      baseExpediente.setCreatedBy(null /*TODO: currentUser*/);

      ExpedienteGroup expedienteGroup = new ExpedienteGroup();
      expedienteGroup.setExpediente(baseExpediente);
      return expedienteGroup;

   }//createNew

   @Override public ExpedienteGroup save(User currentUser, ExpedienteGroup expediente)
   {
      try {
          /*
         ObjectToProtect associatedObject = expediente.getObjectToProtect();
         if ( !associatedObject.isPersisted())
            objectToProtectRepository.saveAndFlush( associatedObject);
          */
         return FilterableCrudService.super.save(currentUser, expediente);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un expediente con esa identificación. Por favor escoja un identificador único para el expediente");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<ExpedienteGroup>     findAll()                            {return expedienteGroupRepository.findAll(ThothSession.getCurrentTenant()); }
   @Override public Optional<ExpedienteGroup> findById(Long id)                    {return expedienteGroupRepository.findById( id);}
   @Override public List<ExpedienteGroup>     findByParent( ExpedienteGroup owner){return expedienteGroupRepository.findByParent(owner.getOwnerId()); }
   @Override public int        countByParent ( ExpedienteGroup owner)              {return expedienteGroupRepository.countByParent (owner.getOwnerId()); }
   @Override public boolean    hasChildren   ( ExpedienteGroup expediente)         {return expedienteGroupRepository.countByChildren(expediente.getId())> 0;}

   @Override public List<ExpedienteGroup> findByNameLikeIgnoreCase (Tenant tenant, String name)
                          { return expedienteGroupRepository.findByNameLikeIgnoreCase (tenant, name); }

   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
                          { return expedienteGroupRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<ExpedienteGroup> expedientes = expedienteGroupRepository.findExpedientesGranted(role);
      List<ObjectToProtect>     objects = new ArrayList<>();
      expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants

   @Override public List<ExpedienteGroup> findObjectsGranted( Role role)
             {  return expedienteGroupRepository.findExpedientesGranted(role); }

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

}//ExpedienteGroupService
