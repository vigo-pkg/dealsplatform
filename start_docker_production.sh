#!/bin/bash

echo "🚀 Запуск Deals Platform в Docker (Production Profile)..."
echo "======================================================"

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
echo "🗄️  Профиль: PostgreSQL (Production)"
echo "💡 Для разработки используйте: ./start_docker_dev.sh"
echo "💡 Для Render.com используйте: render.yaml"
echo ""

# Остановка существующих контейнеров
echo "🛑 Остановка существующих контейнеров..."
docker-compose down

# Удаление старых образов
echo "🧹 Удаление старых образов..."
docker-compose down --rmi all

# Сборка и запуск
echo "🏗️  Сборка и запуск контейнеров..."
docker-compose up --build -d

# Проверка статуса
echo ""
echo "⏳ Ожидание запуска сервисов..."
sleep 10

# Проверка статуса контейнеров
echo "📊 Статус контейнеров:"
docker-compose ps

echo ""
echo "✅ Deals Platform запущена в Docker!"
echo "📡 Backend: http://localhost:8080"
echo "📚 API документация: http://localhost:8080/swagger-ui/"
echo "🗄️  База данных: PostgreSQL (в контейнере)"
echo ""
echo "🛑 Для остановки: docker-compose down"
echo "📋 Для логов: docker-compose logs -f backend"
