package com.virtualgarage.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "components",
       indexes = {
           @Index(name = "idx_component_build", columnList = "build_id"),
           @Index(name = "idx_component_category", columnList = "category"),
           @Index(name = "idx_component_status", columnList = "status"),
           @Index(name = "idx_component_brand_name", columnList = "brand, name")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(name = "brand", nullable = false)
    private String brand;

    @Size(max = 100)
    @Column(name = "part_number")
    private String partNumber;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ComponentCategory category;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @Min(1)
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ComponentStatus status = ComponentStatus.PENDING;

    @Size(max = 500)
    @Column(name = "url")
    private String url;

    @Size(max = 500)
    @Column(name = "image_url")
    private String imageUrl;

    @Size(max = 1000)
    @Column(name = "notes")
    private String notes;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "installed_date")
    private LocalDateTime installedDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "build_id", nullable = false)
    private Build build;

    public enum ComponentCategory {
        ENGINE("Engine"),
        TRANSMISSION("Transmission"),
        SUSPENSION("Suspension"),
        BRAKES("Brakes"),
        WHEELS_TIRES("Wheels & Tires"),
        EXHAUST("Exhaust"),
        INTAKE("Intake"),
        BODY("Body"),
        INTERIOR("Interior"),
        ELECTRICAL("Electrical"),
        COOLING("Cooling"),
        FUEL_SYSTEM("Fuel System"),
        DRIVETRAIN("Drivetrain"),
        OTHER("Other");

        private final String displayName;

        ComponentCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ComponentStatus {
        PENDING("Pending"),
        ORDERED("Ordered"),
        RECEIVED("Received"),
        INSTALLED("Installed");

        private final String displayName;

        ComponentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
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
        
        // Auto-set dates based on status changes
        if (status == ComponentStatus.ORDERED && orderDate == null) {
            orderDate = LocalDateTime.now();
        } else if (status == ComponentStatus.RECEIVED && receivedDate == null) {
            receivedDate = LocalDateTime.now();
        } else if (status == ComponentStatus.INSTALLED && installedDate == null) {
            installedDate = LocalDateTime.now();
        }
    }

    // Computed properties
    public BigDecimal getTotalCost() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public String getDisplayName() {
        return brand + " " + name + (partNumber != null ? " (" + partNumber + ")" : "");
    }
}