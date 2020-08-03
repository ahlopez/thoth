package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface RoleRepository extends JpaRepository<Role, Long>
{
   @Query("SELECT r FROM Role r where r.tenant=?1")
   Page<Role> findBy(Tenant tenant, Pageable page);

   Optional<Role> findById(Long id);

   @Query("SELECT r FROM Role r where r.tenant=?1")
   List<Role> findAll(Tenant tenant);

   @Query("SELECT count(r) FROM Role r where r.tenant=?1")
   long countAll(Tenant tenant);

   @Query("SELECT r FROM Role r where r.tenant=?1 and r.name like ?2")
   Page<Role> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable page);

   @Query("SELECT count(r) FROM Role r where r.tenant=?1 and r.name like ?2")
   long countByNameLikeIgnoreCase(Tenant tenant, String name);

}//RoleRepository
