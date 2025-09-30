package com.virtualgarage.repository;

import com.virtualgarage.entity.MaintenanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    
    // Find maintenance records by vehicle
    List<MaintenanceRecord> findByVehicleIdOrderByMaintenanceDateDesc(Long vehicleId);
    
    Page<MaintenanceRecord> findByVehicleIdOrderByMaintenanceDateDesc(Long vehicleId, Pageable pageable);
    
    // Find by maintenance type
    List<MaintenanceRecord> findByVehicleIdAndMaintenanceTypeIgnoreCaseOrderByMaintenanceDateDesc(Long vehicleId, String maintenanceType);
    
    // Find recent maintenance records
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.vehicle.id = :vehicleId AND mr.maintenanceDate >= :since ORDER BY mr.maintenanceDate DESC")
    List<MaintenanceRecord> findRecentMaintenanceForVehicle(@Param("vehicleId") Long vehicleId, @Param("since") LocalDateTime since);
    
    // Find maintenance records by date range
    Page<MaintenanceRecord> findByVehicleIdAndMaintenanceDateBetweenOrderByMaintenanceDateDesc(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find upcoming maintenance
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.vehicle.id = :vehicleId AND mr.nextServiceDue IS NOT NULL AND mr.nextServiceDue <= :date ORDER BY mr.nextServiceDue ASC")
    List<MaintenanceRecord> findUpcomingMaintenanceForVehicle(@Param("vehicleId") Long vehicleId, @Param("date") LocalDateTime date);
    
    // Get total maintenance cost for vehicle
    @Query("SELECT COALESCE(SUM(mr.cost), 0) FROM MaintenanceRecord mr WHERE mr.vehicle.id = :vehicleId AND mr.cost IS NOT NULL")
    BigDecimal getTotalMaintenanceCostForVehicle(@Param("vehicleId") Long vehicleId);
    
    // Get maintenance cost by date range
    @Query("SELECT COALESCE(SUM(mr.cost), 0) FROM MaintenanceRecord mr WHERE mr.vehicle.id = :vehicleId AND mr.maintenanceDate BETWEEN :startDate AND :endDate AND mr.cost IS NOT NULL")
    BigDecimal getMaintenanceCostForPeriod(@Param("vehicleId") Long vehicleId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Count maintenance records by type
    @Query("SELECT mr.maintenanceType, COUNT(mr) FROM MaintenanceRecord mr WHERE mr.vehicle.id = :vehicleId GROUP BY mr.maintenanceType ORDER BY COUNT(mr) DESC")
    List<Object[]> getMaintenanceCountByType(@Param("vehicleId") Long vehicleId);
    
    // Find last maintenance of specific type
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.vehicle.id = :vehicleId AND LOWER(mr.maintenanceType) = LOWER(:maintenanceType) ORDER BY mr.maintenanceDate DESC")
    List<MaintenanceRecord> findLastMaintenanceOfType(@Param("vehicleId") Long vehicleId, @Param("maintenanceType") String maintenanceType);
    
    // Find maintenance by service provider
    Page<MaintenanceRecord> findByVehicleIdAndServiceProviderIgnoreCaseContainingOrderByMaintenanceDateDesc(Long vehicleId, String serviceProvider, Pageable pageable);
    
    // Delete maintenance records by vehicle
    void deleteByVehicleId(Long vehicleId);
    
    // Count maintenance records for vehicle
    long countByVehicleId(Long vehicleId);
    
    // Find maintenance records with warranty still valid
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.vehicle.id = :vehicleId AND mr.warrantyUntil IS NOT NULL AND mr.warrantyUntil >= :currentDate ORDER BY mr.warrantyUntil ASC")
    List<MaintenanceRecord> findActiveWarrantiesForVehicle(@Param("vehicleId") Long vehicleId, @Param("currentDate") LocalDateTime currentDate);
}