package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User-submitted part suggestion entity.
 * Allows community members to recommend parts for specific vehicles.
 */
@Entity
@Table(name = "part_suggestions", indexes = {
    @Index(name = "idx_part_suggestion_author", columnList = "author_id"),
    @Index(name = "idx_part_suggestion_vehicle", columnList = "vehicle_make, vehicle_model"),
    @Index(name = "idx_part_suggestion_category", columnList = "part_category"),
    @Index(name = "idx_part_suggestion_rating", columnList = "average_rating"),
    @Index(name = "idx_part_suggestion_created", columnList = "created_at")
})
public class PartSuggestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @NotBlank(message = "Part name is required")
    @Size(min = 2, max = 255)
    @Column(name = "part_name", nullable = false, length = 255)
    private String partName;
    
    @Column(name = "part_number", length = 100)
    private String partNumber;
    
    @NotBlank(message = "Brand is required")
    @Column(nullable = false, length = 100)
    private String brand;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "Part category is required")
    @Column(name = "part_category", nullable = false, length = 100)
    private String partCategory;
    
    // Vehicle compatibility info
    @Column(name = "vehicle_make", length = 100)
    private String vehicleMake;
    
    @Column(name = "vehicle_model", length = 100)
    private String vehicleModel;
    
    @Column(name = "year_start")
    private Integer yearStart;
    
    @Column(name = "year_end")
    private Integer yearEnd;
    
    @Column(name = "engine_type", length = 100)
    private String engineType;
    
    // Pricing and sourcing
    @Column(name = "price_paid", precision = 10, scale = 2)
    private BigDecimal pricePaid;
    
    @Column(name = "purchase_url", length = 500)
    private String purchaseUrl;
    
    @Column(name = "where_to_buy", length = 255)
    private String whereToBuy;
    
    // Installation info
    @Enumerated(EnumType.STRING)
    @Column(name = "installation_difficulty", length = 20)
    private InstallationDifficulty installationDifficulty;
    
    @Column(name = "installation_time_minutes")
    private Integer installationTimeMinutes;
    
    @Column(name = "installation_notes", columnDefinition = "TEXT")
    private String installationNotes;
    
    @Column(name = "tools_required", columnDefinition = "TEXT")
    private String toolsRequired;
    
    // Ratings and community feedback
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;
    
    @Column(name = "upvote_count", nullable = false)
    private Integer upvoteCount = 0;
    
    @Column(name = "downvote_count", nullable = false)
    private Integer downvoteCount = 0;
    
    // Related videos
    @OneToMany(mappedBy = "partSuggestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstructionalVideo> videos = new ArrayList<>();
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum InstallationDifficulty {
        EASY,           // Anyone can do it
        MODERATE,       // Some experience helpful
        DIFFICULT,      // Experienced DIYer
        PROFESSIONAL    // Best left to pros
    }
    
    // Constructors
    public PartSuggestion() {}
    
    public PartSuggestion(User author, String partName, String brand, String partCategory) {
        this.author = author;
        this.partName = partName;
        this.brand = brand;
        this.partCategory = partCategory;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public String getPartName() {
        return partName;
    }
    
    public void setPartName(String partName) {
        this.partName = partName;
    }
    
    public String getPartNumber() {
        return partNumber;
    }
    
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPartCategory() {
        return partCategory;
    }
    
    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }
    
    public String getVehicleMake() {
        return vehicleMake;
    }
    
    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }
    
    public String getVehicleModel() {
        return vehicleModel;
    }
    
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    
    public Integer getYearStart() {
        return yearStart;
    }
    
    public void setYearStart(Integer yearStart) {
        this.yearStart = yearStart;
    }
    
    public Integer getYearEnd() {
        return yearEnd;
    }
    
    public void setYearEnd(Integer yearEnd) {
        this.yearEnd = yearEnd;
    }
    
    public String getEngineType() {
        return engineType;
    }
    
    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }
    
    public BigDecimal getPricePaid() {
        return pricePaid;
    }
    
    public void setPricePaid(BigDecimal pricePaid) {
        this.pricePaid = pricePaid;
    }
    
    public String getPurchaseUrl() {
        return purchaseUrl;
    }
    
    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }
    
    public String getWhereToBuy() {
        return whereToBuy;
    }
    
    public void setWhereToBuy(String whereToBuy) {
        this.whereToBuy = whereToBuy;
    }
    
    public InstallationDifficulty getInstallationDifficulty() {
        return installationDifficulty;
    }
    
    public void setInstallationDifficulty(InstallationDifficulty installationDifficulty) {
        this.installationDifficulty = installationDifficulty;
    }
    
    public Integer getInstallationTimeMinutes() {
        return installationTimeMinutes;
    }
    
    public void setInstallationTimeMinutes(Integer installationTimeMinutes) {
        this.installationTimeMinutes = installationTimeMinutes;
    }
    
    public String getInstallationNotes() {
        return installationNotes;
    }
    
    public void setInstallationNotes(String installationNotes) {
        this.installationNotes = installationNotes;
    }
    
    public String getToolsRequired() {
        return toolsRequired;
    }
    
    public void setToolsRequired(String toolsRequired) {
        this.toolsRequired = toolsRequired;
    }
    
    public BigDecimal getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getRatingCount() {
        return ratingCount;
    }
    
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    public Integer getUpvoteCount() {
        return upvoteCount;
    }
    
    public void setUpvoteCount(Integer upvoteCount) {
        this.upvoteCount = upvoteCount;
    }
    
    public Integer getDownvoteCount() {
        return downvoteCount;
    }
    
    public void setDownvoteCount(Integer downvoteCount) {
        this.downvoteCount = downvoteCount;
    }
    
    public List<InstructionalVideo> getVideos() {
        return videos;
    }
    
    public void setVideos(List<InstructionalVideo> videos) {
        this.videos = videos;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
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
    
    // Helper methods
    public void addVideo(InstructionalVideo video) {
        videos.add(video);
        video.setPartSuggestion(this);
    }
    
    public void upvote() {
        this.upvoteCount++;
    }
    
    public void downvote() {
        this.downvoteCount++;
    }
    
    public int getScore() {
        return upvoteCount - downvoteCount;
    }
    
    public boolean isCompatibleWith(String make, String model, Integer year) {
        if (vehicleMake != null && !vehicleMake.equalsIgnoreCase(make)) {
            return false;
        }
        if (vehicleModel != null && !vehicleModel.equalsIgnoreCase(model)) {
            return false;
        }
        if (year != null && yearStart != null && year < yearStart) {
            return false;
        }
        if (year != null && yearEnd != null && year > yearEnd) {
            return false;
        }
        return true;
    }
}
