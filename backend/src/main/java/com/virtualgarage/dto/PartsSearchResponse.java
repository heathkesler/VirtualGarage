package com.virtualgarage.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for AI-powered parts search.
 * Now includes installation videos and community suggestions.
 */
public class PartsSearchResponse {
    
    private Long vehicleId;
    private String vehicleName;
    private String query;
    private List<PartSuggestion> suggestions;
    private List<CommunityPartSuggestion> communitySuggestions;
    private String summary;
    private String llmProvider;
    
    public Long getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getVehicleName() {
        return vehicleName;
    }
    
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public List<PartSuggestion> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<PartSuggestion> suggestions) {
        this.suggestions = suggestions;
    }
    
    public List<CommunityPartSuggestion> getCommunitySuggestions() {
        return communitySuggestions;
    }
    
    public void setCommunitySuggestions(List<CommunityPartSuggestion> communitySuggestions) {
        this.communitySuggestions = communitySuggestions;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getLlmProvider() {
        return llmProvider;
    }
    
    public void setLlmProvider(String llmProvider) {
        this.llmProvider = llmProvider;
    }
    
    /**
     * AI-generated part suggestion.
     */
    public static class PartSuggestion {
        private String name;
        private String partNumber;
        private String description;
        private String brand;
        private String priceRange;
        private String installationDifficulty;
        private String notes;
        private List<String> whereToBuy;
        private List<InstallationVideo> installationVideos = new ArrayList<>();
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getPartNumber() {
            return partNumber;
        }
        
        public void setPartNumber(String partNumber) {
            this.partNumber = partNumber;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getBrand() {
            return brand;
        }
        
        public void setBrand(String brand) {
            this.brand = brand;
        }
        
        public String getPriceRange() {
            return priceRange;
        }
        
        public void setPriceRange(String priceRange) {
            this.priceRange = priceRange;
        }
        
        public String getInstallationDifficulty() {
            return installationDifficulty;
        }
        
        public void setInstallationDifficulty(String installationDifficulty) {
            this.installationDifficulty = installationDifficulty;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
        
        public List<String> getWhereToBuy() {
            return whereToBuy;
        }
        
        public void setWhereToBuy(List<String> whereToBuy) {
            this.whereToBuy = whereToBuy;
        }
        
        public List<InstallationVideo> getInstallationVideos() {
            return installationVideos;
        }
        
        public void setInstallationVideos(List<InstallationVideo> installationVideos) {
            this.installationVideos = installationVideos;
        }
    }
    
    /**
     * Installation video reference.
     */
    public static class InstallationVideo {
        private Long id;
        private String title;
        private String videoUrl;
        private String thumbnailUrl;
        private String embedUrl;
        private String source;
        private String duration;
        private String difficultyLevel;
        private Double rating;
        private Integer viewCount;
        private Boolean isExactMatch;  // true if video matches exact vehicle
        
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
        
        public String getVideoUrl() {
            return videoUrl;
        }
        
        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
        
        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
        
        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }
        
        public String getEmbedUrl() {
            return embedUrl;
        }
        
        public void setEmbedUrl(String embedUrl) {
            this.embedUrl = embedUrl;
        }
        
        public String getSource() {
            return source;
        }
        
        public void setSource(String source) {
            this.source = source;
        }
        
        public String getDuration() {
            return duration;
        }
        
        public void setDuration(String duration) {
            this.duration = duration;
        }
        
        public String getDifficultyLevel() {
            return difficultyLevel;
        }
        
        public void setDifficultyLevel(String difficultyLevel) {
            this.difficultyLevel = difficultyLevel;
        }
        
        public Double getRating() {
            return rating;
        }
        
        public void setRating(Double rating) {
            this.rating = rating;
        }
        
        public Integer getViewCount() {
            return viewCount;
        }
        
        public void setViewCount(Integer viewCount) {
            this.viewCount = viewCount;
        }
        
        public Boolean getIsExactMatch() {
            return isExactMatch;
        }
        
        public void setIsExactMatch(Boolean isExactMatch) {
            this.isExactMatch = isExactMatch;
        }
    }
    
    /**
     * Community-submitted part suggestion.
     */
    public static class CommunityPartSuggestion {
        private Long id;
        private String partName;
        private String partNumber;
        private String brand;
        private String description;
        private String partCategory;
        private Double pricePaid;
        private String purchaseUrl;
        private String whereToBuy;
        private String installationDifficulty;
        private Integer installationTimeMinutes;
        private String installationNotes;
        private String toolsRequired;
        private Double averageRating;
        private Integer ratingCount;
        private Integer upvoteCount;
        private Boolean isVerified;
        private String authorName;
        private List<InstallationVideo> videos = new ArrayList<>();
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
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
        
        public Double getPricePaid() {
            return pricePaid;
        }
        
        public void setPricePaid(Double pricePaid) {
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
        
        public String getInstallationDifficulty() {
            return installationDifficulty;
        }
        
        public void setInstallationDifficulty(String installationDifficulty) {
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
        
        public Double getAverageRating() {
            return averageRating;
        }
        
        public void setAverageRating(Double averageRating) {
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
        
        public Boolean getIsVerified() {
            return isVerified;
        }
        
        public void setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
        }
        
        public String getAuthorName() {
            return authorName;
        }
        
        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }
        
        public List<InstallationVideo> getVideos() {
            return videos;
        }
        
        public void setVideos(List<InstallationVideo> videos) {
            this.videos = videos;
        }
    }
}
