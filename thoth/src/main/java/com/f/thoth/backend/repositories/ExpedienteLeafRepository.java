package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ExpedienteLeafRepository extends JpaRepository<Expediente, Long>
{
   @Query("SELECT e FROM Expediente e "+
          "WHERE e.expediente.tenant = :tenant")
   Page<Expediente> findAll( @Param("tenant") Tenant tenant, Pageable page);

   @Query("SELECT e FROM Expediente e "+
          "WHERE e.expediente.code = :code" )
   Expediente findByCode(@Param("code") String code);

   @Query("SELECT e FROM Expediente e "+
          "WHERE e.expediente.tenant = :tenant")
   List<Expediente> findAll( @Param("tenant")Tenant tenant);

   @Query("SELECT count(*) FROM Expediente e "+
          "WHERE e.expediente.tenant = :tenant")
   long countAll( @Param("tenant") Tenant tenant);

   @Query("SELECT e FROM Expediente e "+
          "WHERE e.expediente.tenant = :tenant AND lower(e.expediente.name) LIKE lower(concat('%', :name,'%'))")
   Page<Expediente> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(e) FROM Expediente e "+
          "WHERE e.expediente.tenant = :tenant AND lower(e.expediente.name) LIKE lower(concat('%', :name,'%'))")
   Page<Expediente> countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   //   ----------- Hierarchical handling ----------------
   Optional<Expediente> findById(Long id);

   @Query("SELECT e FROM Expediente e "+
          "WHERE ((e.expediente.ownerId IS null AND :owner IS null) OR e.expediente.ownerId = :owner)")
   List<Expediente> findByParent( @Param("owner") Long parentId);

   @Query("SELECT count(*) FROM Expediente e "+
          "WHERE ((e.expediente.ownerId IS null AND :owner IS null) OR e.expediente.ownerId = :owner) ")
   int countByParent( @Param("owner") Long parentId);

   @Query("SELECT count(*) FROM Expediente e "+
          "WHERE ((e.expediente.ownerId IS null AND :group is null) or e.expediente.ownerId = :group)")
   int countByChildren( @Param("group") Long groupId);

   @Query("SELECT e FROM Expediente e "+
          "WHERE e.expediente.tenant = :tenant AND LOWER(e.expediente.name) LIKE lower(concat('%', :name,'%'))")
   List<Expediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(e) FROM Expediente e "+
          "WHERE e.expediente.tenant = :tenant AND LOWER(e.expediente.name) LIKE LOWER(CONCAT('%', :name,'%'))")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   //   ----------- ACL handling ----------------

   @Query("SELECT DISTINCT e FROM Expediente e "+
          "JOIN   Permission p ON e.expediente.objectToProtect = p.objectToProtect "+
          "WHERE  p.role = :role")
   List<Expediente> findExpedientesGranted( @Param("role") Role role);

}//ExpedienteRepository
