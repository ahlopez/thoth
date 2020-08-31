package com.f.thoth.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.Permission;
import com.f.thoth.backend.data.security.Role;

public interface PermissionRepository<T> extends JpaRepository<Permission, Long>
{
   @Query("SELECT DISTINCT p FROM Permission p WHERE p.objectToProtect IN(?1)")
   List<Permission>findByObjects(List<T>objects);
   
   @Query("SELECT DISTINCT p FROM Permission p WHERE p.objectToProtect = ?2 AND p.role = ?1")
   Permission findByRoleObject(Role role, String objectToProtect);

}//PermissionRepository
