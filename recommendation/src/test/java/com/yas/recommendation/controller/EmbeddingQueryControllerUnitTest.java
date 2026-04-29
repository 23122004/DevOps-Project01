package com.yas.recommendation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmbeddingQueryControllerUnitTest {

    @Mock
    private VectorQuery<ProductDocument, RelatedProductVm> relatedProductSearch;

    @InjectMocks
    private EmbeddingQueryController embeddingQueryController;

    @Test
    void searchProduct_shouldReturnList() {
        Long productId = 1L;
        RelatedProductVm vm = new RelatedProductVm();
        vm.setProductId(2L);
        vm.setName("Similar Product");
        List<RelatedProductVm> expectedList = List.of(vm);
        when(relatedProductSearch.similaritySearch(productId)).thenReturn(expectedList);

        List<RelatedProductVm> result = embeddingQueryController.searchProduct(productId);

        assertThat(result).isEqualTo(expectedList);
    }
}
