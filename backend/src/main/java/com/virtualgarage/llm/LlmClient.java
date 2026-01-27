package com.virtualgarage.llm;

/**
 * Interface for LLM (Large Language Model) clients.
 * Abstraction layer to support multiple LLM providers (Claude, Grok, etc.)
 */
public interface LlmClient {
    
    /**
     * Send a chat message to the LLM and get a response.
     *
     * @param systemPrompt The system prompt that sets the context/behavior
     * @param userMessage The user's message/query
     * @return The LLM's response text
     * @throws LlmException if the API call fails
     */
    String chat(String systemPrompt, String userMessage);
    
    /**
     * Get the provider name for this client.
     *
     * @return Provider name (e.g., "claude", "grok")
     */
    String getProviderName();
    
    /**
     * Check if the client is properly configured and ready to use.
     *
     * @return true if the client can make API calls
     */
    boolean isAvailable();
}
