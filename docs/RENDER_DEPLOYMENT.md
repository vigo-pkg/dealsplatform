# Развертывание Deals Platform на Render.com

## Обзор

Render.com - это облачная платформа для развертывания веб-приложений и баз данных. Deals Platform может быть развернута на Render.com с автоматическим использованием PostgreSQL сервиса.

## Преимущества Render.com

- ✅ **Автоматическое развертывание** из Git репозитория
- ✅ **Встроенный PostgreSQL** сервис
- ✅ **SSL сертификаты** автоматически
- ✅ **Масштабирование** по требованию
- ✅ **Мониторинг** и логи
- ✅ **CDN** для статических файлов

## Структура развертывания

```
┌─────────────────────────────────────────────────────────┐
│                    Render.com                          │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────┐    ┌─────────────────────────────┐ │
│  │   Web Service   │    │     PostgreSQL Service      │ │
│  │   (Backend)     │◄──►│     (Database)              │ │
│  │   Port: $PORT   │    │     Region: Frankfurt       │ │
│  │   Profile:      │    │     Plan: Starter           │ │
│  │   postgres      │    │                             │ │
│  └─────────────────┘    └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Файлы конфигурации

### 1. `render.yaml`
Основной файл конфигурации для Render.com:
```yaml
services:
  - type: web
    name: deals-platform-backend
    env: docker
    plan: starter
    region: frankfurt
    buildCommand: docker build -t deals-platform .
    startCommand: docker run -p $PORT:8080 deals-platform
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: postgres
      - key: SPRING_DATASOURCE_URL
        fromDatabase:
          name: deals-platform-postgres
          property: connectionString
      # ... другие переменные
```

### 2. `env.example`
Пример переменных окружения:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/database
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
SPRING_PROFILES_ACTIVE=postgres
```

## Пошаговое развертывание

### Шаг 1: Подготовка репозитория

1. **Убедитесь, что все файлы закоммичены:**
   ```bash
   git add .
   git commit -m "Add Render.com deployment configuration"
   git push origin main
   ```

2. **Проверьте наличие файлов:**
   - `render.yaml` ✅
   - `Dockerfile` ✅
   - `env.example` ✅

### Шаг 2: Создание Render.com аккаунта

1. Перейдите на [render.com](https://render.com)
2. Зарегистрируйтесь или войдите в аккаунт
3. Подключите ваш GitHub репозиторий

### Шаг 3: Создание сервиса

1. **Нажмите "New +" → "Web Service"**
2. **Подключите репозиторий:**
   - Выберите `vigo-pkg/dealsplatform`
   - Выберите ветку `main`

3. **Настройте сервис:**
   - **Name**: `deals-platform-backend`
   - **Environment**: `Docker`
   - **Region**: `Frankfurt` (или ближайший к вам)
   - **Branch**: `main`
   - **Build Command**: `docker build -t deals-platform .`
   - **Start Command**: `docker run -p $PORT:8080 deals-platform`

4. **Настройте переменные окружения:**
   ```bash
   SPRING_PROFILES_ACTIVE=postgres
   JWT_SECRET=your-secret-key-here
   JWT_EXPIRATION=86400000
   ```

### Шаг 4: Создание PostgreSQL сервиса

1. **Нажмите "New +" → "PostgreSQL"**
2. **Настройте базу данных:**
   - **Name**: `deals-platform-postgres`
   - **Database**: `deals_platform`
   - **User**: `deals_platform_user`
   - **Region**: `Frankfurt` (тот же, что и web сервис)
   - **Plan**: `Starter` (для начала)

3. **Получите настройки подключения:**
   - **Host**: `your-host.render.com`
   - **Port**: `5432`
   - **Database**: `deals_platform`
   - **User**: `deals_platform_user`
   - **Password**: `auto-generated`

### Шаг 5: Настройка переменных окружения

В web сервисе добавьте переменные:

```bash
# База данных
SPRING_DATASOURCE_URL=jdbc:postgresql://your-host.render.com:5432/deals_platform
SPRING_DATASOURCE_USERNAME=deals_platform_user
SPRING_DATASOURCE_PASSWORD=your-password

# Spring профиль
SPRING_PROFILES_ACTIVE=postgres

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Порт
SERVER_PORT=8080
```

### Шаг 6: Развертывание

1. **Нажмите "Create Web Service"**
2. **Дождитесь сборки и развертывания**
3. **Проверьте логи на наличие ошибок**

## Автоматическое развертывание

### С `render.yaml`

Если у вас есть `render.yaml`, Render.com автоматически:

1. **Создаст PostgreSQL сервис** с указанными параметрами
2. **Настроит переменные окружения** автоматически
3. **Свяжет сервисы** между собой
4. **Развернет приложение** с правильными настройками

### Без `render.yaml`

При ручном создании сервисов:

1. **Создайте PostgreSQL сервис** первым
2. **Создайте Web сервис** и укажите переменные окружения
3. **Свяжите сервисы** через переменные окружения

## Проверка развертывания

### 1. Проверка API
```bash
# Проверка здоровья
curl https://your-app.onrender.com/actuator/health

# Проверка Swagger
curl https://your-app.onrender.com/swagger-ui/
```

### 2. Проверка базы данных
```bash
# В Render.com Dashboard
# Перейдите в PostgreSQL сервис
# Проверьте статус "Available"
```

### 3. Проверка логов
```bash
# В Render.com Dashboard
# Перейдите в Web сервис
# Откройте вкладку "Logs"
```

## Мониторинг и поддержка

### Логи
- **Build Logs**: логи сборки Docker образа
- **Runtime Logs**: логи работы приложения
- **Database Logs**: логи PostgreSQL сервиса

### Метрики
- **Response Time**: время ответа API
- **Error Rate**: процент ошибок
- **Database Connections**: количество подключений к БД

### Алерты
- **Failed Deployments**: неудачные развертывания
- **High Error Rate**: высокий процент ошибок
- **Database Issues**: проблемы с базой данных

## Troubleshooting

### Проблемы сборки
```bash
# Проверьте Dockerfile
# Убедитесь, что все файлы в репозитории
# Проверьте логи сборки
```

### Проблемы запуска
```bash
# Проверьте переменные окружения
# Убедитесь, что PostgreSQL доступен
# Проверьте логи runtime
```

### Проблемы базы данных
```bash
# Проверьте статус PostgreSQL сервиса
# Убедитесь в правильности переменных окружения
# Проверьте права доступа пользователя
```

## Обновление приложения

### Автоматическое обновление
1. **Закоммитьте изменения** в GitHub
2. **Push в main ветку**
3. **Render.com автоматически пересоберет и развернет**

### Ручное обновление
1. **В Render.com Dashboard**
2. **Нажмите "Manual Deploy"**
3. **Выберите ветку и нажмите "Deploy"**

## Масштабирование

### Автоматическое масштабирование
```yaml
# В render.yaml
services:
  - type: web
    name: deals-platform-backend
    plan: starter
    autoScaling:
      minInstances: 1
      maxInstances: 10
      targetCPUUtilizationPercent: 70
```

### Ручное масштабирование
1. **В Render.com Dashboard**
2. **Перейдите в настройки сервиса**
3. **Измените план или количество инстансов**

## Безопасность

### Переменные окружения
- **Не коммитьте** секреты в Git
- **Используйте** переменные окружения Render.com
- **Генерируйте** JWT_SECRET автоматически

### Сетевая безопасность
- **HTTPS** включен автоматически
- **Firewall** настроен Render.com
- **Database** доступен только из web сервиса

### База данных
- **Пароли** генерируются автоматически
- **SSL** подключения включены
- **Backup** выполняется автоматически

## Стоимость

### Starter план (рекомендуется для начала)
- **Web Service**: $7/месяц
- **PostgreSQL**: $7/месяц
- **Общая стоимость**: $14/месяц

### Free план (ограниченный)
- **Web Service**: бесплатно (с ограничениями)
- **PostgreSQL**: не доступен
- **Использование**: только для тестирования

## Альтернативы

### Другие облачные платформы
- **Heroku**: похожий функционал, но дороже
- **Railway**: современная альтернатива
- **Fly.io**: для глобального развертывания

### Self-hosted решения
- **VPS + Docker**: полный контроль
- **Kubernetes**: для сложных развертываний
- **AWS/GCP**: для enterprise решений
