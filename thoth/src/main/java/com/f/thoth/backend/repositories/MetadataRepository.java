package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.metadata.Metadata;
import com.f.thoth.backend.data.security.Tenant;

public interface MetadataRepository extends JpaRepository<Metadata, Long>
{
   @Query("SELECT m FROM Metadata m where m.tenant=?1")
   Page<Metadata> findBy(Tenant tenant, Pageable page);

   Optional<Metadata> findById(Long id);

   @Query("SELECT m FROM Metadata m where m.tenant=?1")
   List<Metadata> findAll(Tenant tenant);

   @Query("SELECT count(m) FROM Metadata m where m.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT m FROM Metadata m where m.tenant=?1 and m.name like ?2")
   Page<Metadata> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(m) FROM Metadata m where m.tenant=?1 and m.name like ?2")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

}//MetadataRepository

