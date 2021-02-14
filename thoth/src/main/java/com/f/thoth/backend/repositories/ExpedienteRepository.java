package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ExpedienteRepository extends JpaRepository<BaseExpediente, Long>
{
   @Query("SELECT e FROM Expediente e where e.tenant=?1")
   Page<BaseExpediente> findBy(Tenant tenant, Pageable page);

   @Query("SELECT e FROM Expediente e where e.tenant=?1")
   List<BaseExpediente> findAll(Tenant tenant);

   @Query("SELECT count(e) FROM Expediente e where e.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT e FROM Expediente e where e.tenant= ?1 and e.name like ?2")
   Page<BaseExpediente> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(e) FROM Expediente e where e.tenant= ?1 and e.name like ?2")
   Page<BaseExpediente> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<BaseExpediente> findById(Long id);

   @Query("SELECT e FROM Expediente e WHERE (e.owner is null and ?1 is null) or (e.owner=?1)")
   List<BaseExpediente> findByParent( BaseExpediente parent);

   @Query("SELECT count(e) FROM Expediente e WHERE (e.owner is null and ?1 is null) or (e.owner=?1)")
   int countByParent( BaseExpediente parent);

   @Query("SELECT count(e) FROM Expediente e WHERE (e.owner is null and ?1 is null) or (e.owner=?1)")
   int countByChildren(BaseExpediente group);

   @Query("SELECT e FROM Expediente e WHERE e.tenant=?1 and lower(e.name) like lower(concat('%', ?2,'%'))")
   List<BaseExpediente> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(e) FROM Expediente e WHERE e.tenant=?1 and lower(e.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT e FROM Expediente e, Permission p  WHERE e.objectToProtect = p.objectToProtect and p.role = ?1")
   List<BaseExpediente> findExpedientesGranted( Role role);

}//ExpedienteRepository
