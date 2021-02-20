package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;

public interface BaseExpedienteRepository extends JpaRepository<BaseExpediente, Long> 
{

}
