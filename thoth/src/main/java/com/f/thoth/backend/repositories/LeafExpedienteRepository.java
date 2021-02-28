package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;


public interface LeafExpedienteRepository extends JpaRepository<LeafExpediente, Long>
{
   @Query("SELECT leaf FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.id = leaf.expediente.id")
   Page<LeafExpediente> findBy(Tenant tenant, Pageable page);

   @Query("SELECT leaf FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.id = leaf.expediente.id")
   List<LeafExpediente> findAll(Tenant tenant);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.id = leaf.expediente.id")
   long countAll(Tenant tenant);

   @Query("SELECT leaf FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name like ?2 AND base.id = leaf.expediente.id")
   Page<LeafExpediente> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name like ?2 AND base.id = leaf.expediente.id")
   Page<LeafExpediente> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<LeafExpediente> findById(Long id);

   @Query("SELECT leaf FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 is null) or base.owner=?1.expediente) AND base.id = leaf.expediente.id")
   List<LeafExpediente> findByParent( LeafExpediente parent);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "((e.owner IS null AND ?1 is null) OR (base.owner=?1.expediente) AND base.id = leaf.expediente.id")
   int countByParent( LeafExpediente parent);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "((e.owner IS null AND ?1 is null) OR (base.owner=?1.expediente) AND base.id = leaf.expediente.id")
   int countByChildren(LeafExpediente group);

   @Query("SELECT leaf FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(base.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND base.id = leaf.expediente.id")
   List<LeafExpediente> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(base.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND base.id = leaf.expediente.id")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);


   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT leaf FROM LeafExpediente leaf, BaseExpediente base, Permission p  WHERE "+
          "base.objectToProtect = p.objectToProtect AND p.role = ?1 AND base.id = leaf.expediente.id")
   List<LeafExpediente> findExpedientesGranted( Role role);

}//LeafExpedienteRepository
