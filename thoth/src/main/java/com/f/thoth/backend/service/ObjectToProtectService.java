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
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;

@Service
public class ObjectToProtectService implements FilterableCrudService<ObjectToProtect>
{

   private final ObjectToProtectRepository objectToProtectRepository;

   @Autowired
   public ObjectToProtectService(ObjectToProtectRepository objectToProtectRepository)
   {
      this.objectToProtectRepository = objectToProtectRepository;
   }

   public List<ObjectToProtect> findAll()
   {
      return objectToProtectRepository.findAll(ThothSession.getCurrentTenant()); 
   }//findAll

   @Override
   public Page<ObjectToProtect> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return objectToProtectRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return objectToProtectRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = objectToProtectRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<ObjectToProtect> find(Pageable pageable)
   {
      return objectToProtectRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<ObjectToProtect, Long> getRepository()
   {
      return objectToProtectRepository;
   }

   @Override
   public ObjectToProtect createNew(User currentUser)
   {
      ObjectToProtect objectToProtect = new ObjectToProtect();
      objectToProtect.setTenant(ThothSession.getCurrentTenant()); 
      return objectToProtect;
   }

   @Override
   public ObjectToProtect save(User currentUser, ObjectToProtect entity)
   {
      try {
         return FilterableCrudService.super.save(currentUser, entity);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un Objeto con esa llave. Por favor escoja una llave única para el objeto");
      }

   }//save

}//ObjectToProtectService
