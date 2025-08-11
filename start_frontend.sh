#!/bin/bash

echo "🚀 Запуск Deals Platform Frontend..."
echo "=================================="

# Проверка наличия Python
if command -v python3 &> /dev/null; then
    echo "✅ Python 3 найден"
    cd frontend
    echo "🌐 Запуск HTTP сервера на порту 3000..."
    echo "📱 Frontend будет доступен по адресу: http://localhost:3000"
    echo "🛑 Для остановки нажмите Ctrl+C"
    echo ""
    python3 -m http.server 3000
elif command -v python &> /dev/null; then
    echo "✅ Python найден"
    cd frontend
    echo "🌐 Запуск HTTP сервера на порту 3000..."
    echo "📱 Frontend будет доступен по адресу: http://localhost:3000"
    echo "🛑 Для остановки нажмите Ctrl+C"
    echo ""
    python -m http.server 3000
elif command -v node &> /dev/null; then
    echo "✅ Node.js найден"
    cd frontend
    echo "🌐 Запуск HTTP сервера на порту 3000..."
    echo "📱 Frontend будет доступен по адресу: http://localhost:3000"
    echo "🛑 Для остановки нажмите Ctrl+C"
    echo ""
    npx http-server -p 3000
else
    echo "❌ Не найден Python или Node.js"
    echo "💡 Установите Python 3 или Node.js для запуска frontend"
    echo "💡 Или используйте Live Server в VS Code"
    exit 1
fi
