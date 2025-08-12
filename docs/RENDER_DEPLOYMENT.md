# 🚀 Развертывание Deals Platform на Render.com

## 📋 Обзор

Render.com - это современная платформа для развертывания веб-приложений и баз данных. Этот документ описывает процесс развертывания Deals Platform на Render.com.

## 🎯 Преимущества Render.com

- **Автоматическое развертывание** из Git репозитория
- **Бесплатный план** для небольших проектов
- **Встроенная PostgreSQL** база данных
- **Автоматический HTTPS** и SSL сертификаты
- **Глобальный CDN** для быстрой загрузки
- **Простота настройки** через YAML конфигурацию

## 🚨 Решение проблемы с JDBC URL

### Проблема
Render.com предоставляет `connectionString` в формате:
```
postgresql://user:password@host/database
```

Но Spring Boot ожидает JDBC URL в формате:
```
jdbc:postgresql://host:port/database
```

### Решение
Создан специальный `DatabaseConfig` класс, который автоматически преобразует connectionString от Render.com в правильный JDBC URL.

## 🛠️ Подготовка к развертыванию

### 1. Проверьте файлы
Убедитесь, что у вас есть:
- ✅ `render.yaml` - конфигурация Render.com
- ✅ `Dockerfile` - для сборки Docker образа
- ✅ `src/main/resources/application-postgres.yml` - профиль PostgreSQL
- ✅ `src/main/java/com/dealsplatform/config/DatabaseConfig.java` - конфигурация БД

### 2. Настройка переменных окружения
Render.com автоматически создаст:
- `SPRING_DATASOURCE_URL` - connectionString от PostgreSQL
- `SPRING_DATASOURCE_USERNAME` - имя пользователя БД
- `SPRING_DATASOURCE_PASSWORD` - пароль БД
- `JWT_SECRET` - секретный ключ для JWT

## 🚀 Процесс развертывания

### Шаг 1: Подключение к Render.com
1. Зайдите на [render.com](https://render.com)
2. Создайте аккаунт или войдите
3. Подключите ваш GitHub репозиторий

### Шаг 2: Автоматическое развертывание
1. Render.com автоматически обнаружит `render.yaml`
2. Создаст PostgreSQL базу данных
3. Соберет и развернет Docker контейнер
4. Настроит переменные окружения

### Шаг 3: Проверка развертывания
1. Дождитесь завершения сборки (обычно 5-10 минут)
2. Проверьте логи на наличие ошибок
3. Откройте приложение по предоставленному URL

## 🔧 Конфигурация

### render.yaml
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

databases:
  - name: deals-platform-postgres
    databaseName: deals_platform
    user: deals_platform_user
    plan: starter
    region: frankfurt
```

### DatabaseConfig.java
Автоматически преобразует connectionString от Render.com в JDBC URL и настраивает пул соединений.

## 📊 Мониторинг и логи

### Просмотр логов
1. В Render.com Dashboard выберите ваш сервис
2. Перейдите на вкладку "Logs"
3. Проверьте логи на наличие ошибок

### Типичные проблемы
- **JDBC URL ошибки** - решены через DatabaseConfig
- **Подключение к БД** - проверьте переменные окружения
- **Порт занят** - Render.com автоматически назначает порт

## 🔄 Обновления

### Автоматические обновления
1. Push в `main` ветку GitHub
2. Render.com автоматически пересоберет и развернет
3. Zero-downtime развертывание

### Ручные обновления
1. В Dashboard выберите "Manual Deploy"
2. Выберите ветку или коммит
3. Запустите развертывание

## 💰 Стоимость

### Бесплатный план
- **Web Service**: 750 часов/месяц
- **PostgreSQL**: 90 часов/месяц
- **Память**: 512 MB RAM
- **Диск**: 1 GB

### Платные планы
- **Starter**: $7/месяц
- **Standard**: $25/месяц
- **Pro**: $50/месяц

## 🆘 Устранение неполадок

### Проблема: "Driver org.postgresql.Driver claims to not accept jdbcUrl"
**Решение**: Проверьте, что `DatabaseConfig.java` правильно преобразует connectionString.

### Проблема: "Connection refused"
**Решение**: Убедитесь, что PostgreSQL сервис запущен и переменные окружения корректны.

### Проблема: "Port already in use"
**Решение**: Render.com автоматически назначает порт через переменную `$PORT`.

## 📚 Дополнительные ресурсы

- [Render.com Documentation](https://render.com/docs)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/)

## 🎉 Готово!

После успешного развертывания ваше приложение будет доступно по URL вида:
```
https://deals-platform-backend.onrender.com
```

База данных будет автоматически создана и настроена, а все переменные окружения будут правильно переданы в приложение.

---

**Примечание**: Первое развертывание может занять 10-15 минут. Последующие обновления будут происходить быстрее.
