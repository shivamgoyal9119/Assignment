# 🚀 Backend Engineering Assignment

### Spring Boot Microservice with Redis Guardrails & Notification Engine

---

## 📌 Overview

This project implements a **high-performance, stateless backend microservice** using Spring Boot that enforces strict **Redis-based guardrails** to control bot interactions and prevent system abuse.

It simulates a **social media backend system** with:

* Post creation
* Commenting (human + bot)
* Likes
* Real-time virality tracking

The system is designed to handle **high concurrency**, ensuring **data integrity and scalability** under heavy load.

---

## 🧰 Tech Stack

| Layer                 | Technology                  |
| --------------------- | --------------------------- |
| Backend               | Java 17, Spring Boot 3      |
| Database              | PostgreSQL                  |
| Cache / Control Layer | Redis                       |
| ORM                   | Spring Data JPA (Hibernate) |
| Containerization      | Docker                      |

---

## 🏗️ Architecture Overview

* **PostgreSQL** → Source of truth (persistent data)
* **Redis** → Real-time control layer (counters, locks, cooldowns)
* **Spring Boot** → REST API + business logic
* **Scheduler** → Event-driven notification batching

---

## 🔥 Core Features

---

### 📊 1. Virality Engine (Real-Time Scoring)

Each interaction updates a Redis counter instantly:

| Interaction   | Score |
| ------------- | ----- |
| Bot Reply     | +1    |
| Human Like    | +20   |
| Human Comment | +50   |

**Redis Key**

```text
post:{id}:virality_score
```

**Implementation**

* Uses Redis atomic `INCR`
* Eliminates DB load for real-time metrics
* Ensures consistent updates under concurrency

---

### 🔒 2. Redis Guardrails (Concurrency Control)

#### ✅ Horizontal Cap (Bot Limit)

* Maximum **100 bot replies per post**

**Redis Key**

```text
post:{id}:bot_count
```

**Approach**

* Atomic increment using Redis
* Protected using **distributed lock (`SETNX`)**
* Guarantees strict enforcement under concurrent requests

---

#### ✅ Vertical Cap (Thread Depth)

* Maximum **depth level = 20**

```java
if (depthLevel > 20) reject
```

---

#### ✅ Cooldown Cap (Bot ↔ Human)

* A bot cannot interact with the same user more than once every **10 minutes**

**Redis Key**

```text
cooldown:bot_{id}:human_{id}
```

**Implementation**

* Redis `SET` with TTL
* Prevents spam interactions efficiently

---

### 🧊 3. Notification Engine (Smart Batching)

Prevents notification flooding using Redis-based throttling.

#### Behavior

| Condition       | Action                      |
| --------------- | --------------------------- |
| No cooldown     | Send immediate notification |
| Cooldown active | Store in Redis list         |

---

#### Redis Keys

```text
user:{id}:notif_cooldown
user:{id}:pending_notifs
```

---

#### ⏱ Scheduler

* Runs every **5 minutes**
* Aggregates pending notifications

**Output Example**

```text
Summarized Push Notification: Bot X and N others interacted with your posts.
```

---

## 🧪 Concurrency Handling (Key Highlight)

### Problem

Simultaneous bot requests can bypass limits due to race conditions.

### Solution

* Redis atomic operations (`INCR`)
* Distributed locking using:

```java
setIfAbsent (SETNX)
```

### Result

* Strict enforcement of **100 bot replies limit**
* No race conditions
* Successfully validated under **200 concurrent requests**

---

## 📡 API Endpoints

### 📝 Create Post

```http
POST /api/posts
```

---

### 💬 Add Comment

```http
POST /api/posts/{postId}/comments
```

---

### 👍 Like Post

```http
POST /api/posts/{postId}/like
```

---

## 🐳 Running Locally

### 1. Start Services

```bash
docker-compose up -d
```

---

### 2. Services

| Service    | Port |
| ---------- | ---- |
| PostgreSQL | 5432 |
| Redis      | 6379 |

---

### 3. Run Application

```bash
mvn spring-boot:run
```

---

## 📁 Project Structure

```
src/
 ├── controller/
 ├── service/
 ├── repository/
 ├── model/
 ├── config/
docker-compose.yml
README.md
```

---

## 🧠 Key Design Decisions

* Redis used as **gatekeeper layer** (not persistent storage)
* PostgreSQL used as **source of truth**
* Backend remains **completely stateless**
* All guardrails enforced **before database writes**
* Designed to handle **high concurrency safely**

---

## 🚀 Highlights

* Thread-safe Redis guardrails
* Real-time scoring system
* Distributed cooldown enforcement
* Event-driven notification batching
* Clean, modular backend architecture

---

## 🏁 Conclusion

This project demonstrates:

* Scalable backend architecture
* Strong understanding of Redis and concurrency control
* Real-world system design patterns
* Production-grade API development practices

---
