package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

@ExtendWith(MockitoExtension.class)
class DocumentRowMapperTest {

    @Mock
    private ResultSet resultSet;

    @Test
    void mapRow_shouldReturnDocument() throws SQLException {
        ObjectMapper objectMapper = new ObjectMapper();
        DocumentRowMapper mapper = new DocumentRowMapper(objectMapper);

        when(resultSet.getString("id")).thenReturn("doc1");
        when(resultSet.getString("content")).thenReturn("some content");
        when(resultSet.getObject("metadata")).thenReturn("{\"key\":\"value\"}");

        Document result = mapper.mapRow(resultSet, 1);

        assertThat(result.getId()).isEqualTo("doc1");
        assertThat(result.getContent()).isEqualTo("some content");
        assertThat(result.getMetadata()).containsEntry("key", "value");
    }
}
