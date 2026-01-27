package com.virtualgarage.repository;

import com.virtualgarage.entity.InstructionalVideo;
import com.virtualgarage.entity.PartSuggestion;
import com.virtualgarage.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructionalVideoRepository extends JpaRepository<InstructionalVideo, Long> {
    
    Optional<InstructionalVideo> findByIdAndIsActiveTrue(Long id);
    
    Page<InstructionalVideo> findBySubmittedByAndIsActiveTrueOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<InstructionalVideo> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    Page<InstructionalVideo> findByCategoryAndIsActiveTrueOrderByAverageRatingDesc(String category, Pageable pageable);
    
    List<InstructionalVideo> findByPartSuggestionAndIsActiveTrue(PartSuggestion partSuggestion);
    
    @Query("SELECT v FROM InstructionalVideo v WHERE v.isActive = true AND " +
           "(v.vehicleMake IS NULL OR LOWER(v.vehicleMake) = LOWER(:make)) AND " +
           "(v.vehicleModel IS NULL OR LOWER(v.vehicleModel) = LOWER(:model)) AND " +
           "(v.yearStart IS NULL OR v.yearStart <= :year) AND " +
           "(v.yearEnd IS NULL OR v.yearEnd >= :year) " +
           "ORDER BY v.averageRating DESC, v.viewCount DESC")
    Page<InstructionalVideo> findCompatibleVideos(
            @Param("make") String make, 
            @Param("model") String model, 
            @Param("year") Integer year, 
            Pageable pageable);
    
    @Query("SELECT v FROM InstructionalVideo v WHERE v.isActive = true AND " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.partType) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY v.averageRating DESC, v.viewCount DESC")
    Page<InstructionalVideo> searchVideos(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT v FROM InstructionalVideo v WHERE v.isActive = true AND " +
           "(LOWER(v.partType) LIKE LOWER(CONCAT('%', :partType, '%')) OR " +
           "LOWER(v.title) LIKE LOWER(CONCAT('%', :partType, '%')) OR " +
           "LOWER(v.category) LIKE LOWER(CONCAT('%', :partType, '%'))) AND " +
           "(v.vehicleMake IS NULL OR LOWER(v.vehicleMake) = LOWER(:make)) AND " +
           "(v.vehicleModel IS NULL OR LOWER(v.vehicleModel) = LOWER(:model)) AND " +
           "(v.yearStart IS NULL OR v.yearStart <= :year) AND " +
           "(v.yearEnd IS NULL OR v.yearEnd >= :year) " +
           "ORDER BY " +
           "CASE WHEN LOWER(v.vehicleMake) = LOWER(:make) AND LOWER(v.vehicleModel) = LOWER(:model) THEN 0 " +
           "     WHEN LOWER(v.vehicleMake) = LOWER(:make) THEN 1 " +
           "     ELSE 2 END, " +
           "v.averageRating DESC, v.viewCount DESC")
    List<InstructionalVideo> findVideosForPartAndVehicle(
            @Param("partType") String partType,
            @Param("make") String make, 
            @Param("model") String model, 
            @Param("year") Integer year);
    
    @Query("SELECT DISTINCT v.category FROM InstructionalVideo v WHERE v.isActive = true ORDER BY v.category")
    List<String> findAllCategories();
    
    @Modifying
    @Query("UPDATE InstructionalVideo v SET v.viewCount = v.viewCount + 1 WHERE v.id = :id")
    void incrementViewCount(@Param("id") Long id);
    
    List<InstructionalVideo> findTop10ByIsActiveTrueOrderByViewCountDesc();
    
    List<InstructionalVideo> findTop10ByIsActiveTrueOrderByAverageRatingDesc();
}
