package com.nyamnyam.coach.ai.rag.service;

import com.nyamnyam.coach.ai.rag.config.HealthGuideRagProperties;
import com.nyamnyam.coach.ai.rag.document.RagReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HealthGuideRetrievalService {

    private static final Logger log = LoggerFactory.getLogger(HealthGuideRetrievalService.class);

    private final HealthGuideRagProperties properties;
    private final ObjectProvider<VectorStore> vectorStoreProvider;

    public HealthGuideRetrievalService(
            HealthGuideRagProperties properties,
            ObjectProvider<VectorStore> vectorStoreProvider
    ) {
        this.properties = properties;
        this.vectorStoreProvider = vectorStoreProvider;
    }

    public List<RagReference> retrieve(String query) {
        VectorStore vectorStore = vectorStoreProvider.getIfAvailable();
        if (vectorStore == null || query == null || query.isBlank()) {
            return List.of();
        }

        try {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(properties.getTopK())
                    .similarityThreshold(properties.getSimilarityThreshold())
                    .build();
            return vectorStore.similaritySearch(request).stream()
                    .map(this::toReference)
                    .toList();
        } catch (Exception exception) {
            log.warn("Health guide retrieval failed error={} queryLength={}", exception.getClass().getSimpleName(), query.length());
            return List.of();
        }
    }

    private RagReference toReference(Document document) {
        Map<String, Object> metadata = document.getMetadata();
        return new RagReference(
                stringValue(metadata.get("sourceName")),
                stringValue(metadata.get("sourceUrl")),
                stringValue(metadata.get("documentTitle")),
                stringValue(metadata.get("topic")),
                document.getText(),
                document.getScore()
        );
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString();
    }
}
