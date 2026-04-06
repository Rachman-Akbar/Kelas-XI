# Local Village Marketplace - Architecture (MVP)

## 1) High-Level Architecture

- Backend: Laravel 13 REST API + Sanctum + MySQL
- Frontend: Next.js (App Router) + TypeScript + Tailwind + Axios
- Deployment mode for now: local development, separated apps in one repository folder.
- Restriction rule: all product visibility and order actions are constrained by `village_id` and must be enforced on backend query/service layer.

## 2) Backend Clean Architecture Direction

Feature-based modules under `app/Modules`:

- `Auth`
- `User`
- `Village`
- `Product`
- `Order`
- `Admin`

Each module follows a consistent shape:

- `Controllers/`
- `Requests/` (validation)
- `Services/` (business logic)
- `Repositories/` (query abstraction where needed)
- `Resources/` (API response transformer)
- `Policies/` (authorization)

Global technical layers:

- `app/Models` for Eloquent entities
- `routes/api.php` for versioned API grouping
- `app/Http/Middleware` for cross-cutting restrictions if needed

## 3) Database Schema (MVP)

### villages

- id (PK)
- name (string)
- code (string, unique)
- district (string, nullable)
- city (string, nullable)
- province (string, nullable)
- is_active (boolean, default true)
- created_at, updated_at

### users

- id (PK)
- village_id (FK -> villages.id, indexed)
- name (string)
- email (string, unique)
- password (string)
- phone (string, nullable)
- address (text, nullable)
- role (enum: admin,seller,buyer)
- is_active (boolean, default true)
- created_at, updated_at

### categories

- id (PK)
- village_id (FK -> villages.id, indexed, nullable for global category)
- name (string)
- slug (string)
- is_active (boolean, default true)
- created_at, updated_at

### products

- id (PK)
- village_id (FK -> villages.id, indexed)
- seller_id (FK -> users.id, indexed)
- category_id (FK -> categories.id, indexed)
- name (string)
- slug (string)
- description (text)
- price (decimal(12,2))
- stock (integer)
- status (enum: draft,published,archived,blocked)
- moderation_status (enum: pending,approved,rejected)
- created_at, updated_at

### product_images

- id (PK)
- product_id (FK -> products.id, indexed)
- path (string)
- is_primary (boolean, default false)
- sort_order (integer, default 0)
- created_at, updated_at

### orders

- id (PK)
- village_id (FK -> villages.id, indexed)
- buyer_id (FK -> users.id, indexed)
- seller_id (FK -> users.id, indexed)
- order_number (string, unique)
- status (enum: pending,paid,processed,completed,cancelled)
- subtotal (decimal(12,2))
- total (decimal(12,2))
- notes (text, nullable)
- created_at, updated_at

### order_items

- id (PK)
- order_id (FK -> orders.id, indexed)
- product_id (FK -> products.id, indexed)
- product_name (string)
- product_price (decimal(12,2))
- quantity (integer)
- line_total (decimal(12,2))
- created_at, updated_at

### password_reset_tokens and personal_access_tokens

- default Laravel tables for auth and Sanctum

## 4) API Design (MVP v1)

Base prefix: `/api/v1`

### Auth

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/forgot-password`
- `POST /auth/reset-password`
- `POST /auth/logout` (auth)
- `GET /auth/me` (auth)

### Villages

- `GET /villages` (admin)
- `POST /villages` (admin)
- `GET /villages/{id}` (admin)
- `PUT /villages/{id}` (admin)
- `DELETE /villages/{id}` (admin)

### Categories

- `GET /categories` (same village scope)
- `POST /categories` (admin)
- `PUT /categories/{id}` (admin)
- `DELETE /categories/{id}` (admin)

### Products

- `GET /products` (public authenticated: same village + approved + published)
- `GET /products/{id}` (same village)
- `POST /products` (seller)
- `PUT /products/{id}` (seller owner/admin)
- `DELETE /products/{id}` (seller owner/admin)
- `POST /products/{id}/images` (seller owner/admin)

### Orders

- `POST /orders/checkout` (buyer)
- `GET /orders` (buyer own + seller own)
- `GET /orders/{id}` (owner role/admin)
- `PATCH /orders/{id}/status` (seller/admin)

### Admin

- `GET /admin/dashboard`
- `GET /admin/users`
- `PATCH /admin/users/{id}/status`
- `GET /admin/products/moderation`
- `PATCH /admin/products/{id}/moderation`
- `GET /admin/transactions`

## 5) Frontend Feature Structure (Next.js)

`frontend/src`:

- `app/` (route segments + layouts)
- `features/auth/`
- `features/products/`
- `features/orders/`
- `features/admin/`
- `features/profile/`
- `components/` (shared UI)
- `services/` (axios client + API services)
- `hooks/` (shared hooks)
- `types/` (DTOs and domain types)
- `utils/` (helpers)

Layout plan:

- Public layout: landing/login/register/product browsing shell
- User layout: buyer/seller authenticated pages
- Admin dashboard layout: admin-only navigation

## 6) Enforcement of Village Restriction

Must be backend-first:

- On registration, user must pick one village.
- All product read queries include `where village_id = auth()->user()->village_id`.
- Product create/update force `village_id` from authenticated user, not request payload.
- Order checkout validates every product belongs to buyer village.
- Admin can view all villages or scoped by assigned village based on role design.

## 7) Incremental Scaffolding Plan

1. Install and configure Sanctum + API routes + auth flow skeleton.
2. Create module directories and base classes (controllers/services/resources/requests).
3. Build villages/users/categories/products/order migrations and models.
4. Build Product list/detail and basic CRUD with validation and API resources.
5. Build checkout endpoint and order management.
6. Generate Next.js app in `frontend/` with Tailwind and feature structure.
7. Integrate auth + product listing pages using Axios service layer.
8. Add admin dashboard skeleton and role-based route guards.
