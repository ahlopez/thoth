package com.f.thoth.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.numerator.Sequence;

public interface SequenceRepository extends JpaRepository<Sequence, Long> 
{
	   @Query("SELECT s FROM Sequence s where s.name=?1")
	   Optional<Sequence> findByName(String sequenceName);
	   
	   @Query("SELECT s FROM Sequence s where s.code=?1")
	   Optional<Sequence> findByCode(String sequenceCode);

}//SequenceRepository
