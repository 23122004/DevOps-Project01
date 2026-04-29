package com.yas.promotion.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromotionPostVmTest {

    @Test
    void createPromotionApplies_whenProduct_shouldMapProductIds() {
        PromotionPostVm vm = PromotionPostVm.builder()
                .productIds(List.of(1L, 2L))
                .build();
        Promotion promotion = Promotion.builder()
                .applyTo(ApplyTo.PRODUCT)
                .build();

        List<PromotionApply> result = PromotionPostVm.createPromotionApplies(vm, promotion);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(1).getProductId()).isEqualTo(2L);
    }

    @Test
    void createPromotionApplies_whenBrand_shouldMapBrandIds() {
        PromotionPostVm vm = PromotionPostVm.builder()
                .brandIds(List.of(10L))
                .build();
        Promotion promotion = Promotion.builder()
                .applyTo(ApplyTo.BRAND)
                .build();

        List<PromotionApply> result = PromotionPostVm.createPromotionApplies(vm, promotion);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrandId()).isEqualTo(10L);
    }

    @Test
    void createPromotionApplies_whenCategory_shouldMapCategoryIds() {
        PromotionPostVm vm = PromotionPostVm.builder()
                .categoryIds(List.of(100L))
                .build();
        Promotion promotion = Promotion.builder()
                .applyTo(ApplyTo.CATEGORY)
                .build();

        List<PromotionApply> result = PromotionPostVm.createPromotionApplies(vm, promotion);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(100L);
    }
}
