package com.virtualgarage.dto;

import java.util.List;

/**
 * Response DTO for AI-powered parts search.
 */
public class PartsSearchResponse {
    
    private Long vehicleId;
    private String vehicleName;
    private String query;
    private List<PartSuggestion> suggestions;
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
    
    public static class PartSuggestion {
        private String name;
        private String partNumber;
        private String description;
        private String brand;
        private String priceRange;
        private String installationDifficulty;
        private String notes;
        private List<String> whereToBuy;
        
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
    }
}
