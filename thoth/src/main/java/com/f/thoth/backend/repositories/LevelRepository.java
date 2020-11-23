package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.security.Tenant;

public interface LevelRepository extends JpaRepository<Level, Long>
{
   @Query("SELECT l FROM Level l where l.tenant=?1")
   Page<Level> findBy(Tenant tenant, Pageable page);

   Optional<Level> findById(Long id);

   @Query("SELECT l FROM Level l where l.tenant=?1")
   List<Level> findAll(Tenant tenant);

   @Query("SELECT count(l) FROM Level l where l.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT l FROM Level l where l.tenant=?1 and l.name like ?2")
   Page<Level> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT l FROM Level l where l.tenant=?1 and l.name like ?2")
   List<Level> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(l) FROM Level l where l.tenant=?1 and l.name like ?2")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT l FROM Level l where l.orden=?1")
   Level findByLevel(Integer order);


}//LevelRepository
