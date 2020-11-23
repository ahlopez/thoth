package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.metadata.Field;
import com.f.thoth.backend.data.security.Tenant;

public interface FieldRepository extends JpaRepository<Field, Long>
{
   @Query("SELECT f FROM Field f where f.tenant=?1")
   Page<Field> findBy(Tenant tenant, Pageable page);

   Optional<Field> findById(Long id);

   @Query("SELECT f FROM Field f where f.tenant=?1")
   List<Field> findAll(Tenant tenant);

   @Query("SELECT count(f) FROM Field f where f.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT f FROM Field f where f.tenant=?1 and f.name like ?2")
   Page<Field> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(f) FROM Field f where f.tenant=?1 and f.name like ?2")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

}//FieldRepository


