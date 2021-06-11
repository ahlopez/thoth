package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;

public interface SingleUserRepository extends JpaRepository<User, Long>
{
   @EntityGraph(value = User.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM User u WHERE u.tenant= :tenant")
   Page<User> findBy(@Param("tenant") Tenant tenant, Pageable page);

   @EntityGraph(value = User.FULL, type = EntityGraphType.LOAD)
   Optional<User> findById(Long id);

   @EntityGraph(value = User.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM User u WHERE u.email= :email")
   User findByEmailIgnoreCase(@Param("email") String username);

   @EntityGraph(value = User.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM User u WHERE u.tenant= :tenant")
   List<User> findAll(@Param("tenant") Tenant tenant);

   @Query("SELECT count(u) FROM User u WHERE u.tenant= :tenant")
   long countAll(@Param("tenant") Tenant tenant);

   @EntityGraph(value = User.BRIEF, type = EntityGraphType.LOAD)
   @Query("SELECT u FROM User u WHERE u.tenant= :tenant AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name,'%'))")
   Page<User> findByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

   @Query("SELECT COUNT(u) FROM User u WHERE u.tenant= :tenant AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name,'%'))")
   long countByNameLikeIgnoreCase(@Param("tenant") Tenant tenant, @Param("name") String name);

}//SingleUserRepository
