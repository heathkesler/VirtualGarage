package com.virtualgarage.service;

import com.virtualgarage.entity.*;
import com.virtualgarage.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for forum functionality including threads and posts.
 */
@Service
@Transactional
public class ForumService {
    
    private static final Logger logger = LoggerFactory.getLogger(ForumService.class);
    
    private final ForumCategoryRepository categoryRepository;
    private final ForumThreadRepository threadRepository;
    private final ForumPostRepository postRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    
    public ForumService(ForumCategoryRepository categoryRepository,
                        ForumThreadRepository threadRepository,
                        ForumPostRepository postRepository,
                        UserRepository userRepository,
                        VehicleRepository vehicleRepository) {
        this.categoryRepository = categoryRepository;
        this.threadRepository = threadRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }
    
    // ==================== Categories ====================
    
    public List<ForumCategory> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }
    
    public ForumCategory getCategoryBySlug(String slug) {
        return categoryRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + slug));
    }
    
    // ==================== Threads ====================
    
    public ForumThread createThread(Long authorId, Long categoryId, String title, String content,
                                     ForumThread.ThreadType type, Long vehicleId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));
        
        ForumCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        ForumThread thread = new ForumThread(title, content, category, author);
        thread.setThreadType(type);
        thread.setLastActivityAt(LocalDateTime.now());
        
        if (vehicleId != null) {
            Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
            thread.setRelatedVehicle(vehicle);
        }
        
        thread = threadRepository.save(thread);
        category.incrementThreadCount();
        categoryRepository.save(category);
        
        logger.info("Created thread '{}' by user {} in category {}", title, authorId, categoryId);
        return thread;
    }
    
    @Transactional(readOnly = true)
    public ForumThread getThread(Long threadId) {
        return threadRepository.findByIdAndIsActiveTrue(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found: " + threadId));
    }
    
    public void incrementThreadViews(Long threadId) {
        threadRepository.incrementViewCount(threadId);
    }
    
    @Transactional(readOnly = true)
    public Page<ForumThread> getThreadsByCategory(String categorySlug, Pageable pageable) {
        ForumCategory category = getCategoryBySlug(categorySlug);
        return threadRepository.findByCategoryAndIsActiveTrueOrderByIsPinnedDescLastActivityAtDesc(category, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ForumThread> getRecentThreads(Pageable pageable) {
        return threadRepository.findByIsActiveTrueOrderByLastActivityAtDesc(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ForumThread> searchThreads(String query, Pageable pageable) {
        return threadRepository.searchThreads(query, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ForumThread> getQuestions(Pageable pageable) {
        return threadRepository.findByThreadType(ForumThread.ThreadType.QUESTION, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<ForumThread> getUnansweredQuestions(Pageable pageable) {
        return threadRepository.findUnansweredQuestions(pageable);
    }
    
    public ForumThread togglePinned(Long threadId, Long moderatorId) {
        ForumThread thread = getThread(threadId);
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!moderator.getIsModerator()) {
            throw new IllegalStateException("Only moderators can pin threads");
        }
        
        thread.setIsPinned(!thread.getIsPinned());
        return threadRepository.save(thread);
    }
    
    public ForumThread toggleLocked(Long threadId, Long moderatorId) {
        ForumThread thread = getThread(threadId);
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!moderator.getIsModerator()) {
            throw new IllegalStateException("Only moderators can lock threads");
        }
        
        thread.setIsLocked(!thread.getIsLocked());
        return threadRepository.save(thread);
    }
    
    // ==================== Posts ====================
    
    public ForumPost createPost(Long authorId, Long threadId, String content, Long replyToId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));
        
        ForumThread thread = getThread(threadId);
        
        if (thread.getIsLocked()) {
            throw new IllegalStateException("Cannot post in a locked thread");
        }
        
        ForumPost post = new ForumPost(content, thread, author);
        
        if (replyToId != null) {
            ForumPost replyTo = postRepository.findByIdAndIsActiveTrue(replyToId).orElse(null);
            post.setReplyTo(replyTo);
        }
        
        post = postRepository.save(post);
        
        // Update thread
        thread.addPost(post);
        thread.setLastActivityAt(LocalDateTime.now());
        thread.setLastPostAuthor(author);
        threadRepository.save(thread);
        
        // Update category post count
        thread.getCategory().incrementPostCount();
        categoryRepository.save(thread.getCategory());
        
        // Give author reputation
        author.addReputation(1);
        userRepository.save(author);
        
        logger.info("Created post in thread {} by user {}", threadId, authorId);
        return post;
    }
    
    @Transactional(readOnly = true)
    public Page<ForumPost> getPostsByThread(Long threadId, Pageable pageable) {
        ForumThread thread = getThread(threadId);
        return postRepository.findByThreadAndIsActiveTrueOrderByCreatedAtAsc(thread, pageable);
    }
    
    public ForumPost upvotePost(Long postId, Long userId) {
        ForumPost post = postRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        post.upvote();
        post.getAuthor().addReputation(2);
        userRepository.save(post.getAuthor());
        
        return postRepository.save(post);
    }
    
    public ForumPost downvotePost(Long postId, Long userId) {
        ForumPost post = postRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        post.downvote();
        return postRepository.save(post);
    }
    
    public ForumPost markAsAcceptedAnswer(Long postId, Long threadOwnerId) {
        ForumPost post = postRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        ForumThread thread = post.getThread();
        
        // Only thread author can mark accepted answer
        if (!thread.getAuthor().getId().equals(threadOwnerId)) {
            throw new IllegalStateException("Only the thread author can mark an accepted answer");
        }
        
        post.setIsAcceptedAnswer(true);
        thread.setIsSolved(true);
        
        // Give reputation bonus
        post.getAuthor().addReputation(15);
        userRepository.save(post.getAuthor());
        
        threadRepository.save(thread);
        return postRepository.save(post);
    }
    
    public ForumPost editPost(Long postId, Long authorId, String newContent) {
        ForumPost post = postRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new IllegalStateException("Only the author can edit this post");
        }
        
        post.setContent(newContent);
        post.setIsEdited(true);
        return postRepository.save(post);
    }
    
    public void deletePost(Long postId, Long userId) {
        ForumPost post = postRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!post.getAuthor().getId().equals(userId) && !user.getIsModerator()) {
            throw new IllegalStateException("Cannot delete this post");
        }
        
        post.setIsActive(false);
        postRepository.save(post);
    }
}
