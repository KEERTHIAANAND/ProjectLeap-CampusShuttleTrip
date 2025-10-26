package com.campustracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TripLogRequest {
    private Long userId;
    private String action;
    private String remarks;
    private LocalDateTime timestamp;
}
