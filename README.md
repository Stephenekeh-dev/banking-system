#  Banking Microservices System

A production-ready microservices banking application built with **Spring Boot 3**, **Kafka**, **PostgreSQL**, **Docker**, and **JWT authentication**.

---

##  Architecture Overview

```
                        ┌──────────────────────────────────┐
                        │           API Gateway             │
                        │        (Spring Cloud GW)          │
                        │           Port: 8080              │
                        └────────────┬─────────────────────┘
                                     │ Routes
          ┌──────────────────────────┼───────────────────────────┐
          │                          │                           │
          ▼                          ▼                           ▼
  ┌───────────────┐        ┌─────────────────┐        ┌──────────────────┐
  │  auth-service │        │ account-service  │        │transaction-service│
  │   Port: 8081  │        │   Port: 8082    │        │    Port: 8083    │
  │  authdb (PG)  │        │  accountdb (PG) │        │ transactiondb(PG)│
  └───────┬───────┘        └────────┬────────┘        └────────┬─────────┘
          │                         │                           │
          │              ┌──────────┴───────────────────────────┘
          │              │           Kafka Events
          │              ▼
          │    ┌─────────────────────────────────────────────────┐
          │    │              Apache Kafka (Port: 9092)           │
          │    │   Topics: transactions, audit-events,           │
          │    │           notification-topic, fraud-events       │
          │    └──┬──────────┬───────────────┬───────────────────┘
          │       │          │               │
          ▼       ▼          ▼               ▼
  ┌─────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
  │audit-service│ │notification- │ │fraud-service │ │approval-     │
  │  Port: 8085 │ │   service    │ │  Port: 8086  │ │  service     │
  │ auditdb(PG) │ │  Port: 8084  │ │  frauddb(PG) │ │  Port: 8087  │
  └─────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

---

## Service Communication Flow

```
User → Gateway → auth-service  ──► issues JWT token
                                         │
User → Gateway → account-service ◄───────┘ (JWT validated)
                      │
                      │ Kafka: "transactions" topic
                      ▼
              transaction-service (persists tx records)
                      │
                      ├─── Kafka: "audit-events" ──────► audit-service (logs all events)
                      ├─── Kafka: "notification-topic" ► notification-service (emails/SMS)
                      └─── Kafka: "fraud-events" ──────► fraud-service (flags suspicious tx)
                                                               │
                                                               ▼
                                                        approval-service
                                                     (approves/rejects flagged tx)
```

---

## 🔐 Authentication Flow

```
1. POST /api/auth/register  → Creates user, hashes password, returns 200
2. POST /api/auth/login     → Validates credentials, returns JWT (10 hr expiry)
3. All other requests       → Gateway forwards JWT to each service
4. Each service             → Validates JWT using shared secret
```

---

##  Services Summary

| Service              | Port | Database        | Responsibilities                        |
|----------------------|------|-----------------|------------------------------------------|
| `gateway`            | 8080 | None            | Route requests, rate limiting            |
| `auth-service`       | 8081 | authdb          | Register, Login, JWT issuance            |
| `account-service`    | 8082 | accountdb       | Create/manage accounts, balance updates  |
| `transaction-service`| 8083 | transactiondb   | Record & query transactions              |
| `notification-service`| 8084| None            | Send email/SMS notifications via Kafka   |
| `audit-service`      | 8085 | auditdb         | Log all system events via Kafka          |
| `fraud-service`      | 8086 | frauddb         | Detect and record suspicious activity    |
| `approval-service`   | 8087 | approvaldb      | Approve/reject flagged transactions      |

---

##  Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17+
- Maven 3.8+

### Run Everything with Docker Compose
```bash
git clone <repo-url>
cd banking-system
docker-compose up --build
```

### Run Individual Services Locally
```bash
# Start infrastructure first
docker-compose up -d postgres kafka zookeeper

# Run a service
cd auth-service
mvn spring-boot:run
```

---

## 🌐 API Endpoints

### Auth Service (`/api/auth`)
| Method | Endpoint           | Auth | Description         |
|--------|--------------------|------|---------------------|
| POST   | `/register`        | No   | Register new user   |
| POST   | `/login`           | No   | Login, get JWT      |

### Account Service (`/api/accounts`)
| Method | Endpoint                    | Auth | Description              |
|--------|-----------------------------|------|--------------------------|
| POST   | `/create`                   | JWT  | Create new account       |
| GET    | `/{accountNumber}`          | JWT  | Get account details      |
| POST   | `/update-balance`           | JWT  | Deposit or Withdraw      |

### Transaction Service (`/api/transactions`)
| Method | Endpoint   | Auth | Description               |
|--------|------------|------|---------------------------|
| POST   | `/`        | JWT  | Create transaction record |
| GET    | `/`        | JWT  | Get user's transactions   |

### Fraud Service (`/api/fraud`)
| Method | Endpoint                          | Auth | Description                   |
|--------|-----------------------------------|------|-------------------------------|
| POST   | `/`                               | JWT  | Flag suspicious activity      |
| GET    | `/`                               | JWT  | Get all fraud records         |
| GET    | `/user/{userId}`                  | JWT  | Get fraud by user             |
| GET    | `/transaction/{transactionId}`    | JWT  | Get fraud by transaction      |
| DELETE | `/{id}`                           | JWT  | Remove fraud record           |

### Approval Service (`/api/approvals`)
| Method | Endpoint                              | Auth | Description                 |
|--------|---------------------------------------|------|-----------------------------|
| POST   | `/`                                   | JWT  | Create approval request     |
| PUT    | `/{id}/status`                        | JWT  | Update approval status      |
| GET    | `/transaction/{transactionId}`        | JWT  | Get approvals by transaction|
| GET    | `/status/{status}`                    | JWT  | Get approvals by status     |

---

## Kafka Topics

| Topic                | Producer            | Consumer(s)                        |
|----------------------|---------------------|------------------------------------|
| `transactions`       | account-service     | transaction-service                |
| `audit-events`       | auth-service, all   | audit-service                      |
| `notification-topic` | transaction-service | notification-service               |
| `fraud-events`       | transaction-service | fraud-service                      |

---

## 🗄️ Database Schema

### authdb
```sql
users (id, email, password, full_name, profile_picture_url, role)
```

### accountdb
```sql
accounts (id, user_id, user_email, account_number, account_type, balance, created_at)
```

### transactiondb
```sql
transactions (id, account_number, user_email, amount, type, target_account, created_at)
```

### auditdb
```sql
audit_logs (id, service_name, action, performed_by, details, timestamp)
```

### approvaldb
```sql
approvals (id, transaction_id, status, reason, created_at, updated_at)
```

### frauddb
```sql
fraud_activities (id, transaction_id, user_id, reason, amount, timestamp)
```

---

## Environment Variables

All sensitive values should be set as environment variables in production:

| Variable              | Default (dev)          | Description                  |
|-----------------------|------------------------|------------------------------|
| `JWT_SECRET`          | (set in env)           | Shared JWT signing secret    |
| `DB_PASSWORD`         | (set in env)           | PostgreSQL password          |
| `KAFKA_BOOTSTRAP`     | `kafka:9092`           | Kafka broker address         |
| `UPLOAD_DIR`          | `uploads/`             | Profile picture upload dir   |

---

##  Testing

```bash
# Run all tests across all services
mvn test

# Run tests for a specific service
cd account-service && mvn test
```

---

## Known Issues & TODOs

- [ ] Move JWT secret to environment variable / Vault (currently hardcoded)
- [ ] Replace `localhost` Kafka/DB URLs with Docker service names
- [ ] Implement actual fraud detection logic (currently placeholder)
- [ ] Add distributed tracing (Sleuth/Zipkin)
- [ ] Add Eureka service discovery
- [ ] Switch profile picture storage to S3/cloud storage
- [ ] Add pagination to list endpoints
- [ ] Implement circuit breaker (Resilience4j)