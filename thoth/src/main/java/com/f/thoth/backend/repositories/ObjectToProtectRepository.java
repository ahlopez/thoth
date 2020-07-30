package com.f.thoth.backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.security.ObjectToProtect;

public interface ObjectToProtectRepository extends JpaRepository<ObjectToProtect, Long>
{

   Page<ObjectToProtect> findBy(Pageable page);

   Optional<ObjectToProtect> findById(Long id);

   Page<ObjectToProtect> findByNameLikeIgnoreCase(String name, Pageable page);

   int countByNameLikeIgnoreCase(String name);

}//ObjectToProtectRepository
