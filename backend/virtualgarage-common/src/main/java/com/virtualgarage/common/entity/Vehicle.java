package com.virtualgarage.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles",
       indexes = {
           @Index(name = "idx_vehicle_garage", columnList = "garage_id"),
           @Index(name = "idx_vehicle_make_model", columnList = "make, model"),
           @Index(name = "idx_vehicle_year", columnList = "year"),
           @Index(name = "idx_vehicle_vin", columnList = "vin", unique = true)
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "make", nullable = false)
    private String make;

    @NotBlank
    @Size(max = 50)
    @Column(name = "model", nullable = false)
    private String model;

    @Min(1900)
    @Max(2030)
    @Column(name = "year", nullable = false)
    private Integer year;

    @Size(max = 50)
    @Column(name = "color")
    private String color;

    @Size(max = 17)
    @Column(name = "vin", unique = true)
    private String vin;

    @Column(name = "mileage")
    private Integer mileage;

    @Size(max = 500)
    @Column(name = "photo")
    private String photo;

    @Size(max = 500)
    @Column(name = "stock_photo_url")
    private String stockPhotoUrl;

    @Size(max = 50)
    @Column(name = "trim")
    private String trim;

    @Size(max = 50)
    @Column(name = "engine")
    private String engine;

    @Size(max = 50)
    @Column(name = "transmission")
    private String transmission;

    @Size(max = 20)
    @Column(name = "fuel_type")
    private String fuelType;

    @Size(max = 20)
    @Column(name = "drivetrain")
    private String drivetrain;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garage_id", nullable = false)
    private Garage garage;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Build> builds = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VehiclePhoto> photos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void addBuild(Build build) {
        builds.add(build);
        build.setVehicle(this);
    }

    public void removeBuild(Build build) {
        builds.remove(build);
        build.setVehicle(null);
    }

    public void addPhoto(VehiclePhoto photo) {
        photos.add(photo);
        photo.setVehicle(this);
    }

    public void removePhoto(VehiclePhoto photo) {
        photos.remove(photo);
        photo.setVehicle(null);
    }

    // Computed properties
    public String getDisplayName() {
        return year + " " + make + " " + model + (trim != null ? " " + trim : "");
    }
}