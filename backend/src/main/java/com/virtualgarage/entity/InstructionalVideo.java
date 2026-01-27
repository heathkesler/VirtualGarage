package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Instructional video entity for DIY guides and tutorials.
 * Can be linked to parts, vehicles, or submitted by users.
 */
@Entity
@Table(name = "instructional_videos", indexes = {
    @Index(name = "idx_video_submitter", columnList = "submitted_by_id"),
    @Index(name = "idx_video_part", columnList = "part_suggestion_id"),
    @Index(name = "idx_video_vehicle", columnList = "vehicle_make, vehicle_model"),
    @Index(name = "idx_video_category", columnList = "category"),
    @Index(name = "idx_video_rating", columnList = "average_rating"),
    @Index(name = "idx_video_part_type", columnList = "part_type")
})
public class InstructionalVideo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Video title is required")
    @Size(min = 5, max = 255)
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "Video URL is required")
    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;
    
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VideoSource source = VideoSource.YOUTUBE;
    
    @Column(name = "video_id", length = 100)
    private String videoId;
    
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    
    // Video categorization
    @NotBlank(message = "Category is required")
    @Column(nullable = false, length = 100)
    private String category;
    
    @Column(name = "part_type", length = 100)
    private String partType;
    
    // Vehicle compatibility
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
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id")
    private User submittedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_suggestion_id")
    private PartSuggestion partSuggestion;
    
    // Ratings
    @Column(name = "average_rating", precision = 3, scale = 2)
    private java.math.BigDecimal averageRating = java.math.BigDecimal.ZERO;
    
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", length = 20)
    private DifficultyLevel difficultyLevel;
    
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
    
    public enum VideoSource {
        YOUTUBE,
        VIMEO,
        DAILYMOTION,
        CUSTOM
    }
    
    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }
    
    // Constructors
    public InstructionalVideo() {}
    
    public InstructionalVideo(String title, String videoUrl, String category) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.category = category;
        parseVideoUrl(videoUrl);
    }
    
    // Parse video URL to extract source and ID
    private void parseVideoUrl(String url) {
        if (url == null) return;
        
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            this.source = VideoSource.YOUTUBE;
            // Extract video ID from various YouTube URL formats
            if (url.contains("v=")) {
                int start = url.indexOf("v=") + 2;
                int end = url.indexOf("&", start);
                this.videoId = end > 0 ? url.substring(start, end) : url.substring(start);
            } else if (url.contains("youtu.be/")) {
                int start = url.indexOf("youtu.be/") + 9;
                int end = url.indexOf("?", start);
                this.videoId = end > 0 ? url.substring(start, end) : url.substring(start);
            }
            // Set thumbnail if not already set
            if (this.thumbnailUrl == null && this.videoId != null) {
                this.thumbnailUrl = "https://img.youtube.com/vi/" + this.videoId + "/mqdefault.jpg";
            }
        } else if (url.contains("vimeo.com")) {
            this.source = VideoSource.VIMEO;
            // Extract Vimeo ID
            String[] parts = url.split("/");
            this.videoId = parts[parts.length - 1].split("\\?")[0];
        } else if (url.contains("dailymotion.com")) {
            this.source = VideoSource.DAILYMOTION;
        } else {
            this.source = VideoSource.CUSTOM;
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        parseVideoUrl(videoUrl);
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public VideoSource getSource() {
        return source;
    }
    
    public void setSource(VideoSource source) {
        this.source = source;
    }
    
    public String getVideoId() {
        return videoId;
    }
    
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    
    public Integer getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPartType() {
        return partType;
    }
    
    public void setPartType(String partType) {
        this.partType = partType;
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
    
    public User getSubmittedBy() {
        return submittedBy;
    }
    
    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }
    
    public PartSuggestion getPartSuggestion() {
        return partSuggestion;
    }
    
    public void setPartSuggestion(PartSuggestion partSuggestion) {
        this.partSuggestion = partSuggestion;
    }
    
    public java.math.BigDecimal getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(java.math.BigDecimal averageRating) {
        this.averageRating = averageRating;
    }
    
    public Integer getRatingCount() {
        return ratingCount;
    }
    
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
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
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public String getEmbedUrl() {
        if (source == VideoSource.YOUTUBE && videoId != null) {
            return "https://www.youtube.com/embed/" + videoId;
        } else if (source == VideoSource.VIMEO && videoId != null) {
            return "https://player.vimeo.com/video/" + videoId;
        }
        return videoUrl;
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
    
    public String getFormattedDuration() {
        if (durationSeconds == null) return null;
        int hours = durationSeconds / 3600;
        int minutes = (durationSeconds % 3600) / 60;
        int seconds = durationSeconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }
}
