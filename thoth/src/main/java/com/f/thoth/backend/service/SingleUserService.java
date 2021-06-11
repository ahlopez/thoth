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
import com.f.thoth.backend.repositories.SingleUserRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class SingleUserService implements FilterableCrudService<User>
{
   private final SingleUserRepository singleUserRepository;

   @Autowired
   public SingleUserService(SingleUserRepository singleUserRepository)
   {
      this.singleUserRepository = singleUserRepository;
   }

   public List<User> findAll()
   {
      return singleUserRepository.findAll(tenant());
   }//findAll

   @Override
   public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return singleUserRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return singleUserRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = singleUserRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<User> find(Pageable pageable)
   {
      return singleUserRepository.findBy(tenant(), pageable);
   }


   public User findByEmail(String email)
   {  return singleUserRepository.findByEmailIgnoreCase(email);
   }

   @Override
   public JpaRepository<User, Long> getRepository()
   {
      return singleUserRepository;
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

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//SingleUserService
