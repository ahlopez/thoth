package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface VolumeRepository extends JpaRepository<Volume, Long>
{
   @Query("SELECT v FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Volume> findBy(Tenant tenant, Pageable page);

   @Query("SELECT v FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findAll(Tenant tenant);

   @Query("SELECT COUNT(v) FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countAll(Tenant tenant);

   @Query("SELECT v FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name LIKE ?2 AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Volume> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT COUNT(v) FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND base.name LIKE ?2 AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Volume> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<Volume> findById(Long id);

   @Query("SELECT v FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 IS null) OR base.owner=?1) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findByParent( Volume parent);

   @Query("SELECT COUNT(v) FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 IS null) OR base.owner=?1) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByParent( Volume parent);

   @Query("SELECT COUNT(v) FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "((base.owner IS null AND ?1 is null) or base.owner=?1) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByChildren(Volume group);

   @Query("SELECT v FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(base.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT COUNT(v) FROM Volume v, LeafExpediente leaf, BaseExpediente base WHERE "+
          "base.tenant=?1 AND LOWER(base.name) LIKE LOWER(CONCAT('%', ?2,'%')) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT v FROM Volume v, LeafExpediente leaf, BaseExpediente base, Permission p  WHERE "+
          "base.objectToProtect = p.objectToProtect AND p.role = ?1 AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findExpedientesGranted( Role role);

}//VolumeRepository
