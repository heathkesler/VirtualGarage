package com.virtualgarage.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "builds",
       indexes = {
           @Index(name = "idx_build_vehicle", columnList = "vehicle_id"),
           @Index(name = "idx_build_status", columnList = "status"),
           @Index(name = "idx_build_created", columnList = "created_at")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Build {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "total_cost", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.00")
    @Builder.Default
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BuildStatus status = BuildStatus.PLANNING;

    @Column(name = "target_completion_date")
    private LocalDateTime targetCompletionDate;

    @Column(name = "actual_completion_date")
    private LocalDateTime actualCompletionDate;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Component> components = new ArrayList<>();

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BuildPhoto> photos = new ArrayList<>();

    public enum BuildStatus {
        PLANNING, IN_PROGRESS, COMPLETED, ON_HOLD, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Recalculate total cost
        this.totalCost = components.stream()
                .map(component -> component.getPrice().multiply(BigDecimal.valueOf(component.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Helper methods
    public void addComponent(Component component) {
        components.add(component);
        component.setBuild(this);
        recalculateTotalCost();
    }

    public void removeComponent(Component component) {
        components.remove(component);
        component.setBuild(null);
        recalculateTotalCost();
    }

    public void addPhoto(BuildPhoto photo) {
        photos.add(photo);
        photo.setBuild(this);
    }

    public void removePhoto(BuildPhoto photo) {
        photos.remove(photo);
        photo.setBuild(null);
    }

    private void recalculateTotalCost() {
        this.totalCost = components.stream()
                .map(component -> component.getPrice().multiply(BigDecimal.valueOf(component.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Computed properties
    public int getCompletedComponentsCount() {
        return (int) components.stream()
                .filter(c -> c.getStatus() == Component.ComponentStatus.INSTALLED)
                .count();
    }

    public double getCompletionPercentage() {
        if (components.isEmpty()) {
            return 0.0;
        }
        return (double) getCompletedComponentsCount() / components.size() * 100.0;
    }
}