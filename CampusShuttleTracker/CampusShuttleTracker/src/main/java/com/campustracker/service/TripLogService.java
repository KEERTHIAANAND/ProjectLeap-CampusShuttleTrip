package com.campustracker.service;

import com.campustracker.model.Trip;
import com.campustracker.model.TripLog;
import com.campustracker.model.User;
import com.campustracker.repository.TripLogRepository;
import com.campustracker.repository.TripRepository;
import com.campustracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripLogService {

    private final TripLogRepository logRepo;
    private final TripRepository tripRepo;
    private final UserRepository userRepo;

    public TripLog addLog(Long tripId, Long userId, TripLog log){
        // Fetch existing Trip and User
        Trip trip = tripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Assign Trip and User to the log
        log.setTrip(trip);
        log.setUser(user);

        // Update trip status if applicable
        if("StatusUpdate".equalsIgnoreCase(log.getAction()) && log.getRemarks() != null){
            trip.setCurrentStatus(log.getRemarks());
            tripRepo.save(trip);
        }

        return logRepo.save(log);
    }

    public List<TripLog> getLogs(Long tripId){
        if (tripId == null) {
            return logRepo.findAll(); // Return all logs if no trip specified
        }
        return logRepo.findByTrip_TripId(tripId);
    }
    
    public List<TripLog> getRecentLogs(int limit) {
        List<TripLog> allLogs = logRepo.findAll();
        return allLogs.stream()
            .sorted((log1, log2) -> {
                if (log1.getTimestamp() == null && log2.getTimestamp() == null) return 0;
                if (log1.getTimestamp() == null) return 1;
                if (log2.getTimestamp() == null) return -1;
                return log2.getTimestamp().compareTo(log1.getTimestamp());
            })
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }
}
