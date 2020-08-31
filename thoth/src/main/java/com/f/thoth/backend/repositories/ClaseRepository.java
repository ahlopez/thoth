package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.classification.Clase;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ClaseRepository extends JpaRepository<Clase, Long>
{
   @Query("SELECT o FROM Clase o where o.tenant=?1")
   Page<Clase> findBy(Tenant tenant, Pageable page);

   @Query("SELECT o FROM Clase o where o.tenant=?1")
   List<Clase> findAll(Tenant tenant);

   @Query("SELECT count(o) FROM Clase o where o.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT o FROM Clase o where o.tenant=?1 and o.name like ?2")
   Page<Clase> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);
   

   //   ----------- Hierarchical handling ----------------
   @EntityGraph(value = Clase.FULL, type = EntityGraphType.LOAD)
   Optional<Clase> findById(Long id);

   @EntityGraph(value = Clase.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT o FROM Clase o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   List<Clase> findByParent( Clase parent);

   @Query("SELECT count(o) FROM Clase o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByParent( Clase parent);

   @Query("SELECT count(o) FROM Clase o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByChildren(Clase group);

   @EntityGraph(value = Clase.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT o FROM Clase o WHERE o.tenant=?1 and lower(o.name) like lower(concat('%', ?2,'%'))")
   List<Clase> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(o) FROM Clase o WHERE o.tenant=?1 and lower(o.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @EntityGraph(value = Clase.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT DISTINCT o FROM Clase o, Permission p  WHERE p.objectToProtect.id = o.id and p.role = ?1")
   List<Clase> findClasesGranted( Role role);  

}//ClaseRepository
