# 📋 ASSIGNMENT & RESEARCH REPORT
## IMPLEMENT SPRING CLOUD EUREKA & LOAD BALANCING
### Modern Distributed E-Commerce Microservices Platform

---

> **Student / Author:** ISTAD Advanced Software Engineering  
> **Topic:** Spring Cloud Netflix Eureka, Client-Side Load Balancing, Hexagonal Architecture  
> **Course:** Advanced Microservices Architecture  
> **Academic Score Target:** 100 / 100 Points (+80 pts Implementation +20 pts Best Practices)  

---

## 📑 TABLE OF CONTENTS
1. [Executive Summary & Architecture Overview](#1-executive-summary--architecture-overview)
2. [Part 1: Hard-Coded Service Communication (+20 pts)](#2-part-1-hard-coded-service-communication-20-pts)
   - 1.1 Architecture & Implementation Mechanics
   - 1.2 Configuration Setup
   - 1.3 Step-by-Step Execution & Testing Procedure
   - 1.4 Screenshot Submission Checklist
   - 1.5 Analytical Report: Problems, Limitations & Disadvantages of Hard-Coded Addresses
3. [Part 2: Research Spring Cloud Eureka (+20 pts)](#3-part-2-research-spring-cloud-eureka-20-pts)
   - 2.1 Origin and Historical Context (Netflix OSS)
   - 2.2 Purpose and CAP Theorem Positioning (AP System)
   - 2.3 Key Capabilities
   - 2.4 Drawbacks and Limitations
   - 2.5 Component Deep Dive: Server, Client, Registration, and Discovery
   - 2.6 Eureka Lifecycle Sequence Diagram
4. [Part 3: Integrate Eureka & Dynamic Load Balancing (+20 pts)](#4-part-3-integrate-eureka--dynamic-load-balancing-20-pts)
   - 3.1 Migration Strategy & Configuration Setup
   - 3.2 Dynamic Resolution & Client-Side Load Balancing
   - 3.3 Step-by-Step Execution & Verification Guide
   - 3.4 Screenshot Submission Checklist
   - 3.5 Comparative Analysis: Hard-Coded REST vs. Eureka Service Discovery
   - 3.6 Remaining Limitations of Eureka Discovery
5. [Part 4: Experiment with Eureka Health Tracking & Resiliency (+20 pts)](#5-part-4-experiment-with-eureka-health-tracking--resiliency-20-pts)
   - 4.1 Health Status Tracking & Actuator Integration
   - 4.2 Configuration Tuning Parameters
   - 4.3 Experiment 1: Normal Startup & Dynamic Registration
   - 4.4 Experiment 2: Graceful Service Shutdown (Deregistration)
   - 4.5 Experiment 3: Abrupt Failure / Crash (Lease Expiration & Eviction)
   - 4.6 Experiment 4: Self-Preservation Mode Trigger
   - 4.7 Technical Research: Client-Side vs Server-Side Service Discovery & Load Balancing
6. [Best Practices Code & E-Commerce Implementation (+20 pts)](#6-best-practices-code--e-commerce-implementation-20-pts)
   - 6.1 Clean / Hexagonal Ports & Adapters Architecture
   - 6.2 Service Capabilities & Functionalities (Customer, Business, Order, Payment)
   - 6.3 Complete End-to-End Order Creation Flow Trace
   - 6.4 Best Practices Coding Checklist
7. [Appendix: Command & Payload Reference](#7-appendix-command--payload-reference)

---

## 1. Executive Summary & Architecture Overview

Modern enterprise microservice architectures require components to locate and interact with each other seamlessly. In static or traditional environments, services address one another using hard-coded hostnames or IP addresses. However, modern cloud and containerized environments feature dynamic elasticity: containers scale horizontally, IP addresses change during restarts, and physical nodes fluctuate.

This project implements, evaluates, and documents both **Hard-Coded Service Communication** and **Dynamic Service Discovery with Client-Side Load Balancing** using **Spring Cloud Netflix Eureka** and **Spring Cloud LoadBalancer** within an enterprise **E-Commerce Platform** comprising four communicative microservices and a dedicated discovery server:

```mermaid
flowchart TD
    subgraph Discovery ["Service Discovery Layer"]
        Eureka["Eureka Discovery Server\n(Port 8761)"]
    end

    subgraph CoreServices ["E-Commerce Microservices"]
        Customer["Customer Service\n(Port 9091)"]
        Business["Business Service\n(Port 9093)"]
        Order["Order Service (Orchestrator)\n(Port 9092)"]
        Payment["Payment Service\n(Port 9094)"]
    end

    Customer -- "Register & Heartbeat (30s)" --> Eureka
    Business -- "Register & Heartbeat (30s)" --> Eureka
    Order -- "Register & Heartbeat (30s)" --> Eureka
    Payment -- "Register & Heartbeat (30s)" --> Eureka

    Order -. "1. Fetch Registry Cache" .-> Eureka
    Order == "2. Validate Customer" ==> Customer
    Order == "3. Validate Business" ==> Business
    Order == "4. Process Payment" ==> Payment

    classDef discovery fill:#f9f,stroke:#333,stroke-width:2px;
    classDef service fill:#bbf,stroke:#333,stroke-width:1px;
    class Eureka discovery;
    class Customer,Business,Order,Payment service;
```

### Architectural Services Matrix

| Microservice | Port | Domain Responsibility | Database / Storage | Communication Roles |
| :--- | :---: | :--- | :--- | :--- |
| **`discovery-server`** | `8761` | Dynamic Service Registry & Health Tracking | In-Memory Registry (`ConcurrentHashMap`) | Eureka Registry Server |
| **`customer-service`** | `9091` | Customer identity, profile, and account status | PostgreSQL / MongoDB (Reactive fallback) | Eureka Client, Provider |
| **`business-service`** | `9093` | Merchant, store, and catalog validation | MongoDB (Reactive fallback) | Eureka Client, Provider |
| **`order-service`** | `9092` | Order lifecycle, orchestrator, order tracking | PostgreSQL (`ecom_db`) | Eureka Client, Consumer & Orchestrator |
| **`payment-service`** | `9094` | Transaction processing, payment validation | PostgreSQL (`ecom_db`) | Eureka Client, Provider |

---

## 2. Part 1: Hard-Coded Service Communication (+20 pts)

### 1.1 Architecture & Implementation Mechanics
In Part 1, `order-service` interacts with upstream services by referencing exact, static network locations (`http://localhost:9091`, `http://localhost:9093`, and `http://localhost:9094`).

```mermaid
flowchart LR
    Client([HTTP Client / Postman]) -->|POST /api/orders| OrderService[Order Service :9092]
    
    subgraph DirectCommunication ["Part 1: Hard-Coded Direct REST Calls"]
        OrderService -->|GET http://localhost:9091/api/customers/...| CustomerService[Customer Service :9091]
        OrderService -->|GET http://localhost:9093/api/businesses/...| BusinessService[Business Service :9093]
        OrderService -->|POST http://localhost:9094/api/payments| PaymentService[Payment Service :9094]
    end
```

To support toggling between Part 1 and Part 3 without altering business logic, a configurable dual-mode client adapter pattern is implemented in `order-service`:

1. **Direct RestTemplate Bean** (`BeanConfig.java`):
   ```java
   @Bean(name = "directRestTemplate")
   public RestTemplate directRestTemplate() {
       return new RestTemplate(); // Standard RestTemplate without @LoadBalanced
   }
   ```
2. **Client Adapters** (`CustomerClientAdapter`, `BusinessClientAdapter`, `PaymentClientAdapter`):
   When `ecommerce.communication.mode` is set to `hardcoded`, the adapter bypasses service discovery and issues calls directly to configured host:port URLs:
   ```java
   if ("hardcoded".equalsIgnoreCase(communicationMode)) {
       url = customerHardcodedUrl + "/api/customers/" + customerId;
       restTemplate = directRestTemplate;
       log.info("[HARD-CODED MODE] Calling Customer Service at: {}", url);
   }
   ```

### 1.2 Configuration Setup
To activate Part 1 hard-coded communication, configure `order-service/order-service-main/src/main/resources/application.yaml`:

```yaml
# ========================================================
# Part 1 Configuration: Hard-Coded Service Addresses
# ========================================================
ecommerce:
  communication:
    mode: hardcoded
  services:
    customer-url: http://localhost:9091
    business-url: http://localhost:9093
    payment-url: http://localhost:9094

# Disable Eureka registration and registry fetching for pure hard-coded mode
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### 1.3 Step-by-Step Execution & Testing Procedure

#### Step 1: Boot Up the Provider Microservices
Start each target service in individual terminal windows or inside IntelliJ IDEA:
```powershell
# Terminal 1: Customer Service (Port 9091)
.\gradlew :microservices:customer-service:customer-service-main:bootRun

# Terminal 2: Business Service (Port 9093)
.\gradlew :microservices:business-service:business-service-main:bootRun

# Terminal 3: Payment Service (Port 9094)
.\gradlew :microservices:payment-service:payment-service-main:bootRun
```

#### Step 2: Boot Up Order Service in Hard-Coded Mode
```powershell
# Terminal 4: Order Service (Port 9092)
.\gradlew :microservices:order-service:order-service-main:bootRun
```

#### Step 3: Send an Order Creation Request
Execute the following HTTP request using cURL or Postman:

```bash
curl -X POST http://localhost:9092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "d3b07384-d113-494b-9c8e-aa02d8f94119",
    "businessId": "e4c18495-e224-4a5c-ad9f-bb13e9a05220",
    "price": 45.00,
    "items": [
      {
        "productId": "f5d29506-f335-4b6d-be0a-cc24fa106331",
        "quantity": 1,
        "price": 45.00,
        "subTotal": 45.00
      }
    ],
    "deliveryAddress": {
      "street": "Russian Federation Blvd",
      "postalCode": "12000",
      "city": "Phnom Penh"
    }
  }'
```

#### Step 4: Verify the JSON Response
Expected HTTP Status: `201 Created`
```json
{
  "orderTrackingId": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
  "orderStatus": "PENDING",
  "message": "Order created and paid successfully (Payment ID: 9f8e7d6c-5b4a-3210-fedc-ba9876543210)"
}
```

#### Step 5: Verify the Order Service Console Log
Check the terminal log of `order-service` to confirm the hard-coded execution:
```text
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Step 1: Validating customer d3b07384... via Customer Service
INFO [order-service] k.e.i.p.o.c.CustomerClientAdapter : [HARD-CODED MODE] Calling Customer Service at: http://localhost:9091/api/customers/d3b07384-d113-494b-9c8e-aa02d8f94119
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Step 2: Validating business e4c18495... via Business Service
INFO [order-service] k.e.i.p.o.c.BusinessClientAdapter : [HARD-CODED MODE] Calling Business Service at: http://localhost:9093/api/businesses/e4c18495-e224-4a5c-ad9f-bb13e9a05220
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Step 3: Processing payment of $45.00 for Order ... via Payment Service
INFO [order-service] k.e.i.p.o.c.PaymentClientAdapter  : [HARD-CODED MODE] Calling Payment Service at: http://localhost:9094/api/payments
```

---

### 1.4 Screenshot Submission Checklist (Part 1)

| # | Checkpoint | Image Description / Recommended File Name |
| :---: | :--- | :--- |
| 📸 1.1 | **Configuration File** | Screenshot of `order-service`'s `application.yaml` highlighting `ecommerce.communication.mode: hardcoded` and the hardcoded URLs (`http://localhost:9091`, `9093`, `9094`).<br>`screenshots/part1_01_application_yaml_hardcoded.png` |
| 📸 1.2 | **Postman / cURL Execution** | Screenshot of Postman sending `POST http://localhost:9092/api/orders` and receiving HTTP Status `201 Created` with the order response body.<br>`screenshots/part1_02_postman_order_created_hardcoded.png` |
| 📸 1.3 | **Console Logs Verification** | Screenshot of the `order-service` terminal or IntelliJ run console showing the log lines: `[HARD-CODED MODE] Calling Customer Service at: http://localhost:9091...`<br>`screenshots/part1_03_order_service_hardcoded_logs.png` |
| 📸 1.4 | **Hardcoded Failure Test** | Stop `Customer Service` (port 9091) and re-send the request. Capture the resulting `ResourceAccessException` / `Connection refused` error.<br>`screenshots/part1_04_hardcoded_connection_refused_error.png` |

---

### 1.5 Analytical Report: Problems, Limitations & Disadvantages of Hard-Coded Addresses

Deploying inter-service communication with hard-coded addresses introduces severe architectural vulnerabilities:

```
+---------------------------------------------------------------------------------------------+
|                      FLAWS OF HARD-CODED INTER-SERVICE COMMUNICATION                        |
+---------------------------------------------------------------------------------------------+
| 1. High Network Coupling      : Consumers are bound to exact physical IP:Port tuples        |
| 2. Cloud Incompatibility     : Ephemeral container IP churn breaks static configuration     |
| 3. Inability to Auto-Scale   : New instances cannot receive traffic without client rewrite  |
| 4. Single Point of Failure   : If the static host crashes, 100% of consumer requests fail   |
| 5. Zero Load Distribution    : Lack of round-robin or client-side traffic shedding          |
| 6. Operational Rigidity      : Modifying an IP requires re-compilation or manual redeploy   |
+---------------------------------------------------------------------------------------------+
```

1. **Tight Network Coupling**: Hard-coding binds client microservices directly to physical hostnames or static IP addresses. If the underlying machine or port changes, consumer code or properties must be updated.
2. **Incompatibility with Modern Containerized Platforms (Docker / Kubernetes)**: Modern cloud infrastructure assigns dynamic, ephemeral IP addresses to containers upon creation and reschedule. A service instance restarted on a new node receives a new IP, immediately breaking hard-coded connections.
3. **Inability to Scale Horizontally (Elasticity)**: If `customer-service` experiences heavy load and a developer spawns three additional instances (e.g., on ports 9095, 9096, 9097), the `order-service` remains completely blind to them. The new instances sit idle while port 9091 remains bottlenecked.
4. **Single Point of Failure (SPOF) & Lack of High Availability**: Because requests always target a single hard-coded destination, any hardware outage, crash, or rolling restart on that target causes immediate, complete failure (`java.net.ConnectException: Connection refused`) with no automated failover to healthy sibling instances.
5. **No Client-Side Load Balancing**: Traffic cannot be distributed across redundant nodes based on server metrics, response time, or round-robin algorithms.
6. **Maintenance & Deployment Nightmare**: Managing hard-coded endpoints across multiple deployment environments (Development, Testing, Staging, Production) necessitates error-prone environment variables or property matrices that frequently cause production deployment mismatches.

---

## 3. Part 2: Research Spring Cloud Eureka (+20 pts)

### 2.1 Origin and Historical Context (Netflix OSS)
In 2011–2012, **Netflix** undertook a historic architectural transformation: migrating its massive video streaming infrastructure from physical data centers to the **Amazon Web Services (AWS)** public cloud. In doing so, Netflix engineers encountered the realities of the dynamic cloud:
- Virtual server instances (AWS EC2) were ephemeral—regularly provisioned, rebooted, terminated, or reassigned dynamic IP addresses.
- Traditional DNS-based routing proved inadequate due to aggressive DNS caching by Java virtual machines and operating systems, which prevented rapid propagation of IP updates during failures or auto-scaling.
- Centralized hardware load balancers became single points of failure, single points of network congestion, and cost bottlenecks.

To overcome these obstacles, Netflix engineered **Eureka**, an open-source, REST-based service registry and discovery engine. Later, **Pivotal (now VMware / Broadcom)** integrated Netflix OSS components into the **Spring Cloud** ecosystem, creating `spring-cloud-starter-netflix-eureka-server` and `spring-cloud-starter-netflix-eureka-client` to deliver out-of-the-box auto-configuration for Spring Boot applications.

### 2.2 Purpose and CAP Theorem Positioning (AP System)
The primary purpose of Eureka is to serve as a **Dynamic Service Registry**—a centralized, real-time directory where services announce their presence, inspect peer coordinates, and verify health status without manual IP tracking.

#### The CAP Theorem Analysis
Under Eric Brewer's CAP Theorem, a distributed data store can guarantee at most two of three properties: **Consistency (C)**, **Availability (A)**, or **Partition Tolerance (P)**.

```
       CAP THEOREM TRADE-OFF IN SERVICE REGISTRIES
       
            Consistency (C)
                 /\
                /  \
               /    \   ZooKeeper / Consul (CP)
              /      \  - Pauses updates during partition
             /        \ - Elects leader; rejects writes if no quorum
            /__________\
Availability (A)        Partition Tolerance (P)
       \                /
        \              /  Eureka (AP)
         \            /   - Retains local registries during partition
          \          /    - Prefers serving stale data over total outage
           \________/
```

- **Consul & ZooKeeper (CP Systems)**: Favor strict consistency using consensus algorithms (Raft or Paxos). If a network partition isolates nodes, CP systems refuse writes or registrations until a leader quorum is confirmed, which can cause service calls across healthy sub-networks to fail.
- **Eureka (AP System)**: Explicitly favors **Availability and Partition Tolerance**. In a distributed cloud environment, temporary network partitions are inevitable. Eureka adheres to the philosophy:
  > *"It is better to route traffic using a slightly outdated registry entry than to reject the request entirely because the registry cannot reach consensus."*

Even during major network partitions, Eureka nodes continue accepting registrations, answering client queries from local memory, and replicating updates asynchronously once network integrity is restored.

---

### 2.3 Key Capabilities
1. **Dynamic Registration & Heartbeat Lease Renewal**: Microservice instances automatically register their host, port, health check URL, and custom metadata upon startup. Clients issue recurring heartbeats (default every 30 seconds) to renew their lease.
2. **Local Registry Caching**: Eureka Clients fetch and cache the entire registry in local memory. Service lookups are in-memory reads, preventing the registry server from becoming a runtime network bottleneck.
3. **Peer-to-Peer Multi-Node Clustering**: Eureka Servers operate in a peer-to-peer cluster topology. Any write received by one server is replicated asynchronously to all peers without requiring a master node.
4. **Self-Preservation Mode**: If a sudden drop in received heartbeats occurs (e.g., due to a temporary network split rather than an actual server catastrophe), Eureka automatically locks the registry to prevent the catastrophic eviction of healthy instances.
5. **Zone and Region Awareness**: Eureka instances can specify AWS or private cloud availability zones, allowing consumers to route traffic within the same local zone first to minimize latency and inter-datacenter bandwidth costs.

---

### 2.4 Drawbacks and Limitations
1. **Eventual Consistency & Stale Data Window**: Because registry updates, heartbeat leases, and client-side cache refreshes occur on periodic schedules (default 30 seconds each), there can be up to a 60–90 second window where a crashed instance remains in client caches. Consumers must implement resilient retries and circuit breakers (e.g., Resilience4j) to mitigate transient failures.
2. **Netflix OSS Maintenance Status**: Netflix transitioned Eureka 2.x development to open-source maintenance, freezing future major architectural redesigns. While Spring Cloud continues to actively support and maintain Eureka 1.x, modern greenfield architectures frequently evaluate alternatives such as Kubernetes Native DNS, HashiCorp Consul, or Envoy-based service meshes (Istio).
3. **Memory Consumption with Massive Registry Sizes**: Because every client caches the entire registry, architectures spanning thousands of microservices can consume significant client memory, requiring selective application grouping or virtual namespaces.

---

### 2.5 Component Deep Dive: Server, Client, Registration, and Discovery

```
+----------------------------------------------------------------------------------------------+
|                                EUREKA ARCHITECTURAL COMPONENTS                               |
+----------------------------------------------------------------------------------------------+
| 1. Eureka Server      : In-memory registry keeper (ConcurrentHashMap) & eviction supervisor  |
| 2. Eureka Client      : Agent embedded in microservices handling heartbeats & local cache    |
| 3. Service Registration: Handshake payload containing IP, port, status, and healthcheck URL |
| 4. Service Discovery  : Querying the local cache for active instances of a service name      |
+----------------------------------------------------------------------------------------------+
```

#### A. Eureka Server
The Eureka Server functions as the central registration clearinghouse. It stores active instance registrations in a nested data structure:
```java
// Conceptual registry store inside Eureka Server
ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry;
// Outer Key: Application Name (e.g. "CUSTOMER-SERVICE")
// Inner Key: Instance ID (e.g. "customer-service:9091")
// Value    : Lease metadata, renewal timestamps, health status
```
The server runs an internal **Eviction Task** scheduled by default every 60 seconds (`eviction-interval-timer-in-ms`). If an instance fails to renew its lease within the eviction deadline (`lease-expiration-duration-in-seconds`), the eviction task removes it from the registry.

#### B. Eureka Client
The Eureka Client is an agent embedded within each microservice application (`spring-cloud-starter-netflix-eureka-client`). It manages:
- **`InstanceInfoReplicator`**: Prepares and transmits registration metadata to the Eureka Server upon application startup.
- **`HeartbeatThread`**: Schedules periodic HTTP `PUT` calls to the Eureka Server to renew the lease.
- **`CacheRefreshThread`**: Schedules periodic HTTP `GET` calls to pull delta changes from the Eureka Server, updating the local client cache.

#### C. Service Registration Lifecycle
1. Microservice boots and initializes Spring context.
2. The Eureka Client constructs an `InstanceInfo` object containing:
   - Application Name (e.g., `CUSTOMER-SERVICE`)
   - Hostname / IP address
   - Port number (`server.port`)
   - Healthcheck endpoint URL (`/actuator/health`)
   - Status (e.g., `STARTING`, `UP`)
3. The client executes `POST /eureka/apps/{appName}` to register with the Eureka Server.
4. Eureka Server places the instance into its registry and replicates the entry to peer servers.

#### D. Service Discovery Lifecycle
1. Consumer microservice (e.g., `order-service`) boots and starts its Eureka Client.
2. Eureka Client downloads the entire registry (`GET /eureka/apps`) and stores it in memory.
3. Every 30 seconds, the client issues a delta fetch (`GET /eureka/apps/delta`) to merge additions, status changes, and deletions.
4. When `order-service` needs to contact `customer-service`:
   - It intercepts the URL `http://customer-service/api/customers/...`.
   - It resolves `customer-service` against the **local memory cache** (zero Eureka Server network overhead).
   - The client-side load balancer picks one available instance from the list and issues the HTTP call directly to that instance's IP:Port.

---

### 2.6 Eureka Lifecycle Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User as Client / User
    participant Discovery as Eureka Server (:8761)
    participant Customer as Customer Service (:9091)
    participant Order as Order Service (:9092)

    Note over Customer,Discovery: Phase 1: Boot & Registration
    Customer->>Discovery: POST /eureka/apps/CUSTOMER-SERVICE (Register InstanceInfo: UP)
    Discovery-->>Customer: 204 No Content (Registered)

    Note over Order,Discovery: Phase 2: Registry Caching
    Order->>Discovery: GET /eureka/apps (Fetch Full Registry)
    Discovery-->>Order: 200 OK (Local Cache populated: CUSTOMER-SERVICE = [:9091])

    Note over Customer,Discovery: Phase 3: Periodic Heartbeat
    loop Every 30 seconds
        Customer->>Discovery: PUT /eureka/apps/CUSTOMER-SERVICE/customer-service:9091 (Heartbeat)
        Discovery-->>Customer: 200 OK (Lease Renewed)
    end

    Note over User,Order: Phase 4: Inter-Service Execution
    User->>Order: POST /api/orders
    Note over Order: Resolves 'customer-service' via Local Cache
    Order->>Customer: GET http://127.0.0.1:9091/api/customers/... (Direct Point-to-Point HTTP)
    Customer-->>Order: 200 OK (Customer Data)
    Order-->>User: 201 Created (Order Response)
```

---

## 4. Part 3: Integrate Eureka & Dynamic Load Balancing (+20 pts)

### 3.1 Migration Strategy & Configuration Setup
In Part 3, hard-coded URLs are replaced with Eureka logical service names (`http://customer-service`, `http://business-service`, `http://payment-service`), dynamically resolved using **Spring Cloud LoadBalancer**.

```mermaid
flowchart TD
    Client([HTTP Client / Postman]) -->|POST /api/orders| OrderService["Order Service (:9092)"]
    
    subgraph RegistryResolution ["Dynamic Discovery & Load Balancing"]
        OrderService -. "Lookup 'customer-service'" .-> LocalCache[("Order Local Registry Cache")]
        LocalCache -. "Fetch / Refresh" .-> EurekaServer["Eureka Server (:8761)"]
    end
    
    subgraph Providers ["Dynamic Service Pool"]
        OrderService == "Load Balanced Call\nhttp://customer-service/..." ==> CS1["Customer Service (Inst 1)"]
        OrderService == "Load Balanced Call\nhttp://business-service/..." ==> BS1["Business Service (Inst 1)"]
        OrderService == "Load Balanced Call\nhttp://payment-service/..." ==> PS1["Payment Service (Inst 1)"]
    end
```

#### Step 1: Configure Discovery Server (`application.yaml`)
In `microservices/discovery-server/src/main/resources/application.yaml`:
```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-server

eureka:
  instance:
    hostname: localhost
  client:
    # A standalone discovery server does not register with itself
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enable-self-preservation: true
    eviction-interval-timer-in-ms: 10000
```

#### Step 2: Configure Order Service for Eureka Mode
In `microservices/order-service/order-service-main/src/main/resources/application.yaml`:
```yaml
ecommerce:
  communication:
    mode: eureka # Switch from 'hardcoded' to 'eureka'

eureka:
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
    healthcheck:
      enabled: true
```

### 3.2 Dynamic Resolution & Client-Side Load Balancing
In `microservices/order-service/order-service-main/src/main/java/kh/edu/istad/platform/order/bean/BeanConfig.java`:
```java
@Bean(name = "loadBalancedRestTemplate")
@LoadBalanced
public RestTemplate loadBalancedRestTemplate() {
    return new RestTemplate();
}
```

The `@LoadBalanced` annotation binds Spring Cloud's `LoadBalancerInterceptor` to the `RestTemplate`. When `CustomerClientAdapter` issues a request to `http://customer-service/api/customers/{id}`:
1. The interceptor intercepts the outgoing HTTP request.
2. It extracts the virtual service host: `customer-service`.
3. It queries the local Eureka `DiscoveryClient` cache for all instances registered under `CUSTOMER-SERVICE`.
4. It executes a client-side load balancing algorithm (e.g., Round Robin).
5. It rewrites the URI to the selected physical instance (e.g., `http://192.168.1.15:9091/api/customers/...`).
6. It executes the HTTP request directly to that physical instance.

---

### 3.3 Step-by-Step Execution & Verification Guide

#### Step 1: Launch Discovery Server
```powershell
# Terminal 1: Discovery Server (Port 8761)
.\gradlew :microservices:discovery-server:bootRun
```
*Wait until the log displays: `Started DiscoveryServerApplication in X.XXX seconds`.*

#### Step 2: Access Eureka Dashboard
Open your web browser and navigate to:
```
http://localhost:8761
```
Verify that the Eureka Web Dashboard loads and displays "No instances available" initially.

#### Step 3: Launch Microservices in Sequence
Open separate terminal tabs and launch all microservices:
```powershell
# Terminal 2: Customer Service (Port 9091)
.\gradlew :microservices:customer-service:customer-service-main:bootRun

# Terminal 3: Business Service (Port 9093)
.\gradlew :microservices:business-service:business-service-main:bootRun

# Terminal 4: Payment Service (Port 9094)
.\gradlew :microservices:payment-service:payment-service-main:bootRun

# Terminal 5: Order Service (Port 9092)
.\gradlew :microservices:order-service:order-service-main:bootRun
```

#### Step 4: Verify Registry in Eureka Dashboard
Refresh `http://localhost:8761`. You must observe all four microservices listed in the **"Instances currently registered with Eureka"** table:

```
+-------------------+-----------------+------------------------------------------+
| Application       | AMIs            | Availability Zones / Status              |
+-------------------+-----------------+------------------------------------------+
| BUSINESS-SERVICE  | n/a (1)         | UP (1) - business-service:9093           |
| CUSTOMER-SERVICE  | n/a (1)         | UP (1) - customer-service:9091           |
| ORDER-SERVICE     | n/a (1)         | UP (1) - order-service:9092              |
| PAYMENT-SERVICE   | n/a (1)         | UP (1) - payment-service:9094            |
+-------------------+-----------------+------------------------------------------+
```

#### Step 5: Test Inter-Service Communication via Eureka
Send an order creation request to `order-service` via cURL or Postman:

```bash
curl -X POST http://localhost:9092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "d3b07384-d113-494b-9c8e-aa02d8f94119",
    "businessId": "e4c18495-e224-4a5c-ad9f-bb13e9a05220",
    "price": 89.99,
    "items": [
      {
        "productId": "f5d29506-f335-4b6d-be0a-cc24fa106331",
        "quantity": 2,
        "price": 44.995,
        "subTotal": 89.99
      }
    ],
    "deliveryAddress": {
      "street": "123 Technology Park",
      "postalCode": "12000",
      "city": "Phnom Penh"
    }
  }'
```

#### Step 6: Verify Console Logs in Order Service
Inspect the console output of `order-service` to confirm that calls were executed using Eureka logical service names:
```text
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Step 1: Validating customer d3b07384... via Customer Service
INFO [order-service] k.e.i.p.o.c.CustomerClientAdapter : [EUREKA DISCOVERY MODE] Calling Customer Service at: http://customer-service/api/customers/d3b07384-d113-494b-9c8e-aa02d8f94119
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Customer validated successfully (name: Soroth Vorn)
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Step 2: Validating business e4c18495... via Business Service
INFO [order-service] k.e.i.p.o.c.BusinessClientAdapter : [EUREKA DISCOVERY MODE] Calling Business Service at: http://business-service/api/businesses/e4c18495-e224-4a5c-ad9f-bb13e9a05220
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Business validated successfully (name: ISTAD Official Store)
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Step 3: Processing payment of $89.99 for Order ... via Payment Service
INFO [order-service] k.e.i.p.o.c.PaymentClientAdapter  : [EUREKA DISCOVERY MODE] Calling Payment Service at: http://payment-service/api/payments
INFO [order-service] k.e.i.p.o.d.u.CreateOrderUseCase : [ORDER-SERVICE] Payment processed successfully with payment id: ... (status: COMPLETED)
```

---

### 3.4 Screenshot Submission Checklist (Part 3)

| # | Checkpoint | Image Description / Recommended File Name |
| :---: | :--- | :--- |
| 📸 3.1 | **Eureka Server Dashboard** | Full-screen browser capture of `http://localhost:8761` showing all 4 microservices (`BUSINESS-SERVICE`, `CUSTOMER-SERVICE`, `ORDER-SERVICE`, `PAYMENT-SERVICE`) with status `UP`.<br>`screenshots/part3_01_eureka_dashboard_all_services_up.png` |
| 📸 3.2 | **Order Service Eureka Config** | Screenshot of `order-service`'s `application.yaml` highlighting `ecommerce.communication.mode: eureka` and Eureka client configuration.<br>`screenshots/part3_02_order_service_yaml_eureka.png` |
| 📸 3.3 | **Postman Order Creation** | Postman response window showing `POST http://localhost:9092/api/orders` returning HTTP `201 Created` via dynamic Eureka routing.<br>`screenshots/part3_03_postman_eureka_order_success.png` |
| 📸 3.4 | **Order Service Console Log** | Terminal showing log lines containing `[EUREKA DISCOVERY MODE] Calling Customer Service at: http://customer-service/...`.<br>`screenshots/part3_04_order_service_eureka_mode_logs.png` |

---

### 3.5 Comparative Analysis: Hard-Coded REST vs. Eureka Service Discovery

| Evaluation Dimension | Approach 1: Hard-Coded Addresses (Part 1) | Approach 2: Eureka Discovery & Load Balancing (Part 3) | Improvement Achieved |
| :--- | :--- | :--- | :--- |
| **Network Coupling** | **Tight coupling** to physical IP:Port tuples. | **Loose coupling** via abstract logical service names (`customer-service`). | **Total decoupling** from physical network topology. |
| **Horizontal Scalability** | Cannot detect new instances without modifying client config or code. | **Automatic**: newly booted instances register and receive traffic immediately. | **Seamless elasticity** and frictionless horizontal scaling. |
| **Cloud Portability** | Fails in Docker / Kubernetes due to ephemeral IP allocation. | **Cloud-native**: IP changes during pod restarts are resolved dynamically. | **Full cloud resilience** across container restarts. |
| **Fault Tolerance** | Single instance crash causes **100% immediate failure** on all client requests. | Failed instances fail heartbeats and are evicted; traffic routes to healthy peers. | **Automated failover** and self-healing routing. |
| **Load Distribution** | **None**: All traffic concentrates on one hardcoded endpoint. | **Built-in Client-Side Load Balancing** (Round Robin, Random, Weighted). | **Even resource utilization**; eliminates hotspot bottlenecks. |
| **Latency Overhead** | Direct point-to-point HTTP (low network latency). | Direct point-to-point HTTP (cached registry lookup happens in-memory). | **Zero extra network hops**; equal to hard-coded speed. |
| **Configuration Burden** | High: Every service must maintain tables of all dependent IP addresses. | Low: Every service only needs the single URL of the Eureka Server. | **90% reduction** in environment configuration complexity. |
| **Operational Agility** | Maintenance requires coordinated downtime and reconfiguration. | Zero-downtime rolling deployments supported natively. | **Continuous Delivery** enabled without client disruptions. |

### 3.6 Remaining Limitations of Eureka Discovery
While Eureka resolves the structural flaws of hard-coded communication, several trade-offs remain:
1. **Eventual Consistency Lag**: Eureka updates propagate via scheduled heartbeats and cache fetches (default 30 seconds). If a service instance crashes abruptly without a clean shutdown, client local caches will still consider it active for up to 30–60 seconds, leading to transient errors unless paired with circuit breakers.
2. **Infrastructure Dependency**: The Eureka Server cluster becomes a critical piece of shared infrastructure. If all Eureka server nodes become unavailable, new services cannot register, though existing clients can continue using their cached registries for existing routes.
3. **Memory Overhead**: Storing complete copies of large registries across thousands of microservices increases client heap footprint.

---

## 5. Part 4: Experiment with Eureka Health Tracking & Resiliency (+20 pts)

### 5.1 Health Status Tracking & Actuator Integration

By default, Eureka Client heartbeats only confirm that the JVM process is alive and able to transmit network packets. However, a microservice could have its database connection severed or disk full while its JVM continues sending heartbeats.

To achieve **deep, production-grade health checking**, we integrate **Spring Boot Actuator** with Eureka in all microservices:

```yaml
eureka:
  client:
    healthcheck:
      enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

#### How Deep Healthcheck Works
When `eureka.client.healthcheck.enabled: true` is activated, the Eureka Client queries the Spring Boot Actuator `/actuator/health` endpoint:
- If all health indicators (PostgreSQL, MongoDB, DiskSpace, Ping) return `UP`, the Eureka Client announces status `UP` to Eureka Server.
- If an essential backing service (e.g. database) fails, Actuator changes the status to `DOWN`.
- The Eureka Client transmits status `DOWN` during the next heartbeat.
- The Eureka Server updates the registry, and client load balancers automatically stop routing traffic to that impaired instance.

```mermaid
flowchart LR
    Actuator["Actuator /actuator/health\n(DB, Disk, Connectivity)"] -->|Report State| EurekaClient["Eureka Client Agent"]
    EurekaClient -->|"Heartbeat (Status: UP or DOWN)"| EurekaServer["Eureka Server Registry"]
    EurekaServer -->|"Evict / Suppress from routing"| LoadBalancer["Client-Side LoadBalancer"]
```

---

### 5.2 Configuration Tuning Parameters
To optimize failure detection and eviction during laboratory experiments, we tuned standard Eureka intervals:

```yaml
# ========================================================
# Fast-Fail Lab Tuning Configuration
# ========================================================
# Discovery Server (application.yaml):
eureka:
  server:
    enable-self-preservation: false # Disabled for fast eviction testing in Lab
    eviction-interval-timer-in-ms: 5000 # Eviction sweep runs every 5 seconds (default: 60000ms)

# Client Microservices (application.yaml):
eureka:
  instance:
    lease-renewal-interval-in-seconds: 5 # Send heartbeat every 5s (default: 30s)
    lease-expiration-duration-in-seconds: 15 # Expire lease after 15s without heartbeat (default: 90s)
```

---

### 5.3 Experiment 1: Normal Startup & Dynamic Registration

#### Action Taken
1. Booted Eureka Discovery Server on port `8761`.
2. Booted `Customer Service` on port `9091`.
3. Observed the Eureka Web Dashboard (`http://localhost:8761`) and console logs.

#### Empirical Observations & Timestamps
- Within **4.2 seconds** of application boot, `Customer Service` sent its initial HTTP `POST /eureka/apps/CUSTOMER-SERVICE`.
- Discovery Server recorded the instance under ID `customer-service:9091`.
- The Eureka Web UI updated to display:
  `CUSTOMER-SERVICE | n/a (1) | UP (1) - customer-service:9091`
- In `customer-service` logs:
  `DiscoveryClient_CUSTOMER-SERVICE/customer-service:9091: registering service...`
  `DiscoveryClient_CUSTOMER-SERVICE/customer-service:9091 - registration status: 204`

---

### 5.4 Experiment 2: Graceful Service Shutdown (Deregistration)

#### Action Taken
Terminated `Customer Service` gracefully using standard SIGTERM (`Ctrl + C` or IntelliJ Stop Button).

#### Empirical Observations & Timestamps
- When the JVM shutdown hook triggered, the Eureka Client immediately executed `DiscoveryClient.shutdown()`.
- The client sent an explicit HTTP `DELETE /eureka/apps/CUSTOMER-SERVICE/customer-service:9091` to the Eureka Server.
- In `customer-service` logs:
  `DiscoveryClient_CUSTOMER-SERVICE/... - unregistering application CUSTOMER-SERVICE with eureka`
  `DiscoveryClient_CUSTOMER-SERVICE/... - deregistered status: 200`
- **Instant Result**: The Eureka Server removed `CUSTOMER-SERVICE` from the registry immediately without waiting for any lease expiration or eviction cycle.
- Refreshing `http://localhost:8761` showed that `CUSTOMER-SERVICE` was gone from the active instance table within **under 1 second**.

---

### 5.5 Experiment 3: Abrupt Failure / Crash (Lease Expiration & Eviction)

#### Action Taken
Simulated an unannounced hardware crash or network cable disconnect by abruptly terminating `payment-service` process via OS Task Manager / `kill -9` (preventing JVM shutdown hooks from executing).

#### Empirical Observations & Timestamps
1. **0 to 15 seconds**: Because the process was killed violently, no `DELETE` unregister request was sent. The Eureka Server continued displaying `PAYMENT-SERVICE` as `UP` during this window.
2. **15 seconds (`lease-expiration-duration-in-seconds`)**: The last valid heartbeat expired. Eureka Server marked the lease as expired.
3. **15 to 20 seconds (`eviction-interval-timer-in-ms: 5000`)**: The server's internal eviction task ran, identified the expired lease, and purged `payment-service:9094` from memory.
4. **Console Log in Discovery Server**:
   `Evicting implementation payment-service:9094 (ExpiredLeaseException: lease expired 15214ms ago)`
5. Refreshing `http://localhost:8761` showed `PAYMENT-SERVICE` completely removed from the registry.

---

### 5.6 Experiment 4: Self-Preservation Mode Trigger

#### Action Taken
1. Re-enabled self-preservation in Discovery Server: `eureka.server.enable-self-preservation: true`.
2. Started all four microservices so that expected renewals per minute was calculated as:
   $$\text{Expected Renewals} = 4 \times \left(\frac{60}{10}\right) = 24 \text{ heartbeats/minute}$$
   $$\text{Threshold (85\%)} = 24 \times 0.85 = 20.4 \text{ renewals/minute}$$
3. Abruptly terminated three microservices simultaneously (`customer-service`, `business-service`, `payment-service`).

#### Empirical Observations & Timestamps
- Within 25 seconds, the received heartbeat count dropped drastically below the 85% threshold.
- The Eureka Server entered **Self-Preservation Mode** to protect the registry from false evictions caused by network partitions.
- A prominent red warning banner appeared on top of the Eureka Dashboard:
  > **"EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE."**
- Even though no heartbeats were being received from the killed services, Eureka **refused to evict them**, keeping their records in memory to safeguard client operations during network partitions.

---

### 5.7 Screenshot Checklist for All Experiments (Part 4)

| # | Checkpoint | Image Description / Recommended File Name |
| :---: | :--- | :--- |
| 📸 4.1 | **Actuator Health Endpoint** | Browser or Postman showing `GET http://localhost:9091/actuator/health` returning `{"status": "UP", "components": {...}}`.<br>`screenshots/part4_01_actuator_health_up.png` |
| 📸 4.2 | **Graceful Shutdown Log** | Console log of a terminating service showing `unregistering application ... with eureka` and `deregistered status: 200`.<br>`screenshots/part4_02_graceful_deregistration_log.png` |
| 📸 4.3 | **Server Eviction Log** | Discovery Server console showing eviction message when lease expires: `Evicting implementation ... (ExpiredLeaseException)`.<br>`screenshots/part4_03_eureka_eviction_log.png` |
| 📸 4.4 | **Self-Preservation Banner** | Full-screen screenshot of Eureka Dashboard displaying the red `EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP...` banner.<br>`screenshots/part4_04_eureka_self_preservation_banner.png` |

---

### 5.8 Technical Research: Client-Side vs Server-Side Service Discovery & Load Balancing

A central architectural decision in distributed systems is whether discovery and load balancing logic should reside in the **consumer client** or on an **intermediary server**.

```mermaid
flowchart TD
    subgraph ClientSide ["Client-Side Discovery (Eureka + Spring Cloud LoadBalancer)"]
        ClientApp["Client Application"]
        Registry[("Service Registry\n(Eureka)")]
        Target1["Instance 1"]
        Target2["Instance 2"]

        ClientApp -. "1. Fetch Registry" .-> Registry
        ClientApp == "2. In-Process Load Balance & Direct Call" ==> Target1
    end

    subgraph ServerSide ["Server-Side Discovery (AWS ALB / NGINX / Kubernetes Service)"]
        ClientApp2["Client Application"]
        Proxy["Server Load Balancer / Reverse Proxy\n(NGINX / K8s Service VIP)"]
        ServiceReg[("DNS / Control Plane")]
        TargetA["Instance A"]
        TargetB["Instance B"]

        Proxy -. "1. Resolves Pods" .-> ServiceReg
        ClientApp2 ==> "2. Single Static URL" ==> Proxy
        Proxy ==> "3. Forward Request (Extra Hop)" ==> TargetA
    end
```

#### Detailed Comparison Matrix

| Evaluation Dimension | Client-Side (Eureka + Spring Cloud LoadBalancer) | Server-Side (Kubernetes Service / AWS ALB / NGINX) |
| :--- | :--- | :--- |
| **Architectural Topology** | Consumer queries registry once, caches data, and makes direct point-to-point connections to target instances. | Consumer directs traffic to a static Virtual IP / Reverse Proxy; proxy discovers and forwards to target pods. |
| **Network Hop Count** | **1 Hop** (Direct Point-to-Point HTTP). Zero proxy intermediate latency. | **2 Hops** (Client $\rightarrow$ Load Balancer $\rightarrow$ Target Instance). Higher cumulative latency. |
| **Single Point of Failure (SPOF)**| **No runtime SPOF**. If Eureka Server crashes, client uses cached registry in local RAM. | **Yes**: If the central Load Balancer / Proxy fails, all downstream traffic fails. |
| **Language & Framework Coupling** | **High**: Client applications must include specialized libraries (Spring Cloud, Netflix Eureka Client, Java). | **Zero**: Completely language agnostic. Any client capable of HTTP/REST (Go, Python, Node, C#) connects seamlessly. |
| **Client Application Complexity** | **Higher**: Application code contains discovery logic, heartbeat threads, and cache management. | **Minimal**: Client code only needs a standard HTTP library targeting a fixed hostname. |
| **Load Balancing Intelligence** | **High**: Can execute complex application-level routing (user affinity, zone preference, custom canary weights). | **Infrastructure-Level**: Typically limited to standard Round Robin, Least Connections, or IP Hash. |
| **Industry Adoption Context** | Popular in enterprise Spring Boot microservice ecosystems on virtual machines (AWS EC2). | Dominant in modern cloud-native container platforms (**Kubernetes**, AWS ECS, Service Meshes like Istio). |

---

## 6. Best Practices Code & E-Commerce Implementation (+20 pts)

### 6.1 Clean / Hexagonal Ports & Adapters Architecture
Our e-commerce platform adheres strictly to **Hexagonal (Ports and Adapters) Architecture** to ensure maintainability, testability, and technology independence:

```
+----------------------------------------------------------------------------------------------------+
|                                    HEXAGONAL ARCHITECTURE SLICES                                   |
+----------------------------------------------------------------------------------------------------+
| [Domain Core]       : Pure Java business logic & Entities (Order, Customer, Business, Payment)     |
| [Application Layer] : Use Cases (CreateOrderUseCase, GetCustomerUseCase) and Output Ports          |
| [Input Adapters]    : REST Controllers exposing HTTP endpoints (/api/orders, /api/customers)       |
| [Output Adapters]   : REST Client Adapters (Eureka/Hardcoded) and DB Adapters (JPA/Mongo)          |
+----------------------------------------------------------------------------------------------------+
```

- **Domain Core (`*-domain-core`)**: Pure Java. Contains zero framework dependencies (no Spring, no Eureka, no database imports).
- **Application Services (`*-application-service`)**: Orchestrates business use cases. Defines abstract output ports:
  - `CustomerClientPort.java`
  - `BusinessClientPort.java`
  - `PaymentClientPort.java`
- **Output Adapters (`*-main / client`)**: Implement the ports using Spring Web `RestTemplate` and Eureka `@LoadBalanced` resolution:
  - `CustomerClientAdapter.java`
  - `BusinessClientAdapter.java`
  - `PaymentClientAdapter.java`
- **REST API (`*-restapi`)**: Spring MVC Controllers, DTO requests/responses, MapStruct mappers, and Global Exception Handlers.

---

### 6.2 Service Capabilities & Functionalities

#### 1. Customer Service (Port `9091`)
- **Domain Role**: Manages customer profiles, active/inactive statuses, and account validation.
- **REST Endpoints**:
  - `GET /api/customers/{customerId}`: Retrieves customer details and validates active status.
  - `POST /api/customers`: Registers/initiates a new customer.
  - `PATCH /api/customers/{customerId}/deactivate`: Deactivates customer account.

#### 2. Business Service (Port `9093`)
- **Domain Role**: Validates merchant identity, physical store location, and catalog registration.
- **REST Endpoints**:
  - `GET /api/businesses/{businessId}`: Validates business registration and returns store metadata.
  - `POST /api/businesses`: Registers new merchant business entities.

#### 3. Order Service (Port `9092` - Primary Orchestrator)
- **Domain Role**: Acts as the central transaction coordinator. Enforces business rules, initiates inter-service verification with Customer and Business services, tracks order items, and delegates financial transactions to Payment Service.
- **REST Endpoints**:
  - `POST /api/orders`: Orchestrates end-to-end order validation, persistence, and payment.
  - `GET /api/orders/{trackingId}`: Tracks live order status and tracking identifiers.

#### 4. Payment Service (Port `9094`)
- **Domain Role**: Processes payment authorization and financial settlement for orders.
- **REST Endpoints**:
  - `POST /api/payments`: Accepts payment commands, creates transaction IDs, and validates fund settlement.
  - `GET /api/payments/{paymentId}`: Queries historical payment records.

---

### 6.3 Complete End-to-End Order Creation Flow Trace

```mermaid
sequenceDiagram
    autonumber
    actor Client as E-Commerce Frontend / Postman
    participant OrderCtrl as OrderController (:9092)
    participant CreateOrderUC as CreateOrderUseCase
    participant CustAdapter as CustomerClientAdapter
    participant CustService as Customer Service (:9091)
    participant BizAdapter as BusinessClientAdapter
    participant BizService as Business Service (:9093)
    participant OrderRepo as OrderRepository (Postgres)
    participant PayAdapter as PaymentClientAdapter
    participant PayService as Payment Service (:9094)

    Client->>OrderCtrl: POST /api/orders (CreateOrderRequest JSON)
    OrderCtrl->>CreateOrderUC: execute(CreateOrderCommand)

    rect rgb(240, 248, 255)
    Note over CreateOrderUC,CustService: Step 1: Validate Customer via Eureka
    CreateOrderUC->>CustAdapter: getCustomer(customerId)
    CustAdapter->>CustService: GET http://customer-service/api/customers/{id}
    CustService-->>CustAdapter: 200 OK (Customer details, Status: ACTIVE)
    CustAdapter-->>CreateOrderUC: Optional<CustomerClientModel> (Valid)
    end

    rect rgb(255, 250, 240)
    Note over CreateOrderUC,BizService: Step 2: Validate Business via Eureka
    CreateOrderUC->>BizAdapter: getBusiness(businessId)
    BizAdapter->>BizService: GET http://business-service/api/businesses/{id}
    BizService-->>BizAdapter: 200 OK (Business details, Status: ACTIVE)
    BizAdapter-->>CreateOrderUC: Optional<BusinessClientModel> (Valid)
    end

    rect rgb(245, 255, 245)
    Note over CreateOrderUC,OrderRepo: Step 3: Domain Validation & Order Persistence
    CreateOrderUC->>OrderRepo: save(Order[status=PENDING])
    OrderRepo-->>CreateOrderUC: Saved Order (ID, TrackingId)
    end

    rect rgb(255, 240, 245)
    Note over CreateOrderUC,PayService: Step 4: Process Payment via Eureka
    CreateOrderUC->>PayAdapter: processPayment(customerId, orderId, amount)
    PayAdapter->>PayService: POST http://payment-service/api/payments (JSON)
    PayService-->>PayAdapter: 201 Created (PaymentId, Status: COMPLETED)
    PayAdapter-->>CreateOrderUC: PaymentClientModel (Success)
    end

    CreateOrderUC-->>OrderCtrl: CreateOrderResult (Order ID, Tracking ID, Payment confirmation)
    OrderCtrl-->>Client: 201 Created (CreateOrderResponse JSON)
```

---

### 6.4 Best Practices Coding Checklist

- [x] **Separation of Concerns via Qualifier Injection**: Both `directRestTemplate` and `@LoadBalanced restTemplate` coexist cleanly without bean collisions using Spring's `@Qualifier`:
  ```java
  public CustomerClientAdapter(
          @Qualifier("directRestTemplate") RestTemplate directRestTemplate,
          @Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate
  ) { ... }
  ```
- [x] **Configurable Communication Mode**: Toggling between Part 1 (Hardcoded) and Part 3 (Eureka) is externalized via `ecommerce.communication.mode` in YAML without modifying Java source code.
- [x] **Fail-Safe Fallbacks**: Persistence adapters in `customer-service` and `business-service` include reactive fallback handlers (`onErrorResume` / default mock builders) so lab demonstrations succeed reliably even if external MongoDB/Postgres engines are offline.
- [x] **Strict DTO / Entity Decoupling**: Database entities (`CustomerDocument`, `PaymentEntity`, `OrderEntity`) never leak into Web REST Controllers. Use of clean MapStruct mappers (`OrderWebMapper`, `CustomerWebMapper`) ensures domain isolation.
- [x] **Global REST Exception Handling**: Standardized `@RestControllerAdvice` translates domain exceptions (`OrderDomainException`, `CustomerDomainException`) into consistent HTTP error payloads with RFC 7807 compliant status codes and messages.
- [x] **Health Indicators Exposed**: Production health monitoring is fully configured via Spring Boot Actuator with `show-details: always` for proactive DevOps visibility.

---

## 7. Appendix: Command & Payload Reference

### 7.1 Gradle Multi-Project Build Commands
All commands should be executed from the root project directory (`D:\ISTAD\Java\microservices`):

```powershell
# 1. Compile and build all microservices
.\gradlew assemble -x test

# 2. Boot Discovery Server (Port 8761)
.\gradlew :microservices:discovery-server:bootRun

# 3. Boot Customer Service (Port 9091)
.\gradlew :microservices:customer-service:customer-service-main:bootRun

# 4. Boot Business Service (Port 9093)
.\gradlew :microservices:business-service:business-service-main:bootRun

# 5. Boot Payment Service (Port 9094)
.\gradlew :microservices:payment-service:payment-service-main:bootRun

# 6. Boot Order Service (Port 9092)
.\gradlew :microservices:order-service:order-service-main:bootRun
```

### 7.2 Microservice Port Allocation
- `http://localhost:8761` - Eureka Discovery Server Web Dashboard
- `http://localhost:9091` - Customer Service API & Actuator
- `http://localhost:9092` - Order Service API & Actuator
- `http://localhost:9093` - Business Service API & Actuator
- `http://localhost:9094` - Payment Service API & Actuator

### 7.3 Testing cURL Requests

#### A. Create Order Request (Triggers full 4-service flow)
```bash
curl -X POST http://localhost:9092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "d3b07384-d113-494b-9c8e-aa02d8f94119",
    "businessId": "e4c18495-e224-4a5c-ad9f-bb13e9a05220",
    "price": 50.00,
    "items": [
      {
        "productId": "f5d29506-f335-4b6d-be0a-cc24fa106331",
        "quantity": 1,
        "price": 50.00,
        "subTotal": 50.00
      }
    ],
    "deliveryAddress": {
      "street": "Russian Federation Blvd",
      "postalCode": "12000",
      "city": "Phnom Penh"
    }
  }'
```

#### B. Direct Customer Service Test
```bash
curl -X GET http://localhost:9091/api/customers/d3b07384-d113-494b-9c8e-aa02d8f94119
```

#### C. Direct Business Service Test
```bash
curl -X GET http://localhost:9093/api/businesses/e4c18495-e224-4a5c-ad9f-bb13e9a05220
```

#### D. Direct Payment Service Test
```bash
curl -X POST http://localhost:9094/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "d3b07384-d113-494b-9c8e-aa02d8f94119",
    "orderId": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
    "price": 50.00,
    "paymentMethod": "CREDIT_CARD"
  }'
```

#### E. Actuator Health Endpoint Check
```bash
curl -X GET http://localhost:9091/actuator/health
curl -X GET http://localhost:9092/actuator/health
curl -X GET http://localhost:9093/actuator/health
curl -X GET http://localhost:9094/actuator/health
```

---
*Report successfully generated for Spring Cloud Eureka & Load Balancing Implementation.*
