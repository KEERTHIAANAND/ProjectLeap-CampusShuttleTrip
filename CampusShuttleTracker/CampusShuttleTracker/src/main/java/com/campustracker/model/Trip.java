package com.campustracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shuttle_id")
    private Shuttle shuttle;

    private LocalDate tripDate;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(length = 20)
    private String currentStatus; // Scheduled / Ongoing / Completed
}
