package com.f.thoth.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.repositories.UserRepository;

@Service
public class UserService implements FilterableCrudService<User>
{

   public  static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "Usuario bloqueado. No puede ser modificado ni borrado";
 //  private static final String DELETING_SELF_NOT_PERMITTED      = "Usted no puede borrar su propio usuario";
   private final UserRepository userRepository;

   @Autowired
   public UserService(UserRepository userRepository)
   {
      this.userRepository = userRepository;
   }//UserService

   public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return getRepository().findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase( repositoryFilter, repositoryFilter, repositoryFilter, pageable);
      } else
      {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return userRepository.countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase( repositoryFilter, repositoryFilter, repositoryFilter);
      } else
      {
         return count();
      }
   }//countAnyMatching

   @Override
   public UserRepository getRepository()
   {
      return userRepository;
   }//getRepository

   public Page<User> find(Pageable pageable)
   {
      return getRepository().findBy(pageable);
   }//find

   @Override
   public User save(com.f.thoth.backend.data.security.User currentUser, User entity)
   {
      throwIfUserLocked(entity);
      return getRepository().saveAndFlush(entity);
   }//save

   @Override
   @Transactional
   public void delete(com.f.thoth.backend.data.security.User currentUser, User userToDelete)
   {
     // throwIfDeletingSelf(currentUser, userToDelete);
      throwIfUserLocked(userToDelete);
      FilterableCrudService.super.delete(currentUser, userToDelete);
   }//delete

   /*
   private void throwIfDeletingSelf(User currentUser, User user)
   {
      if (currentUser.equals(user))
      {
         throw new UserFriendlyDataException(DELETING_SELF_NOT_PERMITTED);
      }
   }//throwIfDeletingSelf
    */
   private void throwIfUserLocked(User entity)
   {
      if (entity != null && entity.isLocked())
      {
         throw new UserFriendlyDataException(MODIFY_LOCKED_USER_NOT_PERMITTED);
      }
   }//throwIfUserLocked

   @Override
   public User createNew(com.f.thoth.backend.data.security.User currentUser)
   {
      return new User();
   }//createNew

}//UserService
