package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ObjectToProtectRepository extends JpaRepository<ObjectToProtect, Long>
{
   @EntityGraph(value = ObjectToProtect.FULL, type = EntityGraphType.LOAD)
   Optional<ObjectToProtect> findById(Long id);
   
   @EntityGraph(value = ObjectToProtect.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT o FROM ObjectToProtect o where o = ?1")
   Optional<ObjectToProtect> findByObject(ObjectToProtect object);

   @EntityGraph(value = ObjectToProtect.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT o FROM ObjectToProtect o")
   Page<ObjectToProtect> findBy(Tenant tenant, Pageable page);

   @EntityGraph(value = ObjectToProtect.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT o FROM ObjectToProtect o")
   List<ObjectToProtect> findAll(Tenant tenant);

   @Query("SELECT count(o) FROM ObjectToProtect o")
   long countAll();
 
   @Query("SELECT o FROM ObjectToProtect o JOIN o.acl p WHERE o = p.objectToProtect AND p.role= ?1")
   List<ObjectToProtect>findObjectsGranted( Role role);

}//ObjectToProtectRepository
