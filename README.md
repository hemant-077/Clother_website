# Clothly Ecommerce

Full-stack clothing ecommerce starter with React, Spring Boot, MySQL, and Stripe Checkout.

## Stack

- Frontend: React + Vite
- Backend: Spring Boot, JPA, Bean Validation
- Database: MySQL
- Payments: Stripe Checkout + webhook support

## What Is Implemented

- Clothing catalog with product variants for size, color, SKU, and stock
- Search and category filtering
- Persistent React cart with quantity controls
- Server-side checkout validation and pricing
- Stock reservation during checkout creation
- Stripe Checkout session creation
- Stripe webhook handling for paid and expired sessions
- Seed catalog data on first backend startup

## Run MySQL

Create a local database user or use your existing MySQL root user. The backend JDBC URL includes `createDatabaseIfNotExist=true`, so `clothes_store` can be created automatically if the user has permission.

```bash
export DB_URL='jdbc:mysql://localhost:3306/clothes_store?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
```

## Backend

```bash
cd backend
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export STRIPE_ENABLED=true
export STRIPE_SECRET_KEY=sk_test_your_key
mvn spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

For webhook confirmation from Stripe:

```bash
export STRIPE_WEBHOOK_SECRET=whsec_your_secret
```

Stripe webhook endpoint:

```text
POST http://localhost:8080/api/stripe/webhook
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```text
http://localhost:5173
```

Optional frontend env:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

## API

```text
GET  /api/products
GET  /api/products/{id}
POST /api/checkout
GET  /api/orders/{orderNumber}
POST /api/stripe/webhook
```

## Stripe Notes

Checkout requires `STRIPE_ENABLED=true` and a backend secret key that starts with `sk_`. If Stripe is disabled or the key is missing, the API returns an error instead of placing the order directly.
# Clother_website
