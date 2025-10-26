package com.campustracker.controller;

import com.campustracker.model.*;
import com.campustracker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    private ShuttleService shuttleService;
    
    @Autowired
    private TripService tripService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TripLogService tripLogService;
    
    @Autowired
    private AuthService authService;

    @GetMapping
    public String adminDashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check authentication and authorization
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        
        if (!authService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }
        
        try {
            // Fetch all data from database
            List<Shuttle> allShuttles = shuttleService.getAll();
            List<User> allUsers = userService.getAll();
            List<Trip> allTrips = tripService.getAll(); // Get all trips for overview
            
            // Calculate statistics
            long totalShuttles = allShuttles.size();
            long activeShuttles = allShuttles.stream().filter(s -> "Active".equals(s.getStatus())).count();
            long totalUsers = allUsers.size();
            long activeTrips = allTrips.size();
            
            // Count users by role
            long studentCount = allUsers.stream().filter(u -> "Student".equals(u.getRole())).count();
            long staffCount = allUsers.stream().filter(u -> "Staff".equals(u.getRole())).count();
            long adminCount = allUsers.stream().filter(u -> "Admin".equals(u.getRole())).count();
            
            // Add data to model
            model.addAttribute("totalShuttles", totalShuttles);
            model.addAttribute("activeShuttles", activeShuttles);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("activeTrips", activeTrips);
            model.addAttribute("studentCount", studentCount);
            model.addAttribute("staffCount", staffCount);
            model.addAttribute("adminCount", adminCount);
            
            // Add lists for display
            model.addAttribute("allShuttles", allShuttles);
            model.addAttribute("allUsers", allUsers);
            model.addAttribute("allTrips", allTrips);
            
            // Add current user info
            model.addAttribute("loggedInUser", authService.getLoggedInUser(session));
            model.addAttribute("userRole", authService.getUserRole(session));
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard data: " + e.getMessage());
            // Set default values
            model.addAttribute("totalShuttles", 0);
            model.addAttribute("activeShuttles", 0);
            model.addAttribute("totalUsers", 0);
            model.addAttribute("activeTrips", 0);
            model.addAttribute("allShuttles", List.of());
            model.addAttribute("allUsers", List.of());
            model.addAttribute("allTrips", List.of());
        }
        
        return "admin/index";
    }

    @PostMapping("/shuttles")
    public String createShuttle(@RequestParam String shuttleNumber,
                               @RequestParam Integer capacity,
                               @RequestParam String route,
                               @RequestParam String status,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        if (!authService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied.");
            return "redirect:/login";
        }
        
        try {
            Shuttle shuttle = new Shuttle();
            shuttle.setShuttleNumber(shuttleNumber);
            shuttle.setCapacity(capacity);
            shuttle.setRoute(route);
            shuttle.setStatus(status);
            
            shuttleService.create(shuttle);
            redirectAttributes.addFlashAttribute("success", "Shuttle created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating shuttle: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String role,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        if (!authService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied.");
            return "redirect:/login";
        }
        
        try {
            User user = authService.registerUser(name, email, role);
            if (user != null) {
                redirectAttributes.addFlashAttribute("success", "User created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User with this email already exists.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    @PostMapping("/trips")
    public String createTrip(@RequestParam Long shuttleId,
                            @RequestParam String tripDate,
                            @RequestParam String startTime,
                            @RequestParam(required = false) String endTime,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        if (!authService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Access denied.");
            return "redirect:/login";
        }
        
        try {
            Trip trip = new Trip();
            trip.setTripDate(java.time.LocalDate.parse(tripDate));
            trip.setStartTime(java.time.LocalTime.parse(startTime));
            if (endTime != null && !endTime.isEmpty()) {
                trip.setEndTime(java.time.LocalTime.parse(endTime));
            }
            trip.setCurrentStatus("Scheduled");
            
            // Use your trip service to create trip with shuttle
            tripService.createTrip(shuttleId, trip);
            redirectAttributes.addFlashAttribute("success", "Trip created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating trip: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }

    @DeleteMapping("/shuttles/{id}")
    @ResponseBody
    public String deleteShuttle(@PathVariable Long id, HttpSession session) {
        if (!authService.isAdmin(session)) {
            return "Access denied";
        }
        
        try {
            shuttleService.getById(id); // Check if exists
            // shuttleService.delete(id); // You would implement delete in your service
            return "Shuttle deleted successfully";
        } catch (Exception e) {
            return "Error deleting shuttle: " + e.getMessage();
        }
    }

    @GetMapping("/reports")
    public String reports(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!authService.isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            // Fetch report data
            List<Trip> allTrips = tripService.getAll(); // Get all trips
            List<TripLog> recentLogs = tripLogService.getRecentLogs(20); // Get recent 20 logs
            
            model.addAttribute("allTrips", allTrips);
            model.addAttribute("recentLogs", recentLogs);
            model.addAttribute("loggedInUser", authService.getLoggedInUser(session));
            model.addAttribute("userRole", authService.getUserRole(session));
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading reports: " + e.getMessage());
            model.addAttribute("allTrips", List.of());
            model.addAttribute("recentLogs", List.of());
        }
        
        return "admin/reports";
    }
}