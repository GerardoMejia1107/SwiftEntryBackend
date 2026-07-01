package com.gerardo.swiftentrybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SwiftEntryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftEntryBackendApplication.class, args);
    }

}
