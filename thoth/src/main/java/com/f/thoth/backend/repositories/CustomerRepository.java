package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
