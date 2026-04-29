package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JdbcVectorServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    private JdbcVectorService jdbcVectorService;

    @DocumentMetadata(docIdPrefix = "TEST", contentFormat = "{content}", documentFormatter = DefaultDocumentFormatter.class)
    private static class TestDocument extends BaseDocument {
    }

    @BeforeEach
    void setUp() {
        jdbcVectorService = new JdbcVectorService(jdbcTemplate, objectMapper, embeddingSearchConfiguration);
        ReflectionTestUtils.setField(jdbcVectorService, "vectorTableName", "test_vector_store");
    }

    @Test
    void similarityProduct_shouldCallJdbcTemplateQuery() {
        Long id = 123L;
        Document doc = new Document("content");
        List<Document> expectedDocs = List.of(doc);

        when(embeddingSearchConfiguration.similarityThreshold()).thenReturn(0.5);
        when(embeddingSearchConfiguration.topK()).thenReturn(5);
        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(DocumentRowMapper.class)))
            .thenReturn(expectedDocs);

        List<Document> result = jdbcVectorService.similarityProduct(id, TestDocument.class);

        assertThat(result).isEqualTo(expectedDocs);
    }

    @Test
    void getDocIdPrefix_whenNoAnnotation_shouldReturnDefault() {
        String prefix = ReflectionTestUtils.invokeMethod(jdbcVectorService, "getDocIdPrefix", BaseDocument.class);
        assertThat(prefix).isEqualTo(JdbcVectorService.DEFAULT_DOCID_PREFIX);
    }

    @Test
    void generateUuid_shouldReturnDeterministicUuid() {
        UUID uuid1 = ReflectionTestUtils.invokeMethod(jdbcVectorService, "generateUuid", "PREFIX", 1L);
        UUID uuid2 = ReflectionTestUtils.invokeMethod(jdbcVectorService, "generateUuid", "PREFIX", 1L);
        assertThat(uuid1).isEqualTo(uuid2);
    }
}
