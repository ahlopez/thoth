package com.f.thoth.backend.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.LevelRepository;

@Service
public class LevelService implements FilterableCrudService<Level>
{
   private final LevelRepository levelRepository;

   @Autowired
   public LevelService(LevelRepository levelRepository)
   {
      this.levelRepository = levelRepository;
   }

   public List<Level> findAll()
   {
      return levelRepository.findAll(ThothSession.getCurrentTenant());
   }//findAll

   @Override
   public Page<Level> findAnyMatching(Optional<String> filter, Pageable pageable)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return levelRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter, pageable);
      } else {
         return find(pageable);
      }
   }//findAnyMatching


   public List<Level> findAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent())
      {
         String repositoryFilter = "%" + filter.get() + "%";
         return levelRepository.findByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         return findAll();
      }
   }//findAnyMatching

   @Override
   public long countAnyMatching(Optional<String> filter)
   {
      if (filter.isPresent()) {
         String repositoryFilter = "%" + filter.get() + "%";
         return levelRepository.countByNameLikeIgnoreCase(ThothSession.getCurrentTenant(), repositoryFilter);
      } else {
         long n = levelRepository.countAll(ThothSession.getCurrentTenant());
         return n;
      }
   }//countAnyMatching

   public Page<Level> find(Pageable pageable)
   {
      return levelRepository.findBy(ThothSession.getCurrentTenant(), pageable);
   }

   @Override
   public JpaRepository<Level, Long> getRepository()
   {
      return levelRepository;
   }

   @Override
   public Level createNew(User currentUser)
   {
      Level level = new Level();
      return level;
   }

   @Override
   public Level save(User currentUser, Level entity)
   {
      try {
         Level newLevel =  FilterableCrudService.super.save(currentUser, entity);
         ThothSession.updateSession();
         return newLevel;
      } catch (DataIntegrityViolationException e) {
         throw new UserFriendlyDataException("Ya hay un nivel con esa llave. Por favor escoja una llave única para el nivel");
      }

   }//save


}//LevelService
