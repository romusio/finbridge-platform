-- Создание базы данных и пользователя уже выполнено через переменные окружения
-- Дополнительные настройки базы данных

-- Включаем расширения PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Создаем индексы для оптимизации
-- (Таблицы будут созданы автоматически через Hibernate)

-- Комментарии для будущих таблиц
COMMENT ON SCHEMA public IS 'FinBridge Platform Database Schema';
