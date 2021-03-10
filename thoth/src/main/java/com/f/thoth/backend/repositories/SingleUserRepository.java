package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.security.Tenant;

public interface SingleUserRepository extends JpaRepository<User, Long>
{
   @EntityGraph(value = User.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM User u where u.tenant=?1")
   Page<User> findBy(Tenant tenant, Pageable page);

   @EntityGraph(value = User.FULL, type = EntityGraphType.LOAD)
   Optional<User> findById(Long id);

   @EntityGraph(value = User.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM User u where u.tenant=?1")
   List<User> findAll(Tenant tenant);

   @Query("SELECT count(u) FROM User u where u.tenant=?1")
   long countAll(Tenant tenant);

   @EntityGraph(value = User.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM User u where u.tenant=?1 and u.name like ?2")
   Page<User> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(u) FROM User u where u.tenant=?1 and u.name like ?2")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

}//SingleUserRepository
