package com.campustracker.service;

import com.campustracker.model.Trip;
import com.campustracker.model.Shuttle;
import com.campustracker.repository.TripRepository;
import com.campustracker.repository.ShuttleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepo;
    private final ShuttleRepository shuttleRepo;

    public Trip createTrip(Long shuttleId, Trip trip){
        Shuttle s = shuttleRepo.findById(shuttleId).orElseThrow(() -> new RuntimeException("Shuttle not found"));
        trip.setShuttle(s);
        trip.setCurrentStatus("Scheduled");
        return tripRepo.save(trip);
    }

    public List<Trip> getByShuttle(Long shuttleId){ 
        if (shuttleId == null) {
            return tripRepo.findAll(); // Return all trips if no shuttle specified
        }
        return tripRepo.findByShuttle_ShuttleId(shuttleId); 
    }
    
    public List<Trip> getActive(){ return tripRepo.findByCurrentStatus("Ongoing"); }
    
    public List<Trip> getScheduled(){ return tripRepo.findByCurrentStatus("Scheduled"); }
    
    public List<Trip> getTodaysTrips() {
        return tripRepo.findAll().stream()
            .filter(trip -> trip.getTripDate().equals(java.time.LocalDate.now()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Trip> getAvailableTrips() {
        // Get both ongoing and scheduled trips that users can interact with
        List<Trip> available = new java.util.ArrayList<>();
        available.addAll(tripRepo.findByCurrentStatus("Ongoing"));
        available.addAll(tripRepo.findByCurrentStatus("Scheduled"));
        return available;
    }
    
    public List<Trip> getUpcomingTrips() {
        return tripRepo.findAll().stream()
            .filter(trip -> trip.getTripDate().isAfter(java.time.LocalDate.now()) ||
                           (trip.getTripDate().equals(java.time.LocalDate.now()) && "Scheduled".equals(trip.getCurrentStatus())))
            .sorted((t1, t2) -> {
                int dateComparison = t1.getTripDate().compareTo(t2.getTripDate());
                if (dateComparison != 0) return dateComparison;
                return t1.getStartTime().compareTo(t2.getStartTime());
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Trip> getTripsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return tripRepo.findAll().stream()
            .filter(trip -> !trip.getTripDate().isBefore(startDate) && !trip.getTripDate().isAfter(endDate))
            .sorted((t1, t2) -> {
                int dateComparison = t1.getTripDate().compareTo(t2.getTripDate());
                if (dateComparison != 0) return dateComparison;
                return t1.getStartTime().compareTo(t2.getStartTime());
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    public Trip getById(Long id){ return tripRepo.findById(id).orElseThrow(() -> new RuntimeException("Trip not found")); }
    public List<Trip> getAll(){ return tripRepo.findAll(); }
    
    public void deleteTrip(Long id) {
        if (!tripRepo.existsById(id)) {
            throw new RuntimeException("Trip not found with id: " + id);
        }
        tripRepo.deleteById(id);
    }
    
    // Get available shuttles for boarding based on time and status calculations
    public List<Trip> getAvailableShuttlesForBoarding() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        
        return tripRepo.findAll().stream()
            .filter(trip -> {
                // Include trips for today
                if (!trip.getTripDate().equals(today)) {
                    return false;
                }
                
                // Include ongoing trips
                if ("Ongoing".equals(trip.getCurrentStatus())) {
                    return true;
                }
                
                // Include scheduled trips that are about to start (within 15 minutes)
                if ("Scheduled".equals(trip.getCurrentStatus())) {
                    java.time.LocalTime startTime = trip.getStartTime();
                    java.time.LocalTime fifteenMinutesBefore = startTime.minusMinutes(15);
                    return currentTime.isAfter(fifteenMinutesBefore) || currentTime.equals(fifteenMinutesBefore);
                }
                
                return false;
            })
            .sorted((t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()))
            .collect(java.util.stream.Collectors.toList());
    }
}
