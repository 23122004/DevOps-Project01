package com.yas.recommendation.vector.common.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;

import com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter;

class BaseDocumentTest {

    @DocumentMetadata(docIdPrefix = "TEST", contentFormat = "{name}", documentFormatter = DefaultDocumentFormatter.class)
    private static class TestDocument extends BaseDocument {}

    private static class InvalidDocument extends BaseDocument {}

    @Test
    void toDocument_shouldConvertToSpringAiDocument() {
        TestDocument doc = new TestDocument();
        doc.setContent("Test Content");
        doc.setMetadata(Map.of("key", "value"));
        IdGenerator idGenerator = contents -> "generated-id";

        Document result = doc.toDocument(idGenerator);

        assertThat(result.getContent()).isEqualTo("Test Content");
        assertThat(result.getMetadata()).containsEntry("key", "value");
        assertThat(result.getId()).isEqualTo("generated-id");
    }

    @Test
    void toDocument_withoutAnnotation_shouldThrowException() {
        InvalidDocument doc = new InvalidDocument();
        doc.setContent("Content");
        doc.setMetadata(Map.of());
        IdGenerator idGenerator = contents -> "id";

        assertThatThrownBy(() -> doc.toDocument(idGenerator))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document must annotated by '@DocumentMetadata'");
    }

    @Test
    void toDocument_withNullContent_shouldThrowException() {
        TestDocument doc = new TestDocument();
        doc.setMetadata(Map.of());
        IdGenerator idGenerator = contents -> "id";

        assertThatThrownBy(() -> doc.toDocument(idGenerator))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document's content cannot be null");
    }
}
