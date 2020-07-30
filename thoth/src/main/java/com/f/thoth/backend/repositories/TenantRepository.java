package com.f.thoth.backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.security.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long>
{

   @EntityGraph(value = Tenant.BRIEF, type = EntityGraphType.LOAD)
   Page<Tenant> findBy(Pageable page);

   @Override
   @EntityGraph(value = Tenant.FULL, type = EntityGraphType.LOAD)
   Optional<Tenant> findById(Long id);

   @EntityGraph(value = Tenant.BRIEF, type = EntityGraphType.LOAD)
   Page<Tenant> findByNameLikeIgnoreCase(String name, Pageable page);

   int countByNameLikeIgnoreCase(String name);

}//TenantRepository
