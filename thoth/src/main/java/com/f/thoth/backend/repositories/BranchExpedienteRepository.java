package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;


public interface BranchExpedienteRepository extends JpaRepository<BranchExpediente, Long>
{
   @Query("SELECT branch FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.id = branch.expediente.id")
   Page<BranchExpediente> findBy(Tenant tenant, Pageable page);

   @Query("SELECT branch FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.id = branch.expediente.id")
   List<BranchExpediente> findAll(Tenant tenant);

   @Query("SELECT count(branch) FROM BranchExpediente, branch BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.id = branch.expediente.id")
   long countAll(Tenant tenant);

   @Query("SELECT branch FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name like ?2 AND base.id = branch.expediente.id")
   Page<BranchExpediente> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(branch) FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name like ?2 AND base.id = branch.expediente.id")
   Page<BranchExpediente> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<BranchExpediente> findById(Long id);

   @Query("SELECT branch FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 is null) or base.owner=?1.expediente) AND base.id = branch.expediente.id")
   List<BranchExpediente> findByParent( BranchExpediente parent);

   @Query("SELECT count(branch) FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "((e.owner IS null AND ?1 is null) OR (base.owner=?1.expediente) AND base.id = branch.expediente.id")
   int countByParent( BranchExpediente parent);

   @Query("SELECT count(branch) FROM BranchExpediente branch BaseExpediente base WHERE "+
          "((e.owner IS null AND ?1 is null) OR (base.owner=?1.expediente) AND base.id = branch.expediente.id")
   int countByChildren(BranchExpediente group);

   @Query("SELECT branch FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(base.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND base.id = branch.expediente.id")
   List<BranchExpediente> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(branch) FROM BranchExpediente branch, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(base.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND base.id = branch.expediente.id")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);


   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT branch FROM BranchExpediente branch, BaseExpediente base, Permission p  WHERE "+
          "base.objectToProtect = p.objectToProtect AND p.role = ?1 AND base.id = branch.expediente.id")
   List<BranchExpediente> findExpedientesGranted( Role role);

}//BranchExpedienteRepository
