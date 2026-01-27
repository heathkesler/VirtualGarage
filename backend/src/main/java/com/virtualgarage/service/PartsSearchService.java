package com.virtualgarage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualgarage.dto.PartsSearchRequest;
import com.virtualgarage.dto.PartsSearchResponse;
import com.virtualgarage.dto.PartsSearchResponse.PartSuggestion;
import com.virtualgarage.entity.Vehicle;
import com.virtualgarage.llm.LlmClient;
import com.virtualgarage.llm.LlmClientFactory;
import com.virtualgarage.llm.LlmException;
import com.virtualgarage.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for AI-powered vehicle parts search.
 * Uses configurable LLM providers (Claude, Grok) to suggest parts.
 */
@Service
public class PartsSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(PartsSearchService.class);
    
    private final LlmClientFactory llmClientFactory;
    private final VehicleRepository vehicleRepository;
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
                              ObjectMapper objectMapper) {
        this.llmClientFactory = llmClientFactory;
        this.vehicleRepository = vehicleRepository;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Search for parts using AI assistance.
     *
     * @param request The search request containing vehicle ID and query
     * @return Parts search response with AI-generated suggestions
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
        
        // Build response
        PartsSearchResponse response = new PartsSearchResponse();
        response.setVehicleId(vehicle.getId());
        response.setVehicleName(formatVehicleName(vehicle));
        response.setQuery(request.getQuery());
        response.setSuggestions(suggestions);
        response.setLlmProvider(client.getProviderName());
        response.setSummary(String.format("Found %d suggestions for %s on your %s", 
                suggestions.size(), request.getQuery(), formatVehicleName(vehicle)));
        
        return response;
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
            
            List<PartSuggestion> suggestions = new ArrayList<>();
            suggestions.add(fallback);
            return suggestions;
        }
    }
    
    private String formatVehicleName(Vehicle vehicle) {
        return String.format("%d %s %s", vehicle.getYear(), vehicle.getMake(), vehicle.getModel());
    }
}
