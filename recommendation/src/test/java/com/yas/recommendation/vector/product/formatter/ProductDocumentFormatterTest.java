package com.yas.recommendation.vector.product.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductDocumentFormatterTest {

    private ProductDocumentFormatter formatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formatter = new ProductDocumentFormatter();
        objectMapper = new ObjectMapper();
    }

    @Test
    void format_shouldReplaceTemplateVariables() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "iPhone");
        entityMap.put("price", 1000.0);
        entityMap.put("categories", List.of(new CategoryVm(1L, "Smartphone", null, "slug", null, null, (short) 1, true)));
        entityMap.put("attributeValues", List.of(new ProductAttributeValueVm(1L, "Color", "Red")));

        String template = "Product: {name}, Price: {price}, Categories: {categories}, Attributes: {attributeValues}";
        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).contains("Product: iPhone");
        assertThat(result).contains("Price: 1000.0");
        assertThat(result).contains("Categories: [Smartphone]");
        assertThat(result).contains("Attributes: [Color: Red]");
    }

    @Test
    void format_withNullCollections_shouldReturnEmptyBrackets() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "iPhone");
        entityMap.put("categories", null);
        entityMap.put("attributeValues", null);

        String template = "Cat: {categories}, Attr: {attributeValues}";
        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).isEqualTo("Cat: [], Attr: []");
    }

    @Test
    void format_shouldRemoveHtmlTags() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("description", "<p>Hello <b>World</b></p>");

        String template = "{description}";
        String result = formatter.format(entityMap, template, objectMapper);

        assertThat(result).isEqualTo("Hello World");
    }
}
