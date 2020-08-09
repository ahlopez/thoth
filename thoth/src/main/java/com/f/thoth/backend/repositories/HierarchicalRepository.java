package com.f.thoth.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.UserGroup;

public interface HierarchicalRepository<T> extends JpaRepository<T, Long>
{
     @EntityGraph(value = UserGroup.BRIEF, type = EntityGraphType.LOAD)
     @Query("SELECT g FROM UserGroup g where g.parent.id=?1")
     List<T> findByParent( Long parentId);

     @Query("SELECT count(g) FROM UserGroup g where g.parent.id=?1")
     int countByParent( Long parentId);

     boolean existsByParent(Long parentId);

     @EntityGraph(value = UserGroup.BRIEF, type = EntityGraphType.LOAD)
     @Query("SELECT g FROM UserGroup g where g.tenant=?1 and g.name like ?2")
     List<T> findByNameLikeIgnoreCase(Tenant tenant, String name);

     @Query("SELECT count(g) FROM UserGroup g where g.tenant=?1 and g.name like ?2")
     long countByNameLikeIgnoreCase(Tenant tenant, String name);

}//HierarchicalRepository
