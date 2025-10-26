package com.campustracker.controller;

import com.campustracker.model.User;
import com.campustracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User u){ return ResponseEntity.ok(service.create(u)); }

    @GetMapping
    public ResponseEntity<List<User>> all(){ return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id){ return ResponseEntity.ok(service.getById(id)); }
}
