package com.virtualgarage.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class VehicleEventRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        // Vehicle Events Processing Route
        from("kafka:vehicle-events?brokers={{spring.kafka.bootstrap-servers}}" +
             "&groupId={{spring.kafka.consumer.group-id}}" +
             "&autoOffsetReset=earliest" +
             "&keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer" +
             "&valueDeserializer=org.springframework.kafka.support.serializer.JsonDeserializer" +
             "&allowManualCommit=true")
            .routeId("vehicle-events-processor")
            .log(LoggingLevel.INFO, "Processing vehicle event: ${body}")
            .choice()
                .when(simple("${body[eventType]} == 'VEHICLE_CREATED'"))
                    .to("direct:handleVehicleCreated")
                .when(simple("${body[eventType]} == 'VEHICLE_UPDATED'"))
                    .to("direct:handleVehicleUpdated")
                .when(simple("${body[eventType]} == 'VEHICLE_DELETED'"))
                    .to("direct:handleVehicleDeleted")
                .when(simple("${body[eventType]} == 'VEHICLE_VIEWED'"))
                    .to("direct:handleVehicleViewed")
                .otherwise()
                    .log(LoggingLevel.WARN, "Unknown vehicle event type: ${body[eventType]}")
            .end()
            .log(LoggingLevel.DEBUG, "Completed processing vehicle event");

        // Individual event handlers
        from("direct:handleVehicleCreated")
            .routeId("handle-vehicle-created")
            .log(LoggingLevel.INFO, "Vehicle created: ${body[vehicleId]}")
            .process(exchange -> {
                // Add any custom processing logic here
                // For example: send notifications, update caches, etc.
                Object body = exchange.getIn().getBody();
                log.info("Processing vehicle creation event: {}", body);
            });

        from("direct:handleVehicleUpdated")
            .routeId("handle-vehicle-updated")
            .log(LoggingLevel.INFO, "Vehicle updated: ${body[vehicleId]}")
            .process(exchange -> {
                Object body = exchange.getIn().getBody();
                log.info("Processing vehicle update event: {}", body);
            });

        from("direct:handleVehicleDeleted")
            .routeId("handle-vehicle-deleted")
            .log(LoggingLevel.INFO, "Vehicle deleted: ${body[vehicleId]}")
            .process(exchange -> {
                Object body = exchange.getIn().getBody();
                log.info("Processing vehicle deletion event: {}", body);
            });

        from("direct:handleVehicleViewed")
            .routeId("handle-vehicle-viewed")
            .log(LoggingLevel.DEBUG, "Vehicle viewed: ${body[vehicleId]}")
            .process(exchange -> {
                // Track vehicle views for analytics
                Object body = exchange.getIn().getBody();
                log.debug("Processing vehicle view event: {}", body);
            });

        // Image Events Processing Route
        from("kafka:image-events?brokers={{spring.kafka.bootstrap-servers}}" +
             "&groupId={{spring.kafka.consumer.group-id}}" +
             "&autoOffsetReset=earliest")
            .routeId("image-events-processor")
            .log(LoggingLevel.INFO, "Processing image event: ${body}")
            .choice()
                .when(simple("${body[eventType]} == 'IMAGE_UPLOADED'"))
                    .to("direct:handleImageUploaded")
                .when(simple("${body[eventType]} == 'IMAGE_DELETED'"))
                    .to("direct:handleImageDeleted")
                .when(simple("${body[eventType]} == 'IMAGE_SET_PRIMARY'"))
                    .to("direct:handleImageSetPrimary")
                .otherwise()
                    .log(LoggingLevel.WARN, "Unknown image event type: ${body[eventType]}")
            .end();

        from("direct:handleImageUploaded")
            .routeId("handle-image-uploaded")
            .log(LoggingLevel.INFO, "Image uploaded for vehicle: ${body[vehicleId]}")
            .process(exchange -> {
                // Process image upload - could trigger thumbnail generation, etc.
                Object body = exchange.getIn().getBody();
                log.info("Processing image upload event: {}", body);
            });

        from("direct:handleImageDeleted")
            .routeId("handle-image-deleted")
            .log(LoggingLevel.INFO, "Image deleted for vehicle: ${body[vehicleId]}")
            .process(exchange -> {
                // Clean up related resources
                Object body = exchange.getIn().getBody();
                log.info("Processing image deletion event: {}", body);
            });

        from("direct:handleImageSetPrimary")
            .routeId("handle-image-set-primary")
            .log(LoggingLevel.INFO, "Primary image set for vehicle: ${body[vehicleId]}")
            .process(exchange -> {
                // Update vehicle primary image reference
                Object body = exchange.getIn().getBody();
                log.info("Processing image set primary event: {}", body);
            });

        // User Activity Processing Route
        from("kafka:user-activity?brokers={{spring.kafka.bootstrap-servers}}" +
             "&groupId={{spring.kafka.consumer.group-id}}" +
             "&autoOffsetReset=earliest")
            .routeId("user-activity-processor")
            .log(LoggingLevel.DEBUG, "Processing user activity: ${body}")
            .choice()
                .when(simple("${body[activity]} == 'USER_SEARCH'"))
                    .to("direct:handleUserSearch")
                .when(simple("${body[activity]} == 'USER_FILTER'"))
                    .to("direct:handleUserFilter")
                .when(simple("${body[activity]} == 'USER_LOGIN'"))
                    .to("direct:handleUserLogin")
                .otherwise()
                    .log(LoggingLevel.DEBUG, "Other user activity: ${body[activity]}")
            .end();

        from("direct:handleUserSearch")
            .routeId("handle-user-search")
            .log(LoggingLevel.DEBUG, "User search performed by: ${body[userId]}")
            .process(exchange -> {
                // Track search analytics
                Object body = exchange.getIn().getBody();
                log.debug("Processing user search activity: {}", body);
            });

        from("direct:handleUserFilter")
            .routeId("handle-user-filter")
            .log(LoggingLevel.DEBUG, "User filter applied by: ${body[userId]}")
            .process(exchange -> {
                // Track filter usage analytics
                Object body = exchange.getIn().getBody();
                log.debug("Processing user filter activity: {}", body);
            });

        from("direct:handleUserLogin")
            .routeId("handle-user-login")
            .log(LoggingLevel.INFO, "User login: ${body[userId]}")
            .process(exchange -> {
                // Track user sessions
                Object body = exchange.getIn().getBody();
                log.info("Processing user login activity: {}", body);
            });

        // File Processing Route (for future image uploads)
        from("file:{{virtual-garage.file.upload-dir}}?noop=true&recursive=true&include=.*\\.(jpg|jpeg|png|gif|webp)")
            .routeId("file-processor")
            .log(LoggingLevel.INFO, "Processing uploaded file: ${header.CamelFileName}")
            .process(exchange -> {
                String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
                log.info("Processing file upload: {}", fileName);
                // Add file processing logic here (resize, thumbnail generation, etc.)
            });

        // Health Check Route
        from("timer:health-check?period=30000")
            .routeId("health-check")
            .log(LoggingLevel.DEBUG, "Health check - Camel routes are running")
            .process(exchange -> {
                // Perform health checks
                log.debug("Performing health check for Camel routes");
            });
    }
}