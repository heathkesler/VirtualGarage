package com.virtualgarage.controller;

import com.virtualgarage.entity.ForumCategory;
import com.virtualgarage.entity.ForumPost;
import com.virtualgarage.entity.ForumThread;
import com.virtualgarage.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for forum functionality.
 */
@RestController
@RequestMapping("/forum")
@Tag(name = "Forum", description = "Forum discussion endpoints")
public class ForumController {
    
    private final ForumService forumService;
    
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }
    
    // ==================== Categories ====================
    
    @GetMapping("/categories")
    @Operation(summary = "Get all forum categories")
    public ResponseEntity<List<ForumCategory>> getCategories() {
        return ResponseEntity.ok(forumService.getAllCategories());
    }
    
    @GetMapping("/categories/{slug}")
    @Operation(summary = "Get category by slug")
    public ResponseEntity<ForumCategory> getCategory(@PathVariable String slug) {
        return ResponseEntity.ok(forumService.getCategoryBySlug(slug));
    }
    
    // ==================== Threads ====================
    
    @PostMapping("/threads")
    @Operation(summary = "Create a new thread")
    public ResponseEntity<ForumThread> createThread(@RequestBody CreateThreadRequest request) {
        ForumThread thread = forumService.createThread(
                request.authorId,
                request.categoryId,
                request.title,
                request.content,
                request.threadType != null ? request.threadType : ForumThread.ThreadType.DISCUSSION,
                request.vehicleId
        );
        return ResponseEntity.ok(thread);
    }
    
    @GetMapping("/threads/{id}")
    @Operation(summary = "Get thread by ID")
    public ResponseEntity<ForumThread> getThread(@PathVariable Long id) {
        forumService.incrementThreadViews(id);
        return ResponseEntity.ok(forumService.getThread(id));
    }
    
    @GetMapping("/threads")
    @Operation(summary = "Get recent threads")
    public ResponseEntity<Page<ForumThread>> getRecentThreads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(forumService.getRecentThreads(pageable));
    }
    
    @GetMapping("/categories/{slug}/threads")
    @Operation(summary = "Get threads in a category")
    public ResponseEntity<Page<ForumThread>> getThreadsByCategory(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(forumService.getThreadsByCategory(slug, pageable));
    }
    
    @GetMapping("/threads/search")
    @Operation(summary = "Search threads")
    public ResponseEntity<Page<ForumThread>> searchThreads(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(forumService.searchThreads(query, pageable));
    }
    
    @GetMapping("/questions")
    @Operation(summary = "Get Q&A threads")
    public ResponseEntity<Page<ForumThread>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(forumService.getQuestions(pageable));
    }
    
    @GetMapping("/questions/unanswered")
    @Operation(summary = "Get unanswered questions")
    public ResponseEntity<Page<ForumThread>> getUnansweredQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(forumService.getUnansweredQuestions(pageable));
    }
    
    @PostMapping("/threads/{id}/pin")
    @Operation(summary = "Toggle thread pinned status (moderator)")
    public ResponseEntity<ForumThread> togglePinned(
            @PathVariable Long id,
            @RequestParam Long moderatorId) {
        return ResponseEntity.ok(forumService.togglePinned(id, moderatorId));
    }
    
    @PostMapping("/threads/{id}/lock")
    @Operation(summary = "Toggle thread locked status (moderator)")
    public ResponseEntity<ForumThread> toggleLocked(
            @PathVariable Long id,
            @RequestParam Long moderatorId) {
        return ResponseEntity.ok(forumService.toggleLocked(id, moderatorId));
    }
    
    // ==================== Posts ====================
    
    @PostMapping("/posts")
    @Operation(summary = "Create a new post/reply")
    public ResponseEntity<ForumPost> createPost(@RequestBody CreatePostRequest request) {
        ForumPost post = forumService.createPost(
                request.authorId,
                request.threadId,
                request.content,
                request.replyToId
        );
        return ResponseEntity.ok(post);
    }
    
    @GetMapping("/threads/{threadId}/posts")
    @Operation(summary = "Get posts in a thread")
    public ResponseEntity<Page<ForumPost>> getPostsByThread(
            @PathVariable Long threadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(forumService.getPostsByThread(threadId, pageable));
    }
    
    @PostMapping("/posts/{id}/upvote")
    @Operation(summary = "Upvote a post")
    public ResponseEntity<ForumPost> upvotePost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(forumService.upvotePost(id, userId));
    }
    
    @PostMapping("/posts/{id}/downvote")
    @Operation(summary = "Downvote a post")
    public ResponseEntity<ForumPost> downvotePost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(forumService.downvotePost(id, userId));
    }
    
    @PostMapping("/posts/{id}/accept")
    @Operation(summary = "Mark post as accepted answer")
    public ResponseEntity<ForumPost> acceptAnswer(
            @PathVariable Long id,
            @RequestParam Long threadOwnerId) {
        return ResponseEntity.ok(forumService.markAsAcceptedAnswer(id, threadOwnerId));
    }
    
    @PutMapping("/posts/{id}")
    @Operation(summary = "Edit a post")
    public ResponseEntity<ForumPost> editPost(
            @PathVariable Long id,
            @RequestParam Long authorId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(forumService.editPost(id, authorId, body.get("content")));
    }
    
    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete a post")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestParam Long userId) {
        forumService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    // Request DTOs
    public static class CreateThreadRequest {
        public Long authorId;
        public Long categoryId;
        public String title;
        public String content;
        public ForumThread.ThreadType threadType;
        public Long vehicleId;
    }
    
    public static class CreatePostRequest {
        public Long authorId;
        public Long threadId;
        public String content;
        public Long replyToId;
    }
}
