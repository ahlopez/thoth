package com.f.thoth.backend.repositories;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.f.thoth.backend.data.gdoc.numerator.Sequence;

public interface SequenceRepository extends JpaRepository<Sequence, Long> 
{
	   @Query("SELECT s FROM Sequence s where s.nombre=?1")
	   Optional<Sequence> findByName(String sequenceName);
	   
	   @Query("SELECT s FROM Sequence s where s.code=?1")
	   Optional<Sequence> findByCode(String sequenceCode);
	   
	   @Transactional
	   @Modifying	   
	   @Query("UPDATE Sequence seq SET seq.value=:sequenceValue where seq.id= :sequenceId")
	   void update(@Param("sequenceId") Long id, @Param("sequenceValue") AtomicLong value);

}//SequenceRepository
