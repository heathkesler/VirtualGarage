package com.virtualgarage.controller;

import com.virtualgarage.entity.InstructionalVideo;
import com.virtualgarage.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for instructional videos.
 */
@RestController
@RequestMapping("/videos")
@Tag(name = "Videos", description = "Instructional video endpoints")
public class VideoController {
    
    private final VideoService videoService;
    
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }
    
    @PostMapping
    @Operation(summary = "Submit a new instructional video")
    public ResponseEntity<InstructionalVideo> createVideo(@RequestBody CreateVideoRequest request) {
        InstructionalVideo video = videoService.createVideo(
                request.submitterId,
                request.title,
                request.videoUrl,
                request.description,
                request.category,
                request.partType,
                request.vehicleMake,
                request.vehicleModel,
                request.yearStart,
                request.yearEnd,
                request.difficultyLevel
        );
        return ResponseEntity.ok(video);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get video by ID")
    public ResponseEntity<InstructionalVideo> getVideo(@PathVariable Long id) {
        videoService.incrementViewCount(id);
        return ResponseEntity.ok(videoService.getVideo(id));
    }
    
    @GetMapping
    @Operation(summary = "Get recent videos")
    public ResponseEntity<Page<InstructionalVideo>> getRecentVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(videoService.getRecentVideos(pageable));
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get videos by category")
    public ResponseEntity<Page<InstructionalVideo>> getVideosByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(videoService.getVideosByCategory(category, pageable));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search videos")
    public ResponseEntity<Page<InstructionalVideo>> searchVideos(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(videoService.searchVideos(query, pageable));
    }
    
    @GetMapping("/compatible")
    @Operation(summary = "Find videos compatible with a vehicle")
    public ResponseEntity<Page<InstructionalVideo>> getCompatibleVideos(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(videoService.getCompatibleVideos(make, model, year, pageable));
    }
    
    @GetMapping("/installation")
    @Operation(summary = "Find installation videos for a specific part and vehicle")
    public ResponseEntity<List<InstructionalVideo>> findInstallationVideos(
            @RequestParam String partType,
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(videoService.findInstallationVideos(partType, make, model, year, limit));
    }
    
    @GetMapping("/popular")
    @Operation(summary = "Get most viewed videos")
    public ResponseEntity<List<InstructionalVideo>> getPopularVideos() {
        return ResponseEntity.ok(videoService.getPopularVideos());
    }
    
    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated videos")
    public ResponseEntity<List<InstructionalVideo>> getTopRatedVideos() {
        return ResponseEntity.ok(videoService.getTopRatedVideos());
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get all video categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(videoService.getAllCategories());
    }
    
    @PostMapping("/{id}/rate")
    @Operation(summary = "Rate a video")
    public ResponseEntity<InstructionalVideo> rateVideo(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam int rating) {
        return ResponseEntity.ok(videoService.rateVideo(id, userId, rating));
    }
    
    @PostMapping("/{id}/link-part/{partId}")
    @Operation(summary = "Link video to a part suggestion")
    public ResponseEntity<InstructionalVideo> linkToPart(
            @PathVariable Long id,
            @PathVariable Long partId) {
        return ResponseEntity.ok(videoService.linkVideoToPart(id, partId));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a video")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable Long id,
            @RequestParam Long userId) {
        videoService.deleteVideo(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    // Request DTO
    public static class CreateVideoRequest {
        public Long submitterId;
        public String title;
        public String videoUrl;
        public String description;
        public String category;
        public String partType;
        public String vehicleMake;
        public String vehicleModel;
        public Integer yearStart;
        public Integer yearEnd;
        public InstructionalVideo.DifficultyLevel difficultyLevel;
    }
}
