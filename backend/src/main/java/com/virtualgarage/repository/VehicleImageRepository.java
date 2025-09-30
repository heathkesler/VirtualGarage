package com.virtualgarage.repository;

import com.virtualgarage.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
    
    // Find images by vehicle
    List<VehicleImage> findByVehicleIdOrderByDisplayOrderAsc(Long vehicleId);
    
    // Find primary image for vehicle
    Optional<VehicleImage> findByVehicleIdAndIsPrimaryTrue(Long vehicleId);
    
    // Find images by vehicle with pagination
    List<VehicleImage> findByVehicleIdOrderByIsPrimaryDescDisplayOrderAsc(Long vehicleId);
    
    // Count images for a vehicle
    long countByVehicleId(Long vehicleId);
    
    // Update primary image - set all other images for this vehicle to not primary
    @Modifying
    @Query("UPDATE VehicleImage vi SET vi.isPrimary = false WHERE vi.vehicle.id = :vehicleId AND vi.id != :imageId")
    void clearPrimaryFlagForOtherImages(@Param("vehicleId") Long vehicleId, @Param("imageId") Long imageId);
    
    // Set image as primary
    @Modifying
    @Query("UPDATE VehicleImage vi SET vi.isPrimary = true WHERE vi.id = :imageId")
    void setPrimaryImage(@Param("imageId") Long imageId);
    
    // Find images by URL (for cleanup operations)
    Optional<VehicleImage> findByImageUrl(String imageUrl);
    
    // Delete images by vehicle ID
    void deleteByVehicleId(Long vehicleId);
    
    // Get max display order for vehicle
    @Query("SELECT COALESCE(MAX(vi.displayOrder), 0) FROM VehicleImage vi WHERE vi.vehicle.id = :vehicleId")
    Integer getMaxDisplayOrderForVehicle(@Param("vehicleId") Long vehicleId);
}