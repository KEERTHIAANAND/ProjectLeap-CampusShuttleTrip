package com.campustracker.repository;

import com.campustracker.model.TripLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripLogRepository extends JpaRepository<TripLog, Long> {
    List<TripLog> findByTrip_TripId(Long tripId);
}
