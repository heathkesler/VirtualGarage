package com.virtualgarage.controller;

import com.virtualgarage.dto.PartsSearchRequest;
import com.virtualgarage.dto.PartsSearchResponse;
import com.virtualgarage.llm.LlmException;
import com.virtualgarage.service.PartsSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for AI-powered parts search.
 */
@RestController
@RequestMapping("/parts")
@Tag(name = "Parts Search", description = "AI-powered vehicle parts search")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PartsSearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(PartsSearchController.class);
    
    private final PartsSearchService partsSearchService;
    
    public PartsSearchController(PartsSearchService partsSearchService) {
        this.partsSearchService = partsSearchService;
    }
    
    @PostMapping("/search")
    @Operation(summary = "Search for parts", 
               description = "Use AI to find compatible parts for a specific vehicle")
    public ResponseEntity<PartsSearchResponse> searchParts(@Valid @RequestBody PartsSearchRequest request) {
        logger.info("Parts search request received for vehicle: {}", request.getVehicleId());
        
        try {
            PartsSearchResponse response = partsSearchService.searchParts(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            throw e;
        } catch (LlmException e) {
            logger.error("LLM service error: {}", e.getMessage());
            throw e;
        }
    }
    
    @GetMapping("/search/{vehicleId}")
    @Operation(summary = "Quick search for parts",
               description = "Simple GET endpoint for basic parts search")
    public ResponseEntity<PartsSearchResponse> quickSearch(
            @PathVariable Long vehicleId,
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults) {
        
        PartsSearchRequest request = new PartsSearchRequest();
        request.setVehicleId(vehicleId);
        request.setQuery(query);
        request.setMaxResults(maxResults);
        
        return searchParts(request);
    }
    
    @GetMapping("/providers")
    @Operation(summary = "Get available LLM providers",
               description = "List configured LLM providers for parts search")
    public ResponseEntity<Map<String, Object>> getProviders() {
        // This could be expanded to show which providers are configured
        return ResponseEntity.ok(Map.of(
            "providers", new String[]{"claude", "grok"},
            "note", "Provider is configured via application properties"
        ));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Bad Request",
            "message", e.getMessage()
        ));
    }
    
    @ExceptionHandler(LlmException.class)
    public ResponseEntity<Map<String, String>> handleLlmException(LlmException e) {
        return ResponseEntity.internalServerError().body(Map.of(
            "error", "LLM Service Error",
            "message", e.getMessage(),
            "provider", e.getProvider()
        ));
    }
}
