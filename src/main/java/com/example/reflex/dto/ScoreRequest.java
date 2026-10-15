package com.example.reflex.dto;

public record ScoreRequest(String playerName, long targetMs, long elapsedMs, long differenceMs, boolean won) {}