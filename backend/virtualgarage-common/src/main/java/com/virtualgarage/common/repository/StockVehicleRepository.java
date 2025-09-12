package com.virtualgarage.common.repository;

import com.virtualgarage.common.entity.StockVehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockVehicleRepository extends JpaRepository<StockVehicle, String> {
    
    Page<StockVehicle> findByMakeContainingIgnoreCase(String make, Pageable pageable);
    
    Page<StockVehicle> findByModelContainingIgnoreCase(String model, Pageable pageable);
    
    Page<StockVehicle> findByYear(Integer year, Pageable pageable);
    
    Page<StockVehicle> findByCategory(String category, Pageable pageable);
    
    @Query("SELECT s FROM StockVehicle s WHERE " +
           "(:make IS NULL OR LOWER(s.make) LIKE LOWER(CONCAT('%', :make, '%'))) AND " +
           "(:model IS NULL OR LOWER(s.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:year IS NULL OR s.year = :year)")
    Page<StockVehicle> findByFilters(@Param("make") String make, 
                                   @Param("model") String model, 
                                   @Param("year") Integer year, 
                                   Pageable pageable);
    
    List<String> findDistinctMakeByOrderByMake();
    
    @Query("SELECT DISTINCT s.model FROM StockVehicle s WHERE s.make = :make ORDER BY s.model")
    List<String> findDistinctModelsByMake(@Param("make") String make);
    
    @Query("SELECT DISTINCT s.year FROM StockVehicle s ORDER BY s.year DESC")
    List<Integer> findDistinctYears();
}