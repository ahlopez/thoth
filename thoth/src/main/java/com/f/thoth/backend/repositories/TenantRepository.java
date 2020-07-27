package com.f.thoth.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.security.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long>
{

   Page<Tenant> findBy(Pageable page);

   Page<Tenant> findByNameLikeIgnoreCase(String name, Pageable page);

   int countByNameLikeIgnoreCase(String name);

}//TenantRepository
