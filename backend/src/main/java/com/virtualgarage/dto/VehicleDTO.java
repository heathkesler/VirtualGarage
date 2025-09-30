package com.virtualgarage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VehicleDTO {
    
    private Long id;
    
    @NotBlank(message = "Vehicle name is required")
    private String name;
    
    @NotBlank(message = "Make is required")
    private String make;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    private String color;
    private String engine;
    
    @JsonProperty("engine_size")
    private String engineSize;
    
    private String transmission;
    private String drivetrain;
    
    @JsonProperty("fuel_type")
    private String fuelType;
    
    private Integer mileage;
    
    @JsonProperty("mileage_unit")
    private String mileageUnit;
    
    private BigDecimal value;
    
    @JsonProperty("purchase_price")
    private BigDecimal purchasePrice;
    
    @JsonProperty("purchase_date")
    private LocalDateTime purchaseDate;
    
    private String status;
    
    @JsonProperty("vin_number")
    private String vinNumber;
    
    @JsonProperty("license_plate")
    private String licensePlate;
    
    @JsonProperty("primary_image_url")
    private String primaryImageUrl;
    
    private String description;
    private String notes;
    private List<String> tags;
    
    @JsonProperty("image_count")
    private Integer imageCount;
    
    @JsonProperty("maintenance_count")
    private Integer maintenanceCount;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public VehicleDTO() {}
    
    public VehicleDTO(String name, String make, String model, Integer year, String type) {
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
    
    public Integer getImageCount() {
        return imageCount;
    }
    
    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }
    
    public Integer getMaintenanceCount() {
        return maintenanceCount;
    }
    
    public void setMaintenanceCount(Integer maintenanceCount) {
        this.maintenanceCount = maintenanceCount;
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
}