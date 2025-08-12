package com.dealsplatform.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConfigTest {

    private DatabaseConfig databaseConfig;
    private Method convertToJdbcUrlMethod;

    @BeforeEach
    void setUp() throws Exception {
        databaseConfig = new DatabaseConfig();
        // Получаем доступ к приватному методу через рефлексию
        convertToJdbcUrlMethod = DatabaseConfig.class.getDeclaredMethod("convertToJdbcUrl", String.class);
        convertToJdbcUrlMethod.setAccessible(true);
    }

    @Test
    void testConvertPostgresqlUrlToJdbc() throws Exception {
        // Тестируем URL от Render.com
        String renderUrl = "postgresql://dbpostgres_p4tp_user:1RCarCNAGp1MF4pqlMZxhiO0TAgirOAZ@dpg-d2d7naruibrs739bgkd0-a/dbpostgres_p4tp";
        String expected = "jdbc:postgresql://dpg-d2d7naruibrs739bgkd0-a:5432/dbpostgres_p4tp";
        
        String result = (String) convertToJdbcUrlMethod.invoke(databaseConfig, renderUrl);
        
        assertEquals(expected, result, "URL должен быть правильно преобразован");
    }

    @Test
    void testConvertPostgresqlUrlWithPort() throws Exception {
        // Тестируем URL с уже указанным портом
        String renderUrl = "postgresql://user:pass@host:5433/database";
        String expected = "jdbc:postgresql://host:5433/database";
        
        String result = (String) convertToJdbcUrlMethod.invoke(databaseConfig, renderUrl);
        
        assertEquals(expected, result, "URL с портом должен быть правильно преобразован");
    }

    @Test
    void testAlreadyJdbcUrl() throws Exception {
        // Тестируем уже JDBC URL
        String jdbcUrl = "jdbc:postgresql://localhost:5432/test";
        
        String result = (String) convertToJdbcUrlMethod.invoke(databaseConfig, jdbcUrl);
        
        assertEquals(jdbcUrl, result, "JDBC URL должен остаться без изменений");
    }

    @Test
    void testNullUrl() throws Exception {
        // Тестируем null URL
        String result = (String) convertToJdbcUrlMethod.invoke(databaseConfig, (String) null);
        
        assertEquals("jdbc:postgresql://localhost:5432/deals_platform", result, "Должен вернуться localhost URL");
    }

    @Test
    void testEmptyUrl() throws Exception {
        // Тестируем пустой URL
        String result = (String) convertToJdbcUrlMethod.invoke(databaseConfig, "");
        
        assertEquals("jdbc:postgresql://localhost:5432/deals_platform", result, "Должен вернуться localhost URL");
    }

    @Test
    void testInvalidUrl() throws Exception {
        // Тестируем некорректный URL
        String invalidUrl = "invalid://url";
        
        String result = (String) convertToJdbcUrlMethod.invoke(databaseConfig, invalidUrl);
        
        assertEquals(invalidUrl, result, "Некорректный URL должен вернуться как есть");
    }
}
