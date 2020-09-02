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
import com.f.thoth.backend.data.security.Operation;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface OperationRepository extends JpaRepository<Operation, Long>
{
   @Query("SELECT o FROM Operation o where o.tenant=?1")
   Page<Operation> findBy(Tenant tenant, Pageable page);
   
   @Query("SELECT o FROM Operation o where o.objectToProtect=?1")
   Operation findByObjectToProtect(ObjectToProtect objectToProtect);

   @Query("SELECT o FROM Operation o where o.tenant=?1")
   List<Operation> findAll(Tenant tenant);

   @Query("SELECT count(o) FROM Operation o where o.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT o FROM Operation o where o.tenant=?1 and lower(o.name) like  lower(?2)")
   Page<Operation> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);   

   @Query("SELECT o FROM Operation o where o.tenant=?1 and lower(o.name) like lower(?2)")
   List<Operation> findByNameLikeIgnoreCase(Tenant tenant, String name);   

   //   ----------- Hierarchical handling ----------------
   @EntityGraph(value = Operation.FULL, type = EntityGraphType.LOAD)
   Optional<Operation> findById(Long id);

   @EntityGraph(value = Operation.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT o FROM Operation o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   List<Operation> findByParent( Operation parent);

   @Query("SELECT count(o) FROM Operation o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByParent( Operation parent);

   @Query("SELECT count(o) FROM Operation o WHERE (o.owner is null and ?1 is null) or (o.owner=?1)")
   int countByChildren(Operation group);

   @Query("SELECT count(o) FROM Operation o WHERE o.tenant=?1 and lower(o.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @EntityGraph(value = Operation.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT DISTINCT o FROM Operation o, Permission p  WHERE o.objectToProtect = p.objectToProtect and p.role = ?1")
   List<Operation> findOperationsGranted( Role role);
   
   /*
   SELECT e1 from Entity1 as e1 
   where exists
   (select e2 from Entity2 as e2 join e2.e3 as ent3
   where ent3.id=e1.id and e2.name='Test')
   */
   
}//ObjectToProtectRepository
