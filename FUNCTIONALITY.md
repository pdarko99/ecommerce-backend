# E-Commerce Application - Functionality Overview

## 1. Authentication

### User Registration
- Users can create an account with email, password, first name, and last name
- Password confirmation required
- Email uniqueness validation
- Passwords are encrypted using BCrypt
- JWT token returned upon successful registration

### User Login
- Email and password authentication
- JWT token returned upon successful login
- Token contains user email as subject
- Configurable token expiration

---

## 2. User Management

### User Entity Fields
- ID, Email, Password, First Name, Last Name
- Avatar URL, Phone Number
- Admin flag (isAdmin)
- Created/Updated timestamps

---

## 3. Product Management

### Product Entity Fields
- ID, Title, Description
- Price, Quantity (stock)
- Product Image URL
- Category ID
- Created/Updated timestamps

### Product Operations (Admin)
- **Create Product**: Add new product with title, description, price, quantity, category, and image upload
- **Update Product**: Modify any product field including image replacement
- **Delete Product**: Remove product and associated image
- **Fetch Products**: Paginated list with search functionality
- **Get Product by ID**: Single product details

### Product Features
- Image upload and storage
- Search by title or description
- Pagination support
- Stock tracking
- Category association

---

## 4. Category Management

### Category Entity Fields
- ID, Name (unique), Description
- Created/Updated timestamps

### Category Operations (Admin)
- **Create Category**: Add new category with name and description
- **Update Category**: Modify category name or description
- **Delete Category**: Remove category (blocked if products exist)
- **List Categories**: Get all categories with product counts
- **Get Category by ID**: Single category details

---

## 5. Shopping & Purchases

### Purchase Flow
1. User authenticates with JWT token
2. User submits product(s) to purchase
3. System validates stock availability
4. System creates an Order record
5. System creates PurchasedProduct records linked to order
6. System decrements product stock
7. System returns purchase confirmation

### Single Product Purchase
- Purchase one product at a time
- Specify product ID and quantity
- Creates individual order

### Bulk Purchase
- Purchase multiple products in one transaction
- All-or-nothing transaction (if one fails, all rollback)
- Creates single order with multiple items
- Calculates total order amount

### Purchase Tracking
- Price at time of purchase stored (historical pricing)
- Order ID links related purchases
- Purchase timestamp recorded

### View Purchase History
- Users can view their purchased products
- Ordered by most recent first
- Includes product details and purchase date

---

## 6. Order Management

### Order Entity Fields
- ID, User ID
- Total Amount
- Status (PENDING, COMPLETED, CANCELLED)
- Created/Updated timestamps

### Order Features
- Groups multiple purchased items
- Tracks total transaction value
- Status tracking for order lifecycle

---

## 7. Admin Dashboard

### Overview Statistics
| Metric | Description |
|--------|-------------|
| Total Revenue | Sum of all completed orders |
| Today's Revenue | Revenue from today |
| Week Revenue | Revenue from last 7 days |
| Month Revenue | Revenue from last 30 days |
| Total Orders | Count of all orders |
| Today's Orders | Orders placed today |
| Week Orders | Orders from last 7 days |
| Month Orders | Orders from last 30 days |
| Total Users | Registered user count |
| New Users Today | Users registered today |
| New Users Week | Users registered in last 7 days |
| New Users Month | Users registered in last 30 days |
| Total Products | Product count in catalog |
| Low Stock Count | Products with quantity < 10 |
| Out of Stock Count | Products with quantity = 0 |
| Total Items Sold | Sum of all quantities sold |

### Top Selling Products
- Ranked by total quantity sold
- Includes product details (title, price, image)
- Shows total units sold
- Shows total revenue per product
- Includes category name
- Configurable limit (default 10)

### Category Statistics
- Sales breakdown by category
- Product count per category
- Total units sold per category
- Total revenue per category
- Sorted by best-selling categories

### Low Stock Alerts
- Products with stock below threshold (default: 10)
- Sorted by lowest stock first
- Includes product details and current stock
- Category information included

### Recent Orders
- Most recent orders with details
- Customer information (name, email)
- Order total and status
- Item count per order
- Individual items with quantities and prices
- Configurable limit (default 10)

---

## 8. File Storage

### Image Upload
- Supports product image uploads
- Multipart form data handling
- Stores images in configured upload directory
- Returns accessible URL for stored images

### Image Management
- Automatic cleanup on product update (old image deleted)
- Image deletion on product removal

---

## 9. Security Features

### Password Security
- BCrypt encryption with strength 12
- Password confirmation on registration

### JWT Authentication
- Token-based authentication
- Configurable secret key
- Configurable expiration time
- Token validation and parsing
- Subject extraction for user identification

---

## 10. API Response Format

All API responses follow a consistent format:

```json
{
  "status": "success" | "error",
  "message": "Description of result",
  "data": { ... } | null
}
```

---

## 11. Database Features

### Entities and Relationships
- **Users** - Independent entity
- **Categories** - Independent entity
- **Products** - References Category (categoryId)
- **Orders** - References User (userId)
- **PurchasedProducts** - References Order, Product, and User

### Indexing
- Email index on Users table
- Title index on Products table

### Timestamps
- All entities have createdAt and updatedAt
- Automatic timestamp management via Hibernate

---

## 12. Error Handling

### Validation Errors
- Email already exists
- Password mismatch
- Invalid credentials
- Product not found
- Category not found
- Insufficient stock

### Global Exception Handling
- Centralized error responses
- Consistent error format
- Appropriate HTTP status codes

---

## 13. API Endpoints Summary

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | User login |

### Products (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/products | List products (paginated) |
| GET | /api/admin/products/{id} | Get product by ID |
| POST | /api/admin/products | Create product |
| PUT | /api/admin/products | Update product |
| DELETE | /api/admin/products/{id} | Delete product |

### Categories (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/categories | List all categories |
| GET | /api/admin/categories/{id} | Get category by ID |
| POST | /api/admin/categories | Create category |
| PUT | /api/admin/categories/{id} | Update category |
| DELETE | /api/admin/categories/{id} | Delete category |

### Purchases (Authenticated Users)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/purchased-products | Get user's purchases |
| POST | /api/purchased-products | Purchase single product |
| POST | /api/purchased-products/bulk | Purchase multiple products |

### Dashboard (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/dashboard | Full dashboard data |
| GET | /api/admin/dashboard/overview | Overview statistics |
| GET | /api/admin/dashboard/top-products | Top selling products |
| GET | /api/admin/dashboard/category-stats | Category statistics |
| GET | /api/admin/dashboard/low-stock | Low stock alerts |
| GET | /api/admin/dashboard/recent-orders | Recent orders |
