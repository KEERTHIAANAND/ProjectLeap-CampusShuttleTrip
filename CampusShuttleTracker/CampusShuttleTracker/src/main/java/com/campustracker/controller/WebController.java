package com.campustracker.controller;

import com.campustracker.model.Trip;
import com.campustracker.model.Shuttle;
import com.campustracker.model.User;
import com.campustracker.service.TripService;
import com.campustracker.service.ShuttleService;
import com.campustracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private TripService tripService;
    
    @Autowired
    private ShuttleService shuttleService;
    
    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        try {
            // Fetch real data for home page
            List<Trip> activeTrips = tripService.getActive();
            List<Shuttle> shuttles = shuttleService.getAll();
            
            // Filter active shuttles for better display
            List<Shuttle> activeShuttles = shuttles.stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .collect(java.util.stream.Collectors.toList());
            
            // Add statistics
            model.addAttribute("activeTrips", activeTrips);
            model.addAttribute("shuttles", shuttles);
            model.addAttribute("activeShuttles", activeShuttles);
            model.addAttribute("totalShuttles", shuttles.size());
            model.addAttribute("activeShuttleCount", activeShuttles.size());
            model.addAttribute("activeTripCount", activeTrips.size());
            
            // Add user info to model
            if (authService.isLoggedIn(session)) {
                model.addAttribute("loggedInUser", authService.getLoggedInUser(session));
                model.addAttribute("userRole", authService.getUserRole(session));
                model.addAttribute("isLoggedIn", true);
            } else {
                model.addAttribute("isLoggedIn", false);
            }
            
        } catch (Exception e) {
            model.addAttribute("activeTrips", List.of());
            model.addAttribute("shuttles", List.of());
            model.addAttribute("activeShuttles", List.of());
            model.addAttribute("totalShuttles", 0);
            model.addAttribute("activeShuttleCount", 0);
            model.addAttribute("activeTripCount", 0);
            model.addAttribute("error", "Error loading data: " + e.getMessage());
        }
        
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Check if user is logged in
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        
        try {
            // Fetch comprehensive data for dashboard
            List<Trip> activeTrips = tripService.getActive();
            List<Shuttle> allShuttles = shuttleService.getAll();
            
            // Calculate statistics
            long activeShuttleCount = allShuttles.stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .count();
            
            // Add data to model
            model.addAttribute("activeTrips", activeTrips);
            model.addAttribute("allShuttles", allShuttles);
            model.addAttribute("totalShuttles", allShuttles.size());
            model.addAttribute("activeShuttleCount", activeShuttleCount);
            model.addAttribute("activeTripCount", activeTrips.size());
            
            // Add user info to model
            User loggedInUser = authService.getLoggedInUser(session);
            String userRole = authService.getUserRole(session);
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("userRole", userRole);
            
            // Add role-specific data
            if ("Admin".equals(userRole)) {
                // Admins can see all data
                model.addAttribute("showAdminData", true);
            } else {
                // Regular users see limited data
                model.addAttribute("showAdminData", false);
            }
            
        } catch (Exception e) {
            model.addAttribute("activeTrips", List.of());
            model.addAttribute("allShuttles", List.of());
            model.addAttribute("totalShuttles", 0);
            model.addAttribute("activeShuttleCount", 0);
            model.addAttribute("activeTripCount", 0);
            model.addAttribute("error", "Error loading dashboard data: " + e.getMessage());
        }
        
        return "dashboard";
    }



    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // If already logged in, redirect to dashboard
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String name, 
                       @RequestParam String email, 
                       @RequestParam String role,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        try {
            User user = authService.authenticateUser(name, email, role);
            
            if (user != null) {
                authService.loginUser(session, user);
                redirectAttributes.addFlashAttribute("success", "Login successful! Welcome " + user.getName());
                
                // Redirect based on role
                if ("Admin".equals(role)) {
                    return "redirect:/admin";
                } else {
                    return "redirect:/dashboard";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid credentials. Please check your name, email, and role.");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Login failed. Please try again.");
            return "redirect:/login";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, 
                          @RequestParam String email, 
                          @RequestParam String role,
                          RedirectAttributes redirectAttributes) {
        
        try {
            User user = authService.registerUser(name, email, role);
            
            if (user != null) {
                redirectAttributes.addFlashAttribute("success", "Account created successfully! You can now login.");
            } else {
                redirectAttributes.addFlashAttribute("error", "User with this email already exists.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed. Please try again.");
        }
        
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        if (authService.isLoggedIn(session)) {
            authService.logoutUser(session);
            redirectAttributes.addFlashAttribute("success", "Logged out successfully.");
        }
        return "redirect:/login";
    }
}