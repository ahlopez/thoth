package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.classification.Retention;
import com.f.thoth.backend.data.security.Tenant;

public interface RetentionRepository extends JpaRepository<Retention, Long>
{
   @Query("SELECT r FROM Retention r where r.tenant=?1")
   Page<Retention> findBy(Tenant tenant, Pageable page);

   Optional<Retention> findById(Long id);

   @Query("SELECT r FROM Retention r where r.tenant=?1")
   List<Retention> findAll(Tenant tenant);

   @Query("SELECT count(r) FROM Retention r where r.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT r FROM Retention r where r.tenant=?1 and r.name like ?2")
   Page<Retention> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT r FROM Retention r where r.tenant=?1 and r.name like ?2")
   List<Retention> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(r) FROM Retention r where r.tenant=?1 and r.name like ?2")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

}//RetentionRepository
