// Конфигурация Deals Platform Frontend
const FRONTEND_CONFIG = {
    // API Base URL для Render.com (замените на ваш URL)
    RENDER_API_BASE_URL: 'https://dealsplatform.onrender.com/api',
    
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

// Логирование конфигурации
console.log('🚀 Deals Platform Frontend Config:', {
    'Render API URL': FRONTEND_CONFIG.RENDER_API_BASE_URL,
    'Local API URL': FRONTEND_CONFIG.LOCAL_API_BASE_URL,
    'Current Hostname': window.location.hostname,
    'Detected API URL': FRONTEND_CONFIG.getApiBaseUrl()
});
