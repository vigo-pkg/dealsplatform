# 🎯 Deals Platform - Платформа для заключения пари

**Deals Platform** - это полнофункциональная веб-платформа для заключения пари между сторонами с автоматическим управлением статусами, системой голосования и разрешения споров через независимых наблюдателей.

## 🚀 Быстрый старт

```bash
# 1. Запуск Backend
mvn spring-boot:run

# 2. Запуск Frontend (в новом терминале)
cd frontend && python3 -m http.server 3000
```

**Доступные адреса:**
- 🌐 **Frontend**: http://localhost:3000
- 🔧 **Backend API**: http://localhost:8080
- 📚 **API документация**: http://localhost:8080/swagger-ui/
- 🗄️ **База данных**: http://localhost:8080/h2-console

## 🔐 Тестовые пользователи

Система автоматически создает 4 тестовых пользователя для демонстрации:

| Email | Пароль | Роль |
|-------|--------|------|
| `alice@example.com` | `password123` | Создатель пари |
| `bob@example.com` | `password123` | Участник |
| `charlie@example.com` | `password123` | Участник |
| `diana@example.com` | `password123` | Наблюдатель |

## 🎯 Основные возможности

- ✅ **Регистрация и аутентификация** пользователей по email с JWT
- ✅ **Создание пари** с описанием, временем и продолжительностью
- ✅ **Автоматическое управление статусами** (OPEN → IN_PROGRESS → IMPLEMENTED → RESOLVED/CONFLICT)
- ✅ **Система голосования** участников (1 - выиграл, 0 - проиграл)
- ✅ **Независимые наблюдатели** для разрешения споров
- ✅ **REST API** с полной OpenAPI документацией
- ✅ **Современный UI** на Bootstrap 5

## 🏗️ Технологии

### Backend
- **Java 17** + **Spring Boot 3.x**
- **Spring Security** + **JWT**
- **Spring Data JPA** + **H2/PostgreSQL**
- **Maven** + **OpenAPI 3**

### Frontend
- **HTML5** + **CSS3** + **Vanilla JavaScript**
- **Bootstrap 5** + **Font Awesome**
- **Responsive дизайн**

## 📚 Документация

Вся подробная документация находится в папке [`docs/`](./docs/):

- 📖 **[README.md](./docs/README.md)** - полная документация системы
- 🚀 **[QUICK_START.md](./docs/QUICK_START.md)** - быстрый старт и решение проблем
- 🌐 **[CORS_GUIDE.md](./docs/CORS_GUIDE.md)** - CORS и сетевая безопасность
- 🔐 **[TEST_DATA.md](./docs/TEST_DATA.md)** - подробное описание тестовых данных
- 📝 **[DOCUMENTATION_UPDATE.md](./docs/DOCUMENTATION_UPDATE.md)** - описание обновлений
- 📚 **[README_DOCS.md](./docs/README_DOCS.md)** - структура документации
- 📖 **[HOW_TO_USE.md](./docs/HOW_TO_USE.md)** - как использовать документацию
- 📋 **[SUMMARY.md](./docs/SUMMARY.md)** - итоговое описание документации

## 🧪 Тестирование

### Автоматические тесты
```bash
# Selenium тесты
pip install -r requirements.txt
python test_selenium.py
```

### Ручное тестирование
1. Откройте http://localhost:3000
2. Войдите как `alice@example.com` / `password123`
3. Создайте новое пари
4. Протестируйте все функции

## 🔧 Конфигурация

### База данных
- **Разработка**: H2 in-memory (`jdbc:h2:mem:testdb`)
- **Продакшн**: PostgreSQL

### Переменные окружения
```yaml
# application.yml
spring:
  security:
    jwt:
      secret: dealsPlatformSecretKey2024VeryLongAndSecureKeyForJWTTokenGeneration
      expiration: 86400000 # 24 часа
```

## 🐳 Docker

```bash
# Сборка и запуск
docker-compose up -d

# Или отдельно
docker build -t deals-platform .
docker run -p 8080:8080 deals-platform
```

## 📁 Структура проекта

```
dealsPlatform/
├── docs/                          # 📚 Документация
│   ├── README.md                  # Полная документация
│   ├── QUICK_START.md             # Быстрый старт
│   ├── CORS_GUIDE.md              # CORS руководство
│   └── DOCUMENTATION_UPDATE.md    # Описание обновлений
├── src/                           # 🔧 Backend код
│   └── main/java/com/dealsplatform/
│       ├── config/                # Конфигурации
│       ├── controller/            # REST контроллеры
│       ├── service/               # Бизнес-логика
│       ├── entity/                # JPA сущности
│       └── security/              # JWT и безопасность
├── frontend/                      # 🎨 Frontend
│   ├── index.html                 # Главная страница
│   ├── app.js                     # JavaScript логика
│   └── styles.css                 # Стили
├── pom.xml                        # Maven конфигурация
├── Dockerfile                     # Docker образ
├── docker-compose.yml             # Docker Compose
└── test_selenium.py               # Автоматические тесты
```

## 🚨 Решение проблем

### Ошибка "Failed to fetch"
- ✅ **Решено**: CORS настроен в `SecurityConfig.java`
- 📖 **Подробности**: см. [CORS_GUIDE.md](./docs/CORS_GUIDE.md)

### Проблемы с базой данных
- ✅ **H2**: автоматически создается при запуске
- 📖 **PostgreSQL**: инструкции в [QUICK_START.md](./docs/QUICK_START.md)

### Порт занят
```bash
# Найти процесс на порту 3000
lsof -i :3000

# Остановить процесс
kill <PID>
```

## 🔮 Планы развития

- [ ] Уведомления в реальном времени
- [ ] Мобильная версия
- [ ] Система рейтингов
- [ ] Чат между участниками
- [ ] API для мобильных приложений

## 📞 Поддержка

### Документация
- 📚 **Полная документация**: [`docs/README.md`](./docs/README.md)
- 🚀 **Быстрый старт**: [`docs/QUICK_START.md`](./docs/QUICK_START.md)
- 🌐 **CORS руководство**: [`docs/CORS_GUIDE.md`](./docs/CORS_GUIDE.md)

### Ресурсы
- **Swagger UI**: http://localhost:8080/swagger-ui/
- **H2 Console**: http://localhost:8080/h2-console
- **GitHub Issues**: для сообщения о проблемах

### Тестовые данные
- **Пользователи**: 4 предустановленных аккаунта
- **Пари**: 3 примера для демонстрации
- **Автоматическое создание**: при каждом запуске

---

**Deals Platform** - создано с ❤️ для сообщества

*Версия: 1.0.0 | Документация: [`docs/`](./docs/)*
