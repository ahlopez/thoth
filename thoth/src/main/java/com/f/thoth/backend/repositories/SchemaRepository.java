package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.Tenant;

public interface SchemaRepository extends JpaRepository<Schema, Long>
{
   @Query("SELECT s FROM Schema s where s.tenant=?1")
   Page<Schema> findBy(Tenant tenant, Pageable page);

   Optional<Schema> findById(Long id);

   @Query("SELECT s FROM Schema s where s.tenant=?1")
   List<Schema> findAll(Tenant tenant);

   @Query("SELECT count(s) FROM Schema s where s.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT s FROM Schema s where s.tenant=?1 and s.name like ?2")
   List<Schema> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT s FROM Schema s where s.tenant=?1 and s.name like ?2")
   Page<Schema> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable pageable);

   @Query("SELECT count(s) FROM Schema s where s.tenant=?1 and s.name like ?2")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

}//SchemaRepository
