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
 * Ollama LLM client implementation for local AI models.
 * Supports DeepSeek, Qwen, Llama, and other Ollama-compatible models.
 */
@Component
public class OllamaLlmClient implements LlmClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaLlmClient.class);
    private static final String PROVIDER_NAME = "ollama";
    
    private final LlmProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    
    public OllamaLlmClient(LlmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }
    
    @Override
    public String chat(String systemPrompt, String userMessage) {
        LlmProperties.Ollama config = properties.getOllama();
        
        if (!isAvailable()) {
            throw new LlmException(PROVIDER_NAME, "Ollama server not available at " + config.getBaseUrl(), 0);
        }
        
        try {
            String requestBody = buildRequestBody(systemPrompt, userMessage, config);
            
            logger.debug("Sending request to Ollama API with model: {}", config.getModel());
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/api/chat"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                logger.error("Ollama API error: status={}, body={}", response.statusCode(), response.body());
                throw new LlmException(PROVIDER_NAME, "API request failed: " + response.body(), response.statusCode());
            }
            
            return parseResponse(response.body());
            
        } catch (LlmException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error calling Ollama API", e);
            throw new LlmException(PROVIDER_NAME, "Failed to call API: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public boolean isAvailable() {
        LlmProperties.Ollama config = properties.getOllama();
        try {
            // Check if Ollama is running by hitting the /api/tags endpoint
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/api/tags"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            logger.warn("Ollama server not available: {}", e.getMessage());
            return false;
        }
    }
    
    private String buildRequestBody(String systemPrompt, String userMessage, LlmProperties.Ollama config) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", config.getModel());
            root.put("stream", false);
            
            // Build messages array for chat API
            ArrayNode messages = root.putArray("messages");
            
            // Add system message if provided
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                ObjectNode systemMsg = messages.addObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
            }
            
            // Add user message
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            
            // Options for generation
            ObjectNode options = root.putObject("options");
            options.put("num_predict", config.getMaxTokens());
            
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new LlmException(PROVIDER_NAME, "Failed to build request body", e);
        }
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            
            // Ollama chat API returns response in "message.content"
            JsonNode message = root.path("message");
            if (message.has("content")) {
                return message.get("content").asText();
            }
            
            // Fallback for generate API format
            if (root.has("response")) {
                return root.get("response").asText();
            }
            
            throw new LlmException(PROVIDER_NAME, "Unexpected response format: " + responseBody, 0);
            
        } catch (LlmException e) {
            throw e;
        } catch (Exception e) {
            throw new LlmException(PROVIDER_NAME, "Failed to parse response: " + e.getMessage(), e);
        }
    }
}
