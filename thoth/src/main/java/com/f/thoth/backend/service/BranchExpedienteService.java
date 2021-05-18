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
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.BranchExpedienteRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class BranchExpedienteService implements FilterableCrudService<BranchExpediente>, PermissionService<BranchExpediente>
{
   private final BranchExpedienteRepository   branchExpedienteRepository;
   private final PermissionRepository         permissionRepository;
   private final ObjectToProtectRepository    objectToProtectRepository;

   @Autowired
   public BranchExpedienteService(BranchExpedienteRepository   branchExpedienteRepository,
                                  PermissionRepository         permissionRepository,
                                  ObjectToProtectRepository    objectToProtectRepository)
   {
      this.branchExpedienteRepository  = branchExpedienteRepository;
      this.permissionRepository        = permissionRepository;
      this.objectToProtectRepository   = objectToProtectRepository;
   }//BranchExpedienteService constructor


   @Override public Page<BranchExpediente> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return branchExpedienteRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching


   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return branchExpedienteRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = branchExpedienteRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching


   public Page<BranchExpediente> find(Pageable pageable)
       { return branchExpedienteRepository.findBy(ThothSession.getCurrentTenant(), pageable);}
   
   public BranchExpediente  findByCode(String code) { return branchExpedienteRepository.findByCode(code);}

   @Override public JpaRepository<BranchExpediente, Long> getRepository()
       { return branchExpedienteRepository; }

   @Override public BranchExpediente createNew(User currentUser)
   {
      BaseExpediente   baseExpediente   = new BaseExpediente();
      baseExpediente.setTenant(ThothSession.getCurrentTenant());
      baseExpediente.setCreatedBy(null /*TODO: currentUser*/);

      BranchExpediente branchExpediente = new BranchExpediente();
      branchExpediente.setExpediente(baseExpediente);
      return branchExpediente;

   }//createNew

   @Override public BranchExpediente save(User currentUser, BranchExpediente expediente)
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
   @Override public List<BranchExpediente>     findAll()                            {return branchExpedienteRepository.findAll(ThothSession.getCurrentTenant()); }
   @Override public Optional<BranchExpediente> findById(Long id)                    {return branchExpedienteRepository.findById( id);}
   @Override public List<BranchExpediente>     findByParent( BranchExpediente owner){return branchExpedienteRepository.findByParent(owner.getOwner()); }
   @Override public int        countByParent ( BranchExpediente owner)              {return branchExpedienteRepository.countByParent (owner.getOwner()); }
   @Override public boolean    hasChildren   ( BranchExpediente expediente)         {return branchExpedienteRepository.countByChildren(expediente.getPath())> 0;}

   @Override public List<BranchExpediente> findByNameLikeIgnoreCase (Tenant tenant, String name)
                          { return branchExpedienteRepository.findByNameLikeIgnoreCase (tenant, name); }

   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
                          { return branchExpedienteRepository.countByNameLikeIgnoreCase(tenant, name); }

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<BranchExpediente> expedientes = branchExpedienteRepository.findExpedientesGranted(role);
      List<ObjectToProtect>     objects = new ArrayList<>();
      expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants

   @Override public List<BranchExpediente> findObjectsGranted( Role role)
             {  return branchExpedienteRepository.findExpedientesGranted(role); }

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

}//BranchExpedienteService
