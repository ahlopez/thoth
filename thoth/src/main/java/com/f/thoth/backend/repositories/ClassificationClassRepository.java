package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.classification.ClassificationClass;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ClassificationClassRepository extends JpaRepository<ClassificationClass, Long>
{
   @Query("SELECT o FROM ClassificationClass o where o.tenant=?1")
   Page<ClassificationClass> findBy(Tenant tenant, Pageable page);

   @Query("SELECT o FROM ClassificationClass o where o.tenant=?1")
   List<ClassificationClass> findAll(Tenant tenant);

   @Query("SELECT count(o) FROM ClassificationClass o where o.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT o FROM ClassificationClass o where o.tenant=?1 and o.name like ?2")
   Page<ClassificationClass> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);
   

   //   ----------- Hierarchical handling ----------------
   @EntityGraph(value = ClassificationClass.FULL, type = EntityGraphType.LOAD)
   Optional<ClassificationClass> findById(Long id);

   @EntityGraph(value = ClassificationClass.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT o FROM ClassificationClass o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   List<ClassificationClass> findByParent( ClassificationClass parent);

   @Query("SELECT count(o) FROM ClassificationClass o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByParent( ClassificationClass parent);

   @Query("SELECT count(o) FROM ClassificationClass o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByChildren(ClassificationClass group);

   @EntityGraph(value = ClassificationClass.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT o FROM ClassificationClass o WHERE o.tenant=?1 and lower(o.name) like lower(concat('%', ?2,'%'))")
   List<ClassificationClass> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(o) FROM ClassificationClass o WHERE o.tenant=?1 and lower(o.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @EntityGraph(value = ClassificationClass.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT DISTINCT c FROM ClassificationClass c, Permission p  WHERE p.objectToProtect = c.objectToProtect.getKey() and p.role = ?1")
   List<ClassificationClass> findClasesGranted( Role role);  

}//ClaseRepository
