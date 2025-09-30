package com.virtualgarage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class VirtualGarageApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualGarageApiApplication.class, args);
    }
}