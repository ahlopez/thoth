package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.metadata.DocumentType;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long>
{
   @Query("SELECT d FROM DocumentType d where d.tenant=?1")
   Page<DocumentType> findBy(Tenant tenant, Pageable page);

   @Query("SELECT d FROM DocumentType d where d.tenant=?1")
   List<DocumentType> findAll(Tenant tenant);

   @Query("SELECT count(d) FROM DocumentType d where d.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT d FROM DocumentType d where d.tenant= ?1 and d.name like ?2")
   Page<DocumentType> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(d) FROM DocumentType d where d.tenant= ?1 and d.name like ?2")
   Page<DocumentType> countByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);


   //   ----------- Hierarchical handling ----------------
   @EntityGraph(value = DocumentType.FULL, type = EntityGraphType.LOAD)
   Optional<DocumentType> findById(Long id);

   @EntityGraph(value = DocumentType.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT d FROM DocumentType d WHERE (d.owner is null and ?1 is null) or (d.owner=?1)")
   List<DocumentType> findByParent( DocumentType parent);

   @Query("SELECT count(d) FROM DocumentType d WHERE (d.owner is null and ?1 is null) or (d.owner=?1)")
   int countByParent( DocumentType parent);

   @Query("SELECT count(d) FROM DocumentType d WHERE (d.owner is null and ?1 is null) or (d.owner=?1)")
   int countByChildren(DocumentType group);

   @EntityGraph(value = DocumentType.BRIEF, type = EntityGraphType.FETCH)
   @Query("SELECT d FROM DocumentType d WHERE d.tenant=?1 and lower(d.name) like lower(concat('%', ?2,'%'))")
   List<DocumentType> findByNameLikeIgnoreCase(Tenant tenant, String name);

   @Query("SELECT count(d) FROM DocumentType d WHERE d.tenant=?1 and lower(d.name) like lower(concat('%', ?2,'%'))")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

   //   ----------- ACL handling ----------------
   @EntityGraph(value = DocumentType.FULL, type = EntityGraphType.LOAD)
   @Query("SELECT DISTINCT d FROM DocumentType d, Permission p  WHERE d.objectToProtect = p.objectToProtect and p.role = ?1")
   List<DocumentType> findDocumentTypesGranted( Role role);

}
