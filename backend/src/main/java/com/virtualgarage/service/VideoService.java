package com.virtualgarage.service;

import com.virtualgarage.entity.InstructionalVideo;
import com.virtualgarage.entity.PartSuggestion;
import com.virtualgarage.entity.User;
import com.virtualgarage.repository.InstructionalVideoRepository;
import com.virtualgarage.repository.PartSuggestionRepository;
import com.virtualgarage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing instructional videos.
 */
@Service
@Transactional
public class VideoService {
    
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);
    
    private final InstructionalVideoRepository videoRepository;
    private final UserRepository userRepository;
    private final PartSuggestionRepository partSuggestionRepository;
    
    public VideoService(InstructionalVideoRepository videoRepository,
                        UserRepository userRepository,
                        PartSuggestionRepository partSuggestionRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.partSuggestionRepository = partSuggestionRepository;
    }
    
    public InstructionalVideo createVideo(Long submitterId, String title, String videoUrl, 
                                          String description, String category, String partType,
                                          String vehicleMake, String vehicleModel,
                                          Integer yearStart, Integer yearEnd,
                                          InstructionalVideo.DifficultyLevel difficulty) {
        User submitter = null;
        if (submitterId != null) {
            submitter = userRepository.findById(submitterId).orElse(null);
        }
        
        InstructionalVideo video = new InstructionalVideo(title, videoUrl, category);
        video.setDescription(description);
        video.setPartType(partType);
        video.setSubmittedBy(submitter);
        video.setVehicleMake(vehicleMake);
        video.setVehicleModel(vehicleModel);
        video.setYearStart(yearStart);
        video.setYearEnd(yearEnd);
        video.setDifficultyLevel(difficulty);
        
        video = videoRepository.save(video);
        
        if (submitter != null) {
            submitter.addReputation(5);
            userRepository.save(submitter);
        }
        
        logger.info("Created video '{}' for {}", title, partType);
        return video;
    }
    
    public InstructionalVideo linkVideoToPart(Long videoId, Long partSuggestionId) {
        InstructionalVideo video = videoRepository.findByIdAndIsActiveTrue(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + videoId));
        
        PartSuggestion part = partSuggestionRepository.findByIdAndIsActiveTrue(partSuggestionId)
                .orElseThrow(() -> new IllegalArgumentException("Part suggestion not found: " + partSuggestionId));
        
        video.setPartSuggestion(part);
        return videoRepository.save(video);
    }
    
    @Transactional(readOnly = true)
    public InstructionalVideo getVideo(Long videoId) {
        return videoRepository.findByIdAndIsActiveTrue(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + videoId));
    }
    
    public void incrementViewCount(Long videoId) {
        videoRepository.incrementViewCount(videoId);
    }
    
    @Transactional(readOnly = true)
    public Page<InstructionalVideo> getRecentVideos(Pageable pageable) {
        return videoRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<InstructionalVideo> getVideosByCategory(String category, Pageable pageable) {
        return videoRepository.findByCategoryAndIsActiveTrueOrderByAverageRatingDesc(category, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<InstructionalVideo> searchVideos(String query, Pageable pageable) {
        return videoRepository.searchVideos(query, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<InstructionalVideo> getVideosForPart(PartSuggestion part) {
        return videoRepository.findByPartSuggestionAndIsActiveTrue(part);
    }
    
    /**
     * Find installation videos for a specific part type and vehicle.
     * Results are prioritized: exact vehicle match > same make > general videos.
     */
    @Transactional(readOnly = true)
    public List<InstructionalVideo> findInstallationVideos(String partType, String make, 
                                                           String model, Integer year, int limit) {
        List<InstructionalVideo> videos = videoRepository.findVideosForPartAndVehicle(
                partType, make, model, year);
        
        if (videos.size() > limit) {
            return videos.subList(0, limit);
        }
        return videos;
    }
    
    @Transactional(readOnly = true)
    public Page<InstructionalVideo> getCompatibleVideos(String make, String model, 
                                                        Integer year, Pageable pageable) {
        return videoRepository.findCompatibleVideos(make, model, year, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<InstructionalVideo> getPopularVideos() {
        return videoRepository.findTop10ByIsActiveTrueOrderByViewCountDesc();
    }
    
    @Transactional(readOnly = true)
    public List<InstructionalVideo> getTopRatedVideos() {
        return videoRepository.findTop10ByIsActiveTrueOrderByAverageRatingDesc();
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return videoRepository.findAllCategories();
    }
    
    public InstructionalVideo rateVideo(Long videoId, Long userId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        InstructionalVideo video = getVideo(videoId);
        
        // Calculate new average (simplified - in production, track individual ratings)
        java.math.BigDecimal currentTotal = video.getAverageRating()
                .multiply(java.math.BigDecimal.valueOf(video.getRatingCount()));
        int newCount = video.getRatingCount() + 1;
        java.math.BigDecimal newAverage = currentTotal
                .add(java.math.BigDecimal.valueOf(rating))
                .divide(java.math.BigDecimal.valueOf(newCount), 2, java.math.RoundingMode.HALF_UP);
        
        video.setAverageRating(newAverage);
        video.setRatingCount(newCount);
        
        return videoRepository.save(video);
    }
    
    public void deleteVideo(Long videoId, Long userId) {
        InstructionalVideo video = getVideo(videoId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        boolean isOwner = video.getSubmittedBy() != null && 
                          video.getSubmittedBy().getId().equals(userId);
        
        if (!isOwner && !user.getIsModerator()) {
            throw new IllegalStateException("Cannot delete this video");
        }
        
        video.setIsActive(false);
        videoRepository.save(video);
    }
}
