# Bus Reservation System - Database Schema

## Database Overview

**Database Name:** `bus_reservation_db`
**Charset:** UTF-8 MB4
**Engine:** InnoDB

The database consists of 6 main tables:
1. `users` - User/Passenger information
2. `routes` - Bus routes between cities
3. `buses` - Bus information
4. `seats` - Individual seat information
5. `bookings` - Bus ticket bookings
6. `payments` - Payment transaction records

---

## Table Schemas

### 1. USERS Table
Stores user/passenger registration and profile information.

```sql
CREATE TABLE users (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20),
  address VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(100),
  postal_code VARCHAR(20),
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX idx_email (email),
  INDEX idx_username (username)
);
```

**Columns:**
- `user_id` - Unique identifier (Primary Key)
- `first_name` - User's first name
- `last_name` - User's last name
- `email` - Email address (Unique)
- `username` - Login username (Unique)
- `password` - Hashed password
- `phone_number` - Contact number
- `address` - Street address
- `city` - City
- `state` - State/Province
- `postal_code` - ZIP/Postal code
- `is_active` - Account status
- `created_at` - Registration timestamp
- `updated_at` - Last update timestamp

---

### 2. ROUTES Table
Stores information about bus routes between cities.

```sql
CREATE TABLE routes (
  route_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  source VARCHAR(100) NOT NULL,
  destination VARCHAR(100) NOT NULL,
  distance_km DECIMAL(10, 2),
  approximate_duration_hours INT,
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  INDEX idx_source (source),
  INDEX idx_destination (destination),
  UNIQUE KEY unique_route (source, destination)
);
```

**Columns:**
- `route_id` - Unique identifier (Primary Key)
- `source` - Departure city
- `destination` - Arrival city
- `distance_km` - Distance in kilometers
- `approximate_duration_hours` - Travel time in hours
- `is_active` - Route status
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

---

### 3. BUSES Table
Stores bus information including type, capacity, and schedule.

```sql
CREATE TABLE buses (
  bus_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bus_name VARCHAR(100) NOT NULL,
  bus_number VARCHAR(50) NOT NULL UNIQUE,
  total_seats INT NOT NULL,
  bus_type VARCHAR(50),
  amenities VARCHAR(255),
  route_id BIGINT NOT NULL,
  departure_time VARCHAR(5),
  arrival_time VARCHAR(5),
  price_per_seat DECIMAL(10, 2) NOT NULL,
  operator_name VARCHAR(100),
  is_active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  FOREIGN KEY (route_id) REFERENCES routes(route_id),
  INDEX idx_route_id (route_id),
  INDEX idx_bus_number (bus_number),
  INDEX idx_operator (operator_name)
);
```

**Columns:**
- `bus_id` - Unique identifier (Primary Key)
- `bus_name` - Name of the bus (e.g., "Express Coach")
- `bus_number` - Registration number (Unique)
- `total_seats` - Total seat capacity
- `bus_type` - Type (AC, Non-AC, Sleeper, Semi-Sleeper)
- `amenities` - Available amenities (WiFi, Charging Port, etc.)
- `route_id` - Route ID (Foreign Key)
- `departure_time` - Departure time (HH:MM format)
- `arrival_time` - Arrival time (HH:MM format)
- `price_per_seat` - Cost per seat
- `operator_name` - Bus company name
- `is_active` - Bus status
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

---

### 4. SEATS Table
Stores individual seat information for each bus on specific dates.

```sql
CREATE TABLE seats (
  seat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bus_id BIGINT NOT NULL,
  seat_number INT NOT NULL,
  seat_type VARCHAR(20),
  is_available BOOLEAN NOT NULL DEFAULT true,
  booking_date VARCHAR(10),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (bus_id) REFERENCES buses(bus_id),
  INDEX idx_bus_id (bus_id),
  INDEX idx_booking_date (booking_date),
  UNIQUE KEY unique_seat (bus_id, seat_number, booking_date)
);
```

**Columns:**
- `seat_id` - Unique identifier (Primary Key)
- `bus_id` - Bus ID (Foreign Key)
- `seat_number` - Seat number (1-40)
- `seat_type` - Type (WINDOW, MIDDLE, AISLE)
- `is_available` - Availability status
- `booking_date` - Date for which seat is tracked (YYYY-MM-DD)
- `created_at` - Creation timestamp

**Seat Types:**
- `WINDOW` - Window side seat
- `MIDDLE` - Middle seat
- `AISLE` - Aisle side seat

---

### 5. BOOKINGS Table
Stores bus ticket booking information.

```sql
CREATE TABLE bookings (
  booking_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  bus_id BIGINT NOT NULL,
  seat_numbers VARCHAR(255) NOT NULL,
  number_of_seats INT NOT NULL,
  journey_date VARCHAR(10) NOT NULL,
  total_price DECIMAL(10, 2) NOT NULL,
  booking_status VARCHAR(50) NOT NULL,
  payment_status VARCHAR(50) NOT NULL,
  passenger_name VARCHAR(100) NOT NULL,
  passenger_email VARCHAR(100) NOT NULL,
  passenger_phone VARCHAR(20) NOT NULL,
  booking_reference VARCHAR(50) UNIQUE,
  booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cancelled_at TIMESTAMP NULL,
  
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (bus_id) REFERENCES buses(bus_id),
  INDEX idx_user_id (user_id),
  INDEX idx_bus_id (bus_id),
  INDEX idx_booking_reference (booking_reference),
  INDEX idx_journey_date (journey_date),
  INDEX idx_booking_status (booking_status)
);
```

**Columns:**
- `booking_id` - Unique identifier (Primary Key)
- `user_id` - User ID (Foreign Key)
- `bus_id` - Bus ID (Foreign Key)
- `seat_numbers` - Comma-separated seat numbers
- `number_of_seats` - Total seats booked
- `journey_date` - Travel date (YYYY-MM-DD)
- `total_price` - Total booking price
- `booking_status` - Status (PENDING, CONFIRMED, CANCELLED)
- `payment_status` - Payment status (PENDING, PAID, FAILED)
- `passenger_name` - Name of passenger
- `passenger_email` - Passenger email
- `passenger_phone` - Passenger phone
- `booking_reference` - Unique booking reference code
- `booked_at` - Booking timestamp
- `updated_at` - Last update timestamp
- `cancelled_at` - Cancellation timestamp

**Status Values:**
- Booking Status: `PENDING`, `CONFIRMED`, `CANCELLED`
- Payment Status: `PENDING`, `PAID`, `FAILED`

---

### 6. PAYMENTS Table
Stores payment transaction information.

```sql
CREATE TABLE payments (
  payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  booking_id BIGINT NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  payment_method VARCHAR(50) NOT NULL,
  transaction_id VARCHAR(100),
  payment_status VARCHAR(50) NOT NULL,
  payment_gateway VARCHAR(50),
  reference_number VARCHAR(100),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  processed_at TIMESTAMP NULL,
  
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
  INDEX idx_booking_id (booking_id),
  INDEX idx_transaction_id (transaction_id),
  INDEX idx_payment_status (payment_status),
  UNIQUE KEY unique_booking_payment (booking_id)
);
```

**Columns:**
- `payment_id` - Unique identifier (Primary Key)
- `booking_id` - Booking ID (Foreign Key, Unique)
- `amount` - Payment amount
- `payment_method` - Method (CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING)
- `transaction_id` - Payment gateway transaction ID
- `payment_status` - Status (PENDING, SUCCESS, FAILED)
- `payment_gateway` - Gateway used (Stripe, PayPal, RazorPay, etc.)
- `reference_number` - Internal reference number
- `created_at` - Creation timestamp
- `processed_at` - Processing completion timestamp

---

## Entity Relationships

```
users (1) ──────── (N) bookings
          └─────────── (1) payments

routes (1) ──────── (N) buses
                      └──── (N) seats
                      
buses (1) ──────---- (N) bookings
       └────────────── (N) seats
       
bookings (1) ────── (1) payments
```

---

## Sample Data Queries

### Insert Sample Route
```sql
INSERT INTO routes (source, destination, distance_km, approximate_duration_hours, is_active)
VALUES ('New York', 'Boston', 215, 4, true);
```

### Insert Sample Bus
```sql
INSERT INTO buses (bus_name, bus_number, total_seats, bus_type, amenities, route_id, departure_time, arrival_time, price_per_seat, operator_name)
VALUES ('Express Coach', 'BUS-001', 40, 'AC', 'WiFi,Charging Port,Blanket', 1, '10:00', '14:00', 50.00, 'Express Travel');
```

### Insert Sample User
```sql
INSERT INTO users (first_name, last_name, email, username, password, phone_number, city)
VALUES ('John', 'Doe', 'john@example.com', 'johndoe', '$2a$10$encrypted_password', '9876543210', 'New York');
```

### Insert Sample Booking
```sql
INSERT INTO bookings (user_id, bus_id, seat_numbers, number_of_seats, journey_date, total_price, booking_status, payment_status, passenger_name, passenger_email, passenger_phone, booking_reference)
VALUES (1, 1, '5,6,7', 3, '2024-09-15', 150.00, 'PENDING', 'PENDING', 'John Doe', 'john@example.com', '9876543210', 'BUS-12345678');
```

---

## Indexing Strategy

### Primary Indexes
- Primary Keys on all tables (auto-increment)
- Foreign Keys for relationships

### Search Indexes
- `users.email` - For email lookups
- `users.username` - For username lookups
- `routes.source, routes.destination` - For route searches
- `buses.bus_number` - For bus identification
- `buses.operator_name` - For operator searches
- `bookings.user_id` - For user bookings
- `bookings.booking_reference` - For booking lookup
- `bookings.journey_date` - For date-based searches

---

## Performance Considerations

1. **Seat Initialization:** Seats are created per date to optimize availability queries
2. **Indexing:** Frequently searched columns are indexed
3. **Partitioning:** For production, consider partitioning bookings by date
4. **Archive:** Consider archiving old cancelled bookings quarterly

---

## Backup and Recovery

### Regular Backups
```bash
mysqldump -u root -p bus_reservation_db > bus_reservation_backup.sql
```

### Restore from Backup
```bash
mysql -u root -p bus_reservation_db < bus_reservation_backup.sql
```

---

## Database Maintenance

### Check Table Status
```sql
CHECK TABLE users, routes, buses, seats, bookings, payments;
```

### Optimize Tables
```sql
OPTIMIZE TABLE users, routes, buses, seats, bookings, payments;
```

### View Database Size
```sql
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'bus_reservation_db'
ORDER BY size_mb DESC;
```

---

## Security Considerations

1. **Passwords:** Stored as BCrypt hashes (never plain text)
2. **Sensitive Data:** Email and phone numbers should be validated
3. **SQL Injection:** Prepared statements used (ORM prevents this)
4. **Access Control:** Implement role-based access control for admin operations

---

## Migration Scripts (For Updates)

### Add New Column Example
```sql
ALTER TABLE users ADD COLUMN verification_token VARCHAR(255) AFTER password;
ALTER TABLE users ADD COLUMN is_verified BOOLEAN DEFAULT false;
```

### Add New Index
```sql
CREATE INDEX idx_users_email ON users(email);
```

---

## Scaling Considerations

For production environments with high traffic:

1. **Read Replicas:** Set up MySQL replication
2. **Partitioning:** Partition bookings table by date
3. **Caching:** Use Redis for frequently accessed data
4. **Connection Pooling:** Configure HikariCP in Spring Boot

---

## Common Queries

### Get available seats for a bus on a date
```sql
SELECT * FROM seats 
WHERE bus_id = 1 AND booking_date = '2024-09-15' AND is_available = true
ORDER BY seat_number;
```

### Get user booking history
```sql
SELECT b.*, bu.bus_name, r.source, r.destination
FROM bookings b
JOIN buses bu ON b.bus_id = bu.bus_id
JOIN routes r ON bu.route_id = r.route_id
WHERE b.user_id = 1
ORDER BY b.booked_at DESC;
```

### Get buses for a route on a date with available seats
```sql
SELECT b.*, COUNT(s.seat_id) as available_seats
FROM buses b
LEFT JOIN seats s ON b.bus_id = s.bus_id AND s.booking_date = '2024-09-15' AND s.is_available = true
WHERE b.route_id = 1
GROUP BY b.bus_id;
```

### Get booking statistics
```sql
SELECT 
    booking_status,
    COUNT(*) as count,
    SUM(total_price) as revenue
FROM bookings
GROUP BY booking_status;
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2024-08-31 | Initial database schema |

---

**For more information, refer to the [SETUP_GUIDE.md](SETUP_GUIDE.md) and [API_ENDPOINTS.md](API_ENDPOINTS.md)**
