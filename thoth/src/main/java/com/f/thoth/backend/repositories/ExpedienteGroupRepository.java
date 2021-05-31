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
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = branch.expediente.id")
   Page<ExpedienteGroup> findBy(@Param("tenant") Tenant tenant, Pageable page);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "WHERE branch.expediente.code = :code")
   ExpedienteGroup findByCode(@Param("code") String code);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = branch.expediente.id")
   List<ExpedienteGroup> findAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = branch.expediente.id")
   long countAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.name like :name AND base.id = branch.expediente.id")
   Page<ExpedienteGroup> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.id = branch.expediente.id AND base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%'))")
   Page<ExpedienteGroup> countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<ExpedienteGroup> findById(Long id);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerId IS null AND :owner IS null) OR base.ownerId = :owner) AND base.id = branch.expediente.id")
   List<ExpedienteGroup> findByParent( @Param("owner") Long ownerId);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerId IS null AND :owner IS null) OR (base.ownerId = :owner)) AND base.id = branch.expediente.id")
   int countByParent( @Param("owner") Long ownerId);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "JOIN BaseExpediente base "+
          "WHERE (base.ownerId IS null AND :group IS null) OR (base.ownerId = :group AND base.id = branch.expediente.id)")
   int countByChildren(@Param("group") Long groupId);

   @Query("SELECT branch FROM ExpedienteGroup branch "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = branch.expediente.id")
   List<ExpedienteGroup> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(branch) FROM ExpedienteGroup branch "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = branch.expediente.id")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);


   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT branch FROM ExpedienteGroup branch "+
          "JOIN BaseExpediente base "+
          "JOIN Permission p "+
          "WHERE base.objectToProtect = p.objectToProtect AND p.role = :role AND base.id = branch.expediente.id")
   List<ExpedienteGroup> findExpedientesGranted( @Param("role") Role role);

}//ExpedienteGroupRepository
