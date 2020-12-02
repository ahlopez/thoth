package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.expediente.ExpedienteIndex;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ExpedienteRepository extends JpaRepository<ExpedienteIndex, Long>
{
   @Query("SELECT e FROM ExpedienteIndex e where e.tenant=?1")
   Page<ExpedienteIndex> findBy(Tenant tenant, Pageable page);

   @Query("SELECT e FROM ExpedienteIndex e where e.tenant=?1")
   List<ExpedienteIndex> findAll(Tenant tenant);

   @Query("SELECT count(e) FROM ExpedienteIndex e where e.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT e FROM ExpedienteIndex e where e.tenant= ?1 and e.name like ?2")
   Page<ExpedienteIndex> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(e) FROM ExpedienteIndex e where e.tenant= ?1 and e.name like ?2")
   Page<ExpedienteIndex> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<ExpedienteIndex> findById(Long id);

   @Query("SELECT e FROM ExpedienteIndex e WHERE (e.owner is null and ?1 is null) or (e.owner=?1)")
   List<ExpedienteIndex> findByParent( ExpedienteIndex parent);

   @Query("SELECT count(e) FROM ExpedienteIndex e WHERE (e.owner is null and ?1 is null) or (e.owner=?1)")
   int countByParent( ExpedienteIndex parent);

   @Query("SELECT count(e) FROM ExpedienteIndex e WHERE (e.owner is null and ?1 is null) or (e.owner=?1)")
   int countByChildren(ExpedienteIndex group);

   @Query("SELECT e FROM ExpedienteIndex e WHERE e.tenant=?1 and lower(e.name) like lower(concat('%', ?2,'%'))")
   List<ExpedienteIndex> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(e) FROM ExpedienteIndex e WHERE e.tenant=?1 and lower(e.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT e FROM ExpedienteIndex e, Permission p  WHERE e.objectToProtect = p.objectToProtect and p.role = ?1")
   List<ExpedienteIndex> findExpedientesGranted( Role role);

}//ExpedienteRepository
