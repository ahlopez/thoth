package com.f.thoth.backend.service;

import static com.f.thoth.Parm.TENANT;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.RoleRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class RoleService implements FilterableCrudService<Role>
{
   private final RoleRepository roleRepository;

   @Autowired
   public RoleService(RoleRepository roleRepository)
   {
      this.roleRepository = roleRepository;
   }

   public List<Role> findAll()
   {
      return roleRepository.findAll(tenant());
   }//findAll

   @Override
   public Page<Role> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return roleRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return roleRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = roleRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<Role> find(Pageable pageable)
   {
      return roleRepository.findBy(tenant(), pageable);
   }

   @Override
   public JpaRepository<Role, Long> getRepository()
   {
      return roleRepository;
   }

   @Override
   public Role createNew(User currentUser)
   {
      Role role = new Role();
      role.setTenant(tenant());
      return role;
   }

   @Override
   public Role save(User currentUser, Role entity)
   {
      try
      {  Role newRole =  FilterableCrudService.super.save(currentUser, entity);
         return newRole;
      } catch (DataIntegrityViolationException e)
      {  throw new UserFriendlyDataException("Ya hay un rol con esa llave. Por favor escoja una llave única para el rol");
      }

   }//save

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//RoleService
