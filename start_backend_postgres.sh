#!/bin/bash

echo "🚀 Запуск Deals Platform Backend с PostgreSQL..."
echo "================================================"

# Проверка наличия Java
if ! command -v java &> /dev/null; then
    echo "❌ Java не найден"
    echo "💡 Установите Java 17 или выше"
    exit 1
fi

# Проверка версии Java
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Требуется Java 17 или выше, текущая версия: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION найден"

# Проверка наличия Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven не найден"
    echo "💡 Установите Maven 3.6 или выше"
    exit 1
fi

echo "✅ Maven найден"

# Проверка подключения к PostgreSQL
echo "🔍 Проверка подключения к PostgreSQL..."
if ! command -v psql &> /dev/null; then
    echo "❌ psql не найден"
    echo "💡 Установите PostgreSQL client tools"
    echo "💡 На macOS: brew install postgresql"
    exit 1
fi

# Проверка, запущен ли PostgreSQL
if ! pg_isready -h localhost -p 5432 &> /dev/null; then
    echo "❌ PostgreSQL не запущен или недоступен на localhost:5432"
    echo "💡 Запустите PostgreSQL сервис"
    echo "💡 На macOS: brew services start postgresql"
    exit 1
fi

echo "✅ PostgreSQL сервис доступен"

# Проверка существования базы данных
if ! psql -h localhost -U postgres -lqt | cut -d \| -f 1 | grep -qw deals_platform; then
    echo "⚠️  База данных 'deals_platform' не существует"
    echo "💡 Создаю базу данных..."
    createdb -h localhost -U postgres deals_platform
    if [ $? -eq 0 ]; then
        echo "✅ База данных 'deals_platform' создана"
    else
        echo "❌ Не удалось создать базу данных"
        echo "💡 Проверьте права пользователя postgres"
        exit 1
    fi
else
    echo "✅ База данных 'deals_platform' существует"
fi

# Проверка подключения к базе данных
if ! psql -h localhost -U postgres -d deals_platform -c "SELECT 1;" &> /dev/null; then
    echo "❌ Не удалось подключиться к базе данных"
    echo "💡 Проверьте настройки аутентификации PostgreSQL"
    echo "💡 Убедитесь, что пользователь 'postgres' имеет доступ"
    exit 1
fi

echo "✅ Подключение к PostgreSQL успешно"

echo ""
echo "🏗️  Сборка проекта..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Сборка успешна"
    echo ""
    echo "🚀 Запуск Spring Boot приложения с профилем 'postgres'..."
    echo "📡 Backend будет доступен по адресу: http://localhost:8080"
    echo "📚 API документация: http://localhost:8080/swagger-ui/"
    echo "🗄️  База данных: PostgreSQL (deals_platform)"
    echo "🛑 Для остановки нажмите Ctrl+C"
    echo ""
    
    # Запуск с профилем postgres
    mvn spring-boot:run -Dspring.profiles.active=postgres
else
    echo "❌ Ошибка сборки"
    exit 1
fi
