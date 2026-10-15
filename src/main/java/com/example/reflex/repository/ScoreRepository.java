package com.example.reflex.repository;

import com.example.reflex.model.Score;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByPlayerNameIgnoreCaseOrderByCreatedAtDesc(String playerName, Pageable pageable);
    List<Score> findTop10ByOrderByDifferenceMsAscCreatedAtDesc();
}