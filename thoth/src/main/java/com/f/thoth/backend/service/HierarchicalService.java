package com.f.thoth.backend.service;

import java.util.List;
import java.util.Optional;


import com.f.thoth.backend.data.security.Tenant;

public interface HierarchicalService<T>
{
   Optional<T> findById(Long id);

   List<T> findByParent ( Long parentId);
   int     countByParent( Long parentId);

   boolean existsByParent(Long parentId);

   List<T> findByNameLikeIgnoreCase (Tenant tenant, String name);
   long    countByNameLikeIgnoreCase(Tenant tenant, String name);

}//HierarchicalService
