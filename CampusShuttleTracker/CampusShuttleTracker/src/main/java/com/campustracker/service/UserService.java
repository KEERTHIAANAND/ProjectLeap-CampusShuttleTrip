package com.campustracker.service;

import com.campustracker.model.User;
import com.campustracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;

    public User create(User u){ return repo.save(u); }
    public List<User> getAll(){ return repo.findAll(); }
    public User getById(Long id){ return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found")); }
    
    public void deleteUser(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
