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

import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.RetentionRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class RetentionService implements FilterableCrudService<Retention>
{
   private final RetentionRepository retentionRepository;

   @Autowired
   public RetentionService(RetentionRepository retentionRepository)
   {
      this.retentionRepository = retentionRepository;
   }

   public List<Retention> findAll()
   {
      return retentionRepository.findAll(tenant());
   }//findAll

   @Override
   public Page<Retention> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return retentionRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching


   public List<Retention> findAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return retentionRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         return findAll();
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return retentionRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = retentionRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<Retention> find(Pageable pageable)
   {
      return retentionRepository.findBy(tenant(), pageable);
   }

   @Override
   public JpaRepository<Retention, Long> getRepository()
   {
      return retentionRepository;
   }

   @Override
   public Retention createNew(User currentUser)
   {
      Retention retention = new Retention();
      return retention;
   }

   @Override
   public Retention save(User currentUser, Retention entity)
   {
      try
      {  Retention newRetention =  FilterableCrudService.super.save(currentUser, entity);
         return newRetention;
      } catch (DataIntegrityViolationException e)
      {  throw new UserFriendlyDataException("Ya hay un calendario con esa llave. Por favor escoja una llave única para el calendario");
      }

   }//save

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }


}//RetentionService
