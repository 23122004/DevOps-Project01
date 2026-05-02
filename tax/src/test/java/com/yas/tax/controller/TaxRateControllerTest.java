package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TaxRateController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
class TaxRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaxRateService taxRateService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getPageableTaxRates_shouldReturnPage() throws Exception {
        TaxRateListGetVm response = new TaxRateListGetVm(List.of(), 0, 10, 0, 0, true);
        when(taxRateService.getPageableTaxRates(anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/backoffice/tax-rates/paging"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaxRate_shouldReturnTaxRate() throws Exception {
        TaxRateVm vm = new TaxRateVm(1L, 10.0, 1L, null, null, null, null);
        when(taxRateService.findById(1L)).thenReturn(vm);

        mockMvc.perform(get("/backoffice/tax-rates/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createTaxRate_shouldReturnCreated() throws Exception {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, 1L, 1L, 1L, "Zip", "Name");
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        when(taxRateService.create(any(TaxRatePostVm.class))).thenReturn(taxRate);

        mockMvc.perform(post("/backoffice/tax-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTaxRate_shouldReturnNoContent() throws Exception {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, 1L, 1L, 1L, "Zip", "Name");
        doNothing().when(taxRateService).update(any(TaxRatePostVm.class), anyLong());

        mockMvc.perform(put("/backoffice/tax-rates/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxRate_shouldReturnNoContent() throws Exception {
        doNothing().when(taxRateService).delete(1L);

        mockMvc.perform(delete("/backoffice/tax-rates/1"))
                .andExpect(status().isNoContent());
    }
}
