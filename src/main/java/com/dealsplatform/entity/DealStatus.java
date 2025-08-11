package com.dealsplatform.entity;

public enum DealStatus {
    OPEN,           // Пари открыто для присоединения участников
    IN_PROGRESS,    // Пари в процессе выполнения
    IMPLEMENTED,    // Время истекло, доступно голосование
    CONFLICT,       // Конфликт между участниками
    RESOLVED        // Пари разрешено (наблюдателем или согласием сторон)
}
