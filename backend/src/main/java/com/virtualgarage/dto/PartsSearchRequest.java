package com.virtualgarage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for AI-powered parts search.
 */
public class PartsSearchRequest {
    
    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;
    
    @NotBlank(message = "Search query is required")
    private String query;
    
    /**
     * Optional: Include pricing estimates in results
     */
    private boolean includePricing = true;
    
    /**
     * Optional: Include installation difficulty ratings
     */
    private boolean includeInstallationInfo = true;
    
    /**
     * Optional: Maximum number of results
     */
    private int maxResults = 10;
    
    public Long getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public boolean isIncludePricing() {
        return includePricing;
    }
    
    public void setIncludePricing(boolean includePricing) {
        this.includePricing = includePricing;
    }
    
    public boolean isIncludeInstallationInfo() {
        return includeInstallationInfo;
    }
    
    public void setIncludeInstallationInfo(boolean includeInstallationInfo) {
        this.includeInstallationInfo = includeInstallationInfo;
    }
    
    public int getMaxResults() {
        return maxResults;
    }
    
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}
