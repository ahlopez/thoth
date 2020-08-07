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
import com.f.thoth.backend.data.security.SingleUser;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.SingleUserRepository;

@Service
public class SingleUserService implements FilterableCrudService<SingleUser>
{
   private final SingleUserRepository SingleUserRepository;

   @Autowired
   public SingleUserService(SingleUserRepository SingleUserRepository)
   {
      this.SingleUserRepository = SingleUserRepository;
   }

   public List<SingleUser> findAll()
   {
      return SingleUserRepository.findAll(ThothSession.getCurrentTenant());
   }//findAll

   @Override
   public Page<SingleUser> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return SingleUserRepository.findByFirstNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return SingleUserRepository.countByFirstNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = SingleUserRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<SingleUser> find(Pageable pageable)
   {
      return SingleUserRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<SingleUser, Long> getRepository()
   {
      return SingleUserRepository;
   }

   @Override
   public SingleUser createNew(User currentUser)
   {
      SingleUser SingleUser = new SingleUser();
      SingleUser.setTenant(ThothSession.getCurrentTenant());
      return SingleUser;
   }//createNew

   @Override
   public SingleUser save(User currentUser, SingleUser entity)
   {
      try
      {
         SingleUser newSingleUser =  FilterableCrudService.super.save(currentUser, entity);
         return newSingleUser;
      } catch (DataIntegrityViolationException e)
      {
         throw new UserFriendlyDataException("Ya hay un grupo con esa llave. Por favor escoja una llave única para el grupo");
      }

   }//save

}//SingleUserService
