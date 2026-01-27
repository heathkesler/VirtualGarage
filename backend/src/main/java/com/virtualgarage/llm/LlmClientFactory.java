package com.virtualgarage.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory for selecting the appropriate LLM client based on configuration.
 */
@Component
public class LlmClientFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(LlmClientFactory.class);
    
    private final Map<String, LlmClient> clients;
    private final LlmProperties properties;
    
    public LlmClientFactory(ClaudeLlmClient claudeClient, OllamaLlmClient ollamaClient, LlmProperties properties) {
        this.properties = properties;
        this.clients = Map.of(
                "claude", claudeClient,
                "ollama", ollamaClient
        );
    }
    
    /**
     * Get the configured LLM client.
     *
     * @return The active LLM client
     * @throws LlmException if no valid client is available
     */
    public LlmClient getClient() {
        String provider = properties.getProvider().toLowerCase();
        
        LlmClient client = clients.get(provider);
        if (client == null) {
            logger.warn("Unknown LLM provider: {}. Falling back to claude.", provider);
            client = clients.get("claude");
        }
        
        if (client == null || !client.isAvailable()) {
            throw new LlmException("No configured LLM client available. Check your API key configuration.");
        }
        
        logger.debug("Using LLM provider: {}", client.getProviderName());
        return client;
    }
    
    /**
     * Get a specific LLM client by provider name.
     *
     * @param provider The provider name
     * @return The LLM client, or null if not found
     */
    public LlmClient getClient(String provider) {
        return clients.get(provider.toLowerCase());
    }
}
