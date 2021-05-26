package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface BaseExpedienteRepository extends JpaRepository<BaseExpediente, Long>
{
   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant")
   Page<BaseExpediente> findBy(@Param("tenant") Tenant tenant, Pageable page);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND ((base.path IS null AND :path IS NULL) OR base.path = :path)")
   Optional<BaseExpediente> findByPath(@Param("tenant") Tenant tenant,@Param("path") String path);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND (:parentPath IS NOT NULL AND base.path = :parentPath)")
   Optional<BaseExpediente> findParent(@Param("tenant") Tenant tenant,@Param("parentPath") String parentPath);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant")
   List<BaseExpediente> findAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant")
   long countAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.name like :name")
   Page<BaseExpediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%'))")
   Page<BaseExpediente> countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<BaseExpediente> findById(Long id);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.ownerId IS null AND base.classificationClass = :clase")
   List<BaseExpediente>   findByClass(@Param("clase") Classification clase);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.ownerId IS null AND base.classificationClass = :clase")
   int countByClass(@Param("clase") Classification clase);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE ( base.ownerId IS null AND base.classificationClass = :clase AND base.id <> :owner) OR (base.id IS NOT NULL AND base.ownerId = :owner)")
   List<BaseExpediente> findByParent( @Param("owner") Long owner, @Param("clase") Classification clase);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.ownerId = :owner")
   List<BaseExpediente> findByParent( @Param("owner") Long owner);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.ownerId IS null AND base.classificationClass = :clase")
   List<BaseExpediente> findByParent(@Param("clase") Classification clase);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE ( base.ownerId IS null AND base.classificationClass = :clase AND base.id <> :owner) OR (base.ownerId IS NOT NULL AND  base.ownerId = :owner)")
   int countByParent( @Param("owner") Long ownerId, @Param("clase") Classification clase);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.ownerId = :owner")
   int countByParent( @Param("owner") Long ownerId);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.ownerId IS null AND base.classificationClass = :clase")
   int countByParent( @Param("clase") Classification clase);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE ( base.ownerId IS null AND base.classificationClass = :clase) OR (base.ownerId IS NOT NULL AND base.ownerId = :group)")
   int countByChildren(@Param("group") Long groupId, @Param("clase") Classification clase);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.ownerId = :group")
   int countByChildren(@Param("group") Long groupId);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.ownerId IS null AND base.classificationClass = :clase")
   int countByChildren(@Param("clase") Classification clase);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%'))")
   List<BaseExpediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%'))")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT base FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.classificationClass = :clase AND lower(base.name) LIKE lower(concat('%', :name,'%'))")
   List<BaseExpediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, @Param("clase") Classification clase);

   @Query("SELECT count(base) FROM BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.classificationClass = :clase AND lower(base.name) LIKE lower(concat('%', :name,'%'))")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, @Param("clase")  Classification clase);



   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT base FROM BaseExpediente base "+
          "JOIN Permission p "+
          "WHERE base.objectToProtect = p.objectToProtect AND p.role = :role")
   List<BaseExpediente> findExpedientesGranted( @Param("role") Role role);

}//BaseExpedienteRepository
