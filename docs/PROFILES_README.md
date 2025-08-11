# Профили конфигурации Deals Platform

## Обзор

Deals Platform поддерживает несколько профилей конфигурации для разных сценариев использования:

- **default** - общие настройки (без базы данных)
- **h2** - H2 in-memory база данных (для разработки)
- **postgres** - PostgreSQL база данных (для production)

## Структура файлов

```
src/main/resources/
├── application.yml          # Общие настройки (JWT, порт, логирование)
├── application-h2.yml       # H2 профиль
└── application-postgres.yml # PostgreSQL профиль
```

## Профили

### 1. Default профиль (`application.yml`)

Содержит общие настройки, которые применяются ко всем профилям:

- JWT конфигурация
- Порт сервера (8080)
- Уровни логирования
- Spring Security настройки

### 2. H2 профиль (`application-h2.yml`)

**Назначение**: Разработка и тестирование

**Особенности**:
- In-memory база данных
- Автоматическое создание/удаление таблиц при каждом запуске
- H2 консоль доступна по адресу `/h2-console`
- Быстрый старт для разработки

**Запуск**:
```bash
# Автоматически активируется при запуске без профиля
./start_backend.sh

# Или явно указать профиль
mvn spring-boot:run -Dspring.profiles.active=h2
```

### 3. PostgreSQL профиль (`application-postgres.yml`)

**Назначение**: Production и продакшн-подобная разработка

**Особенности**:
- Постоянное хранение данных
- Оптимизированные настройки Hibernate
- Batch операции для производительности
- Логирование SQL запросов

**Запуск**:
```bash
./start_backend_postgres.sh

# Или вручную
mvn spring-boot:run -Dspring.profiles.active=postgres
```

## Запуск с разными профилями

### H2 (по умолчанию)
```bash
./start_backend.sh
```

### PostgreSQL
```bash
./start_backend_postgres.sh
```

### Ручной запуск с профилем
```bash
# H2
mvn spring-boot:run -Dspring.profiles.active=h2

# PostgreSQL
mvn spring-boot:run -Dspring.profiles.active=postgres

# Несколько профилей
mvn spring-boot:run -Dspring.profiles.active=h2,dev
```

## Требования для PostgreSQL

### Установка PostgreSQL
```bash
# macOS
brew install postgresql
brew services start postgresql

# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### Создание базы данных
```bash
# Подключиться к PostgreSQL
psql -U postgres

# Создать базу данных
CREATE DATABASE deals_platform;

# Создать пользователя (опционально)
CREATE USER deals_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE deals_platform TO deals_user;
```

### Настройка аутентификации
Отредактируйте `pg_hba.conf` для настройки метода аутентификации:

```
# Локальные подключения
local   all             postgres                                peer
local   all             all                                     peer
host    all             all             127.0.0.1/32            md5
host    all             all             ::1/128                 md5
```

## Переменные окружения

Можно переопределить настройки через переменные окружения:

```bash
# Активировать профиль
export SPRING_PROFILES_ACTIVE=postgres

# Переопределить настройки базы данных
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/my_db
export SPRING_DATASOURCE_USERNAME=my_user
export SPRING_DATASOURCE_PASSWORD=my_password

# Запустить приложение
mvn spring-boot:run
```

## Миграция между профилями

### С H2 на PostgreSQL
1. Остановить приложение
2. Запустить с профилем postgres: `./start_backend_postgres.sh`
3. Данные будут автоматически созданы в PostgreSQL

### С PostgreSQL на H2
1. Остановить приложение
2. Запустить с профилем h2: `./start_backend.sh`
3. Данные будут созданы заново в памяти

## Мониторинг и логирование

### H2 профиль
- H2 консоль: http://localhost:8080/h2-console
- Логи SQL запросов в консоли

### PostgreSQL профиль
- Детальные логи SQL с параметрами
- Мониторинг производительности через Hibernate статистику

## Troubleshooting

### Ошибка подключения к PostgreSQL
```bash
# Проверить статус сервиса
brew services list | grep postgresql

# Проверить подключение
psql -h localhost -U postgres -d deals_platform

# Проверить права пользователя
sudo -u postgres psql -c "\du"
```

### Ошибка создания базы данных
```bash
# Создать базу вручную
createdb -h localhost -U postgres deals_platform

# Проверить существующие базы
psql -h localhost -U postgres -l
```

### Проблемы с профилями
```bash
# Проверить активный профиль в логах
grep "The following.*profile is active" logs/application.log

# Принудительно установить профиль
export SPRING_PROFILES_ACTIVE=h2
```
