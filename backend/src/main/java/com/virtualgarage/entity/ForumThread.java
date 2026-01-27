package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Forum thread entity representing a discussion topic.
 */
@Entity
@Table(name = "forum_threads", indexes = {
    @Index(name = "idx_forum_thread_category", columnList = "category_id"),
    @Index(name = "idx_forum_thread_author", columnList = "author_id"),
    @Index(name = "idx_forum_thread_created", columnList = "created_at"),
    @Index(name = "idx_forum_thread_last_activity", columnList = "last_activity_at"),
    @Index(name = "idx_forum_thread_pinned", columnList = "is_pinned")
})
public class ForumThread {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Thread title is required")
    @Size(min = 5, max = 255)
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ForumCategory category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle relatedVehicle;
    
    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<ForumPost> posts = new ArrayList<>();
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;
    
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;
    
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;
    
    @Column(name = "is_solved", nullable = false)
    private Boolean isSolved = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "thread_type", nullable = false, length = 30)
    private ThreadType threadType = ThreadType.DISCUSSION;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_post_author_id")
    private User lastPostAuthor;
    
    public enum ThreadType {
        DISCUSSION,     // General discussion
        QUESTION,       // Q&A format
        SHOWCASE,       // Show off your vehicle
        TUTORIAL,       // How-to guides
        PARTS_REQUEST   // Looking for parts
    }
    
    // Constructors
    public ForumThread() {}
    
    public ForumThread(String title, String content, ForumCategory category, User author) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.author = author;
        this.lastActivityAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public ForumCategory getCategory() {
        return category;
    }
    
    public void setCategory(ForumCategory category) {
        this.category = category;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public Vehicle getRelatedVehicle() {
        return relatedVehicle;
    }
    
    public void setRelatedVehicle(Vehicle relatedVehicle) {
        this.relatedVehicle = relatedVehicle;
    }
    
    public List<ForumPost> getPosts() {
        return posts;
    }
    
    public void setPosts(List<ForumPost> posts) {
        this.posts = posts;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getReplyCount() {
        return replyCount;
    }
    
    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
    
    public Boolean getIsPinned() {
        return isPinned;
    }
    
    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }
    
    public Boolean getIsLocked() {
        return isLocked;
    }
    
    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }
    
    public Boolean getIsSolved() {
        return isSolved;
    }
    
    public void setIsSolved(Boolean isSolved) {
        this.isSolved = isSolved;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public ThreadType getThreadType() {
        return threadType;
    }
    
    public void setThreadType(ThreadType threadType) {
        this.threadType = threadType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }
    
    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
    
    public User getLastPostAuthor() {
        return lastPostAuthor;
    }
    
    public void setLastPostAuthor(User lastPostAuthor) {
        this.lastPostAuthor = lastPostAuthor;
    }
    
    // Helper methods
    public void addPost(ForumPost post) {
        posts.add(post);
        post.setThread(this);
        this.replyCount++;
        this.lastActivityAt = LocalDateTime.now();
        this.lastPostAuthor = post.getAuthor();
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
}
