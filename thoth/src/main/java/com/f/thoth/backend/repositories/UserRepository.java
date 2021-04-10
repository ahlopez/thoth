package com.f.thoth.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.entity.User;

public interface UserRepository extends JpaRepository<User, Long> 
{

   User findByEmailIgnoreCase(String email);

   Page<User> findBy(Pageable pageable);

   Page<User> findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(
         String emailLike, String firstNameLike, String lastNameLike, Pageable pageable);

   long countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(
         String emailLike, String firstNameLike, String lastNameLike);
}//UserRepository
