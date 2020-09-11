package com.f.thoth.backend.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.SchemaRepository;

@Service
public class SchemaService implements FilterableCrudService<Schema>
{
   private final SchemaRepository schemaRepository;

   @Autowired
   public SchemaService(SchemaRepository schemaRepository)
   {
      this.schemaRepository = schemaRepository;
   }

   public List<Schema> findAll()
   {
      return schemaRepository.findAll(ThothSession.getCurrentTenant());  
   }//findAll

   public List<Schema> findAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return schemaRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         return findAll();
      }
   }//findAnyMatching
   
   @Override
   public Page<Schema> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return schemaRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching


   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return schemaRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = schemaRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<Schema> find(Pageable pageable)
   {
      return schemaRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<Schema, Long> getRepository()
   {
      return schemaRepository;
   }

   @Override
   public Schema createNew(User currentUser)
   {
      Schema schema = new Schema();
      schema.setTenant(ThothSession.getCurrentTenant());
      return schema;
   }

   @Override
   public Schema save(User currentUser, Schema entity)
   {
      try {
         Schema newSchema =  FilterableCrudService.super.save(currentUser, entity);
         ThothSession.updateSession();
         return newSchema;
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un esquema con esa llave. Por favor escoja una llave única para el esquema");
      }

   }//save

   @Override
   public void delete(User currentUser, Schema entity)
   {
      schemaRepository.delete(entity);
   }
   
   

}//SchemaService
