package com.f.thoth.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.repositories.TenantRepository;

@Service
public class TenantService implements FilterableCrudService<Tenant> 
{

   private final TenantRepository tenantRepository;

   @Autowired
   public TenantService(TenantRepository tenantRepository) {
      this.tenantRepository = tenantRepository;
   }

   @Override
   public Page<Tenant> findAnyMatching(Optional<String> filter, Pageable pageable) {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return tenantRepository.findByNameLikeIgnoreCase(repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }

   @Override
   public long countAnyMatching(Optional<String> filter) {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return tenantRepository.countByNameLikeIgnoreCase(repositoryFilter);
      } else {
         return count();
      }
   }

   public Page<Tenant> find(Pageable pageable) {
      return tenantRepository.findBy(pageable);
   }

   @Override
   public JpaRepository<Tenant, Long> getRepository() {
      return tenantRepository;
   }

   @Override
   public Tenant createNew(User currentUser) {
      return new Tenant();
   }

   @Override
   public Tenant save(User currentUser, Tenant entity) {
      try {
         return FilterableCrudService.super.save(currentUser, entity);
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un cliene con ese nombre. Por favor escoja un nombre �nico para el cliente");
      }

   }

}
