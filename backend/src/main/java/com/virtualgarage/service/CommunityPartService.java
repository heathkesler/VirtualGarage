package com.virtualgarage.service;

import com.virtualgarage.entity.PartSuggestion;
import com.virtualgarage.entity.User;
import com.virtualgarage.repository.PartSuggestionRepository;
import com.virtualgarage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for community-submitted part suggestions.
 */
@Service
@Transactional
public class CommunityPartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommunityPartService.class);
    
    private final PartSuggestionRepository partSuggestionRepository;
    private final UserRepository userRepository;
    
    public CommunityPartService(PartSuggestionRepository partSuggestionRepository,
                                UserRepository userRepository) {
        this.partSuggestionRepository = partSuggestionRepository;
        this.userRepository = userRepository;
    }
    
    public PartSuggestion createPartSuggestion(Long authorId, String partName, String partNumber,
                                                String brand, String description, String partCategory,
                                                String vehicleMake, String vehicleModel,
                                                Integer yearStart, Integer yearEnd,
                                                String engineType, BigDecimal pricePaid,
                                                String purchaseUrl, String whereToBuy,
                                                PartSuggestion.InstallationDifficulty difficulty,
                                                Integer installationTimeMinutes,
                                                String installationNotes, String toolsRequired) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));
        
        PartSuggestion suggestion = new PartSuggestion(author, partName, brand, partCategory);
        suggestion.setPartNumber(partNumber);
        suggestion.setDescription(description);
        suggestion.setVehicleMake(vehicleMake);
        suggestion.setVehicleModel(vehicleModel);
        suggestion.setYearStart(yearStart);
        suggestion.setYearEnd(yearEnd);
        suggestion.setEngineType(engineType);
        suggestion.setPricePaid(pricePaid);
        suggestion.setPurchaseUrl(purchaseUrl);
        suggestion.setWhereToBuy(whereToBuy);
        suggestion.setInstallationDifficulty(difficulty);
        suggestion.setInstallationTimeMinutes(installationTimeMinutes);
        suggestion.setInstallationNotes(installationNotes);
        suggestion.setToolsRequired(toolsRequired);
        
        suggestion = partSuggestionRepository.save(suggestion);
        
        // Give author reputation
        author.addReputation(10);
        userRepository.save(author);
        
        logger.info("Created part suggestion '{}' by user {}", partName, authorId);
        return suggestion;
    }
    
    @Transactional(readOnly = true)
    public PartSuggestion getPartSuggestion(Long id) {
        return partSuggestionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Part suggestion not found: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<PartSuggestion> getRecentSuggestions(Pageable pageable) {
        return partSuggestionRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<PartSuggestion> getSuggestionsByCategory(String category, Pageable pageable) {
        return partSuggestionRepository.findByCategory(category, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<PartSuggestion> searchSuggestions(String query, Pageable pageable) {
        return partSuggestionRepository.searchParts(query, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<PartSuggestion> getCompatibleParts(String make, String model, Integer year, Pageable pageable) {
        return partSuggestionRepository.findCompatibleParts(make, model, year, pageable);
    }
    
    /**
     * Search for community-suggested parts matching a query for a specific vehicle.
     */
    @Transactional(readOnly = true)
    public List<PartSuggestion> searchPartsForVehicle(String query, String make, String model, Integer year) {
        return partSuggestionRepository.searchPartsForVehicle(query, make, model, year);
    }
    
    @Transactional(readOnly = true)
    public List<PartSuggestion> getTopRatedSuggestions() {
        return partSuggestionRepository.findTop10ByIsActiveTrueOrderByAverageRatingDesc();
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return partSuggestionRepository.findAllCategories();
    }
    
    public PartSuggestion upvote(Long suggestionId, Long userId) {
        PartSuggestion suggestion = getPartSuggestion(suggestionId);
        suggestion.upvote();
        
        // Give author reputation
        suggestion.getAuthor().addReputation(1);
        userRepository.save(suggestion.getAuthor());
        
        return partSuggestionRepository.save(suggestion);
    }
    
    public PartSuggestion downvote(Long suggestionId, Long userId) {
        PartSuggestion suggestion = getPartSuggestion(suggestionId);
        suggestion.downvote();
        return partSuggestionRepository.save(suggestion);
    }
    
    public PartSuggestion rateSuggestion(Long suggestionId, Long userId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        PartSuggestion suggestion = getPartSuggestion(suggestionId);
        
        // Calculate new average
        BigDecimal currentTotal = suggestion.getAverageRating()
                .multiply(BigDecimal.valueOf(suggestion.getRatingCount()));
        int newCount = suggestion.getRatingCount() + 1;
        BigDecimal newAverage = currentTotal
                .add(BigDecimal.valueOf(rating))
                .divide(BigDecimal.valueOf(newCount), 2, java.math.RoundingMode.HALF_UP);
        
        suggestion.setAverageRating(newAverage);
        suggestion.setRatingCount(newCount);
        
        return partSuggestionRepository.save(suggestion);
    }
    
    public PartSuggestion verifySuggestion(Long suggestionId, Long moderatorId) {
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!moderator.getIsModerator()) {
            throw new IllegalStateException("Only moderators can verify suggestions");
        }
        
        PartSuggestion suggestion = getPartSuggestion(suggestionId);
        suggestion.setIsVerified(true);
        
        // Bonus reputation for verified suggestion
        suggestion.getAuthor().addReputation(25);
        userRepository.save(suggestion.getAuthor());
        
        return partSuggestionRepository.save(suggestion);
    }
    
    public void deleteSuggestion(Long suggestionId, Long userId) {
        PartSuggestion suggestion = getPartSuggestion(suggestionId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!suggestion.getAuthor().getId().equals(userId) && !user.getIsModerator()) {
            throw new IllegalStateException("Cannot delete this suggestion");
        }
        
        suggestion.setIsActive(false);
        partSuggestionRepository.save(suggestion);
    }
}
