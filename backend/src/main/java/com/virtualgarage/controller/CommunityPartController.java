package com.virtualgarage.controller;

import com.virtualgarage.entity.PartSuggestion;
import com.virtualgarage.service.CommunityPartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for community-submitted part suggestions.
 */
@RestController
@RequestMapping("/community/parts")
@Tag(name = "Community Parts", description = "Community part suggestion endpoints")
public class CommunityPartController {
    
    private final CommunityPartService communityPartService;
    
    public CommunityPartController(CommunityPartService communityPartService) {
        this.communityPartService = communityPartService;
    }
    
    @PostMapping
    @Operation(summary = "Submit a new part suggestion")
    public ResponseEntity<PartSuggestion> createPartSuggestion(@RequestBody CreatePartSuggestionRequest request) {
        PartSuggestion suggestion = communityPartService.createPartSuggestion(
                request.authorId,
                request.partName,
                request.partNumber,
                request.brand,
                request.description,
                request.partCategory,
                request.vehicleMake,
                request.vehicleModel,
                request.yearStart,
                request.yearEnd,
                request.engineType,
                request.pricePaid,
                request.purchaseUrl,
                request.whereToBuy,
                request.installationDifficulty,
                request.installationTimeMinutes,
                request.installationNotes,
                request.toolsRequired
        );
        return ResponseEntity.ok(suggestion);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get part suggestion by ID")
    public ResponseEntity<PartSuggestion> getPartSuggestion(@PathVariable Long id) {
        return ResponseEntity.ok(communityPartService.getPartSuggestion(id));
    }
    
    @GetMapping
    @Operation(summary = "Get recent part suggestions")
    public ResponseEntity<Page<PartSuggestion>> getRecentSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(communityPartService.getRecentSuggestions(pageable));
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get part suggestions by category")
    public ResponseEntity<Page<PartSuggestion>> getSuggestionsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(communityPartService.getSuggestionsByCategory(category, pageable));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search part suggestions")
    public ResponseEntity<Page<PartSuggestion>> searchSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(communityPartService.searchSuggestions(query, pageable));
    }
    
    @GetMapping("/compatible")
    @Operation(summary = "Find part suggestions compatible with a vehicle")
    public ResponseEntity<Page<PartSuggestion>> getCompatibleParts(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(communityPartService.getCompatibleParts(make, model, year, pageable));
    }
    
    @GetMapping("/vehicle-search")
    @Operation(summary = "Search part suggestions for a specific vehicle")
    public ResponseEntity<List<PartSuggestion>> searchPartsForVehicle(
            @RequestParam String query,
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year) {
        return ResponseEntity.ok(communityPartService.searchPartsForVehicle(query, make, model, year));
    }
    
    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated part suggestions")
    public ResponseEntity<List<PartSuggestion>> getTopRatedSuggestions() {
        return ResponseEntity.ok(communityPartService.getTopRatedSuggestions());
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get all part categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(communityPartService.getAllCategories());
    }
    
    @PostMapping("/{id}/upvote")
    @Operation(summary = "Upvote a part suggestion")
    public ResponseEntity<PartSuggestion> upvote(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(communityPartService.upvote(id, userId));
    }
    
    @PostMapping("/{id}/downvote")
    @Operation(summary = "Downvote a part suggestion")
    public ResponseEntity<PartSuggestion> downvote(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(communityPartService.downvote(id, userId));
    }
    
    @PostMapping("/{id}/rate")
    @Operation(summary = "Rate a part suggestion")
    public ResponseEntity<PartSuggestion> rateSuggestion(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam int rating) {
        return ResponseEntity.ok(communityPartService.rateSuggestion(id, userId, rating));
    }
    
    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify a part suggestion (moderator)")
    public ResponseEntity<PartSuggestion> verifySuggestion(
            @PathVariable Long id,
            @RequestParam Long moderatorId) {
        return ResponseEntity.ok(communityPartService.verifySuggestion(id, moderatorId));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a part suggestion")
    public ResponseEntity<Void> deleteSuggestion(
            @PathVariable Long id,
            @RequestParam Long userId) {
        communityPartService.deleteSuggestion(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    // Request DTO
    public static class CreatePartSuggestionRequest {
        public Long authorId;
        public String partName;
        public String partNumber;
        public String brand;
        public String description;
        public String partCategory;
        public String vehicleMake;
        public String vehicleModel;
        public Integer yearStart;
        public Integer yearEnd;
        public String engineType;
        public BigDecimal pricePaid;
        public String purchaseUrl;
        public String whereToBuy;
        public PartSuggestion.InstallationDifficulty installationDifficulty;
        public Integer installationTimeMinutes;
        public String installationNotes;
        public String toolsRequired;
    }
}
