package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;

public interface ObjectToProtectRepository extends JpaRepository<ObjectToProtect, Long>
{
   @Query("SELECT o FROM ObjectToProtect o where o.tenant=?1")
   Page<ObjectToProtect> findBy(Tenant tenant, Pageable page);

   @Query("SELECT o FROM ObjectToProtect o where o.tenant=?1")
   List<ObjectToProtect> findAll(Tenant tenant);

   @Query("SELECT count(o) FROM ObjectToProtect o where o.tenant=?1")
   long countAll(Tenant tenant);

   Optional<ObjectToProtect> findById(Long id);

   @Query("SELECT o FROM ObjectToProtect o where o.tenant=?1 and o.name like ?2")
   Page<ObjectToProtect> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(o) FROM ObjectToProtect o where o.tenant=?1 and o.name like ?2")
   int countByNameLikeIgnoreCase(Tenant tenant, String name);

}//ObjectToProtectRepository
