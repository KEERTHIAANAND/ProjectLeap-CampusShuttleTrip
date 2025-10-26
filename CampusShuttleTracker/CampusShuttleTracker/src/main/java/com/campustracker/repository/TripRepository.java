package com.campustracker.repository;

import com.campustracker.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByShuttle_ShuttleId(Long shuttleId);
    List<Trip> findByCurrentStatus(String status);
}
