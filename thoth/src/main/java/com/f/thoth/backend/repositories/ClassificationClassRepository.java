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
   @Query("SELECT c FROM ClassificationClass c where c.tenant=?1")
   Page<ClassificationClass> findBy(Tenant tenant, Pageable page);

   @Query("SELECT c FROM ClassificationClass c where c.tenant=?1")
   List<ClassificationClass> findAll(Tenant tenant);

   @Query("SELECT count(c) FROM ClassificationClass c where c.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT c FROM ClassificationClass c where c.tenant= ?1 and c.objectToProtect.name like ?2")
   Page<ClassificationClass> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);
   

   //   ----------- Hierarchical handling ----------------
   @EntityGraph(value = ClassificationClass.FULL, type = EntityGraphType.LOAD)
   Optional<ClassificationClass> findById(Long id);

   @EntityGraph(value = ClassificationClass.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT c FROM ClassificationClass c WHERE (c.owner is null and ?1 is null) or (c.owner=?1)")
   List<ClassificationClass> findByParent( ClassificationClass parent);

   @Query("SELECT count(c) FROM ClassificationClass c WHERE (c.owner is null and ?1 is null) or (c.owner=?1)")
   int countByParent( ClassificationClass parent);

   @Query("SELECT count(c) FROM ClassificationClass c WHERE (c.owner is null and ?1 is null) or (c.owner=?1)")
   int countByChildren(ClassificationClass group);

   @EntityGraph(value = ClassificationClass.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT c FROM ClassificationClass c WHERE c.tenant=?1 and lower(c.objectToProtect.name) like lower(concat('%', ?2,'%'))")
   List<ClassificationClass> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(c) FROM ClassificationClass c WHERE c.tenant=?1 and lower(c.objectToProtect.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @EntityGraph(value = ClassificationClass.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT DISTINCT c FROM ClassificationClass c, Permission p  WHERE c.objectToProtect.getKey() = p.objectToProtect and p.role = ?1")
   List<ClassificationClass> findClasesGranted( Role role);  

}//ClaseRepository
