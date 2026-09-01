# Bus Reservation System - Complete Backend

## 🚀 Project Overview

A comprehensive, production-ready backend for a **Bus Reservation System** built with Spring Boot 3.1.5, Java 17, and MySQL. This system provides REST APIs for user authentication, bus search, seat availability, and booking management.

### ✨ Key Features

- ✅ **User Authentication & Authorization** - Secure login/registration with JWT tokens
- ✅ **Bus Search & Filtering** - Search buses by route, date, type, and operator
- ✅ **Seat Management** - Real-time seat availability tracking
- ✅ **Booking System** - Complete booking lifecycle management
- ✅ **Payment Processing** - Payment transaction tracking
- ✅ **Dashboard** - User booking statistics and summary
- ✅ **RESTful APIs** - Clean, well-documented REST endpoints
- ✅ **Error Handling** - Centralized exception handling
- ✅ **Logging** - Comprehensive logging with SLF4J

---

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Setup Guide](#setup-guide)
5. [API Documentation](#api-documentation)
6. [Database Schema](#database-schema)
7. [Features](#features)
8. [Contributing](#contributing)
9. [License](#license)

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Setup
```bash
# 1. Clone the repository
git clone <repository-url>
cd Bus-reservation-System

# 2. Configure database
# Edit src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/bus_reservation_db

# 3. Build the project
mvn clean install

# 4. Run the application
mvn spring-boot:run

# Application starts at: http://localhost:8080/api
```

For detailed setup instructions, see [SETUP_GUIDE.md](SETUP_GUIDE.md)

---

## 🛠 Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Framework** | Spring Boot | 3.1.5 |
| **Language** | Java | 17 |
| **Database** | MySQL | 8.0+ |
| **ORM** | Spring Data JPA | 3.1.5 |
| **Authentication** | JWT (JJWT) | 0.12.3 |
| **Security** | Spring Security | 3.1.5 |
| **Password Encoding** | BCrypt | Spring Security |
| **Mapper** | ModelMapper | 3.1.1 |
| **Logging** | SLF4J + Logback | - |
| **Build Tool** | Maven | 3.6+ |

---

## 📁 Project Structure

```
Bus-reservation-System/
├── src/main/java/com/busreservation/
│   ├── BusReservationApplication.java          # Main Spring Boot class
│   │
│   ├── controller/                             # REST Controllers
│   │   ├── AuthController.java                # Authentication (login/register)
│   │   ├── BusController.java                 # Bus search and management
│   │   ├── BookingController.java             # Booking operations
│   │   ├── SeatController.java                # Seat availability
│   │   ├── UserController.java                # User profile management
│   │   ├── RouteController.java               # Route management
│   │   └── DashboardController.java           # Dashboard data
│   │
│   ├── service/                               # Business Logic Layer
│   │   ├── UserService.java                   # User operations
│   │   ├── BusService.java                    # Bus management
│   │   ├── RouteService.java                  # Route management
│   │   ├── BookingService.java                # Booking logic
│   │   ├── SeatService.java                   # Seat management
│   │   └── PaymentService.java                # Payment processing
│   │
│   ├── repository/                            # Data Access Layer
│   │   ├── UserRepository.java
│   │   ├── BusRepository.java
│   │   ├── RouteRepository.java
│   │   ├── SeatRepository.java
│   │   ├── BookingRepository.java
│   │   └── PaymentRepository.java
│   │
│   ├── entity/                                # JPA Entity Classes
│   │   ├── User.java
│   │   ├── Bus.java
│   │   ├── Route.java
│   │   ├── Seat.java
│   │   ├── Booking.java
│   │   └── Payment.java
│   │
│   ├── dto/                                   # Data Transfer Objects
│   │   ├── UserRegisterDTO.java
│   │   ├── UserLoginDTO.java
│   │   ├── UserResponseDTO.java
│   │   ├── BusDTO.java
│   │   ├── RouteDTO.java
│   │   ├── SeatDTO.java
│   │   ├── BookingDTO.java
│   │   ├── BookingRequestDTO.java
│   │   ├── SearchBusRequestDTO.java
│   │   ├── LoginResponseDTO.java
│   │   └── ApiResponse.java
│   │
│   ├── security/                              # Security Configuration
│   │   ├── JwtTokenProvider.java             # JWT token generation & validation
│   │   └── SecurityConfig.java               # Spring Security configuration
│   │
│   └── exception/                             # Exception Handling
│       └── GlobalExceptionHandler.java       # Centralized error handling
│
├── src/main/resources/
│   └── application.properties                # Application configuration
│
├── pom.xml                                   # Maven configuration
├── README.md                                 # Project overview
├── SETUP_GUIDE.md                           # Detailed setup instructions
├── API_ENDPOINTS.md                         # Complete API documentation
└── DATABASE_SCHEMA.md                       # Database schema details
```

---

## 📚 Setup Guide

Detailed step-by-step setup instructions are available in [SETUP_GUIDE.md](SETUP_GUIDE.md)

### Quick Summary:
1. Install Java 17, Maven, MySQL
2. Create MySQL database: `bus_reservation_db`
3. Update database credentials in `application.properties`
4. Run: `mvn clean install && mvn spring-boot:run`

---

## 📖 API Documentation

Complete API documentation with request/response examples is available in [API_ENDPOINTS.md](API_ENDPOINTS.md)

### Main API Endpoints:

#### Authentication
- `POST /v1/auth/register` - Register new user
- `POST /v1/auth/login` - Login user
- `GET /v1/auth/validate-token` - Validate JWT token

#### Bus Operations
- `POST /v1/buses/search` - Search buses
- `GET /v1/buses/{busId}` - Get bus details
- `GET /v1/buses/type/{busType}` - Filter by type

#### Bookings
- `POST /v1/bookings` - Create booking
- `GET /v1/bookings/{bookingId}` - Get booking details
- `GET /v1/bookings/user/{userId}` - User bookings
- `PUT /v1/bookings/{bookingId}/confirm` - Confirm booking
- `PUT /v1/bookings/{bookingId}/cancel` - Cancel booking

#### Dashboard
- `GET /v1/dashboard/{userId}` - User dashboard
- `GET /v1/dashboard/{userId}/booking-summary` - Booking statistics

#### Seats
- `GET /v1/seats/bus/{busId}/available` - Available seats
- `GET /v1/seats/bus/{busId}` - All seats

---

## 🗄️ Database Schema

Comprehensive database schema documentation is available in [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)

### Main Tables:
- **users** - User/passenger information
- **routes** - Bus routes between cities
- **buses** - Bus details and schedules
- **seats** - Individual seat tracking
- **bookings** - Booking records
- **payments** - Payment transactions

---

## ✨ Features

### 1. User Management
- User registration with validation
- Secure login with JWT authentication
- User profile management
- Email and username uniqueness checks

### 2. Bus Search
- Search buses by source, destination, and date
- Filter by bus type and operator
- Real-time seat availability
- Bus pricing and schedule information

### 3. Seat Management
- Automatic seat initialization per date
- Track seat availability in real-time
- Seat type classification (Window, Middle, Aisle)
- Prevent double booking

### 4. Booking System
- Create bookings with multiple seats
- Unique booking reference generation
- Booking status tracking (Pending, Confirmed, Cancelled)
- Payment status tracking

### 5. Dashboard
- User profile overview
- Recent bookings display
- Booking statistics and summary
- Total amount spent calculation

### 6. Security
- BCrypt password encryption
- JWT token-based authentication
- Token expiration and validation
- CORS support for frontend integration

---

## 🔧 Configuration

### Database Configuration
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bus_reservation_db
spring.datasource.username=root
spring.datasource.password=
```

### JWT Configuration
```properties
jwt.secret=mySecretKeyForJWTTokenGeneration
jwt.expiration=86400000  # 24 hours
```

### Server Configuration
```properties
server.port=8080
server.servlet.context-path=/api
```

---

## 📝 API Request/Response Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "username": "johndoe",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

### Search Buses
```bash
curl -X POST http://localhost:8080/api/v1/buses/search \
  -H "Content-Type: application/json" \
  -d '{
    "source": "New York",
    "destination": "Boston",
    "journeyDate": "2024-09-15"
  }'
```

### Create Booking
```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "userId: 1" \
  -d '{
    "busId": 1,
    "seatNumbers": [5, 6, 7],
    "journeyDate": "2024-09-15",
    "passengerName": "John Doe",
    "passengerEmail": "john@example.com",
    "passengerPhone": "9876543210"
  }'
```

---

## 🧪 Testing

### Using Postman
1. Import the provided Postman collection
2. Set base URL: `http://localhost:8080/api`
3. Test endpoints with provided examples

### Using cURL
See examples in [API_ENDPOINTS.md](API_ENDPOINTS.md)

### Using VS Code REST Client
Install "REST Client" extension and create `.http` files

---

## 🚀 Deployment

### Build Production JAR
```bash
mvn clean package
```

### Run JAR
```bash
java -jar target/bus-reservation-system-1.0.0.jar
```

For production environment configuration, see [SETUP_GUIDE.md](SETUP_GUIDE.md#environment-configurations)

---

## 🐛 Troubleshooting

Common issues and solutions are documented in [SETUP_GUIDE.md#troubleshooting](SETUP_GUIDE.md#troubleshooting)

---

## 📊 Future Enhancements

- [ ] Payment gateway integration (Stripe, PayPal)
- [ ] Email notifications
- [ ] SMS alerts for bookings
- [ ] Admin dashboard
- [ ] Rating and review system
- [ ] Cancellation and refund policies
- [ ] Multi-language support
- [ ] Mobile app APIs

---

## 📞 Support & Documentation

- **API Documentation:** [API_ENDPOINTS.md](API_ENDPOINTS.md)
- **Setup Guide:** [SETUP_GUIDE.md](SETUP_GUIDE.md)
- **Database Schema:** [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)

---

## 👥 Team Members

1. **Subha Varshini TJ**
2. **Prakalya RB**

---

## 📄 License

This project is provided as-is for educational and development purposes.

---

## 🙏 Acknowledgments

- Spring Boot Team
- MySQL Community
- JWT Library Contributors

---

**Happy Coding! 🚀**

For detailed information about specific components, please refer to the documentation files:
- Setup Instructions: [SETUP_GUIDE.md](SETUP_GUIDE.md)
- API Endpoints: [API_ENDPOINTS.md](API_ENDPOINTS.md)
- Database Schema: [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)