#!/bin/bash

echo "🚀 Запуск Deals Platform Backend (H2 Profile)..."
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
echo ""
echo "🗄️  Профиль: H2 (in-memory база данных)"
echo "💡 Для PostgreSQL используйте: ./start_backend_postgres.sh"
echo ""


echo ""
echo "🏗️  Сборка проекта..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Сборка успешна"
    echo ""
    echo "🚀 Запуск Spring Boot приложения с профилем 'h2'..."
    echo "📡 Backend будет доступен по адресу: http://localhost:8080"
    echo "📚 API документация: http://localhost:8080/swagger-ui/"
    echo "🗄️  База данных: H2 (in-memory)"
    echo "🛑 Для остановки нажмите Ctrl+C"
    echo ""
    
    # Запуск с профилем h2
    mvn spring-boot:run -Dspring.profiles.active=h2
else
    echo "❌ Ошибка сборки"
    exit 1
fi
