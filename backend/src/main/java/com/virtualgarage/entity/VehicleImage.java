package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_images", indexes = {
    @Index(name = "idx_vehicle_image_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_vehicle_image_is_primary", columnList = "is_primary"),
    @Index(name = "idx_vehicle_image_created", columnList = "created_at")
})
public class VehicleImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Image URL is required")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
    
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    @Column(name = "original_filename", length = 255)
    private String originalFilename;
    
    @Column(name = "content_type", length = 100)
    private String contentType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "image_width")
    private Integer imageWidth;
    
    @Column(name = "image_height")
    private Integer imageHeight;
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(length = 255)
    private String caption;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "alt_text", length = 255)
    private String altText;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Default constructor
    public VehicleImage() {}
    
    // Constructor with essential fields
    public VehicleImage(String imageUrl, Vehicle vehicle) {
        this.imageUrl = imageUrl;
        this.vehicle = vehicle;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Integer getImageWidth() {
        return imageWidth;
    }
    
    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }
    
    public Integer getImageHeight() {
        return imageHeight;
    }
    
    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAltText() {
        return altText;
    }
    
    public void setAltText(String altText) {
        this.altText = altText;
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
    
    @Override
    public String toString() {
        return "VehicleImage{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", isPrimary=" + isPrimary +
                ", displayOrder=" + displayOrder +
                '}';
    }
}