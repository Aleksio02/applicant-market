# Инструкция по установке (beta):
### Требования: maven 3, jdk 21
### Порядок действий:
- установить контейнеры с помощью команды
  `docker compose -f docker-compose.yml -f docker-compose-apps.yml up -d --build`
- собрать и запустить проект миграций:
  `cd migration`
  `mvn clean compile exec:java`

### Сервер работает на порту 8081, клиент доступен по адресу `http://localhost:8888` (8888 порт)
