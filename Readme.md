# User service

## Описание сервиса
Данный сервис занимается регистрацией/авторизацией, 
генерацией кода верификации/верификацией, 
а также позволяет устанавливать/менять различные настройки пользователя.

## Требования
- Maven
- JDK 17

## Конфигурация
1. Приложения:
   - _spring.application.name = user-service_
   - _server.port = 8080_
2. Подключение к PostgreSQL:
   - _spring.datasource.url = jdbc:postgresql://postgres:5432/user_service_db_
   - _spring.datasource.username = postgres_
   - _spring.datasource.password = qwerty_
3. Подключение к Redis:
   - _spring.data.redis.host = redis-server_
   - _spring.data.redis.port = 6379_

## Создание docker-image
Для создания образа сервиса необходимо:
1. Собрать docker-image с помощью Dockerfile: _docker build -t **your_image_name** ._