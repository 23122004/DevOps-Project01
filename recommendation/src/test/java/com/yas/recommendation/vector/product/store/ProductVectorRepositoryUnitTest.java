package com.yas.recommendation.vector.product.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProductVectorRepositoryUnitTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ProductService productService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    private ProductVectorRepository productVectorRepository;

    @BeforeEach
    void setUp() {
        productVectorRepository = new ProductVectorRepository(vectorStore, productService);
        ReflectionTestUtils.setField(productVectorRepository, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(productVectorRepository, "embeddingSearchConfiguration", embeddingSearchConfiguration);
    }

    @Test
    void add_shouldFetchProductAndAddToVectorStore() {
        Long productId = 1L;
        ProductDetailVm vm = new ProductDetailVm(productId, "Product", "short", "desc", "spec", "sku", "gtin", "slug", true, true, true, true, true, 10.0, 1L, List.of(), "metaTitle", "metaKeyword", "metaDescription", 1L, "Brand", List.of(), List.of(), null, List.of());
        Map<String, Object> entityMap = Map.of("id", productId, "name", "Product");

        when(productService.getProductDetail(productId)).thenReturn(vm);
        when(objectMapper.convertValue(any(), eq(Map.class))).thenReturn(new java.util.HashMap<>(entityMap));

        productVectorRepository.add(productId);

        verify(vectorStore).add(anyList());
    }

    @Test
    void delete_shouldDeleteFromVectorStore() {
        Long productId = 1L;
        productVectorRepository.delete(productId);
        verify(vectorStore).delete(anyList());
    }

    @Test
    void search_shouldCallSimilaritySearch() {
        Long productId = 1L;
        ProductDetailVm vm = new ProductDetailVm(productId, "Product", "short", "desc", "spec", "sku", "gtin", "slug", true, true, true, true, true, 10.0, 1L, List.of(), "metaTitle", "metaKeyword", "metaDescription", 1L, "Brand", List.of(), List.of(), null, List.of());
        Map<String, Object> entityMap = Map.of("id", productId, "name", "Product");
        Document doc = new Document("content", Map.of("id", 2L));

        when(productService.getProductDetail(productId)).thenReturn(vm);
        when(objectMapper.convertValue(any(), eq(Map.class))).thenReturn(new java.util.HashMap<>(entityMap));
        when(embeddingSearchConfiguration.topK()).thenReturn(5);
        when(embeddingSearchConfiguration.similarityThreshold()).thenReturn(0.5);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

        productVectorRepository.search(productId);

        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    private <T> Class<T> eq(Class<T> clazz) {
        return org.mockito.ArgumentMatchers.eq(clazz);
    }
}
