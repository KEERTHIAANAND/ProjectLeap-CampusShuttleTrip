package com.campustracker.repository;

import com.campustracker.model.Shuttle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShuttleRepository extends JpaRepository<Shuttle, Long> {
}
