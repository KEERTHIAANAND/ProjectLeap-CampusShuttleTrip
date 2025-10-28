package com.campustracker.controller;

import com.campustracker.model.Trip;
import com.campustracker.model.Shuttle;
import com.campustracker.model.User;
import com.campustracker.model.TripLog;
import com.campustracker.service.TripService;
import com.campustracker.service.ShuttleService;
import com.campustracker.service.AuthService;
import com.campustracker.service.TripLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @Autowired
    private TripLogService tripLogService;

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
            // Get user info
            User loggedInUser = authService.getLoggedInUser(session);
            String userRole = authService.getUserRole(session);
            
            // Fetch comprehensive data for dashboard from database
            List<Trip> allTrips = tripService.getAll();
            List<Trip> activeTrips = tripService.getActive();
            List<Trip> scheduledTrips = tripService.getScheduled();
            List<Trip> todaysTrips = tripService.getTodaysTrips();
            List<Trip> availableTrips = tripService.getAvailableTrips();
            List<Trip> upcomingTrips = tripService.getUpcomingTrips();
            List<Shuttle> allShuttles = shuttleService.getAll();
            // Get user-specific or all logs based on role
            List<TripLog> recentLogs;
            if ("Admin".equals(userRole)) {
                // Admins see all logs
                recentLogs = tripLogService.getRecentLogs(20);
            } else {
                // Students and Faculty see only their recent trips
                recentLogs = tripLogService.getUserRecentTrips(loggedInUser.getUserId(), 10);
            }
            
            // For non-admin users, get available shuttles and create boarding status map
            java.util.Map<Long, Boolean> userBoardingStatus = new java.util.HashMap<>();
            List<Trip> availableShuttles = new java.util.ArrayList<>();
            if (!"Admin".equals(userRole)) {
                availableShuttles = tripService.getAvailableShuttlesForBoarding();
                for (Trip trip : availableShuttles) {
                    boolean isBoarded = tripLogService.isUserBoardedOnTrip(loggedInUser.getUserId(), trip.getTripId());
                    userBoardingStatus.put(trip.getTripId(), isBoarded);
                }
            }
            
            // Calculate statistics
            long activeShuttleCount = allShuttles.stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .count();
            
            // Add data based on user role
            if ("Admin".equals(userRole)) {
                // Admins see all data
                model.addAttribute("trips", allTrips);
                model.addAttribute("showAdminData", true);
                model.addAttribute("canCreateTrips", true);
                model.addAttribute("canManageShuttles", true);
            } else if ("Staff".equals(userRole) || "Faculty".equals(userRole)) {
                // Staff and Faculty can view schedules and trip logs from DB
                model.addAttribute("trips", availableTrips);
                model.addAttribute("showAdminData", false);
                model.addAttribute("canCreateTrips", false);
                model.addAttribute("canManageShuttles", false);
                model.addAttribute("canViewAllSchedules", true);
                model.addAttribute("canViewTripLogs", true);
            } else {
                // Students see available trips they can book
                model.addAttribute("trips", availableTrips);
                model.addAttribute("showAdminData", false);
                model.addAttribute("canCreateTrips", false);
                model.addAttribute("canManageShuttles", false);
                model.addAttribute("canViewAllSchedules", true);
                model.addAttribute("canViewTripLogs", true);
            }
            
            // Common data for all users - from database
            model.addAttribute("allTrips", allTrips);
            model.addAttribute("activeTrips", activeTrips);
            model.addAttribute("scheduledTrips", scheduledTrips);
            model.addAttribute("todaysTrips", todaysTrips);
            model.addAttribute("availableTrips", availableTrips);
            model.addAttribute("upcomingTrips", upcomingTrips.stream().limit(5).collect(java.util.stream.Collectors.toList()));
            model.addAttribute("allShuttles", allShuttles);
            model.addAttribute("recentLogs", recentLogs);
            model.addAttribute("totalShuttles", allShuttles.size());
            model.addAttribute("activeShuttleCount", activeShuttleCount);
            model.addAttribute("activeTripCount", activeTrips.size());
            model.addAttribute("scheduledTripCount", scheduledTrips.size());
            model.addAttribute("todaysTripCount", todaysTrips.size());
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("userRole", userRole);
            model.addAttribute("userBoardingStatus", userBoardingStatus);
            model.addAttribute("availableShuttles", availableShuttles);
            
            // Group trips by shuttle for schedule display
            java.util.Map<Long, List<Trip>> tripsByShuttle = allTrips.stream()
                .filter(trip -> trip.getShuttle() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                    trip -> trip.getShuttle().getShuttleId()
                ));
            model.addAttribute("tripsByShuttle", tripsByShuttle);
            
        } catch (Exception e) {
            model.addAttribute("trips", List.of());
            model.addAttribute("activeTrips", List.of());
            model.addAttribute("scheduledTrips", List.of());
            model.addAttribute("todaysTrips", List.of());
            model.addAttribute("availableTrips", List.of());
            model.addAttribute("upcomingTrips", List.of());
            model.addAttribute("allShuttles", List.of());
            model.addAttribute("recentLogs", List.of());
            model.addAttribute("tripsByShuttle", new java.util.HashMap<>());
            model.addAttribute("totalShuttles", 0);
            model.addAttribute("activeShuttleCount", 0);
            model.addAttribute("activeTripCount", 0);
            model.addAttribute("scheduledTripCount", 0);
            model.addAttribute("todaysTripCount", 0);
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



    @GetMapping("/dashboard/trip/{tripId}")
    public String viewTripDetails(@PathVariable Long tripId, 
                                 Model model, 
                                 HttpSession session, 
                                 RedirectAttributes redirectAttributes) {
        
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        
        try {
            Trip trip = tripService.getById(tripId);
            List<TripLog> tripLogs = tripLogService.getLogs(tripId);
            
            model.addAttribute("trip", trip);
            model.addAttribute("tripLogs", tripLogs);
            model.addAttribute("loggedInUser", authService.getLoggedInUser(session));
            model.addAttribute("userRole", authService.getUserRole(session));
            
            return "trip-details";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Trip not found: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }
}