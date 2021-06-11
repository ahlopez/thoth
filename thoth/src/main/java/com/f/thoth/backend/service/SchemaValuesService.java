package com.f.thoth.backend.service;

import static com.f.thoth.Parm.TENANT;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.repositories.SchemaValuesRepository;
import com.vaadin.flow.server.VaadinSession;

@Service
public class SchemaValuesService  implements FilterableCrudService<SchemaValues>
{
  private final SchemaValuesRepository schemaValuesRepository;

  @Autowired
  public SchemaValuesService(SchemaValuesRepository schemaValuesRepository)
  {
    this.schemaValuesRepository = schemaValuesRepository;
  }

  @Override public SchemaValues  createNew( User user)
  {
    SchemaValues values = new SchemaValues();
    return values;
  }//createNew

  @Override
  public Page<SchemaValues> findAnyMatching(Optional<String> filter, Pageable pageable)
  {
    if (filter.isPresent())
    {
      String repositoryFilter = "%" + filter.get() + "%";
      return schemaValuesRepository.findByNameLikeIgnoreCase(tenant(), repositoryFilter, pageable);
    } else {
      return find(pageable);
    }
  }//findAnyMatching

  public Page<SchemaValues> find(Pageable pageable)
  {
    return schemaValuesRepository.findBy(tenant(), pageable);
  }

  @Override
  public JpaRepository<SchemaValues, Long> getRepository()
  {
    return schemaValuesRepository;
  }



  @Override
  public long countAnyMatching(Optional<String> filter)
  {
    if (filter.isPresent()) {
      String repositoryFilter = "%" + filter.get() + "%";
      return schemaValuesRepository.countByNameLikeIgnoreCase(tenant(), repositoryFilter);
    } else {
      long n = schemaValuesRepository.countAll(tenant());
      return n;
    }
  }//countAnyMatching


  private Tenant  tenant() { return (Tenant)VaadinSession.getCurrent().getAttribute(TENANT); }

}//SchemaValuesService
