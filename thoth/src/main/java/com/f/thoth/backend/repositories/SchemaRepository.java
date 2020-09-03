package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.gdoc.metadata.Schema;

public interface SchemaRepository extends JpaRepository<Schema, Long>
{

}
