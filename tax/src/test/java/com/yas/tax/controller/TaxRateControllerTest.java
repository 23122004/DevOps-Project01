package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class TaxRateControllerTest {

    @Mock
    private TaxRateService taxRateService;

    @InjectMocks
    private TaxRateController taxRateController;

    private TaxClass taxClass;
    private TaxRate taxRate;
    private TaxRateVm taxRateVm;

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

        taxRateVm = TaxRateVm.fromModel(taxRate);
    }

    @Test
    void getPageableTaxRates_shouldReturnOk() {
        TaxRateListGetVm listGetVm = new TaxRateListGetVm(Collections.emptyList(), 0, 5, 0, 0, true);
        when(taxRateService.getPageableTaxRates(anyInt(), anyInt())).thenReturn(listGetVm);

        ResponseEntity<TaxRateListGetVm> response = taxRateController.getPageableTaxRates(0, 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listGetVm);
    }

    @Test
    void getTaxRate_shouldReturnOk() {
        when(taxRateService.findById(1L)).thenReturn(taxRateVm);

        ResponseEntity<TaxRateVm> response = taxRateController.getTaxRate(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(taxRateVm);
    }

    @Test
    void createTaxRate_shouldReturnCreated() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxRateService.createTaxRate(postVm)).thenReturn(taxRate);

        ResponseEntity<TaxRateVm> response = taxRateController.createTaxRate(
            postVm, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().rate()).isEqualTo(10.0);
    }

    @Test
    void updateTaxRate_shouldReturnNoContent() {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "54321", 1L, 2L, 2L);
        doNothing().when(taxRateService).updateTaxRate(any(), anyLong());

        ResponseEntity<Void> response = taxRateController.updateTaxRate(1L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteTaxRate_shouldReturnNoContent() {
        doNothing().when(taxRateService).delete(anyLong());

        ResponseEntity<Void> response = taxRateController.deleteTaxRate(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getTaxPercentByAddress_shouldReturnOk() {
        when(taxRateService.getTaxPercent(1L, 1L, 1L, "12345")).thenReturn(10.0);

        ResponseEntity<Double> response = taxRateController.getTaxPercentByAddress(1L, 1L, 1L, "12345");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(10.0);
    }

    @Test
    void getBatchTaxPercentsByAddress_shouldReturnOk() {
        when(taxRateService.getBulkTaxRate(any(), anyLong(), anyLong(), anyString()))
            .thenReturn(List.of(taxRateVm));

        ResponseEntity<List<TaxRateVm>> response =
            taxRateController.getBatchTaxPercentsByAddress(List.of(1L), 1L, 1L, "12345");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}
