package com.f.thoth.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.security.User;

public interface UserRepository extends JpaRepository<User, Long> {

   User findByEmailIgnoreCase(String email);

   Page<User> findBy(Pageable pageable);

   Page<User> findByEmailLikeIgnoreCaseOrNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(
         String emailLike, String firstNameLike, String lastNameLike, Pageable pageable);

   long countByEmailLikeIgnoreCaseOrNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(
         String emailLike, String nameLike, String lastNameLike);
}//UserRepository
