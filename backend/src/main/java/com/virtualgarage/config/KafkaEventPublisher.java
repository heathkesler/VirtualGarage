package com.virtualgarage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${virtual-garage.kafka.topics.vehicle-events:vehicle-events}")
    private String vehicleEventsTopic;

    @Value("${virtual-garage.kafka.topics.image-events:image-events}")
    private String imageEventsTopic;

    @Value("${virtual-garage.kafka.topics.user-activity:user-activity}")
    private String userActivityTopic;

    @Autowired
    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishVehicleEvent(String eventType, Long vehicleId, Object data) {
        Map<String, Object> event = Map.of(
            "eventType", eventType,
            "vehicleId", vehicleId,
            "data", data,
            "timestamp", LocalDateTime.now(),
            "source", "virtual-garage-api"
        );

        logger.debug("Publishing vehicle event: {} for vehicle ID: {}", eventType, vehicleId);
        publishEvent(vehicleEventsTopic, vehicleId.toString(), event);
    }

    public void publishImageEvent(String eventType, Long vehicleId, Long imageId, Object data) {
        Map<String, Object> event = Map.of(
            "eventType", eventType,
            "vehicleId", vehicleId,
            "imageId", imageId,
            "data", data,
            "timestamp", LocalDateTime.now(),
            "source", "virtual-garage-api"
        );

        logger.debug("Publishing image event: {} for vehicle ID: {}, image ID: {}", eventType, vehicleId, imageId);
        publishEvent(imageEventsTopic, vehicleId.toString(), event);
    }

    public void publishUserActivity(String activity, String userId, Object data) {
        Map<String, Object> event = Map.of(
            "activity", activity,
            "userId", userId,
            "data", data,
            "timestamp", LocalDateTime.now(),
            "source", "virtual-garage-api"
        );

        logger.debug("Publishing user activity: {} for user: {}", activity, userId);
        publishEvent(userActivityTopic, userId, event);
    }

    private void publishEvent(String topic, String key, Object event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Successfully sent event to topic {} with key {} at offset {}",
                        topic, key, result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send event to topic {} with key {}", topic, key, ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing event to topic {} with key {}", topic, key, e);
        }
    }

    // Vehicle event types
    public static final String VEHICLE_CREATED = "VEHICLE_CREATED";
    public static final String VEHICLE_UPDATED = "VEHICLE_UPDATED";
    public static final String VEHICLE_DELETED = "VEHICLE_DELETED";
    public static final String VEHICLE_VIEWED = "VEHICLE_VIEWED";

    // Image event types
    public static final String IMAGE_UPLOADED = "IMAGE_UPLOADED";
    public static final String IMAGE_DELETED = "IMAGE_DELETED";
    public static final String IMAGE_SET_PRIMARY = "IMAGE_SET_PRIMARY";

    // User activity types
    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_SEARCH = "USER_SEARCH";
    public static final String USER_FILTER = "USER_FILTER";
}