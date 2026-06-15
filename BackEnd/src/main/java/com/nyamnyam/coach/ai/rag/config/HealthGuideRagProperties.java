package com.nyamnyam.coach.ai.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rag.health-guides")
public class HealthGuideRagProperties {

    private String seedLocation = "classpath:rag/health-guides/health-guides.jsonl";
    private boolean ingestionEnabled = false;
    private int topK = 5;
    private double similarityThreshold = 0.5;

    public String getSeedLocation() {
        return seedLocation;
    }

    public void setSeedLocation(String seedLocation) {
        this.seedLocation = seedLocation;
    }

    public boolean isIngestionEnabled() {
        return ingestionEnabled;
    }

    public void setIngestionEnabled(boolean ingestionEnabled) {
        this.ingestionEnabled = ingestionEnabled;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }
}
