package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long>
{
   @Query("SELECT e FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Expediente> findBy(Tenant tenant, Pageable page);

   @Query("SELECT e FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findAll(Tenant tenant);

   @Query("SELECT COUNT(e) FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countAll(Tenant tenant);

   @Query("SELECT e FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name LIKE ?2 AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Expediente> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT COUNT(e) FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name LIKE ?2 AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Expediente> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<Expediente> findById(Long id);

   @Query("SELECT e FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 IS null) OR base.owner=?1) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findByParent( Expediente parent);

   @Query("SELECT COUNT(e) FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 IS null) OR base.owner=?1) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByParent( Expediente parent);

   @Query("SELECT COUNT(e) FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 is null) or base.owner=?1) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByChildren(Expediente group);

   @Query("SELECT e FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(base.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT COUNT(e) FROM Expediente e, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(e.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT e FROM Expediente e, LeafExpediente leaf, BaseExpediente base, Permission p  WHERE "+
          "base.objectToProtect = p.objectToProtect AND p.role = ?1 AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findExpedientesGranted( Role role);

}//ExpedienteRepository
