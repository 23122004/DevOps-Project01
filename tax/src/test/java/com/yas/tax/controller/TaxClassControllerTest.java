package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
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
class TaxClassControllerTest {

    @Mock
    private TaxClassService taxClassService;

    @InjectMocks
    private TaxClassController taxClassController;

    private TaxClass taxClass;
    private TaxClassVm taxClassVm;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder().build();
        taxClass.setId(1L);
        taxClass.setName("Standard");
        taxClassVm = new TaxClassVm(1L, "Standard");
    }

    @Test
    void getPageableTaxClasses_shouldReturnOk() {
        TaxClassListGetVm listGetVm = new TaxClassListGetVm(
            List.of(taxClassVm), 0, 5, 1, 1, true);
        when(taxClassService.getPageableTaxClasses(anyInt(), anyInt())).thenReturn(listGetVm);

        ResponseEntity<TaxClassListGetVm> response = taxClassController.getPageableTaxClasses(0, 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listGetVm);
    }

    @Test
    void listTaxClasses_shouldReturnOk() {
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(taxClassVm));

        ResponseEntity<List<TaxClassVm>> response = taxClassController.listTaxClasses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getTaxClass_shouldReturnOk() {
        when(taxClassService.findById(1L)).thenReturn(taxClassVm);

        ResponseEntity<TaxClassVm> response = taxClassController.getTaxClass(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(taxClassVm);
    }

    @Test
    void createTaxClass_shouldReturnCreated() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Standard");
        when(taxClassService.create(postVm)).thenReturn(taxClass);

        ResponseEntity<TaxClassVm> response = taxClassController.createTaxClass(
            postVm, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().name()).isEqualTo("Standard");
    }

    @Test
    void updateTaxClass_shouldReturnNoContent() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Updated");
        doNothing().when(taxClassService).update(any(), anyLong());

        ResponseEntity<Void> response = taxClassController.updateTaxClass(1L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteTaxClass_shouldReturnNoContent() {
        doNothing().when(taxClassService).delete(anyLong());

        ResponseEntity<Void> response = taxClassController.deleteTaxClass(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
