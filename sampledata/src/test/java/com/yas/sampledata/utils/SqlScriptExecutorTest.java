package com.yas.sampledata.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SqlScriptExecutorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @InjectMocks
    private SqlScriptExecutor sqlScriptExecutor;

    @Test
    void executeScriptsForSchema_shouldExecuteSuccessfully() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenReturn(true);

        sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath*:db/dummy/*.sql");

        verify(connection).setSchema("public");
    }

    @Test
    void executeScriptsForSchema_whenSQLException_shouldCatchAndLog() throws Exception {
        when(dataSource.getConnection()).thenThrow(new java.sql.SQLException("DB error"));

        sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath*:db/dummy/*.sql");

        // Verification is that it doesn't throw an exception (caught and logged)
        verify(dataSource).getConnection();
    }
}
