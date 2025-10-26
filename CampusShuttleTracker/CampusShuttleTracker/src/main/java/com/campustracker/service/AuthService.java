package com.campustracker.service;

import com.campustracker.model.User;
import com.campustracker.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User authenticateUser(String name, String email, String role) {
        // First, try to find existing user by email
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Verify the name and role match
            if (user.getName().equals(name) && user.getRole().equals(role)) {
                return user;
            } else {
                // User exists but credentials don't match
                return null;
            }
        } else {
            // Create new user if doesn't exist (auto-registration)
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setRole(role);
            return userRepository.save(newUser);
        }
    }

    public User registerUser(String name, String email, String role) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return null; // User already exists
        }

        // Create new user
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setRole(role);
        return userRepository.save(newUser);
    }

    public void loginUser(HttpSession session, User user) {
        session.setAttribute("loggedInUser", user);
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userName", user.getName());
    }

    public void logoutUser(HttpSession session) {
        session.removeAttribute("loggedInUser");
        session.removeAttribute("userRole");
        session.removeAttribute("userName");
        session.invalidate();
    }

    public User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

    public String getUserRole(HttpSession session) {
        return (String) session.getAttribute("userRole");
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUser") != null;
    }

    public boolean hasRole(HttpSession session, String role) {
        String userRole = getUserRole(session);
        return userRole != null && userRole.equals(role);
    }

    public boolean isAdmin(HttpSession session) {
        return hasRole(session, "Admin");
    }

    public boolean isStudent(HttpSession session) {
        return hasRole(session, "Student");
    }

    public boolean isStaff(HttpSession session) {
        return hasRole(session, "Staff");
    }
}