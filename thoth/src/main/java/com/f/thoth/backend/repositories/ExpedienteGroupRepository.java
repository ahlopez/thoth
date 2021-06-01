package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;


public interface ExpedienteGroupRepository extends JpaRepository<ExpedienteGroup, Long>
{
   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.tenant = :tenant")
   Page<ExpedienteGroup> findBy(@Param("tenant") Tenant tenant, Pageable page);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.code = :code")
   ExpedienteGroup findByCode(@Param("code") String code);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.tenant = :tenant")
   List<ExpedienteGroup> findAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.tenant = :tenant")
   long countAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.tenant = :tenant AND lower(branch.expediente.name) LIKE lower(concat('%', :name,'%'))")
   Page<ExpedienteGroup> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.tenant = :tenant AND lower(branch.expediente.name) LIKE lower(concat('%', :name,'%'))")
   Page<ExpedienteGroup> countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<ExpedienteGroup> findById(Long id);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "WHERE (branch.expediente.ownerId IS null AND :owner IS null) OR branch.expediente.ownerId = :owner")
   List<ExpedienteGroup> findByParent( @Param("owner") Long ownerId);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "WHERE (branch.expediente.ownerId IS NULL AND :owner IS NULL) OR branch.expediente.ownerId = :owner")
   int countByParent( @Param("owner") Long ownerId);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "WHERE (branch.expediente.ownerId IS NULL AND :group IS NULL) OR branch.expediente.ownerId = :group")
   int countByChildren(@Param("group") Long groupId);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.tenant = :tenant AND lower(branch.expediente.name) LIKE lower(concat('%', :name,'%'))")
   List<ExpedienteGroup> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.tenant = :tenant AND lower(branch.expediente.name) LIKE lower(concat('%', :name,'%'))")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);


   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT branch FROM ExpedienteGroup branch "+
          "JOIN   Permission p ON branch.expediente.objectToProtect = p.objectToProtect "+
          "WHERE  p.role = :role")
   List<ExpedienteGroup> findExpedientesGranted( @Param("role") Role role);

}//ExpedienteGroupRepository
