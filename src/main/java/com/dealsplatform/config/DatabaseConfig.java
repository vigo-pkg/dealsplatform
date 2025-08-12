package com.dealsplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url:}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${spring.datasource.driver-class-name:}")
    private String driverClassName;

    /**
     * Конфигурация DataSource для Render.com
     * Преобразует connectionString в правильный JDBC URL
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "postgres")
    public DataSource dataSource() {
        System.out.println("🚀 Создание DataSource для профиля postgres");
        System.out.println("📊 Исходные параметры:");
        System.out.println("   URL: " + dataSourceUrl);
        System.out.println("   Username: " + username);
        System.out.println("   Driver: " + driverClassName);
        
        HikariDataSource dataSource = new HikariDataSource();
        
        // Преобразуем URL от Render.com в JDBC формат
        String jdbcUrl = convertToJdbcUrl(dataSourceUrl);
        System.out.println("🔗 Итоговый JDBC URL: " + jdbcUrl);
        
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        
        // Настройки пула соединений
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setLeakDetectionThreshold(60000);
        
        // Дополнительные настройки для PostgreSQL
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource.addDataSourceProperty("useLocalSessionState", "true");
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");
        dataSource.addDataSourceProperty("cacheResultSetMetadata", "true");
        dataSource.addDataSourceProperty("cacheServerConfiguration", "true");
        dataSource.addDataSourceProperty("elideSetAutoCommits", "true");
        dataSource.addDataSourceProperty("maintainTimeStats", "false");
        
        System.out.println("✅ DataSource создан успешно");
        return dataSource;
    }

    /**
     * Преобразует connectionString от Render.com в JDBC URL
     * 
     * @param connectionString строка подключения от Render.com
     * @return JDBC URL для Spring Boot
     */
    private String convertToJdbcUrl(String connectionString) {
        System.out.println("🔍 Преобразование connectionString: " + connectionString);
        
        if (connectionString == null || connectionString.trim().isEmpty()) {
            System.out.println("⚠️ connectionString пустой, используем localhost");
            return "jdbc:postgresql://localhost:5432/deals_platform";
        }
        
        // Если уже JDBC URL - возвращаем как есть
        if (connectionString.startsWith("jdbc:")) {
            System.out.println("✅ Уже JDBC URL: " + connectionString);
            return connectionString;
        }
        
        // Преобразуем postgresql://user:pass@host/db в jdbc:postgresql://host:5432/db
        if (connectionString.startsWith("postgresql://")) {
            try {
                System.out.println("🔄 Преобразуем postgresql:// в jdbc:postgresql://");
                
                // Убираем postgresql://
                String withoutProtocol = connectionString.substring(13);
                System.out.println("📝 Без протокола: " + withoutProtocol);
                
                // Находим @ для разделения credentials и host
                int atIndex = withoutProtocol.indexOf('@');
                if (atIndex != -1) {
                    // Извлекаем host и database (после @)
                    String hostAndDb = withoutProtocol.substring(atIndex + 1);
                    System.out.println("🌐 Host и database: " + hostAndDb);
                    
                    // Проверяем, есть ли порт
                    if (!hostAndDb.contains(":")) {
                        // Добавляем стандартный порт PostgreSQL
                        hostAndDb = hostAndDb.replace("/", ":5432/");
                        System.out.println("🔌 Добавлен порт 5432: " + hostAndDb);
                    }
                    
                    String result = "jdbc:postgresql://" + hostAndDb;
                    System.out.println("✅ Результат преобразования: " + result);
                    return result;
                } else {
                    System.out.println("❌ Не найден символ @ в connectionString");
                }
            } catch (Exception e) {
                // В случае ошибки возвращаем исходную строку
                System.err.println("💥 Ошибка преобразования connectionString: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Если не удалось преобразовать, возвращаем как есть
        System.out.println("⚠️ Не удалось преобразовать, возвращаем как есть: " + connectionString);
        return connectionString;
    }
}
