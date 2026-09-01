# Bus Reservation System - Developer Quick Reference

## 🚀 Getting Started (5 minutes)

### 1. Start MySQL
```bash
# Windows
net start MySQL80

# macOS
brew services start mysql

# Linux
sudo systemctl start mysql
```

### 2. Create Database
```bash
mysql -u root -p
CREATE DATABASE bus_reservation_db;
```

### 3. Build & Run
```bash
cd Bus-reservation-System
mvn clean install
mvn spring-boot:run
```

### 4. Test
- Application starts at: `http://localhost:8080/api`
- Try: `http://localhost:8080/api/v1/routes`

---

## 📡 Quick API Testing

### Register & Login
```bash
# Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@test.com","username":"john","password":"pass123","confirmPassword":"pass123"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'

# Copy the token from response
```

### Add Route (Admin)
```bash
curl -X POST http://localhost:8080/api/v1/routes \
  -H "Content-Type: application/json" \
  -d '{"source":"New York","destination":"Boston","distanceKm":215,"approximateDurationHours":4}'
```

### Add Bus (Admin)
```bash
curl -X POST http://localhost:8080/api/v1/buses \
  -H "Content-Type: application/json" \
  -d '{
    "busName":"Express Coach",
    "busNumber":"BUS-001",
    "totalSeats":40,
    "busType":"AC",
    "amenities":"WiFi,Charging",
    "route":{"routeId":1},
    "departureTime":"10:00",
    "arrivalTime":"14:00",
    "pricePerSeat":50.0,
    "operatorName":"Express Travel"
  }'
```

### Search Buses
```bash
curl -X POST http://localhost:8080/api/v1/buses/search \
  -H "Content-Type: application/json" \
  -d '{"source":"New York","destination":"Boston","journeyDate":"2024-09-15"}'
```

### Create Booking
```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "userId: 1" \
  -d '{
    "busId":1,
    "seatNumbers":[5,6,7],
    "journeyDate":"2024-09-15",
    "passengerName":"John Doe",
    "passengerEmail":"john@test.com",
    "passengerPhone":"9876543210"
  }'
```

---

## 🔑 Authentication

All endpoints (except login/register) require JWT token:

```
Authorization: Bearer <token>
```

Token format from login response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

---

## 🗂️ Key Classes Reference

### Controllers
- `AuthController` - Login/Register (`:8080/api/v1/auth`)
- `BusController` - Bus search (`:8080/api/v1/buses`)
- `BookingController` - Bookings (`:8080/api/v1/bookings`)
- `SeatController` - Seats (`:8080/api/v1/seats`)
- `UserController` - User profile (`:8080/api/v1/users`)
- `DashboardController` - Dashboard (`:8080/api/v1/dashboard`)
- `RouteController` - Routes (`:8080/api/v1/routes`)

### Services
- `UserService` - User operations
- `BusService` - Bus management
- `BookingService` - Booking logic
- `SeatService` - Seat availability
- `PaymentService` - Payment processing
- `RouteService` - Route management

### Entities
- `User` - User/Passenger
- `Bus` - Bus information
- `Route` - Route between cities
- `Seat` - Individual seat
- `Booking` - Bus ticket booking
- `Payment` - Payment record

---

## 🔧 Development Workflow

### Adding a New Endpoint

1. **Create DTO** (if needed)
   ```java
   @Data
   public class NewDTO {
       private String field1;
       private Integer field2;
   }
   ```

2. **Create Service Method**
   ```java
   @Service
   public class MyService {
       public NewDTO doSomething(NewDTO input) {
           // Business logic
           return result;
       }
   }
   ```

3. **Create Controller Method**
   ```java
   @RestController
   @RequestMapping("/v1/resources")
   public class MyController {
       @PostMapping
       public ResponseEntity<ApiResponse<NewDTO>> create(@RequestBody NewDTO input) {
           NewDTO result = service.doSomething(input);
           return ResponseEntity.ok(new ApiResponse<>(true, "Success", result));
       }
   }
   ```

### Adding a New Entity

1. Create entity class with `@Entity` annotation
2. Create repository extending `JpaRepository`
3. Create service for business logic
4. Create controller with endpoints
5. Update `application.properties` if needed

---

## 🐛 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Connection refused (MySQL) | Start MySQL: `net start MySQL80` (Windows) |
| Port 8080 in use | Change port in `application.properties`: `server.port=8081` |
| Maven build fails | Clear cache: `mvn clean install -U` |
| JWT invalid | Ensure token is in `Authorization: Bearer <token>` header |
| Seat not found | Initialize seats first (auto-happens on booking) |
| Duplicate email | Email must be unique across users |
| Password too short | Password must be minimum 6 characters |

---

## 📋 Database Commands

### View All Tables
```sql
USE bus_reservation_db;
SHOW TABLES;
```

### Check Users
```sql
SELECT * FROM users;
```

### Check Bookings
```sql
SELECT * FROM bookings;
```

### Check Available Seats
```sql
SELECT * FROM seats 
WHERE bus_id = 1 AND booking_date = '2024-09-15' AND is_available = true;
```

### Clear All Data
```sql
-- Caution: This deletes all data
DELETE FROM payments;
DELETE FROM bookings;
DELETE FROM seats;
DELETE FROM buses;
DELETE FROM routes;
DELETE FROM users;
```

### Reset Auto Increment
```sql
ALTER TABLE users AUTO_INCREMENT = 1;
```

---

## 📦 Project Structure Commands

```bash
# Build project
mvn clean install

# Run tests
mvn test

# Build JAR only (skip tests)
mvn clean package -DskipTests

# View dependencies
mvn dependency:tree

# Check for outdated dependencies
mvn versions:display-updates

# Format code
mvn spotless:apply

# Run application
mvn spring-boot:run

# Debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

---

## 🔍 Logging

### View Logs
```bash
# Real-time logs while running
tail -f target/logs/app.log

# View in application console (by default)
```

### Change Log Level
Edit `application.properties`:
```properties
logging.level.com.busreservation=DEBUG
logging.level.org.springframework.web=INFO
```

### Log Levels (highest to lowest)
1. TRACE - Very detailed
2. DEBUG - Detailed debug info
3. INFO - General information
4. WARN - Warning messages
5. ERROR - Error messages

---

## 🌐 Postman Collection

### Create New Request

**Authorization Tab:**
- Type: Bearer Token
- Token: `{{jwt_token}}`

**Headers Tab:**
- Content-Type: application/json
- userId: 1 (for bookings)

### Sample Variables
- `base_url`: http://localhost:8080/api
- `jwt_token`: <token from login>
- `bus_id`: 1
- `user_id`: 1

---

## 🚀 Performance Tips

1. **Use Indexes** - Frequently queried columns are indexed
2. **Pagination** - Implement limit/offset for large lists
3. **Caching** - Consider Redis for seat availability
4. **Connection Pooling** - HikariCP configured (10 connections)
5. **Lazy Loading** - Use `@Lazy` for heavy relationships

---

## 📚 File Locations Reference

| File | Location |
|------|----------|
| Application Config | `src/main/resources/application.properties` |
| Controllers | `src/main/java/com/busreservation/controller/` |
| Services | `src/main/java/com/busreservation/service/` |
| Entities | `src/main/java/com/busreservation/entity/` |
| DTOs | `src/main/java/com/busreservation/dto/` |
| Repositories | `src/main/java/com/busreservation/repository/` |
| Main Class | `src/main/java/com/busreservation/BusReservationApplication.java` |

---

## ✅ Checklist Before Commit

- [ ] Code follows naming conventions
- [ ] No hardcoded values (except constants)
- [ ] Proper error handling
- [ ] Logging added for important operations
- [ ] DTOs used for API responses
- [ ] Service layer used for business logic
- [ ] No N+1 query problems
- [ ] Database transactions used where needed
- [ ] Code compiles without errors
- [ ] Tests pass (if applicable)

---

## 🔐 Security Checklist

- [ ] Passwords hashed (BCrypt)
- [ ] Input validation on all endpoints
- [ ] SQL injection prevention (using ORM)
- [ ] CORS properly configured
- [ ] JWT tokens validated
- [ ] Sensitive data not logged
- [ ] Error messages don't expose system details
- [ ] Admin endpoints protected

---

## 📞 Quick Help Commands

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check MySQL connection
mysql -u root -p -e "SELECT 1"

# View Spring Boot logs
tail -f target/spring.log

# Kill process on port 8080 (Linux/Mac)
lsof -ti:8080 | xargs kill -9

# Kill process on port 8080 (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

## 📖 Documentation Links

- [Full Setup Guide](SETUP_GUIDE.md)
- [API Endpoints](API_ENDPOINTS.md)
- [Database Schema](DATABASE_SCHEMA.md)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [JWT Documentation](https://jwt.io/)

---

**Happy Coding! 🎉**
