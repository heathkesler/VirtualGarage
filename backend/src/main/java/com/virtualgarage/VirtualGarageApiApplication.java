package com.virtualgarage;

import com.virtualgarage.llm.LlmProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableConfigurationProperties(LlmProperties.class)
public class VirtualGarageApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualGarageApiApplication.class, args);
    }
}