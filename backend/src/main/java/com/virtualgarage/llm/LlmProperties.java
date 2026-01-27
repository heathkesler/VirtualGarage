package com.virtualgarage.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for LLM clients.
 */
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
    
    /**
     * The active LLM provider to use (claude, grok)
     */
    private String provider = "ollama";
    
    private Claude claude = new Claude();
    private Grok grok = new Grok();
    private Ollama ollama = new Ollama();
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public Claude getClaude() {
        return claude;
    }
    
    public void setClaude(Claude claude) {
        this.claude = claude;
    }
    
    public Grok getGrok() {
        return grok;
    }
    
    public void setGrok(Grok grok) {
        this.grok = grok;
    }
    
    public Ollama getOllama() {
        return ollama;
    }
    
    public void setOllama(Ollama ollama) {
        this.ollama = ollama;
    }
    
    public static class Claude {
        private String apiKey;
        private String model = "claude-sonnet-4-20250514";
        private String baseUrl = "https://api.anthropic.com";
        private int maxTokens = 4096;
        private int timeoutSeconds = 60;
        
        public String getApiKey() {
            return apiKey;
        }
        
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public int getMaxTokens() {
            return maxTokens;
        }
        
        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }
        
        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }
        
        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }
    }
    
    public static class Grok {
        private String apiKey;
        private String model = "grok-1";
        private String baseUrl = "https://api.x.ai";
        private int maxTokens = 4096;
        private int timeoutSeconds = 60;
        
        public String getApiKey() {
            return apiKey;
        }
        
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public int getMaxTokens() {
            return maxTokens;
        }
        
        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }
        
        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }
        
        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }
    }
    
    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "deepseek-coder-v2:latest";
        private int maxTokens = 4096;
        private int timeoutSeconds = 120;
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public int getMaxTokens() {
            return maxTokens;
        }
        
        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }
        
        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }
        
        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }
    }
}
