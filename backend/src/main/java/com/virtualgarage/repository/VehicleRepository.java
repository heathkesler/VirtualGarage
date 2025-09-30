package com.virtualgarage.repository;

import com.virtualgarage.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    // Basic queries
    List<Vehicle> findByIsActiveTrue();
    
    Page<Vehicle> findByIsActiveTrue(Pageable pageable);
    
    Optional<Vehicle> findByIdAndIsActiveTrue(Long id);
    
    // Search queries
    @Query("SELECT v FROM Vehicle v WHERE v.isActive = true AND " +
           "(LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.make) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.model) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.type) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Vehicle> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Filter by type
    Page<Vehicle> findByTypeIgnoreCaseAndIsActiveTrue(String type, Pageable pageable);
    
    // Filter by make
    Page<Vehicle> findByMakeIgnoreCaseAndIsActiveTrue(String make, Pageable pageable);
    
    // Filter by year range
    Page<Vehicle> findByYearBetweenAndIsActiveTrue(Integer startYear, Integer endYear, Pageable pageable);
    
    // Filter by tags
    @Query("SELECT DISTINCT v FROM Vehicle v JOIN v.tags t WHERE LOWER(t) = LOWER(:tag) AND v.isActive = true")
    Page<Vehicle> findByTag(@Param("tag") String tag, Pageable pageable);
    
    // Filter by value range
    Page<Vehicle> findByValueBetweenAndIsActiveTrue(BigDecimal minValue, BigDecimal maxValue, Pageable pageable);
    
    // Advanced search with multiple filters
    @Query("SELECT v FROM Vehicle v WHERE v.isActive = true AND " +
           "(:searchTerm IS NULL OR " +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.make) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.model) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.type) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:type IS NULL OR LOWER(v.type) = LOWER(:type)) AND " +
           "(:make IS NULL OR LOWER(v.make) = LOWER(:make)) AND " +
           "(:startYear IS NULL OR v.year >= :startYear) AND " +
           "(:endYear IS NULL OR v.year <= :endYear) AND " +
           "(:minValue IS NULL OR v.value >= :minValue) AND " +
           "(:maxValue IS NULL OR v.value <= :maxValue)")
    Page<Vehicle> findWithFilters(@Param("searchTerm") String searchTerm,
                                  @Param("type") String type,
                                  @Param("make") String make,
                                  @Param("startYear") Integer startYear,
                                  @Param("endYear") Integer endYear,
                                  @Param("minValue") BigDecimal minValue,
                                  @Param("maxValue") BigDecimal maxValue,
                                  Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.isActive = true")
    long countActiveVehicles();
    
    @Query("SELECT SUM(v.value) FROM Vehicle v WHERE v.isActive = true AND v.value IS NOT NULL")
    BigDecimal getTotalValue();
    
    @Query("SELECT AVG(v.value) FROM Vehicle v WHERE v.isActive = true AND v.value IS NOT NULL")
    BigDecimal getAverageValue();
    
    @Query("SELECT v.type, COUNT(v) FROM Vehicle v WHERE v.isActive = true GROUP BY v.type")
    List<Object[]> getVehicleCountByType();
    
    @Query("SELECT v.make, COUNT(v) FROM Vehicle v WHERE v.isActive = true GROUP BY v.make ORDER BY COUNT(v) DESC")
    List<Object[]> getVehicleCountByMake();
    
    // Recent vehicles
    @Query("SELECT v FROM Vehicle v WHERE v.isActive = true ORDER BY v.createdAt DESC")
    Page<Vehicle> findRecentVehicles(Pageable pageable);
    
    // Vehicles by date range
    Page<Vehicle> findByCreatedAtBetweenAndIsActiveTrue(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Vehicles with maintenance due
    @Query("SELECT DISTINCT v FROM Vehicle v JOIN v.maintenanceRecords mr WHERE " +
           "v.isActive = true AND mr.nextServiceDue <= :date")
    List<Vehicle> findWithMaintenanceDue(@Param("date") LocalDateTime date);
    
    // Vehicles by VIN (for validation)
    Optional<Vehicle> findByVinNumber(String vinNumber);
    
    boolean existsByVinNumber(String vinNumber);
    
    // High-value vehicles
    @Query("SELECT v FROM Vehicle v WHERE v.isActive = true AND v.value > :threshold ORDER BY v.value DESC")
    Page<Vehicle> findHighValueVehicles(@Param("threshold") BigDecimal threshold, Pageable pageable);
    
    // Custom query for dashboard statistics
    @Query("SELECT new map(" +
           "COUNT(v) as totalVehicles, " +
           "COALESCE(SUM(v.value), 0) as totalValue, " +
           "COALESCE(AVG(v.value), 0) as averageValue, " +
           "COUNT(CASE WHEN v.createdAt >= :monthAgo THEN 1 END) as newThisMonth" +
           ") FROM Vehicle v WHERE v.isActive = true")
    List<Object> getDashboardStats(@Param("monthAgo") LocalDateTime monthAgo);
}