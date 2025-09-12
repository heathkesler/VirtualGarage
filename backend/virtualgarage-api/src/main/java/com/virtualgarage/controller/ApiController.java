package com.virtualgarage.controller;

import com.virtualgarage.common.entity.StockVehicle;
import com.virtualgarage.common.repository.StockVehicleRepository;
// Swagger imports temporarily removed
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
// @Tag removed temporarily
public class ApiController {

    private final StockVehicleRepository stockVehicleRepository;

    @GetMapping("/health")
    // Swagger annotations removed temporarily
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Virtual Garage API",
            "version", "1.0.0",
            "timestamp", java.time.Instant.now().toString()
        ));
    }

    @GetMapping("/vehicles/stock")
    // Swagger annotations removed temporarily
    public ResponseEntity<Page<StockVehicle>> getStockVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StockVehicle> vehicles = stockVehicleRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/vehicles/stock/search")
    // Swagger annotations removed temporarily
    public ResponseEntity<Page<StockVehicle>> searchStockVehicles(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<StockVehicle> vehicles;
        if (make != null || model != null || year != null) {
            vehicles = stockVehicleRepository.findByFilters(make, model, year, PageRequest.of(page, size));
        } else {
            vehicles = stockVehicleRepository.findAll(PageRequest.of(page, size));
        }
        return ResponseEntity.ok(vehicles);
    }
}