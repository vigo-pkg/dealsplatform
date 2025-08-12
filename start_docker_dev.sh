#!/bin/bash

echo "🚀 Запуск Deals Platform в Docker (Development Profile)..."
echo "========================================================"

# Проверка наличия Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не найден"
    echo "💡 Установите Docker Desktop или Docker Engine"
    exit 1
fi

# Проверка наличия Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose не найден"
    echo "💡 Установите Docker Compose"
    exit 1
fi

echo "✅ Docker найден"
echo "✅ Docker Compose найден"

echo ""
echo "🗄️  Профиль: H2 (Development)"
echo "💡 Для production используйте: ./start_docker_production.sh"
echo ""

# Остановка существующих контейнеров
echo "🛑 Остановка существующих контейнеров..."
docker-compose -f docker-compose.dev.yml down

# Удаление старых образов
echo "🧹 Удаление старых образов..."
docker-compose -f docker-compose.dev.yml down --rmi all

# Сборка и запуск
echo "🏗️  Сборка и запуск контейнеров..."
docker-compose -f docker-compose.dev.yml up --build -d

# Проверка статуса
echo ""
echo "⏳ Ожидание запуска сервисов..."
sleep 10

# Проверка статуса контейнеров
echo "📊 Статус контейнеров:"
docker-compose -f docker-compose.dev.yml ps

echo ""
echo "✅ Deals Platform запущена в Docker (Development)!"
echo "📡 Backend: http://localhost:8080"
echo "📚 API документация: http://localhost:8080/swagger-ui/"
echo "🗄️  База данных: H2 (in-memory)"
echo ""
echo "🛑 Для остановки: docker-compose -f docker-compose.dev.yml down"
echo "📋 Для логов: docker-compose -f docker-compose.dev.yml logs -f backend"
