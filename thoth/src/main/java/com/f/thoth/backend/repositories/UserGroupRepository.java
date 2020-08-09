package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long>, HierarchicalRepository<UserGroup>
{
   @EntityGraph(value = UserGroup.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT g FROM UserGroup g where g.tenant=?1")
   Page<UserGroup> findBy(Tenant tenant, Pageable page);

   @EntityGraph(value = UserGroup.FULL, type = EntityGraphType.LOAD)
   Optional<UserGroup> findById(Long id);

   @EntityGraph(value = UserGroup.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT g FROM UserGroup g where g.tenant=?1")
   List<UserGroup> findAll(Tenant tenant);

   @Query("SELECT count(g) FROM UserGroup g where g.tenant=?1")
   long countAll(Tenant tenant);

   @EntityGraph(value = UserGroup.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT g FROM UserGroup g where g.tenant=?1 and g.name like ?2")
   Page<UserGroup> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

}//UserGroupRepository
