package com.campustracker.repository;

import com.campustracker.model.TripLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TripLogRepository extends JpaRepository<TripLog, Long> {
    List<TripLog> findByTrip_TripId(Long tripId);
    
    // Find logs by user ID ordered by timestamp descending
    @Query("SELECT tl FROM TripLog tl WHERE tl.user.userId = :userId ORDER BY tl.timestamp DESC")
    List<TripLog> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
    
    // Find all logs ordered by timestamp descending
    @Query("SELECT tl FROM TripLog tl ORDER BY tl.timestamp DESC")
    List<TripLog> findAllOrderByTimestampDesc();
}
