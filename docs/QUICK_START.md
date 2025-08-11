# 🚀 Быстрый старт Deals Platform

Этот документ поможет вам быстро запустить Deals Platform и начать работу с системой.

## 📋 Предварительные требования

### Системные требования
- **Java 17+** (OpenJDK или Oracle JDK)
- **Maven 3.6+**
- **Python 3.7+** (для запуска frontend)
- **Браузер** (Chrome, Firefox, Safari)

### Проверка установки
```bash
# Проверка Java
java -version

# Проверка Maven
mvn -version

# Проверка Python
python3 --version
```

## 🏃‍♂️ Быстрый запуск

### 1. Клонирование и подготовка
```bash
git clone <repository-url>
cd dealsPlatform

# Очистка и компиляция
mvn clean compile
```

### 2. Запуск Backend
```bash
# Запуск Spring Boot приложения
mvn spring-boot:run
```

**Ожидаемый результат:**
```
Started DealsPlatformApplication in X.XX seconds
Tomcat started on port 8080 (http)
H2 console available at '/h2-console'
```

### 3. Запуск Frontend
```bash
# В новом терминале
cd frontend
python3 -m http.server 3000
```

**Ожидаемый результат:**
```
Serving HTTP on :: port 3000 (http://[::]:3000/)
```

### 4. Проверка работоспособности
- **Backend API**: http://localhost:8080 ✅
- **Frontend**: http://localhost:3000 ✅
- **Swagger UI**: http://localhost:8080/swagger-ui/ ✅
- **H2 Console**: http://localhost:8080/h2-console ✅

## 🧪 Тестирование системы

### Автоматические тесты
```bash
# Установка Python зависимостей
pip install -r requirements.txt

# Запуск Selenium тестов
python test_selenium.py
```

### Ручное тестирование
1. **Откройте**: http://localhost:3000
2. **Зарегистрируйтесь** с новым email
3. **Создайте пари** через форму
4. **Присоединитесь** к существующим пари
5. **Протестируйте** все функции

## 🔐 Тестовые данные

### Предустановленные пользователи
Система автоматически создает 4 тестовых пользователя:

| Email | Пароль | Роль | Описание |
|-------|--------|------|----------|
| `alice@example.com` | `password123` | Создатель пари | Основной пользователь для демонстрации |
| `bob@example.com` | `password123` | Участник | Участник пари и наблюдатель |
| `charlie@example.com` | `password123` | Участник | Участник пари |
| `diana@example.com` | `password123` | Наблюдатель | Создатель пари и наблюдатель |

### Предустановленные пари
Система создает 3 тестовых пари с разными статусами для демонстрации всех возможностей:

#### Пари 1: "Кто больше отжиманий сделает за 1 минуту?"
- **Статус**: `OPEN` - открыто для присоединения участников
- **Создатель**: `alice@example.com`
- **Время старта**: через 1 час от запуска системы
- **Продолжительность**: 60 минут
- **Назначение**: демонстрация присоединения к пари

#### Пари 2: "Кто быстрее решит 10 математических задач?"
- **Статус**: `IN_PROGRESS` - в процессе выполнения
- **Создатель**: `bob@example.com`
- **Участники**: `bob@example.com`, `charlie@example.com`
- **Время старта**: 30 минут назад
- **Продолжительность**: 120 минут
- **Назначение**: демонстрация пари в процессе

#### Пари 3: "Кто больше слов напишет за 5 минут?"
- **Статус**: `IMPLEMENTED` - завершено, доступно голосование
- **Создатель**: `diana@example.com`
- **Участники**: `diana@example.com`, `alice@example.com`
- **Наблюдатель**: `bob@example.com`
- **Время старта**: 2 часа назад
- **Продолжительность**: 5 минут
- **Назначение**: демонстрация голосования и наблюдателей

## 🌐 CORS и сетевая конфигурация

### Что такое CORS?
**CORS (Cross-Origin Resource Sharing)** - механизм безопасности браузера, который контролирует cross-origin запросы.

### В нашей системе
- **Frontend**: `http://localhost:3000`
- **Backend**: `http://localhost:8080`
- **Проблема**: Разные порты = cross-origin

### Решение
CORS настроен в `SecurityConfig.java`:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:3000", 
        "http://127.0.0.1:3000"
    ));
    // ... остальная конфигурация
}
```

## 🚨 Решение проблем

### Проблема: "Failed to fetch"
**Симптомы:**
- Ошибка в консоли браузера
- Не удается создать пари
- API запросы не проходят

**Решение:**
1. Проверить, что backend запущен на порту 8080
2. Проверить CORS конфигурацию
3. Убедиться, что frontend на порту 3000

### Проблема: "Cannot load driver class: org.h2.Driver"
**Симптомы:**
- Backend не запускается
- Ошибка при инициализации базы данных

**Решение:**
```bash
# Проверить зависимости в pom.xml
# Убедиться, что H2 dependency присутствует
mvn clean compile
```

### Проблема: "Connection refused"
**Симптомы:**
- Backend не может подключиться к базе данных
- Ошибки в логах Spring Boot

**Решение:**
1. Для H2: проверить конфигурацию в `application.yml`
2. Для PostgreSQL: убедиться, что база запущена

### Проблема: "Port already in use"
**Симптомы:**
- Ошибка при запуске frontend
- "Address already in use"

**Решение:**
```bash
# Найти процесс на порту 3000
lsof -i :3000

# Остановить процесс
kill <PID>

# Или использовать другой порт
python3 -m http.server 3001
```

## 🔧 Конфигурация

### Backend (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
  security:
    jwt:
      secret: dealsPlatformSecretKey2024VeryLongAndSecureKeyForJWTTokenGeneration
      expiration: 86400000

server:
  port: 8080
```

### Frontend (app.js)
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
const FRONTEND_URL = 'http://localhost:3000';
```

## 📊 Мониторинг

### Логи Backend
```bash
# Просмотр логов в реальном времени
tail -f logs/spring-boot.log

# Или через Maven
mvn spring-boot:run | tee backend.log
```

### Логи Frontend
```bash
# Python HTTP сервер логирует все запросы
# Проверяйте консоль браузера для JavaScript ошибок
```

### База данных
- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (пустой)

## 🧹 Очистка и перезапуск

### Полная перезагрузка
```bash
# 1. Остановить все процессы
pkill -f "spring-boot:run"
pkill -f "python3 -m http.server"

# 2. Очистить Maven
mvn clean

# 3. Пересобрать
mvn compile

# 4. Запустить заново
mvn spring-boot:run &
cd frontend && python3 -m http.server 3000 &
```

### Очистка базы данных
```bash
# H2 автоматически очищается при перезапуске
# Для PostgreSQL:
psql -U postgres -d deals_platform -c "TRUNCATE TABLE deal_votes, deal_observers, deal_participants, deals, users RESTART IDENTITY CASCADE;"
```

## 📚 Полезные команды

### Maven
```bash
# Очистка и компиляция
mvn clean compile

# Запуск с профилем
mvn spring-boot:run -Dspring.profiles.active=dev

# Сборка JAR
mvn clean package

# Запуск тестов
mvn test
```

### Система
```bash
# Проверка портов
lsof -i :8080
lsof -i :3000

# Проверка процессов Java
ps aux | grep java

# Проверка процессов Python
ps aux | grep python
```

### Docker (опционально)
```bash
# Запуск PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_DB=deals_platform \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:15

# Сборка и запуск backend
docker build -t deals-platform .
docker run -d --name backend -p 8080:8080 deals-platform
```

## 🎯 Следующие шаги

После успешного запуска:

1. **Изучите API** через Swagger UI
2. **Протестируйте все функции** через frontend
3. **Запустите Selenium тесты** для проверки
4. **Изучите код** для понимания архитектуры
5. **Внесите изменения** и протестируйте

## 📞 Получение помощи

### Документация
- **README.md** - полная документация
- **Swagger UI** - интерактивная API документация
- **Код** - хорошо документирован с комментариями

### Логи и отладка
- Проверяйте консоль браузера
- Изучайте логи Spring Boot
- Используйте H2 Console для базы данных

### Сообщество
- Создавайте Issues в репозитории
- Описывайте проблемы подробно
- Прикладывайте логи и скриншоты

---

**Удачи! 🎉**

*Deals Platform - создано с ❤️ для сообщества*
