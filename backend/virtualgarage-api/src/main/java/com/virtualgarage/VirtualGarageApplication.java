package com.virtualgarage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
// import org.springframework.kafka.annotation.EnableKafka; // Temporarily removed
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for Virtual Garage backend API.
 * 
 * This application provides REST APIs for:
 * - User authentication and authorization
 * - Vehicle management and garage organization
 * - AI-powered parts search and recommendations
 * - Build management and component tracking
 * - File uploads and media management
 */
@SpringBootApplication(scanBasePackages = "com.virtualgarage")
@EnableJpaAuditing
@EnableCaching
// @EnableKafka // Temporarily removed
@EnableAsync
@EnableTransactionManagement
public class VirtualGarageApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualGarageApplication.class, args);
    }
}