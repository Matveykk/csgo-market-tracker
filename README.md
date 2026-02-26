# 🎮 CS:GO Market Tracker

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)

**REST API для отслеживания цен на скины CS:GO с исторической аналитикой и статистикой**

[Демо API](http://localhost:8080/swagger-ui.html) • [Документация](#-api-documentation)

</div>

---

## 📋 О проекте

CS:GO Market Tracker — это backend-система для мониторинга и анализа цен на скины CS:GO. Проект предоставляет REST API для работы со скинами, историей цен и статистикой рынка.

### ✨ Основные возможности

- 📊 **Управление скинами** — CRUD операции для CS:GO скинов
- 💰 **История цен** — отслеживание изменения цен во времени
- 📈 **Аналитика** — статистика (мин/макс/средняя цена) за период
- 🔍 **Поиск** — быстрый поиск скинов по названию
- 📖 **Swagger UI** — интерактивная документация API
- 🐳 **Docker** — простой запуск через Docker Compose
- 🗄️ **Flyway** — версионирование базы данных

---

## 🛠 Технологический стек

### Backend
- **Java 17** — современная версия JDK
- **Spring Boot 3.2** — основной фреймворк
- **Spring Data JPA** — работа с базой данных
- **Spring Validation** — валидация входных данных
- **Lombok** — уменьшение boilerplate кода

### База данных
- **PostgreSQL 15** — основная БД
- **Flyway** — миграции схемы БД
- **Redis 7** — кэширование (готовится к использованию)

### DevOps
- **Docker & Docker Compose** — контейнеризация
- **Maven** — сборка проекта

### Документация
- **Swagger/OpenAPI 3** — автоматическая документация API

---

## 🚀 Быстрый старт

### Требования

- Java 17+
- Docker & Docker Compose
- Maven (опционально, есть Maven Wrapper)

### Установка и запуск

1. **Клонируй репозиторий**
```bash
git clone https://github.com/Matveykk/csgo-market-tracker.git
cd csgo-market-tracker
```

2. **Запусти базу данных (PostgreSQL + Redis)**
```bash
docker compose up -d
```

3. **Запусти приложение**

**Через Maven:**
```bash
./mvnw spring-boot:run
```

**Через JAR:**
```bash
./mvnw clean package
java -jar target/csgo-market-tracker-0.0.1-SNAPSHOT.jar
```

4. **Открой Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

---

## 📊 Структура базы данных

### Таблица `skins`
Информация о скинах CS:GO

| Колонка | Тип | Описание |
|---------|-----|----------|
| `id` | BIGSERIAL | Первичный ключ |
| `market_hash_name` | VARCHAR(255) | Уникальное название на Steam Market |
| `weapon_type` | VARCHAR(100) | Тип оружия (Rifle, Pistol, Knife и т.д.) |
| `skin_name` | VARCHAR(100) | Название скина |
| `wear` | VARCHAR(50) | Степень износа (Factory New, Field-Tested и т.д.) |
| `rarity` | VARCHAR(50) | Редкость (Consumer, Covert и т.д.) |
| `image_url` | TEXT | URL изображения скина |
| `created_at` | TIMESTAMP | Дата создания записи |
| `updated_at` | TIMESTAMP | Дата последнего обновления |

### Таблица `price_history`
История цен скинов

| Колонка | Тип | Описание |
|---------|-----|----------|
| `id` | BIGSERIAL | Первичный ключ |
| `skin_id` | BIGINT | Foreign Key → skins.id |
| `price` | DECIMAL(10,2) | Цена в USD |
| `volume` | INT | Объём продаж за 24ч |
| `source` | VARCHAR(50) | Источник данных (Steam Market, Manual и т.д.) |
| `recorded_at` | TIMESTAMP | Время записи цены |

---

## 📖 API Documentation

### Управление скинами

#### `GET /api/v1/skins`
Получить список всех скинов с пагинацией

**Параметры:**
- `page` (int, default: 0) — номер страницы
- `size` (int, default: 20) — размер страницы
- `sortBy` (string, default: "id") — поле сортировки
- `sortDirection` (string, default: "ASC") — направление сортировки

**Ответ:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "marketHashName": "AK-47 | Redline (Field-Tested)",
      "weaponType": "Rifle",
      "skinName": "Redline",
      "wear": "Field-Tested",
      "rarity": "Classified",
      "imageUrl": "https://...",
      "createdAt": "2026-02-25T10:30:00",
      "updatedAt": "2026-02-25T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

---

#### `GET /api/v1/skins/{id}`
Получить скин по ID

**Ответ:** `200 OK` или `400 Bad Request`

---

#### `GET /api/v1/skins/search?query={query}`
Поиск скинов по названию

**Параметры:**
- `query` (string, required) — поисковый запрос

**Ответ:** `200 OK` если найдено, `404 Not Found` если не найдено
```json
{
  "results": [...],
  "totalResults": 5,
  "query": "redline",
  "message": "5 skin(s) found"
}
```

---

#### `POST /api/v1/skins`
Создать новый скин

**Request Body:**
```json
{
  "marketHashName": "AWP | Dragon Lore (Factory New)",
  "weaponType": "Sniper Rifle",
  "skinName": "Dragon Lore",
  "wear": "Factory New",
  "rarity": "Covert",
  "imageUrl": "https://..."
}
```

**Ответ:** `201 Created`

---

#### `PUT /api/v1/skins/{id}`
Обновить существующий скин

**Ответ:** `200 OK`

---

#### `DELETE /api/v1/skins/{id}`
Удалить скин

**Ответ:** `204 No Content`

---

### Управление ценами

#### `POST /api/v1/prices`
Добавить новую цену для скина

**Request Body:**
```json
{
  "skinId": 1,
  "price": 8500.50,
  "volume": 12,
  "source": "Steam Market"
}
```

**Ответ:** `201 Created`

---

#### `GET /api/v1/prices/skin/{skinId}`
Получить всю историю цен для скина

**Ответ:** `200 OK`
```json
[
  {
    "id": 1,
    "skinId": 1,
    "skinName": "AWP | Dragon Lore (Factory New)",
    "price": 8500.50,
    "volume": 12,
    "source": "Steam Market",
    "recordedAt": "2026-02-25T14:30:00"
  }
]
```

---

#### `GET /api/v1/prices/skin/{skinId}/current`
Получить текущую (последнюю) цену скина

**Ответ:** `200 OK`

---

#### `GET /api/v1/prices/skin/{skinId}/range`
Получить историю цен за период

**Параметры:**
- `startDate` (ISO 8601, required) — начало периода
- `endDate` (ISO 8601, required) — конец периода

**Пример:** `/api/v1/prices/skin/1/range?startDate=2026-02-01T00:00:00&endDate=2026-02-28T23:59:59`

---

#### `GET /api/v1/prices/skin/{skinId}/stats`
Получить статистику цен за период

**Параметры:**
- `startDate` (ISO 8601, required)
- `endDate` (ISO 8601, required)

**Ответ:** `200 OK`
```json
{
  "skinId": 1,
  "skinName": "AWP | Dragon Lore (Factory New)",
  "currentPrice": 8500.50,
  "averagePrice": 8450.75,
  "minPrice": 8300.00,
  "maxPrice": 8600.00,
  "dataPoints": 15
}
```

---

## 🧪 Примеры использования

### Создание скина и добавление цен
```bash
# 1. Создать скин
curl -X POST http://localhost:8080/api/v1/skins \
  -H "Content-Type: application/json" \
  -d '{
    "marketHashName": "M4A4 | Howl (Field-Tested)",
    "weaponType": "Rifle",
    "skinName": "Howl",
    "wear": "Field-Tested",
    "rarity": "Contraband"
  }'

# 2. Добавить цену
curl -X POST http://localhost:8080/api/v1/prices \
  -H "Content-Type: application/json" \
  -d '{
    "skinId": 1,
    "price": 4500.00,
    "volume": 5,
    "source": "Steam Market"
  }'

# 3. Получить статистику
curl "http://localhost:8080/api/v1/prices/skin/1/stats?startDate=2026-02-01T00:00:00&endDate=2026-02-28T23:59:59"
```

---

## 🤝 Contributing

Contributions are welcome! Если хочешь помочь с проектом:

1. Fork репозиторий
2. Создай feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Открой Pull Request

---

## 👤 Автор

**Матвей Бойченцев**

- GitHub: [@Matveykk](https://github.com/Matveykk)
- Telegram: [@thereisofc](https://t.me/thereisofc)
- Email: boymotya@gmail.com

---

## 📄 Лицензия

Этот проект создан в образовательных целях.

---

## 🙏 Благодарности

- [Steam Community Market](https://steamcommunity.com/market/) — источник данных
- [Spring Boot](https://spring.io/projects/spring-boot) — основной фреймворк
- [Swagger UI](https://swagger.io/) — документация API

---

<div align="center">

⭐ **Star этот репозиторий, если он был полезен!** ⭐

Made with ❤️ by Matveykk

</div>