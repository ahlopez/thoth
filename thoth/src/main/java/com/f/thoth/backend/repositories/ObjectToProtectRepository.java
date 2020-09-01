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
   @Query("SELECT o FROM ObjectToProtect o where o.tenant=?1")
   Page<ObjectToProtect> findBy(Tenant tenant, Pageable page);

   @Query("SELECT o FROM ObjectToProtect o where o.tenant=?1")
   List<ObjectToProtect> findAll(Tenant tenant);

   @Query("SELECT count(o) FROM ObjectToProtect o where o.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT o FROM ObjectToProtect o where o.tenant=?1 and o.name like ?2")
   Page<ObjectToProtect> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);
   

   //   ----------- Hierarchical handling ----------------
   @EntityGraph(value = ObjectToProtect.FULL, type = EntityGraphType.LOAD)
   Optional<ObjectToProtect> findById(Long id);

   @EntityGraph(value = ObjectToProtect.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT o FROM ObjectToProtect o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   List<ObjectToProtect> findByParent( ObjectToProtect parent);

   @Query("SELECT count(o) FROM ObjectToProtect o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByParent( ObjectToProtect parent);

   @Query("SELECT count(o) FROM ObjectToProtect o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByChildren(ObjectToProtect group);

   @EntityGraph(value = ObjectToProtect.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT o FROM ObjectToProtect o WHERE o.tenant=?1 and lower(o.name) like lower(concat('%', ?2,'%'))")
   List<ObjectToProtect> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(o) FROM ObjectToProtect o WHERE o.tenant=?1 and lower(o.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @EntityGraph(value = ObjectToProtect.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT DISTINCT o FROM ObjectToProtect o, Permission p  WHERE p.objectToProtect = o and p.role = ?1")
   List<ObjectToProtect> findObjectsGranted( Role role);
   
   /*
   SELECT e1 from Entity1 as e1 
   where exists
   (select e2 from Entity2 as e2 join e2.e3 as ent3
   where ent3.id=e1.id and e2.name='Test')
   */
   
}//ObjectToProtectRepository
