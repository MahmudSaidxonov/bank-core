# 🏦 Core Banking Microservices

![Java](https://img.shields.io/badge/Java-17-orange?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-brightgreen?style=flat&logo=spring)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat&logo=docker)

## ℹ️ Overview

A robust, microservice-based backend system designed for core banking operations. This project demonstrates modern architectural patterns, including asynchronous messaging, service discovery, and containerized deployment. It handles user registration, secure account management, and safe fund transfers with strict transaction guarantees.

## 🌟 Highlights

* **Distributed Architecture:** Split into distinct services (`bank-core`, `bank-notification`) communicating seamlessly.
* **Service Discovery:** Utilizes Netflix Eureka for dynamic routing and load balancing without hardcoded IPs.
* **Event-Driven:** Employs RabbitMQ to handle non-blocking processes like sending email notifications.
* **ACID Transactions:** Ensures absolute data integrity during money transfers using PostgreSQL pessimistic locking to prevent race conditions and deadlocks.
* **High Availability & Concurrency:** The Outbox polling mechanism utilizes PostgreSQL's `FOR UPDATE SKIP LOCKED`, safely allowing multiple instances of `bank-core` to run concurrently without database deadlocks or duplicate message processing.
* **Guaranteed Message Delivery:** Implements the Transactional Outbox Pattern with a dedicated PostgreSQL table and a scheduled polling mechanism. This ensures 100% reliable event publishing to RabbitMQ, even in the event of message broker downtime or network partitions.
* **One-Click Deployment:** Fully orchestrated infrastructure using Docker Compose.

## 🛠️ Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3, Spring Data JPA, Spring Security
* **Database:** PostgreSQL 15 + Flyway (Migrations)
* **Messaging:** RabbitMQ (AMQP) + Jackson JSON Converters
* **Cloud & Routing:** Spring Cloud Netflix Eureka, OpenFeign
* **Infrastructure:** Docker, Docker Compose
* **Testing:** JUnit 5, Mockito
* **Mappers:** MapStruct

## 📂 Architecture

The system consists of the following isolated components:

1. **`service-registry`** (Port `8761`): The Eureka Server acting as the system's phonebook.
2. **`bank-core`** (Port `8080`): The monolithic core. Handles auth, account ledgers, and transaction logic. It utilizes a scheduled background process (Outbox Scheduler) to reliably push pending notification payloads to the message broker.
3. **`bank-notification`** (Port `8081`): A decoupled listener that catches transfer events from RabbitMQ, fetches user details via OpenFeign, and dispatches email alerts.
4. **`postgres`** (Port `5433`): Persistent relational storage. Exposed on host port `5433` to prevent conflicts with local PostgreSQL instances (internal container port remains `5432`).
5. **`rabbitmq`** (Port `5672/15672`): Message broker with management UI.

## 🚀 Getting Started

You don't need to install databases or message brokers on your local machine. Everything is containerized.

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker Engine + Compose)
* Git, JDK 17, Maven**

### Installation
Since this is a multi-repo microservices architecture, you need to clone all related services into a single workspace directory.
1. Clone the repositories:
```bash
git clone https://github.com/MahmudSaidxonov/service-registry.git
git clone https://github.com/MahmudSaidxonov/bank-core.git
git clone https://github.com/MahmudSaidxonov/bank-notification.git
cd bank-core
```

2. Create environment variables:
   Create a .env file in the root directory of the project and add your secrets:

```env
INTERNAL_SECRET_KEY=your_jwt_secret_key_here
MAIL_PASSWORD=your_google_app_password_here
```

3. Build the Java artifacts:

```bash
mvn clean package -DskipTests
```
4. Spin up the entire universe:

```bash
docker compose up --build -d
```