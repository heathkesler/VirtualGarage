package com.virtualgarage.repository;

import com.virtualgarage.entity.PartSuggestion;
import com.virtualgarage.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartSuggestionRepository extends JpaRepository<PartSuggestion, Long> {
    
    Optional<PartSuggestion> findByIdAndIsActiveTrue(Long id);
    
    Page<PartSuggestion> findByAuthorAndIsActiveTrueOrderByCreatedAtDesc(User author, Pageable pageable);
    
    Page<PartSuggestion> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT p FROM PartSuggestion p WHERE p.isActive = true AND p.partCategory = :category " +
           "ORDER BY p.averageRating DESC, p.upvoteCount DESC")
    Page<PartSuggestion> findByCategory(@Param("category") String category, Pageable pageable);
    
    @Query("SELECT p FROM PartSuggestion p WHERE p.isActive = true AND " +
           "(p.vehicleMake IS NULL OR LOWER(p.vehicleMake) = LOWER(:make)) AND " +
           "(p.vehicleModel IS NULL OR LOWER(p.vehicleModel) = LOWER(:model)) AND " +
           "(p.yearStart IS NULL OR p.yearStart <= :year) AND " +
           "(p.yearEnd IS NULL OR p.yearEnd >= :year) " +
           "ORDER BY p.averageRating DESC, p.upvoteCount DESC")
    Page<PartSuggestion> findCompatibleParts(
            @Param("make") String make, 
            @Param("model") String model, 
            @Param("year") Integer year, 
            Pageable pageable);
    
    @Query("SELECT p FROM PartSuggestion p WHERE p.isActive = true AND " +
           "(LOWER(p.partName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.partCategory) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.averageRating DESC")
    Page<PartSuggestion> searchParts(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT p FROM PartSuggestion p WHERE p.isActive = true AND " +
           "(LOWER(p.partName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.partCategory) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(p.vehicleMake IS NULL OR LOWER(p.vehicleMake) = LOWER(:make)) AND " +
           "(p.vehicleModel IS NULL OR LOWER(p.vehicleModel) = LOWER(:model)) AND " +
           "(p.yearStart IS NULL OR p.yearStart <= :year) AND " +
           "(p.yearEnd IS NULL OR p.yearEnd >= :year) " +
           "ORDER BY p.averageRating DESC, p.upvoteCount DESC")
    List<PartSuggestion> searchPartsForVehicle(
            @Param("query") String query,
            @Param("make") String make, 
            @Param("model") String model, 
            @Param("year") Integer year);
    
    @Query("SELECT DISTINCT p.partCategory FROM PartSuggestion p WHERE p.isActive = true ORDER BY p.partCategory")
    List<String> findAllCategories();
    
    List<PartSuggestion> findTop10ByIsActiveTrueOrderByAverageRatingDesc();
}
