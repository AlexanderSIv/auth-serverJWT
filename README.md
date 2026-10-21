# Auth Server JWT

**Сервер аутентификации и API для приложения EPill на Spring Boot + JWT + MySQL**

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-6DB33F?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-HS256-blue?style=flat-square&logo=jsonwebtokens)](https://jwt.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Build-Maven-red?style=flat-square&logo=apachemaven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](https://opensource.org/licenses/MIT)
---

## О проекте

**Auth Server JWT** — REST-сервер аутентификации и прикладного API для экосистемы **EPill** (приложение для контроля приёма лекарств). Проект закрывает две задачи:

1. **Аутентификация и авторизация** — регистрация пользователей, выдача JWT-токенов, stateless-проверка подписи и ролей на каждом запросе.
2. **Прикладной API** — справочник лекарств и журнал приёма препаратов (`pill-swallowed`), привязанный к аутентифицированному пользователю.

Пользователи, роли, лекарства и события хранятся в **MySQL 8.x** через Spring Data JPA. Пароли хешируются **BCrypt**. Доступ к защищённым эндпоинтам — по заголовку `Authorization: Bearer <token>`.

> **Статус:** `v0.1 beta`. Ядро аутентификации стабильно, токены выпускаются и проверяются. Проект создавался как демонстрационный и запускался для показа работоспособности; готов к развитию (refresh-токены, нормальный CORS, тесты, OpenAPI).

---

## Ключевые возможности

| Функция | Описание |
|---|---|
| **JWT-аутентификация (HS256)** | Выпуск подписанных токенов с claims `roles` и настраиваемым временем жизни (по умолчанию `2h`). |
| **Stateless-архитектура** | `SessionCreationPolicy.STATELESS` — сервер не хранит сессии, каждый запрос валидируется по подписи токена. |
| **BCrypt-хеширование паролей** | `BCryptPasswordEncoder(10)` — пароли в БД не хранятся в открытом виде. |
| **Роли и разграничение доступа** | `ROLE_USER` при регистрации; `/admin` защищён `hasRole("ADMIN")` (роль заводится вручную в БД). |
| **MySQL + Spring Data JPA** | Сущности `User`, `Role`, `Drug`, `Event`; `ddl-auto: update` для dev-режима. |
| **Прикладной API** | `GET /drugs/{drugName}`, `POST /add-drug`, `POST /pill-swallowed` — справочник и журнал приёма. |
| **Единый формат ошибок** | `ApplicationError` (`status`, `message`, `timestamp`) для предсказуемых ответов API. |
| **Maven Wrapper** | Запуск без установки Maven: `./mvnw spring-boot:run`. |

---

## Технологический стек

- **Язык:** Java 17+
- **Фреймворк:** Spring Boot 3.x (Web, Security, Data JPA)
- **Аутентификация:** JSON Web Tokens (JJWT), HS256, BCrypt
- **База данных:** MySQL 8.x
- **Сборка:** Maven (wrapper `mvnw`)
- **Формат:** JSON over HTTP

---

## Архитектура

```
                    ┌─────────────────────────────────────────┐
                    │           Auth Server (:8080)           │
                    │            context-path: /epill         │
                    │                                         │
  Клиент (EPill)    │  POST /auth          → JWT              │
  ───────────────►  │  POST /sign-up       → создание юзера   │
                    │  GET  /info          → username         │
                    │  GET  /secured       → protected ping   │
                    │  GET  /admin         → ROLE_ADMIN       │
                    │  GET  /drugs/{name}  → карточка лекарства│
                    │  POST /add-drug      → добавление       │
                    │  POST /pill-swallowed→ журнал приёма     │
                    │                                         │
                    │   JwtRequestFilter → SecurityContext    │
                    │   UserService → UserDetailsService      │
                    │   JwtUtils → подпись/парсинг HS256      │
                    └───────────────────┬─────────────────────┘
                                        │ JPA
                                        ▼
                                 ┌─────────────┐
                                 │   MySQL 8   │
                                 │  userdb     │
                                 └─────────────┘
```

**Поток аутентификации:**

1. `POST /epill/sign-up` — регистрация, пароль хешируется BCrypt, выдаётся `ROLE_USER`.
2. `POST /epill/auth` — `AuthenticationManager` проверяет пару логин/пароль, `JwtUtils` выпускает токен с claims `roles`, `sub = username`, `exp = now + lifetime`.
3. Клиент шлёт `Authorization: Bearer <token>` на защищённые эндпоинты.
4. `JwtRequestFilter` (наследник `OncePerRequestFilter`) парсит токен, кладёт `UsernamePasswordAuthenticationToken` в `SecurityContext`. `ExpiredJwtException` и `SignatureException` логируются, запрос идёт дальше без аутентификации.

---

## API

Все эндпоинты — под контекстом **`/epill`**. Базовый URL: `http://localhost:8080/epill`.

### Публичные

| Метод | Путь | Тело | Ответ |
|---|---|---|---|
| `POST` | `/sign-up` | `{username, password, confirmPassword, email}` | `UserDTO {id, username, email}` или `ApplicationError` |
| `POST` | `/auth` | `{username, password}` | `JwtResponse {token}` или `401` |

### Защищённые (`Authorization: Bearer <token>`)

| Метод | Путь | Назначение | Ответ |
|---|---|---|---|
| `GET` | `/info` | Имя текущего пользователя | `String` (username) |
| `GET` | `/secured` | Проверка валидности токена | `"secured"` |
| `GET` | `/drugs/{drugName}` | Карточка лекарства | `Drug` или `400` «не найдено» |
| `POST` | `/add-drug` | Добавить лекарство | `String` или `400` «уже есть» |
| `POST` | `/pill-swallowed` | Записать событие приёма | `String` |

### Только `ROLE_ADMIN`

| Метод | Путь | Ответ |
|---|---|---|
| `GET` | `/admin` | `"for administrator only"` |

### Пример

```bash
# Регистрация
curl -X POST http://localhost:8080/epill/sign-up \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"pass123","confirmPassword":"pass123","email":"a@b.c"}'

# Логин → токен
curl -X POST http://localhost:8080/epill/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"pass123"}'

# Защищённый запрос
curl http://localhost:8080/epill/info \
  -H "Authorization: Bearer <token>"
```

---

## Модель данных

| Сущность | Таблица | Поля |
|---|---|---|
| `User` | `users` | `id`, `username`, `password` (BCrypt), `email`, `roles` (M2M) |
| `Role` | `roles` | `id`, `name` (`ROLE_USER`, `ROLE_ADMIN`) |
| `Drug` | `drugs` | `id`, `mnn`, `pharma_group`, `trade_name`, `symptoms` |
| `Event` | `events` | `id`, `name` (username), `drug`, `date`, `gender`, `height`, `weight`, `age`, `symptoms` |

Связь `users ↔ roles` — many-to-many через `user_roles`.

---

## Быстрый старт

### Требования

- JDK 17+
- MySQL 8.x
- Maven не обязателен — используется `./mvnw`

### 1. База данных

```sql
CREATE DATABASE userdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Схема создаётся автоматически (`ddl-auto: update`). Роль `ROLE_USER` должна существовать до первой регистрации — заведите её вручную:

```sql
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');  -- опционально, для /admin
```

### 2. Конфигурация

`src/main/resources/application.yaml` — параметры подключения и JWT:

```yaml
server:
  port: 8080
  servlet:
    context-path: /epill
  address: 0.0.0.0
jwt:
  secret: <demo-заглушка>
  lifetime: 2h
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/userdb
    username: root
    password: <demo-заглушка>
```

### 3. Запуск

```bash
git clone https://github.com/AlexanderSIv/auth-serverJWT.git
cd auth-serverJWT
./mvnw spring-boot:run
```

Сервер: `http://localhost:8080/epill`.

---

## Безопасность (demo-контекст)

Проект создавался как демонстрационный и запускался однократно для показа работоспособности JWT-аутентификации. Секрет подписи JWT и пароль БД в `application.yaml` — **намеренные демо-заглушки** для воспроизводимости «из коробки» (клонировал → создал БД → запустил → работает).

В production-сценарии:

- `jwt.secret` задаётся через env (`JWT_SECRET`) и должен быть криптостойким (≥256 бит, `openssl rand -base64 32`);
- пароль БД — через env / секрет-менеджер, не в репозитории;
- TLS терминируется на reverse-proxy (Nginx / Caddy / Cloudflare / ALB), приложение слушает HTTP только внутри приватной сети;
- CORS сужается до конкретных origin'ов клиента (сейчас стоит `*` + `allowCredentials(true)` — комбинация, которую браузеры не пропускают; для демо не критично, для прода — обязательно чинить);
- `ddl-auto: update` заменяется на миграции (Flyway / Liquibase).

Здесь этого нет сознательно: это не production-код, а портфолио-демо.

---


## Известные ограничения / планы развития

- [ ] Refresh-токены с ротацией
- [ ] Выдача `ROLE_ADMIN` через API или сид-скрипт (сейчас `/admin` недостижим из коробки)
- [ ] Вынос `JWT_SECRET` и пароля БД в env для prod-профиля
- [ ] Rate limiting на `/auth` и `/sign-up`
- [ ] OpenAPI / Swagger
- [ ] Unit- и integration-тесты (JUnit + Testcontainers)

---

## Лицензия

Распространяется под лицензией **MIT**.

---

**Автор:** [AlexanderSIv](https://github.com/AlexanderSIv)  
