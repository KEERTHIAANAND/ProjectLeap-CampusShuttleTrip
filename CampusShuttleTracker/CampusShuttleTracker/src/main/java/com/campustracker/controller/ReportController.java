package com.campustracker.controller;

import com.campustracker.model.Trip;
import com.campustracker.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final TripService tripService;

    @GetMapping("/active-trips")
    public ResponseEntity<List<Trip>> active(){ return ResponseEntity.ok(tripService.getActive()); }
}