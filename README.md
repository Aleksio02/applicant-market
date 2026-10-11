# Applicant Market

Цифровая платформа-агрегатор ИТ-вакансий с верифицированным профилем
достижений участника ФСП.

## Стек

- **Backend:** Java 21, Spring Boot 4.1.1, PostgreSQL 17, Redis 8.6.1, Flyway, OpenAPI / Swagger
- **Frontend:** React 19, TypeScript, Vite, Tailwind CSS
- **Инфраструктура:** Docker Compose, Nginx

## Запуск

### Требования

- Maven 3
- JDK 21
- Docker Desktop

### Порядок действий

- установить контейнеры с помощью команды
  `docker compose -f docker-compose.yml -f docker-compose-apps.yml up -d --build`
- собрать и запустить проект миграций:
  `cd migration`
  `mvn clean compile exec:java`


### Доступ

- API: `http://localhost:8081`
- Frontend: `http://localhost:8888`
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`