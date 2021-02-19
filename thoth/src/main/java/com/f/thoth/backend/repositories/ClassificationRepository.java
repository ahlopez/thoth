package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ClassificationRepository extends JpaRepository<Classification, Long>
{
   @Query("SELECT c FROM Classification c where c.tenant=?1")
   Page<Classification> findBy(Tenant tenant, Pageable page);

   @Query("SELECT c FROM Classification c where c.tenant=?1")
   List<Classification> findAll(Tenant tenant);

   @Query("SELECT count(c) FROM Classification c where c.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT c FROM Classification c where c.tenant= ?1 and c.name like ?2")
   Page<Classification> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(c) FROM Classification c where c.tenant= ?1 and c.name like ?2")
   Page<Classification> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   @EntityGraph(value = Classification.FULL, type = EntityGraphType.LOAD)
   Optional<Classification> findById(Long id);

   @EntityGraph(value = Classification.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT c FROM Classification c WHERE (c.owner is null and ?1 is null) or (c.owner=?1)")
   List<Classification> findByParent( Classification parent);
   
   @EntityGraph(value= Classification.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT c FROM Classification c WHERE (c.tenant=?1 and c.owner = null")
   List<Classification> findRoots( Tenant tenant);
   
   @EntityGraph(value= Classification.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT c FROM Classification c WHERE (c.tenant=?1 AND (SELECT count(l) FROM Classification WHERE l.owner = c) = 0)")
   List<Classification> findLeaves( Tenant tenant);

   @Query("SELECT count(c) FROM Classification c WHERE (c.owner is null and ?1 is null) or (c.owner=?1)")
   int countByParent( Classification parent);

   @Query("SELECT count(c) FROM Classification c WHERE c.owner=?1")
   int countByChildren(Classification group);

   @EntityGraph(value = Classification.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT c FROM Classification c WHERE c.tenant=?1 and lower(c.name) like lower(concat('%', ?2,'%'))")
   List<Classification> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(c) FROM Classification c WHERE c.tenant=?1 and lower(c.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);
   
   //   ----------- ACL handling ----------------
   @EntityGraph(value = Classification.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT DISTINCT c FROM Classification c, Permission p  WHERE c.objectToProtect = p.objectToProtect and p.role = ?1")
   List<Classification> findClasesGranted( Role role);

}//ClassificationRepository
