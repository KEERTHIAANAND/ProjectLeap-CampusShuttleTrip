package com.campustracker.controller;

import com.campustracker.model.Shuttle;
import com.campustracker.service.ShuttleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shuttles")
@RequiredArgsConstructor
public class ShuttleController {
    private final ShuttleService service;

    @PostMapping
    public ResponseEntity<Shuttle> create(@RequestBody Shuttle s){ return ResponseEntity.ok(service.create(s)); }

    @GetMapping
    public ResponseEntity<List<Shuttle>> all(){ return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<Shuttle> get(@PathVariable Long id){ return ResponseEntity.ok(service.getById(id)); }
}
