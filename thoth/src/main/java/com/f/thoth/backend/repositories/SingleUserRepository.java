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
import com.f.thoth.backend.data.security.SingleUser;

public interface SingleUserRepository extends JpaRepository<SingleUser, Long>
{
   @EntityGraph(value = SingleUser.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT g FROM SingleUser g where g.tenant=?1")
   Page<SingleUser> findBy(Tenant tenant, Pageable page);

   @EntityGraph(value = SingleUser.FULL, type = EntityGraphType.LOAD)
   Optional<SingleUser> findById(Long id);

   @EntityGraph(value = SingleUser.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM SingleUser u where u.tenant=?1")
   List<SingleUser> findAll(Tenant tenant);

   @Query("SELECT count(u) FROM SingleUser u where u.tenant=?1")
   long countAll(Tenant tenant);

   @EntityGraph(value = SingleUser.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM SingleUser u where u.tenant=?1 and u.firstName like ?2")
   Page<SingleUser> findByFirstNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(u) FROM SingleUser u where u.tenant=?1 and u.firstName like ?2")
   long countByFirstNameLikeIgnoreCase(Tenant tenant, String name);

}//SingleUserRepository
