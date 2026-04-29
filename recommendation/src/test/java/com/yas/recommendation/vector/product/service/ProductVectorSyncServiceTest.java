package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductVectorSyncServiceTest {

    @Mock
    private ProductVectorRepository productVectorRepository;

    @InjectMocks
    private ProductVectorSyncService productVectorSyncService;

    @Mock
    private Product product;

    @Test
    void createProductVector_whenPublished_shouldAdd() {
        when(product.isPublished()).thenReturn(true);
        when(product.getId()).thenReturn(1L);

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository).add(1L);
    }

    @Test
    void createProductVector_whenNotPublished_shouldNotAdd() {
        when(product.isPublished()).thenReturn(false);

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository, never()).add(any());
    }

    @Test
    void updateProductVector_whenPublished_shouldUpdate() {
        when(product.isPublished()).thenReturn(true);
        when(product.getId()).thenReturn(1L);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).update(1L);
    }

    @Test
    void updateProductVector_whenNotPublished_shouldDelete() {
        when(product.isPublished()).thenReturn(false);
        when(product.getId()).thenReturn(1L);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).delete(1L);
    }

    @Test
    void deleteProductVector_shouldDelete() {
        productVectorSyncService.deleteProductVector(1L);
        verify(productVectorRepository).delete(1L);
    }

    private Long any() {
        return org.mockito.ArgumentMatchers.anyLong();
    }
}
