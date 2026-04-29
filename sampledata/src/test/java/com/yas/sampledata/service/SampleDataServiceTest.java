package com.yas.sampledata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

import com.yas.sampledata.utils.SqlScriptExecutor;
import com.yas.sampledata.viewmodel.SampleDataVm;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SampleDataServiceTest {

    @Mock
    private DataSource productDataSource;

    @Mock
    private DataSource mediaDataSource;

    private SampleDataService sampleDataService;

    @BeforeEach
    void setUp() {
        sampleDataService = new SampleDataService(productDataSource, mediaDataSource);
    }

    @Test
    void createSampleData_shouldExecuteScriptsAndReturnSuccess() {
        try (MockedConstruction<SqlScriptExecutor> mocked = mockConstruction(SqlScriptExecutor.class)) {
            SampleDataVm result = sampleDataService.createSampleData();

            assertThat(result.message()).isEqualTo("Insert Sample Data successfully!");
            SqlScriptExecutor executor = mocked.constructed().get(0);
            verify(executor).executeScriptsForSchema(eq(productDataSource), eq("public"), eq("classpath*:db/product/*.sql"));
            verify(executor).executeScriptsForSchema(eq(mediaDataSource), eq("public"), eq("classpath*:db/media/*.sql"));
        }
    }
}
