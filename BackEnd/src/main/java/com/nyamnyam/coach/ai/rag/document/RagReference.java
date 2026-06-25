package com.nyamnyam.coach.ai.rag.document;

public record RagReference(
        String sourceName,
        String sourceUrl,
        String documentTitle,
        String topic,
        String content,
        Double score
) {
}
