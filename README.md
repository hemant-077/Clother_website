# Clother Website

A full-stack clothing e-commerce application built with React on the frontend and Spring Boot on the backend. The project includes product listing, cart management, checkout, Stripe payment integration, order tracking, and MySQL-backed persistence.

## Overview

This project is a modern clothing store demo where users can:

- browse products from a catalog
- view product details and variants
- add items to a cart
- update quantities in the cart
- place an order through checkout
- pay using Stripe Checkout
- receive payment confirmation via webhook
- view order details from the backend

It is designed as a starter/full-stack e-commerce project that can be used for learning, prototyping, or extending into a real online store.

---

## Tech Stack

### Frontend
- React
- Vite
- JavaScript / JSX
- Custom styling

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Bean Validation

### Database
- MySQL

### Payments
- Stripe Checkout
- Stripe Webhooks

---

## Project Structure

```text
Clothes_Website/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── .env.example
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── index.html
├── pom.xml
├── README.md
└── .gitignore
```

---

## Features Implemented

- Product catalog with variants for size, color, SKU, and stock
- Dynamic cart management in React
- Checkout request validation with backend rules
- Order creation and database persistence
- Stripe Checkout session generation
- Webhook handling for successful or expired payment sessions
- Seed data loader for initial product catalog setup
- CORS configuration for frontend-backend communication

---

## Prerequisites

Before running the project, make sure you have:

- Java 17 or later
- Maven
- Node.js and npm
- MySQL installed and running
- A Stripe account with test keys

---

## MySQL Setup

Create a local MySQL database or use an existing one. The application is configured to create the database automatically if permitted.

Example environment variables:

```bash
export DB_URL='jdbc:mysql://localhost:3306/clothes_store?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
```

If your password is different, replace it accordingly.

---

## Backend Setup

Run the backend from the root project or inside the backend folder:

```bash
cd backend
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export STRIPE_ENABLED=true
export STRIPE_SECRET_KEY=sk_test_your_key
export STRIPE_WEBHOOK_SECRET=whsec_your_secret
mvn spring-boot:run
```

The backend server will start on:

```text
http://localhost:8080
```

### Backend API Endpoints

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/checkout
GET    /api/orders/{orderNumber}
POST   /api/stripe/webhook
```

---

## Frontend Setup

Install dependencies and launch the app:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on:

```text
http://localhost:5173
```

Optional frontend env example:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## Stripe Configuration

Stripe payments are enabled through backend environment variables.

Required values:

- `STRIPE_ENABLED=true`
- `STRIPE_SECRET_KEY=sk_test_...`
- `STRIPE_WEBHOOK_SECRET=whsec_...`

Important:
- The secret key must start with `sk_`
- If Stripe is disabled or the key is missing, checkout will not proceed successfully
- Webhook endpoint is used to confirm successful or expired payments

Stripe webhook route:

```text
POST http://localhost:8080/api/stripe/webhook
```

---

## Running the Full App

1. Start MySQL
2. Start the backend:
   ```bash
   cd backend && mvn spring-boot:run
   ```
3. Start the frontend:
   ```bash
   cd frontend && npm install && npm run dev
   ```
4. Open the frontend in the browser at:
   ```text
   http://localhost:5173
   ```

---

## Notes

- The app uses `spring.jpa.hibernate.ddl-auto=update`, so database tables are created automatically.
- The backend includes a data seeder that loads initial product catalog data on startup.
- This project is well-suited for learning full-stack e-commerce architecture and can be expanded with authentication, admin panels, order filters, and more.

---

## License

This project is for learning and development purposes.
