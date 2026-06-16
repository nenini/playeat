package com.nyamnyam.coach.ai.rag.document;

public record HealthGuideDocument(
        String id,
        String sourceName,
        String sourceUrl,
        String documentTitle,
        String topic,
        String trustLevel,
        String content
) {
}
