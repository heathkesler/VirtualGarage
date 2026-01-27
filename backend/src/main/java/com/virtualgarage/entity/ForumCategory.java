package com.virtualgarage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Forum category entity for organizing discussions.
 */
@Entity
@Table(name = "forum_categories", indexes = {
    @Index(name = "idx_forum_category_slug", columnList = "slug", unique = true),
    @Index(name = "idx_forum_category_order", columnList = "display_order")
})
public class ForumCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Slug is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "icon_name", length = 50)
    private String iconName;
    
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumThread> threads = new ArrayList<>();
    
    @Column(name = "thread_count", nullable = false)
    private Integer threadCount = 0;
    
    @Column(name = "post_count", nullable = false)
    private Integer postCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public ForumCategory() {}
    
    public ForumCategory(String name, String slug, String description) {
        this.name = name;
        this.slug = slug;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIconName() {
        return iconName;
    }
    
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<ForumThread> getThreads() {
        return threads;
    }
    
    public void setThreads(List<ForumThread> threads) {
        this.threads = threads;
    }
    
    public Integer getThreadCount() {
        return threadCount;
    }
    
    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }
    
    public Integer getPostCount() {
        return postCount;
    }
    
    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void incrementThreadCount() {
        this.threadCount++;
    }
    
    public void incrementPostCount() {
        this.postCount++;
    }
}
