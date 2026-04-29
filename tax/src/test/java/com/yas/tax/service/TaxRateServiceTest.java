package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.Collections;
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
class TaxRateServiceTest {

    @Mock
    private TaxRateRepository taxRateRepository;

    @Mock
    private TaxClassRepository taxClassRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private TaxRateService taxRateService;

    private TaxClass taxClass;
    private TaxRate taxRate;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder().build();
        taxClass.setId(1L);
        taxClass.setName("Standard");

        taxRate = TaxRate.builder()
            .rate(10.0)
            .zipCode("12345")
            .taxClass(taxClass)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();
        taxRate.setId(1L);
    }

    @Test
    void createTaxRate_whenTaxClassExists_shouldSaveAndReturn() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any())).thenReturn(taxRate);

        TaxRate result = taxRateService.createTaxRate(postVm);

        assertThat(result.getRate()).isEqualTo(10.0);
    }

    @Test
    void createTaxRate_whenTaxClassNotFound_shouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 99L, 1L, 1L);
        when(taxClassRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.createTaxRate(postVm))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateTaxRate_whenTaxRateExists_shouldUpdate() {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "54321", 1L, 2L, 2L);
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);

        taxRateService.updateTaxRate(postVm, 1L);

        verify(taxRateRepository).save(taxRate);
        assertThat(taxRate.getRate()).isEqualTo(15.0);
    }

    @Test
    void updateTaxRate_whenTaxRateNotFound_shouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "54321", 1L, 2L, 2L);
        when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, 99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenTaxRateExists_shouldDelete() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);

        taxRateService.delete(1L);

        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void delete_whenTaxRateNotFound_shouldThrowNotFoundException() {
        when(taxRateRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taxRateService.delete(99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_whenExists_shouldReturnTaxRateVm() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertThat(result.rate()).isEqualTo(10.0);
    }

    @Test
    void findById_whenNotFound_shouldThrowNotFoundException() {
        when(taxRateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxRateService.findById(99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllTaxRates() {
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getPageableTaxRates_whenEmptyStateOrProvinceIds_shouldReturnEmptyDetailVms() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<TaxRate> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 5);

        assertThat(result.taxRateGetDetailContent()).isEmpty();
    }

    @Test
    void getTaxPercent_whenTaxPercentFound_shouldReturnValue() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(10.5);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");

        assertThat(result).isEqualTo(10.5);
    }

    @Test
    void getTaxPercent_whenNull_shouldReturnZero() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");

        assertThat(result).isZero();
    }
}
