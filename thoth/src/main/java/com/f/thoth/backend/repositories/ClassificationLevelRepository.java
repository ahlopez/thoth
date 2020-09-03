package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.classification.ClassificationLevel;

public interface ClassificationLevelRepository extends JpaRepository<ClassificationLevel, Long>
{
   
   @Query("SELECT lv FROM ClassificationLevel lv where lv.level=?1")
   ClassificationLevel findByLevel(Integer level);

}
