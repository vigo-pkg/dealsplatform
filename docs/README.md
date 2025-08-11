# Deals Platform - Платформа для заключения пари

**Deals Platform** - это полнофункциональная веб-платформа для заключения пари между сторонами с автоматическим управлением статусами, системой голосования и разрешения споров через независимых наблюдателей.

## 🚀 Основные возможности

### 👥 Управление пользователями
- **Регистрация и аутентификация** по email с JWT токенами
- **Безопасное хранение паролей** с использованием BCrypt
- **Управление сессиями** через JWT (JSON Web Tokens)

### 🎯 Система пари (Deals)
- **Создание пари** с описанием, временем старта и продолжительностью
- **Автоматическое управление статусами**:
  - `OPEN` - пари открыто для присоединения участников
  - `IN_PROGRESS` - пари в процессе выполнения
  - `IMPLEMENTED` - время истекло, доступно голосование
  - `CONFLICT` - конфликт между участниками
  - `RESOLVED` - пари разрешено (наблюдателем или согласием сторон)

### 🔄 Жизненный цикл пари
1. **Создание** - пользователь создает пари со статусом `OPEN`
2. **Присоединение участников** - другие пользователи могут присоединиться
3. **Автоматический переход** в `IN_PROGRESS` когда все участники присоединились
4. **Истечение времени** - автоматический переход в `IMPLEMENTED`
5. **Голосование** - участники голосуют (1 - выиграл, 0 - проиграл)
6. **Разрешение** - автоматическое или через наблюдателя

### 👁️ Система наблюдателей
- **Независимые наблюдатели** могут присоединяться к пари
- **Разрешение споров** - наблюдатель выносит финальное решение при конфликтах
- **Автоматическое назначение** наблюдателей для разрешения споров

## 🏗️ Архитектура системы

### Backend (Java 17 + Spring Boot 3.x)
```
┌─────────────────────────────────────────────────────────────┐
│                    Deals Platform                          │
├─────────────────────────────────────────────────────────────┤
│  Controllers (REST API)                                    │
│  ├── AuthController (регистрация/аутентификация)           │
│  └── DealController (управление пари)                     │
├─────────────────────────────────────────────────────────────┤
│  Services (бизнес-логика)                                  │
│  ├── UserService (пользователи)                            │
│  └── DealService (пари + автоматизация)                   │
├─────────────────────────────────────────────────────────────┤
│  Repositories (доступ к данным)                            │
│  ├── UserRepository                                        │
│  ├── DealRepository                                        │
│  ├── DealParticipantRepository                             │
│  ├── DealObserverRepository                                │
│  └── DealVoteRepository                                    │
├─────────────────────────────────────────────────────────────┤
│  Security (JWT + Spring Security)                          │
│  ├── JwtTokenProvider                                      │
│  ├── JwtAuthenticationFilter                               │
│  └── CustomUserDetailsService                              │
├─────────────────────────────────────────────────────────────┤
│  Database (H2 in-memory / PostgreSQL)                     │
│  └── JPA Entities                                          │
└─────────────────────────────────────────────────────────────┘
```

### Frontend (HTML/CSS/JS + Bootstrap 5)
```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend                                │
├─────────────────────────────────────────────────────────────┤
│  index.html (основная страница)                            │
│  ├── Навигация и модальные окна                            │
│  ├── Формы регистрации/входа                               │
│  ├── Дашборд с пари                                        │
│  └── Детальный просмотр пари                               │
├─────────────────────────────────────────────────────────────┤
│  app.js (JavaScript логика)                                │
│  ├── API взаимодействие                                    │
│  ├── Управление состоянием                                 │
│  ├── Рендеринг контента                                    │
│  └── Обработка событий                                     │
├─────────────────────────────────────────────────────────────┤
│  styles.css (кастомные стили)                              │
│  ├── Адаптивный дизайн                                     │
│  ├── Анимации и переходы                                   │
│  └── Кастомные компоненты                                  │
└─────────────────────────────────────────────────────────────┘
```

## 🌐 CORS и Preflight запросы

### Что такое CORS?
**CORS (Cross-Origin Resource Sharing)** - это механизм безопасности браузера, который контролирует, может ли веб-страница делать запросы к другому домену/порту, отличному от того, с которого она была загружена.

### Проблема CORS в нашей системе
В нашем случае:
- **Frontend** работает на `http://localhost:3000`
- **Backend** работает на `http://localhost:8080`
- Браузер блокирует запросы между разными портами как "cross-origin"

### Как работает CORS в Deals Platform

#### 1. Конфигурация CORS в Spring Security
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Разрешенные источники (frontend)
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:3000", 
        "http://127.0.0.1:3000"
    ));
    
    // Разрешенные HTTP методы
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));
    
    // Разрешенные заголовки
    configuration.setAllowedHeaders(Arrays.asList("*"));
    
    // Разрешить передачу cookies и заголовков авторизации
    configuration.setAllowCredentials(true);
    
    // Заголовки, доступные для frontend
    configuration.setExposedHeaders(Arrays.asList("Authorization"));
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

#### 2. Preflight запросы (OPTIONS)
Когда frontend делает "сложный" запрос (например, POST с JSON), браузер сначала отправляет **preflight запрос** типа OPTIONS:

```
OPTIONS /api/deals HTTP/1.1
Origin: http://localhost:3000
Access-Control-Request-Method: POST
Access-Control-Request-Headers: Content-Type, Authorization
```

Backend отвечает с заголовками CORS:
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Expose-Headers: Authorization
Access-Control-Allow-Credentials: true
```

#### 3. Основной запрос
После успешного preflight браузер отправляет основной запрос:
```
POST /api/deals HTTP/1.1
Origin: http://localhost:3000
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
  "description": "Тестовое пари",
  "startTime": "2024-12-12T10:00:00",
  "durationMinutes": 60
}
```

### Типы CORS запросов

#### Простые запросы (Simple Requests)
- GET, HEAD, POST
- Простые заголовки (Accept, Accept-Language, Content-Language, Content-Type)
- Preflight НЕ требуется

#### Сложные запросы (Complex Requests)
- PUT, DELETE, PATCH
- Кастомные заголовки (Authorization, X-*)
- Content-Type: application/json
- **Требуют preflight запрос**

### Примеры CORS в нашей системе

#### ✅ Успешный запрос
```javascript
// Frontend (app.js)
fetch('http://localhost:8080/api/deals', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(dealData)
})
.then(response => response.json())
.then(data => console.log('Успех:', data));
```

#### ❌ Блокированный запрос (без CORS)
```
Access to fetch at 'http://localhost:8080/api/deals' from origin 
'http://localhost:3000' has been blocked by CORS policy: 
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

## 🔐 Безопасность

### JWT (JSON Web Tokens)
- **Структура**: `header.payload.signature`
- **Секретный ключ**: `dealsPlatformSecretKey2024VeryLongAndSecureKeyForJWTTokenGeneration`
- **Время жизни**: 24 часа
- **Алгоритм**: HS512 (HMAC SHA-512)

### Spring Security
- **CSRF защита**: отключена (не нужна для REST API)
- **CORS**: настроен для frontend
- **Аутентификация**: JWT-based
- **Авторизация**: на уровне endpoints

### Валидация данных
- **DTO валидация** с аннотациями `@Valid`
- **Бизнес-логика** в сервисах
- **SQL инъекции**: защищены через JPA/Hibernate

## 📊 База данных

### H2 (для разработки/тестирования)
- **URL**: `jdbc:h2:mem:testdb`
- **Пользователь**: `sa`
- **Пароль**: (пустой)
- **Консоль**: http://localhost:8080/h2-console

### PostgreSQL (для продакшена)
- **URL**: `jdbc:postgresql://localhost:5432/deals_platform`
- **Пользователь**: `postgres`
- **Пароль**: `postgres`

### Схема базы данных
```sql
-- Пользователи
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Пари
CREATE TABLE deals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('OPEN','IN_PROGRESS','IMPLEMENTED','CONFLICT','RESOLVED')),
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    end_time TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

-- Участники пари
CREATE TABLE deal_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deal_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    is_creator BOOLEAN,
    FOREIGN KEY (deal_id) REFERENCES deals(id),
    FOREIGN KEY (participant_id) REFERENCES users(id)
);

-- Наблюдатели
CREATE TABLE deal_observers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deal_id BIGINT NOT NULL,
    observer_id BIGINT NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    final_decision BOOLEAN,
    decision_at TIMESTAMP,
    FOREIGN KEY (deal_id) REFERENCES deals(id),
    FOREIGN KEY (observer_id) REFERENCES users(id)
);

-- Голоса
CREATE TABLE deal_votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    deal_id BIGINT NOT NULL,
    voter_id BIGINT NOT NULL,
    vote BOOLEAN NOT NULL,
    voted_at TIMESTAMP NOT NULL,
    FOREIGN KEY (deal_id) REFERENCES deals(id),
    FOREIGN KEY (voter_id) REFERENCES users(id)
);
```

## 🔐 Тестовые данные

Система автоматически создает тестовых пользователей и пари при каждом запуске через `DataInitializer.java`.

### Предустановленные пользователи
| Email | Пароль | Роль |
|-------|--------|------|
| `alice@example.com` | `password123` | Создатель пари |
| `bob@example.com` | `password123` | Участник |
| `charlie@example.com` | `password123` | Участник |
| `diana@example.com` | `password123` | Наблюдатель |

### Предустановленные пари
Система автоматически создает 3 тестовых пари с разными статусами:

#### Пари 1: "Кто больше отжиманий сделает за 1 минуту?"
- **Статус**: `OPEN` (открыто для присоединения)
- **Создатель**: `alice@example.com`
- **Время старта**: через 1 час от запуска
- **Продолжительность**: 60 минут

#### Пари 2: "Кто быстрее решит 10 математических задач?"
- **Статус**: `IN_PROGRESS` (в процессе выполнения)
- **Создатель**: `bob@example.com`
- **Участники**: `bob@example.com`, `charlie@example.com`
- **Время старта**: 30 минут назад
- **Продолжительность**: 120 минут

#### Пари 3: "Кто больше слов напишет за 5 минут?"
- **Статус**: `IMPLEMENTED` (завершено, доступно голосование)
- **Создатель**: `diana@example.com`
- **Участники**: `diana@example.com`, `alice@example.com`
- **Наблюдатель**: `bob@example.com`
- **Время старта**: 2 часа назад
- **Продолжительность**: 5 минут

### Автоматическое создание
- **При запуске**: система проверяет, есть ли данные в базе
- **Если база пуста**: автоматически создаются тестовые пользователи и пари
- **При каждом перезапуске**: данные пересоздаются (H2 in-memory)

## 🚀 Запуск системы

### 1. Запуск Backend
```bash
# Компиляция
mvn clean compile

# Запуск
mvn spring-boot:run
```

### 2. Запуск Frontend
```bash
cd frontend
python3 -m http.server 3000
```

### 3. Проверка работы
- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **API документация**: http://localhost:8080/swagger-ui/
- **H2 консоль**: http://localhost:8080/h2-console

## 🧪 Тестирование

### Автоматические тесты
```bash
# Selenium тесты
pip install -r requirements.txt
python test_selenium.py
```

### Postman коллекция
- **Файл**: `Deals_Platform_API.postman_collection.json`
- **Содержит**: все API endpoints с примерами

### Ручное тестирование
1. **Регистрация**: http://localhost:3000
2. **Создание пари**: через форму на главной странице
3. **Присоединение**: к существующим пари
4. **Голосование**: после истечения времени

## 📚 API Endpoints

### Аутентификация
- `POST /api/auth/register` - регистрация пользователя
- `POST /api/auth/login` - вход в систему

### Пари
- `GET /api/deals` - список всех пари
- `GET /api/deals/{id}` - детали пари
- `POST /api/deals` - создание пари
- `GET /api/deals/status/{status}` - пари по статусу

### Участие
- `POST /api/deals/{id}/join` - присоединиться как участник
- `POST /api/deals/{id}/observe` - присоединиться как наблюдатель

### Голосование и решения
- `POST /api/deals/{id}/vote` - проголосовать
- `POST /api/deals/{id}/observer-decision` - решение наблюдателя

## 🔧 Конфигурация

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
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
      expiration: 86400000 # 24 часа

server:
  port: 8080

logging:
  level:
    com.dealsplatform: DEBUG
    org.springframework.security: DEBUG
```

### Переменные окружения
- `SPRING_PROFILES_ACTIVE` - активный профиль
- `JWT_SECRET` - секретный ключ для JWT
- `JWT_EXPIRATION` - время жизни токена

## 🐳 Docker

### Backend
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/deals-platform-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Запуск с Docker Compose
```bash
docker-compose up -d
```

## 📈 Мониторинг и логирование

### Логирование
- **Backend**: Spring Boot logging с уровнем DEBUG
- **Frontend**: Console.log для отладки
- **База данных**: Hibernate SQL queries

### Метрики
- **Время ответа API**: через Spring Boot Actuator
- **Статистика пари**: количество по статусам
- **Активность пользователей**: логи аутентификации

## 🚨 Устранение неполадок

### CORS ошибки
```
Access to fetch at 'http://localhost:8080/api/deals' from origin 
'http://localhost:3000' has been blocked by CORS policy
```
**Решение**: Проверить CORS конфигурацию в `SecurityConfig.java`

### JWT ошибки
```
JWT signature does not match locally computed signature
```
**Решение**: Проверить секретный ключ в `application.yml`

### База данных
```
Cannot load driver class: org.h2.Driver
```
**Решение**: Проверить зависимости в `pom.xml`

## 🔮 Планы развития

### Краткосрочные (1-2 месяца)
- [ ] Добавление уведомлений
- [ ] Улучшение UI/UX
- [ ] Мобильная версия

### Среднесрочные (3-6 месяцев)
- [ ] Система рейтингов
- [ ] Чат между участниками
- [ ] API для мобильных приложений

### Долгосрочные (6+ месяцев)
- [ ] Машинное обучение для разрешения споров
- [ ] Блокчейн интеграция
- [ ] Мультиязычность

## 📞 Поддержка

### Документация
- **README.md** - основная документация
- **QUICK_START.md** - быстрый старт
- **CORS_GUIDE.md** - подробное руководство по CORS и сетевой безопасности
- **Swagger UI** - интерактивная API документация

### Контакты
- **Issues**: GitHub Issues
- **Discussions**: GitHub Discussions
- **Wiki**: GitHub Wiki

---

**Deals Platform** - создано с ❤️ для сообщества

*Версия: 1.0.0 | Последнее обновление: Август 2024*
