package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface VolumeRepository extends JpaRepository<Volume, Long>
{
   @Query("SELECT v FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Volume> findAll( @Param("tenant") Tenant tenant, Pageable page);
   

   @Query("SELECT v FROM Volume v "+
          "JOIN LeafExpediente leaf ON v.expediente.id = leaf.id "+
          "JOIN BaseExpediente base ON leaf.expediente.id = base.id "+
          "WHERE (base.tenant = :tenant AND base.code = :code)")
   Volume findByCode( @Param("tenant") Tenant tenant, @Param("code") String code);


   @Query("SELECT v FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findAll( @Param("tenant") Tenant tenant);

   @Query("SELECT COUNT(v) FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countAll( @Param("tenant") Tenant tenant);

   @Query("SELECT v FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Volume> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(v) FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Volume> countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<Volume> findById(Long id);

   @Query("SELECT v FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerId IS null AND :owner IS null) OR base.ownerId = :owner) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findByParent( @Param("owner") Long parentId);

   @Query("SELECT COUNT(v) FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerId IS null AND :owner IS null) OR base.ownerId = :owner) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByParent( @Param("owner") Long parentId);

   @Query("SELECT COUNT(v) FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerId IS null AND :group is null) or base.ownerId = :group) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByChildren( @Param("group") Long groupId);

   @Query("SELECT v FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(v) FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name);

   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT v FROM Volume v "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "JOIN Permission p  "+
          "WHERE base.objectToProtect = p.objectToProtect AND p.role = :role AND v.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Volume> findExpedientesGranted( @Param("role") Role role);

}//VolumeRepository
