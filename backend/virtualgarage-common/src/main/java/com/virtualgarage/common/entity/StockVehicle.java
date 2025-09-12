package com.virtualgarage.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_vehicles",
       indexes = {
           @Index(name = "idx_stock_vehicle_make_model_year", columnList = "make, model, year"),
           @Index(name = "idx_stock_vehicle_category", columnList = "category")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "make", nullable = false)
    private String make;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "trim")
    private String trim;

    @Column(name = "body_style")
    private String bodyStyle;

    @Column(name = "engine")
    private String engine;

    @Column(name = "transmission")
    private String transmission;

    @Column(name = "drivetrain")
    private String drivetrain;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "category")
    private String category;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public String getDisplayName() {
        return year + " " + make + " " + model + (trim != null ? " " + trim : "");
    }
}