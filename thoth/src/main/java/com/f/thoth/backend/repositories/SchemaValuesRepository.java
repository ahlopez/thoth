package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.Tenant;

public interface SchemaValuesRepository extends JpaRepository<SchemaValues, Long>
{
	@Query("SELECT v FROM SchemaValues v where v.tenant=?1")
	Page<SchemaValues> findBy(Tenant tenant, Pageable page);

	Optional<SchemaValues> findById(Long id);

	@Query("SELECT v FROM SchemaValues v where v.tenant=?1 and v.valores = ?2")
	SchemaValues findByName(Tenant tenant, String name);

	@Query("SELECT v FROM SchemaValues v where v.tenant=?1")
	List<SchemaValues> findAll(Tenant tenant);

	@Query("SELECT count(v) FROM SchemaValues v where v.tenant=?1")
	long countAll(Tenant tenant);

	@Query("SELECT v FROM SchemaValues v where v.tenant=?1 and v.valores like ?2")
	List<SchemaValues> findByNameLikeIgnoreCase(Tenant tenant, String name);

	@Query("SELECT v FROM SchemaValues v where v.tenant=?1 and v.valores like ?2")
	Page<SchemaValues> findByNameLikeIgnoreCase(Tenant tenant, String name, Pageable pageable);

	@Query("SELECT count(v) FROM SchemaValues v where v.tenant=?1 and v.valores like ?2")
	long countByNameLikeIgnoreCase(Tenant tenant, String name);


}//SchemaValuesRepository
