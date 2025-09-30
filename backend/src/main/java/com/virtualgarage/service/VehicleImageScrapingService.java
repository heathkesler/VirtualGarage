package com.virtualgarage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for scraping vehicle images from various sources when user doesn't provide one.
 * Uses multiple strategies including Unsplash API, automotive websites, and manufacturer sites.
 */
@Service
public class VehicleImageScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleImageScrapingService.class);
    
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;
    
    // Default Unsplash Access Key (demo/development - replace with your own)
    @Value("${vehicle.image.unsplash.access-key:}")
    private String unsplashAccessKey;
    
    // Fallback image sources - more specific categories
    private static final List<String> FALLBACK_IMAGES = Arrays.asList(
        "https://images.unsplash.com/photo-1494905998402-395d579af36f?w=800&h=600&fit=crop&crop=center", // 0 - Classic Muscle Car
        "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop&crop=center", // 1 - Modern Car
        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop&crop=center", // 2 - Sports Car
        "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800&h=600&fit=crop&crop=center", // 3 - Electric Car
        "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop&crop=center", // 4 - Supercar
        "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop&crop=center", // 5 - Classic Roadster
        "https://images.unsplash.com/photo-1586636965570-b67b5a5b2ff0?w=800&h=600&fit=crop&crop=center", // 6 - Pickup Truck
        "https://images.unsplash.com/photo-1519641080-df4b2d4aa07a?w=800&h=600&fit=crop&crop=center", // 7 - SUV/Crossover
        "https://images.unsplash.com/photo-1605559424843-9e4c228bf1c2?w=800&h=600&fit=crop&crop=center", // 8 - Luxury Sedan
        "https://images.unsplash.com/photo-1542282088-fe8426682b8f?w=800&h=600&fit=crop&crop=center"  // 9 - Classic Truck
    );

    public VehicleImageScrapingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Scrapes a vehicle image based on make, model, and year.
     * Tries multiple sources in order of preference.
     */
    public Optional<String> scrapeVehicleImage(String make, String model, Integer year) {
        logger.info("Scraping vehicle image for: {} {} {}", year, make, model);
        
        try {
            // Strategy 1: Try Unsplash API with specific search
            Optional<String> unsplashImage = scrapeFromUnsplash(make, model, year);
            if (unsplashImage.isPresent()) {
                logger.info("Found image from Unsplash for {} {} {}", year, make, model);
                return unsplashImage;
            }
            
            // Strategy 2: Try generic automotive search on Unsplash
            Optional<String> genericImage = scrapeGenericVehicleImage(make, model);
            if (genericImage.isPresent()) {
                logger.info("Found generic image for {} {}", make, model);
                return genericImage;
            }
            
            // Strategy 3: Return type-based fallback image
            Optional<String> fallbackImage = getFallbackImageByVehicleType(make, model);
            logger.info("Using fallback image for {} {} {}", year, make, model);
            return fallbackImage;
            
        } catch (Exception e) {
            logger.error("Error scraping vehicle image for {} {} {}: {}", year, make, model, e.getMessage());
            return getFallbackImageByVehicleType(make, model);
        }
    }

    /**
     * Scrapes image from Unsplash API with specific vehicle search
     */
    private Optional<String> scrapeFromUnsplash(String make, String model, Integer year) {
        if (unsplashAccessKey == null || unsplashAccessKey.trim().isEmpty()) {
            logger.debug("Unsplash access key not configured, skipping API search");
            return Optional.empty();
        }
        
        try {
            // Create search query
            String query = String.format("%d %s %s car", year != null ? year : 2020, make, model);
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("https://api.unsplash.com/search/photos?query=%s&per_page=5&orientation=landscape", encodedQuery);
            
            HttpGet request = new HttpGet(url);
            request.addHeader("Authorization", "Client-ID " + unsplashAccessKey);
            request.addHeader("User-Agent", "Virtual-Garage/1.0");
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    JsonNode results = jsonNode.get("results");
                    
                    if (results != null && results.isArray() && results.size() > 0) {
                        JsonNode firstResult = results.get(0);
                        JsonNode urls = firstResult.get("urls");
                        if (urls != null) {
                            String imageUrl = urls.get("regular").asText();
                            return Optional.of(imageUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch from Unsplash API: {}", e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Scrapes generic vehicle image from Unsplash using simpler search
     */
    private Optional<String> scrapeGenericVehicleImage(String make, String model) {
        try {
            // Try with just make and model
            List<String> searchTerms = Arrays.asList(
                make + " " + model + " car",
                make + " car",
                model + " car",
                "luxury car",
                "sports car"
            );
            
            for (String searchTerm : searchTerms) {
                Optional<String> result = searchUnsplashByTerm(searchTerm);
                if (result.isPresent()) {
                    return result;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch generic vehicle image: {}", e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Searches Unsplash by specific term
     */
    private Optional<String> searchUnsplashByTerm(String searchTerm) {
        if (unsplashAccessKey == null || unsplashAccessKey.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            String encodedQuery = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
            String url = String.format("https://api.unsplash.com/search/photos?query=%s&per_page=3&orientation=landscape", encodedQuery);
            
            HttpGet request = new HttpGet(url);
            request.addHeader("Authorization", "Client-ID " + unsplashAccessKey);
            request.addHeader("User-Agent", "Virtual-Garage/1.0");
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    JsonNode results = jsonNode.get("results");
                    
                    if (results != null && results.isArray() && results.size() > 0) {
                        JsonNode firstResult = results.get(0);
                        JsonNode urls = firstResult.get("urls");
                        if (urls != null) {
                            String imageUrl = urls.get("regular").asText();
                            return Optional.of(imageUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to search Unsplash for term '{}': {}", searchTerm, e.getMessage());
        }
        
        return Optional.empty();
    }

    /**
     * Returns a fallback image based on vehicle characteristics
     */
    private Optional<String> getFallbackImageByVehicleType(String make, String model) {
        String makeLower = make != null ? make.toLowerCase() : "";
        String modelLower = model != null ? model.toLowerCase() : "";
        
        // Check for specific vehicle types first (most specific to least specific)
        if (isTruckVehicle(makeLower, modelLower)) {
            return Optional.of(FALLBACK_IMAGES.get(6)); // Pickup Truck
        } else if (isClassicTruck(makeLower, modelLower)) {
            return Optional.of(FALLBACK_IMAGES.get(9)); // Classic Truck
        } else if (isSUVVehicle(makeLower, modelLower)) {
            return Optional.of(FALLBACK_IMAGES.get(7)); // SUV/Crossover
        } else if (isExoticVehicle(makeLower, modelLower)) {
            return Optional.of(FALLBACK_IMAGES.get(4)); // Supercar
        } else if (isElectricVehicle(makeLower, modelLower)) {
            return Optional.of(FALLBACK_IMAGES.get(3)); // Electric Car
        } else if (isSportsVehicle(makeLower, modelLower)) {
            return Optional.of(FALLBACK_IMAGES.get(2)); // Sports Car
        } else if (isLuxurySedan(makeLower, modelLower)) {
            return Optional.of(FALLBACK_IMAGES.get(8)); // Luxury Sedan
        } else if (isClassicVehicle(makeLower, modelLower)) {
            // Check if it's a roadster/convertible for more specific classic image
            if (isRoadster(makeLower, modelLower)) {
                return Optional.of(FALLBACK_IMAGES.get(5)); // Classic Roadster
            } else {
                return Optional.of(FALLBACK_IMAGES.get(0)); // Classic Muscle Car
            }
        } else {
            return Optional.of(FALLBACK_IMAGES.get(1)); // Modern Car
        }
    }

    /**
     * Determines if vehicle is classic based on make/model
     */
    private boolean isClassicVehicle(String make, String model) {
        List<String> classicMakes = Arrays.asList("ford", "chevrolet", "chevy", "dodge", "plymouth", "pontiac", 
            "oldsmobile", "buick", "cadillac", "lincoln", "mercury", "chrysler");
        List<String> classicModels = Arrays.asList("mustang", "camaro", "corvette", "charger", "challenger", 
            "firebird", "gto", "chevelle", "nova", "impala", "cuda", "roadrunner");
        
        return classicMakes.contains(make) || 
               classicModels.stream().anyMatch(model::contains);
    }

    /**
     * Determines if vehicle is sports/performance based on make/model
     */
    private boolean isSportsVehicle(String make, String model) {
        List<String> sportsMakes = Arrays.asList("porsche", "bmw", "mercedes", "audi", "lexus", "infiniti");
        List<String> sportsModels = Arrays.asList("911", "m3", "m4", "m5", "amg", "rs", "gtr", "gt-r", 
            "supra", "rx-7", "rx7", "350z", "370z", "z4", "s4", "s6");
        
        return sportsMakes.contains(make) || 
               sportsModels.stream().anyMatch(model::contains);
    }

    /**
     * Determines if vehicle is electric based on make/model
     */
    private boolean isElectricVehicle(String make, String model) {
        List<String> electricMakes = Arrays.asList("tesla", "nissan", "chevrolet", "bmw", "audi");
        List<String> electricModels = Arrays.asList("model", "leaf", "volt", "bolt", "i3", "i4", "e-tron", 
            "etron", "taycan", "mach-e", "mustang mach");
        
        return make.equals("tesla") || 
               electricModels.stream().anyMatch(model::contains);
    }

    /**
     * Determines if vehicle is exotic/supercar based on make/model
     */
    private boolean isExoticVehicle(String make, String model) {
        List<String> exoticMakes = Arrays.asList("ferrari", "lamborghini", "mclaren", "bugatti", "koenigsegg", 
            "pagani", "aston martin", "rolls royce", "bentley", "maserati");
        List<String> exoticModels = Arrays.asList("720s", "650s", "570s", "gallardo", "huracan", "aventador", 
            "veyron", "chiron", "db11", "vantage", "continental", "ghost", "phantom");
        
        return exoticMakes.contains(make) || 
               exoticModels.stream().anyMatch(model::contains);
    }
    
    /**
     * Determines if vehicle is a truck based on make/model
     */
    private boolean isTruckVehicle(String make, String model) {
        List<String> truckMakes = Arrays.asList("ford", "chevrolet", "chevy", "gmc", "ram", "dodge", "nissan", "toyota");
        List<String> truckModels = Arrays.asList("f-150", "f150", "silverado", "sierra", "ram 1500", "tacoma", 
            "tundra", "frontier", "colorado", "canyon", "ranger", "ridgeline", "titan");
        
        return (truckMakes.contains(make) && (model.contains("truck") || model.contains("pickup"))) ||
               truckModels.stream().anyMatch(model::contains);
    }
    
    /**
     * Determines if vehicle is a classic truck based on make/model and context
     */
    private boolean isClassicTruck(String make, String model) {
        List<String> classicTruckMakes = Arrays.asList("chevrolet", "chevy", "ford", "dodge", "gmc", "international");
        List<String> classicTruckModels = Arrays.asList("c10", "c20", "k10", "f-100", "f100", "apache", "stepside", "fleetside");
        
        boolean isClassicMake = classicTruckMakes.contains(make);
        boolean hasClassicModel = classicTruckModels.stream().anyMatch(model::contains);
        boolean hasTruckKeyword = model.contains("truck") || model.contains("pickup");
        
        return (isClassicMake && hasClassicModel) || (isClassicMake && hasTruckKeyword);
    }
    
    /**
     * Determines if vehicle is an SUV/Crossover based on make/model
     */
    private boolean isSUVVehicle(String make, String model) {
        List<String> suvModels = Arrays.asList("explorer", "expedition", "suburban", "tahoe", "yukon", "escalade", 
            "x3", "x5", "x7", "q5", "q7", "gle", "glc", "rx", "gx", "lx", "pilot", "highlander", "4runner", "sequoia");
        
        return model.contains("suv") || model.contains("crossover") || 
               suvModels.stream().anyMatch(model::contains);
    }
    
    /**
     * Determines if vehicle is a luxury sedan based on make/model
     */
    private boolean isLuxurySedan(String make, String model) {
        List<String> luxuryMakes = Arrays.asList("mercedes", "bmw", "audi", "lexus", "infiniti", "cadillac", "lincoln", "genesis");
        List<String> sedanModels = Arrays.asList("s-class", "e-class", "c-class", "7 series", "5 series", "3 series", 
            "a8", "a6", "a4", "ls", "es", "is", "gs", "q50", "q70", "ct6", "ats", "cts");
        
        return (luxuryMakes.contains(make) && (model.contains("sedan") || sedanModels.stream().anyMatch(model::contains)));
    }
    
    /**
     * Determines if vehicle is a roadster/convertible based on make/model
     */
    private boolean isRoadster(String make, String model) {
        List<String> roadsterModels = Arrays.asList("roadster", "convertible", "cabriolet", "spider", "spyder", 
            "miata", "z4", "boxster", "sl", "slk", "cobra", "corvette");
        
        return roadsterModels.stream().anyMatch(model::contains);
    }

    /**
     * Validates if a URL returns a valid image
     */
    public boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return false;
        }
        
        try {
            HttpGet request = new HttpGet(imageUrl);
            request.addHeader("User-Agent", "Virtual-Garage/1.0");
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String contentType = response.getFirstHeader("Content-Type").getValue();
                return response.getCode() == 200 && 
                       contentType != null && 
                       contentType.startsWith("image/");
            }
        } catch (Exception e) {
            logger.debug("Invalid image URL: {}", imageUrl);
            return false;
        }
    }
}