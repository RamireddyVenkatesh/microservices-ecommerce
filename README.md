# A Microservices Approach to Product and Inventory Management

## Project Overview

This repository contains the complete back-end and infrastructure for a foundational E-commerce platform, built as a final project for COMP-699A (Fall 2025). The core mandate was to move away from monolithic architecture to a **scalable, fault-tolerant Microservices design**.

The entire system is containerized with Docker, proving the ability to deploy complex, decoupled services reliably.

### Key Architectural Pillars

| Pillar | Implementation | Academic Rationale |
| :--- | :--- | :--- |
| **Decomposition** | **Product Service** and **Inventory Service** are strictly separated by business capability. | Achieves high cohesion and loose coupling (Domain-Driven Design). |
| **Data Integrity** | **Database-per-Service** pattern: dedicated PostgreSQL instances (`product_db`, `inventory_db`). | Guarantees data encapsulation and eliminates data coupling. |
| **Resilience** | **Resilience4J Circuit Breaker** on Product-to-Inventory calls. | Prevents cascading failures by returning a reliable, degraded response (e.g., "Out of Stock") if a dependency is down. |
| **Security** | **JWT Authentication Filter** centralized within the API Gateway. | Streamlines development by separating security concerns from core business logic. |

---

## Technology Stack

| Component | Technology | Role |
| :--- | :--- | :--- |
| **Backend** | Spring Boot (Java 17) | Core framework for robust, production-ready microservices. |
| **Database** | PostgreSQL | Persistent data storage with transactional integrity (ACID). |
| **Orchestration** | Docker & Docker Compose | Containerization and environment management. |
| **Service Mesh** | Spring Cloud Gateway, Eureka | Handles secure routing, load balancing (`lb://`), and dynamic service discovery. |
| **Frontend** | React Admin UI (Minimal) | Serves as an administrative front-end to showcase back-end functionality. |

---

## Getting Started (Run the Full System)

The simplest way to run the entire, complex system is using Docker Compose.

### Prerequisites

1.  **Java 17+** (Required only to build the JAR files locally).
2.  **Docker Desktop** (or Docker Engine) installed and running.
3.  **Maven** (Required to package the JARs).

### Build and Run Steps

1.  **Clone the Repository:**
    ```bash
    git clone [YOUR_REPO_URL]
    cd microservices-ecommerce
    ```

2.  **Package All Services (Build the JARs):**
    You must build the final executable JAR files *before* Docker can copy them into the container images.
    ```bash
    mvn clean package -DskipTests
    ```

3.  **Launch the System (Databases, Eureka, and All Microservices):**
    This command builds the final Docker images, sets up the network, and launches all 7 containers.
    ```bash
    docker-compose up --build -d
    ```

4.  **Verify Services (Wait ~30 seconds for all services to register):**
    Check the Eureka Dashboard:
    ```    http://localhost:8761
    ```
    You should see `API-GATEWAY`, `PRODUCT-SERVICE`, and `INVENTORY-SERVICE` all listed as **UP**.

5.  **Start the Frontend UI:**
    ```bash
    cd admin-ui
    npm install
    npm start
    ```

---

## Final Verification and Demo Endpoints

All external calls must be routed through the API Gateway (`http://localhost:8080`).

| Test Scenario | Endpoint / Method | Required Header | Expected Result |
| :--- | :--- | :--- | :--- |
| **Success / Get All Products** | `GET /api/products` | `Authorization: Bearer [YOUR_JWT_TOKEN]` | `200 OK` + JSON Array of products. |
| **Security Test** | `GET /api/products` | (No Header) | `401 Unauthorized` (Gateway's JWT Filter working). |
| **Resilience Test (CRITICAL)** | `GET /api/products/{id}` | `Authorization: Bearer [TOKEN]` | **1. Stop Inventory Service:** `docker stop inventory-service` **2. Refresh browser.** Status remains `500 Server Error for HTTP GET "/api/products"` |
| **Inventory Update (M3)** | `PUT /api/inventory/{sku}/stock` | `Authorization: Bearer [TOKEN]` | `200 OK` (Tests transactional stock management). |

*(Note: Use the included `JwtGenerator.java` utility in the `api-gateway` project to get a valid, long-lived JWT token.)*