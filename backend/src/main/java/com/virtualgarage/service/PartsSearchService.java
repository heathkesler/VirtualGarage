package com.virtualgarage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualgarage.dto.PartsSearchRequest;
import com.virtualgarage.dto.PartsSearchResponse;
import com.virtualgarage.dto.PartsSearchResponse.PartSuggestion;
import com.virtualgarage.dto.PartsSearchResponse.InstallationVideo;
import com.virtualgarage.dto.PartsSearchResponse.CommunityPartSuggestion;
import com.virtualgarage.entity.InstructionalVideo;
import com.virtualgarage.entity.Vehicle;
import com.virtualgarage.llm.LlmClient;
import com.virtualgarage.llm.LlmClientFactory;
import com.virtualgarage.llm.LlmException;
import com.virtualgarage.repository.InstructionalVideoRepository;
import com.virtualgarage.repository.PartSuggestionRepository;
import com.virtualgarage.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for AI-powered vehicle parts search.
 * Uses configurable LLM providers (Claude, Grok) to suggest parts,
 * and enriches results with installation videos.
 */
@Service
public class PartsSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(PartsSearchService.class);
    private static final int MAX_VIDEOS_PER_PART = 3;
    private static final int MAX_COMMUNITY_SUGGESTIONS = 5;
    
    private final LlmClientFactory llmClientFactory;
    private final VehicleRepository vehicleRepository;
    private final InstructionalVideoRepository videoRepository;
    private final PartSuggestionRepository partSuggestionRepository;
    private final ObjectMapper objectMapper;
    
    private static final String SYSTEM_PROMPT = """
        You are an expert automotive parts advisor. Your role is to help users find the right parts
        for their vehicles. You have extensive knowledge of:
        - OEM and aftermarket parts compatibility
        - Part numbers and cross-references
        - Pricing ranges and value recommendations
        - Installation difficulty and requirements
        - Reputable brands and suppliers
        
        When responding, always consider:
        1. The specific year, make, model, and engine of the vehicle
        2. OEM vs aftermarket options with pros/cons
        3. Quality tiers (economy, mid-range, premium)
        4. Common compatibility issues or gotchas
        
        Respond ONLY with a valid JSON array of part suggestions. No additional text.
        Each suggestion should have these fields:
        - name: string (part name)
        - partNumber: string (OEM or common aftermarket number if known, or "Varies by brand")
        - description: string (brief description)
        - brand: string (recommended brand or "Multiple options")
        - priceRange: string (e.g., "$50-100")
        - installationDifficulty: string (Easy/Medium/Hard/Professional)
        - notes: string (any important notes)
        - whereToBuy: array of strings (recommended retailers/sources)
        """;
    
    public PartsSearchService(LlmClientFactory llmClientFactory, 
                              VehicleRepository vehicleRepository,
                              InstructionalVideoRepository videoRepository,
                              PartSuggestionRepository partSuggestionRepository,
                              ObjectMapper objectMapper) {
        this.llmClientFactory = llmClientFactory;
        this.vehicleRepository = vehicleRepository;
        this.videoRepository = videoRepository;
        this.partSuggestionRepository = partSuggestionRepository;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Search for parts using AI assistance, enhanced with installation videos.
     *
     * @param request The search request containing vehicle ID and query
     * @return Parts search response with AI-generated suggestions and video links
     */
    public PartsSearchResponse searchParts(PartsSearchRequest request) {
        logger.info("Parts search request for vehicle {} with query: {}", 
                request.getVehicleId(), request.getQuery());
        
        // Get vehicle details
        Vehicle vehicle = vehicleRepository.findByIdAndIsActiveTrue(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + request.getVehicleId()));
        
        // Build the user message with vehicle context
        String userMessage = buildUserMessage(vehicle, request);
        
        // Get LLM client and call API
        LlmClient client = llmClientFactory.getClient();
        String llmResponse;
        
        try {
            llmResponse = client.chat(SYSTEM_PROMPT, userMessage);
            logger.debug("LLM response: {}", llmResponse);
        } catch (LlmException e) {
            logger.error("LLM API call failed", e);
            throw e;
        }
        
        // Parse LLM response into structured data
        List<PartSuggestion> suggestions = parseLlmResponse(llmResponse);
        
        // Limit results if requested
        if (suggestions.size() > request.getMaxResults()) {
            suggestions = suggestions.subList(0, request.getMaxResults());
        }
        
        // Enrich each suggestion with installation videos
        for (PartSuggestion suggestion : suggestions) {
            List<InstallationVideo> videos = findInstallationVideos(
                    suggestion.getName(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getYear()
            );
            suggestion.setInstallationVideos(videos);
        }
        
        // Get community suggestions for this vehicle and query
        List<CommunityPartSuggestion> communitySuggestions = findCommunitySuggestions(
                request.getQuery(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear()
        );
        
        // Build response
        PartsSearchResponse response = new PartsSearchResponse();
        response.setVehicleId(vehicle.getId());
        response.setVehicleName(formatVehicleName(vehicle));
        response.setQuery(request.getQuery());
        response.setSuggestions(suggestions);
        response.setCommunitySuggestions(communitySuggestions);
        response.setLlmProvider(client.getProviderName());
        
        int videoCount = suggestions.stream()
                .mapToInt(s -> s.getInstallationVideos().size())
                .sum();
        
        response.setSummary(String.format(
                "Found %d AI suggestions with %d installation videos, plus %d community suggestions for %s on your %s", 
                suggestions.size(), videoCount, communitySuggestions.size(),
                request.getQuery(), formatVehicleName(vehicle)));
        
        return response;
    }
    
    /**
     * Find installation videos for a specific part and vehicle.
     * Prioritizes videos matching the exact vehicle, then same make, then general.
     */
    private List<InstallationVideo> findInstallationVideos(String partName, String make, 
                                                           String model, Integer year) {
        List<InstructionalVideo> videos = videoRepository.findVideosForPartAndVehicle(
                partName, make, model, year);
        
        return videos.stream()
                .limit(MAX_VIDEOS_PER_PART)
                .map(video -> convertToDto(video, make, model))
                .collect(Collectors.toList());
    }
    
    /**
     * Find community-submitted part suggestions for the vehicle and query.
     */
    private List<CommunityPartSuggestion> findCommunitySuggestions(String query, String make,
                                                                    String model, Integer year) {
        List<com.virtualgarage.entity.PartSuggestion> suggestions = 
                partSuggestionRepository.searchPartsForVehicle(query, make, model, year);
        
        return suggestions.stream()
                .limit(MAX_COMMUNITY_SUGGESTIONS)
                .map(this::convertToCommunityDto)
                .collect(Collectors.toList());
    }
    
    private InstallationVideo convertToDto(InstructionalVideo video, String make, String model) {
        InstallationVideo dto = new InstallationVideo();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setVideoUrl(video.getVideoUrl());
        dto.setThumbnailUrl(video.getThumbnailUrl());
        dto.setEmbedUrl(video.getEmbedUrl());
        dto.setSource(video.getSource().name());
        dto.setDuration(video.getFormattedDuration());
        dto.setDifficultyLevel(video.getDifficultyLevel() != null ? 
                video.getDifficultyLevel().name() : null);
        dto.setRating(video.getAverageRating() != null ? 
                video.getAverageRating().doubleValue() : null);
        dto.setViewCount(video.getViewCount());
        
        // Check if video is an exact match for this vehicle
        boolean isExact = video.getVehicleMake() != null && 
                          video.getVehicleMake().equalsIgnoreCase(make) &&
                          video.getVehicleModel() != null &&
                          video.getVehicleModel().equalsIgnoreCase(model);
        dto.setIsExactMatch(isExact);
        
        return dto;
    }
    
    private CommunityPartSuggestion convertToCommunityDto(com.virtualgarage.entity.PartSuggestion entity) {
        CommunityPartSuggestion dto = new CommunityPartSuggestion();
        dto.setId(entity.getId());
        dto.setPartName(entity.getPartName());
        dto.setPartNumber(entity.getPartNumber());
        dto.setBrand(entity.getBrand());
        dto.setDescription(entity.getDescription());
        dto.setPartCategory(entity.getPartCategory());
        dto.setPricePaid(entity.getPricePaid() != null ? entity.getPricePaid().doubleValue() : null);
        dto.setPurchaseUrl(entity.getPurchaseUrl());
        dto.setWhereToBuy(entity.getWhereToBuy());
        dto.setInstallationDifficulty(entity.getInstallationDifficulty() != null ? 
                entity.getInstallationDifficulty().name() : null);
        dto.setInstallationTimeMinutes(entity.getInstallationTimeMinutes());
        dto.setInstallationNotes(entity.getInstallationNotes());
        dto.setToolsRequired(entity.getToolsRequired());
        dto.setAverageRating(entity.getAverageRating() != null ? 
                entity.getAverageRating().doubleValue() : null);
        dto.setRatingCount(entity.getRatingCount());
        dto.setUpvoteCount(entity.getUpvoteCount());
        dto.setIsVerified(entity.getIsVerified());
        dto.setAuthorName(entity.getAuthor().getDisplayName());
        
        // Get videos linked to this part suggestion
        List<InstallationVideo> videos = entity.getVideos().stream()
                .filter(v -> v.getIsActive())
                .limit(MAX_VIDEOS_PER_PART)
                .map(v -> convertToDto(v, entity.getVehicleMake(), entity.getVehicleModel()))
                .collect(Collectors.toList());
        dto.setVideos(videos);
        
        return dto;
    }
    
    private String buildUserMessage(Vehicle vehicle, PartsSearchRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Vehicle: ").append(vehicle.getYear()).append(" ")
          .append(vehicle.getMake()).append(" ").append(vehicle.getModel());
        
        if (vehicle.getEngine() != null) {
            sb.append("\nEngine: ").append(vehicle.getEngine());
        }
        if (vehicle.getEngineSize() != null) {
            sb.append(" (").append(vehicle.getEngineSize()).append(")");
        }
        if (vehicle.getTransmission() != null) {
            sb.append("\nTransmission: ").append(vehicle.getTransmission());
        }
        if (vehicle.getDrivetrain() != null) {
            sb.append("\nDrivetrain: ").append(vehicle.getDrivetrain());
        }
        
        sb.append("\n\nSearch query: ").append(request.getQuery());
        sb.append("\n\nPlease suggest up to ").append(request.getMaxResults()).append(" relevant parts.");
        
        if (request.isIncludePricing()) {
            sb.append("\nInclude pricing estimates.");
        }
        if (request.isIncludeInstallationInfo()) {
            sb.append("\nInclude installation difficulty ratings.");
        }
        
        return sb.toString();
    }
    
    private List<PartSuggestion> parseLlmResponse(String response) {
        try {
            // Clean up response - remove markdown code blocks if present
            String cleaned = response.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();
            
            return objectMapper.readValue(cleaned, new TypeReference<List<PartSuggestion>>() {});
        } catch (Exception e) {
            logger.warn("Failed to parse LLM response as JSON, returning raw response as single suggestion", e);
            
            // Fallback: return raw response as a single "summary" suggestion
            PartSuggestion fallback = new PartSuggestion();
            fallback.setName("AI Suggestions");
            fallback.setDescription(response);
            fallback.setPartNumber("N/A");
            fallback.setBrand("See description");
            fallback.setPriceRange("Varies");
            fallback.setInstallationDifficulty("Varies");
            fallback.setNotes("Raw AI response - structured parsing failed");
            fallback.setWhereToBuy(List.of("RockAuto", "AutoZone", "Amazon"));
            fallback.setInstallationVideos(new ArrayList<>());
            
            List<PartSuggestion> suggestions = new ArrayList<>();
            suggestions.add(fallback);
            return suggestions;
        }
    }
    
    private String formatVehicleName(Vehicle vehicle) {
        return String.format("%d %s %s", vehicle.getYear(), vehicle.getMake(), vehicle.getModel());
    }
}
