# Bus Reservation System - API Endpoints Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints except login and registration require a JWT token in the Authorization header:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 1. AUTHENTICATION ENDPOINTS

### Register User
**POST** `/v1/auth/register`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "username": "johndoe",
  "password": "password123",
  "confirmPassword": "password123",
  "phoneNumber": "9876543210",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "postalCode": "10001"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "username": "johndoe",
    "phoneNumber": "9876543210",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "isActive": true,
    "createdAt": "2024-08-31T10:00:00"
  },
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Login User
**POST** `/v1/auth/login`

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "userId": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com",
      "username": "johndoe",
      "isActive": true,
      "createdAt": "2024-08-31T10:00:00"
    }
  },
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Check Email Exists
**GET** `/v1/auth/check-email?email=john@example.com`

**Response (200):**
```json
{
  "success": true,
  "message": "Email check completed",
  "data": true
}
```

---

### Check Username Exists
**GET** `/v1/auth/check-username?username=johndoe`

**Response (200):**
```json
{
  "success": true,
  "message": "Username check completed",
  "data": false
}
```

---

### Validate Token
**GET** `/v1/auth/validate-token`
**Headers:** `Authorization: Bearer <JWT_TOKEN>`

**Response (200):**
```json
{
  "success": true,
  "message": "Token validation completed",
  "data": true
}
```

---

## 2. BUS SEARCH & LISTING ENDPOINTS

### Search Buses
**POST** `/v1/buses/search`

**Request Body:**
```json
{
  "source": "New York",
  "destination": "Boston",
  "journeyDate": "2024-09-15"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Buses found successfully",
  "data": [
    {
      "busId": 1,
      "busName": "Express Coach",
      "busNumber": "BUS-001",
      "totalSeats": 40,
      "busType": "AC",
      "amenities": "WiFi,Charging Port",
      "route": {
        "routeId": 1,
        "source": "New York",
        "destination": "Boston",
        "distanceKm": 215,
        "approximateDurationHours": 4
      },
      "departureTime": "10:00",
      "arrivalTime": "14:00",
      "pricePerSeat": 50.0,
      "operatorName": "Express Travel",
      "availableSeats": 15,
      "isActive": true
    }
  ],
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Get Bus by ID
**GET** `/v1/buses/{busId}?journeyDate=2024-09-15`

**Response (200):**
```json
{
  "success": true,
  "message": "Bus retrieved successfully",
  "data": {
    "busId": 1,
    "busName": "Express Coach",
    "busNumber": "BUS-001",
    "totalSeats": 40,
    "busType": "AC",
    "amenities": "WiFi,Charging Port",
    "route": {...},
    "departureTime": "10:00",
    "arrivalTime": "14:00",
    "pricePerSeat": 50.0,
    "operatorName": "Express Travel",
    "availableSeats": 15,
    "isActive": true
  },
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Get All Buses
**GET** `/v1/buses?journeyDate=2024-09-15`

**Response (200):** Same as search buses

---

### Get Buses by Type
**GET** `/v1/buses/type/AC?journeyDate=2024-09-15`

**Response (200):** List of buses with type AC

---

### Get Buses by Operator
**GET** `/v1/buses/operator/Express%20Travel?journeyDate=2024-09-15`

**Response (200):** List of buses by operator

---

### Add Bus (Admin)
**POST** `/v1/buses`

**Request Body:**
```json
{
  "busName": "Express Coach",
  "busNumber": "BUS-001",
  "totalSeats": 40,
  "busType": "AC",
  "amenities": "WiFi,Charging Port",
  "route": {
    "routeId": 1
  },
  "departureTime": "10:00",
  "arrivalTime": "14:00",
  "pricePerSeat": 50.0,
  "operatorName": "Express Travel"
}
```

**Response (201):** Created bus object

---

### Update Bus (Admin)
**PUT** `/v1/buses/{busId}`

**Request Body:** Same as Add Bus

**Response (200):** Updated bus object

---

### Delete Bus (Admin)
**DELETE** `/v1/buses/{busId}`

**Response (200):**
```json
{
  "success": true,
  "message": "Bus deleted successfully",
  "data": null
}
```

---

## 3. BOOKING ENDPOINTS

### Create Booking
**POST** `/v1/bookings`
**Headers:** `userId: 1`

**Request Body:**
```json
{
  "busId": 1,
  "seatNumbers": [5, 6, 7],
  "journeyDate": "2024-09-15",
  "passengerName": "John Doe",
  "passengerEmail": "john@example.com",
  "passengerPhone": "9876543210"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "bookingId": 1,
    "userId": 1,
    "busId": 1,
    "seatNumbers": "5,6,7",
    "numberOfSeats": 3,
    "journeyDate": "2024-09-15",
    "totalPrice": 150.0,
    "bookingStatus": "PENDING",
    "paymentStatus": "PENDING",
    "passengerName": "John Doe",
    "passengerEmail": "john@example.com",
    "passengerPhone": "9876543210",
    "bookingReference": "BUS-12345678",
    "bookedAt": "2024-08-31T10:00:00"
  },
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Get Booking by ID
**GET** `/v1/bookings/{bookingId}`

**Response (200):** Booking object

---

### Get Booking by Reference
**GET** `/v1/bookings/reference/BUS-12345678`

**Response (200):** Booking object

---

### Get User Bookings
**GET** `/v1/bookings/user/{userId}`

**Response (200):** List of bookings

---

### Get Active Bookings
**GET** `/v1/bookings/user/{userId}/active`

**Response (200):** List of active bookings

---

### Get Recent Bookings (Dashboard)
**GET** `/v1/bookings/user/{userId}/recent?limit=5`

**Response (200):** List of recent bookings

---

### Confirm Booking
**PUT** `/v1/bookings/{bookingId}/confirm`

**Response (200):** Updated booking with status CONFIRMED

---

### Cancel Booking
**PUT** `/v1/bookings/{bookingId}/cancel`

**Response (200):** Cancelled booking object

---

### Get Bookings by Status
**GET** `/v1/bookings/status/CONFIRMED`

**Response (200):** List of bookings with specified status

---

## 4. SEAT ENDPOINTS

### Get Available Seats
**GET** `/v1/seats/bus/{busId}/available?journeyDate=2024-09-15`

**Response (200):**
```json
{
  "success": true,
  "message": "Available seats retrieved successfully",
  "data": [
    {
      "seatId": 1,
      "seatNumber": 1,
      "seatType": "WINDOW",
      "isAvailable": true,
      "bookingDate": "2024-09-15"
    },
    {
      "seatId": 2,
      "seatNumber": 2,
      "seatType": "MIDDLE",
      "isAvailable": true,
      "bookingDate": "2024-09-15"
    }
  ],
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Get All Seats
**GET** `/v1/seats/bus/{busId}?journeyDate=2024-09-15`

**Response (200):** List of all seats (available and booked)

---

### Get Seat by ID
**GET** `/v1/seats/{seatId}`

**Response (200):** Seat object

---

### Count Available Seats
**GET** `/v1/seats/bus/{busId}/count?journeyDate=2024-09-15`

**Response (200):**
```json
{
  "success": true,
  "message": "Available seat count retrieved successfully",
  "data": 15,
  "timestamp": "2024-08-31T10:00:00"
}
```

---

## 5. USER ENDPOINTS

### Get User by ID
**GET** `/v1/users/{userId}`

**Response (200):** User object

---

### Get User by Email
**GET** `/v1/users/email/john@example.com`

**Response (200):** User object

---

### Get User by Username
**GET** `/v1/users/username/johndoe`

**Response (200):** User object

---

### Update User Profile
**PUT** `/v1/users/{userId}`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "9876543210",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "postalCode": "10001"
}
```

**Response (200):** Updated user object

---

### Get User Profile
**GET** `/v1/users/{userId}/profile`

**Response (200):** User object

---

## 6. DASHBOARD ENDPOINTS

### Get Dashboard
**GET** `/v1/dashboard/{userId}`

**Response (200):**
```json
{
  "success": true,
  "message": "Dashboard data retrieved successfully",
  "data": {
    "userProfile": {...},
    "recentBookings": [...],
    "totalBookings": 5,
    "activeBookings": 2,
    "cancelledBookings": 1
  },
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Get Booking Summary
**GET** `/v1/dashboard/{userId}/booking-summary`

**Response (200):**
```json
{
  "success": true,
  "message": "Booking summary retrieved successfully",
  "data": {
    "totalBookings": 5,
    "confirmedBookings": 3,
    "pendingBookings": 1,
    "cancelledBookings": 1,
    "totalAmountSpent": 500.0
  },
  "timestamp": "2024-08-31T10:00:00"
}
```

---

### Get User Statistics
**GET** `/v1/dashboard/{userId}/stats`

**Response (200):**
```json
{
  "success": true,
  "message": "User statistics retrieved successfully",
  "data": {
    "userId": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "totalBookings": 5,
    "memberSince": "2024-08-31T10:00:00",
    "bookingByStatus": {
      "CONFIRMED": 3,
      "PENDING": 1
    }
  },
  "timestamp": "2024-08-31T10:00:00"
}
```

---

## 7. ROUTE ENDPOINTS

### Get All Routes
**GET** `/v1/routes`

**Response (200):** List of all active routes

---

### Get Route by ID
**GET** `/v1/routes/{routeId}`

**Response (200):** Route object

---

### Search Routes
**GET** `/v1/routes/search?source=New York&destination=Boston`

**Response (200):** List of matching routes

---

### Get Routes by Source
**GET** `/v1/routes/source/New York`

**Response (200):** List of routes from source

---

### Get Routes by Destination
**GET** `/v1/routes/destination/Boston`

**Response (200):** List of routes to destination

---

### Add Route (Admin)
**POST** `/v1/routes`

**Request Body:**
```json
{
  "source": "New York",
  "destination": "Boston",
  "distanceKm": 215,
  "approximateDurationHours": 4
}
```

**Response (201):** Created route object

---

### Update Route (Admin)
**PUT** `/v1/routes/{routeId}`

**Request Body:** Same as Add Route

**Response (200):** Updated route object

---

### Delete Route (Admin)
**DELETE** `/v1/routes/{routeId}`

**Response (200):** Success message

---

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "Email is required",
  "data": null,
  "statusCode": 400,
  "errorCode": "ILLEGAL_ARGUMENT",
  "timestamp": "2024-08-31T10:00:00"
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Invalid username or password",
  "data": null,
  "statusCode": 401,
  "errorCode": "UNAUTHORIZED",
  "timestamp": "2024-08-31T10:00:00"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Bus not found with busId: 999",
  "data": null,
  "statusCode": 404,
  "errorCode": "NOT_FOUND",
  "timestamp": "2024-08-31T10:00:00"
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "An unexpected error occurred",
  "data": null,
  "statusCode": 500,
  "errorCode": "INTERNAL_SERVER_ERROR",
  "timestamp": "2024-08-31T10:00:00"
}
```
