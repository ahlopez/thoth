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
import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.LeafExpedienteRepository;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.PermissionRepository;

@Service
public class LeafExpedienteService implements FilterableCrudService<LeafExpediente>, PermissionService<LeafExpediente>
{
   private final LeafExpedienteRepository     leafExpedienteRepository;
   private final PermissionRepository         permissionRepository;
   private final ObjectToProtectRepository    objectToProtectRepository;

   @Autowired
   public LeafExpedienteService(LeafExpedienteRepository    leafExpedienteRepository,
                                PermissionRepository        permissionRepository,
                                ObjectToProtectRepository   objectToProtectRepository)
   {
      this.leafExpedienteRepository    = leafExpedienteRepository;
      this.permissionRepository        = permissionRepository;
      this.objectToProtectRepository   = objectToProtectRepository;
   }//LeafExpedienteService constructor


   @Override public Page<LeafExpediente> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return leafExpedienteRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
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
         return leafExpedienteRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else
      {
         long n = leafExpedienteRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching


   public Page<LeafExpediente> find(Pageable pageable) { return leafExpedienteRepository.findBy(ThothSession.getCurrentTenant(), pageable);}

   @Override public JpaRepository<LeafExpediente, Long> getRepository()   { return leafExpedienteRepository;}

   @Override public LeafExpediente createNew(User currentUser)
   {
      BaseExpediente   baseExpediente   = new BaseExpediente();
      baseExpediente.setTenant(ThothSession.getCurrentTenant());
      baseExpediente.setCreatedBy(null /*TODO: currentUser*/);

      LeafExpediente leafExpediente = new LeafExpediente();
      leafExpediente.setExpediente(baseExpediente);
      return leafExpediente;

   }//createNew

   @Override public LeafExpediente save(User currentUser, LeafExpediente expediente)
   {
      try
      {
         ObjectToProtect associatedObject = expediente.getObjectToProtect();
         if ( !associatedObject.isPersisted())
            objectToProtectRepository.saveAndFlush( associatedObject);

         return FilterableCrudService.super.save(currentUser, expediente);
      } catch (DataIntegrityViolationException e)
      {
         throw new UserFriendlyDataException("Ya hay un expediente con esa identificación. Por favor escoja un identificador único para el expediente");
      }

   }//save


   //  ----- implements HierarchicalService ------
   @Override public List<LeafExpediente>     findAll()                    {return leafExpedienteRepository.findAll(ThothSession.getCurrentTenant());}
   @Override public Optional<LeafExpediente> findById(Long id)            {return leafExpedienteRepository.findById( id);}
   @Override public List<LeafExpediente>     findByParent( LeafExpediente owner){return owner == null? new ArrayList<>(): leafExpedienteRepository.findByParent(owner.getExpediente());}
   @Override public int        countByParent ( LeafExpediente owner)      {return owner == null? 0: leafExpedienteRepository.countByParent (owner.getExpediente());}
   @Override public boolean    hasChildren   ( LeafExpediente expediente) {return expediente == null? false: leafExpedienteRepository.countByChildren(expediente.getExpediente())> 0;}

   @Override public List<LeafExpediente> findByNameLikeIgnoreCase (Tenant tenant, String name)
             { return leafExpedienteRepository.findByNameLikeIgnoreCase (tenant, name);}

   @Override public long  countByNameLikeIgnoreCase(Tenant tenant, String name)
             { return leafExpedienteRepository.countByNameLikeIgnoreCase(tenant, name);}

   //  --------  Permission handling ---------------------

   @Override public List<Permission> findGrants( Role role)
   {
      List<LeafExpediente> expedientes = leafExpedienteRepository.findExpedientesGranted(role);
      List<ObjectToProtect>     objects = new ArrayList<>();
      expedientes.forEach( expediente-> objects.add(expediente.getObjectToProtect()));
      return  permissionRepository.findByObjects(objects);
   }//findGrants

   @Override public List<LeafExpediente> findObjectsGranted( Role role)
            { return leafExpedienteRepository.findExpedientesGranted(role);}

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

}//LeafExpedienteService
