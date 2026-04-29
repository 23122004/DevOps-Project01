package com.yas.recommendation.vector.common.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DefaultDocumentFormatterTest {

    @Test
    void format_shouldReplaceVariablesAndRemoveHtml() {
        DefaultDocumentFormatter formatter = new DefaultDocumentFormatter();
        Map<String, Object> entityMap = Map.of("name", "<b>Test</b>");
        String template = "Name: {name}";

        String result = formatter.format(entityMap, template, new ObjectMapper());

        assertThat(result).isEqualTo("Name: Test");
    }
}
