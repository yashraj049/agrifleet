# 🚜 AgriFleet - Farm Equipment Leasing Platform

A full-stack enterprise-grade platform connecting farmers with equipment owners for heavy machinery leasing.

## 🌟 Real World Problem Solved
Small-scale farmers in India cannot afford heavy machinery (tractors, harvesters). Equipment owners have idle machinery during off-seasons. AgriFleet bridges this gap.

## 🏗️ Tech Stack
- **Backend:** Java 21, Spring Boot 4.1.1
- **Database:** MySQL 9.7, Redis (caching)
- **Security:** JWT Authentication, Spring Security
- **Frontend:** React.js, Tailwind CSS

## ⚡ Key Features
- 📍 **Geospatial Search** — Haversine formula finds equipment within GPS radius
- 💰 **Dynamic Pricing** — 15% price increase during peak harvest seasons (Kharif/Rabi)
- 🔒 **Double Booking Prevention** — Optimistic locking with @Version annotation
- 🔄 **Booking State Machine** — REQUESTED→APPROVED→IN_TRANSIT→ACTIVE→COMPLETED
- 👥 **Role Based Access** — FARMER / OWNER / ADMIN roles with JWT
- 🌾 **Peak Season Detection** — Auto-detects Indian harvest seasons

## 🚀 API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login & get JWT token |
| GET | /api/equipment/search | Search nearby equipment |
| POST | /api/equipment/add | Add new equipment (Owner) |
| POST | /api/bookings/create | Create booking (Farmer) |
| PUT | /api/bookings/{id}/status | Update booking status |

## 🗄️ Database Schema
- **users** — Farmers and Equipment Owners
- **equipment** — Listed machinery with GPS coordinates
- **bookings** — Booking lifecycle with state machine

## ⚙️ Setup & Run
```bash
# 1. Clone the repo
git clone https://github.com/yashraj049/agrifleet.git

# 2. Create MySQL database
mysql -u root -p
CREATE DATABASE agrifleet;

# 3. Update application.properties
spring.datasource.password=your_password

# 4. Run
./mvnw spring-boot:run
```

## 👨‍💻 Developer
**Yashraj Patil** — MCA Data Science, MIT-ADT University