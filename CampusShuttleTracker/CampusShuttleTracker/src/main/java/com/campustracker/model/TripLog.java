package com.campustracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20)
    private String action; // Boarding / Alighting / StatusUpdate

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(length = 255)
    private String remarks;
}
