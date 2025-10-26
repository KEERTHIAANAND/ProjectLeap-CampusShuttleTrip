package com.campustracker.controller;

import com.campustracker.model.Trip;
import com.campustracker.model.TripLog;
import com.campustracker.dto.TripLogRequest;
import com.campustracker.service.TripService;
import com.campustracker.service.TripLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final TripLogService logService;

    @PostMapping("/shuttle/{id}")
    public ResponseEntity<Trip> createTrip(@PathVariable Long id, @RequestBody Trip trip){
        return ResponseEntity.ok(tripService.createTrip(id, trip));
    }

    @GetMapping("/shuttle/{id}")
    public ResponseEntity<List<Trip>> getTrips(@PathVariable Long id){
        return ResponseEntity.ok(tripService.getByShuttle(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTrip(@PathVariable Long id){
        return ResponseEntity.ok(tripService.getById(id));
    }

    // Add TripLog with DTO
    @PostMapping("/{id}/logs")
    public ResponseEntity<TripLog> addLog(
            @PathVariable Long id,
            @RequestBody TripLogRequest request) {

        // Validate required fields
        if(request.getUserId() == null || request.getAction() == null){
            return ResponseEntity.badRequest().build();
        }

        TripLog log = TripLog.builder()
                .action(request.getAction())
                .remarks(request.getRemarks())
                .timestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                .build();

        return ResponseEntity.ok(logService.addLog(id, request.getUserId(), log));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<TripLog>> getLogs(@PathVariable Long id){
        return ResponseEntity.ok(logService.getLogs(id));
    }
}
