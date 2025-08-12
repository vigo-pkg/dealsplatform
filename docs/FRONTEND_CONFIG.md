# ⚙️ Конфигурация Frontend

## 📋 Обзор

Frontend Deals Platform использует конфигурационный файл `config.js` для настройки API URL и других параметров.

## 🔧 Настройка API URL

### **Для Render.com (Production)**

1. **Откройте файл** `frontend/config.js`
2. **Измените значение** `RENDER_API_BASE_URL`:

```javascript
const FRONTEND_CONFIG = {
    // API Base URL для Render.com (замените на ваш URL)
    RENDER_API_BASE_URL: 'https://your-backend-app.onrender.com/api',
    
    // Локальный API URL для разработки
    LOCAL_API_BASE_URL: 'http://localhost:8080/api',
    
    // ... остальная конфигурация
};
```

3. **Замените** `your-backend-app.onrender.com` на реальное имя вашего приложения на Render.com

### **Примеры URL**

```javascript
// Если ваше приложение называется "deals-platform-backend"
RENDER_API_BASE_URL: 'https://deals-platform-backend.onrender.com/api'

// Если ваше приложение называется "my-deals-app"
RENDER_API_BASE_URL: 'https://my-deals-app.onrender.com/api'
```

## 🌐 Автоматическое определение

Frontend автоматически определяет, какой URL использовать:

- **На localhost** → использует `LOCAL_API_BASE_URL`
- **На Render.com** → использует `RENDER_API_BASE_URL`
- **На других доменах** → использует `RENDER_API_BASE_URL`

## 📁 Структура файлов

```
frontend/
├── index.html          # Главная страница
├── app.js             # Основная логика приложения
├── config.js          # Конфигурация (ЭТОТ ФАЙЛ)
└── styles.css         # Стили
```

## 🔍 Проверка конфигурации

### **1. Console логи**

Откройте Developer Tools → Console:

```
🚀 Deals Platform Frontend Config: {
  'Render API URL': 'https://your-backend-app.onrender.com/api',
  'Local API URL': 'http://localhost:8080/api',
  'Current Hostname': 'your-app.onrender.com',
  'Detected API URL': 'https://your-backend-app.onrender.com/api'
}
```

### **2. Проверка API URL**

В консоли введите:
```javascript
console.log('Current API URL:', API_BASE_URL);
```

## 🚨 Частые проблемы

### **Проблема: "failed to fetch"**
**Решение**: Проверьте правильность URL в `config.js`

### **Проблема: CORS ошибки**
**Решение**: Убедитесь, что бэкенд обновлен с новой CORS конфигурацией

### **Проблема: Неправильный URL**
**Решение**: Проверьте, что в `RENDER_API_BASE_URL` указан полный URL включая `/api`

## 📝 Пример полной конфигурации

```javascript
// Конфигурация Deals Platform Frontend
const FRONTEND_CONFIG = {
    // API Base URL для Render.com (замените на ваш URL)
    RENDER_API_BASE_URL: 'https://deals-platform-backend.onrender.com/api',
    
    // Локальный API URL для разработки
    LOCAL_API_BASE_URL: 'http://localhost:8080/api',
    
    // Автоматическое определение URL
    getApiBaseUrl: function() {
        // Если мы на Render.com (production)
        if (window.location.hostname.includes('onrender.com')) {
            return this.RENDER_API_BASE_URL;
        }
        // Если мы на localhost (development)
        else if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
            return this.LOCAL_API_BASE_URL;
        }
        // Для других доменов используем Render.com URL
        else {
            return this.RENDER_API_BASE_URL;
        }
    }
};
```

## 🔄 Обновление конфигурации

### **После изменения config.js**

1. **Сохраните файл**
2. **Обновите страницу** в браузере (F5)
3. **Проверьте консоль** на наличие ошибок
4. **Проверьте API запросы** в Network tab

### **Автоматическое обновление**

При push в GitHub:
- **Render.com автоматически** пересоберет фронтенд
- **Новая конфигурация** будет применена

## 🎯 Лучшие практики

### **1. URL формат**
```javascript
// ✅ Правильно
RENDER_API_BASE_URL: 'https://your-app.onrender.com/api'

// ❌ Неправильно
RENDER_API_BASE_URL: 'https://your-app.onrender.com'  // без /api
RENDER_API_BASE_URL: 'your-app.onrender.com/api'      // без https://
```

### **2. Именование**
- Используйте **описательные имена** для приложений
- **Тестируйте** URL перед развертыванием
- **Документируйте** изменения

### **3. Безопасность**
- **Не коммитьте** реальные URL в публичные репозитории
- **Используйте** переменные окружения для production
- **Проверяйте** HTTPS для production

## 🆘 Устранение неполадок

### **Проверка 1: Правильность URL**
```bash
# В браузере откройте
https://your-backend-app.onrender.com/api/health

# Должен быть ответ от API
```

### **Проверка 2: CORS настройки**
```bash
# В Developer Tools → Network
# Проверьте, что запросы идут на правильный URL
# Убедитесь, что нет CORS ошибок
```

### **Проверка 3: Конфигурация**
```bash
# В консоли проверьте
console.log(FRONTEND_CONFIG.RENDER_API_BASE_URL);
console.log(API_BASE_URL);
```

## 🎉 Результат

После правильной настройки:
- ✅ API запросы будут идти на правильный URL
- ✅ Фронтенд будет работать на Render.com
- ✅ Аутентификация будет функционировать
- ✅ Все функции приложения будут доступны

---

**Примечание**: Всегда проверяйте URL в браузере перед развертыванием!
