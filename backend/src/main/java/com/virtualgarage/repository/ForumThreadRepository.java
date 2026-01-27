package com.virtualgarage.repository;

import com.virtualgarage.entity.ForumCategory;
import com.virtualgarage.entity.ForumThread;
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
public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    
    Optional<ForumThread> findByIdAndIsActiveTrue(Long id);
    
    Page<ForumThread> findByCategoryAndIsActiveTrueOrderByIsPinnedDescLastActivityAtDesc(
            ForumCategory category, Pageable pageable);
    
    Page<ForumThread> findByAuthorAndIsActiveTrueOrderByCreatedAtDesc(User author, Pageable pageable);
    
    Page<ForumThread> findByIsActiveTrueOrderByLastActivityAtDesc(Pageable pageable);
    
    @Query("SELECT t FROM ForumThread t WHERE t.isActive = true AND t.threadType = :type " +
           "ORDER BY t.isPinned DESC, t.lastActivityAt DESC")
    Page<ForumThread> findByThreadType(@Param("type") ForumThread.ThreadType type, Pageable pageable);
    
    @Query("SELECT t FROM ForumThread t WHERE t.isActive = true AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY t.lastActivityAt DESC")
    Page<ForumThread> searchThreads(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT t FROM ForumThread t WHERE t.isActive = true AND t.relatedVehicle.id = :vehicleId " +
           "ORDER BY t.lastActivityAt DESC")
    Page<ForumThread> findByVehicle(@Param("vehicleId") Long vehicleId, Pageable pageable);
    
    @Query("SELECT t FROM ForumThread t WHERE t.isActive = true AND t.threadType = 'QUESTION' " +
           "AND t.isSolved = false ORDER BY t.createdAt DESC")
    Page<ForumThread> findUnansweredQuestions(Pageable pageable);
    
    @Modifying
    @Query("UPDATE ForumThread t SET t.viewCount = t.viewCount + 1 WHERE t.id = :id")
    void incrementViewCount(@Param("id") Long id);
    
    List<ForumThread> findTop5ByIsActiveTrueOrderByViewCountDesc();
    
    Long countByCategoryAndIsActiveTrue(ForumCategory category);
}
