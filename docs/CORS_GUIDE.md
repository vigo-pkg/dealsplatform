# 🌐 CORS и сетевая безопасность в Deals Platform

Подробное руководство по пониманию и настройке CORS (Cross-Origin Resource Sharing) в системе Deals Platform.

## 📖 Что такое CORS?

### Определение
**CORS (Cross-Origin Resource Sharing)** - это стандарт безопасности веб-браузеров, который определяет, может ли веб-страница делать запросы к другому домену, порту или протоколу, отличному от того, с которого она была загружена.

### Зачем нужен CORS?
- **Безопасность**: Предотвращает злонамеренные сайты от доступа к ресурсам других доменов
- **Контроль доступа**: Позволяет серверам контролировать, кто может обращаться к их API
- **Защита пользователей**: Блокирует неавторизованные cross-origin запросы

## 🎯 Проблема CORS в Deals Platform

### Архитектура системы
```
┌─────────────────┐    HTTP запросы    ┌─────────────────┐
│   Frontend      │ ──────────────────► │    Backend      │
│ localhost:3000  │                    │ localhost:8080  │
└─────────────────┘                    └─────────────────┘
```

### Почему возникает проблема?
- **Frontend**: работает на порту 3000 (`http://localhost:3000`)
- **Backend**: работает на порту 8080 (`http://localhost:8080`)
- **Браузер**: считает это cross-origin запросом и блокирует

### Пример ошибки
```javascript
// В консоли браузера
Access to fetch at 'http://localhost:8080/api/deals' from origin 
'http://localhost:3000' has been blocked by CORS policy: 
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

## 🔧 Решение CORS в Spring Boot

### 1. Конфигурация CORS в SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
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
}
```

### 2. Альтернативная конфигурация через @CrossOrigin

```java
@RestController
@RequestMapping("/api/deals")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class DealController {
    // ... методы контроллера
}
```

### 3. Глобальная конфигурация через WebMvcConfigurer

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:3000", "http://127.0.0.1:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .exposedHeaders("Authorization");
    }
}
```

## 🔄 Preflight запросы (OPTIONS)

### Что такое Preflight?
**Preflight** - это предварительный HTTP запрос типа OPTIONS, который браузер автоматически отправляет перед "сложными" cross-origin запросами.

### Когда отправляется Preflight?
- **HTTP методы**: PUT, DELETE, PATCH
- **Заголовки**: Authorization, Content-Type: application/json, X-*
- **Запросы**: с credentials или custom headers

### Пример Preflight запроса

#### 1. Браузер отправляет OPTIONS
```
OPTIONS /api/deals HTTP/1.1
Host: localhost:8080
Origin: http://localhost:3000
Access-Control-Request-Method: POST
Access-Control-Request-Headers: Content-Type, Authorization
```

#### 2. Backend отвечает с CORS заголовками
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Expose-Headers: Authorization
Access-Control-Allow-Credentials: true
```

#### 3. Браузер отправляет основной запрос
```
POST /api/deals HTTP/1.1
Host: localhost:8080
Origin: http://localhost:3000
Content-Type: application/json
Authorization: Bearer <jwt-token>

{
  "description": "Тестовое пари",
  "startTime": "2024-12-12T10:00:00",
  "durationMinutes": 60
}
```

## 📋 Типы CORS запросов

### Простые запросы (Simple Requests)
**Preflight НЕ требуется**

#### Условия:
- **Методы**: GET, HEAD, POST
- **Заголовки**: только простые (Accept, Accept-Language, Content-Language, Content-Type)
- **Content-Type**: application/x-www-form-urlencoded, multipart/form-data, text/plain

#### Пример:
```javascript
// Простой GET запрос
fetch('http://localhost:8080/api/deals')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Сложные запросы (Complex Requests)
**Preflight ТРЕБУЕТСЯ**

#### Условия:
- **Методы**: PUT, DELETE, PATCH
- **Заголовки**: Authorization, X-*, custom headers
- **Content-Type**: application/json

#### Пример:
```javascript
// Сложный POST запрос с JSON и Authorization
fetch('http://localhost:8080/api/deals', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(dealData)
})
.then(response => response.json())
.then(data => console.log(data));
```

## 🛡️ Безопасность CORS

### Важные заголовки

#### Access-Control-Allow-Origin
```
Access-Control-Allow-Origin: http://localhost:3000
```
- **Значение**: конкретный домен или `*` (не рекомендуется для production)
- **Безопасность**: указывает, какие домены могут обращаться к API

#### Access-Control-Allow-Credentials
```
Access-Control-Allow-Credentials: true
```
- **Значение**: `true` разрешает передачу cookies и Authorization заголовков
- **Важно**: если `true`, то `Access-Control-Allow-Origin` не может быть `*`

#### Access-Control-Allow-Headers
```
Access-Control-Allow-Headers: Content-Type, Authorization
```
- **Значение**: список разрешенных заголовков
- **Безопасность**: контролирует, какие заголовки может отправлять клиент

#### Access-Control-Expose-Headers
```
Access-Control-Expose-Headers: Authorization
```
- **Значение**: заголовки, доступные для JavaScript на frontend
- **Пример**: делает заголовок Authorization доступным для `response.headers.get('Authorization')`

### Рекомендации по безопасности

#### 1. Ограничение источников
```java
// ❌ Небезопасно
configuration.setAllowedOriginPatterns(Arrays.asList("*"));

// ✅ Безопасно
configuration.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:3000",
    "https://yourdomain.com"
));
```

#### 2. Ограничение методов
```java
// ❌ Слишком широко
configuration.setAllowedMethods(Arrays.asList("*"));

// ✅ Контролируемо
configuration.setAllowedMethods(Arrays.asList(
    "GET", "POST", "PUT", "DELETE", "OPTIONS"
));
```

#### 3. Ограничение заголовков
```java
// ❌ Слишком широко
configuration.setAllowedHeaders(Arrays.asList("*"));

// ✅ Контролируемо
configuration.setAllowedHeaders(Arrays.asList(
    "Content-Type", "Authorization", "Accept"
));
```

## 🔍 Отладка CORS проблем

### 1. Проверка в браузере

#### Chrome DevTools
1. Откройте **Network** tab
2. Сделайте запрос к API
3. Найдите **OPTIONS** запрос (preflight)
4. Проверьте **Response Headers** на CORS заголовки

#### Firefox DevTools
1. Откройте **Network** tab
2. Фильтруйте по **XHR** запросам
3. Проверьте **Response** на CORS заголовки

### 2. Проверка в консоли

#### Frontend ошибки
```javascript
// Типичные CORS ошибки
Access to fetch at 'http://localhost:8080/api/deals' from origin 
'http://localhost:3000' has been blocked by CORS policy

// Проверка в консоли
console.log('Origin:', window.location.origin);
console.log('API URL:', 'http://localhost:8080/api/deals');
```

#### Backend логи
```bash
# Проверка CORS запросов в логах Spring Boot
grep "CORS" logs/spring-boot.log

# Или в реальном времени
tail -f logs/spring-boot.log | grep "CORS"
```

### 3. Тестирование CORS

#### curl для preflight
```bash
# Тест OPTIONS запроса
curl -v -X OPTIONS \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type, Authorization" \
  http://localhost:8080/api/deals
```

#### curl для основного запроса
```bash
# Тест POST запроса
curl -v -X POST \
  -H "Origin: http://localhost:3000" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"description":"test"}' \
  http://localhost:8080/api/deals
```

## 🚀 Production настройки

### 1. Ограничение доменов
```java
@Profile("production")
@Bean
public CorsConfigurationSource productionCorsConfiguration() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Только production домены
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "https://yourdomain.com",
        "https://www.yourdomain.com"
    ));
    
    // Ограниченные методы
    configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
    
    // Только необходимые заголовки
    configuration.setAllowedHeaders(Arrays.asList(
        "Content-Type", "Authorization"
    ));
    
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(Arrays.asList("Authorization"));
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 2. Переменные окружения
```yaml
# application-production.yml
spring:
  security:
    cors:
      allowed-origins: ${CORS_ALLOWED_ORIGINS:https://yourdomain.com}
      allowed-methods: ${CORS_ALLOWED_METHODS:GET,POST}
      allowed-headers: ${CORS_ALLOWED_HEADERS:Content-Type,Authorization}
```

### 3. Nginx reverse proxy
```nginx
# nginx.conf
server {
    listen 80;
    server_name yourdomain.com;
    
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        
        # CORS заголовки
        add_header Access-Control-Allow-Origin "https://yourdomain.com";
        add_header Access-Control-Allow-Methods "GET, POST, OPTIONS";
        add_header Access-Control-Allow-Headers "Content-Type, Authorization";
        add_header Access-Control-Allow-Credentials "true";
    }
}
```

## 📚 Полезные ресурсы

### Документация
- [MDN Web Docs - CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Spring Security Reference - CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html)
- [W3C CORS Specification](https://www.w3.org/TR/cors/)

### Инструменты
- [CORS Tester](https://www.test-cors.org/)
- [Postman](https://www.postman.com/) - для тестирования API
- [curl](https://curl.se/) - для командной строки

### Примеры кода
- [Spring Boot CORS Examples](https://spring.io/guides/gs/rest-service-cors/)
- [JavaScript Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)

## 🎯 Чек-лист настройки CORS

### ✅ Backend
- [ ] CORS конфигурация в SecurityConfig
- [ ] Правильные allowed origins
- [ ] Поддержка preflight запросов (OPTIONS)
- [ ] Настройка заголовков Authorization
- [ ] allowCredentials = true

### ✅ Frontend
- [ ] Правильный API URL
- [ ] Корректные заголовки в fetch запросах
- [ ] Обработка CORS ошибок
- [ ] Тестирование всех типов запросов

### ✅ Тестирование
- [ ] Preflight запросы работают
- [ ] Основные запросы проходят
- [ ] Authorization заголовки передаются
- [ ] CORS ошибки отсутствуют в консоли

---

**CORS Guide** - часть документации Deals Platform

*Создано с ❤️ для понимания сетевой безопасности*
