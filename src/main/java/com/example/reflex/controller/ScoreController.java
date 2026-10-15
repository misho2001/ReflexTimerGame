package com.example.reflex.controller;

import com.example.reflex.dto.ScoreRequest;
import com.example.reflex.model.Score;
import com.example.reflex.service.ScoreService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ScoreController {
    private final ScoreService service;
    public ScoreController(ScoreService service) { this.service = service; }

    @PostMapping("/scores")
    public Score save(@RequestBody ScoreRequest request) { return service.save(request); }

    @GetMapping("/players/{name}/recent")
    public List<Score> recent(@PathVariable String name) { return service.recent(name); }

    @GetMapping("/scores/top10")
    public List<Score> top10() { return service.top10(); }
}