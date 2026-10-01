\# 🏟️ BookMyTurf — Turf Reservation Platform



A full-stack web application for booking sports turfs. Customers can search turfs, check real-time slot availability, and send booking requests. Turf Owners can list their turfs, manage availability, offer discounts, and accept or reject bookings.



\## 🚀 Features



\### Customer

\- Register / Login

\- Search turfs by name or location

\- View turf details, pricing, and operating hours

\- Check real-time slot availability by date

\- View active discounts on a turf

\- Book a slot (creates a pending booking request)

\- View booking history with status (Pending / Accepted / Rejected / Cancelled)

\- Cancel a booking while it is still Pending

\- View unique booking reference ID



\### Turf Owner

\- Register / Login

\- Add new turfs (name, location, address, sport type, price, slot duration, hours)

\- Edit existing turf details

\- Offer discounts on specific dates/time windows with a custom amount

\- View and manage all owned turfs

\- View incoming booking requests

\- Accept or reject booking requests

\- View confirmed bookings and booking reference IDs



\### Platform-wide

\- Role-based authentication (JWT) and authorization

\- Passwords hashed with BCrypt — never stored in plain text

\- Prevents double-booking of accepted slots

\- Dark-themed, responsive UI with a turf-sunset background



\## 🛠️ Technology Stack



\*\*Frontend:\*\* React.js, React Router, Axios, CSS (custom design system)

\*\*Backend:\*\* Spring Boot (Java 21), Spring Security, Spring Data JPA, JWT (jjwt)

\*\*Database:\*\* MySQL

\*\*Build Tools:\*\* Maven (backend), npm (frontend)



\## 📁 Project Structure

bookmyturf/

├── backend/ # Spring Boot application

│ └── src/main/java/com/turfreservation/

│ ├── controller/ # REST controllers

│ ├── service/ # Business logic

│ ├── repository/ # Spring Data JPA repositories

│ ├── entity/ # JPA entities (User, Turf, Booking, TurfDiscount)

│ ├── dto/ # Request/response DTOs

│ ├── security/ # JWT utilities and filter

│ ├── config/ # Security configuration

│ └── exception/ # Custom exceptions + global handler

├── frontend/ # React application

│ └── src/

│ ├── components/ # Reusable UI components

│ ├── pages/

│ │ ├── auth/ # Login, Register

│ │ ├── customer/ # Dashboard, Turf Details, Booking History

│ │ └── owner/ # Dashboard, Add/Edit Turf, Booking Requests

│ ├── services/ # Axios API calls

│ └── context/ # Auth context (JWT/session state)

└── README.md





\## 🗄️ Database Schema



\### users

| Column | Type | Notes |

|---|---|---|

| id | BIGINT (PK) | Auto-increment |

| name | VARCHAR | |

| email | VARCHAR | Unique |

| password | VARCHAR | BCrypt hash |

| phone | VARCHAR | |

| role | VARCHAR | `CUSTOMER` or `TURF\_OWNER` |

| created\_at | DATETIME | |



\### turfs

| Column | Type | Notes |

|---|---|---|

| id | BIGINT (PK) | |

| turf\_name | VARCHAR | |

| location | VARCHAR | |

| address | VARCHAR | |

| sport\_type | VARCHAR | |

| description | VARCHAR(1000) | |

| price\_per\_slot | DOUBLE | |

| slot\_duration | INT | Minutes |

| opening\_time | TIME | |

| closing\_time | TIME | |

| owner\_id | BIGINT (FK → users.id) | |

| created\_at | DATETIME | |



\### bookings

| Column | Type | Notes |

|---|---|---|

| id | BIGINT (PK) | |

| booking\_reference\_id | VARCHAR | Unique, e.g. `TRF-2026-AB1234` |

| customer\_id | BIGINT (FK → users.id) | |

| turf\_id | BIGINT (FK → turfs.id) | |

| booking\_date | DATE | |

| start\_time | TIME | |

| end\_time | TIME | |

| price | DOUBLE | After any applicable discount |

| status | ENUM | `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED` |

| created\_at | DATETIME | |

| updated\_at | DATETIME | |



\### turf\_discounts

| Column | Type | Notes |

|---|---|---|

| id | BIGINT (PK) | |

| turf\_id | BIGINT (FK → turfs.id) | |

| discount\_name | VARCHAR | |

| discount\_date | DATE | |

| start\_time | TIME | |

| end\_time | TIME | |

| discount\_amount | DOUBLE | ₹ off |

| created\_at | DATETIME | |



\## ⚙️ Setup Instructions



\### Prerequisites

\- Java 21 (or compatible JDK)

\- Maven (bundled with most IDEs, e.g. Eclipse)

\- Node.js 16+ and npm

\- MySQL 8.x



\### 1. Clone the repository

```bash

git clone <your-repo-url>

cd bookmyturf

```



\### 2. Configure MySQL

Create the database:

```sql

CREATE DATABASE turf\_reservation\_db;

```



\### 3. Configure the backend

Open `backend/src/main/resources/application.properties` and set your MySQL credentials:

```properties

spring.datasource.url=jdbc:mysql://localhost:3306/turf\_reservation\_db?useSSL=false\&allowPublicKeyRetrieval=true\&serverTimezone=UTC\&createDatabaseIfNotExist=true

spring.datasource.username=root

spring.datasource.password=YOUR\_MYSQL\_PASSWORD

```



\### 4. Run the backend

Using Eclipse: Right-click the `backend` project → Run As → Spring Boot App

Using Maven CLI:

```bash

cd backend

mvn spring-boot:run

```

The backend starts on `http://localhost:8080`. Hibernate auto-creates all tables on first run.



\### 5. Run the frontend

```bash

cd frontend

npm install

npm start

```

The frontend starts on `http://localhost:3000` and opens automatically in your browser.



\## 🔌 API Overview



| Method | Endpoint | Access | Description |

|---|---|---|---|

| POST | `/api/auth/register` | Public | Register a new user |

| POST | `/api/auth/login` | Public | Login and receive a JWT |

| GET | `/api/turfs/all` | Public | List all turfs |

| GET | `/api/turfs/search?keyword=` | Public | Search turfs by name/location |

| GET | `/api/turfs/search/location?location=` | Public | Search turfs by location |

| GET | `/api/turfs/{id}` | Public | Get turf details |

| POST | `/api/turfs/owner/add` | Turf Owner | Add a turf |

| PUT | `/api/turfs/owner/{turfId}` | Turf Owner | Update a turf |

| GET | `/api/turfs/owner/my-turfs` | Turf Owner | List own turfs |

| GET | `/api/slots/{turfId}?date=` | Public | Get available slots for a date |

| GET | `/api/discounts/turf/{turfId}` | Public | Get active discounts for a turf |

| POST | `/api/discounts/owner/{turfId}/add` | Turf Owner | Add a discount |

| DELETE | `/api/discounts/owner/{discountId}` | Turf Owner | Delete a discount |

| POST | `/api/bookings/create` | Customer | Create a booking request |

| GET | `/api/bookings/customer/history` | Customer | View own booking history |

| PUT | `/api/bookings/{id}/cancel` | Customer | Cancel own pending booking |

| GET | `/api/bookings/owner/requests` | Turf Owner | View booking requests |

| PUT | `/api/bookings/{id}/accept` | Turf Owner | Accept a booking |

| PUT | `/api/bookings/{id}/reject` | Turf Owner | Reject a booking |

| GET | `/api/bookings/{id}` | Owner/Customer | Get booking details |



All protected endpoints require `Authorization: Bearer <token>` header.



\## 👥 User Roles and Permissions



| Action | Customer | Turf Owner |

|---|---|---|

| Search/view turfs | ✅ | ✅ |

| Add/edit turfs | ❌ | ✅ (own turfs only) |

| Add discounts | ❌ | ✅ (own turfs only) |

| Create bookings | ✅ | ❌ |

| Accept/reject bookings | ❌ | ✅ (own turfs only) |

| Cancel own booking | ✅ (while pending) | ❌ |



Role is embedded in the JWT and enforced on every request via Spring Security; mismatched roles receive `403 Forbidden`.



\## 🔄 Booking Workflow



1\. Customer selects a turf, date, and available time slot

2\. Customer clicks "Book Slot" → system checks for conflicts, applies any matching discount, and generates a unique booking reference ID

3\. Booking is created with status `PENDING`

4\. Turf Owner views the request on their Booking Requests page

5\. Owner \*\*Accepts\*\* → status becomes `ACCEPTED`, slot is locked for other customers

&#x20;  Owner \*\*Rejects\*\* → status becomes `REJECTED`, slot remains available

6\. Customer can \*\*Cancel\*\* their own request anytime before it's accepted/rejected → status becomes `CANCELLED`



\## ⚠️ Known Limitations



\- No real payment gateway integration (booking reference ID serves as the transaction identifier)

\- No email/SMS notifications

\- No password reset / forgot password flow

\- No admin role or platform-wide analytics

\- No image upload for turfs

\- Token revocation on logout is client-side only (JWT is stateless; no server-side blacklist)



\## 🔮 Future Improvements



\- Real payment gateway integration

\- Email/SMS notifications for booking status changes

\- Forgot password functionality

\- Turf image uploads and galleries

\- Ratings and reviews for turfs

\- Admin dashboard with platform-wide analytics

\- Advanced location search (map-based)



\## ✅ Manual Testing Checklist



\- \[ ] Register as Customer and as Turf Owner

\- \[ ] Login with correct/incorrect role and verify role mismatch is rejected

\- \[ ] Turf Owner adds a turf

\- \[ ] Turf Owner edits turf details

\- \[ ] Turf Owner adds a discount for a specific date/time

\- \[ ] Customer searches turfs by name and by location

\- \[ ] Customer views turf details and sees the discount listed

\- \[ ] Customer selects a date and views available slots

\- \[ ] Customer books a discounted slot and confirms the reduced price

\- \[ ] Customer cancels a pending booking

\- \[ ] Turf Owner accepts a booking request

\- \[ ] Turf Owner rejects a booking request

\- \[ ] Confirm an accepted slot is no longer available to other customers (double-booking prevention)

\- \[ ] Customer views booking history filtered by status

\- \[ ] Attempt to access an owner-only endpoint with a customer token → confirm `403 Forbidden`



\## 📄 License



This project was built for academic/learning purposes.

