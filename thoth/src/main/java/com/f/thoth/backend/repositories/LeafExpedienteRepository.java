package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;


public interface LeafExpedienteRepository extends JpaRepository<LeafExpediente, Long>
{
   @Query("SELECT leaf FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant= :tenant AND base.id = leaf.expediente.id")
   Page<LeafExpediente> findBy(@Param("tenant") Tenant tenant, Pageable page);

   @Query("SELECT leaf FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = leaf.expediente.id")
   List<LeafExpediente> findAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND base.id = leaf.expediente.id")
   long countAll(@Param("tenant") Tenant tenant);

   @Query("SELECT leaf FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = leaf.expediente.id")
   Page<LeafExpediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = leaf.expediente.id")
   Page<LeafExpediente> countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   Optional<LeafExpediente> findById(Long id);

   @Query("SELECT leaf FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.owner IS null AND :owner IS null) or base.owner= :owner) AND base.id = leaf.expediente.id")
   List<LeafExpediente> findByParent( @Param("owner") BaseExpediente owner);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.owner IS null AND :owner is null) OR base.owner= :owner) AND base.id = leaf.expediente.id")
   int countByParent( @Param("owner") BaseExpediente owner);

   @Query("SELECT count(leaf) FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.owner IS null AND :group is null) OR base.owner= :group) AND base.id = leaf.expediente.id")
   int countByChildren( @Param("group") BaseExpediente group);
 
   @Query("SELECT leaf FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = leaf.expediente.id")
   List<LeafExpediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);
 
   @Query("SELECT count(leaf) FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND LOWER(base.name) LIKE lower(concat('%', :name,'%')) AND base.id = leaf.expediente.id")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);


   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT leaf FROM LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "JOIN Permission p  WHERE "+
          "base.objectToProtect = p.objectToProtect AND p.role = :role AND base.id = leaf.expediente.id")
   List<LeafExpediente> findExpedientesGranted( @Param("role") Role role);

}//LeafExpedienteRepository
