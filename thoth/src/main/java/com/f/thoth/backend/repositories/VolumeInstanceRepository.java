package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;

public interface VolumeInstanceRepository extends JpaRepository<VolumeInstance, Long> 
{

}//VolumeInstanceRepository
