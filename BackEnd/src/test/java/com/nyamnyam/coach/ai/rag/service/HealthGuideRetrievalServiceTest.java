package com.nyamnyam.coach.ai.rag.service;

import com.nyamnyam.coach.ai.rag.config.HealthGuideRagProperties;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthGuideRetrievalServiceTest {

    @Test
    void retrieveReturnsEmptyWhenVectorStoreIsUnavailable() {
        HealthGuideRetrievalService service = new HealthGuideRetrievalService(
                new HealthGuideRagProperties(),
                unavailableProvider()
        );

        assertThat(service.retrieve("나트륨 줄이기")).isEmpty();
    }

    @Test
    void retrieveMapsDocumentsToReferences() {
        VectorStore vectorStore = mock(VectorStore.class);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(
                Document.builder()
                        .id("mfds-sodium-001")
                        .text("나트륨 섭취를 줄이려면 국물 섭취를 줄인다.")
                        .metadata(Map.of(
                                "sourceName", "식품의약품안전처",
                                "sourceUrl", "https://www.mfds.go.kr/",
                                "documentTitle", "나트륨 저감 안내",
                                "topic", "sodium"
                        ))
                        .score(0.91)
                        .build()
        ));

        HealthGuideRetrievalService service = new HealthGuideRetrievalService(
                new HealthGuideRagProperties(),
                availableProvider(vectorStore)
        );

        var references = service.retrieve("나트륨 섭취가 높아요");

        assertThat(references).hasSize(1);
        assertThat(references.get(0).sourceName()).isEqualTo("식품의약품안전처");
        assertThat(references.get(0).topic()).isEqualTo("sodium");
        assertThat(references.get(0).content()).contains("나트륨");
    }

    private ObjectProvider<VectorStore> unavailableProvider() {
        ObjectProvider<VectorStore> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);
        return provider;
    }

    private ObjectProvider<VectorStore> availableProvider(VectorStore vectorStore) {
        ObjectProvider<VectorStore> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(vectorStore);
        return provider;
    }
}
