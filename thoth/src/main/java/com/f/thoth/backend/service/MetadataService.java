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

import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.MetadataRepository;
import com.vaadin.flow.server.VaadinSession;

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
      return metadataRepository.findAll(tenant());
   }//findAll

   @Override
   public Page<Metadata> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return metadataRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return metadataRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = metadataRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<Metadata> find(Pageable pageable)
   {
      return metadataRepository.findBy(tenant(), pageable);
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
      metadata.setTenant(tenant());
      return metadata;
   }

   @Override
   public Metadata save(User currentUser, Metadata entity)
   {
      try
      {  Metadata newMetadata =  FilterableCrudService.super.save(currentUser, entity);
         return newMetadata;
      } catch (DataIntegrityViolationException e)
      {  throw new UserFriendlyDataException("Ya hay un rol con esa llave. Por favor escoja una llave única para el rol");
      }

   }//save

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//MetadataService
