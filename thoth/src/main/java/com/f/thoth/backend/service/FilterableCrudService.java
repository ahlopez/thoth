package com.f.thoth.backend.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.f.thoth.backend.data.entity.AbstractEntity;

public interface FilterableCrudService<T extends AbstractEntity> extends CrudService<T>
{

   Page<T> findAnyMatching(Optional<String> filter, Pageable pageable);

   long countAnyMatching(Optional<String> filter);

}//FilterableCrudService
