package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VectorQueryUnitTest {

    @Mock
    private JdbcVectorService jdbcVectorService;

    @Mock
    private ObjectMapper objectMapper;

    private VectorQuery<ProductDocument, RelatedProductVm> vectorQuery;

    @BeforeEach
    void setUp() {
        vectorQuery = new VectorQuery<ProductDocument, RelatedProductVm>(ProductDocument.class, RelatedProductVm.class) {};
        ReflectionTestUtils.setField(vectorQuery, "jdbcVectorService", jdbcVectorService);
        ReflectionTestUtils.setField(vectorQuery, "objectMapper", objectMapper);
    }

    @Test
    void similaritySearch_shouldReturnResults() {
        Long productId = 1L;
        Document doc = new Document("content", Map.of("id", 2L));
        List<Document> docs = List.of(doc);
        RelatedProductVm vm = new RelatedProductVm();
        vm.setProductId(2L);
        vm.setName("Similar");
        vm.setSlug("slug");
        vm.setPrice(BigDecimal.valueOf(10.0));

        when(jdbcVectorService.similarityProduct(productId, ProductDocument.class)).thenReturn(docs);
        when(objectMapper.convertValue(any(Map.class), eq(RelatedProductVm.class))).thenReturn(vm);

        List<RelatedProductVm> result = vectorQuery.similaritySearch(productId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(vm);
    }

    @Test
    void toResult_whenMetadataIsNull_shouldFilterOut() {
        Document doc = org.mockito.Mockito.mock(Document.class);
        when(doc.getMetadata()).thenReturn(null);
        List<Document> docs = List.of(doc);

        List<RelatedProductVm> result = vectorQuery.toResult(docs);

        assertThat(result).isEmpty();
    }
}
