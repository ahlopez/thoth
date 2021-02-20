package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;

public interface BranchExpedienteRepository extends JpaRepository<BranchExpediente, Long> 
{

}
