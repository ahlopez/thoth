package com.f.thoth.backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.security.Role;

public interface RoleRepository extends JpaRepository<Role, Long>
{

   Page<Role> findBy(Pageable page);

   Optional<Role> findById(Long id);

   Page<Role> findByNameLikeIgnoreCase(String name, Pageable page);

   int countByNameLikeIgnoreCase(String name);

}//RoleRepository
