package com.example.reflex.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "scores", indexes = @Index(name = "idx_score_difference", columnList = "difference_ms"))
public class Score {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 30)
    private String playerName;
    @Column(nullable = false)
    private long targetMs;
    @Column(nullable = false)
    private long elapsedMs;
    @Column(name = "difference_ms", nullable = false)
    private long differenceMs;
    @Column(nullable = false)
    private boolean won;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Score() {}
    public Score(String playerName, long targetMs, long elapsedMs, long differenceMs, boolean won) {
        this.playerName = playerName; this.targetMs = targetMs; this.elapsedMs = elapsedMs;
        this.differenceMs = differenceMs; this.won = won; this.createdAt = Instant.now();
    }
    public Long getId(){return id;} public String getPlayerName(){return playerName;}
    public long getTargetMs(){return targetMs;} public long getElapsedMs(){return elapsedMs;}
    public long getDifferenceMs(){return differenceMs;} public boolean isWon(){return won;}
    public Instant getCreatedAt(){return createdAt;}
}