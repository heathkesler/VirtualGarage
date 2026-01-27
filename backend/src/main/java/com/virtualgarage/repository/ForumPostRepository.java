package com.virtualgarage.repository;

import com.virtualgarage.entity.ForumPost;
import com.virtualgarage.entity.ForumThread;
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
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    
    Optional<ForumPost> findByIdAndIsActiveTrue(Long id);
    
    Page<ForumPost> findByThreadAndIsActiveTrueOrderByCreatedAtAsc(ForumThread thread, Pageable pageable);
    
    Page<ForumPost> findByAuthorAndIsActiveTrueOrderByCreatedAtDesc(User author, Pageable pageable);
    
    @Query("SELECT p FROM ForumPost p WHERE p.thread = :thread AND p.isActive = true " +
           "ORDER BY p.isAcceptedAnswer DESC, p.upvoteCount - p.downvoteCount DESC, p.createdAt ASC")
    Page<ForumPost> findByThreadOrderByScore(@Param("thread") ForumThread thread, Pageable pageable);
    
    @Query("SELECT p FROM ForumPost p WHERE p.isActive = true AND " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY p.createdAt DESC")
    Page<ForumPost> searchPosts(@Param("query") String query, Pageable pageable);
    
    Long countByThreadAndIsActiveTrue(ForumThread thread);
    
    Long countByAuthorAndIsActiveTrue(User author);
    
    List<ForumPost> findTop10ByIsActiveTrueOrderByUpvoteCountDesc();
}
