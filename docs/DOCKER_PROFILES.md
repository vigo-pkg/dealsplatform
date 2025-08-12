# Docker профили Deals Platform

## Обзор

Deals Platform поддерживает два Docker профиля для разных сценариев использования:

- **Production** - с PostgreSQL базой данных
- **Development** - с H2 in-memory базой данных

## Production профиль (PostgreSQL)

### Файлы
- `Dockerfile` - основной образ
- `docker-compose.yml` - production stack
- `start_docker_production.sh` - скрипт запуска

### Запуск
```bash
# Автоматический запуск
./start_docker_production.sh

# Ручной запуск
docker-compose up --build -d
```

### Конфигурация
- **Профиль**: `postgres`
- **База данных**: PostgreSQL 15
- **Порт**: 8080
- **Сеть**: `deals_platform_network`

### Переменные окружения
```yaml
POSTGRES_DB: deals_platform
POSTGRES_USER: postgres
POSTGRES_PASSWORD: postgres
SPRING_PROFILES_ACTIVE: postgres
```

## Development профиль (H2)

### Файлы
- `Dockerfile.dev` - development образ
- `docker-compose.dev.yml` - development stack
- `start_docker_dev.sh` - скрипт запуска

### Запуск
```bash
# Автоматический запуск
./start_docker_dev.sh

# Ручной запуск
docker-compose -f docker-compose.dev.yml up --build -d
```

### Конфигурация
- **Профиль**: `h2`
- **База данных**: H2 in-memory
- **Порт**: 8080
- **Сеть**: `deals_platform_dev_network`

### Переменные окружения
```yaml
SPRING_PROFILES_ACTIVE: h2
```

## Архитектура

### Production Stack
```
┌─────────────────┐    ┌─────────────────┐
│   Backend       │    │   PostgreSQL    │
│   (Port 8080)   │◄──►│   (Port 5432)   │
│   Profile:      │    │   Container     │
│   postgres      │    │                 │
└─────────────────┘    └─────────────────┘
```

### Development Stack
```
┌─────────────────┐
│   Backend       │
│   (Port 8080)   │
│   Profile: h2   │
│   (H2 in-mem)   │
└─────────────────┘
```

## Команды управления

### Общие команды
```bash
# Просмотр статуса
docker-compose ps

# Просмотр логов
docker-compose logs -f backend

# Остановка
docker-compose down

# Пересборка
docker-compose up --build -d
```

### Development команды
```bash
# Просмотр статуса
docker-compose -f docker-compose.dev.yml ps

# Просмотр логов
docker-compose -f docker-compose.dev.yml logs -f backend

# Остановка
docker-compose -f docker-compose.dev.yml down

# Пересборка
docker-compose -f docker-compose.dev.yml up --build -d
```

## Переключение между профилями

### С Production на Development
```bash
# Остановить production
docker-compose down

# Запустить development
./start_docker_dev.sh
```

### С Development на Production
```bash
# Остановить development
docker-compose -f docker-compose.dev.yml down

# Запустить production
./start_docker_production.sh
```

## Troubleshooting

### Проблемы с PostgreSQL
```bash
# Проверить статус контейнера
docker-compose ps postgres

# Просмотр логов PostgreSQL
docker-compose logs postgres

# Подключение к базе данных
docker-compose exec postgres psql -U postgres -d deals_platform
```

### Проблемы с Backend
```bash
# Проверить статус контейнера
docker-compose ps backend

# Просмотр логов backend
docker-compose logs -f backend

# Перезапуск backend
docker-compose restart backend
```

### Очистка Docker
```bash
# Удаление всех контейнеров
docker-compose down --rmi all

# Удаление всех образов
docker system prune -a

# Удаление всех томов
docker volume prune
```

## Переменные окружения

### Переопределение настроек
Можно переопределить настройки через переменные окружения:

```bash
# Переопределить профиль
export SPRING_PROFILES_ACTIVE=postgres

# Переопределить настройки базы данных
export SPRING_DATASOURCE_URL=jdbc:postgresql://myhost:5432/mydb
export SPRING_DATASOURCE_USERNAME=myuser
export SPRING_DATASOURCE_PASSWORD=mypassword

# Запустить с переопределенными настройками
docker-compose up -d
```

## Мониторинг

### Проверка здоровья
```bash
# Проверить API
curl http://localhost:8080/actuator/health

# Проверить Swagger
curl http://localhost:8080/swagger-ui/
```

### Метрики
```bash
# Просмотр использования ресурсов
docker stats

# Просмотр логов в реальном времени
docker-compose logs -f
```

## Безопасность

### Production рекомендации
- Измените пароли PostgreSQL по умолчанию
- Используйте секреты для хранения паролей
- Ограничьте доступ к портам
- Настройте firewall правила

### Development рекомендации
- Используйте локальные порты
- Не экспортируйте порты наружу
- Используйте временные контейнеры
