package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Forum post entity representing a reply in a thread.
 */
@Entity
@Table(name = "forum_posts", indexes = {
    @Index(name = "idx_forum_post_thread", columnList = "thread_id"),
    @Index(name = "idx_forum_post_author", columnList = "author_id"),
    @Index(name = "idx_forum_post_created", columnList = "created_at"),
    @Index(name = "idx_forum_post_accepted", columnList = "is_accepted_answer")
})
public class ForumPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Post content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private ForumThread thread;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private ForumPost replyTo;
    
    @Column(name = "upvote_count", nullable = false)
    private Integer upvoteCount = 0;
    
    @Column(name = "downvote_count", nullable = false)
    private Integer downvoteCount = 0;
    
    @Column(name = "is_accepted_answer", nullable = false)
    private Boolean isAcceptedAnswer = false;
    
    @Column(name = "is_edited", nullable = false)
    private Boolean isEdited = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public ForumPost() {}
    
    public ForumPost(String content, ForumThread thread, User author) {
        this.content = content;
        this.thread = thread;
        this.author = author;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.isEdited = true;
    }
    
    public ForumThread getThread() {
        return thread;
    }
    
    public void setThread(ForumThread thread) {
        this.thread = thread;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public ForumPost getReplyTo() {
        return replyTo;
    }
    
    public void setReplyTo(ForumPost replyTo) {
        this.replyTo = replyTo;
    }
    
    public Integer getUpvoteCount() {
        return upvoteCount;
    }
    
    public void setUpvoteCount(Integer upvoteCount) {
        this.upvoteCount = upvoteCount;
    }
    
    public Integer getDownvoteCount() {
        return downvoteCount;
    }
    
    public void setDownvoteCount(Integer downvoteCount) {
        this.downvoteCount = downvoteCount;
    }
    
    public Boolean getIsAcceptedAnswer() {
        return isAcceptedAnswer;
    }
    
    public void setIsAcceptedAnswer(Boolean isAcceptedAnswer) {
        this.isAcceptedAnswer = isAcceptedAnswer;
    }
    
    public Boolean getIsEdited() {
        return isEdited;
    }
    
    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    // Helper methods
    public void upvote() {
        this.upvoteCount++;
    }
    
    public void downvote() {
        this.downvoteCount++;
    }
    
    public int getScore() {
        return upvoteCount - downvoteCount;
    }
}
