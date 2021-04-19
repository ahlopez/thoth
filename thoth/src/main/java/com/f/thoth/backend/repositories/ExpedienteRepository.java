package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.expediente.Expediente;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long>
{
   @Query("SELECT e FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Expediente> findAll( @Param("tenant") Tenant tenant, Pageable page);

   @Query("SELECT e FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findAll( @Param("tenant")Tenant tenant);

   @Query("SELECT count(*) FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countAll( @Param("tenant") Tenant tenant);

   @Query("SELECT e FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Expediente> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT count(e) FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND lower(base.name) LIKE lower(concat('%', :name,'%')) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   Page<Expediente> countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   //   ----------- Hierarchical handling ----------------
   Optional<Expediente> findById(Long id);

   @Query("SELECT e FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerPath IS null AND :owner IS null) OR base.ownerPath = :owner) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findByParent( @Param("owner") String parentPath);

   @Query("SELECT count(*) FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerPath IS null AND :owner IS null) OR base.ownerPath = :owner) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByParent( @Param("owner") String parentPath);

   @Query("SELECT count(*) FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE ((base.ownerPath IS null AND :group is null) or base.ownerPath = :group) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   int countByChildren( @Param("group") String groupPath);

   @Query("SELECT e FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND LOWER(base.name) LIKE lower(concat('%', :name,'%')) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   @Query("SELECT count(e) FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "WHERE base.tenant = :tenant AND LOWER(base.name) LIKE LOWER(CONCAT('%', :name,'%')) AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

   //   ----------- ACL handling ----------------
   @Query("SELECT DISTINCT e FROM Expediente e "+
          "JOIN LeafExpediente leaf "+
          "JOIN BaseExpediente base "+
          "JOIN Permission p "+
          "WHERE base.objectToProtect = p.objectToProtect AND p.role = :role AND e.expediente.id = leaf.id AND leaf.expediente.id = base.id")
   List<Expediente> findExpedientesGranted( @Param("role") Role role);

}//ExpedienteRepository
