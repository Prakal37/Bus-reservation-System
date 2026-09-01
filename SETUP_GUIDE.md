# Bus Reservation System - Setup Guide

## Prerequisites

Before setting up the Bus Reservation System, ensure you have the following installed:

1. **Java 17 or higher**
   - Download from [java.oracle.com](https://www.oracle.com/java/technologies/downloads/)
   - Verify installation: `java -version`

2. **Maven 3.6+**
   - Download from [maven.apache.org](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **MySQL 8.0+**
   - Download from [mysql.com](https://www.mysql.com/downloads/)
   - Verify installation: `mysql --version`

4. **Git**
   - Download from [git-scm.com](https://git-scm.com/)
   - Verify installation: `git --version`

---

## Step 1: Database Setup

### Create Database
Open MySQL and execute the following commands:

```sql
-- Create database
CREATE DATABASE bus_reservation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE bus_reservation_db;

-- Grant privileges to a user (optional)
CREATE USER 'bus_user'@'localhost' IDENTIFIED BY 'bus_password';
GRANT ALL PRIVILEGES ON bus_reservation_db.* TO 'bus_user'@'localhost';
FLUSH PRIVILEGES;
```

### Verify Database
```sql
SHOW DATABASES;
USE bus_reservation_db;
```

---

## Step 2: Clone/Extract Project

If you're using Git:
```bash
git clone <repository-url>
cd Bus-reservation-System
```

Or if you have the project extracted:
```bash
cd Bus-reservation-System
```

---

## Step 3: Configure Application Properties

Edit the file: `src/main/resources/application.properties`

Update the following properties according to your setup:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bus_reservation_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

# Or if you created a separate user:
spring.datasource.username=bus_user
spring.datasource.password=bus_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# JWT Configuration (keep as is or change the secret if needed)
jwt.secret=mySecretKeyForJWTTokenGenerationBusReservationSystemSecurityKey2024
jwt.expiration=86400000
```

**Important:**
- `spring.jpa.hibernate.ddl-auto=update` will automatically create/update tables
- To start fresh, use `spring.jpa.hibernate.ddl-auto=create`
- For production, use `spring.jpa.hibernate.ddl-auto=validate`

---

## Step 4: Build the Project

Navigate to the project directory and run:

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run tests (if any)
- Create a JAR file

**Expected output:** `BUILD SUCCESS`

If you encounter dependency issues, try:
```bash
mvn clean install -U
```

---

## Step 5: Run the Application

### Option 1: Using Maven
```bash
mvn spring-boot:run
```

### Option 2: Using JAR file
```bash
java -jar target/bus-reservation-system-1.0.0.jar
```

### Expected Output
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.1.5)

2024-08-31 10:00:00.000  INFO 1234 --- [           main] 
c.busreservation.BusReservationApplication : Starting BusReservationApplication
...
2024-08-31 10:00:05.000  INFO 1234 --- [           main] 
c.busreservation.BusReservationApplication : Started BusReservationApplication in 5.123 seconds
```

**The application is now running on:** `http://localhost:8080/api`

---

## Step 6: Verify Installation

### Check API Health
Open a browser or use curl:

```bash
# This should return API responses (or 404 if no route exists)
curl http://localhost:8080/api/v1/buses
```

### Check Database Connection
If the application starts without errors, the database connection is successful.

You can verify by checking MySQL:
```bash
mysql -u root -p bus_reservation_db
SHOW TABLES;
```

Expected tables:
```
- users
- routes
- buses
- seats
- bookings
- payments
```

---

## Step 7: Create Initial Data (Optional)

### Using MySQL
You can insert sample data for testing:

```sql
-- Insert a route
INSERT INTO routes (source, destination, distance_km, approximate_duration_hours, is_active, created_at, updated_at)
VALUES ('New York', 'Boston', 215, 4, true, NOW(), NOW());

-- Insert a bus
INSERT INTO buses (bus_name, bus_number, total_seats, bus_type, amenities, route_id, departure_time, arrival_time, price_per_seat, operator_name, is_active, created_at, updated_at)
VALUES ('Express Coach', 'BUS-001', 40, 'AC', 'WiFi,Charging Port', 1, '10:00', '14:00', 50.0, 'Express Travel', true, NOW(), NOW());

-- Insert a user
INSERT INTO users (first_name, last_name, email, username, password, phone_number, is_active, created_at, updated_at)
VALUES ('John', 'Doe', 'john@example.com', 'johndoe', '$2a$10$...', '9876543210', true, NOW(), NOW());
```

---

## Troubleshooting

### Issue 1: MySQL Connection Error
```
ERROR: java.sql.SQLException: Access denied for user 'root'@'localhost'
```
**Solution:**
- Verify MySQL is running
- Check username and password in `application.properties`
- Ensure database exists

### Issue 2: Port Already in Use
```
ERROR: Address already in use: bind
```
**Solution:**
- Change port in `application.properties`: `server.port=8081`
- Or kill the process using port 8080

### Issue 3: Maven Build Fails
```
ERROR: [ERROR] FATAL ERROR in native method: SIGABRT
```
**Solution:**
- Clear Maven cache: `mvn clean install -U`
- Ensure Java 17 is being used: `java -version`

### Issue 4: Hibernate Table Creation Fails
```
ERROR: Table 'bus_reservation_db.users' doesn't exist
```
**Solution:**
- Set `spring.jpa.hibernate.ddl-auto=create` temporarily
- Run the application once
- Change back to `update`

### Issue 5: JWT Token Errors
```
ERROR: Invalid JWT token
```
**Solution:**
- Ensure token is passed in Authorization header
- Format: `Authorization: Bearer <token>`
- Check token expiration

---

## API Testing Tools

### Option 1: Postman
1. Download [Postman](https://www.postman.com/downloads/)
2. Import the API collection
3. Set the base URL to `http://localhost:8080/api`
4. Test endpoints

### Option 2: cURL
```bash
# Register user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","username":"johndoe","password":"password123","confirmPassword":"password123"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","password":"password123"}'

# Search buses
curl -X POST http://localhost:8080/api/v1/buses/search \
  -H "Content-Type: application/json" \
  -d '{"source":"New York","destination":"Boston","journeyDate":"2024-09-15"}'
```

### Option 3: VS Code REST Client Extension
1. Install "REST Client" extension
2. Create a file `requests.http`
3. Write requests in the file
4. Click "Send Request" above each request

---

## Project Structure

```
Bus-reservation-System/
├── src/main/
│   ├── java/com/busreservation/
│   │   ├── BusReservationApplication.java (Main class)
│   │   ├── controller/          (REST Controllers)
│   │   ├── service/             (Business Logic)
│   │   ├── repository/          (Data Access)
│   │   ├── entity/              (JPA Entities)
│   │   ├── dto/                 (Data Transfer Objects)
│   │   ├── security/            (JWT and Security)
│   │   └── exception/           (Exception Handling)
│   └── resources/
│       └── application.properties (Configuration)
├── pom.xml                      (Maven Configuration)
├── README.md                    (Project Overview)
├── API_ENDPOINTS.md             (API Documentation)
├── SETUP_GUIDE.md              (This file)
└── DATABASE_SCHEMA.md          (Database Schema)
```

---

## Next Steps

1. **Read API Documentation:** See [API_ENDPOINTS.md](API_ENDPOINTS.md)
2. **Understand Database Schema:** See [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)
3. **Test the APIs:** Use Postman or cURL
4. **Build the Frontend:** Connect your UI to these backend APIs
5. **Deploy:** Follow deployment guidelines for production

---

## Environment Configurations

### Development
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.com.busreservation=DEBUG
```

### Production
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.busreservation=INFO
spring.datasource.hikari.maximum-pool-size=10
```

---

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JWT Documentation](https://jwt.io/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Maven Documentation](https://maven.apache.org/guides/)

---

## Support

If you encounter any issues:
1. Check the logs in console
2. Review the [Troubleshooting](#troubleshooting) section
3. Check the [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)
4. Review [API_ENDPOINTS.md](API_ENDPOINTS.md)

---

**Happy Coding! 🚀**
