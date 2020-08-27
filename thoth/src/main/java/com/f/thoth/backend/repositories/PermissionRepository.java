package com.f.thoth.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long>
{
   @Query("SELECT DISTINCT p FROM Permission p WHERE p.objectToProtect.id IN(?1)")
   List<Permission>findByObjects(List<ObjectToProtect>objects);
   
}
