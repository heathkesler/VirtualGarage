package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_records", indexes = {
    @Index(name = "idx_maintenance_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_maintenance_date", columnList = "maintenance_date"),
    @Index(name = "idx_maintenance_type", columnList = "maintenance_type")
})
public class MaintenanceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Maintenance type is required")
    @Column(name = "maintenance_type", nullable = false, length = 100)
    private String maintenanceType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Maintenance date is required")
    @Column(name = "maintenance_date", nullable = false)
    private LocalDateTime maintenanceDate;
    
    @Column(name = "mileage_at_service")
    private Integer mileageAtService;
    
    @Column(name = "service_provider", length = 200)
    private String serviceProvider;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
    
    @Column(length = 20)
    private String currency = "USD";
    
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;
    
    @Column(name = "warranty_until")
    private LocalDateTime warrantyUntil;
    
    @Column(name = "next_service_due")
    private LocalDateTime nextServiceDue;
    
    @Column(name = "next_service_mileage")
    private Integer nextServiceMileage;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor
    public MaintenanceRecord() {}
    
    // Constructor with essential fields
    public MaintenanceRecord(String maintenanceType, LocalDateTime maintenanceDate, Vehicle vehicle) {
        this.maintenanceType = maintenanceType;
        this.maintenanceDate = maintenanceDate;
        this.vehicle = vehicle;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMaintenanceType() {
        return maintenanceType;
    }
    
    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getMaintenanceDate() {
        return maintenanceDate;
    }
    
    public void setMaintenanceDate(LocalDateTime maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }
    
    public Integer getMileageAtService() {
        return mileageAtService;
    }
    
    public void setMileageAtService(Integer mileageAtService) {
        this.mileageAtService = mileageAtService;
    }
    
    public String getServiceProvider() {
        return serviceProvider;
    }
    
    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    public LocalDateTime getWarrantyUntil() {
        return warrantyUntil;
    }
    
    public void setWarrantyUntil(LocalDateTime warrantyUntil) {
        this.warrantyUntil = warrantyUntil;
    }
    
    public LocalDateTime getNextServiceDue() {
        return nextServiceDue;
    }
    
    public void setNextServiceDue(LocalDateTime nextServiceDue) {
        this.nextServiceDue = nextServiceDue;
    }
    
    public Integer getNextServiceMileage() {
        return nextServiceMileage;
    }
    
    public void setNextServiceMileage(Integer nextServiceMileage) {
        this.nextServiceMileage = nextServiceMileage;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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
    
    @Override
    public String toString() {
        return "MaintenanceRecord{" +
                "id=" + id +
                ", maintenanceType='" + maintenanceType + '\'' +
                ", maintenanceDate=" + maintenanceDate +
                ", cost=" + cost +
                '}';
    }
}