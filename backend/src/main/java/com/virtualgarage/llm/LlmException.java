package com.virtualgarage.llm;

/**
 * Exception thrown when LLM API calls fail.
 */
public class LlmException extends RuntimeException {
    
    private final String provider;
    private final int statusCode;
    
    public LlmException(String message) {
        super(message);
        this.provider = "unknown";
        this.statusCode = 0;
    }
    
    public LlmException(String message, Throwable cause) {
        super(message, cause);
        this.provider = "unknown";
        this.statusCode = 0;
    }
    
    public LlmException(String provider, String message, int statusCode) {
        super(String.format("[%s] %s (status: %d)", provider, message, statusCode));
        this.provider = provider;
        this.statusCode = statusCode;
    }
    
    public LlmException(String provider, String message, Throwable cause) {
        super(String.format("[%s] %s", provider, message), cause);
        this.provider = provider;
        this.statusCode = 0;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
