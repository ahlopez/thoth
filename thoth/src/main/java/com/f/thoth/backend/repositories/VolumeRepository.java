package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.gdoc.expediente.Volume;

public interface VolumeRepository extends JpaRepository<Volume, Long> 
{

}//VolumeRepository
