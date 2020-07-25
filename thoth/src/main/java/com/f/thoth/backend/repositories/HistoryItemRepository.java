package com.f.thoth.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f.thoth.backend.data.entity.HistoryItem;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
}
