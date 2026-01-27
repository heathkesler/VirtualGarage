package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles", indexes = {
    @Index(name = "idx_vehicle_year", columnList = "\"year\""),
    @Index(name = "idx_vehicle_make_model", columnList = "make, model"),
    @Index(name = "idx_vehicle_type", columnList = "\"type\""),
    @Index(name = "idx_vehicle_created", columnList = "created_at")
})
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Vehicle name is required")
    @Column(nullable = false, length = 255)
    private String name;
    
    @NotBlank(message = "Make is required")
    @Column(nullable = false, length = 100)
    private String make;
    
    @NotBlank(message = "Model is required")
    @Column(nullable = false, length = 100)
    private String model;
    
    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    @Column(name = "\"year\"", nullable = false)
    private Integer year;
    
    @NotBlank(message = "Type is required")
    @Column(name = "\"type\"", nullable = false, length = 100)
    private String type;
    
    @Column(length = 50)
    private String color;
    
    @Column(length = 100)
    private String engine;
    
    @Column(name = "engine_size", length = 50)
    private String engineSize;
    
    @Column(length = 50)
    private String transmission;
    
    @Column(length = 50)
    private String drivetrain;
    
    @Column(name = "fuel_type", length = 50)
    private String fuelType;
    
    private Integer mileage;
    
    @Column(name = "mileage_unit", length = 10)
    private String mileageUnit = "miles";
    
    @Column(name = "\"value\"", precision = 12, scale = 2)
    private BigDecimal value;
    
    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;
    
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;
    
    @NotBlank(message = "Status is required")
    @Column(nullable = false, length = 50)
    private String status = "Excellent";
    
    @Column(name = "vin_number", length = 17, unique = true)
    private String vinNumber;
    
    @Column(name = "license_plate", length = 20)
    private String licensePlate;
    
    @Column(name = "primary_image_url", length = 500)
    private String primaryImageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ElementCollection
    @CollectionTable(name = "vehicle_tags", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<VehicleImage> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    // Default constructor
    public Vehicle() {}
    
    // Constructor with essential fields
    public Vehicle(String name, String make, String model, Integer year, String type) {
        this.name = name;
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getEngine() {
        return engine;
    }
    
    public void setEngine(String engine) {
        this.engine = engine;
    }
    
    public String getEngineSize() {
        return engineSize;
    }
    
    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }
    
    public String getTransmission() {
        return transmission;
    }
    
    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }
    
    public String getDrivetrain() {
        return drivetrain;
    }
    
    public void setDrivetrain(String drivetrain) {
        this.drivetrain = drivetrain;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public Integer getMileage() {
        return mileage;
    }
    
    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }
    
    public String getMileageUnit() {
        return mileageUnit;
    }
    
    public void setMileageUnit(String mileageUnit) {
        this.mileageUnit = mileageUnit;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getVinNumber() {
        return vinNumber;
    }
    
    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }
    
    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public List<VehicleImage> getImages() {
        return images;
    }
    
    public void setImages(List<VehicleImage> images) {
        this.images = images;
    }
    
    public List<MaintenanceRecord> getMaintenanceRecords() {
        return maintenanceRecords;
    }
    
    public void setMaintenanceRecords(List<MaintenanceRecord> maintenanceRecords) {
        this.maintenanceRecords = maintenanceRecords;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    // Helper methods
    public void addImage(VehicleImage image) {
        images.add(image);
        image.setVehicle(this);
    }
    
    public void removeImage(VehicleImage image) {
        images.remove(image);
        image.setVehicle(null);
    }
    
    public void addMaintenanceRecord(MaintenanceRecord record) {
        maintenanceRecords.add(record);
        record.setVehicle(this);
    }
    
    public void removeMaintenanceRecord(MaintenanceRecord record) {
        maintenanceRecords.remove(record);
        record.setVehicle(null);
    }
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", type='" + type + '\'' +
                '}';
    }
}