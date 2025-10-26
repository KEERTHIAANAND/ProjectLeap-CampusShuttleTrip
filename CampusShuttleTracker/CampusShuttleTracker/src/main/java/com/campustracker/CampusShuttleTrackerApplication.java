package com.campustracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Campus Shuttle Tracker Spring Boot application.
 * This class bootstraps the Spring Boot application and starts the embedded server.
 */
@SpringBootApplication
public class CampusShuttleTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusShuttleTrackerApplication.class, args);
    }
}
