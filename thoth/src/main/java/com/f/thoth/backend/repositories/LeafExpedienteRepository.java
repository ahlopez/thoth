package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.gdoc.expediente.LeafExpediente;

public interface LeafExpedienteRepository extends JpaRepository<LeafExpediente, Long> 
{

}
