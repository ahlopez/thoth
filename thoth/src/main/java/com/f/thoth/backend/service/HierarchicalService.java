package com.f.thoth.backend.service;

import java.util.List;
import java.util.Optional;

import com.f.thoth.backend.data.security.Tenant;

public interface HierarchicalService<T>
{
   List<T> findAll();
   
   Optional<T> findById(Long id);

   List<T> findByParent  ( T parent);
   int     countByParent ( T parent);
   boolean hasChildren   ( T node);

   List<T> findByNameLikeIgnoreCase (Tenant tenant, String name);
   long    countByNameLikeIgnoreCase(Tenant tenant, String name);

}//HierarchicalService
