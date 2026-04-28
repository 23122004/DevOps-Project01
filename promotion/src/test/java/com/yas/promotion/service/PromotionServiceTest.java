package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.PromotionUsage;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.repository.PromotionUsageRepository;
import com.yas.promotion.viewmodel.BrandVm;
import com.yas.promotion.viewmodel.CategoryGetVm;
import com.yas.promotion.viewmodel.ProductVm;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionPutVm;
import com.yas.promotion.viewmodel.PromotionUsageVm;
import com.yas.promotion.viewmodel.PromotionVerifyResultDto;
import com.yas.promotion.viewmodel.PromotionVerifyVm;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionUsageRepository promotionUsageRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private PromotionService promotionService;

    private Promotion promotion;
    private PromotionPostVm promotionPostVm;
    private PromotionPutVm promotionPutVm;

    @BeforeEach
    void setUp() {
        promotion = Promotion.builder()
                .id(1L)
                .name("Sample Promotion")
                .slug("sample-promotion")
                .description("Sample Description")
                .couponCode("CODE123")
                .applyTo(ApplyTo.PRODUCT)
                .usageType(UsageType.UNLIMITED)
                .discountType(DiscountType.PERCENTAGE)
                .discountPercentage(10L)
                .isActive(true)
                .startDate(Instant.parse("2024-01-01T00:00:00Z"))
                .endDate(Instant.parse("2024-12-31T23:59:59Z"))
                .minimumOrderPurchaseAmount(100L)
                .promotionApplies(new ArrayList<>())
                .build();
    }

    @Test
    void createPromotion_ShouldReturnPromotionDetailVm() {
        promotionPostVm = PromotionPostVm.builder()
                .name("Sample Promotion")
                .slug("sample-promotion")
                .description("Sample Description")
                .couponCode("CODE123")
                .applyTo(ApplyTo.PRODUCT)
                .usageType(UsageType.UNLIMITED)
                .discountType(DiscountType.PERCENTAGE)
                .discountPercentage(10L)
                .isActive(true)
                .startDate(Date.from(Instant.parse("2024-01-01T00:00:00Z")))
                .endDate(Date.from(Instant.parse("2024-12-31T23:59:59Z")))
                .minimumOrderPurchaseAmount(100L)
                .productIds(List.of(1L))
                .build();

        when(promotionRepository.findBySlugAndIsActiveTrue(promotionPostVm.getSlug())).thenReturn(Optional.empty());
        when(promotionRepository.findByCouponCodeAndIsActiveTrue(promotionPostVm.getCouponCode())).thenReturn(Optional.empty());
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        PromotionDetailVm result = promotionService.createPromotion(promotionPostVm);

        assertNotNull(result);
        assertEquals(promotion.getId(), result.id());
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    void createPromotion_WhenSlugExists_ShouldThrowDuplicatedException() {
        promotionPostVm = PromotionPostVm.builder()
                .slug("sample-promotion")
                .couponCode("CODE123")
                .startDate(Date.from(Instant.parse("2024-01-01T00:00:00Z")))
                .endDate(Date.from(Instant.parse("2024-12-31T23:59:59Z")))
                .build();

        when(promotionRepository.findBySlugAndIsActiveTrue(promotionPostVm.getSlug())).thenReturn(Optional.of(promotion));

        assertThrows(DuplicatedException.class, () -> promotionService.createPromotion(promotionPostVm));
    }

    @Test
    void getPromotion_WhenExists_ShouldReturnPromotionDetailVm() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        PromotionDetailVm result = promotionService.getPromotion(1L);

        assertNotNull(result);
        assertEquals(promotion.getId(), result.id());
    }

    @Test
    void getPromotion_WhenNotExists_ShouldThrowNotFoundException() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> promotionService.getPromotion(1L));
    }

    @Test
    void deletePromotion_WhenNotInUse_ShouldDelete() {
        when(promotionUsageRepository.existsByPromotionId(1L)).thenReturn(false);

        promotionService.deletePromotion(1L);

        verify(promotionRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePromotion_WhenInUse_ShouldThrowBadRequestException() {
        when(promotionUsageRepository.existsByPromotionId(1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> promotionService.deletePromotion(1L));
    }

    @Test
    void verifyPromotion_Success_ShouldReturnPromotionVerifyResultDto() {
        PromotionVerifyVm verifyVm = new PromotionVerifyVm(
                "CODE123", 150L, List.of(100L, 200L));

        promotion.setDiscountType(DiscountType.FIXED);
        promotion.setDiscountAmount(20L);
        promotion.setMinimumOrderPurchaseAmount(100L);
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("CODE123")).thenReturn(Optional.of(promotion));

        ProductVm mockedProduct = new ProductVm(100L, "Product 100", "product-100", true, true, false, true, 50.0, ZonedDateTime.now(), 1L);
        when(productService.getProductByIds(anyList())).thenReturn(List.of(mockedProduct));

        PromotionVerifyResultDto result = promotionService.verifyPromotion(verifyVm);

        assertTrue(result.isValid());
        assertEquals(100L, result.productId());
        assertEquals(DiscountType.FIXED, result.discountType());
    }

    @Test
    void verifyPromotion_WhenExhaustedUsage_ShouldThrowBadRequest() {
        promotion.setUsageType(UsageType.LIMITED);
        promotion.setUsageLimit(5);
        promotion.setUsageCount(5);
        PromotionVerifyVm verifyVm = new PromotionVerifyVm("CODE123", 150L, List.of(100L));

        when(promotionRepository.findByCouponCodeAndIsActiveTrue("CODE123")).thenReturn(Optional.of(promotion));

        assertThrows(BadRequestException.class, () -> promotionService.verifyPromotion(verifyVm));
    }
}
