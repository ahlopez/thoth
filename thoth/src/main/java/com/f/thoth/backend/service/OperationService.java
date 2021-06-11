package com.f.thoth.backend.service;

import static com.f.thoth.Parm.TENANT;

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

import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Operation;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.OperationRepository;
import com.f.thoth.backend.repositories.PermissionRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class OperationService implements FilterableCrudService<Operation>, PermissionService<Operation>
{

   private final OperationRepository       operationRepository;
   private final PermissionRepository      permissionRepository;
   private final ObjectToProtectRepository objectToProtectRepository;

   @Autowired
   public OperationService(OperationRepository operationRepository,
                           ObjectToProtectRepository objectToProtectRepository,
                           PermissionRepository permissionRepository)
   {
      this.operationRepository       = operationRepository;
      this.permissionRepository      = permissionRepository;
      this.objectToProtectRepository = objectToProtectRepository;
   }

   @Override
   public Page<Operation> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return operationRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return operationRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = operationRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<Operation> find(Pageable pageable)
   {
      return operationRepository.findBy(tenant(), pageable);
   }

   @Override
   public JpaRepository<Operation, Long> getRepository()
   {
      return operationRepository;
   }

   @Override
   public Operation createNew(User currentUser)
   {
      Operation operation = new Operation();
      operation.setTenant(tenant());
      return operation;
   }

   @Override
   public Operation save(User currentUser, Operation operation)
   {
      try {
         return FilterableCrudService.super.save(currentUser, operation);

      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un Objeto con esa llave. Por favor escoja una llave única para el objeto");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<Operation> findAll() { return operationRepository.findAll(tenant()); }

   @Override public Optional<Operation> findById(Long id)            { return operationRepository.findById( id);}

   @Override public List<Operation> findByParent  ( Operation owner) { return operationRepository.findByParent  (owner); }
   @Override public int             countByParent ( Operation owner) { return operationRepository.countByParent (owner); }
   @Override public boolean         hasChildren   ( Operation object){ return operationRepository.countByChildren(object) > 0; }

   @Override public List<Operation> findByNameLikeIgnoreCase (Tenant tenant, String name)
   {
         String repositoryFilter = "%" + name + "%";
         return operationRepository.findByNameLikeIgnoreCase(tenant, repositoryFilter);
   }
   @Override public long            countByNameLikeIgnoreCase(Tenant tenant, String name) { return operationRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<Operation> operations = operationRepository.findOperationsGranted(role);
      List<ObjectToProtect> associatedObjects = new ArrayList<>();
      operations.forEach(operation-> associatedObjects.add(operation.getObjectToProtect()));
      return  permissionRepository.findByObjects(associatedObjects);
   }//findGrants

   @Override public List<Operation> findObjectsGranted( Role role)
   {
      List<Operation> granted = operationRepository.findOperationsGranted(role);
      return granted;
   }

   public void grantRevoke( User currentUser, Role role, Set<Permission> newGrants, Set<Permission> newRevokes)
   {
      grant ( currentUser, role, newGrants);
      revoke( currentUser, role, newRevokes);

   }//grantRevoke

   @Override public void grant( User currentUser, Role role, Set<Permission> newGrants)
   {
      newGrants.forEach( newGrant->
      {
         if (!newGrant.isPersisted())
            permissionRepository.saveAndFlush(newGrant);

         Operation operation= operationRepository.findByObjectToProtect(newGrant.getObjectToProtect());
         if ( operation != null)
         {
            operation.grant(newGrant);
            operationRepository.saveAndFlush(operation);
        }
      });
   }//grant


   @Override public void revoke( User currentUser, Role role, Set<Permission> newRevokes)
   {
      newRevokes.forEach( newRevoke->
      {
         Operation operation= operationRepository.findByObjectToProtect(newRevoke.getObjectToProtect());
         if ( operation != null)
         {
            Permission toRevoke = permissionRepository.findByRoleObject(newRevoke.getRole(),newRevoke.getObjectToProtect());
            operation.revoke(toRevoke);
            objectToProtectRepository.saveAndFlush(operation.getObjectToProtect());
            permissionRepository.delete(toRevoke);
        }
      });

   }//revoke

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }


}//ObjectToProtectService
