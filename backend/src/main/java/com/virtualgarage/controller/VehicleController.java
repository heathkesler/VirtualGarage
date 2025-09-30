package com.virtualgarage.controller;

import com.virtualgarage.dto.VehicleDTO;
import com.virtualgarage.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicle Management", description = "APIs for managing vehicles in the virtual garage")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Retrieve a paginated list of all active vehicles")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles")
    public ResponseEntity<Page<VehicleDTO>> getAllVehicles(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("GET /vehicles - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<VehicleDTO> vehicles = vehicleService.getAllVehicles(pageable);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieve a specific vehicle by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicle")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        logger.debug("GET /vehicles/{}", id);
        
        Optional<VehicleDTO> vehicle = vehicleService.getVehicleById(id);
        return vehicle.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search vehicles", description = "Search vehicles by name, make, model, or type")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    public ResponseEntity<Page<VehicleDTO>> searchVehicles(
            @Parameter(description = "Search term") @RequestParam(required = false) String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("GET /vehicles/search - query: {}", q);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<VehicleDTO> vehicles = vehicleService.searchVehicles(q, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter vehicles", description = "Filter vehicles by multiple criteria")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered results")
    public ResponseEntity<Page<VehicleDTO>> filterVehicles(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) BigDecimal minValue,
            @RequestParam(required = false) BigDecimal maxValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("GET /vehicles/filter - type: {}, make: {}", type, make);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<VehicleDTO> vehicles = vehicleService.getVehiclesWithFilters(
            search, type, make, startYear, endYear, minValue, maxValue, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get vehicles by type", description = "Retrieve vehicles filtered by type")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles by type")
    public ResponseEntity<Page<VehicleDTO>> getVehiclesByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("GET /vehicles/type/{}", type);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<VehicleDTO> vehicles = vehicleService.getVehiclesByType(type, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get vehicles by tag", description = "Retrieve vehicles filtered by tag")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles by tag")
    public ResponseEntity<Page<VehicleDTO>> getVehiclesByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("GET /vehicles/tag/{}", tag);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<VehicleDTO> vehicles = vehicleService.getVehiclesByTag(tag, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    @Operation(summary = "Create new vehicle", description = "Create a new vehicle in the garage")
    @ApiResponse(responseCode = "201", description = "Successfully created vehicle")
    @ApiResponse(responseCode = "400", description = "Invalid vehicle data")
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        logger.debug("POST /vehicles - creating vehicle: {}", vehicleDTO.getName());
        
        try {
            VehicleDTO createdVehicle = vehicleService.createVehicle(vehicleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating vehicle: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle", description = "Update an existing vehicle")
    @ApiResponse(responseCode = "200", description = "Successfully updated vehicle")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @ApiResponse(responseCode = "400", description = "Invalid vehicle data")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleDTO vehicleDTO) {
        logger.debug("PUT /vehicles/{}", id);
        
        try {
            VehicleDTO updatedVehicle = vehicleService.updateVehicle(id, vehicleDTO);
            return ResponseEntity.ok(updatedVehicle);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating vehicle {}: {}", id, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle", description = "Soft delete a vehicle (mark as inactive)")
    @ApiResponse(responseCode = "204", description = "Successfully deleted vehicle")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        logger.debug("DELETE /vehicles/{}", id);
        
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting vehicle {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve key statistics for the dashboard")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        logger.debug("GET /vehicles/stats/dashboard");
        
        Map<String, Object> stats = vehicleService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/by-type")
    @Operation(summary = "Get vehicle statistics by type", description = "Retrieve vehicle count grouped by type")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved type statistics")
    public ResponseEntity<List<Map<String, Object>>> getVehicleStatsByType() {
        logger.debug("GET /vehicles/stats/by-type");
        
        List<Map<String, Object>> stats = vehicleService.getVehicleStatsByType();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/by-make")
    @Operation(summary = "Get vehicle statistics by make", description = "Retrieve vehicle count grouped by make")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved make statistics")
    public ResponseEntity<List<Map<String, Object>>> getVehicleStatsByMake() {
        logger.debug("GET /vehicles/stats/by-make");
        
        List<Map<String, Object>> stats = vehicleService.getVehicleStatsByMake();
        return ResponseEntity.ok(stats);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}