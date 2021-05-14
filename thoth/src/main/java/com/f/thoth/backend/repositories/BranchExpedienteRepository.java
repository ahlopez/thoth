package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;


public interface BranchExpedienteRepository extends JpaRepository<BranchExpediente, Long>
{
   @Query("SELECT branch FROM BranchExpediente branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = branch.expediente.id")
   Page<BranchExpediente> findBy(@Param("tenant") Tenant tenant, Pageable page);
   
   @Query("SELECT branch FROM BranchExpediente branch "+
          "WHERE branch.expediente.code = :code")  
   BranchExpediente findByCode(@Param("code") String code);

   @Query("SELECT branch FROM BranchExpediente branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = branch.expediente.id")
   List<BranchExpediente> findAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(branch) FROM BranchExpediente branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = branch.expediente.id")
   long countAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(branch) FROM BranchExpediente branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.name like :name AND base.id = branch.expediente.id")
   Page<BranchExpediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(branch) FROM BranchExpediente branch "+
          "JOIN  BaseExpediente base "+
          "WHERE base.id = branch.expediente.id AND base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%'))")
   Page<BranchExpediente> countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);
   

   //   ----------- Hierarchical handling ----------------
   Optional<BranchExpediente> findById(Long id);

   @Query("SELECT branch FROM BranchExpediente branch "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerPath IS null AND :owner IS null) OR base.ownerPath = :owner) AND base.id = branch.expediente.id")
   List<BranchExpediente> findByParent( @Param("owner") String ownerPath);

   @Query("SELECT count(branch) FROM BranchExpediente branch "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerPath IS null AND :owner IS null) OR (base.ownerPath = :owner)) AND base.id = branch.expediente.id")
   int countByParent( @Param("owner") String ownerPath);

   @Query("SELECT count(branch) FROM BranchExpediente branch "+
          "JOIN BaseExpediente base "+
          "WHERE (base.ownerPath IS null AND :group IS null) OR (base.ownerPath = :group AND base.id = branch.expediente.id)")
   int countByChildren(@Param("group") String group);

   @Query("SELECT branch FROM BranchExpediente branch "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = branch.expediente.id")
   List<BranchExpediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(branch) FROM BranchExpediente branch "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = branch.expediente.id")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);


   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT branch FROM BranchExpediente branch "+
          "JOIN BaseExpediente base "+
          "JOIN Permission p "+
          "WHERE base.objectToProtect = p.objectToProtect AND p.role = :role AND base.id = branch.expediente.id")
   List<BranchExpediente> findExpedientesGranted( @Param("role") Role role);

}//BranchExpedienteRepository
