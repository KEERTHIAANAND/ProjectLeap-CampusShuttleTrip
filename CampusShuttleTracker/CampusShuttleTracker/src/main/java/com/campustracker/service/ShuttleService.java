package com.campustracker.service;

import com.campustracker.model.Shuttle;
import com.campustracker.repository.ShuttleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShuttleService {
    private final ShuttleRepository repo;

    public Shuttle create(Shuttle s){ return repo.save(s); }
    public List<Shuttle> getAll(){ return repo.findAll(); }
    public Shuttle getById(Long id){ return repo.findById(id).orElseThrow(() -> new RuntimeException("Shuttle not found")); }
}
