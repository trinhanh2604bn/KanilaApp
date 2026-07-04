# Kanila Backend Models Report

## 1. Overview
This report provides a detailed catalog and analysis of all Mongoose models and MongoDB collections defined in the **Kanila App** backend.

### Project Details
- **Backend Stack**: Node.js, Express.js, MongoDB Atlas (Mongoose ODM)
- **Total Mongoose Models**: 69
- **Total MongoDB Collections**: 69
- **Collection Naming Convention**:
  - **Explicitly Named**: 29 models specify a custom `collection` option.
  - **Mongoose Inferred**: 40 models rely on Mongoose's default pluralization logic (e.g., `Product` -> `products`).

---

## 2. Model Summary Table
Below is a consolidated summary of all detected models, their file paths, collection names, module domains, and timestamp tracking capabilities.

| No. | Model Name | File Path | Collection Name | Module / Domain | Timestamps | Main Purpose |
|---|---|---|---|---|---|---|
| 1 | **Account** | [`models/account.model.js`](file:///d:/KanilaApp/backend/models/account.model.js) | `accounts` | Auth / Accounts | Custom | User credentials, authentication profiles, and account statuses. |
| 2 | **AccountAuthProvider** | [`models/accountAuthProvider.model.js`](file:///d:/KanilaApp/backend/models/accountAuthProvider.model.js) | `account_auth_providers` | Auth & Access Control | Custom | External oauth or auth providers linked to accounts. |
| 3 | **AccountRole** | [`models/accountRole.model.js`](file:///d:/KanilaApp/backend/models/accountRole.model.js) | `account_roles` | Auth & Access Control | No | Intersection model mapping accounts to roles. |
| 4 | **Address** | [`models/address.model.js`](file:///d:/KanilaApp/backend/models/address.model.js) | `customer_addresses` | Users & Customers | Custom | Customer shipping and billing address records. |
| 5 | **AdminProfile** | [`models/adminProfile.model.js`](file:///d:/KanilaApp/backend/models/adminProfile.model.js) | `admin_profiles` | Users & Customers | Custom | Profile metadata for administrative accounts. |
| 6 | **AuditLog** | [`models/auditLog.model.js`](file:///d:/KanilaApp/backend/models/auditLog.model.js) | `audit_logs` | Support & Logs | No | Action logs for security audit trails. |
| 7 | **Brand** | [`models/brand.model.js`](file:///d:/KanilaApp/backend/models/brand.model.js) | `brands` | Products & Categories | Yes | Manufacturer/brand metadata for product categorization. |
| 8 | **Cart** | [`models/cart.model.js`](file:///d:/KanilaApp/backend/models/cart.model.js) | `carts` | Cart | Custom | Cart metadata, totals, and owner information. |
| 9 | **CartItem** | [`models/cartItem.model.js`](file:///d:/KanilaApp/backend/models/cartItem.model.js) | `cart_items` | Cart | Custom | Items stored inside customer and guest carts. |
| 10 | **Category** | [`models/category.model.js`](file:///d:/KanilaApp/backend/models/category.model.js) | `categories` | Products & Categories | Yes | Hierarchy nodes for product categorization tree. |
| 11 | **CheckoutAddress** | [`models/checkoutAddress.model.js`](file:///d:/KanilaApp/backend/models/checkoutAddress.model.js) | `checkout_addresses` | Checkout | Custom | Shipping/billing address selected for checkout. |
| 12 | **CheckoutSession** | [`models/checkoutSession.model.js`](file:///d:/KanilaApp/backend/models/checkoutSession.model.js) | `checkout_sessions` | Checkout | Custom | State of checkout sessions, shipping, and billing options. |
| 13 | **CheckoutShippingMethod** | [`models/checkoutShippingMethod.model.js`](file:///d:/KanilaApp/backend/models/checkoutShippingMethod.model.js) | `checkout_shipping_methods` | Checkout | No | Selected shipping rate and details for checkout. |
| 14 | **Coupon** | [`models/coupon.model.js`](file:///d:/KanilaApp/backend/models/coupon.model.js) | `coupons` | Vouchers & Promotions | Yes | Voucher definitions, constraints, and discount values. |
| 15 | **CouponRedemption** | [`models/couponRedemption.model.js`](file:///d:/KanilaApp/backend/models/couponRedemption.model.js) | `couponredemptions` | Vouchers & Promotions | Yes | Record of customer coupon usages. |
| 16 | **Customer** | [`models/customer.model.js`](file:///d:/KanilaApp/backend/models/customer.model.js) | `customer_profiles` | Users & Customers | Custom | Customer profile details linked to accounts. |
| 17 | **CustomerConsent** | [`models/customerConsent.model.js`](file:///d:/KanilaApp/backend/models/customerConsent.model.js) | `customer_consents` | Users & Customers | No | Tracking of terms and privacy policy consents. |
| 18 | **CustomerCoupon** | [`models/customerCoupon.model.js`](file:///d:/KanilaApp/backend/models/customerCoupon.model.js) | `customer_coupons` | Users & Customers | Yes | Coupons claimed by or assigned to specific customers. |
| 19 | **CustomerPreference** | [`models/customerPreference.model.js`](file:///d:/KanilaApp/backend/models/customerPreference.model.js) | `customer_preferences` | Users & Customers | No | Saved preferences for skincare, tone, and skin type. |
| 20 | **CustomerRecommendationSnapshot** | [`models/customerRecommendationSnapshot.model.js`](file:///d:/KanilaApp/backend/models/customerRecommendationSnapshot.model.js) | `customer_recommendation_snapshots` | Users & Customers | Yes | Skincare product recommendations snapshot. |
| 21 | **GuestSession** | [`models/guestSession.model.js`](file:///d:/KanilaApp/backend/models/guestSession.model.js) | `guest_sessions` | Cart | Custom | Session identifiers for unauthenticated visitors. |
| 22 | **InventoryBalance** | [`models/inventoryBalance.model.js`](file:///d:/KanilaApp/backend/models/inventoryBalance.model.js) | `inventorybalances` | Inventory & Warehousing | Yes | Physical stock levels across warehouses. |
| 23 | **InventoryTransaction** | [`models/inventoryTransaction.model.js`](file:///d:/KanilaApp/backend/models/inventoryTransaction.model.js) | `inventorytransactions` | Inventory & Warehousing | Yes | History of stock updates, adjustments, and receipts. |
| 24 | **LoyaltyAccount** | [`models/loyaltyAccount.model.js`](file:///d:/KanilaApp/backend/models/loyaltyAccount.model.js) | `loyaltyaccounts` | Loyalty Program | Yes | Loyalty points balances, history, and status tiers. |
| 25 | **LoyaltyPointLedger** | [`models/loyaltyPointLedger.model.js`](file:///d:/KanilaApp/backend/models/loyaltyPointLedger.model.js) | `loyaltypointledgers` | Loyalty Program | Yes | Point accrual and redemption transaction history. |
| 26 | **LoyaltyTier** | [`models/loyaltyTier.model.js`](file:///d:/KanilaApp/backend/models/loyaltyTier.model.js) | `loyaltytiers` | Loyalty Program | Yes | Definition of rewards tiers and thresholds. |
| 27 | **Order** | [`models/order.model.js`](file:///d:/KanilaApp/backend/models/order.model.js) | `orders` | Orders & Shipments | Custom | Purchase orders containing order status and payment information. |
| 28 | **OrderAddress** | [`models/orderAddress.model.js`](file:///d:/KanilaApp/backend/models/orderAddress.model.js) | `order_addresses` | Orders & Shipments | No | Snapshot of shipping/billing address used for an order. |
| 29 | **OrderItem** | [`models/orderItem.model.js`](file:///d:/KanilaApp/backend/models/orderItem.model.js) | `order_items` | Orders & Shipments | Custom | Snapshot of items purchased in an order. |
| 30 | **OrderStatusHistory** | [`models/orderStatusHistory.model.js`](file:///d:/KanilaApp/backend/models/orderStatusHistory.model.js) | `order_status_history` | Orders & Shipments | No | Track status progression transitions of an order. |
| 31 | **OrderTotal** | [`models/orderTotal.model.js`](file:///d:/KanilaApp/backend/models/orderTotal.model.js) | `order_totals` | Orders & Shipments | Custom | Breakdown of order tax, shipping, and discount amounts. |
| 32 | **PasswordResetOtp** | [`models/passwordResetOtp.model.js`](file:///d:/KanilaApp/backend/models/passwordResetOtp.model.js) | `password_reset_otps` | Auth & Access Control | Yes | OTP codes for password recovery. |
| 33 | **PaymentIntent** | [`models/paymentIntent.model.js`](file:///d:/KanilaApp/backend/models/paymentIntent.model.js) | `paymentintents` | Payments & Returns | Yes | Stripe or payment gateway payment intents. |
| 34 | **PaymentMethod** | [`models/paymentMethod.model.js`](file:///d:/KanilaApp/backend/models/paymentMethod.model.js) | `payment_methods` | Payments & Returns | Custom | Customer's saved card or payment credentials. |
| 35 | **PaymentTransaction** | [`models/paymentTransaction.model.js`](file:///d:/KanilaApp/backend/models/paymentTransaction.model.js) | `paymenttransactions` | Payments & Returns | Yes | Record of actual payment gateway transactions. |
| 36 | **Permission** | [`models/permission.model.js`](file:///d:/KanilaApp/backend/models/permission.model.js) | `permissions` | Auth & Access Control | No | Capabilities and access controls available in system. |
| 37 | **PriceBook** | [`models/priceBook.model.js`](file:///d:/KanilaApp/backend/models/priceBook.model.js) | `pricebooks` | Price Management | Yes | Price lists defining customer-specific pricing catalogs. |
| 38 | **PriceBookEntry** | [`models/priceBookEntry.model.js`](file:///d:/KanilaApp/backend/models/priceBookEntry.model.js) | `pricebookentries` | Price Management | Yes | Specific price entries matching products to price books. |
| 39 | **PriceHistory** | [`models/priceHistory.model.js`](file:///d:/KanilaApp/backend/models/priceHistory.model.js) | `pricehistories` | Price Management | Yes | Historic price trends for audit and price tracking. |
| 40 | **Product** | [`models/product.model.js`](file:///d:/KanilaApp/backend/models/product.model.js) | `products` | Products & Categories | Yes | Skincare/makeup product main listing and details. |
| 41 | **ProductAttribute** | [`models/productAttribute.model.js`](file:///d:/KanilaApp/backend/models/productAttribute.model.js) | `productattributes` | Products & Categories | Yes | Key-value properties for product categorization. |
| 42 | **ProductCategory** | [`models/productCategory.model.js`](file:///d:/KanilaApp/backend/models/productCategory.model.js) | `productcategories` | General | Yes | Joint mapping between products and categories. |
| 43 | **ProductMedia** | [`models/productMedia.model.js`](file:///d:/KanilaApp/backend/models/productMedia.model.js) | `productmedias` | Products & Categories | Yes | Images and videos uploaded for product listings. |
| 44 | **ProductOption** | [`models/productOption.model.js`](file:///d:/KanilaApp/backend/models/productOption.model.js) | `productoptions` | Products & Categories | Yes | Product attributes configuration (e.g., Color, Size). |
| 45 | **ProductOptionValue** | [`models/productOptionValue.model.js`](file:///d:/KanilaApp/backend/models/productOptionValue.model.js) | `productoptionvalues` | Products & Categories | Yes | Permitted choices for product options. |
| 46 | **ProductVariant** | [`models/productVariant.model.js`](file:///d:/KanilaApp/backend/models/productVariant.model.js) | `productvariants` | Products & Categories | Yes | Specific SKU configurations of a product. |
| 47 | **Promotion** | [`models/promotion.model.js`](file:///d:/KanilaApp/backend/models/promotion.model.js) | `promotions` | Vouchers & Promotions | Yes | General marketing discounts and campaigns. |
| 48 | **PromotionRule** | [`models/promotionRule.model.js`](file:///d:/KanilaApp/backend/models/promotionRule.model.js) | `promotionrules` | Vouchers & Promotions | Yes | Condition trees qualifying orders for promotions. |
| 49 | **PromotionTarget** | [`models/promotionTarget.model.js`](file:///d:/KanilaApp/backend/models/promotionTarget.model.js) | `promotiontargets` | Vouchers & Promotions | Yes | Items, brands, or cart totals receiving promotions. |
| 50 | **RecommendationLog** | [`models/recommendation-log.model.js`](file:///d:/KanilaApp/backend/models/recommendation-log.model.js) | `recommendation_logs` | Support & Logs | Yes | Logs of AI skincare match queries and results. |
| 51 | **Refund** | [`models/refund.model.js`](file:///d:/KanilaApp/backend/models/refund.model.js) | `refunds` | Payments & Returns | Yes | Records of customer refunds issued for returns. |
| 52 | **Return** | [`models/return.model.js`](file:///d:/KanilaApp/backend/models/return.model.js) | `returns` | Payments & Returns | Yes | Customer product return requests and progress. |
| 53 | **ReturnItem** | [`models/returnItem.model.js`](file:///d:/KanilaApp/backend/models/returnItem.model.js) | `returnitems` | Payments & Returns | Yes | Individual items returned and their condition. |
| 54 | **Review** | [`models/review.model.js`](file:///d:/KanilaApp/backend/models/review.model.js) | `reviews` | Reviews & Ratings | Yes | Customer feedback, ratings, and text reviews. |
| 55 | **ReviewMedia** | [`models/reviewMedia.model.js`](file:///d:/KanilaApp/backend/models/reviewMedia.model.js) | `reviewmedias` | Products & Categories | Yes | Media files (photos/videos) attached to reviews. |
| 56 | **ReviewSummary** | [`models/reviewSummary.model.js`](file:///d:/KanilaApp/backend/models/reviewSummary.model.js) | `reviewsummaries` | Reviews & Ratings | Yes | Aggregated rating stats for product catalog views. |
| 57 | **ReviewVote** | [`models/reviewVote.model.js`](file:///d:/KanilaApp/backend/models/reviewVote.model.js) | `reviewvotes` | Reviews & Ratings | Yes | Helpful votes received by reviews. |
| 58 | **Role** | [`models/role.model.js`](file:///d:/KanilaApp/backend/models/role.model.js) | `roles` | Auth & Access Control | Custom | Standardized groups of permissions. |
| 59 | **RolePermission** | [`models/rolePermission.model.js`](file:///d:/KanilaApp/backend/models/rolePermission.model.js) | `role_permissions` | Auth & Access Control | No | Intersection mapping between roles and permissions. |
| 60 | **Shipment** | [`models/shipment.model.js`](file:///d:/KanilaApp/backend/models/shipment.model.js) | `shipments` | Orders & Shipments | Yes | Delivery shipments dispatched for orders. |
| 61 | **ShipmentEvent** | [`models/shipmentEvent.model.js`](file:///d:/KanilaApp/backend/models/shipmentEvent.model.js) | `shipmentevents` | Orders & Shipments | Yes | Delivery tracking history and status updates. |
| 62 | **ShipmentItem** | [`models/shipmentItem.model.js`](file:///d:/KanilaApp/backend/models/shipmentItem.model.js) | `shipmentitems` | Orders & Shipments | Yes | Items included in a specific shipment packet. |
| 63 | **ShippingMethod** | [`models/shippingMethod.model.js`](file:///d:/KanilaApp/backend/models/shippingMethod.model.js) | `shipping_methods` | General | Custom | Shipping options and base rates configured for checkout. |
| 64 | **StockReservation** | [`models/stockReservation.model.js`](file:///d:/KanilaApp/backend/models/stockReservation.model.js) | `stockreservations` | Inventory & Warehousing | Yes | Temporary stock locks for items in active checkouts. |
| 65 | **VariantMedia** | [`models/variantMedia.model.js`](file:///d:/KanilaApp/backend/models/variantMedia.model.js) | `variantmedias` | Products & Categories | Yes | Media files associated with specific product variants. |
| 66 | **VariantOptionValue** | [`models/variantOptionValue.model.js`](file:///d:/KanilaApp/backend/models/variantOptionValue.model.js) | `variantoptionvalues` | Products & Categories | Yes | Joint mapping linking variants to option choices. |
| 67 | **Warehouse** | [`models/warehouse.model.js`](file:///d:/KanilaApp/backend/models/warehouse.model.js) | `warehouses` | Inventory & Warehousing | Yes | Storage facilities housing inventory. |
| 68 | **Wishlist** | [`models/wishlist.model.js`](file:///d:/KanilaApp/backend/models/wishlist.model.js) | `wishlists` | Cart | Yes | Customer's wishlist containing saved items. |
| 69 | **WishlistItem** | [`models/wishlistItem.model.js`](file:///d:/KanilaApp/backend/models/wishlistItem.model.js) | `wishlistitems` | Cart | Yes | Individual items added to a customer's wishlist. |

---

## 3. Detailed Model Documentation
This section details the schema definition, field options, constraints, relationships, indexes, and hooks for each model.

---

### 3.1 Account

#### Basic Information
- **Model name**: Account
- **File path**: [`models/account.model.js`](file:///d:/KanilaApp/backend/models/account.model.js)
- **Collection name**: `accounts`
- **Module/domain**: Auth / Accounts
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| account_type | `String` | Yes | No | `customer` | `["customer","admin","staff"]` | - | No | - | Field mapping for account_type |
| email | `String` | Yes | Yes | `-` | - | - | No | `trim, lowercase` | Field mapping for email |
| phone | `String` | No | No | `` | - | - | No | `trim` | Field mapping for phone |
| username | `String` | No | Yes | `-` | - | - | No | `trim, sparse` | Field mapping for username |
| password_hash | `String` | Yes | No | `-` | - | - | No | - | Field mapping for password_hash |
| account_status | `String` | No | No | `active` | `["active","inactive","locked"]` | - | No | - | Field mapping for account_status |
| email_verified_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for email_verified_at |
| phone_verified_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for phone_verified_at |
| last_login_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for last_login_at |
| failed_login_count | `Number` | No | No | `0` | - | - | No | - | Field mapping for failed_login_count |
| locked_until | `Date` | No | No | `null` | - | - | No | - | Field mapping for locked_until |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ email: 1 }` | Unique, Single-field | `-` |
| `{ username: 1 }` | Unique, Sparse, Single-field | `-` |
| `{ email: 1 }` | Unique, Single-field | `-` |
| `{ username: 1 }` | Unique, Sparse, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `account_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Sensitive Field: `password_hash` contains credentials or secrets. Ensure this is excluded from API responses (e.g. via `select: false` or manual sanitization).
- **[Risk/Note]** Schema Warning: Mongoose warns of duplicate index on `email` and `username`. This is due to declaring indices both in-line (e.g. `unique: true`) and via `schema.index()`. Recommend cleaning up to avoid redundant indices.

---

### 3.2 AccountAuthProvider

#### Basic Information
- **Model name**: AccountAuthProvider
- **File path**: [`models/accountAuthProvider.model.js`](file:///d:/KanilaApp/backend/models/accountAuthProvider.model.js)
- **Collection name**: `account_auth_providers`
- **Module/domain**: Auth & Access Control
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":false},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| account_id | `ObjectId` | Yes | No | `-` | - | **Account** | Yes | - | Field mapping for account_id |
| provider_code | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for provider_code |
| provider_subject | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for provider_subject |
| provider_email | `String` | No | No | `` | - | - | No | `trim, lowercase` | Field mapping for provider_email |
| linked_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for linked_at |
| last_used_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for last_used_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ account_id: 1 }` | Single-field | `-` |
| `{ account_id: 1, provider_code: 1 }` | Unique, Compound | `-` |

#### Relationships
- `account_id` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.3 AccountRole

#### Basic Information
- **Model name**: AccountRole
- **File path**: [`models/accountRole.model.js`](file:///d:/KanilaApp/backend/models/accountRole.model.js)
- **Collection name**: `account_roles`
- **Module/domain**: Auth & Access Control
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| account_id | `ObjectId` | Yes | No | `-` | - | **Account** | Yes | - | Field mapping for account_id |
| role_id | `ObjectId` | Yes | No | `-` | - | **Role** | Yes | - | Field mapping for role_id |
| assigned_by_account_id | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for assigned_by_account_id |
| assigned_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for assigned_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ account_id: 1 }` | Single-field | `-` |
| `{ role_id: 1 }` | Single-field | `-` |
| `{ account_id: 1, role_id: 1 }` | Unique, Compound | `-` |

#### Relationships
- `account_id` → **Account** (Many-to-One): Identifies the related Account model.
- `role_id` → **Role** (Many-to-One): Identifies the related Role model.
- `assigned_by_account_id` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `assigned_by_account_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.4 Address

#### Basic Information
- **Model name**: Address
- **File path**: [`models/address.model.js`](file:///d:/KanilaApp/backend/models/address.model.js)
- **Collection name**: `customer_addresses`
- **Module/domain**: Users & Customers
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| address_label | `String` | No | No | `` | - | - | No | `trim` | Field mapping for address_label |
| recipient_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for recipient_name |
| phone | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for phone |
| address_line_1 | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for address_line_1 |
| address_line_2 | `String` | No | No | `` | - | - | No | `trim` | Field mapping for address_line_2 |
| ward | `String` | No | No | `` | - | - | No | `trim` | Field mapping for ward |
| district | `String` | No | No | `` | - | - | No | `trim` | Field mapping for district |
| city | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for city |
| country_code | `String` | No | No | `VN` | - | - | No | `trim` | Field mapping for country_code |
| postal_code | `String` | No | No | `` | - | - | No | `trim` | Field mapping for postal_code |
| address_type | `String` | No | No | `home` | `["home","office","other"]` | - | No | `trim` | Field mapping for address_type |
| address_note | `String` | No | No | `` | - | - | No | `trim` | Field mapping for address_note |
| is_default_shipping | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for is_default_shipping |
| is_default_billing | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for is_default_billing |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ customer_id: 1 }` | Single-field | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `address_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.5 AdminProfile

#### Basic Information
- **Model name**: AdminProfile
- **File path**: [`models/adminProfile.model.js`](file:///d:/KanilaApp/backend/models/adminProfile.model.js)
- **Collection name**: `admin_profiles`
- **Module/domain**: Users & Customers
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| account_id | `ObjectId` | Yes | Yes | `-` | - | **Account** | Yes | - | Field mapping for account_id |
| employee_code | `String` | No | No | `` | - | - | No | `trim` | Field mapping for employee_code |
| full_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for full_name |
| department | `String` | No | No | `` | - | - | No | `trim` | Field mapping for department |
| job_title | `String` | No | No | `` | - | - | No | `trim` | Field mapping for job_title |
| manager_account_id | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for manager_account_id |
| employment_status | `String` | No | No | `active` | `["active","inactive","terminated","leave"]` | - | No | - | Field mapping for employment_status |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ account_id: 1 }` | Unique, Single-field | `-` |

#### Relationships
- `account_id` → **Account** (One-to-One): Identifies the related Account model.
- `manager_account_id` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `manager_account_id` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.6 AuditLog

#### Basic Information
- **Model name**: AuditLog
- **File path**: [`models/auditLog.model.js`](file:///d:/KanilaApp/backend/models/auditLog.model.js)
- **Collection name**: `audit_logs`
- **Module/domain**: Support & Logs
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| actor_account_id | `ObjectId` | No | No | `null` | - | **Account** | Yes | - | Field mapping for actor_account_id |
| action_code | `String` | Yes | No | `-` | - | - | Yes | `trim` | Field mapping for action_code |
| entity_name | `String` | Yes | No | `-` | - | - | Yes | `trim` | Field mapping for entity_name |
| entity_id | `Mixed` | No | No | `null` | - | - | No | - | Field mapping for entity_id |
| old_values_json | `Mixed` | No | No | `null` | - | - | No | - | Field mapping for old_values_json |
| new_values_json | `Mixed` | No | No | `null` | - | - | No | - | Field mapping for new_values_json |
| ip_address | `String` | No | No | `` | - | - | No | - | Field mapping for ip_address |
| user_agent | `String` | No | No | `` | - | - | No | - | Field mapping for user_agent |
| created_at | `Date` | No | No | `[Function: now]` | - | - | Yes | - | Field mapping for created_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ actor_account_id: 1 }` | Single-field | `-` |
| `{ action_code: 1 }` | Single-field | `-` |
| `{ entity_name: 1 }` | Single-field | `-` |
| `{ created_at: 1 }` | Single-field | `-` |

#### Relationships
- `actor_account_id` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.7 Brand

#### Basic Information
- **Model name**: Brand
- **File path**: [`models/brand.model.js`](file:///d:/KanilaApp/backend/models/brand.model.js)
- **Collection name**: `brands`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| brandName | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for brandName |
| brandCode | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for brandCode |
| description | `String` | No | No | `` | - | - | No | - | Field mapping for description |
| logoUrl | `String` | No | No | `` | - | - | No | - | Field mapping for logoUrl |
| brandStatus | `String` | No | No | `active` | `["active","inactive","draft"]` | - | No | - | Catalog lifecycle (aligns with brand_status in relational designs). |
| isActive | `Boolean` | No | No | `true` | - | - | No | - | Field mapping for isActive |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ brandCode: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.8 Cart

#### Basic Information
- **Model name**: Cart
- **File path**: [`models/cart.model.js`](file:///d:/KanilaApp/backend/models/cart.model.js)
- **Collection name**: `carts`
- **Module/domain**: Cart
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| owner_type | `String` | No | No | `customer` | `["customer","guest"]` | - | Yes | - | Field mapping for owner_type |
| customer_id | `ObjectId` | No | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| guest_session_id | `String` | No | No | `null` | - | - | Yes | `trim` | Field mapping for guest_session_id |
| cart_status | `String` | No | No | `active` | `["active","converted","expired","merged"]` | - | No | - | Field mapping for cart_status |
| currency_code | `String` | No | No | `VND` | - | - | No | `trim` | Field mapping for currency_code |
| item_count | `Number` | No | No | `0` | - | - | No | - | Field mapping for item_count |
| subtotal_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for subtotal_amount |
| discount_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for discount_amount |
| total_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for total_amount |
| expires_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for expires_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ owner_type: 1 }` | Single-field | `-` |
| `{ customer_id: 1 }` | Single-field | `-` |
| `{ guest_session_id: 1 }` | Single-field | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `cart_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.9 CartItem

#### Basic Information
- **Model name**: CartItem
- **File path**: [`models/cartItem.model.js`](file:///d:/KanilaApp/backend/models/cartItem.model.js)
- **Collection name**: `cart_items`
- **Module/domain**: Cart
- **Schema options**: `{"timestamps":{"createdAt":false,"updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| line_key | `String` | Yes | No | `-` | - | - | Yes | `trim` | Field mapping for line_key |
| product_id | `ObjectId` | No | No | `null` | - | **Product** | Yes | - | Field mapping for product_id |
| cart_id | `ObjectId` | Yes | No | `-` | - | **Cart** | Yes | - | Field mapping for cart_id |
| variant_id | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variant_id |
| sku_snapshot | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for sku_snapshot |
| product_name_snapshot | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for product_name_snapshot |
| variant_name_snapshot | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for variant_name_snapshot |
| brand_name_snapshot | `String` | No | No | `` | - | - | No | `trim` | Field mapping for brand_name_snapshot |
| image_url_snapshot | `String` | No | No | `` | - | - | No | `trim` | Field mapping for image_url_snapshot |
| compare_at_price_amount | `Number` | No | No | `0` | - | - | No | `min: 0` | Field mapping for compare_at_price_amount |
| stock_status | `String` | No | No | `in_stock` | - | - | No | `trim` | Field mapping for stock_status |
| quantity | `Number` | Yes | No | `-` | - | - | No | `min: 1` | Field mapping for quantity |
| selected | `Boolean` | No | No | `true` | - | - | No | - | Field mapping for selected |
| unit_price_amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for unit_price_amount |
| discount_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for discount_amount |
| final_unit_price_amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for final_unit_price_amount |
| line_total_amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for line_total_amount |
| added_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for added_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ line_key: 1 }` | Single-field | `-` |
| `{ product_id: 1 }` | Single-field | `-` |
| `{ cart_id: 1 }` | Single-field | `-` |
| `{ cart_id: 1, line_key: 1 }` | Unique, Compound | `ux_cart_line_key` |

#### Relationships
- `product_id` → **Product** (Many-to-One): Identifies the related Product model.
- `cart_id` → **Cart** (Many-to-One): Identifies the related Cart model.
- `variant_id` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `cart_item_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `variant_id` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.10 Category

#### Basic Information
- **Model name**: Category
- **File path**: [`models/category.model.js`](file:///d:/KanilaApp/backend/models/category.model.js)
- **Collection name**: `categories`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| categoryName | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for categoryName |
| categoryCode | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for categoryCode |
| description | `String` | No | No | `` | - | - | No | - | Field mapping for description |
| parentCategoryId | `ObjectId` | No | No | `null` | - | **Category** | No | - | Field mapping for parentCategoryId |
| displayOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for displayOrder |
| categoryStatus | `String` | No | No | `active` | `["active","inactive","draft"]` | - | No | - | Catalog visibility (aligns with category_status in relational designs). |
| isActive | `Boolean` | No | No | `true` | - | - | No | - | Field mapping for isActive |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ categoryCode: 1 }` | Unique, Single-field | `-` |

#### Relationships
- `parentCategoryId` → **Category** (Many-to-One): Identifies the related Category model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `parentCategoryId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.11 CheckoutAddress

#### Basic Information
- **Model name**: CheckoutAddress
- **File path**: [`models/checkoutAddress.model.js`](file:///d:/KanilaApp/backend/models/checkoutAddress.model.js)
- **Collection name**: `checkout_addresses`
- **Module/domain**: Checkout
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| checkout_session_id | `ObjectId` | Yes | No | `-` | - | **CheckoutSession** | Yes | - | Field mapping for checkout_session_id |
| address_type | `String` | Yes | No | `-` | `["shipping","billing"]` | - | No | - | Field mapping for address_type |
| recipient_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for recipient_name |
| phone | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for phone |
| address_line_1 | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for address_line_1 |
| address_line_2 | `String` | No | No | `` | - | - | No | `trim` | Field mapping for address_line_2 |
| ward | `String` | No | No | `` | - | - | No | `trim` | Field mapping for ward |
| district | `String` | No | No | `` | - | - | No | `trim` | Field mapping for district |
| city | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for city |
| country_code | `String` | No | No | `VN` | - | - | No | `trim` | Field mapping for country_code |
| postal_code | `String` | No | No | `` | - | - | No | `trim` | Field mapping for postal_code |
| is_selected | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for is_selected |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ checkout_session_id: 1 }` | Single-field | `-` |

#### Relationships
- `checkout_session_id` → **CheckoutSession** (Many-to-One): Identifies the related CheckoutSession model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `checkout_address_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.12 CheckoutSession

#### Basic Information
- **Model name**: CheckoutSession
- **File path**: [`models/checkoutSession.model.js`](file:///d:/KanilaApp/backend/models/checkoutSession.model.js)
- **Collection name**: `checkout_sessions`
- **Module/domain**: Checkout
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| owner_type | `String` | No | No | `customer` | `["customer","guest"]` | - | Yes | - | Field mapping for owner_type |
| guest_session_id | `String` | No | No | `null` | - | - | Yes | `trim` | Field mapping for guest_session_id |
| cart_id | `ObjectId` | Yes | No | `-` | - | **Cart** | Yes | - | Field mapping for cart_id |
| customer_id | `ObjectId` | No | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| guest_email | `String` | No | No | `` | - | - | No | `trim` | Field mapping for guest_email |
| guest_phone | `String` | No | No | `` | - | - | No | `trim` | Field mapping for guest_phone |
| guest_full_name | `String` | No | No | `` | - | - | No | `trim` | Field mapping for guest_full_name |
| checkout_status | `String` | No | No | `in_progress` | `["in_progress","completed","expired"]` | - | No | - | Field mapping for checkout_status |
| currency_code | `String` | No | No | `VND` | - | - | No | `trim` | Field mapping for currency_code |
| selected_shipping_address_id | `ObjectId` | No | No | `null` | - | **CheckoutAddress** | No | - | Field mapping for selected_shipping_address_id |
| selected_billing_address_id | `ObjectId` | No | No | `null` | - | **CheckoutAddress** | No | - | Field mapping for selected_billing_address_id |
| selected_shipping_method_id | `ObjectId` | No | No | `null` | - | **CheckoutShippingMethod** | No | - | Field mapping for selected_shipping_method_id |
| selected_payment_method_id | `ObjectId` | No | No | `null` | - | **PaymentMethod** | No | - | Field mapping for selected_payment_method_id |
| subtotal_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for subtotal_amount |
| shipping_fee_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for shipping_fee_amount |
| discount_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for discount_amount |
| applied_coupon_id | `ObjectId` | No | No | `null` | - | **Coupon** | No | - | Field mapping for applied_coupon_id |
| applied_coupon_code | `String` | No | No | `` | - | - | No | `trim, uppercase` | Field mapping for applied_coupon_code |
| coupon_discount_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for coupon_discount_amount |
| tax_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for tax_amount |
| total_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for total_amount |
| expires_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for expires_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ owner_type: 1 }` | Single-field | `-` |
| `{ guest_session_id: 1 }` | Single-field | `-` |
| `{ cart_id: 1 }` | Single-field | `-` |
| `{ customer_id: 1 }` | Single-field | `-` |

#### Relationships
- `cart_id` → **Cart** (Many-to-One): Identifies the related Cart model.
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `selected_shipping_address_id` → **CheckoutAddress** (Many-to-One): Identifies the related CheckoutAddress model.
- `selected_billing_address_id` → **CheckoutAddress** (Many-to-One): Identifies the related CheckoutAddress model.
- `selected_shipping_method_id` → **CheckoutShippingMethod** (Many-to-One): Identifies the related CheckoutShippingMethod model.
- `selected_payment_method_id` → **PaymentMethod** (Many-to-One): Identifies the related PaymentMethod model.
- `applied_coupon_id` → **Coupon** (Many-to-One): Identifies the related Coupon model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `checkout_session_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `selected_shipping_address_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `selected_billing_address_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `selected_shipping_method_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `selected_payment_method_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `applied_coupon_id` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.13 CheckoutShippingMethod

#### Basic Information
- **Model name**: CheckoutShippingMethod
- **File path**: [`models/checkoutShippingMethod.model.js`](file:///d:/KanilaApp/backend/models/checkoutShippingMethod.model.js)
- **Collection name**: `checkout_shipping_methods`
- **Module/domain**: Checkout
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| checkout_session_id | `ObjectId` | Yes | No | `-` | - | **CheckoutSession** | Yes | - | Field mapping for checkout_session_id |
| shipping_method_id | `ObjectId` | Yes | No | `-` | - | **ShippingMethod** | No | - | Field mapping for shipping_method_id |
| shipping_method_code | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for shipping_method_code |
| carrier_code | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for carrier_code |
| service_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for service_name |
| estimated_days_min | `Number` | No | No | `0` | - | - | No | - | Field mapping for estimated_days_min |
| estimated_days_max | `Number` | No | No | `0` | - | - | No | - | Field mapping for estimated_days_max |
| shipping_fee_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for shipping_fee_amount |
| currency_code | `String` | No | No | `VND` | - | - | No | `trim` | Field mapping for currency_code |
| is_selected | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for is_selected |
| created_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for created_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ checkout_session_id: 1 }` | Single-field | `-` |

#### Relationships
- `checkout_session_id` → **CheckoutSession** (Many-to-One): Identifies the related CheckoutSession model.
- `shipping_method_id` → **ShippingMethod** (Many-to-One): Identifies the related ShippingMethod model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `checkout_shipping_method_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `shipping_method_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.14 Coupon

#### Basic Information
- **Model name**: Coupon
- **File path**: [`models/coupon.model.js`](file:///d:/KanilaApp/backend/models/coupon.model.js)
- **Collection name**: `coupons`
- **Module/domain**: Vouchers & Promotions
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| promotionId | `ObjectId` | Yes | No | `-` | - | **Promotion** | No | - | Field mapping for promotionId |
| couponCode | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for couponCode |
| validFrom | `Date` | No | No | `null` | - | - | No | - | Field mapping for validFrom |
| validTo | `Date` | No | No | `null` | - | - | No | - | Field mapping for validTo |
| usageLimitTotal | `Number` | No | No | `0` | - | - | No | - | Field mapping for usageLimitTotal |
| usageLimitPerCustomer | `Number` | No | No | `0` | - | - | No | - | Field mapping for usageLimitPerCustomer |
| minOrderAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for minOrderAmount |
| couponStatus | `String` | No | No | `active` | `["active","inactive"]` | - | No | - | Field mapping for couponStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ couponCode: 1 }` | Unique, Single-field | `-` |

#### Relationships
- `promotionId` → **Promotion** (Many-to-One): Identifies the related Promotion model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `promotionId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.15 CouponRedemption

#### Basic Information
- **Model name**: CouponRedemption
- **File path**: [`models/couponRedemption.model.js`](file:///d:/KanilaApp/backend/models/couponRedemption.model.js)
- **Collection name**: `couponredemptions`
- **Module/domain**: Vouchers & Promotions
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| couponId | `ObjectId` | Yes | No | `-` | - | **Coupon** | No | - | Field mapping for couponId |
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | No | - | Field mapping for customer_id |
| order_id | `ObjectId` | No | No | `null` | - | **Order** | No | - | Field mapping for order_id |
| discountAmount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for discountAmount |
| redeemedAt | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for redeemedAt |
| redemptionStatus | `String` | No | No | `used` | `["used","cancelled"]` | - | No | - | Field mapping for redemptionStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `couponId` → **Coupon** (Many-to-One): Identifies the related Coupon model.
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `couponId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `customer_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `couponId`) and snake_case (e.g. `customer_id`) field casing.

---

### 3.16 Customer

#### Basic Information
- **Model name**: Customer
- **File path**: [`models/customer.model.js`](file:///d:/KanilaApp/backend/models/customer.model.js)
- **Collection name**: `customer_profiles`
- **Module/domain**: Users & Customers
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| account_id | `ObjectId` | Yes | Yes | `-` | - | **Account** | No | - | Field mapping for account_id |
| customer_code | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for customer_code |
| first_name | `String` | No | No | `` | - | - | No | `trim` | Field mapping for first_name |
| last_name | `String` | No | No | `` | - | - | No | `trim` | Field mapping for last_name |
| full_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for full_name |
| date_of_birth | `Date` | No | No | `null` | - | - | No | - | Field mapping for date_of_birth |
| gender | `String` | No | No | `` | - | - | No | `trim` | Field mapping for gender |
| avatar_url | `String` | No | No | `` | - | - | No | `trim` | Field mapping for avatar_url |
| customer_status | `String` | No | No | `active` | `["active","inactive"]` | - | No | - | Field mapping for customer_status |
| registered_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for registered_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ account_id: 1 }` | Unique, Single-field | `-` |
| `{ customer_code: 1 }` | Unique, Single-field | `-` |

#### Relationships
- `account_id` → **Account** (One-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `customer_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.17 CustomerConsent

#### Basic Information
- **Model name**: CustomerConsent
- **File path**: [`models/customerConsent.model.js`](file:///d:/KanilaApp/backend/models/customerConsent.model.js)
- **Collection name**: `customer_consents`
- **Module/domain**: Users & Customers
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| consent_type | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for consent_type |
| consent_status | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for consent_status |
| consent_version | `String` | No | No | `` | - | - | No | `trim` | Field mapping for consent_version |
| consented_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for consented_at |
| source_channel | `String` | No | No | `` | - | - | No | `trim` | Field mapping for source_channel |
| created_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for created_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ customer_id: 1 }` | Single-field | `-` |
| `{ customer_id: 1, consent_type: 1 }` | Compound | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `consent_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.18 CustomerCoupon

#### Basic Information
- **Model name**: CustomerCoupon
- **File path**: [`models/customerCoupon.model.js`](file:///d:/KanilaApp/backend/models/customerCoupon.model.js)
- **Collection name**: `customer_coupons`
- **Module/domain**: Users & Customers
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| couponId | `ObjectId` | Yes | No | `-` | - | **Coupon** | Yes | - | Field mapping for couponId |
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| savedAt | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for savedAt |
| status | `String` | No | No | `saved` | `["saved","used","expired"]` | - | Yes | - | Field mapping for status |
| usedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for usedAt |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ couponId: 1 }` | Single-field | `-` |
| `{ customer_id: 1 }` | Single-field | `-` |
| `{ status: 1 }` | Single-field | `-` |
| `{ customer_id: 1, couponId: 1 }` | Unique, Compound | `-` |

#### Relationships
- `couponId` → **Coupon** (Many-to-One): Identifies the related Coupon model.
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `couponId`) and snake_case (e.g. `customer_id`) field casing.

---

### 3.19 CustomerPreference

#### Basic Information
- **Model name**: CustomerPreference
- **File path**: [`models/customerPreference.model.js`](file:///d:/KanilaApp/backend/models/customerPreference.model.js)
- **Collection name**: `customer_preferences`
- **Module/domain**: Users & Customers
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| preference_key | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for preference_key |
| preference_value | `String` | No | No | `` | - | - | No | - | Field mapping for preference_value |
| updated_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for updated_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ customer_id: 1 }` | Single-field | `-` |
| `{ customer_id: 1, preference_key: 1 }` | Unique, Compound | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `preference_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.20 CustomerRecommendationSnapshot

#### Basic Information
- **Model name**: CustomerRecommendationSnapshot
- **File path**: [`models/customerRecommendationSnapshot.model.js`](file:///d:/KanilaApp/backend/models/customerRecommendationSnapshot.model.js)
- **Collection name**: `customer_recommendation_snapshots`
- **Module/domain**: Users & Customers
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| recommendation_type | `String` | Yes | No | `-` | - | - | Yes | - | e.g. "skin_profile_homepage" |
| profile_hash | `String` | Yes | No | `-` | - | - | Yes | - | Stable hash of the skin profile & preferences that influence recommendations. |
| product_ids | `Array` | Yes | No | `-` | - | - | Yes | - | Ranked list of product ids (top-N). |
| items | `Array` | No | No | `[]` | - | - | No | - | Lightweight per-product metadata to keep ranking stable on read time. |
| algorithm_version | `String` | No | No | `rule_v1` | - | - | Yes | - | Field mapping for algorithm_version |
| generated_at | `Date` | No | No | `[Function: now]` | - | - | Yes | - | Field mapping for generated_at |
| expires_at | `Date` | No | No | `null` | - | - | Yes | - | Field mapping for expires_at |
| invalidated_at | `Date` | No | No | `null` | - | - | No | - | Optional soft invalidation marker. |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ customer_id: 1 }` | Single-field | `-` |
| `{ recommendation_type: 1 }` | Single-field | `-` |
| `{ profile_hash: 1 }` | Single-field | `-` |
| `{ product_ids: 1 }` | Single-field | `-` |
| `{ algorithm_version: 1 }` | Single-field | `-` |
| `{ generated_at: 1 }` | Single-field | `-` |
| `{ expires_at: 1 }` | Single-field | `-` |
| `{ customer_id: 1, recommendation_type: 1 }` | Unique, Compound | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.21 GuestSession

#### Basic Information
- **Model name**: GuestSession
- **File path**: [`models/guestSession.model.js`](file:///d:/KanilaApp/backend/models/guestSession.model.js)
- **Collection name**: `guest_sessions`
- **Module/domain**: Cart
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| guest_session_id | `String` | Yes | Yes | `-` | - | - | Yes | `trim` | Field mapping for guest_session_id |
| status | `String` | No | No | `active` | `["active","expired"]` | - | No | - | Field mapping for status |
| last_seen_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for last_seen_at |
| user_agent | `String` | No | No | `` | - | - | No | `trim` | Field mapping for user_agent |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ guest_session_id: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.22 InventoryBalance

#### Basic Information
- **Model name**: InventoryBalance
- **File path**: [`models/inventoryBalance.model.js`](file:///d:/KanilaApp/backend/models/inventoryBalance.model.js)
- **Collection name**: `inventorybalances`
- **Module/domain**: Inventory & Warehousing
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| warehouseId | `ObjectId` | Yes | No | `-` | - | **Warehouse** | No | - | Field mapping for warehouseId |
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| onHandQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for onHandQty |
| reservedQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for reservedQty |
| blockedQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for blockedQty |
| availableQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for availableQty |
| reorderPointQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for reorderPointQty |
| safetyStockQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for safetyStockQty |
| lastCountedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for lastCountedAt |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ variantId: 1, availableQty: 1 }` | Compound | `-` |

#### Relationships
- `warehouseId` → **Warehouse** (Many-to-One): Identifies the related Warehouse model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `warehouseId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.23 InventoryTransaction

#### Basic Information
- **Model name**: InventoryTransaction
- **File path**: [`models/inventoryTransaction.model.js`](file:///d:/KanilaApp/backend/models/inventoryTransaction.model.js)
- **Collection name**: `inventorytransactions`
- **Module/domain**: Inventory & Warehousing
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| warehouseId | `ObjectId` | Yes | No | `-` | - | **Warehouse** | No | - | Field mapping for warehouseId |
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| transactionType | `String` | Yes | No | `-` | - | - | No | - | Field mapping for transactionType |
| quantityChange | `Number` | Yes | No | `-` | - | - | No | - | Field mapping for quantityChange |
| referenceType | `String` | No | No | `` | - | - | No | - | Field mapping for referenceType |
| referenceId | `String` | No | No | `` | - | - | No | - | Field mapping for referenceId |
| reasonCode | `String` | No | No | `` | - | - | No | - | Field mapping for reasonCode |
| note | `String` | No | No | `` | - | - | No | - | Field mapping for note |
| performedByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for performedByAccountId |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `warehouseId` → **Warehouse** (Many-to-One): Identifies the related Warehouse model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.
- `performedByAccountId` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `warehouseId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `performedByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.24 LoyaltyAccount

#### Basic Information
- **Model name**: LoyaltyAccount
- **File path**: [`models/loyaltyAccount.model.js`](file:///d:/KanilaApp/backend/models/loyaltyAccount.model.js)
- **Collection name**: `loyaltyaccounts`
- **Module/domain**: Loyalty Program
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | No | - | Field mapping for customer_id |
| tierId | `ObjectId` | No | No | `null` | - | **LoyaltyTier** | No | - | Field mapping for tierId |
| pointsBalance | `Number` | No | No | `0` | - | - | No | - | Field mapping for pointsBalance |
| lifetimePointsEarned | `Number` | No | No | `0` | - | - | No | - | Field mapping for lifetimePointsEarned |
| lifetimePointsRedeemed | `Number` | No | No | `0` | - | - | No | - | Field mapping for lifetimePointsRedeemed |
| loyaltyStatus | `String` | No | No | `active` | `["active","inactive","suspended"]` | - | No | - | Field mapping for loyaltyStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `tierId` → **LoyaltyTier** (Many-to-One): Identifies the related LoyaltyTier model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `customer_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `tierId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `tierId`) and snake_case (e.g. `customer_id`) field casing.

---

### 3.25 LoyaltyPointLedger

#### Basic Information
- **Model name**: LoyaltyPointLedger
- **File path**: [`models/loyaltyPointLedger.model.js`](file:///d:/KanilaApp/backend/models/loyaltyPointLedger.model.js)
- **Collection name**: `loyaltypointledgers`
- **Module/domain**: Loyalty Program
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| loyaltyAccountId | `ObjectId` | Yes | No | `-` | - | **LoyaltyAccount** | No | - | Field mapping for loyaltyAccountId |
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | No | - | Field mapping for customer_id |
| order_id | `ObjectId` | No | No | `null` | - | **Order** | No | - | Field mapping for order_id |
| transactionType | `String` | Yes | No | `-` | - | - | No | - | Field mapping for transactionType |
| pointsDelta | `Number` | Yes | No | `-` | - | - | No | - | Field mapping for pointsDelta |
| pointsBefore | `Number` | No | No | `0` | - | - | No | - | Field mapping for pointsBefore |
| pointsAfter | `Number` | No | No | `0` | - | - | No | - | Field mapping for pointsAfter |
| expiryDate | `Date` | No | No | `null` | - | - | No | - | Field mapping for expiryDate |
| referenceType | `String` | No | No | `` | - | - | No | - | Field mapping for referenceType |
| referenceId | `String` | No | No | `` | - | - | No | - | Field mapping for referenceId |
| note | `String` | No | No | `` | - | - | No | - | Field mapping for note |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `loyaltyAccountId` → **LoyaltyAccount** (Many-to-One): Identifies the related LoyaltyAccount model.
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `loyaltyAccountId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `customer_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `loyaltyAccountId`) and snake_case (e.g. `customer_id`) field casing.

---

### 3.26 LoyaltyTier

#### Basic Information
- **Model name**: LoyaltyTier
- **File path**: [`models/loyaltyTier.model.js`](file:///d:/KanilaApp/backend/models/loyaltyTier.model.js)
- **Collection name**: `loyaltytiers`
- **Module/domain**: Loyalty Program
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| tierCode | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for tierCode |
| tierName | `String` | Yes | No | `-` | - | - | No | - | Field mapping for tierName |
| minimumPoints | `Number` | No | No | `0` | - | - | No | - | Field mapping for minimumPoints |
| discountRate | `Number` | No | No | `0` | - | - | No | - | Field mapping for discountRate |
| priorityRank | `Number` | No | No | `0` | - | - | No | - | Field mapping for priorityRank |
| tierStatus | `String` | No | No | `active` | `["active","inactive"]` | - | No | - | Field mapping for tierStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ tierCode: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.27 Order

#### Basic Information
- **Model name**: Order
- **File path**: [`models/order.model.js`](file:///d:/KanilaApp/backend/models/order.model.js)
- **Collection name**: `orders`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| owner_type | `String` | No | No | `customer` | `["customer","guest"]` | - | Yes | - | Field mapping for owner_type |
| guest_session_id | `String` | No | No | `null` | - | - | Yes | `trim` | Field mapping for guest_session_id |
| guest_email | `String` | No | No | `` | - | - | Yes | `trim` | Field mapping for guest_email |
| guest_phone | `String` | No | No | `` | - | - | Yes | `trim` | Field mapping for guest_phone |
| guest_full_name | `String` | No | No | `` | - | - | No | `trim` | Field mapping for guest_full_name |
| order_number | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for order_number |
| customer_id | `ObjectId` | No | No | `-` | - | **Customer** | Yes | - | Field mapping for customer_id |
| checkout_session_id | `ObjectId` | No | No | `null` | - | **CheckoutSession** | No | - | Field mapping for checkout_session_id |
| currency_code | `String` | No | No | `VND` | - | - | No | `trim` | Field mapping for currency_code |
| order_status | `String` | No | No | `pending` | `["pending","confirmed","processing","completed","cancelled","returned"]` | - | No | - | Field mapping for order_status |
| payment_status | `String` | No | No | `unpaid` | `["unpaid","pending","authorized","paid","failed","partially_refunded","refunded"]` | - | No | - | Field mapping for payment_status |
| fulfillment_status | `String` | No | No | `unfulfilled` | `["unfulfilled","preparing","partially_shipped","shipped","in_transit","delivered","return_requested","return_approved","partially_returned","returned"]` | - | No | - | Field mapping for fulfillment_status |
| customer_note | `String` | No | No | `` | - | - | No | - | Field mapping for customer_note |
| placed_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for placed_at |
| confirmed_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for confirmed_at |
| cancelled_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for cancelled_at |
| completed_at | `Date` | No | No | `null` | - | - | No | - | Field mapping for completed_at |
| cancellation_reason | `String` | No | No | `` | - | - | No | - | Field mapping for cancellation_reason |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ owner_type: 1 }` | Single-field | `-` |
| `{ guest_session_id: 1 }` | Single-field | `-` |
| `{ guest_email: 1 }` | Single-field | `-` |
| `{ guest_phone: 1 }` | Single-field | `-` |
| `{ order_number: 1 }` | Unique, Single-field | `-` |
| `{ customer_id: 1 }` | Single-field | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `checkout_session_id` → **CheckoutSession** (Many-to-One): Identifies the related CheckoutSession model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `checkout_session_id` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.28 OrderAddress

#### Basic Information
- **Model name**: OrderAddress
- **File path**: [`models/orderAddress.model.js`](file:///d:/KanilaApp/backend/models/orderAddress.model.js)
- **Collection name**: `order_addresses`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | Yes | - | Field mapping for order_id |
| address_type | `String` | Yes | No | `-` | `["shipping","billing"]` | - | No | - | Field mapping for address_type |
| recipient_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for recipient_name |
| phone | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for phone |
| address_line_1 | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for address_line_1 |
| address_line_2 | `String` | No | No | `` | - | - | No | `trim` | Field mapping for address_line_2 |
| ward | `String` | No | No | `` | - | - | No | `trim` | Field mapping for ward |
| district | `String` | No | No | `` | - | - | No | `trim` | Field mapping for district |
| city | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for city |
| country_code | `String` | No | No | `VN` | - | - | No | `trim` | Field mapping for country_code |
| postal_code | `String` | No | No | `` | - | - | No | `trim` | Field mapping for postal_code |
| created_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for created_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ order_id: 1 }` | Single-field | `-` |

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_address_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.29 OrderItem

#### Basic Information
- **Model name**: OrderItem
- **File path**: [`models/orderItem.model.js`](file:///d:/KanilaApp/backend/models/orderItem.model.js)
- **Collection name**: `order_items`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | Yes | - | Field mapping for order_id |
| product_id | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for product_id |
| variant_id | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variant_id |
| sku_snapshot | `String` | Yes | No | `-` | - | - | No | - | Field mapping for sku_snapshot |
| product_name_snapshot | `String` | Yes | No | `-` | - | - | No | - | Field mapping for product_name_snapshot |
| variant_name_snapshot | `String` | Yes | No | `-` | - | - | No | - | Field mapping for variant_name_snapshot |
| quantity | `Number` | Yes | No | `-` | - | - | No | `min: 1` | Field mapping for quantity |
| unit_list_price_amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for unit_list_price_amount |
| unit_sale_price_amount | `Number` | No | No | `0` | - | - | No | `min: 0` | Field mapping for unit_sale_price_amount |
| unit_final_price_amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for unit_final_price_amount |
| line_subtotal_amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for line_subtotal_amount |
| line_discount_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for line_discount_amount |
| line_total_amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for line_total_amount |
| currency_code | `String` | No | No | `VND` | - | - | No | `trim` | Field mapping for currency_code |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ order_id: 1 }` | Single-field | `-` |

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.
- `product_id` → **Product** (Many-to-One): Identifies the related Product model.
- `variant_id` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_item_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `product_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `variant_id` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.30 OrderStatusHistory

#### Basic Information
- **Model name**: OrderStatusHistory
- **File path**: [`models/orderStatusHistory.model.js`](file:///d:/KanilaApp/backend/models/orderStatusHistory.model.js)
- **Collection name**: `order_status_history`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | Yes | - | Field mapping for order_id |
| old_order_status | `String` | No | No | `` | - | - | No | - | Field mapping for old_order_status |
| new_order_status | `String` | No | No | `` | - | - | No | - | Field mapping for new_order_status |
| old_payment_status | `String` | No | No | `` | - | - | No | - | Field mapping for old_payment_status |
| new_payment_status | `String` | No | No | `` | - | - | No | - | Field mapping for new_payment_status |
| old_fulfillment_status | `String` | No | No | `` | - | - | No | - | Field mapping for old_fulfillment_status |
| new_fulfillment_status | `String` | No | No | `` | - | - | No | - | Field mapping for new_fulfillment_status |
| changed_by_account_id | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for changed_by_account_id |
| change_reason | `String` | No | No | `` | - | - | No | - | Field mapping for change_reason |
| changed_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for changed_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ order_id: 1 }` | Single-field | `-` |

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.
- `changed_by_account_id` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_status_history_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `changed_by_account_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.31 OrderTotal

#### Basic Information
- **Model name**: OrderTotal
- **File path**: [`models/orderTotal.model.js`](file:///d:/KanilaApp/backend/models/orderTotal.model.js)
- **Collection name**: `order_totals`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | Yes | - | Field mapping for order_id |
| subtotal_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for subtotal_amount |
| item_discount_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for item_discount_amount |
| order_discount_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for order_discount_amount |
| shipping_fee_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for shipping_fee_amount |
| tax_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for tax_amount |
| grand_total_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for grand_total_amount |
| refunded_amount | `Number` | No | No | `0` | - | - | No | - | Field mapping for refunded_amount |
| currency_code | `String` | No | No | `VND` | - | - | No | `trim` | Field mapping for currency_code |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ order_id: 1 }` | Single-field | `-` |

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_total_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.32 PasswordResetOtp

#### Basic Information
- **Model name**: PasswordResetOtp
- **File path**: [`models/passwordResetOtp.model.js`](file:///d:/KanilaApp/backend/models/passwordResetOtp.model.js)
- **Collection name**: `password_reset_otps`
- **Module/domain**: Auth & Access Control
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| email | `String` | Yes | No | `-` | - | - | Yes | `trim, lowercase` | Field mapping for email |
| otp_hash | `String` | Yes | No | `-` | - | - | Yes | - | Field mapping for otp_hash |
| expires_at | `Date` | Yes | No | `-` | - | - | Yes | - | Field mapping for expires_at |
| used_at | `Date` | No | No | `null` | - | - | Yes | - | Field mapping for used_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ email: 1 }` | Single-field | `-` |
| `{ otp_hash: 1 }` | Single-field | `-` |
| `{ expires_at: 1 }` | Single-field | `-` |
| `{ used_at: 1 }` | Single-field | `-` |
| `{ email: 1, used_at: 1, expires_at: 1 }` | Compound | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Sensitive Field: `otp_hash` contains credentials or secrets. Ensure this is excluded from API responses (e.g. via `select: false` or manual sanitization).

---

### 3.33 PaymentIntent

#### Basic Information
- **Model name**: PaymentIntent
- **File path**: [`models/paymentIntent.model.js`](file:///d:/KanilaApp/backend/models/paymentIntent.model.js)
- **Collection name**: `paymentintents`
- **Module/domain**: Payments & Returns
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | No | - | Field mapping for order_id |
| payment_method_id | `ObjectId` | No | No | `null` | - | **PaymentMethod** | No | - | Field mapping for payment_method_id |
| providerCode | `String` | No | No | `` | - | - | No | - | Field mapping for providerCode |
| providerPaymentIntentId | `String` | No | No | `` | - | - | No | - | Field mapping for providerPaymentIntentId |
| requestedAmount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for requestedAmount |
| authorizedAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for authorizedAmount |
| capturedAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for capturedAmount |
| currencyCode | `String` | No | No | `VND` | - | - | No | - | Field mapping for currencyCode |
| intentStatus | `String` | No | No | `pending` | `["pending","authorized","captured","failed","cancelled"]` | - | No | - | Field mapping for intentStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.
- `payment_method_id` → **PaymentMethod** (Many-to-One): Identifies the related PaymentMethod model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `payment_method_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `providerCode`) and snake_case (e.g. `order_id`) field casing.

---

### 3.34 PaymentMethod

#### Basic Information
- **Model name**: PaymentMethod
- **File path**: [`models/paymentMethod.model.js`](file:///d:/KanilaApp/backend/models/paymentMethod.model.js)
- **Collection name**: `payment_methods`
- **Module/domain**: Payments & Returns
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| payment_method_code | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for payment_method_code |
| payment_method_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for payment_method_name |
| provider_code | `String` | No | No | `` | - | - | No | `trim` | Field mapping for provider_code |
| method_type | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for method_type |
| is_active | `Boolean` | No | No | `true` | - | - | No | - | Field mapping for is_active |
| sort_order | `Number` | No | No | `0` | - | - | No | - | Field mapping for sort_order |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ payment_method_code: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `payment_method_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.35 PaymentTransaction

#### Basic Information
- **Model name**: PaymentTransaction
- **File path**: [`models/paymentTransaction.model.js`](file:///d:/KanilaApp/backend/models/paymentTransaction.model.js)
- **Collection name**: `paymenttransactions`
- **Module/domain**: Payments & Returns
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| paymentIntentId | `ObjectId` | Yes | No | `-` | - | **PaymentIntent** | No | - | Field mapping for paymentIntentId |
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | No | - | Field mapping for order_id |
| transactionType | `String` | Yes | No | `-` | - | - | No | - | Field mapping for transactionType |
| providerTransactionId | `String` | No | No | `` | - | - | No | - | Field mapping for providerTransactionId |
| transactionStatus | `String` | No | No | `pending` | `["pending","success","failed"]` | - | No | - | Field mapping for transactionStatus |
| amount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for amount |
| currencyCode | `String` | No | No | `VND` | - | - | No | - | Field mapping for currencyCode |
| processedAt | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for processedAt |
| rawResponseJson | `String` | No | No | `` | - | - | No | - | Field mapping for rawResponseJson |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `paymentIntentId` → **PaymentIntent** (Many-to-One): Identifies the related PaymentIntent model.
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `paymentIntentId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `paymentIntentId`) and snake_case (e.g. `order_id`) field casing.

---

### 3.36 Permission

#### Basic Information
- **Model name**: Permission
- **File path**: [`models/permission.model.js`](file:///d:/KanilaApp/backend/models/permission.model.js)
- **Collection name**: `permissions`
- **Module/domain**: Auth & Access Control
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| permission_code | `String` | Yes | Yes | `-` | - | - | No | `trim` | Field mapping for permission_code |
| permission_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for permission_name |
| module_name | `String` | No | No | `` | - | - | No | `trim` | Field mapping for module_name |
| description | `String` | No | No | `` | - | - | No | - | Field mapping for description |
| created_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for created_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ permission_code: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.37 PriceBook

#### Basic Information
- **Model name**: PriceBook
- **File path**: [`models/priceBook.model.js`](file:///d:/KanilaApp/backend/models/priceBook.model.js)
- **Collection name**: `pricebooks`
- **Module/domain**: Price Management
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| priceBookCode | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for priceBookCode |
| priceBookName | `String` | Yes | No | `-` | - | - | No | - | Field mapping for priceBookName |
| currencyCode | `String` | Yes | No | `VND` | - | - | No | - | Field mapping for currencyCode |
| isDefault | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for isDefault |
| startAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for startAt |
| endAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for endAt |
| priceBookStatus | `String` | No | No | `active` | `["active","inactive"]` | - | No | - | Field mapping for priceBookStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ priceBookCode: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.38 PriceBookEntry

#### Basic Information
- **Model name**: PriceBookEntry
- **File path**: [`models/priceBookEntry.model.js`](file:///d:/KanilaApp/backend/models/priceBookEntry.model.js)
- **Collection name**: `pricebookentries`
- **Module/domain**: Price Management
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| priceBookId | `ObjectId` | Yes | No | `-` | - | **PriceBook** | No | - | Field mapping for priceBookId |
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| listPriceAmount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for listPriceAmount |
| salePriceAmount | `Number` | No | No | `0` | - | - | No | `min: 0` | Field mapping for salePriceAmount |
| effectiveFrom | `Date` | No | No | `null` | - | - | No | - | Field mapping for effectiveFrom |
| effectiveTo | `Date` | No | No | `null` | - | - | No | - | Field mapping for effectiveTo |
| isActive | `Boolean` | No | No | `true` | - | - | No | - | Field mapping for isActive |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `priceBookId` → **PriceBook** (Many-to-One): Identifies the related PriceBook model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `priceBookId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.39 PriceHistory

#### Basic Information
- **Model name**: PriceHistory
- **File path**: [`models/priceHistory.model.js`](file:///d:/KanilaApp/backend/models/priceHistory.model.js)
- **Collection name**: `pricehistories`
- **Module/domain**: Price Management
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| priceBookId | `ObjectId` | Yes | No | `-` | - | **PriceBook** | No | - | Field mapping for priceBookId |
| currencyCode | `String` | Yes | No | `-` | - | - | No | - | Field mapping for currencyCode |
| oldListPriceAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for oldListPriceAmount |
| oldSalePriceAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for oldSalePriceAmount |
| newListPriceAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for newListPriceAmount |
| newSalePriceAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for newSalePriceAmount |
| changeReason | `String` | No | No | `` | - | - | No | - | Field mapping for changeReason |
| changedByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for changedByAccountId |
| changedAt | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for changedAt |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.
- `priceBookId` → **PriceBook** (Many-to-One): Identifies the related PriceBook model.
- `changedByAccountId` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `priceBookId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `changedByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.40 Product

#### Basic Information
- **Model name**: Product
- **File path**: [`models/product.model.js`](file:///d:/KanilaApp/backend/models/product.model.js)
- **Collection name**: `products`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productName | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for productName |
| productCode | `String` | No | No | `-` | - | - | No | `trim, uppercase` | Field mapping for productCode |
| slug | `String` | No | Yes | `-` | - | - | No | `trim, lowercase, sparse` | URL-friendly identifier (unique when set). |
| brandId | `ObjectId` | Yes | No | `-` | - | **Brand** | No | - | Field mapping for brandId |
| categoryId | `ObjectId` | Yes | No | `-` | - | **Category** | No | - | Primary category (maps to primary_category_id). |
| price | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for price |
| compareAtPrice | `Number` | No | No | `null` | - | - | No | - | List / compare-at price (e.g. strikethrough on cards) — optional |
| imageUrl | `String` | No | No | `` | - | - | No | - | Field mapping for imageUrl |
| shortDescription | `String` | No | No | `` | - | - | No | - | Field mapping for shortDescription |
| longDescription | `String` | No | No | `` | - | - | No | - | Field mapping for longDescription |
| stock | `Number` | No | No | `0` | - | - | No | `min: 0` | Field mapping for stock |
| bought | `Number` | No | No | `0` | - | - | No | `min: 0` | Field mapping for bought |
| averageRating | `Number` | No | No | `0` | - | - | No | `min: 0` | Field mapping for averageRating |
| isActive | `Boolean` | No | No | `true` | - | - | No | - | Field mapping for isActive |
| productStatus | `String` | No | No | `active` | `["active","inactive"]` | - | No | - | Mirrors isActive for APIs that use string status; kept in sync in pre-save. |
| ingredientText | `String` | No | No | `` | - | - | No | - | Field mapping for ingredientText |
| shades | `Array` | No | No | `-` | - | - | No | - | Field mapping for shades |
| skin_types_supported | `Array` | No | No | `[]` | - | - | No | - | Field mapping for skin_types_supported |
| concerns_targeted | `Array` | No | No | `[]` | - | - | No | - | Field mapping for concerns_targeted |
| ingredient_flags | `Array` | No | No | `[]` | - | - | No | - | Field mapping for ingredient_flags |
| key_ingredients | `Array` | No | No | `[]` | - | - | No | - | Field mapping for key_ingredients |
| is_sensitive_friendly | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for is_sensitive_friendly |
| tone_match_supported | `Array` | No | No | `[]` | - | - | No | - | Field mapping for tone_match_supported |
| finish_type | `String` | No | No | `` | - | - | No | - | Field mapping for finish_type |
| coverage_type | `String` | No | No | `` | - | - | No | - | Field mapping for coverage_type |
| sales_count | `Number` | No | No | `0` | - | - | No | `min: 0` | Field mapping for sales_count |
| is_best_seller | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for is_best_seller |
| usageInstruction | `String` | No | No | `` | - | - | No | - | Field mapping for usageInstruction |
| createdByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Optional audit refs — must exist on schema for `.populate()` in getProductById. |
| updatedByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for updatedByAccountId |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ slug: 1 }` | Unique, Sparse, Single-field | `-` |
| `{ createdAt: -1 }` | Single-field | `-` |
| `{ brandId: 1, createdAt: -1 }` | Compound | `-` |
| `{ categoryId: 1, createdAt: -1 }` | Compound | `-` |
| `{ bought: -1, createdAt: -1 }` | Compound | `-` |
| `{ productCode: 1 }` | Single-field | `-` |
| `{ skin_types_supported: 1 }` | Single-field | `-` |
| `{ shades.hex: 1 }` | Single-field | `-` |
| `{ productStatus: 1, isActive: 1, categoryId: 1, price: 1 }` | Compound | `-` |
| `{ productStatus: 1, isActive: 1, brandId: 1, price: 1 }` | Compound | `-` |
| `{ productStatus: 1, isActive: 1, categoryId: 1, bought: -1 }` | Compound | `-` |
| `{ productStatus: 1, isActive: 1, averageRating: -1 }` | Compound | `-` |

#### Relationships
- `brandId` → **Brand** (Many-to-One): Identifies the related Brand model.
- `categoryId` → **Category** (Many-to-One): Primary category (maps to primary_category_id).
- `createdByAccountId` → **Account** (Many-to-One): Optional audit refs — must exist on schema for `.populate()` in getProductById.
- `updatedByAccountId` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `createdByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `updatedByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `productName`) and snake_case (e.g. `skin_types_supported`) field casing.

---

### 3.41 ProductAttribute

#### Basic Information
- **Model name**: ProductAttribute
- **File path**: [`models/productAttribute.model.js`](file:///d:/KanilaApp/backend/models/productAttribute.model.js)
- **Collection name**: `productattributes`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productId | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for productId |
| attributeName | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for attributeName |
| attributeValue | `String` | No | No | `` | - | - | No | - | Field mapping for attributeValue |
| displayOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for displayOrder |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ productId: 1, displayOrder: 1 }` | Compound | `-` |
| `{ attributeName: 1, attributeValue: 1, productId: 1 }` | Compound | `-` |

#### Relationships
- `productId` → **Product** (Many-to-One): Identifies the related Product model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.42 ProductCategory

#### Basic Information
- **Model name**: ProductCategory
- **File path**: [`models/productCategory.model.js`](file:///d:/KanilaApp/backend/models/productCategory.model.js)
- **Collection name**: `productcategories`
- **Module/domain**: General
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productId | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for productId |
| categoryId | `ObjectId` | Yes | No | `-` | - | **Category** | No | - | Field mapping for categoryId |
| isPrimary | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for isPrimary |
| sortOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for sortOrder |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ productId: 1, categoryId: 1 }` | Unique, Compound | `-` |

#### Relationships
- `productId` → **Product** (Many-to-One): Identifies the related Product model.
- `categoryId` → **Category** (Many-to-One): Identifies the related Category model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `categoryId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.43 ProductMedia

#### Basic Information
- **Model name**: ProductMedia
- **File path**: [`models/productMedia.model.js`](file:///d:/KanilaApp/backend/models/productMedia.model.js)
- **Collection name**: `productmedias`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productId | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for productId |
| mediaType | `String` | No | No | `image` | `["image","video"]` | - | No | - | Field mapping for mediaType |
| mediaUrl | `String` | Yes | No | `-` | - | - | No | - | Field mapping for mediaUrl |
| altText | `String` | No | No | `` | - | - | No | - | Field mapping for altText |
| sortOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for sortOrder |
| isPrimary | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for isPrimary |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ productId: 1, isPrimary: -1, sortOrder: 1, createdAt: 1 }` | Compound | `-` |

#### Relationships
- `productId` → **Product** (Many-to-One): Identifies the related Product model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.44 ProductOption

#### Basic Information
- **Model name**: ProductOption
- **File path**: [`models/productOption.model.js`](file:///d:/KanilaApp/backend/models/productOption.model.js)
- **Collection name**: `productoptions`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productId | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for productId |
| optionName | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for optionName |
| displayOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for displayOrder |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ productId: 1, displayOrder: 1 }` | Compound | `-` |
| `{ optionName: 1, productId: 1 }` | Compound | `-` |

#### Relationships
- `productId` → **Product** (Many-to-One): Identifies the related Product model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.45 ProductOptionValue

#### Basic Information
- **Model name**: ProductOptionValue
- **File path**: [`models/productOptionValue.model.js`](file:///d:/KanilaApp/backend/models/productOptionValue.model.js)
- **Collection name**: `productoptionvalues`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productOptionId | `ObjectId` | Yes | No | `-` | - | **ProductOption** | No | - | Field mapping for productOptionId |
| optionValue | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for optionValue |
| displayOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for displayOrder |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ productOptionId: 1, displayOrder: 1 }` | Compound | `-` |
| `{ optionValue: 1, productOptionId: 1 }` | Compound | `-` |

#### Relationships
- `productOptionId` → **ProductOption** (Many-to-One): Identifies the related ProductOption model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.46 ProductVariant

#### Basic Information
- **Model name**: ProductVariant
- **File path**: [`models/productVariant.model.js`](file:///d:/KanilaApp/backend/models/productVariant.model.js)
- **Collection name**: `productvariants`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productId | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for productId |
| sku | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for sku |
| barcode | `String` | No | No | `` | - | - | No | - | Field mapping for barcode |
| variantName | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for variantName |
| variantStatus | `String` | No | No | `active` | `["active","inactive"]` | - | No | - | Field mapping for variantStatus |
| weightGrams | `Number` | No | No | `0` | - | - | No | - | Field mapping for weightGrams |
| volumeMl | `Number` | No | No | `0` | - | - | No | - | Field mapping for volumeMl |
| costAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for costAmount |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ sku: 1 }` | Unique, Single-field | `-` |
| `{ productId: 1, variantStatus: 1 }` | Compound | `-` |

#### Relationships
- `productId` → **Product** (Many-to-One): Identifies the related Product model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.47 Promotion

#### Basic Information
- **Model name**: Promotion
- **File path**: [`models/promotion.model.js`](file:///d:/KanilaApp/backend/models/promotion.model.js)
- **Collection name**: `promotions`
- **Module/domain**: Vouchers & Promotions
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| promotionCode | `String` | No | Yes | `null` | - | - | No | `trim, uppercase, sparse` | Field mapping for promotionCode |
| promotionName | `String` | Yes | No | `-` | - | - | No | - | Field mapping for promotionName |
| description | `String` | No | No | `` | - | - | No | - | Field mapping for description |
| promotionType | `String` | Yes | No | `-` | - | - | No | - | Field mapping for promotionType |
| discountType | `String` | Yes | No | `-` | - | - | No | - | Field mapping for discountType |
| discountValue | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for discountValue |
| maxDiscountAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for maxDiscountAmount |
| startAt | `Date` | Yes | No | `-` | - | - | No | - | Field mapping for startAt |
| endAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for endAt |
| usageLimitTotal | `Number` | No | No | `0` | - | - | No | - | Field mapping for usageLimitTotal |
| usageLimitPerCustomer | `Number` | No | No | `0` | - | - | No | - | Field mapping for usageLimitPerCustomer |
| isAutoApply | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for isAutoApply |
| priority | `Number` | No | No | `0` | - | - | No | - | Field mapping for priority |
| stackableFlag | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for stackableFlag |
| promotionStatus | `String` | No | No | `draft` | `["draft","active","inactive"]` | - | No | - | Field mapping for promotionStatus |
| createdByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for createdByAccountId |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ promotionCode: 1 }` | Unique, Sparse, Single-field | `-` |

#### Relationships
- `createdByAccountId` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `createdByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.48 PromotionRule

#### Basic Information
- **Model name**: PromotionRule
- **File path**: [`models/promotionRule.model.js`](file:///d:/KanilaApp/backend/models/promotionRule.model.js)
- **Collection name**: `promotionrules`
- **Module/domain**: Vouchers & Promotions
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| promotionId | `ObjectId` | Yes | No | `-` | - | **Promotion** | No | - | Field mapping for promotionId |
| ruleType | `String` | Yes | No | `-` | - | - | No | - | Field mapping for ruleType |
| operator | `String` | Yes | No | `-` | - | - | No | - | Field mapping for operator |
| ruleValue | `String` | Yes | No | `-` | - | - | No | - | Field mapping for ruleValue |
| currencyCode | `String` | No | No | `` | - | - | No | - | Field mapping for currencyCode |
| notes | `String` | No | No | `` | - | - | No | - | Field mapping for notes |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `promotionId` → **Promotion** (Many-to-One): Identifies the related Promotion model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `promotionId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.49 PromotionTarget

#### Basic Information
- **Model name**: PromotionTarget
- **File path**: [`models/promotionTarget.model.js`](file:///d:/KanilaApp/backend/models/promotionTarget.model.js)
- **Collection name**: `promotiontargets`
- **Module/domain**: Vouchers & Promotions
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| promotionId | `ObjectId` | Yes | No | `-` | - | **Promotion** | No | - | Field mapping for promotionId |
| targetType | `String` | Yes | No | `-` | - | - | No | - | Field mapping for targetType |
| targetRefId | `ObjectId` | No | No | `null` | - | - | No | - | Field mapping for targetRefId |
| targetRefCode | `String` | No | No | `` | - | - | No | - | Field mapping for targetRefCode |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `promotionId` → **Promotion** (Many-to-One): Identifies the related Promotion model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `promotionId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.50 RecommendationLog

#### Basic Information
- **Model name**: RecommendationLog
- **File path**: [`models/recommendation-log.model.js`](file:///d:/KanilaApp/backend/models/recommendation-log.model.js)
- **Collection name**: `recommendation_logs`
- **Module/domain**: Support & Logs
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | No | No | `null` | - | **Customer** | Yes | - | Field mapping for customer_id |
| product_id | `ObjectId` | Yes | No | `-` | - | **Product** | Yes | - | Field mapping for product_id |
| context | `String` | No | No | `unknown` | `["homepage","profile_page","category_page","preview","unknown"]` | - | Yes | - | Field mapping for context |
| category_context | `String` | No | No | `` | - | - | No | - | Field mapping for category_context |
| score | `Number` | Yes | No | `-` | - | - | No | - | Field mapping for score |
| reason_codes | `Array` | No | No | `[]` | - | - | No | - | Field mapping for reason_codes |
| reasons | `Array` | No | No | `[]` | - | - | No | - | Field mapping for reasons |
| badges | `Array` | No | No | `[]` | - | - | No | - | Field mapping for badges |
| score_breakdown | `Mixed` | No | No | `[Function: anonymous]` | - | - | No | - | Field mapping for score_breakdown |
| rank_position | `Number` | No | No | `0` | - | - | No | - | Field mapping for rank_position |
| generated_at | `Date` | No | No | `[Function: now]` | - | - | Yes | - | Field mapping for generated_at |
| algorithm_version | `String` | No | No | `rule_v1` | - | - | Yes | - | Field mapping for algorithm_version |
| session_id | `String` | No | No | `` | - | - | No | - | Field mapping for session_id |
| request_source | `String` | No | No | `` | - | - | No | - | Field mapping for request_source |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ customer_id: 1 }` | Single-field | `-` |
| `{ product_id: 1 }` | Single-field | `-` |
| `{ context: 1 }` | Single-field | `-` |
| `{ generated_at: 1 }` | Single-field | `-` |
| `{ algorithm_version: 1 }` | Single-field | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `product_id` → **Product** (Many-to-One): Identifies the related Product model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.51 Refund

#### Basic Information
- **Model name**: Refund
- **File path**: [`models/refund.model.js`](file:///d:/KanilaApp/backend/models/refund.model.js)
- **Collection name**: `refunds`
- **Module/domain**: Payments & Returns
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | No | - | Field mapping for order_id |
| paymentTransactionId | `ObjectId` | No | No | `null` | - | **PaymentTransaction** | No | - | Field mapping for paymentTransactionId |
| refundReason | `String` | No | No | `` | - | - | No | - | Field mapping for refundReason |
| refundStatus | `String` | No | No | `requested` | `["requested","approved","processing","completed","rejected"]` | - | No | - | Field mapping for refundStatus |
| requestedAmount | `Number` | Yes | No | `-` | - | - | No | `min: 0` | Field mapping for requestedAmount |
| approvedAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for approvedAmount |
| refundedAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for refundedAmount |
| currencyCode | `String` | No | No | `VND` | - | - | No | - | Field mapping for currencyCode |
| requestedByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for requestedByAccountId |
| approvedByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for approvedByAccountId |
| requestedAt | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for requestedAt |
| approvedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for approvedAt |
| completedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for completedAt |
| note | `String` | No | No | `` | - | - | No | - | Field mapping for note |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.
- `paymentTransactionId` → **PaymentTransaction** (Many-to-One): Identifies the related PaymentTransaction model.
- `requestedByAccountId` → **Account** (Many-to-One): Identifies the related Account model.
- `approvedByAccountId` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `paymentTransactionId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `requestedByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `approvedByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `paymentTransactionId`) and snake_case (e.g. `order_id`) field casing.

---

### 3.52 Return

#### Basic Information
- **Model name**: Return
- **File path**: [`models/return.model.js`](file:///d:/KanilaApp/backend/models/return.model.js)
- **Collection name**: `returns`
- **Module/domain**: Payments & Returns
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | No | - | Field mapping for order_id |
| shipmentId | `ObjectId` | No | No | `null` | - | **Shipment** | No | - | Field mapping for shipmentId |
| returnNumber | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for returnNumber |
| returnReason | `String` | No | No | `` | - | - | No | - | Field mapping for returnReason |
| returnStatus | `String` | No | No | `requested` | `["requested","approved","received","completed","rejected"]` | - | No | - | Field mapping for returnStatus |
| requested_by_customer_id | `ObjectId` | No | No | `null` | - | **Customer** | No | - | Field mapping for requested_by_customer_id |
| approvedByAccountId | `ObjectId` | No | No | `null` | - | **Account** | No | - | Field mapping for approvedByAccountId |
| requestedAt | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for requestedAt |
| approvedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for approvedAt |
| receivedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for receivedAt |
| completedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for completedAt |
| note | `String` | No | No | `` | - | - | No | - | Field mapping for note |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ returnNumber: 1 }` | Unique, Single-field | `-` |

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.
- `shipmentId` → **Shipment** (Many-to-One): Identifies the related Shipment model.
- `requested_by_customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `approvedByAccountId` → **Account** (Many-to-One): Identifies the related Account model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `shipmentId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `requested_by_customer_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `approvedByAccountId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `shipmentId`) and snake_case (e.g. `order_id`) field casing.

---

### 3.53 ReturnItem

#### Basic Information
- **Model name**: ReturnItem
- **File path**: [`models/returnItem.model.js`](file:///d:/KanilaApp/backend/models/returnItem.model.js)
- **Collection name**: `returnitems`
- **Module/domain**: Payments & Returns
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| returnId | `ObjectId` | Yes | No | `-` | - | **Return** | No | - | Field mapping for returnId |
| orderItemId | `ObjectId` | Yes | No | `-` | - | **OrderItem** | No | - | Field mapping for orderItemId |
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| requestedQty | `Number` | Yes | No | `-` | - | - | No | `min: 1` | Field mapping for requestedQty |
| approvedQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for approvedQty |
| receivedQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for receivedQty |
| restockQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for restockQty |
| rejectQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for rejectQty |
| restockStatus | `String` | No | No | `pending` | `["pending","restocked","disposed"]` | - | No | - | Field mapping for restockStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `returnId` → **Return** (Many-to-One): Identifies the related Return model.
- `orderItemId` → **OrderItem** (Many-to-One): Identifies the related OrderItem model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `returnId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `orderItemId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.54 Review

#### Basic Information
- **Model name**: Review
- **File path**: [`models/review.model.js`](file:///d:/KanilaApp/backend/models/review.model.js)
- **Collection name**: `reviews`
- **Module/domain**: Reviews & Ratings
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | No | - | Field mapping for customer_id |
| orderItemId | `ObjectId` | No | No | `null` | - | **OrderItem** | No | - | Field mapping for orderItemId |
| productId | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for productId |
| variantId | `ObjectId` | No | No | `null` | - | **ProductVariant** | No | - | Field mapping for variantId |
| rating | `Number` | Yes | No | `-` | - | - | No | `min: 1, max: 5` | Field mapping for rating |
| reviewTitle | `String` | No | No | `` | - | - | No | - | Field mapping for reviewTitle |
| reviewContent | `String` | No | No | `` | - | - | No | - | Field mapping for reviewContent |
| reviewStatus | `String` | No | No | `visible` | `["visible","hidden"]` | - | No | - | Field mapping for reviewStatus |
| helpfulCount | `Number` | No | No | `0` | - | - | No | - | Field mapping for helpfulCount |
| verifiedPurchaseFlag | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for verifiedPurchaseFlag |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ productId: 1, reviewStatus: 1 }` | Compound | `-` |
| `{ customer_id: 1, productId: 1 }` | Compound | `-` |
| `{ orderItemId: 1, customer_id: 1 }` | Unique, Sparse, Compound | `-` |
| `{ reviewStatus: 1, createdAt: -1 }` | Compound | `-` |

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.
- `orderItemId` → **OrderItem** (Many-to-One): Identifies the related OrderItem model.
- `productId` → **Product** (Many-to-One): Identifies the related Product model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `orderItemId`) and snake_case (e.g. `customer_id`) field casing.

---

### 3.55 ReviewMedia

#### Basic Information
- **Model name**: ReviewMedia
- **File path**: [`models/reviewMedia.model.js`](file:///d:/KanilaApp/backend/models/reviewMedia.model.js)
- **Collection name**: `reviewmedias`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| reviewId | `ObjectId` | Yes | No | `-` | - | **Review** | No | - | Field mapping for reviewId |
| mediaType | `String` | No | No | `image` | `["image","video"]` | - | No | - | Field mapping for mediaType |
| mediaUrl | `String` | Yes | No | `-` | - | - | No | - | Field mapping for mediaUrl |
| sortOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for sortOrder |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ reviewId: 1, sortOrder: 1 }` | Compound | `-` |

#### Relationships
- `reviewId` → **Review** (Many-to-One): Identifies the related Review model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.56 ReviewSummary

#### Basic Information
- **Model name**: ReviewSummary
- **File path**: [`models/reviewSummary.model.js`](file:///d:/KanilaApp/backend/models/reviewSummary.model.js)
- **Collection name**: `reviewsummaries`
- **Module/domain**: Reviews & Ratings
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| productId | `ObjectId` | Yes | Yes | `-` | - | **Product** | No | - | Field mapping for productId |
| reviewCount | `Number` | No | No | `0` | - | - | No | - | Field mapping for reviewCount |
| averageRating | `Number` | No | No | `0` | - | - | No | - | Field mapping for averageRating |
| rating1Count | `Number` | No | No | `0` | - | - | No | - | Field mapping for rating1Count |
| rating2Count | `Number` | No | No | `0` | - | - | No | - | Field mapping for rating2Count |
| rating3Count | `Number` | No | No | `0` | - | - | No | - | Field mapping for rating3Count |
| rating4Count | `Number` | No | No | `0` | - | - | No | - | Field mapping for rating4Count |
| rating5Count | `Number` | No | No | `0` | - | - | No | - | Field mapping for rating5Count |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ productId: 1 }` | Unique, Single-field | `-` |

#### Relationships
- `productId` → **Product** (One-to-One): Identifies the related Product model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.57 ReviewVote

#### Basic Information
- **Model name**: ReviewVote
- **File path**: [`models/reviewVote.model.js`](file:///d:/KanilaApp/backend/models/reviewVote.model.js)
- **Collection name**: `reviewvotes`
- **Module/domain**: Reviews & Ratings
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| reviewId | `ObjectId` | Yes | No | `-` | - | **Review** | No | - | Field mapping for reviewId |
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | No | - | Field mapping for customer_id |
| voteType | `String` | Yes | No | `-` | `["helpful","not_helpful"]` | - | No | - | Field mapping for voteType |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `reviewId` → **Review** (Many-to-One): Identifies the related Review model.
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `reviewId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `customer_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `reviewId`) and snake_case (e.g. `customer_id`) field casing.

---

### 3.58 Role

#### Basic Information
- **Model name**: Role
- **File path**: [`models/role.model.js`](file:///d:/KanilaApp/backend/models/role.model.js)
- **Collection name**: `roles`
- **Module/domain**: Auth & Access Control
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| role_code | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for role_code |
| role_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for role_name |
| description | `String` | No | No | `` | - | - | No | - | Field mapping for description |
| is_system_role | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for is_system_role |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ role_code: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.59 RolePermission

#### Basic Information
- **Model name**: RolePermission
- **File path**: [`models/rolePermission.model.js`](file:///d:/KanilaApp/backend/models/rolePermission.model.js)
- **Collection name**: `role_permissions`
- **Module/domain**: Auth & Access Control
- **Schema options**: `{"versionKey":"__v"}`
- **Timestamps**: No
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| role_id | `ObjectId` | Yes | No | `-` | - | **Role** | Yes | - | Field mapping for role_id |
| permission_id | `ObjectId` | Yes | No | `-` | - | **Permission** | Yes | - | Field mapping for permission_id |
| created_at | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for created_at |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ role_id: 1 }` | Single-field | `-` |
| `{ permission_id: 1 }` | Single-field | `-` |
| `{ role_id: 1, permission_id: 1 }` | Unique, Compound | `-` |

#### Relationships
- `role_id` → **Role** (Many-to-One): Identifies the related Role model.
- `permission_id` → **Permission** (Many-to-One): Identifies the related Permission model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Audit Risk: Timestamps are not enabled. Recommend adding `{ timestamps: true }` to automatically track creation and updates.

---

### 3.60 Shipment

#### Basic Information
- **Model name**: Shipment
- **File path**: [`models/shipment.model.js`](file:///d:/KanilaApp/backend/models/shipment.model.js)
- **Collection name**: `shipments`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| order_id | `ObjectId` | Yes | No | `-` | - | **Order** | No | - | Field mapping for order_id |
| warehouseId | `ObjectId` | No | No | `null` | - | **Warehouse** | No | - | Field mapping for warehouseId |
| shipmentNumber | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for shipmentNumber |
| carrierCode | `String` | No | No | `` | - | - | No | - | Field mapping for carrierCode |
| serviceName | `String` | No | No | `` | - | - | No | - | Field mapping for serviceName |
| trackingNumber | `String` | No | No | `` | - | - | No | - | Field mapping for trackingNumber |
| shipmentStatus | `String` | No | No | `pending` | `["pending","ready_to_ship","shipped","in_transit","delivered","failed","returned"]` | - | No | - | Field mapping for shipmentStatus |
| shippedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for shippedAt |
| deliveredAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for deliveredAt |
| failedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for failedAt |
| shippingFeeAmount | `Number` | No | No | `0` | - | - | No | - | Field mapping for shippingFeeAmount |
| currencyCode | `String` | No | No | `VND` | - | - | No | - | Field mapping for currencyCode |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ shipmentNumber: 1 }` | Unique, Single-field | `-` |

#### Relationships
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.
- `warehouseId` → **Warehouse** (Many-to-One): Identifies the related Warehouse model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `warehouseId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `warehouseId`) and snake_case (e.g. `order_id`) field casing.

---

### 3.61 ShipmentEvent

#### Basic Information
- **Model name**: ShipmentEvent
- **File path**: [`models/shipmentEvent.model.js`](file:///d:/KanilaApp/backend/models/shipmentEvent.model.js)
- **Collection name**: `shipmentevents`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| shipmentId | `ObjectId` | Yes | No | `-` | - | **Shipment** | No | - | Field mapping for shipmentId |
| eventCode | `String` | Yes | No | `-` | - | - | No | - | Field mapping for eventCode |
| eventStatus | `String` | No | No | `` | - | - | No | - | Field mapping for eventStatus |
| eventDescription | `String` | No | No | `` | - | - | No | - | Field mapping for eventDescription |
| eventTime | `Date` | No | No | `[Function: now]` | - | - | No | - | Field mapping for eventTime |
| locationText | `String` | No | No | `` | - | - | No | - | Field mapping for locationText |
| rawPayloadJson | `String` | No | No | `` | - | - | No | - | Field mapping for rawPayloadJson |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `shipmentId` → **Shipment** (Many-to-One): Identifies the related Shipment model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `shipmentId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.62 ShipmentItem

#### Basic Information
- **Model name**: ShipmentItem
- **File path**: [`models/shipmentItem.model.js`](file:///d:/KanilaApp/backend/models/shipmentItem.model.js)
- **Collection name**: `shipmentitems`
- **Module/domain**: Orders & Shipments
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| shipmentId | `ObjectId` | Yes | No | `-` | - | **Shipment** | No | - | Field mapping for shipmentId |
| orderItemId | `ObjectId` | Yes | No | `-` | - | **OrderItem** | No | - | Field mapping for orderItemId |
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| shippedQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for shippedQty |
| deliveredQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for deliveredQty |
| returnedQty | `Number` | No | No | `0` | - | - | No | - | Field mapping for returnedQty |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `shipmentId` → **Shipment** (Many-to-One): Identifies the related Shipment model.
- `orderItemId` → **OrderItem** (Many-to-One): Identifies the related OrderItem model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `shipmentId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `orderItemId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.63 ShippingMethod

#### Basic Information
- **Model name**: ShippingMethod
- **File path**: [`models/shippingMethod.model.js`](file:///d:/KanilaApp/backend/models/shippingMethod.model.js)
- **Collection name**: `shipping_methods`
- **Module/domain**: General
- **Schema options**: `{"timestamps":{"createdAt":"created_at","updatedAt":"updated_at"},"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| shipping_method_code | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for shipping_method_code |
| shipping_method_name | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for shipping_method_name |
| carrier_code | `String` | Yes | No | `-` | - | - | No | `trim` | Field mapping for carrier_code |
| service_level | `String` | No | No | `` | - | - | No | `trim` | Field mapping for service_level |
| description | `String` | No | No | `` | - | - | No | - | Field mapping for description |
| is_active | `Boolean` | No | No | `true` | - | - | No | - | Field mapping for is_active |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| created_at | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for created_at |
| updated_at | `Date` | No | No | `-` | - | - | No | - | Field mapping for updated_at |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ shipping_method_code: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `shipping_method_id`

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.64 StockReservation

#### Basic Information
- **Model name**: StockReservation
- **File path**: [`models/stockReservation.model.js`](file:///d:/KanilaApp/backend/models/stockReservation.model.js)
- **Collection name**: `stockreservations`
- **Module/domain**: Inventory & Warehousing
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| warehouseId | `ObjectId` | Yes | No | `-` | - | **Warehouse** | No | - | Field mapping for warehouseId |
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| cart_id | `ObjectId` | No | No | `null` | - | **Cart** | No | - | Field mapping for cart_id |
| checkout_session_id | `ObjectId` | No | No | `null` | - | **CheckoutSession** | No | - | Field mapping for checkout_session_id |
| order_id | `ObjectId` | No | No | `null` | - | **Order** | No | - | Field mapping for order_id |
| reservedQty | `Number` | Yes | No | `-` | - | - | No | `min: 1` | Field mapping for reservedQty |
| reservationStatus | `String` | No | No | `active` | `["active","released","expired"]` | - | No | - | Field mapping for reservationStatus |
| expiresAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for expiresAt |
| releasedAt | `Date` | No | No | `null` | - | - | No | - | Field mapping for releasedAt |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `warehouseId` → **Warehouse** (Many-to-One): Identifies the related Warehouse model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.
- `cart_id` → **Cart** (Many-to-One): Identifies the related Cart model.
- `checkout_session_id` → **CheckoutSession** (Many-to-One): Identifies the related CheckoutSession model.
- `order_id` → **Order** (Many-to-One): Identifies the related Order model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `warehouseId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `cart_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `checkout_session_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `order_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `warehouseId`) and snake_case (e.g. `cart_id`) field casing.

---

### 3.65 VariantMedia

#### Basic Information
- **Model name**: VariantMedia
- **File path**: [`models/variantMedia.model.js`](file:///d:/KanilaApp/backend/models/variantMedia.model.js)
- **Collection name**: `variantmedias`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| mediaType | `String` | No | No | `image` | `["image","video"]` | - | No | - | Field mapping for mediaType |
| mediaUrl | `String` | Yes | No | `-` | - | - | No | - | Field mapping for mediaUrl |
| sortOrder | `Number` | No | No | `0` | - | - | No | - | Field mapping for sortOrder |
| isPrimary | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for isPrimary |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.66 VariantOptionValue

#### Basic Information
- **Model name**: VariantOptionValue
- **File path**: [`models/variantOptionValue.model.js`](file:///d:/KanilaApp/backend/models/variantOptionValue.model.js)
- **Collection name**: `variantoptionvalues`
- **Module/domain**: Products & Categories
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| variantId | `ObjectId` | Yes | No | `-` | - | **ProductVariant** | No | - | Field mapping for variantId |
| productOptionValueId | `ObjectId` | Yes | No | `-` | - | **ProductOptionValue** | No | - | Field mapping for productOptionValueId |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ variantId: 1, productOptionValueId: 1 }` | Unique, Compound | `-` |

#### Relationships
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.
- `productOptionValueId` → **ProductOptionValue** (Many-to-One): Identifies the related ProductOptionValue model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `productOptionValueId` is not indexed. Querying or populating this relationship will require a collection scan.

---

### 3.67 Warehouse

#### Basic Information
- **Model name**: Warehouse
- **File path**: [`models/warehouse.model.js`](file:///d:/KanilaApp/backend/models/warehouse.model.js)
- **Collection name**: `warehouses`
- **Module/domain**: Inventory & Warehousing
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| warehouseCode | `String` | Yes | Yes | `-` | - | - | No | `trim, uppercase` | Field mapping for warehouseCode |
| warehouseName | `String` | Yes | No | `-` | - | - | No | - | Field mapping for warehouseName |
| contactName | `String` | No | No | `` | - | - | No | - | Field mapping for contactName |
| phone | `String` | No | No | `` | - | - | No | - | Field mapping for phone |
| addressLine1 | `String` | Yes | No | `-` | - | - | No | - | Field mapping for addressLine1 |
| addressLine2 | `String` | No | No | `` | - | - | No | - | Field mapping for addressLine2 |
| ward | `String` | No | No | `` | - | - | No | - | Field mapping for ward |
| district | `String` | No | No | `` | - | - | No | - | Field mapping for district |
| city | `String` | Yes | No | `-` | - | - | No | - | Field mapping for city |
| countryCode | `String` | No | No | `VN` | - | - | No | - | Field mapping for countryCode |
| warehouseStatus | `String` | No | No | `active` | `["active","inactive"]` | - | No | - | Field mapping for warehouseStatus |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
| Fields | Properties | Name |
|---|---|---|
| `{ warehouseCode: 1 }` | Unique, Single-field | `-` |

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- No major security or performance risks identified in this schema.

---

### 3.68 Wishlist

#### Basic Information
- **Model name**: Wishlist
- **File path**: [`models/wishlist.model.js`](file:///d:/KanilaApp/backend/models/wishlist.model.js)
- **Collection name**: `wishlists`
- **Module/domain**: Cart
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| customer_id | `ObjectId` | Yes | No | `-` | - | **Customer** | No | - | Field mapping for customer_id |
| wishlistName | `String` | No | No | `My Wishlist` | - | - | No | - | Field mapping for wishlistName |
| isDefault | `Boolean` | No | No | `false` | - | - | No | - | Field mapping for isDefault |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `customer_id` → **Customer** (Many-to-One): Identifies the related Customer model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `customer_id` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Naming Inconsistency: Model mixes camelCase (e.g. `wishlistName`) and snake_case (e.g. `customer_id`) field casing.

---

### 3.69 WishlistItem

#### Basic Information
- **Model name**: WishlistItem
- **File path**: [`models/wishlistItem.model.js`](file:///d:/KanilaApp/backend/models/wishlistItem.model.js)
- **Collection name**: `wishlistitems`
- **Module/domain**: Cart
- **Schema options**: `{"timestamps":true,"versionKey":"__v"}`
- **Timestamps**: Yes
- **Version key**: Yes (`__v`)
- **Collection naming source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation | Description |
|---|---|---|---|---|---|---|---|---|---|
| wishlistId | `ObjectId` | Yes | No | `-` | - | **Wishlist** | No | - | Field mapping for wishlistId |
| productId | `ObjectId` | Yes | No | `-` | - | **Product** | No | - | Field mapping for productId |
| variantId | `ObjectId` | No | No | `null` | - | **ProductVariant** | No | - | Field mapping for variantId |
| _id | `ObjectId` | No | No | `-` | - | - | No | - | Field mapping for _id |
| createdAt | `Date` | No | No | `-` | - | - | No | `immutable` | Field mapping for createdAt |
| updatedAt | `Date` | No | No | `-` | - | - | No | - | Field mapping for updatedAt |
| __v | `Number` | No | No | `-` | - | - | No | - | Field mapping for __v |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- `wishlistId` → **Wishlist** (Many-to-One): Identifies the related Wishlist model.
- `productId` → **Product** (Many-to-One): Identifies the related Product model.
- `variantId` → **ProductVariant** (Many-to-One): Identifies the related ProductVariant model.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
No schema plugins found.

#### Notes / Risks
- **[Risk/Note]** Performance Risk: Reference field `wishlistId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `productId` is not indexed. Querying or populating this relationship will require a collection scan.
- **[Risk/Note]** Performance Risk: Reference field `variantId` is not indexed. Querying or populating this relationship will require a collection scan.

---

## 4. Cross-Model Relationship Map
The table below represents the relationships and entity mappings between backend models:

| Source Model | Field | Target Model | Relationship Type | Business Meaning |
|---|---|---|---|---|
| AccountAuthProvider | `account_id` | **Account** | Many-to-One | Identifies the related Account model. |
| AccountRole | `account_id` | **Account** | Many-to-One | Identifies the related Account model. |
| AccountRole | `role_id` | **Role** | Many-to-One | Identifies the related Role model. |
| AccountRole | `assigned_by_account_id` | **Account** | Many-to-One | Identifies the related Account model. |
| Address | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| AdminProfile | `account_id` | **Account** | One-to-One | Identifies the related Account model. |
| AdminProfile | `manager_account_id` | **Account** | Many-to-One | Identifies the related Account model. |
| AuditLog | `actor_account_id` | **Account** | Many-to-One | Identifies the related Account model. |
| Cart | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| CartItem | `product_id` | **Product** | Many-to-One | Identifies the related Product model. |
| CartItem | `cart_id` | **Cart** | Many-to-One | Identifies the related Cart model. |
| CartItem | `variant_id` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| Category | `parentCategoryId` | **Category** | Many-to-One | Identifies the related Category model. |
| CheckoutAddress | `checkout_session_id` | **CheckoutSession** | Many-to-One | Identifies the related CheckoutSession model. |
| CheckoutSession | `cart_id` | **Cart** | Many-to-One | Identifies the related Cart model. |
| CheckoutSession | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| CheckoutSession | `selected_shipping_address_id` | **CheckoutAddress** | Many-to-One | Identifies the related CheckoutAddress model. |
| CheckoutSession | `selected_billing_address_id` | **CheckoutAddress** | Many-to-One | Identifies the related CheckoutAddress model. |
| CheckoutSession | `selected_shipping_method_id` | **CheckoutShippingMethod** | Many-to-One | Identifies the related CheckoutShippingMethod model. |
| CheckoutSession | `selected_payment_method_id` | **PaymentMethod** | Many-to-One | Identifies the related PaymentMethod model. |
| CheckoutSession | `applied_coupon_id` | **Coupon** | Many-to-One | Identifies the related Coupon model. |
| CheckoutShippingMethod | `checkout_session_id` | **CheckoutSession** | Many-to-One | Identifies the related CheckoutSession model. |
| CheckoutShippingMethod | `shipping_method_id` | **ShippingMethod** | Many-to-One | Identifies the related ShippingMethod model. |
| Coupon | `promotionId` | **Promotion** | Many-to-One | Identifies the related Promotion model. |
| CouponRedemption | `couponId` | **Coupon** | Many-to-One | Identifies the related Coupon model. |
| CouponRedemption | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| CouponRedemption | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| Customer | `account_id` | **Account** | One-to-One | Identifies the related Account model. |
| CustomerConsent | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| CustomerCoupon | `couponId` | **Coupon** | Many-to-One | Identifies the related Coupon model. |
| CustomerCoupon | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| CustomerPreference | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| CustomerRecommendationSnapshot | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| InventoryBalance | `warehouseId` | **Warehouse** | Many-to-One | Identifies the related Warehouse model. |
| InventoryBalance | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| InventoryTransaction | `warehouseId` | **Warehouse** | Many-to-One | Identifies the related Warehouse model. |
| InventoryTransaction | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| InventoryTransaction | `performedByAccountId` | **Account** | Many-to-One | Identifies the related Account model. |
| LoyaltyAccount | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| LoyaltyAccount | `tierId` | **LoyaltyTier** | Many-to-One | Identifies the related LoyaltyTier model. |
| LoyaltyPointLedger | `loyaltyAccountId` | **LoyaltyAccount** | Many-to-One | Identifies the related LoyaltyAccount model. |
| LoyaltyPointLedger | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| LoyaltyPointLedger | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| Order | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| Order | `checkout_session_id` | **CheckoutSession** | Many-to-One | Identifies the related CheckoutSession model. |
| OrderAddress | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| OrderItem | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| OrderItem | `product_id` | **Product** | Many-to-One | Identifies the related Product model. |
| OrderItem | `variant_id` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| OrderStatusHistory | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| OrderStatusHistory | `changed_by_account_id` | **Account** | Many-to-One | Identifies the related Account model. |
| OrderTotal | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| PaymentIntent | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| PaymentIntent | `payment_method_id` | **PaymentMethod** | Many-to-One | Identifies the related PaymentMethod model. |
| PaymentTransaction | `paymentIntentId` | **PaymentIntent** | Many-to-One | Identifies the related PaymentIntent model. |
| PaymentTransaction | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| PriceBookEntry | `priceBookId` | **PriceBook** | Many-to-One | Identifies the related PriceBook model. |
| PriceBookEntry | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| PriceHistory | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| PriceHistory | `priceBookId` | **PriceBook** | Many-to-One | Identifies the related PriceBook model. |
| PriceHistory | `changedByAccountId` | **Account** | Many-to-One | Identifies the related Account model. |
| Product | `brandId` | **Brand** | Many-to-One | Identifies the related Brand model. |
| Product | `categoryId` | **Category** | Many-to-One | Primary category (maps to primary_category_id). |
| Product | `createdByAccountId` | **Account** | Many-to-One | Optional audit refs — must exist on schema for `.populate()` in getProductById. |
| Product | `updatedByAccountId` | **Account** | Many-to-One | Identifies the related Account model. |
| ProductAttribute | `productId` | **Product** | Many-to-One | Identifies the related Product model. |
| ProductCategory | `productId` | **Product** | Many-to-One | Identifies the related Product model. |
| ProductCategory | `categoryId` | **Category** | Many-to-One | Identifies the related Category model. |
| ProductMedia | `productId` | **Product** | Many-to-One | Identifies the related Product model. |
| ProductOption | `productId` | **Product** | Many-to-One | Identifies the related Product model. |
| ProductOptionValue | `productOptionId` | **ProductOption** | Many-to-One | Identifies the related ProductOption model. |
| ProductVariant | `productId` | **Product** | Many-to-One | Identifies the related Product model. |
| Promotion | `createdByAccountId` | **Account** | Many-to-One | Identifies the related Account model. |
| PromotionRule | `promotionId` | **Promotion** | Many-to-One | Identifies the related Promotion model. |
| PromotionTarget | `promotionId` | **Promotion** | Many-to-One | Identifies the related Promotion model. |
| RecommendationLog | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| RecommendationLog | `product_id` | **Product** | Many-to-One | Identifies the related Product model. |
| Refund | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| Refund | `paymentTransactionId` | **PaymentTransaction** | Many-to-One | Identifies the related PaymentTransaction model. |
| Refund | `requestedByAccountId` | **Account** | Many-to-One | Identifies the related Account model. |
| Refund | `approvedByAccountId` | **Account** | Many-to-One | Identifies the related Account model. |
| Return | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| Return | `shipmentId` | **Shipment** | Many-to-One | Identifies the related Shipment model. |
| Return | `requested_by_customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| Return | `approvedByAccountId` | **Account** | Many-to-One | Identifies the related Account model. |
| ReturnItem | `returnId` | **Return** | Many-to-One | Identifies the related Return model. |
| ReturnItem | `orderItemId` | **OrderItem** | Many-to-One | Identifies the related OrderItem model. |
| ReturnItem | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| Review | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| Review | `orderItemId` | **OrderItem** | Many-to-One | Identifies the related OrderItem model. |
| Review | `productId` | **Product** | Many-to-One | Identifies the related Product model. |
| Review | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| ReviewMedia | `reviewId` | **Review** | Many-to-One | Identifies the related Review model. |
| ReviewSummary | `productId` | **Product** | One-to-One | Identifies the related Product model. |
| ReviewVote | `reviewId` | **Review** | Many-to-One | Identifies the related Review model. |
| ReviewVote | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| RolePermission | `role_id` | **Role** | Many-to-One | Identifies the related Role model. |
| RolePermission | `permission_id` | **Permission** | Many-to-One | Identifies the related Permission model. |
| Shipment | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| Shipment | `warehouseId` | **Warehouse** | Many-to-One | Identifies the related Warehouse model. |
| ShipmentEvent | `shipmentId` | **Shipment** | Many-to-One | Identifies the related Shipment model. |
| ShipmentItem | `shipmentId` | **Shipment** | Many-to-One | Identifies the related Shipment model. |
| ShipmentItem | `orderItemId` | **OrderItem** | Many-to-One | Identifies the related OrderItem model. |
| ShipmentItem | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| StockReservation | `warehouseId` | **Warehouse** | Many-to-One | Identifies the related Warehouse model. |
| StockReservation | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| StockReservation | `cart_id` | **Cart** | Many-to-One | Identifies the related Cart model. |
| StockReservation | `checkout_session_id` | **CheckoutSession** | Many-to-One | Identifies the related CheckoutSession model. |
| StockReservation | `order_id` | **Order** | Many-to-One | Identifies the related Order model. |
| VariantMedia | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| VariantOptionValue | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |
| VariantOptionValue | `productOptionValueId` | **ProductOptionValue** | Many-to-One | Identifies the related ProductOptionValue model. |
| Wishlist | `customer_id` | **Customer** | Many-to-One | Identifies the related Customer model. |
| WishlistItem | `wishlistId` | **Wishlist** | Many-to-One | Identifies the related Wishlist model. |
| WishlistItem | `productId` | **Product** | Many-to-One | Identifies the related Product model. |
| WishlistItem | `variantId` | **ProductVariant** | Many-to-One | Identifies the related ProductVariant model. |

---

## 5. Collection Naming Check
Below is the audit checklist comparing expected vs actual MongoDB collection names. Mongoose default pluralization resolves lowercased models to standard plurals (e.g. `Product` -> `products`).

| Model Name | Expected Collection Name | Actual Collection Name | Explicit or Inferred | Naming Status | Recommendation |
|---|---|---|---|---|---|
| Account | `accounts` | `accounts` | Explicit | Explicit (OK) | No action needed. |
| AccountAuthProvider | `accountauthproviders` | `account_auth_providers` | Explicit | Explicit Custom | Uses custom collection name `account_auth_providers` instead of default plural `accountauthproviders`. OK (Relational structure mapping). |
| AccountRole | `accountroles` | `account_roles` | Explicit | Explicit Custom | Uses custom collection name `account_roles` instead of default plural `accountroles`. OK (Relational structure mapping). |
| Address | `addresses` | `customer_addresses` | Explicit | Explicit Custom | Uses custom collection name `customer_addresses` instead of default plural `addresses`. OK (Relational structure mapping). |
| AdminProfile | `adminprofiles` | `admin_profiles` | Explicit | Explicit Custom | Uses custom collection name `admin_profiles` instead of default plural `adminprofiles`. OK (Relational structure mapping). |
| AuditLog | `auditlogs` | `audit_logs` | Explicit | Explicit Custom | Uses custom collection name `audit_logs` instead of default plural `auditlogs`. OK (Relational structure mapping). |
| Brand | `brands` | `brands` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `brands` exists in database. |
| Cart | `carts` | `carts` | Explicit | Explicit (OK) | No action needed. |
| CartItem | `cartitems` | `cart_items` | Explicit | Explicit Custom | Uses custom collection name `cart_items` instead of default plural `cartitems`. OK (Relational structure mapping). |
| Category | `categories` | `categories` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `categories` exists in database. |
| CheckoutAddress | `checkoutaddresses` | `checkout_addresses` | Explicit | Explicit Custom | Uses custom collection name `checkout_addresses` instead of default plural `checkoutaddresses`. OK (Relational structure mapping). |
| CheckoutSession | `checkoutsessions` | `checkout_sessions` | Explicit | Explicit Custom | Uses custom collection name `checkout_sessions` instead of default plural `checkoutsessions`. OK (Relational structure mapping). |
| CheckoutShippingMethod | `checkoutshippingmethods` | `checkout_shipping_methods` | Explicit | Explicit Custom | Uses custom collection name `checkout_shipping_methods` instead of default plural `checkoutshippingmethods`. OK (Relational structure mapping). |
| Coupon | `coupons` | `coupons` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `coupons` exists in database. |
| CouponRedemption | `couponredemptions` | `couponredemptions` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `couponredemptions` exists in database. |
| Customer | `customers` | `customer_profiles` | Explicit | Explicit Custom | Uses custom collection name `customer_profiles` instead of default plural `customers`. OK (Relational structure mapping). |
| CustomerConsent | `customerconsents` | `customer_consents` | Explicit | Explicit Custom | Uses custom collection name `customer_consents` instead of default plural `customerconsents`. OK (Relational structure mapping). |
| CustomerCoupon | `customercoupons` | `customer_coupons` | Explicit | Explicit Custom | Uses custom collection name `customer_coupons` instead of default plural `customercoupons`. OK (Relational structure mapping). |
| CustomerPreference | `customerpreferences` | `customer_preferences` | Explicit | Explicit Custom | Uses custom collection name `customer_preferences` instead of default plural `customerpreferences`. OK (Relational structure mapping). |
| CustomerRecommendationSnapshot | `customerrecommendationsnapshots` | `customer_recommendation_snapshots` | Explicit | Explicit Custom | Uses custom collection name `customer_recommendation_snapshots` instead of default plural `customerrecommendationsnapshots`. OK (Relational structure mapping). |
| GuestSession | `guestsessions` | `guest_sessions` | Explicit | Explicit Custom | Uses custom collection name `guest_sessions` instead of default plural `guestsessions`. OK (Relational structure mapping). |
| InventoryBalance | `inventorybalances` | `inventorybalances` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `inventorybalances` exists in database. |
| InventoryTransaction | `inventorytransactions` | `inventorytransactions` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `inventorytransactions` exists in database. |
| LoyaltyAccount | `loyaltyaccounts` | `loyaltyaccounts` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `loyaltyaccounts` exists in database. |
| LoyaltyPointLedger | `loyaltypointledgers` | `loyaltypointledgers` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `loyaltypointledgers` exists in database. |
| LoyaltyTier | `loyaltytiers` | `loyaltytiers` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `loyaltytiers` exists in database. |
| Order | `orders` | `orders` | Explicit | Explicit (OK) | No action needed. |
| OrderAddress | `orderaddresses` | `order_addresses` | Explicit | Explicit Custom | Uses custom collection name `order_addresses` instead of default plural `orderaddresses`. OK (Relational structure mapping). |
| OrderItem | `orderitems` | `order_items` | Explicit | Explicit Custom | Uses custom collection name `order_items` instead of default plural `orderitems`. OK (Relational structure mapping). |
| OrderStatusHistory | `orderstatushistories` | `order_status_history` | Explicit | Explicit Custom | Uses custom collection name `order_status_history` instead of default plural `orderstatushistories`. OK (Relational structure mapping). |
| OrderTotal | `ordertotals` | `order_totals` | Explicit | Explicit Custom | Uses custom collection name `order_totals` instead of default plural `ordertotals`. OK (Relational structure mapping). |
| PasswordResetOtp | `passwordresetotps` | `password_reset_otps` | Explicit | Explicit Custom | Uses custom collection name `password_reset_otps` instead of default plural `passwordresetotps`. OK (Relational structure mapping). |
| PaymentIntent | `paymentintents` | `paymentintents` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `paymentintents` exists in database. |
| PaymentMethod | `paymentmethods` | `payment_methods` | Explicit | Explicit Custom | Uses custom collection name `payment_methods` instead of default plural `paymentmethods`. OK (Relational structure mapping). |
| PaymentTransaction | `paymenttransactions` | `paymenttransactions` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `paymenttransactions` exists in database. |
| Permission | `permissions` | `permissions` | Explicit | Explicit (OK) | No action needed. |
| PriceBook | `pricebooks` | `pricebooks` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `pricebooks` exists in database. |
| PriceBookEntry | `pricebookentries` | `pricebookentries` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `pricebookentries` exists in database. |
| PriceHistory | `pricehistories` | `pricehistories` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `pricehistories` exists in database. |
| Product | `products` | `products` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `products` exists in database. |
| ProductAttribute | `productattributes` | `productattributes` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `productattributes` exists in database. |
| ProductCategory | `productcategories` | `productcategories` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `productcategories` exists in database. |
| ProductMedia | `productmedias` | `productmedias` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `productmedias` exists in database. |
| ProductOption | `productoptions` | `productoptions` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `productoptions` exists in database. |
| ProductOptionValue | `productoptionvalues` | `productoptionvalues` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `productoptionvalues` exists in database. |
| ProductVariant | `productvariants` | `productvariants` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `productvariants` exists in database. |
| Promotion | `promotions` | `promotions` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `promotions` exists in database. |
| PromotionRule | `promotionrules` | `promotionrules` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `promotionrules` exists in database. |
| PromotionTarget | `promotiontargets` | `promotiontargets` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `promotiontargets` exists in database. |
| RecommendationLog | `recommendationlogs` | `recommendation_logs` | Explicit | Explicit Custom | Uses custom collection name `recommendation_logs` instead of default plural `recommendationlogs`. OK (Relational structure mapping). |
| Refund | `refunds` | `refunds` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `refunds` exists in database. |
| Return | `returns` | `returns` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `returns` exists in database. |
| ReturnItem | `returnitems` | `returnitems` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `returnitems` exists in database. |
| Review | `reviews` | `reviews` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `reviews` exists in database. |
| ReviewMedia | `reviewmedias` | `reviewmedias` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `reviewmedias` exists in database. |
| ReviewSummary | `reviewsummaries` | `reviewsummaries` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `reviewsummaries` exists in database. |
| ReviewVote | `reviewvotes` | `reviewvotes` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `reviewvotes` exists in database. |
| Role | `roles` | `roles` | Explicit | Explicit (OK) | No action needed. |
| RolePermission | `rolepermissions` | `role_permissions` | Explicit | Explicit Custom | Uses custom collection name `role_permissions` instead of default plural `rolepermissions`. OK (Relational structure mapping). |
| Shipment | `shipments` | `shipments` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `shipments` exists in database. |
| ShipmentEvent | `shipmentevents` | `shipmentevents` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `shipmentevents` exists in database. |
| ShipmentItem | `shipmentitems` | `shipmentitems` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `shipmentitems` exists in database. |
| ShippingMethod | `shippingmethods` | `shipping_methods` | Explicit | Explicit Custom | Uses custom collection name `shipping_methods` instead of default plural `shippingmethods`. OK (Relational structure mapping). |
| StockReservation | `stockreservations` | `stockreservations` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `stockreservations` exists in database. |
| VariantMedia | `variantmedias` | `variantmedias` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `variantmedias` exists in database. |
| VariantOptionValue | `variantoptionvalues` | `variantoptionvalues` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `variantoptionvalues` exists in database. |
| Warehouse | `warehouses` | `warehouses` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `warehouses` exists in database. |
| Wishlist | `wishlists` | `wishlists` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `wishlists` exists in database. |
| WishlistItem | `wishlistitems` | `wishlistitems` | Inferred | Inferred by Mongoose | Relies on Mongoose default plural. Confirm if `wishlistitems` exists in database. |

---

## 6. Field Naming Consistency Check
An audit of field casing across all models reveals inconsistencies between relational-like schema designs (using `snake_case`) and application-like schema designs (using `camelCase`).

| Issue | Models Affected | Description | Recommendation |
|---|---|---|---|
| Mixed Casing within Single Model | `Product` | Fields like `productName` and `productCode` are in camelCase, whereas `skin_types_supported`, `concerns_targeted`, `ingredient_flags` are in snake_case. | Recommend unifying casing conventions to either camelCase or snake_case to avoid confusion. |
| Mixed Naming Style between Models | Most Models (e.g., `Account` vs `Product`) | Models representing relational tables (e.g., `Account`, `Customer`, `Order`, `Cart`) use snake_case for fields (e.g. `created_at`, `customer_id`). Product and Pricebook modules use camelCase (e.g. `compareAtPrice`, `categoryId`). | Document casing mapping carefully in API and frontend layers (e.g. Android models vs backend API payloads). |
| ID Field Mapping Variations | `Account`, `Customer`, `Order`, `Cart` etc. | Virtuals like `account_id`, `customer_id`, `order_id` map to `_id` to bridge relational logic. Some models use camelCase virtuals or raw MongoDB `_id`. | Standardize how identifiers are serialized/deserialized in the API gateway. |
| Timestamp Field Inconsistencies | All Models | Models like `Product` use standard `createdAt`/`updatedAt` (timestamps: true), whereas relational models like `Account` redefine them to `created_at`/`updated_at`. | Pay special attention when writing automated database triggers or queries. |

---

## 7. Recommended Final Entity Dictionary
The following dictionary serves as a unified reference for the mobile frontend and backend engineering teams.

### 7.1 Core Entities
1. **Account**
   - **Collection Name**: `accounts`
   - **Main ID**: `_id` (virtual `account_id`)
   - **Display Field**: `email` / `username`
   - **Status Field**: `account_status` (`active`, `inactive`, `locked`)
   - **Important Relationships**: None (Self-contained credentials)
   - **Frontend Screens**: Login, Registration, Password Reset, Profile Management
2. **Customer**
   - **Collection Name**: `customer_profiles`
   - **Main ID**: `_id` (virtual `customer_id`)
   - **Display Field**: `full_name`
   - **Status Field**: `customer_status` (`active`, `inactive`)
   - **Important Relationships**: `account_id` → **Account**
   - **Frontend Screens**: Account Dashboard, Profile, Loyalty, Settings
3. **Product**
   - **Collection Name**: `products`
   - **Main ID**: `_id`
   - **Display Field**: `productName`
   - **Status Field**: `productStatus` (`active`, `inactive`) / `isActive` (boolean)
   - **Important Relationships**: `brandId` → **Brand**, `categoryId` → **Category**
   - **Frontend Screens**: Homepage, Catalog Listing, Product Detail, Search Results, Skin Match Results
4. **Cart**
   - **Collection Name**: `carts`
   - **Main ID**: `_id` (virtual `cart_id`)
   - **Display Field**: `total_amount`
   - **Status Field**: `cart_status` (`active`, `converted`, `expired`, `merged`)
   - **Important Relationships**: `customer_id` → **Customer** (optional)
   - **Frontend Screens**: Shopping Cart, Drawer
5. **Order**
   - **Collection Name**: `orders`
   - **Main ID**: `_id` (virtual `order_id`)
   - **Display Field**: `order_number`
   - **Status Field**: `order_status` (`pending`, `confirmed`, `processing`, `completed`, `cancelled`, `returned`)
   - **Important Relationships**: `customer_id` → **Customer**, `checkout_session_id` → **CheckoutSession**
   - **Frontend Screens**: Order History, Order Success, Tracking Details

---

## 8. Backend-to-Frontend Notes
This guide lists model mapping directories specifically tailored for Android (Java/XML) development teams:

- **Authentication Module**:
  - Main models: **Account**, **AccountRole**, **Role**, **PasswordResetOtp**, **AccountAuthProvider**
  - Android notes: Serialized responses contain `email` and `username`. Response payloads match snake_case variables. Use `email_verified_at` to check activation status.
- **Product Listing / Catalog**:
  - Main models: **Product**, **Brand**, **Category**, **ProductCategory**, **ProductMedia**
  - Android notes: Note that `Product` fields like `price`, `compareAtPrice`, and `bought` are camelCase, but fields like `skin_types_supported` are snake_case. Populate nested `shades` array to show shade swatches on listings.
- **Product Details & Reviews**:
  - Main models: **Product**, **ProductVariant**, **ProductOption**, **ProductOptionValue**, **Review**, **ReviewMedia**, **ReviewSummary**, **ReviewVote**
  - Android notes: Show rating counts from `ReviewSummary` in detail views. Load shades and variant specifications from `ProductVariant` mapping.
- **Cart Management**:
  - Main models: **Cart**, **CartItem**, **Wishlist**, **WishlistItem**, **GuestSession**
  - Android notes: Carts utilize guest sessions (`guest_session_id`) for unauthenticated carts. Ensure cart merges are triggered on login by calling the merge API.
- **Checkout & Shipping**:
  - Main models: **CheckoutSession**, **CheckoutAddress**, **CheckoutShippingMethod**, **ShippingMethod**, **StockReservation**
  - Android notes: During active checkout checkout sessions, temp stocks are locked via **StockReservation** (TTL indices clear abandoned holds). Use VND currency formatting by default (`currency_code`).
- **Orders & Tracking**:
  - Main models: **Order**, **OrderItem**, **OrderAddress**, **OrderTotal**, **OrderStatusHistory**, **Shipment**, **ShipmentEvent**, **ShipmentItem**
  - Android notes: Order number is unique uppercase string. Track transitions using **OrderStatusHistory** to construct timeline stepper views in the app.
- **Payments & Refunds / Returns**:
  - Main models: **PaymentIntent**, **PaymentMethod**, **PaymentTransaction**, **Return**, **ReturnItem**, **Refund**
  - Android notes: Save user card tokens mapped to **PaymentMethod** identifiers. Returns require selection of return reason and item quantity.
- **Community & Skincare Journey**:
  - Main models: **CustomerPreference**, **CustomerRecommendationSnapshot**, **RecommendationLog**
  - Android notes: Preference settings map to targeted products. Snapshots save results generated from the Skin Match questionnaire.

---

## 9. Final Checklist
- [x] Every model has a clear collection name
- [x] Every required field is documented
- [x] Every enum is documented
- [x] Every relationship is documented
- [x] Every index is documented
- [x] Sensitive fields are identified (e.g., password_hash, token, otp)
- [x] Frontend-critical fields are identified
- [x] Potential naming inconsistencies are listed
