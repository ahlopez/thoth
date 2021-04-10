package com.f.thoth.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.SingleUserRepository;

@Service
public class SingleUserService implements FilterableCrudService<User>
{
   private final SingleUserRepository SingleUserRepository;

   @Autowired
   public SingleUserService(SingleUserRepository SingleUserRepository)
   {
      this.SingleUserRepository = SingleUserRepository;
   }

   public List<User> findAll()
   {
      return SingleUserRepository.findAll(ThothSession.getCurrentTenant());
   }//findAll

   @Override
   public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return SingleUserRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return SingleUserRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = SingleUserRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<User> find(Pageable pageable)
   {
      return SingleUserRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<User, Long> getRepository()
   {
      return SingleUserRepository;
   }

   @Override
   public User createNew(User currentUser)
   {
      User singleUser = new User();
      return singleUser;
   }//createNew

   @Override
   public User save(User currentUser, User entity)
   {
      try
      {
         User newSingleUser =  FilterableCrudService.super.save(currentUser, entity);
         return newSingleUser;
      } catch (DataIntegrityViolationException e)
      {
         throw new UserFriendlyDataException("Ya hay un grupo con esa llave. Por favor escoja una llave única para el grupo");
      }

   }//save

}//SingleUserService
