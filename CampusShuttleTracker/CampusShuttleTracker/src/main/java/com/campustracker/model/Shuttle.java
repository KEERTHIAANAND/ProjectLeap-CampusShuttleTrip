package com.campustracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shuttles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shuttle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shuttleId;

    @Column(length = 20, nullable = false)
    private String shuttleNumber;

    private Integer capacity;

    @Column(length = 100)
    private String route;

    @Column(length = 20)
    private String status; // Active / Maintenance / Offline

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
