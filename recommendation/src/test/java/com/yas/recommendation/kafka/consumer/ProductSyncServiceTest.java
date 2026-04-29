package com.yas.recommendation.kafka.consumer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.kafka.cdc.message.Operation;
import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSyncServiceTest {

    @Mock
    private ProductVectorSyncService productVectorSyncService;

    @InjectMocks
    private ProductSyncService productSyncService;

    @Mock
    private ProductMsgKey key;

    @Mock
    private ProductCdcMessage message;

    @Mock
    private Product product;

    @Test
    void sync_whenMessageIsNull_shouldDelete() {
        when(key.getId()).thenReturn(1L);
        productSyncService.sync(key, null);
        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_whenOperationIsDelete_shouldDelete() {
        when(key.getId()).thenReturn(1L);
        when(message.getOp()).thenReturn(Operation.DELETE);
        productSyncService.sync(key, message);
        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_whenOperationIsCreate_shouldCreate() {
        when(message.getOp()).thenReturn(Operation.CREATE);
        when(message.getAfter()).thenReturn(product);
        productSyncService.sync(key, message);
        verify(productVectorSyncService).createProductVector(product);
    }

    @Test
    void sync_whenOperationIsUpdate_shouldUpdate() {
        when(message.getOp()).thenReturn(Operation.UPDATE);
        when(message.getAfter()).thenReturn(product);
        productSyncService.sync(key, message);
        verify(productVectorSyncService).updateProductVector(product);
    }
}
