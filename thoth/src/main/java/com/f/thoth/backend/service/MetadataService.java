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
import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.MetadataRepository;

@Service
public class MetadataService implements FilterableCrudService<Metadata>
{
   private final MetadataRepository metadataRepository;

   @Autowired
   public MetadataService(MetadataRepository metadataRepository)
   {
      this.metadataRepository = metadataRepository;
   }

   public List<Metadata> findAll()
   {
      return metadataRepository.findAll(ThothSession.getCurrentTenant());  
   }//findAll

   @Override
   public Page<Metadata> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return metadataRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return metadataRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = metadataRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<Metadata> find(Pageable pageable)
   {
      return metadataRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<Metadata, Long> getRepository()
   {
      return metadataRepository;
   }

   @Override
   public Metadata createNew(User currentUser)
   {
      Metadata metadata = new Metadata();
      metadata.setTenant(ThothSession.getCurrentTenant());
      return metadata;
   }

   @Override
   public Metadata save(User currentUser, Metadata entity)
   {
      try {
         Metadata newMetadata =  FilterableCrudService.super.save(currentUser, entity);
         ThothSession.updateSession();
         return newMetadata;
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un rol con esa llave. Por favor escoja una llave única para el rol");
      }

   }//save

}//MetadataService
