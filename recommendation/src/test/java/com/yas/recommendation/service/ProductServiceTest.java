package com.yas.recommendation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private RestClient restClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RecommendationConfig config;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        when(config.getApiUrl()).thenReturn("http://api.yas.local");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getProductDetail_shouldReturnProductDetailVm() {
        long productId = 1L;
        // Create a minimal ProductDetailVm using all-args constructor with nulls
        ProductDetailVm expectedVm = new ProductDetailVm(
            productId, "Product 1", null, null, null, null, null, null,
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null
        );
        ResponseEntity<ProductDetailVm> responseEntity = ResponseEntity.ok(expectedVm);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        ProductDetailVm actualVm = productService.getProductDetail(productId);

        assertThat(actualVm).isEqualTo(expectedVm);
        assertThat(actualVm.id()).isEqualTo(productId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getProductDetail_whenNullBody_shouldReturnNull() {
        long productId = 2L;
        ResponseEntity<ProductDetailVm> responseEntity = ResponseEntity.ok(null);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        ProductDetailVm actualVm = productService.getProductDetail(productId);

        assertThat(actualVm).isNull();
    }
}
