package com.f.thoth.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.RoleRepository;

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
      return roleRepository.findAll(ThothSession.getCurrentTenant());  
   }//findAll

   @Override
   public Page<Role> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return roleRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return roleRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = roleRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<Role> find(Pageable pageable)
   {
      return roleRepository.findBy(ThothSession.getCurrentTenant(), pageable);
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
      role.setTenant(ThothSession.getCurrentTenant());
      return role;
   }

   @Override
   public Role save(User currentUser, Role entity)
   {
      try {
         Role newRole =  FilterableCrudService.super.save(currentUser, entity);
         ThothSession.getCurrentTenant().addRole(newRole);
         return newRole;
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un rol con esa llave. Por favor escoja una llave única para el rol");
      }

   }//save

}//RoleService
