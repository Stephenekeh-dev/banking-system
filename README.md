  # Secure, Fraud-Aware Digital Banking Microservices Platform
  
  ## Overview 
The `Secure Digital Banking Microservices Platform` is a modern, event-driven financial 
application designed to manage banking operations in a scalable, modular, and secure manner. 
Built using `Spring Boot microservices architecture`, it leverages Apache `Kafka` for asynchronous 
messaging, ensuring real-time communication between services. The platform prioritizes security, 
fraud detection, and auditability, making it ideal for digital banking, fintech solutions,
or transaction-heavy applications.

## Key Features
* **Account Management:** Handle customer accounts, balances, and metadata securely.
* **Transaction Processing:** Record deposits, withdrawals, transfers, and enforce transactional rules.
* **Approval & Workflow:** Manage manual and automated approval flows for high-risk transactions.
* **Fraud Detection:** Monitor transactions using velocity checks, limits, blacklists, and alerts.
* **Notifications:** Send real-time updates via email, SMS, or push notifications.
* **Audit & Event Logging:** Persist immutable event logs using Kafka and read-optimized storage (PostgreSQL/Elasticsearch).
* **Security:** OAuth2/JWT authentication, role management, and secure inter-service communication.
* **Scalable Architecture:** Each service has its own database (PostgreSQL) and communicates via Kafka,
supporting high throughput and modular deployment.

## Technology Stack
* **Language and framework:** Maven, Java, Spring Boot, Spring Security.
* **Data stores:** PostgreSQL for persistence and Apache Kafka for event streaming.
    Docker for containerization and deployment.

## Microservices
* **Account Service:** manages accounts, balances, account metadata.
   ### Dependencies 
 * these are the dependencies in the Account_service `pom.xml`
``` <dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-web</artifactId>
			</dependency>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-security</artifactId>
			</dependency>
			<dependency>
				<groupId>io.jsonwebtoken</groupId>
				<artifactId>jjwt-api</artifactId>
				<version>0.11.5</version>
			</dependency>
			<dependency>
				<groupId>io.jsonwebtoken</groupId>
				<artifactId>jjwt-impl</artifactId>
				<version>0.11.5</version>
				<scope>runtime</scope>
			</dependency>
			<dependency>
				<groupId>io.jsonwebtoken</groupId>
				<artifactId>jjwt-jackson</artifactId>
				<version>0.11.5</version>
				<scope>runtime</scope>
			</dependency>
			<dependency>
				<groupId>org.postgresql</groupId>
				<artifactId>postgresql</artifactId>
				<scope>runtime</scope>
			</dependency>

 ```
* **Auth Service:** for registration, authentication and role management.
  ## Dependencies
   * these are the dependencies in the Auth_service `pom.xml`
  ```<dependencies>
		<!-- Core Spring Boot Starters -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
			<version>3.0.15</version> <!-- match your Spring Boot version -->
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>

		<!-- Database -->
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>

		<!-- JWT -->
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>${jjwt.version}</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>${jjwt.version}</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if you prefer -->
			<version>${jjwt.version}</version>
			<scope>runtime</scope>
		</dependency>

		<!-- Actuator for monitoring -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

		<!-- Lombok -->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>

		<!-- Testing -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>```
  
* **Transaction Service:** records transactions (deposits, withdrawals, transfers) and enforces transactional business rules.
* **Approval Service:** handles manual/automated approval flows for high-risk transactions.
* **Notification Service:** for email/SMS/push notifications.
* **Fraud Service:** evaluates fraud rules (limits, velocity, blacklists) and raises alerts or blocks transactions.
* **Audit  service:** Kafka topics + consumer(s) to persist immutable event logs
    to a read-optimized DB (Elastic or PostgreSQL audit table).
* **Gateway:** an API gateway for routing, auth, rate limiting.




