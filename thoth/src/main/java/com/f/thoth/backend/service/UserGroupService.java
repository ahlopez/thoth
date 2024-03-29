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

import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.backend.repositories.UserGroupRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class UserGroupService implements FilterableCrudService<UserGroup>, HierarchicalService<UserGroup>
{
   private final UserGroupRepository userGroupRepository;

   @Autowired
   public UserGroupService(UserGroupRepository userGroupRepository)
   {
      this.userGroupRepository = userGroupRepository;
   }

   @Override public Page<UserGroup> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return userGroupRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      }
      else
      {
         return find(pageable);
      }
   }//findAnyMatching

   @Override public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return userGroupRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      }
      else
      {
         long n = userGroupRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<UserGroup> find(Pageable pageable)
   {
      return userGroupRepository.findBy(tenant(), pageable);
   }

   @Override public JpaRepository<UserGroup, Long> getRepository()
   {
      return userGroupRepository;
   }

   @Override public UserGroup createNew(User currentUser)
   {
      UserGroup userGroup = new UserGroup();
      userGroup.setTenant(tenant());
      return userGroup;
   }//createNew

   @Override public UserGroup save(User currentUser, UserGroup entity)
   {
      try
      {
         UserGroup newUserGroup =  FilterableCrudService.super.save(currentUser, entity);
         Tenant tenant = tenant();
         if (tenant != null)
            tenant.addUserGroup(newUserGroup);

         return newUserGroup;
      }
      catch (DataIntegrityViolationException e)
      {
         throw new UserFriendlyDataException("Ya hay un grupo con esa llave. Por favor escoja una llave única para el grupo");
      }

   }//save

   //  ----- implements HierarchicalService ------
   @Override public List<UserGroup> findAll() { return userGroupRepository.findAll(tenant()); }

   @Override public Optional<UserGroup> findById(Long id)              { return userGroupRepository.findById( id);}

   @Override public List<UserGroup>     findByParent  ( UserGroup owner) { return userGroupRepository.findByParent  (owner); }
   @Override public int                 countByParent ( UserGroup owner) { return userGroupRepository.countByParent (owner); }
   @Override public boolean             hasChildren   ( UserGroup group) { return userGroupRepository.countByChildren(group) > 0; }

   @Override public List<UserGroup>     findByNameLikeIgnoreCase (Tenant tenant, String name) { return userGroupRepository.findByNameLikeIgnoreCase (tenant, name); }
   @Override public long                countByNameLikeIgnoreCase(Tenant tenant, String name) { return userGroupRepository.countByNameLikeIgnoreCase(tenant, name); }

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//UserGroupService
