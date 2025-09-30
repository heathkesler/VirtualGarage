package com.virtualgarage.service;

import com.virtualgarage.dto.VehicleDTO;
import com.virtualgarage.entity.Vehicle;
import com.virtualgarage.repository.VehicleRepository;
import com.virtualgarage.repository.VehicleImageRepository;
import com.virtualgarage.repository.MaintenanceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleImageScrapingService imageScrapingService;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository,
                         VehicleImageRepository vehicleImageRepository,
                         MaintenanceRecordRepository maintenanceRecordRepository,
                         VehicleImageScrapingService imageScrapingService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleImageRepository = vehicleImageRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.imageScrapingService = imageScrapingService;
    }

    @Transactional(readOnly = true)
    public Page<VehicleDTO> getAllVehicles(Pageable pageable) {
        logger.debug("Fetching all vehicles with pagination: {}", pageable);
        Page<Vehicle> vehicles = vehicleRepository.findByIsActiveTrue(pageable);
        return vehicles.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<VehicleDTO> getVehicleById(Long id) {
        logger.debug("Fetching vehicle by id: {}", id);
        Optional<Vehicle> vehicle = vehicleRepository.findByIdAndIsActiveTrue(id);
        return vehicle.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<VehicleDTO> searchVehicles(String searchTerm, Pageable pageable) {
        logger.debug("Searching vehicles with term: {}", searchTerm);
        Page<Vehicle> vehicles;
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            vehicles = vehicleRepository.findByIsActiveTrue(pageable);
        } else {
            vehicles = vehicleRepository.findBySearchTerm(searchTerm.trim(), pageable);
        }
        return vehicles.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<VehicleDTO> getVehiclesByType(String type, Pageable pageable) {
        logger.debug("Fetching vehicles by type: {}", type);
        Page<Vehicle> vehicles = vehicleRepository.findByTypeIgnoreCaseAndIsActiveTrue(type, pageable);
        return vehicles.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<VehicleDTO> getVehiclesByTag(String tag, Pageable pageable) {
        logger.debug("Fetching vehicles by tag: {}", tag);
        Page<Vehicle> vehicles = vehicleRepository.findByTag(tag, pageable);
        return vehicles.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<VehicleDTO> getVehiclesWithFilters(String searchTerm, String type, String make,
                                                   Integer startYear, Integer endYear,
                                                   BigDecimal minValue, BigDecimal maxValue,
                                                   Pageable pageable) {
        logger.debug("Fetching vehicles with filters - search: {}, type: {}, make: {}", searchTerm, type, make);
        Page<Vehicle> vehicles = vehicleRepository.findWithFilters(
            searchTerm, type, make, startYear, endYear, minValue, maxValue, pageable);
        return vehicles.map(this::convertToDTO);
    }

    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        logger.debug("Creating new vehicle: {}", vehicleDTO.getName());
        
        // Validate VIN number if provided
        if (vehicleDTO.getVinNumber() != null && vehicleRepository.existsByVinNumber(vehicleDTO.getVinNumber())) {
            throw new IllegalArgumentException("Vehicle with VIN " + vehicleDTO.getVinNumber() + " already exists");
        }
        
        // Auto-scrape vehicle image if none provided
        if (vehicleDTO.getPrimaryImageUrl() == null || vehicleDTO.getPrimaryImageUrl().trim().isEmpty()) {
            logger.debug("No image provided, attempting to scrape for: {} {} {}", 
                vehicleDTO.getYear(), vehicleDTO.getMake(), vehicleDTO.getModel());
            
            try {
                var scrapedImage = imageScrapingService.scrapeVehicleImage(
                    vehicleDTO.getMake(), 
                    vehicleDTO.getModel(), 
                    vehicleDTO.getYear()
                );
                
                if (scrapedImage.isPresent()) {
                    vehicleDTO.setPrimaryImageUrl(scrapedImage.get());
                    logger.info("Successfully scraped image for {} {} {}: {}", 
                        vehicleDTO.getYear(), vehicleDTO.getMake(), vehicleDTO.getModel(), scrapedImage.get());
                } else {
                    logger.warn("Failed to scrape image for {} {} {}", 
                        vehicleDTO.getYear(), vehicleDTO.getMake(), vehicleDTO.getModel());
                }
            } catch (Exception e) {
                logger.error("Error during image scraping for {} {} {}: {}", 
                    vehicleDTO.getYear(), vehicleDTO.getMake(), vehicleDTO.getModel(), e.getMessage());
            }
        }
        
        Vehicle vehicle = convertToEntity(vehicleDTO);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        logger.info("Created vehicle with id: {}", savedVehicle.getId());
        return convertToDTO(savedVehicle);
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        logger.debug("Updating vehicle with id: {}", id);
        
        Vehicle existingVehicle = vehicleRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + id));

        // Check VIN uniqueness if VIN is being changed
        if (vehicleDTO.getVinNumber() != null && 
            !vehicleDTO.getVinNumber().equals(existingVehicle.getVinNumber()) &&
            vehicleRepository.existsByVinNumber(vehicleDTO.getVinNumber())) {
            throw new IllegalArgumentException("Vehicle with VIN " + vehicleDTO.getVinNumber() + " already exists");
        }

        // Update fields
        updateEntityFromDTO(existingVehicle, vehicleDTO);
        Vehicle savedVehicle = vehicleRepository.save(existingVehicle);
        
        logger.info("Updated vehicle with id: {}", savedVehicle.getId());
        return convertToDTO(savedVehicle);
    }

    public void deleteVehicle(Long id) {
        logger.debug("Deleting vehicle with id: {}", id);
        
        Vehicle vehicle = vehicleRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + id));
        
        // Soft delete
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
        
        logger.info("Soft deleted vehicle with id: {}", id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        logger.debug("Fetching dashboard statistics");
        
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        long totalVehicles = vehicleRepository.countActiveVehicles();
        BigDecimal totalValue = vehicleRepository.getTotalValue();
        BigDecimal averageValue = vehicleRepository.getAverageValue();
        
        if (totalValue == null) totalValue = BigDecimal.ZERO;
        if (averageValue == null) averageValue = BigDecimal.ZERO;
        
        return Map.of(
            "total_vehicles", totalVehicles,
            "total_value", totalValue,
            "average_value", averageValue,
            "new_this_month", vehicleRepository.findByCreatedAtBetweenAndIsActiveTrue(monthAgo, LocalDateTime.now(), Pageable.unpaged()).getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVehicleStatsByType() {
        logger.debug("Fetching vehicle statistics by type");
        return vehicleRepository.getVehicleCountByType()
            .stream()
            .map(result -> Map.of(
                "type", result[0],
                "count", result[1]
            ))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVehicleStatsByMake() {
        logger.debug("Fetching vehicle statistics by make");
        return vehicleRepository.getVehicleCountByMake()
            .stream()
            .map(result -> Map.of(
                "make", result[0],
                "count", result[1]
            ))
            .collect(Collectors.toList());
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setName(vehicle.getName());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setType(vehicle.getType());
        dto.setColor(vehicle.getColor());
        dto.setEngine(vehicle.getEngine());
        dto.setEngineSize(vehicle.getEngineSize());
        dto.setTransmission(vehicle.getTransmission());
        dto.setDrivetrain(vehicle.getDrivetrain());
        dto.setFuelType(vehicle.getFuelType());
        dto.setMileage(vehicle.getMileage());
        dto.setMileageUnit(vehicle.getMileageUnit());
        dto.setValue(vehicle.getValue());
        dto.setPurchasePrice(vehicle.getPurchasePrice());
        dto.setPurchaseDate(vehicle.getPurchaseDate());
        dto.setStatus(vehicle.getStatus());
        dto.setVinNumber(vehicle.getVinNumber());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setPrimaryImageUrl(vehicle.getPrimaryImageUrl());
        dto.setDescription(vehicle.getDescription());
        dto.setNotes(vehicle.getNotes());
        dto.setTags(vehicle.getTags());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        
        // Add counts
        dto.setImageCount((int) vehicleImageRepository.countByVehicleId(vehicle.getId()));
        dto.setMaintenanceCount((int) maintenanceRecordRepository.countByVehicleId(vehicle.getId()));
        
        return dto;
    }

    private Vehicle convertToEntity(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        updateEntityFromDTO(vehicle, dto);
        return vehicle;
    }

    private void updateEntityFromDTO(Vehicle vehicle, VehicleDTO dto) {
        vehicle.setName(dto.getName());
        vehicle.setMake(dto.getMake());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setType(dto.getType());
        vehicle.setColor(dto.getColor());
        vehicle.setEngine(dto.getEngine());
        vehicle.setEngineSize(dto.getEngineSize());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setDrivetrain(dto.getDrivetrain());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setMileage(dto.getMileage());
        vehicle.setMileageUnit(dto.getMileageUnit());
        vehicle.setValue(dto.getValue());
        vehicle.setPurchasePrice(dto.getPurchasePrice());
        vehicle.setPurchaseDate(dto.getPurchaseDate());
        vehicle.setStatus(dto.getStatus() != null ? dto.getStatus() : "Excellent");
        vehicle.setVinNumber(dto.getVinNumber());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setPrimaryImageUrl(dto.getPrimaryImageUrl());
        vehicle.setDescription(dto.getDescription());
        vehicle.setNotes(dto.getNotes());
        
        if (dto.getTags() != null) {
            vehicle.setTags(dto.getTags());
        }
    }
}