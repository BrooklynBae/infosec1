# infosec1 — Защищённый REST API

## Описание проекта

Проект представляет собой REST API на Java и Spring Boot с аутентификацией пользователей, хранением данных в PostgreSQL и автоматизированной проверкой безопасности в GitHub Actions.

В рамках лабораторной работы реализована защита от SQL-инъекций, XSS и нарушений аутентификации, а также настроены инструменты SAST и SCA.

## Используемые технологии

- Java 21
- Spring Boot 4.0.8
- Spring Security
- Spring Data JPA (Hibernate)
- PostgreSQL
- JWT
- BCrypt
- Maven
- SpotBugs
- OWASP Dependency-Check
- GitHub Actions

## Запуск проекта

Для запуска необходимы JDK 21, PostgreSQL и Maven (или Maven Wrapper из проекта).

1. Создать базу данных PostgreSQL с именем `infosec1`.
2. Убедиться, что PostgreSQL доступен на `localhost:5432`, а пользователь `postgres` существует.
3. Установить переменные окружения `DB_PASSWORD` — пароль PostgreSQL, и `JWT_SECRET` — секретный ключ для подписи JWT.

В PowerShell:

```powershell
$env:DB_PASSWORD = "your_postgres_password"
$env:JWT_SECRET = "your_long_random_secret"
.\mvnw.cmd spring-boot:run
```

После запуска API доступен по адресу `http://localhost:8080`.

## API

### 1. POST /auth/register

Регистрация нового пользователя.

Тело запроса:

```json
{
  "username": "testuser",
  "password": "TestPassword123"
}
```

Успешный ответ: `201 Created`.

Пароль перед сохранением хешируется алгоритмом BCrypt.

### 2. POST /auth/login

Аутентификация пользователя по логину и паролю.

Тело запроса:

```json
{
  "username": "testuser",
  "password": "TestPassword123"
}
```

Успешный ответ: `200 OK`.

В ответе возвращается JWT-токен:

```json
{
  "token": "<JWT_TOKEN>"
}
```

Срок действия токена — 1 час.

При неверных учётных данных возвращается `401 Unauthorized`.

### 3. GET /api/data

Получение списка зарегистрированных пользователей.

Для доступа необходимо передать JWT в заголовке запроса:

```http
Authorization: Bearer <JWT_TOKEN>
```

Пример успешного ответа:

```json
{
  "users": ["testuser"]
}
```

Без действительного токена сервер возвращает `401 Unauthorized`.

## Реализованные меры безопасности

### Защита от SQL-инъекций

Для работы с PostgreSQL используется Spring Data JPA и Hibernate. Поиск и сохранение пользователей выполняются через репозиторий, без формирования SQL-запросов конкатенацией пользовательского ввода.

### Защита от XSS

Перед возвращением имён пользователей в ответе `/api/data` применяется `HtmlUtils.htmlEscape()`, преобразующий специальные HTML-символы.

### Защита аутентификации

- Пароли хранятся в виде BCrypt-хешей.
- Для аутентификации используются подписанные JWT-токены.
- Подпись и срок действия JWT проверяются при обработке запросов.
- Доступ к защищённым эндпоинтам контролируется Spring Security.
- Используется stateless-аутентификация.
- Секрет JWT и пароль базы данных передаются через переменные окружения.

## CI/CD и анализ безопасности

В GitHub Actions настроен workflow `Security CI`, запускающийся при `push` и `pull_request`.

Pipeline выполняет следующие этапы:

1. Сборка Maven-проекта.
2. **SAST — SpotBugs:** статический анализ Java-кода.
3. **SCA — OWASP Dependency-Check:** анализ используемых зависимостей на известные уязвимости.
4. Сохранение отчётов безопасности в артефактах GitHub Actions.

Для Dependency-Check установлен порог CVSS 9.0, при достижении которого сборка завершается с ошибкой.

### Результаты SAST

SpotBugs проанализировал 167 строк кода, 10 классов и 6 пакетов. Предупреждений не обнаружено.

![Результат анализа SpotBugs](screenshots/spotbugs.png)

### Результаты SCA

При первоначальном сканировании Dependency-Check были выявлены критические уязвимости Apache Tomcat. После обновления Tomcat до версии 11.0.27 повторная проверка успешно завершилась с установленным порогом CVSS 9.0.

![Результат анализа OWASP Dependency-Check](screenshots/dependency-check.png)

### Успешный запуск CI/CD

[Security CI — успешный запуск GitHub Actions](https://github.com/BrooklynBae/infosec1/actions/runs/39174398746)

## Репозиторий

https://github.com/BrooklynBae/infosec1