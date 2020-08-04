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
import com.f.thoth.backend.data.security.UserGroup;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.UserGroupRepository;

@Service
public class UserGroupService implements FilterableCrudService<UserGroup>
{
   private final UserGroupRepository userGroupRepository;

   @Autowired
   public UserGroupService(UserGroupRepository userGroupRepository)
   {
      this.userGroupRepository = userGroupRepository;
   }

   public List<UserGroup> findAll()
   {
      return userGroupRepository.findAll(ThothSession.getCurrentTenant());
   }//findAll

   @Override
   public Page<UserGroup> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return userGroupRepository.findByFirstNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return userGroupRepository.countByFirstNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = userGroupRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<UserGroup> find(Pageable pageable)
   {
      return userGroupRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<UserGroup, Long> getRepository()
   {
      return userGroupRepository;
   }

   @Override
   public UserGroup createNew(User currentUser)
   {
      UserGroup userGroup = new UserGroup();
      userGroup.setTenant(ThothSession.getCurrentTenant());
      return userGroup;
   }

   @Override
   public UserGroup save(User currentUser, UserGroup entity)
   {
      try {
         UserGroup newUserGroup =  FilterableCrudService.super.save(currentUser, entity);
         ThothSession.getCurrentTenant().addUserGroup(newUserGroup);
         return newUserGroup;
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un grupo con esa llave. Por favor escoja una llave única para el grupo");
      }

   }//save

}//UserGroupService
