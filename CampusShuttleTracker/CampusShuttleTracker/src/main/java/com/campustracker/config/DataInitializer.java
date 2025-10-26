package com.campustracker.config;

import com.campustracker.model.*;
import com.campustracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ShuttleRepository shuttleRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private TripLogRepository tripLogRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            return; // Data already initialized
        }

        // Create sample users
        User admin = User.builder()
            .name("Admin User")
            .email("admin@campus.edu")
            .role("Admin")
            .joinDate(LocalDateTime.now().minusDays(30))
            .build();
        userRepository.save(admin);

        User student1 = User.builder()
            .name("John Smith")
            .email("john.smith@student.edu")
            .role("Student")
            .joinDate(LocalDateTime.now().minusDays(15))
            .build();
        userRepository.save(student1);

        User student2 = User.builder()
            .name("Jane Doe")
            .email("jane.doe@student.edu")
            .role("Student")
            .joinDate(LocalDateTime.now().minusDays(10))
            .build();
        userRepository.save(student2);

        User staff = User.builder()
            .name("Dr. Wilson")
            .email("wilson@staff.edu")
            .role("Staff")
            .joinDate(LocalDateTime.now().minusDays(20))
            .build();
        userRepository.save(staff);

        // Create sample shuttles
        Shuttle shuttle1 = Shuttle.builder()
            .shuttleNumber("SH001")
            .capacity(40)
            .route("Main Campus Loop")
            .status("Active")
            .createdAt(LocalDateTime.now().minusDays(25))
            .build();
        shuttleRepository.save(shuttle1);

        Shuttle shuttle2 = Shuttle.builder()
            .shuttleNumber("SH002")
            .capacity(35)
            .route("North Campus Express")
            .status("Active")
            .createdAt(LocalDateTime.now().minusDays(20))
            .build();
        shuttleRepository.save(shuttle2);

        Shuttle shuttle3 = Shuttle.builder()
            .shuttleNumber("SH003")
            .capacity(30)
            .route("South Campus Local")
            .status("Maintenance")
            .createdAt(LocalDateTime.now().minusDays(15))
            .build();
        shuttleRepository.save(shuttle3);

        // Create sample trips
        Trip trip1 = Trip.builder()
            .shuttle(shuttle1)
            .tripDate(LocalDate.now())
            .startTime(LocalTime.of(8, 0))
            .endTime(null) // Currently ongoing
            .currentStatus("Ongoing")
            .build();
        tripRepository.save(trip1);

        Trip trip2 = Trip.builder()
            .shuttle(shuttle2)
            .tripDate(LocalDate.now())
            .startTime(LocalTime.of(9, 30))
            .endTime(null) // Currently ongoing
            .currentStatus("Ongoing")
            .build();
        tripRepository.save(trip2);

        Trip trip3 = Trip.builder()
            .shuttle(shuttle1)
            .tripDate(LocalDate.now().minusDays(1))
            .startTime(LocalTime.of(7, 30))
            .endTime(LocalTime.of(18, 0))
            .currentStatus("Completed")
            .build();
        tripRepository.save(trip3);

        Trip trip4 = Trip.builder()
            .shuttle(shuttle2)
            .tripDate(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(8, 30))
            .endTime(null)
            .currentStatus("Scheduled")
            .build();
        tripRepository.save(trip4);

        // Create sample trip logs
        TripLog log1 = TripLog.builder()
            .trip(trip1)
            .user(student1)
            .action("Check-in")
            .timestamp(LocalDateTime.now().minusHours(2))
            .remarks("Student boarded at Main Gate")
            .build();
        tripLogRepository.save(log1);

        TripLog log2 = TripLog.builder()
            .trip(trip1)
            .user(student2)
            .action("Check-in")
            .timestamp(LocalDateTime.now().minusHours(1))
            .remarks("Student boarded at Library Stop")
            .build();
        tripLogRepository.save(log2);

        TripLog log3 = TripLog.builder()
            .trip(trip3)
            .user(staff)
            .action("StatusUpdate")
            .timestamp(LocalDateTime.now().minusDays(1).plusHours(10))
            .remarks("Completed")
            .build();
        tripLogRepository.save(log3);

        System.out.println("Sample data initialized successfully!");
        System.out.println("Created: 4 users, 3 shuttles, 4 trips, 3 trip logs");
    }
}