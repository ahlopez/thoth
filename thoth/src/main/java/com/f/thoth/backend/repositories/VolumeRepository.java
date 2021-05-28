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
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant")
   Page<Volume> findAll( @Param("tenant") Tenant tenant, Pageable page);


   @Query("SELECT v FROM Volume v "+
          "WHERE v.expediente.code = :code")
   Volume findByCode( @Param("code") String code);


   @Query("SELECT v FROM Volume v "+
          "WHERE v.expediente.tenant = :tenant")
   List<Volume> findAll( @Param("tenant") Tenant tenant);

   @Query("SELECT COUNT(v) FROM Volume v "+
          "WHERE v.expediente.tenant = :tenant")
   long countAll( @Param("tenant") Tenant tenant);

   @Query("SELECT v FROM Volume v "+
          "WHERE v.expediente.tenant = :tenant AND lower(v.expediente.name) LIKE lower(concat('%', :name,'%'))")
   Page<Volume> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(v) FROM Volume v "+
          "WHERE v.expediente.tenant = :tenant AND lower(v.expediente.name) LIKE lower(concat('%', :name,'%'))")
   Page<Volume> countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<Volume> findById(Long id);

   @Query("SELECT v FROM Volume v "+
          "WHERE ((v.expediente.ownerId IS null AND :owner IS null) OR v.expediente.ownerId = :owner)")
   List<Volume> findByParent( @Param("owner") Long parentId);

   @Query("SELECT COUNT(v) FROM Volume v "+
          "WHERE ((v.expediente.ownerId IS null AND :owner IS null) OR v.expediente.ownerId = :owner)")
   int countByParent( @Param("owner") Long parentId);

   @Query("SELECT COUNT(v) FROM Volume v "+
          "WHERE ((v.expediente.ownerId IS null AND :group is null) or v.expediente.ownerId = :group)")
   int countByChildren( @Param("group") Long groupId);

   @Query("SELECT v FROM Volume v "+
          "WHERE v.expediente.tenant = :tenant AND lower(v.expediente.name) LIKE lower(concat('%', :name,'%'))")
   List<Volume> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(v) FROM Volume v "+
          "WHERE v.expediente.tenant = :tenant AND lower(v.expediente.name) LIKE lower(concat('%', :name,'%'))")
   long countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name);

   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT v FROM Volume v "+
          "JOIN BaseExpediente base "+
          "JOIN Permission p  "+
          "WHERE base.objectToProtect = p.objectToProtect AND p.role = :role")
   List<Volume> findExpedientesGranted( @Param("role") Role role);

}//VolumeRepository
