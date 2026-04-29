package com.yas.sampledata.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.sampledata.service.SampleDataService;
import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SampleDataControllerTest {

    @Mock
    private SampleDataService sampleDataService;

    @InjectMocks
    private SampleDataController sampleDataController;

    @Test
    void createSampleData_shouldReturnSampleDataVm() {
        SampleDataVm expectedResponse = new SampleDataVm("success");
        when(sampleDataService.createSampleData()).thenReturn(expectedResponse);

        SampleDataVm actualResponse = sampleDataController.createSampleData(new SampleDataVm("request"));

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
