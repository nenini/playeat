package com.nyamnyam.coach.ai.rag.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.ai.rag.config.HealthGuideRagProperties;
import com.nyamnyam.coach.ai.rag.document.HealthGuideDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HealthGuideIngestionService {

    private static final Logger log = LoggerFactory.getLogger(HealthGuideIngestionService.class);

    private final HealthGuideRagProperties properties;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final ObjectProvider<VectorStore> vectorStoreProvider;

    public HealthGuideIngestionService(
            HealthGuideRagProperties properties,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            ObjectProvider<VectorStore> vectorStoreProvider
    ) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.vectorStoreProvider = vectorStoreProvider;
    }

    public int ingestSeedDocuments() {
        VectorStore vectorStore = vectorStoreProvider.getIfAvailable();
        if (vectorStore == null) {
            log.warn("Health guide ingestion skipped reason=vector-store-unavailable");
            return 0;
        }
        if (isAlreadySeeded(vectorStore)) {
            log.info("Health guide ingestion skipped reason=already-seeded");
            return 0;
        }

        List<Document> documents = loadSeedDocuments();
        if (documents.isEmpty()) {
            log.warn("Health guide ingestion skipped reason=empty-seed location={}", properties.getSeedLocation());
            return 0;
        }

        try {
            vectorStore.add(documents);
        } catch (Exception e) {
            log.warn("Health guide ingestion failed error={} count={} location={}",
                    e.getClass().getSimpleName(), documents.size(), properties.getSeedLocation());
            return 0;
        }
        log.info("Health guide ingestion completed count={} location={}", documents.size(), properties.getSeedLocation());
        return documents.size();
    }

    private boolean isAlreadySeeded(VectorStore vectorStore) {
        try {
            return !vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query("건강 나트륨 영양")
                            .topK(1)
                            .similarityThreshold(0.0)
                            .build()
            ).isEmpty();
        } catch (Exception e) {
            log.warn("Health guide seed check failed error={} will attempt ingestion", e.getClass().getSimpleName());
            return false;
        }
    }

    private List<Document> loadSeedDocuments() {
        Resource resource = resourceLoader.getResource(properties.getSeedLocation());
        List<Document> documents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                HealthGuideDocument guide = objectMapper.readValue(line, HealthGuideDocument.class);
                documents.add(toDocument(guide));
            }
            return documents;
        } catch (Exception exception) {
            log.warn("Health guide seed load failed location={} error={}", properties.getSeedLocation(), exception.getClass().getSimpleName());
            return List.of();
        }
    }

    private Document toDocument(HealthGuideDocument guide) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("sourceName", guide.sourceName());
        metadata.put("sourceUrl", guide.sourceUrl());
        metadata.put("documentTitle", guide.documentTitle());
        metadata.put("topic", guide.topic());
        metadata.put("trustLevel", guide.trustLevel());

        return Document.builder()
                .id(guide.id())
                .text(guide.content())
                .metadata(metadata)
                .build();
    }
}
