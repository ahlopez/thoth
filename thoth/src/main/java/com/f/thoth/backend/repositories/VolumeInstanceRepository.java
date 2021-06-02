package com.f.thoth.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.f.thoth.backend.data.gdoc.expediente.Volume;
import com.f.thoth.backend.data.gdoc.expediente.VolumeInstance;
import com.f.thoth.backend.data.security.Role;
import com.f.thoth.backend.data.security.Tenant;

public interface VolumeInstanceRepository extends JpaRepository<VolumeInstance, Long> 
{
   @Query("SELECT vi FROM VolumeInstance vi "+
          "WHERE vi.volume.expediente.tenant = :tenant")
   Page<VolumeInstance> findAll( @Param("tenant") Tenant tenant, Pageable page);

   @Query("SELECT vi FROM VolumeInstance vi "+
          "WHERE vi.volume.expediente.tenant = :tenant")
   List<VolumeInstance> findAll( @Param("tenant") Tenant tenant);

   @Query("SELECT count(vi) FROM VolumeInstance vi "+
          "WHERE vi.volume.expediente.tenant = :tenant")
   long countAll( @Param("tenant") Tenant tenant);

   @Query("SELECT vi FROM VolumeInstance vi "+
          "WHERE (vi.volume IS NULL AND :instance IS NULL) OR vi.volume = :volume AND vi.instance = :instance")
   VolumeInstance findByInstanceCode( @Param("volume") Volume volume, @Param("instance") Integer instanceCode);

  @Query("SELECT vi FROM VolumeInstance vi "+
         "WHERE vi.volume.expediente.tenant = :tenant AND lower(vi.volume.expediente.name) LIKE lower(concat('%', :name,'%'))")
  Page<VolumeInstance> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);

  @Query("SELECT count(vi) FROM VolumeInstance vi "+
         "WHERE vi.volume.expediente.tenant = :tenant AND lower(vi.volume.expediente.name) LIKE lower(concat('%', :name,'%'))")
  Page<VolumeInstance> countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name, Pageable page);


  //   ----------- Hierarchical handling ----------------
  Optional<VolumeInstance> findById(Long id);

  @Query("SELECT vi FROM VolumeInstance vi "+
         "WHERE ((vi.volume.id IS null AND :owner IS null) OR vi.volume.id = :owner)")
  List<VolumeInstance> findByParent( @Param("owner") Long parentId);

  @Query("SELECT count(vi) FROM VolumeInstance vi "+
         "WHERE ((vi.volume.id IS null AND :owner IS null) OR vi.volume.id = :owner)")
  int countByParent( @Param("owner") Long parentId);

  @Query("SELECT count(vi) FROM VolumeInstance vi "+
         "WHERE ((vi.volume.id IS null AND :volume is null) or vi.volume.id = :volume)") //TODO: Implantarlo contra el DocumentRepository
  int countByChildren( @Param("volume") Long volumeId);
  
  @Query("SELECT count(vi) FROM VolumeInstance vi "+
         "WHERE ((vi.volume.id IS null AND :volume is null) or vi.volume.id = :volume)")
  int hasChildren( @Param("volume") Long volumeId); //TODO: Implantar countByChildren, hasChildren usando el DocumentRepository

  @Query("SELECT vi FROM VolumeInstance vi "+
         "WHERE vi.volume.expediente.tenant = :tenant AND LOWER(vi.volume.expediente.name) LIKE LOWER(concat('%', :name,'%'))")
  List<VolumeInstance> findByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name);

  @Query("SELECT count(vi) FROM VolumeInstance vi "+
         "WHERE vi.volume.expediente.tenant = :tenant AND LOWER(vi.volume.expediente.name) LIKE LOWER(concat('%', :name,'%'))")
  long countByNameLikeIgnoreCase( @Param("tenant") Tenant tenant, @Param("name") String name);

  //   ----------- ACL handling ----------------
  @Query("SELECT DISTINCT vi FROM Volume v "+
         "JOIN   VolumeInstance vi "+
         "JOIN   Permission p ON v.expediente.objectToProtect = p.objectToProtect "+
         "WHERE  p.role = :role")
  List<VolumeInstance> findVolumeInstancesGranted( @Param("role") Role role);

}//VolumeInstanceRepository
