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
    public Trip getById(Long id){ return tripRepo.findById(id).orElseThrow(() -> new RuntimeException("Trip not found")); }
    public List<Trip> getAll(){ return tripRepo.findAll(); }
}
