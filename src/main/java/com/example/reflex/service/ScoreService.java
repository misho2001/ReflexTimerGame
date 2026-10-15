package com.example.reflex.service;

import com.example.reflex.dto.ScoreRequest;
import com.example.reflex.model.Score;
import com.example.reflex.repository.ScoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScoreService {
    private final ScoreRepository repository;
    public ScoreService(ScoreRepository repository) { this.repository = repository; }
    public Score save(ScoreRequest r) {
        if (r.playerName() == null || r.playerName().trim().length() < 2 || r.playerName().trim().length() > 30)
            throw new IllegalArgumentException("Player name must be 2-30 characters.");
        if (r.targetMs() < 1 || r.elapsedMs() < 0 || r.differenceMs() != Math.abs(r.targetMs() - r.elapsedMs()))
            throw new IllegalArgumentException("Invalid timing values.");
        return repository.save(new Score(r.playerName().trim(), r.targetMs(), r.elapsedMs(), r.differenceMs(), r.won()));
    }
    public List<Score> recent(String name) {
        return repository.findByPlayerNameIgnoreCaseOrderByCreatedAtDesc(name, PageRequest.of(0, 2));
    }
    public List<Score> top10() { return repository.findTop10ByOrderByDifferenceMsAscCreatedAtDesc(); }
}