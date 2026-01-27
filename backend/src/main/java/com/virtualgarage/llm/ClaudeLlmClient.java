package com.virtualgarage.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Claude (Anthropic) LLM client implementation.
 */
@Component
public class ClaudeLlmClient implements LlmClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ClaudeLlmClient.class);
    private static final String API_VERSION = "2023-06-01";
    private static final String PROVIDER_NAME = "claude";
    
    private final LlmProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    
    public ClaudeLlmClient(LlmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }
    
    @Override
    public String chat(String systemPrompt, String userMessage) {
        LlmProperties.Claude config = properties.getClaude();
        
        if (!isAvailable()) {
            throw new LlmException(PROVIDER_NAME, "Claude API key not configured", 0);
        }
        
        try {
            String requestBody = buildRequestBody(systemPrompt, userMessage, config);
            
            logger.debug("Sending request to Claude API with model: {}", config.getModel());
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", config.getApiKey())
                    .header("anthropic-version", API_VERSION)
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                logger.error("Claude API error: status={}, body={}", response.statusCode(), response.body());
                throw new LlmException(PROVIDER_NAME, "API request failed: " + response.body(), response.statusCode());
            }
            
            return parseResponse(response.body());
            
        } catch (LlmException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error calling Claude API", e);
            throw new LlmException(PROVIDER_NAME, "Failed to call API: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public boolean isAvailable() {
        String apiKey = properties.getClaude().getApiKey();
        return apiKey != null && !apiKey.trim().isEmpty();
    }
    
    private String buildRequestBody(String systemPrompt, String userMessage, LlmProperties.Claude config) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", config.getModel());
            root.put("max_tokens", config.getMaxTokens());
            
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                root.put("system", systemPrompt);
            }
            
            ArrayNode messages = root.putArray("messages");
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new LlmException(PROVIDER_NAME, "Failed to build request body", e);
        }
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("content");
            
            if (content.isArray() && content.size() > 0) {
                JsonNode firstBlock = content.get(0);
                if (firstBlock.has("text")) {
                    return firstBlock.get("text").asText();
                }
            }
            
            throw new LlmException(PROVIDER_NAME, "Unexpected response format: " + responseBody, 0);
            
        } catch (LlmException e) {
            throw e;
        } catch (Exception e) {
            throw new LlmException(PROVIDER_NAME, "Failed to parse response: " + e.getMessage(), e);
        }
    }
}
