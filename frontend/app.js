// Конфигурация API - использует настройки из config.js
const API_BASE_URL = FRONTEND_CONFIG.getApiBaseUrl();

console.log('🌐 API Base URL:', API_BASE_URL);
console.log('📍 Current location:', window.location.href);
console.log('🏠 Hostname:', window.location.hostname);

// Глобальные переменные
let currentUser = null;
let currentDealId = null;

// Инициализация приложения
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
    checkAuthStatus();
});

// Инициализация приложения
function initializeApp() {
    // Устанавливаем минимальное время для datetime-local
    const now = new Date();
    const localDateTime = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
    document.getElementById('deal-start-time').min = localDateTime;
    
    // Устанавливаем текущее время + 1 час по умолчанию
    const defaultTime = new Date(now.getTime() + 60 * 60 * 1000);
    const defaultDateTime = new Date(defaultTime.getTime() - defaultTime.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
    document.getElementById('deal-start-time').value = defaultDateTime;
}

// Настройка обработчиков событий
function setupEventListeners() {
    // Навигация
    document.getElementById('nav-dashboard').addEventListener('click', showDashboard);
    document.getElementById('nav-create-deal').addEventListener('click', showCreateDeal);
    document.getElementById('back-to-dashboard').addEventListener('click', showDashboard);
    
    // Аутентификация
    document.getElementById('nav-login').addEventListener('click', showLoginModal);
    document.getElementById('nav-register').addEventListener('click', showRegisterModal);
    document.getElementById('nav-logout').addEventListener('click', logout);
    
    // Формы
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('register-form').addEventListener('submit', handleRegister);
    document.getElementById('create-deal-form').addEventListener('submit', handleCreateDeal);
    
    // Модальные окна
    document.getElementById('voteModal').addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        currentDealId = button.getAttribute('data-deal-id');
    });
    
    document.getElementById('observerDecisionModal').addEventListener('show.bs.modal', function(event) {
        const button = event.relatedTarget;
        currentDealId = button.getAttribute('data-deal-id');
    });
    
    // Голосование
    document.querySelectorAll('[data-vote]').forEach(button => {
        button.addEventListener('click', function() {
            const vote = this.getAttribute('data-vote') === 'true';
            submitVote(vote);
        });
    });
    
    // Решение наблюдателя
    document.querySelectorAll('[data-decision]').forEach(button => {
        button.addEventListener('click', function() {
            const decision = this.getAttribute('data-decision') === 'true';
            submitObserverDecision(decision);
        });
    });
}

// Проверка статуса аутентификации
function checkAuthStatus() {
    const token = localStorage.getItem('jwt_token');
    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            if (payload.exp * 1000 > Date.now()) {
                currentUser = {
                    email: payload.sub,
                    id: payload.userId || null
                };
                updateAuthUI(true);
                loadDashboard();
            } else {
                localStorage.removeItem('jwt_token');
                updateAuthUI(false);
            }
        } catch (e) {
            localStorage.removeItem('jwt_token');
            updateAuthUI(false);
        }
    } else {
        updateAuthUI(false);
    }
}

// Обновление UI аутентификации
function updateAuthUI(isAuthenticated) {
    const authNav = document.getElementById('auth-nav');
    const userNav = document.getElementById('user-nav');
    const userEmail = document.getElementById('user-email');
    
    if (isAuthenticated) {
        authNav.classList.add('d-none');
        userNav.classList.remove('d-none');
        userEmail.textContent = currentUser.email;
    } else {
        authNav.classList.remove('d-none');
        userNav.classList.add('d-none');
        userEmail.textContent = '';
    }
}

// Показать дашборд
function showDashboard() {
    document.getElementById('dashboard-page').classList.remove('d-none');
    document.getElementById('create-deal-page').classList.add('d-none');
    document.getElementById('deal-detail-page').classList.add('d-none');
    
    // Обновить активную навигацию
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
    document.getElementById('nav-dashboard').classList.add('active');
    
    loadDashboard();
}

// Показать страницу создания пари
function showCreateDeal() {
    document.getElementById('dashboard-page').classList.add('d-none');
    document.getElementById('create-deal-page').classList.remove('d-none');
    document.getElementById('deal-detail-page').classList.add('d-none');
    
    // Обновить активную навигацию
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
    document.getElementById('nav-create-deal').classList.add('active');
}

// Показать детали пари
function showDealDetail(dealId) {
    currentDealId = dealId;
    document.getElementById('dashboard-page').classList.add('d-none');
    document.getElementById('create-deal-page').classList.add('d-none');
    document.getElementById('deal-detail-page').classList.remove('d-none');
    
    loadDealDetail(dealId);
}

// Загрузка дашборда
async function loadDashboard() {
    try {
        const deals = await apiCall('/deals', 'GET');
        updateDashboardStats(deals);
        renderDealsList(deals);
    } catch (error) {
        showNotification('Ошибка загрузки дашборда', error.message, 'error');
    }
}

// Обновление статистики дашборда
function updateDashboardStats(deals) {
    const stats = {
        total: deals.length,
        active: deals.filter(d => d.status === 'IN_PROGRESS').length,
        pending: deals.filter(d => d.status === 'IMPLEMENTED').length,
        resolved: deals.filter(d => d.status === 'RESOLVED').length
    };
    
    document.getElementById('total-deals').textContent = stats.total;
    document.getElementById('active-deals').textContent = stats.active;
    document.getElementById('pending-deals').textContent = stats.pending;
    document.getElementById('resolved-deals').textContent = stats.resolved;
}

// Рендеринг списка пари
function renderDealsList(deals) {
    const dealsList = document.getElementById('deals-list');
    
    if (deals.length === 0) {
        dealsList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-handshake"></i>
                <h5>Пари пока нет</h5>
                <p>Создайте первое пари и начните соревноваться!</p>
                <button class="btn btn-primary" onclick="showCreateDeal()">
                    <i class="fas fa-plus me-2"></i>Создать пари
                </button>
            </div>
        `;
        return;
    }
    
    dealsList.innerHTML = deals.map(deal => `
        <div class="deal-item">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h6 class="mb-2">${deal.description}</h6>
                    <div class="deal-meta">
                        <span class="deal-status ${deal.status.toLowerCase().replace('_', '-')}">${getStatusText(deal.status)}</span>
                        <span class="ms-2">Создатель: ${deal.creatorEmail}</span>
                        <span class="ms-2">Участников: ${deal.participants.length}</span>
                        ${deal.observers.length > 0 ? `<span class="ms-2">Наблюдателей: ${deal.observers.length}</span>` : ''}
                    </div>
                    <div class="deal-meta mt-1">
                        <small>Старт: ${formatDateTime(deal.startTime)}</small>
                        <small class="ms-3">Длительность: ${deal.durationMinutes} мин</small>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="deal-actions">
                        <button class="btn btn-outline-primary btn-sm" onclick="showDealDetail(${deal.id})">
                            <i class="fas fa-eye me-1"></i>Просмотр
                        </button>
                        ${getActionButtons(deal)}
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// Получение кнопок действий для пари
function getActionButtons(deal) {
    if (!currentUser) return '';
    
    const isParticipant = deal.participants.some(p => p.email === currentUser.email);
    const isObserver = deal.observers.some(o => o.email === currentUser.email);
    const canVote = deal.status === 'IMPLEMENTED' && isParticipant;
    const isCreator = deal.creatorId == currentUser.id; // Используем == для сравнения числа и строки
    
    // Проверяем статус заявки пользователя
    const userApplication = deal.userApplication;
    const hasPendingApplication = userApplication && userApplication.status === 'PENDING';
    const hasApprovedApplication = userApplication && userApplication.status === 'APPROVED';
    const hasRejectedApplication = userApplication && userApplication.status === 'REJECTED';
    
    let buttons = '';
    
    // Кнопки для подачи заявок (только если нет активной заявки и не является участником/наблюдателем)
    if (deal.status === 'OPEN' && !isParticipant && !hasPendingApplication && !hasApprovedApplication) {
        buttons += `<button class="btn btn-success btn-sm" onclick="createApplication(${deal.id}, 'PARTICIPANT')">
            <i class="fas fa-user-plus me-1"></i>Подать заявку на участие
        </button>`;
    }
    
    if (deal.status !== 'RESOLVED' && !isParticipant && !isObserver && !hasPendingApplication && !hasApprovedApplication) {
        buttons += `<button class="btn btn-info btn-sm" onclick="createApplication(${deal.id}, 'OBSERVER')">
            <i class="fas fa-eye me-1"></i>Подать заявку на наблюдение
        </button>`;
    }
    
    // Показываем статус заявки
    if (hasPendingApplication) {
        const appType = userApplication.applicationType === 'PARTICIPANT' ? 'участие' : 'наблюдение';
        buttons += `<span class="badge bg-warning me-2">Заявка на ${appType} ожидает рассмотрения</span>`;
        buttons += `<button class="btn btn-outline-secondary btn-sm" onclick="withdrawApplication(${deal.id}, ${userApplication.id})">
            <i class="fas fa-times me-1"></i>Отозвать заявку
        </button>`;
    }
    
    if (hasRejectedApplication) {
        const appType = userApplication.applicationType === 'PARTICIPANT' ? 'участие' : 'наблюдение';
        buttons += `<span class="badge bg-danger me-2">Заявка на ${appType} отклонена</span>`;
        // Можно подать новую заявку
        if (deal.status === 'OPEN' && !isParticipant) {
            buttons += `<button class="btn btn-success btn-sm" onclick="createApplication(${deal.id}, 'PARTICIPANT')">
                <i class="fas fa-redo me-1"></i>Подать заявку снова
            </button>`;
        }
    }
    
    if (canVote) {
        buttons += `<button class="btn btn-warning btn-sm" data-bs-toggle="modal" data-bs-target="#voteModal" data-deal-id="${deal.id}">
            <i class="fas fa-vote-yea me-1"></i>Голосовать
        </button>`;
    }
    
    if (deal.status === 'CONFLICT' && isObserver) {
        buttons += `<button class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#observerDecisionModal" data-deal-id="${deal.id}">
            <i class="fas fa-gavel me-1"></i>Решить
        </button>`;
    }
    
    return buttons;
}

// Загрузка деталей пари
async function loadDealDetail(dealId) {
    try {
        const deal = await apiCall(`/deals/${dealId}`, 'GET');
        renderDealDetail(deal);
    } catch (error) {
        showNotification('Ошибка загрузки пари', error.message, 'error');
    }
}

// Рендеринг деталей пари
function renderDealDetail(deal) {
    document.getElementById('deal-title').textContent = deal.description;
    
    const dealContent = document.getElementById('deal-content');
    dealContent.innerHTML = `
        <div class="row">
            <div class="col-md-8">
                <div class="deal-detail-section">
                    <h6>Описание</h6>
                    <p>${deal.description}</p>
                </div>
                
                <div class="deal-detail-section">
                    <h6>Информация</h6>
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>Статус:</strong> <span class="deal-status ${deal.status.toLowerCase().replace('_', '-')}">${getStatusText(deal.status)}</span></p>
                            <p><strong>Создатель:</strong> ${deal.creatorEmail}</p>
                            <p><strong>Создано:</strong> ${formatDateTime(deal.createdAt)}</p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Старт:</strong> ${formatDateTime(deal.startTime)}</p>
                            <p><strong>Длительность:</strong> ${deal.durationMinutes} минут</p>
                            <p><strong>Завершение:</strong> ${formatDateTime(deal.endTime)}</p>
                        </div>
                    </div>
                </div>
                
                <div class="deal-detail-section">
                    <h6>Участники (${deal.participants.length})</h6>
                    ${deal.participants.map(p => `
                        <div class="participant-item ${p.isCreator ? 'creator' : ''}">
                            <div class="d-flex justify-content-between align-items-center">
                                <span>${p.email}</span>
                                <span class="badge ${p.isCreator ? 'bg-primary' : 'bg-secondary'}">${p.isCreator ? 'Создатель' : 'Участник'}</span>
                            </div>
                            <small class="text-muted">Присоединился: ${formatDateTime(p.joinedAt)}</small>
                        </div>
                    `).join('')}
                </div>
                
                ${deal.observers.length > 0 ? `
                <div class="deal-detail-section">
                    <h6>Наблюдатели (${deal.observers.length})</h6>
                    ${deal.observers.map(o => `
                        <div class="observer-item">
                            <div class="d-flex justify-content-between align-items-center">
                                <span>${o.email}</span>
                                ${o.finalDecision !== null ? `
                                    <span class="badge ${o.finalDecision ? 'bg-success' : 'bg-danger'}">
                                        ${o.finalDecision ? 'Первая сторона выиграла' : 'Вторая сторона выиграла'}
                                    </span>
                                ` : '<span class="badge bg-secondary">Ожидает решения</span>'}
                            </div>
                            <small class="text-muted">Присоединился: ${formatDateTime(o.joinedAt)}</small>
                            ${o.decisionAt ? `<br><small class="text-muted">Решение принято: ${formatDateTime(o.decisionAt)}</small>` : ''}
                        </div>
                    `).join('')}
                </div>
                ` : ''}
                
                ${deal.votes.length > 0 ? `
                <div class="deal-detail-section">
                    <h6>Голосование (${deal.votes.length})</h6>
                    ${deal.votes.map(v => `
                        <div class="vote-item ${v.vote ? 'won' : 'lost'}">
                            <div class="d-flex justify-content-between align-items-center">
                                <span>${v.voterEmail}</span>
                                <span class="badge ${v.vote ? 'bg-success' : 'bg-danger'}">
                                    ${v.vote ? 'Выиграл' : 'Проиграл'}
                                </span>
                            </div>
                            <small class="text-muted">Проголосовал: ${formatDateTime(v.votedAt)}</small>
                        </div>
                    `).join('')}
                </div>
                ` : ''}
                
                ${deal.pendingApplications && deal.pendingApplications.length > 0 ? `
                <div class="deal-detail-section">
                    <h6>Ожидающие заявки (${deal.pendingApplications.length})</h6>
                    ${deal.pendingApplications.map(app => `
                        <div class="application-item mb-3 p-3 border rounded">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <div>
                                    <strong>${app.applicantEmail}</strong>
                                    <span class="badge ${app.applicationType === 'PARTICIPANT' ? 'bg-success' : 'bg-info'} ms-2">
                                        ${app.applicationType === 'PARTICIPANT' ? 'Участник' : 'Наблюдатель'}
                                    </span>
                                </div>
                                <div>
                                    <button class="btn btn-success btn-sm me-1" onclick="approveApplication(${deal.id}, ${app.id})">
                                        <i class="fas fa-check me-1"></i>Одобрить
                                    </button>
                                    <button class="btn btn-danger btn-sm" onclick="rejectApplication(${deal.id}, ${app.id})">
                                        <i class="fas fa-times me-1"></i>Отклонить
                                    </button>
                                </div>
                            </div>
                            <small class="text-muted">Подана: ${formatDateTime(app.createdAt)}</small>
                        </div>
                    `).join('')}
                </div>
                ` : ''}
            </div>
            
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h6 class="mb-0">Действия</h6>
                    </div>
                    <div class="card-body">
                        ${getActionButtons(deal)}
                    </div>
                </div>
            </div>
        </div>
    `;
}

// API вызовы
async function apiCall(endpoint, method = 'GET', data = null) {
    const url = `${API_BASE_URL}${endpoint}`;
    console.log('API Call:', { url, method, data });
    
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };
    
    const token = localStorage.getItem('jwt_token');
    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }
    
    if (data) {
        options.body = JSON.stringify(data);
    }
    
    console.log('Request options:', options);
    
    try {
        const response = await fetch(url, options);
        console.log('Response received:', response);
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        console.log('Response data:', result);
        return result;
    } catch (error) {
        console.error('API Call error:', error);
        throw error;
    }
}

// Аутентификация
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    
    console.log('Login attempt:', { email, password });
    
    try {
        const response = await apiCall('/auth/login', 'POST', { email, password });
        console.log('Login successful:', response);
        
        localStorage.setItem('jwt_token', response.token);
        currentUser = { email: response.email, id: response.userId };
        
        updateAuthUI(true);
        showDashboard();
        
        // Закрыть модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
        modal.hide();
        
        showNotification('Успешный вход', 'Добро пожаловать в систему!', 'success');
        
        // Очистить форму
        document.getElementById('login-form').reset();
    } catch (error) {
        console.error('Login error:', error);
        showNotification('Ошибка входа', error.message, 'error');
    }
}

async function handleRegister(event) {
    event.preventDefault();
    
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('register-confirm-password').value;
    
    if (password !== confirmPassword) {
        showNotification('Ошибка', 'Пароли не совпадают', 'error');
        return;
    }
    
    try {
        const response = await apiCall('/auth/register', 'POST', { email, password });
        
        localStorage.setItem('jwt_token', response.token);
        currentUser = { email: response.email, id: response.userId };
        
        updateAuthUI(true);
        showDashboard();
        
        // Закрыть модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('registerModal'));
        modal.hide();
        
        showNotification('Успешная регистрация', 'Аккаунт создан!', 'success');
        
        // Очистить форму
        document.getElementById('register-form').reset();
    } catch (error) {
        showNotification('Ошибка регистрации', error.message, 'error');
    }
}

function logout() {
    localStorage.removeItem('jwt_token');
    currentUser = null;
    updateAuthUI(false);
    showDashboard();
    showNotification('Выход', 'Вы вышли из системы', 'info');
}

// Модальные окна
function showLoginModal() {
    const modal = new bootstrap.Modal(document.getElementById('loginModal'));
    modal.show();
}

function showRegisterModal() {
    const modal = new bootstrap.Modal(document.getElementById('registerModal'));
    modal.show();
}

// Создание пари
async function handleCreateDeal(event) {
    event.preventDefault();
    
    const description = document.getElementById('deal-description').value;
    const startTime = document.getElementById('deal-start-time').value;
    const durationMinutes = parseInt(document.getElementById('deal-duration').value);
    
    try {
        await apiCall('/deals', 'POST', {
            description,
            startTime: new Date(startTime).toISOString(),
            durationMinutes
        });
        
        showNotification('Успех', 'Пари создано!', 'success');
        showDashboard();
        
        // Очистить форму
        document.getElementById('create-deal-form').reset();
        
        // Установить время по умолчанию
        const now = new Date();
        const defaultTime = new Date(now.getTime() + 60 * 60 * 1000);
        const defaultDateTime = new Date(defaultTime.getTime() - defaultTime.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
        document.getElementById('deal-start-time').value = defaultDateTime;
    } catch (error) {
        showNotification('Ошибка', error.message, 'error');
    }
}

// Работа с заявками
async function createApplication(dealId, applicationType) {
    try {
        await apiCall(`/deals/${dealId}/applications`, 'POST', { applicationType });
        const typeText = applicationType === 'PARTICIPANT' ? 'участие' : 'наблюдение';
        showNotification('Успех', `Заявка на ${typeText} подана!`, 'success');
        if (currentDealId === dealId) {
            loadDealDetail(dealId);
        } else {
            loadDashboard();
        }
    } catch (error) {
        showNotification('Ошибка', error.message, 'error');
    }
}

async function approveApplication(dealId, applicationId) {
    try {
        await apiCall(`/deals/${dealId}/applications/${applicationId}/approve`, 'POST');
        showNotification('Успех', 'Заявка одобрена!', 'success');
        loadDealDetail(dealId);
    } catch (error) {
        showNotification('Ошибка', error.message, 'error');
    }
}

async function rejectApplication(dealId, applicationId) {
    try {
        await apiCall(`/deals/${dealId}/applications/${applicationId}/reject`, 'POST');
        showNotification('Успех', 'Заявка отклонена', 'info');
        loadDealDetail(dealId);
    } catch (error) {
        showNotification('Ошибка', error.message, 'error');
    }
}

async function withdrawApplication(dealId, applicationId) {
    try {
        await apiCall(`/deals/${dealId}/applications/${applicationId}`, 'DELETE');
        showNotification('Успех', 'Заявка отозвана', 'info');
        if (currentDealId === dealId) {
            loadDealDetail(dealId);
        } else {
            loadDashboard();
        }
    } catch (error) {
        showNotification('Ошибка', error.message, 'error');
    }
}

// Голосование
async function submitVote(vote) {
    try {
        await apiCall(`/deals/${currentDealId}/vote`, 'POST', { vote });
        showNotification('Успех', 'Ваш голос учтен!', 'success');
        
        // Закрыть модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('voteModal'));
        modal.hide();
        
        // Обновить данные
        if (currentDealId) {
            loadDealDetail(currentDealId);
        }
    } catch (error) {
        showNotification('Ошибка', error.message, 'error');
    }
}

// Решение наблюдателя
async function submitObserverDecision(decision) {
    try {
        await apiCall(`/deals/${currentDealId}/observer-decision`, 'POST', { decision });
        showNotification('Успех', 'Решение принято!', 'success');
        
        // Закрыть модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('observerDecisionModal'));
        modal.hide();
        
        // Обновить данные
        if (currentDealId) {
            loadDealDetail(currentDealId);
        }
    } catch (error) {
        showNotification('Ошибка', error.message, 'error');
    }
}

// Утилиты
function getStatusText(status) {
    const statusMap = {
        'OPEN': 'Открыто',
        'IN_PROGRESS': 'В процессе',
        'IMPLEMENTED': 'Завершено',
        'CONFLICT': 'Конфликт',
        'RESOLVED': 'Разрешено'
    };
    return statusMap[status] || status;
}

function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('ru-RU');
}

// Уведомления
function showNotification(title, message, type = 'info') {
    const notifications = document.getElementById('notifications');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    
    notification.innerHTML = `
        <div class="notification-header">
            <h6 class="notification-title">${title}</h6>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">&times;</button>
        </div>
        <p class="notification-message">${message}</p>
    `;
    
    notifications.appendChild(notification);
    
    // Автоматически удалить через 5 секунд
    setTimeout(() => {
        if (notification.parentElement) {
            notification.classList.add('fade-out');
            setTimeout(() => {
                if (notification.parentElement) {
                    notification.remove();
                }
            }, 300);
        }
    }, 5000);
}
