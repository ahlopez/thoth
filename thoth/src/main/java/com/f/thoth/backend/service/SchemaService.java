package com.f.thoth.backend.service;

import static com.f.thoth.Parm.TENANT;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.SchemaRepository;
import com.vaadin.flow.server.VaadinSession;

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
      return schemaRepository.findAll(tenant());
   }//findAll

   public List<Schema> findAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return schemaRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         return findAll();
      }
   }//findAnyMatching

   public Schema findById( Long id)
   {
      Optional<Schema> schema = schemaRepository.findById(id);
      return  schema.isPresent()? schema.get(): null;
   }//findById


   @Override
   public Page<Schema> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return schemaRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching



   public Schema findByName(String name)
   {
         return schemaRepository.findByName(tenant(), name);
   }//findByName


   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return schemaRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
      } else {
         long n = schemaRepository.countAll(tenant());
         return n;
      }
   }//countAnyMatching

   public Page<Schema> find(Pageable pageable)
   {
      return schemaRepository.findBy(tenant(), pageable);
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
      schema.setTenant(tenant());
      return schema;
   }

   @Override
   public Schema save(User currentUser, Schema entity)
   {
      try
      {  Schema newSchema =  FilterableCrudService.super.save(currentUser, entity);
         return newSchema;
      } catch (DataIntegrityViolationException e)
      {  throw new UserFriendlyDataException("Ya hay un esquema con esa llave. Por favor escoja una llave única para el esquema");
      }

   }//save

   @Override
   public void delete(User currentUser, Schema entity)
   {
      schemaRepository.delete(entity);
   }

   private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//SchemaService
