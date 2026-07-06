# Kanila Backend Models Report

## 1. Overview
This report provides a detailed documentation of the MongoDB collections and Mongoose schemas used in the **Kanila Beauty Commerce** backend.

- **Total Mongoose Models**: 72 active models (with 1 additional deprecated shim file)
- **Total MongoDB Collections**: 72
- **Backend Tech Stack**: Node.js, Express.js, MongoDB Atlas, Mongoose
- **Collection Naming**: 
  - **Explicitly defined**: 60 collections (using the schema `{ collection: "..." }` option)
  - **Mongoose inferred (default pluralization)**: 12 collections

The backend relies heavily on custom collection mappings (mostly utilizing snake_case plurals) to override default Mongoose CamelCase-to-lowercase-plural rules.

---

## 2. Model Summary Table
The table below lists all Mongoose models detected in the codebase:

| No. | Model Name | File Path | MongoDB Collection Name | Module / Domain | Has Timestamps | Main Purpose |
|---|---|---|---|---|---|---|
| 1 | [Account](#3account) | [account.model.js](file:///models/account.model.js) | `accounts` | Auth | Yes (`created_at`/`updated_at`) | Stores core account credentials and authentication status. |
| 2 | [AccountAuthProvider](#3accountauthprovider) | [accountAuthProvider.model.js](file:///models/accountAuthProvider.model.js) | `account_auth_providers` | Auth | Yes (`created_at`/`updatedAt`) | Links account with external OAuth providers (Google, Apple, etc.). |
| 3 | [AccountRole](#3accountrole) | [accountRole.model.js](file:///models/accountRole.model.js) | `account_roles` | Auth | No | Maps accounts to roles. |
| 4 | [Address](#3address) | [address.model.js](file:///models/address.model.js) | `customer_addresses` | Customers | Yes (`created_at`/`updated_at`) | Stores customer shipping and billing addresses. |
| 5 | [AdminProfile](#3adminprofile) | [adminProfile.model.js](file:///models/adminProfile.model.js) | `admin_profiles` | Auth/Admin | Yes (`created_at`/`updated_at`) | Stores profile information for admin and staff users. |
| 6 | [AuditLog](#3auditlog) | [auditLog.model.js](file:///models/auditLog.model.js) | `audit_logs` | System | No | Tracks administrative actions and system audits. |
| 7 | [BeautyReference](#3beautyreference) | [beautyReference.model.js](file:///models/beautyReference.model.js) | `beauty_references` | Skin Journey | Yes (`created_at`/`updated_at`) | Defines reference data for skin types, concerns, and skin tones. |
| 8 | [Brand](#3brand) | [brand.model.js](file:///models/brand.model.js) | `brands` | Products | Yes (`createdAt`/`updatedAt`) | Stores product brand details. |
| 9 | [Cart](#3cart) | [cart.model.js](file:///models/cart.model.js) | `carts` | Cart | Yes (`created_at`/`updated_at`) | Stores customer cart sessions. |
| 10 | [CartItem](#3cartitem) | [cartItem.model.js](file:///models/cartItem.model.js) | `cart_items` | Cart | Yes (`createdAt`/`updated_at`) | Stores items added to a cart. |
| 11 | [Category](#3category) | [category.model.js](file:///models/category.model.js) | `categories` | Categories | Yes (`createdAt`/`updatedAt`) | Stores product categories. |
| 12 | [CheckoutAddress](#3checkoutaddress) | [checkoutAddress.model.js](file:///models/checkoutAddress.model.js) | `checkout_addresses` | Checkout | Yes (`created_at`/`updated_at`) | Stores addresses during a checkout session. |
| 13 | [CheckoutSession](#3checkoutsession) | [checkoutSession.model.js](file:///models/checkoutSession.model.js) | `checkout_sessions` | Checkout | Yes (`created_at`/`updated_at`) | Tracks checkout steps, totals, and session state. |
| 14 | [CheckoutShippingMethod](#3checkoutshippingmethod) | [checkoutShippingMethod.model.js](file:///models/checkoutShippingMethod.model.js) | `checkout_shipping_methods` | Checkout | No | Stores shipping method selection for checkout. |
| 15 | [Coupon](#3coupon) | [coupon.model.js](file:///models/coupon.model.js) | `coupons` | Promotions | Yes (`createdAt`/`updatedAt`) | Stores discount coupon definitions. |
| 16 | [CouponRedemption](#3couponredemption) | [couponRedemption.model.js](file:///models/couponRedemption.model.js) | `coupon_redemptions` | Promotions | Yes (`createdAt`/`updatedAt`) | Tracks usage of coupons by customers. |
| 17 | [Customer](#3customer) | [customer.model.js](file:///models/customer.model.js) | `customer_profiles` | Customers | Yes (`created_at`/`updated_at`) | Stores customer profiles. |
| 18 | [CustomerBeautyProfile](#3customerbeautyprofile) | [customerBeautyProfile.model.js](file:///models/customerBeautyProfile.model.js) | `customer_beauty_profiles` | Skin Journey | Yes (`created_at`/`updated_at`) | Stores skin journey / skin profiles of customers. |
| 19 | [CustomerConsent](#3customerconsent) | [customerConsent.model.js](file:///models/customerConsent.model.js) | `customer_consents` | Customers | No | Tracks customer policy and marketing consents. |
| 20 | [CustomerCoupon](#3customercoupon) | [customerCoupon.model.js](file:///models/customerCoupon.model.js) | `customer_coupons` | Promotions | Yes (`createdAt`/`updatedAt`) | Links customers to available coupons. |
| 21 | [CustomerPreference](#3customerpreference) | [customerPreference.model.js](file:///models/customerPreference.model.js) | `customer_preferences` | Customers | No | Stores customer preferences. |
| 22 | [CustomerRecommendationSnapshot](#3customerrecommendationsnapshot) | [customerRecommendationSnapshot.model.js](file:///models/customerRecommendationSnapshot.model.js) | `customer_recommendation_snapshots` | Skin Journey | Yes (`createdAt`/`updatedAt`) | Stores product recommendation snapshots for a skin profile. |
| 23 | [EmailOtp](#3emailotp) | [emailOtp.model.js](file:///models/emailOtp.model.js) | `email_otps` | Auth | Yes (`created_at`/`updated_at`) | Stores email OTP codes for passwordless authentication. |
| 24 | [GuestSession](#3guestsession) | [guestSession.model.js](file:///models/guestSession.model.js) | `guest_sessions` | Customers | Yes (`created_at`/`updated_at`) | Stores session data for guest users. |
| 25 | [InventoryBalance](#3inventorybalance) | [inventoryBalance.model.js](file:///models/inventoryBalance.model.js) | `inventory_balances` | Inventory | Yes (`createdAt`/`updatedAt`) | Stores current stock levels for product variants in warehouses. |
| 26 | [InventoryTransaction](#3inventorytransaction) | [inventoryTransaction.model.js](file:///models/inventoryTransaction.model.js) | `inventory_transactions` | Inventory | Yes (`createdAt`/`updatedAt`) | Tracks stock adjustments and movements. |
| 27 | [LoyaltyAccount](#3loyaltyaccount) | [loyaltyAccount.model.js](file:///models/loyaltyAccount.model.js) | `loyalty_accounts` | Loyalty | Yes (`createdAt`/`updatedAt`) | Stores customer loyalty points and current tier. |
| 28 | [LoyaltyPointLedger](#3loyaltypointledger) | [loyaltyPointLedger.model.js](file:///models/loyaltyPointLedger.model.js) | `loyalty_point_ledger` | Loyalty | Yes (`createdAt`/`updatedAt`) | Ledger tracking loyalty point earnings and spends. |
| 29 | [LoyaltyTier](#3loyaltytier) | [loyaltyTier.model.js](file:///models/loyaltyTier.model.js) | `loyalty_tiers` | Loyalty | Yes (`createdAt`/`updatedAt`) | Defines rules and benefits for loyalty tiers. |
| 30 | [Order](#3order) | [order.model.js](file:///models/order.model.js) | `orders` | Orders | Yes (`created_at`/`updated_at`) | Stores core order details. |
| 31 | [OrderAddress](#3orderaddress) | [orderAddress.model.js](file:///models/orderAddress.model.js) | `order_addresses` | Orders | No | Stores shipping/billing address used for an order. |
| 32 | [OrderItem](#3orderitem) | [orderItem.model.js](file:///models/orderItem.model.js) | `order_items` | Orders | Yes (`created_at`/`updated_at`) | Stores items purchased in an order. |
| 33 | [OrderStatusHistory](#3orderstatushistory) | [orderStatusHistory.model.js](file:///models/orderStatusHistory.model.js) | `order_status_history` | Orders | No | Tracks status transitions of an order. |
| 34 | [OrderTotal](#3ordertotal) | [orderTotal.model.js](file:///models/orderTotal.model.js) | `order_totals` | Orders | Yes (`created_at`/`updated_at`) | Stores detailed pricing breakdown of an order. |
| 35 | [PaymentIntent](#3paymentintent) | [paymentIntent.model.js](file:///models/paymentIntent.model.js) | `payment_intents` | Payments | Yes (`createdAt`/`updatedAt`) | Tracks external payment intent (e.g., Stripe/PayOS). |
| 36 | [PaymentMethod](#3paymentmethod) | [paymentMethod.model.js](file:///models/paymentMethod.model.js) | `payment_methods` | Payments | Yes (`created_at`/`updated_at`) | Stores saved payment methods for accounts. |
| 37 | [PaymentTransaction](#3paymenttransaction) | [paymentTransaction.model.js](file:///models/paymentTransaction.model.js) | `payment_transactions` | Payments | Yes (`createdAt`/`updatedAt`) | Tracks payment transitions. |
| 38 | [Permission](#3permission) | [permission.model.js](file:///models/permission.model.js) | `permissions` | Auth | No | Stores RBAC permission codes. |
| 39 | [PriceBook](#3pricebook) | [priceBook.model.js](file:///models/priceBook.model.js) | `price_books` | Pricing | Yes (`createdAt`/`updatedAt`) | Defines price books (e.g., retail, promo). |
| 40 | [PriceBookEntry](#3pricebookentry) | [priceBookEntry.model.js](file:///models/priceBookEntry.model.js) | `price_book_entries` | Pricing | Yes (`createdAt`/`updatedAt`) | Maps product variants to prices inside a price book. |
| 41 | [PriceHistory](#3pricehistory) | [priceHistory.model.js](file:///models/priceHistory.model.js) | `price_histories` | Pricing | Yes (`createdAt`/`updatedAt`) | Tracks product pricing histories. |
| 42 | [Product](#3product) | [product.model.js](file:///models/product.model.js) | `products` | Products | Yes (`createdAt`/`updatedAt`) | Stores core product metadata. |
| 43 | [ProductAttribute](#3productattribute) | [productAttribute.model.js](file:///models/productAttribute.model.js) | `product_attributes` | Products | Yes (`createdAt`/`updatedAt`) | Stores custom attributes for products. |
| 44 | [ProductBeautyProfile](#3productbeautyprofile) | [productBeautyProfile.model.js](file:///models/productBeautyProfile.model.js) | `product_beauty_profiles` | Skin Journey | Yes (`created_at`/`updated_at`) | Links products to matching skin concerns/types. |
| 45 | [ProductCategory](#3productcategory) | [productCategory.model.js](file:///models/productCategory.model.js) | `product_categories` | Categories | Yes (`createdAt`/`updatedAt`) | Maps products to categories. |
| 46 | [ProductMedia](#3productmedia) | [productMedia.model.js](file:///models/productMedia.model.js) | `product_media` | Products | Yes (`createdAt`/`updatedAt`) | Stores media links (images/videos) for products. |
| 47 | [ProductOption](#3productoption) | [productOption.model.js](file:///models/productOption.model.js) | `product_options` | Products | Yes (`createdAt`/`updatedAt`) | Stores product options (e.g., size, color). |
| 48 | [ProductOptionValue](#3productoptionvalue) | [productOptionValue.model.js](file:///models/productOptionValue.model.js) | `product_option_values` | Products | Yes (`createdAt`/`updatedAt`) | Stores option values for product options. |
| 49 | [ProductVariant](#3productvariant) | [productVariant.model.js](file:///models/productVariant.model.js) | `product_variants` | Products | Yes (`createdAt`/`updatedAt`) | Stores specific SKU variants for a product. |
| 50 | [Promotion](#3promotion) | [promotion.model.js](file:///models/promotion.model.js) | `promotions` | Promotions | Yes (`createdAt`/`updatedAt`) | Stores discount promotion settings. |
| 51 | [PromotionRule](#3promotionrule) | [promotionRule.model.js](file:///models/promotionRule.model.js) | `promotion_rules` | Promotions | Yes (`createdAt`/`updatedAt`) | Stores eligibility rules for promotions. |
| 52 | [PromotionTarget](#3promotiontarget) | [promotionTarget.model.js](file:///models/promotionTarget.model.js) | `promotion_targets` | Promotions | Yes (`createdAt`/`updatedAt`) | Stores target products/categories for promotions. |
| 53 | [RecommendationLog](#3recommendationlog) | [recommendation-log.model.js](file:///models/recommendation-log.model.js) | `recommendation_logs` | Skin Journey | Yes (`createdAt`/`updatedAt`) | Tracks skin journey recommendation engine runs. |
| 54 | [Refund](#3refund) | [refund.model.js](file:///models/refund.model.js) | `refunds` | Returns & Refunds | Yes (`createdAt`/`updatedAt`) | Stores refund details. |
| 55 | [Return](#3return) | [return.model.js](file:///models/return.model.js) | `returns` | Returns & Refunds | Yes (`createdAt`/`updatedAt`) | Stores order return requests. |
| 56 | [ReturnItem](#3returnitem) | [returnItem.model.js](file:///models/returnItem.model.js) | `returnitems` | Returns & Refunds | Yes (`createdAt`/`updatedAt`) | Stores individual items in a return request. |
| 57 | [Review](#3review) | [review.model.js](file:///models/review.model.js) | `reviews` | Reviews | Yes (`createdAt`/`updatedAt`) | Stores customer reviews for products. |
| 58 | [ReviewMedia](#3reviewmedia) | [reviewMedia.model.js](file:///models/reviewMedia.model.js) | `review_medias` | Reviews | Yes (`createdAt`/`updatedAt`) | Stores media attachments for reviews. |
| 59 | [ReviewSummary](#3reviewsummary) | [reviewSummary.model.js](file:///models/reviewSummary.model.js) | `review_summary` | Reviews | Yes (`createdAt`/`updatedAt`) | Stores aggregated review metrics per product. |
| 60 | [ReviewVote](#3reviewvote) | [reviewVote.model.js](file:///models/reviewVote.model.js) | `review_votes` | Reviews | Yes (`createdAt`/`updatedAt`) | Tracks helpfulness votes on product reviews. |
| 61 | [Role](#3role) | [role.model.js](file:///models/role.model.js) | `roles` | Auth | Yes (`created_at`/`updated_at`) | Stores RBAC role definitions. |
| 62 | [RolePermission](#3rolepermission) | [rolePermission.model.js](file:///models/rolePermission.model.js) | `role_permissions` | Auth | No | Maps RBAC roles to permissions. |
| 63 | [Shipment](#3shipment) | [shipment.model.js](file:///models/shipment.model.js) | `shipments` | Shipments | Yes (`createdAt`/`updatedAt`) | Stores shipment orders. |
| 64 | [ShipmentEvent](#3shipmentevent) | [shipmentEvent.model.js](file:///models/shipmentEvent.model.js) | `shipment_events` | Shipments | Yes (`createdAt`/`updatedAt`) | Tracks courier status updates for shipments. |
| 65 | [ShipmentItem](#3shipmentitem) | [shipmentItem.model.js](file:///models/shipmentItem.model.js) | `shipment_items` | Shipments | Yes (`createdAt`/`updatedAt`) | Stores items included in a shipment. |
| 66 | [ShippingMethod](#3shippingmethod) | [shippingMethod.model.js](file:///models/shippingMethod.model.js) | `shipping_methods` | Shipments | Yes (`created_at`/`updated_at`) | Stores configurations for shipping methods. |
| 67 | [StockReservation](#3stockreservation) | [stockReservation.model.js](file:///models/stockReservation.model.js) | `stock_reservations` | Checkout | Yes (`createdAt`/`updatedAt`) | Holds temporary inventory stock during checkout. |
| 68 | [VariantMedia](#3variantmedia) | [variantMedia.model.js](file:///models/variantMedia.model.js) | `variant_medias` | Products | Yes (`createdAt`/`updatedAt`) | Links media files to specific product variants. |
| 69 | [VariantOptionValue](#3variantoptionvalue) | [variantOptionValue.model.js](file:///models/variantOptionValue.model.js) | `variant_option_values` | Products | Yes (`createdAt`/`updatedAt`) | Maps product variants to their option values. |
| 70 | [Warehouse](#3warehouse) | [warehouse.model.js](file:///models/warehouse.model.js) | `warehouses` | Inventory | Yes (`createdAt`/`updatedAt`) | Stores warehouse locations. |
| 71 | [Wishlist](#3wishlist) | [wishlist.model.js](file:///models/wishlist.model.js) | `wishlists` | Wishlist | Yes (`createdAt`/`updatedAt`) | Stores customer wishlists. |
| 72 | [WishlistItem](#3wishlistitem) | [wishlistItem.model.js](file:///models/wishlistItem.model.js) | `wishlist_items` | Wishlist | Yes (`createdAt`/`updatedAt`) | Stores individual items in a wishlist. |

---

## 3. Detailed Model Documentation
This section contains field-by-field definitions, indexes, relationships, middleware, and virtuals for each Mongoose model.

### 3.1 Account

#### Basic Information
- **Model Name**: Account
- **File Path**: [models/account.model.js](file:///models/account.model.js)
- **Collection Name**: `accounts`
- **Module / Domain**: Auth
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `account_type` | `String` | Yes | No | `"customer"` | "customer", "admin", "staff" | N/A | No | None |  |
| `email` | `String` | Yes | No | None | N/A | N/A | No | trim, lowercase |  |
| `phone` | `String` | No | No | None | N/A | N/A | No | trim |  |
| `username` | `String` | No | No | None | N/A | N/A | No | trim |  |
| `account_status` | `String` | No | No | `"active"` | "active", "inactive", "locked" | N/A | No | None |  |
| `email_verified_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `last_login_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `failed_login_count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `locked_until` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"username":1}` | **Options**: `{"sparse":true,"background":true}`
- **Fields**: `{"email":1}` | **Options**: `{"unique":true,"background":true}`
- **Fields**: `{"username":1}` | **Options**: `{"unique":true,"sparse":true,"background":true}`
- **Fields**: `{"phone":1}` | **Options**: `{"unique":true,"partialFilterExpression":{"phone":{"$exists":true,"$gt":""}},"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `account_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.2 AccountAuthProvider

#### Basic Information
- **Model Name**: AccountAuthProvider
- **File Path**: [models/accountAuthProvider.model.js](file:///models/accountAuthProvider.model.js)
- **Collection Name**: `account_auth_providers`
- **Module / Domain**: Auth
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `account_id` | `SchemaObjectId` | Yes | No | None | N/A | [Account](#3account) | Yes | None |  |
| `provider_code` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `provider_subject` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `provider_email` | `String` | No | No | `""` | N/A | N/A | No | trim, lowercase |  |
| `linked_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `last_used_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"account_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"account_id":1,"provider_code":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`account_id`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the user account that owns this OAuth link.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.3 AccountRole

#### Basic Information
- **Model Name**: AccountRole
- **File Path**: [models/accountRole.model.js](file:///models/accountRole.model.js)
- **Collection Name**: `account_roles`
- **Module / Domain**: Auth
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `account_id` | `SchemaObjectId` | Yes | No | None | N/A | [Account](#3account) | Yes | None |  |
| `role_id` | `SchemaObjectId` | Yes | No | None | N/A | [Role](#3role) | Yes | None |  |
| `assigned_by_account_id` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `assigned_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"account_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"role_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"account_id":1,"role_id":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`account_id`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the user account assigned to the role.
- **`role_id`** &rarr; [Role](#3role) (`Many-to-One`): Identifies the role assigned to the account.
- **`assigned_by_account_id`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for AccountRole.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `assigned_by_account_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.4 Address

#### Basic Information
- **Model Name**: Address
- **File Path**: [models/address.model.js](file:///models/address.model.js)
- **Collection Name**: `customer_addresses`
- **Module / Domain**: Customers
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `address_label` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `recipient_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `phone` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `address_line_1` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `address_line_2` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `ward` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `district` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `city` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `country_code` | `String` | No | No | `"VN"` | N/A | N/A | No | trim |  |
| `postal_code` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `address_type` | `String` | No | No | `"home"` | "home", "office", "other" | N/A | No | trim |  |
| `address_note` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `is_default_shipping` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `is_default_billing` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer profile that owns this address.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `address_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.5 AdminProfile

#### Basic Information
- **Model Name**: AdminProfile
- **File Path**: [models/adminProfile.model.js](file:///models/adminProfile.model.js)
- **Collection Name**: `admin_profiles`
- **Module / Domain**: Auth/Admin
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `account_id` | `SchemaObjectId` | Yes | Yes | None | N/A | [Account](#3account) | Yes | None |  |
| `employee_code` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `full_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `department` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `job_title` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `manager_account_id` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `employment_status` | `String` | No | No | `"active"` | "active", "inactive", "terminated", "leave" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"account_id":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`account_id`** &rarr; [Account](#3account) (`One-to-One`): Identifies the user account associated with this admin profile.
- **`manager_account_id`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for AdminProfile.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `manager_account_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.6 AuditLog

#### Basic Information
- **Model Name**: AuditLog
- **File Path**: [models/auditLog.model.js](file:///models/auditLog.model.js)
- **Collection Name**: `audit_logs`
- **Module / Domain**: System
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `actor_account_id` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | Yes | None |  |
| `action_code` | `String` | Yes | No | None | N/A | N/A | Yes | trim |  |
| `entity_name` | `String` | Yes | No | None | N/A | N/A | Yes | trim |  |
| `entity_id` | `SchemaMixed` | No | No | None | N/A | N/A | No | None |  |
| `old_values_json` | `SchemaMixed` | No | No | None | N/A | N/A | No | None |  |
| `new_values_json` | `SchemaMixed` | No | No | None | N/A | N/A | No | None |  |
| `ip_address` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `user_agent` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | `undefined` | N/A | N/A | Yes | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"actor_account_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"action_code":1}` | **Options**: `{"background":true}`
- **Fields**: `{"entity_name":1}` | **Options**: `{"background":true}`
- **Fields**: `{"created_at":1}` | **Options**: `{"background":true}`

#### Relationships
- **`actor_account_id`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the admin/staff account performing the action.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.7 BeautyReference

#### Basic Information
- **Model Name**: BeautyReference
- **File Path**: [models/beautyReference.model.js](file:///models/beautyReference.model.js)
- **Collection Name**: `beauty_references`
- **Module / Domain**: Skin Journey
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `reference_group` | `String` | Yes | No | None | "skin_type", "skin_concern", "sensitivity_level", "skin_tone", "undertone", "shade_preference", "lip_color_preference", "makeup_style", "beauty_goal", "avoid_ingredient", "preferred_ingredient", "shopping_preference", "budget_range", "texture_preference", "finish_preference", "fragrance_preference", "purchase_intent" | N/A | Yes | None |  |
| `reference_code` | `String` | Yes | No | None | N/A | N/A | Yes | None |  |
| `display_name_vi` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `display_name_en` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `description` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `helper_text` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `parent_code` | `String` | No | No | None | N/A | N/A | No | None |  |
| `sort_order` | `Number` | No | No | `0` | N/A | N/A | Yes | None |  |
| `is_active` | `Boolean` | No | No | `true` | N/A | N/A | Yes | None |  |
| `is_multi_select` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `severity_enabled` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `recommendation_weight` | `Number` | No | No | `1` | N/A | N/A | No | None |  |
| `boost_tags` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `avoid_tags` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `preferred_ingredients` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `avoid_ingredients` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `recommended_categories` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `warning_text` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"reference_group":1}` | **Options**: `{"background":true}`
- **Fields**: `{"reference_code":1}` | **Options**: `{"background":true}`
- **Fields**: `{"sort_order":1}` | **Options**: `{"background":true}`
- **Fields**: `{"is_active":1}` | **Options**: `{"background":true}`
- **Fields**: `{"reference_group":1,"reference_code":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.8 Brand

#### Basic Information
- **Model Name**: Brand
- **File Path**: [models/brand.model.js](file:///models/brand.model.js)
- **Collection Name**: `brands`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `brandName` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `brandCode` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `description` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `logoUrl` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `brandStatus` | `String` | No | No | `"active"` | "active", "inactive", "draft" | N/A | No | None |  |
| `isActive` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"brandCode":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.9 Cart

#### Basic Information
- **Model Name**: Cart
- **File Path**: [models/cart.model.js](file:///models/cart.model.js)
- **Collection Name**: `carts`
- **Module / Domain**: Cart
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `owner_type` | `String` | No | No | `"customer"` | "customer", "guest" | N/A | Yes | None |  |
| `customer_id` | `SchemaObjectId` | No | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `guest_session_id` | `String` | No | No | None | N/A | N/A | Yes | trim |  |
| `cart_status` | `String` | No | No | `"active"` | "active", "converted", "expired", "merged" | N/A | No | None |  |
| `currency_code` | `String` | No | No | `"VND"` | N/A | N/A | No | trim |  |
| `item_count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `subtotal_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `discount_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `total_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `expires_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"owner_type":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"guest_session_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer profile who owns the cart.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `cart_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.10 CartItem

#### Basic Information
- **Model Name**: CartItem
- **File Path**: [models/cartItem.model.js](file:///models/cartItem.model.js)
- **Collection Name**: `cart_items`
- **Module / Domain**: Cart
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `line_key` | `String` | Yes | No | None | N/A | N/A | Yes | trim |  |
| `product_id` | `SchemaObjectId` | No | No | None | N/A | [Product](#3product) | Yes | None |  |
| `cart_id` | `SchemaObjectId` | Yes | No | None | N/A | [Cart](#3cart) | Yes | None |  |
| `variant_id` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `sku_snapshot` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `product_name_snapshot` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `variant_name_snapshot` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `brand_name_snapshot` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `image_url_snapshot` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `compare_at_price_amount` | `Number` | No | No | `0` | N/A | N/A | No | min: 0,Compare at price must not be negative |  |
| `stock_status` | `String` | No | No | `"in_stock"` | N/A | N/A | No | trim |  |
| `quantity` | `Number` | Yes | No | None | N/A | N/A | No | min: 1,Quantity must be at least 1 |  |
| `selected` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `unit_price_amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0,Unit price must not be negative |  |
| `discount_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `final_unit_price_amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0,Final unit price must not be negative |  |
| `line_total_amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0,Line total must not be negative |  |
| `added_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"line_key":1}` | **Options**: `{"background":true}`
- **Fields**: `{"product_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"cart_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"cart_id":1,"line_key":1}` | **Options**: `{"unique":true,"name":"ux_cart_line_key","background":true}`

#### Relationships
- **`product_id`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the referenced Product for CartItem.
- **`cart_id`** &rarr; [Cart](#3cart) (`Many-to-One`): Identifies the parent cart this item belongs to.
- **`variant_id`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the specific product variant added to the cart.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `cart_item_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `variant_id` is not indexed, which may lead to slower query performance during populated reads.
- **Sensitive Field**: Field `line_key` contains potentially sensitive data. Ensure it is serialized/deserialized securely (e.g. omitted in API responses).

---

### 3.11 Category

#### Basic Information
- **Model Name**: Category
- **File Path**: [models/category.model.js](file:///models/category.model.js)
- **Collection Name**: `categories`
- **Module / Domain**: Categories
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `categoryName` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `categoryCode` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `description` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `parentCategoryId` | `SchemaObjectId` | No | No | None | N/A | [Category](#3category) | No | None |  |
| `displayOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `categoryStatus` | `String` | No | No | `"active"` | "active", "inactive", "draft" | N/A | No | None |  |
| `isActive` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"categoryCode":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`parentCategoryId`** &rarr; [Category](#3category) (`Many-to-One`): Identifies the referenced Category for Category.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `parentCategoryId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.12 CheckoutAddress

#### Basic Information
- **Model Name**: CheckoutAddress
- **File Path**: [models/checkoutAddress.model.js](file:///models/checkoutAddress.model.js)
- **Collection Name**: `checkout_addresses`
- **Module / Domain**: Checkout
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `checkout_session_id` | `SchemaObjectId` | Yes | No | None | N/A | [CheckoutSession](#3checkoutsession) | Yes | None |  |
| `address_type` | `String` | Yes | No | None | "shipping", "billing" | N/A | No | None |  |
| `recipient_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `phone` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `address_line_1` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `address_line_2` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `ward` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `district` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `city` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `country_code` | `String` | No | No | `"VN"` | N/A | N/A | No | trim |  |
| `postal_code` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `is_selected` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"checkout_session_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`checkout_session_id`** &rarr; [CheckoutSession](#3checkoutsession) (`Many-to-One`): Identifies the checkout session this address belongs to.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `checkout_address_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.13 CheckoutSession

#### Basic Information
- **Model Name**: CheckoutSession
- **File Path**: [models/checkoutSession.model.js](file:///models/checkoutSession.model.js)
- **Collection Name**: `checkout_sessions`
- **Module / Domain**: Checkout
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `owner_type` | `String` | No | No | `"customer"` | "customer", "guest" | N/A | Yes | None |  |
| `guest_session_id` | `String` | No | No | None | N/A | N/A | Yes | trim |  |
| `cart_id` | `SchemaObjectId` | Yes | No | None | N/A | [Cart](#3cart) | Yes | None |  |
| `customer_id` | `SchemaObjectId` | No | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `guest_email` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `guest_phone` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `guest_full_name` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `checkout_status` | `String` | No | No | `"in_progress"` | "in_progress", "completed", "expired" | N/A | No | None |  |
| `currency_code` | `String` | No | No | `"VND"` | N/A | N/A | No | trim |  |
| `selected_shipping_address_id` | `SchemaObjectId` | No | No | None | N/A | [CheckoutAddress](#3checkoutaddress) | No | None |  |
| `selected_billing_address_id` | `SchemaObjectId` | No | No | None | N/A | [CheckoutAddress](#3checkoutaddress) | No | None |  |
| `selected_shipping_method_id` | `SchemaObjectId` | No | No | None | N/A | [CheckoutShippingMethod](#3checkoutshippingmethod) | No | None |  |
| `selected_payment_method_id` | `SchemaObjectId` | No | No | None | N/A | [PaymentMethod](#3paymentmethod) | No | None |  |
| `subtotal_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `shipping_fee_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `discount_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `applied_coupon_id` | `SchemaObjectId` | No | No | None | N/A | [Coupon](#3coupon) | No | None |  |
| `applied_coupon_code` | `String` | No | No | `""` | N/A | N/A | No | trim, uppercase |  |
| `coupon_discount_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `tax_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `total_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `expires_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"owner_type":1}` | **Options**: `{"background":true}`
- **Fields**: `{"guest_session_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"cart_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`cart_id`** &rarr; [Cart](#3cart) (`Many-to-One`): Identifies the cart that initiated the checkout.
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer account checking out.
- **`selected_shipping_address_id`** &rarr; [CheckoutAddress](#3checkoutaddress) (`Many-to-One`): Identifies the referenced CheckoutAddress for CheckoutSession.
- **`selected_billing_address_id`** &rarr; [CheckoutAddress](#3checkoutaddress) (`Many-to-One`): Identifies the referenced CheckoutAddress for CheckoutSession.
- **`selected_shipping_method_id`** &rarr; [CheckoutShippingMethod](#3checkoutshippingmethod) (`Many-to-One`): Identifies the referenced CheckoutShippingMethod for CheckoutSession.
- **`selected_payment_method_id`** &rarr; [PaymentMethod](#3paymentmethod) (`Many-to-One`): Identifies the referenced PaymentMethod for CheckoutSession.
- **`applied_coupon_id`** &rarr; [Coupon](#3coupon) (`Many-to-One`): Identifies the referenced Coupon for CheckoutSession.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `checkout_session_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `selected_shipping_address_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `selected_billing_address_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `selected_shipping_method_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `selected_payment_method_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `applied_coupon_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.14 CheckoutShippingMethod

#### Basic Information
- **Model Name**: CheckoutShippingMethod
- **File Path**: [models/checkoutShippingMethod.model.js](file:///models/checkoutShippingMethod.model.js)
- **Collection Name**: `checkout_shipping_methods`
- **Module / Domain**: Checkout
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `checkout_session_id` | `SchemaObjectId` | Yes | No | None | N/A | [CheckoutSession](#3checkoutsession) | Yes | None |  |
| `shipping_method_id` | `SchemaObjectId` | Yes | No | None | N/A | [ShippingMethod](#3shippingmethod) | No | None |  |
| `shipping_method_code` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `carrier_code` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `service_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `estimated_days_min` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `estimated_days_max` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `shipping_fee_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `currency_code` | `String` | No | No | `"VND"` | N/A | N/A | No | trim |  |
| `is_selected` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"checkout_session_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`checkout_session_id`** &rarr; [CheckoutSession](#3checkoutsession) (`Many-to-One`): Identifies the checkout session this shipping method is selected for.
- **`shipping_method_id`** &rarr; [ShippingMethod](#3shippingmethod) (`Many-to-One`): Identifies the selected shipping method.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `checkout_shipping_method_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `shipping_method_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.15 Coupon

#### Basic Information
- **Model Name**: Coupon
- **File Path**: [models/coupon.model.js](file:///models/coupon.model.js)
- **Collection Name**: `coupons`
- **Module / Domain**: Promotions
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `promotionId` | `SchemaObjectId` | Yes | No | None | N/A | [Promotion](#3promotion) | No | None |  |
| `couponCode` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `validFrom` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `validTo` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `usageLimitTotal` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `usageLimitPerCustomer` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `minOrderAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `couponStatus` | `String` | No | No | `"active"` | "active", "inactive" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"couponCode":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`promotionId`** &rarr; [Promotion](#3promotion) (`Many-to-One`): Identifies the referenced Promotion for Coupon.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `promotionId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.16 CouponRedemption

#### Basic Information
- **Model Name**: CouponRedemption
- **File Path**: [models/couponRedemption.model.js](file:///models/couponRedemption.model.js)
- **Collection Name**: `coupon_redemptions`
- **Module / Domain**: Promotions
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `couponId` | `SchemaObjectId` | Yes | No | None | N/A | [Coupon](#3coupon) | No | None |  |
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | No | None |  |
| `order_id` | `SchemaObjectId` | No | No | None | N/A | [Order](#3order) | No | None |  |
| `discountAmount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0,Discount amount must not be negative |  |
| `redeemedAt` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `redemptionStatus` | `String` | No | No | `"used"` | "used", "cancelled" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`couponId`** &rarr; [Coupon](#3coupon) (`Many-to-One`): Identifies the coupon being redeemed.
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer who redeemed the coupon.
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the order in which the coupon was redeemed.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `couponId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `customer_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.17 Customer

#### Basic Information
- **Model Name**: Customer
- **File Path**: [models/customer.model.js](file:///models/customer.model.js)
- **Collection Name**: `customer_profiles`
- **Module / Domain**: Customers
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `account_id` | `SchemaObjectId` | Yes | Yes | None | N/A | [Account](#3account) | No | None |  |
| `customer_code` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `first_name` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `last_name` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `full_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `date_of_birth` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `gender` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `avatar_url` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `customer_status` | `String` | No | No | `"active"` | "active", "inactive" | N/A | No | None |  |
| `registered_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"account_id":1}` | **Options**: `{"unique":true,"background":true}`
- **Fields**: `{"customer_code":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`account_id`** &rarr; [Account](#3account) (`One-to-One`): Identifies the authentication account associated with this customer profile.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `customer_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.18 CustomerBeautyProfile

#### Basic Information
- **Model Name**: CustomerBeautyProfile
- **File Path**: [models/customerBeautyProfile.model.js](file:///models/customerBeautyProfile.model.js)
- **Collection Name**: `customer_beauty_profiles`
- **Module / Domain**: Skin Journey
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | Yes | None | N/A | [Customer](#3customer) | Yes | None |  |
| `skin_type` | `String` | No | No | `"unknown"` | N/A | N/A | Yes | None |  |
| `skin_concerns` | `Array<String>` | No | No | `[]` | N/A | N/A | Yes | None |  |
| `sensitivity_level` | `String` | No | No | `"unknown"` | N/A | N/A | Yes | None |  |
| `skin_tone` | `String` | No | No | `"unknown"` | N/A | N/A | Yes | None |  |
| `undertone` | `String` | No | No | `"unknown"` | N/A | N/A | Yes | None |  |
| `shade_preference` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `lip_color_preference` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `makeup_style` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `beauty_goals` | `Array<String>` | No | No | `[]` | N/A | N/A | Yes | None |  |
| `avoid_ingredients` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `preferred_ingredients` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `budget_range` | `String` | No | No | `"unknown"` | N/A | N/A | No | None |  |
| `preferred_brands` | `Array<ObjectId>` | No | No | None | N/A | N/A | No | None |  |
| `disliked_brands` | `Array<ObjectId>` | No | No | None | N/A | N/A | No | None |  |
| `preferred_categories` | `Array<ObjectId>` | No | No | None | N/A | N/A | No | None |  |
| `texture_preference` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `finish_preference` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `fragrance_preference` | `String` | No | No | `"no_preference"` | N/A | N/A | No | None |  |
| `purchase_intent` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `profile_completion_rate` | `Number` | No | No | `0` | N/A | N/A | No | min: 0, max: 100 |  |
| `profile_hash` | `String` | Yes | No | None | N/A | N/A | Yes | None |  |
| `source` | `String` | No | No | `"onboarding"` | "onboarding", "account", "chatbot", "ar", "admin" | N/A | No | None |  |
| `last_updated_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"customer_id":1}` | **Options**: `{"unique":true,"background":true}`
- **Fields**: `{"skin_type":1}` | **Options**: `{"background":true}`
- **Fields**: `{"skin_concerns":1}` | **Options**: `{"background":true}`
- **Fields**: `{"sensitivity_level":1}` | **Options**: `{"background":true}`
- **Fields**: `{"skin_tone":1}` | **Options**: `{"background":true}`
- **Fields**: `{"undertone":1}` | **Options**: `{"background":true}`
- **Fields**: `{"beauty_goals":1}` | **Options**: `{"background":true}`
- **Fields**: `{"profile_hash":1}` | **Options**: `{"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`One-to-One`): Identifies the customer profile this beauty profile belongs to.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.19 CustomerConsent

#### Basic Information
- **Model Name**: CustomerConsent
- **File Path**: [models/customerConsent.model.js](file:///models/customerConsent.model.js)
- **Collection Name**: `customer_consents`
- **Module / Domain**: Customers
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `consent_type` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `consent_status` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `consent_version` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `consented_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `source_channel` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `created_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1,"consent_type":1}` | **Options**: `{"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer who gave the consent.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `consent_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.20 CustomerCoupon

#### Basic Information
- **Model Name**: CustomerCoupon
- **File Path**: [models/customerCoupon.model.js](file:///models/customerCoupon.model.js)
- **Collection Name**: `customer_coupons`
- **Module / Domain**: Promotions
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `couponId` | `SchemaObjectId` | Yes | No | None | N/A | [Coupon](#3coupon) | Yes | None |  |
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `savedAt` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `status` | `String` | No | No | `"saved"` | "saved", "used", "expired" | N/A | Yes | None |  |
| `usedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"couponId":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"status":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1,"couponId":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`couponId`** &rarr; [Coupon](#3coupon) (`Many-to-One`): Identifies the coupon associated with the customer.
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer eligible to use this coupon.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.21 CustomerPreference

#### Basic Information
- **Model Name**: CustomerPreference
- **File Path**: [models/customerPreference.model.js](file:///models/customerPreference.model.js)
- **Collection Name**: `customer_preferences`
- **Module / Domain**: Customers
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `preference_key` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `preference_value` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1,"preference_key":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer who has these preferences.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `preference_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Sensitive Field**: Field `preference_key` contains potentially sensitive data. Ensure it is serialized/deserialized securely (e.g. omitted in API responses).

---

### 3.22 CustomerRecommendationSnapshot

#### Basic Information
- **Model Name**: CustomerRecommendationSnapshot
- **File Path**: [models/customerRecommendationSnapshot.model.js](file:///models/customerRecommendationSnapshot.model.js)
- **Collection Name**: `customer_recommendation_snapshots`
- **Module / Domain**: Skin Journey
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `recommendation_type` | `String` | Yes | No | None | N/A | N/A | Yes | None |  |
| `profile_hash` | `String` | Yes | No | None | N/A | N/A | Yes | None |  |
| `product_ids` | `Array<ObjectId>` | Yes | No | None | N/A | N/A | Yes | None |  |
| `items` | `Array<Mixed>` | No | No | `[]` | N/A | N/A | No | None |  |
| `algorithm_version` | `String` | No | No | `"rule_v1"` | N/A | N/A | Yes | None |  |
| `generated_at` | `Date` | No | No | `undefined` | N/A | N/A | Yes | None |  |
| `expires_at` | `Date` | No | No | None | N/A | N/A | Yes | None |  |
| `invalidated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"recommendation_type":1}` | **Options**: `{"background":true}`
- **Fields**: `{"profile_hash":1}` | **Options**: `{"background":true}`
- **Fields**: `{"product_ids":1}` | **Options**: `{"background":true}`
- **Fields**: `{"algorithm_version":1}` | **Options**: `{"background":true}`
- **Fields**: `{"generated_at":1}` | **Options**: `{"background":true}`
- **Fields**: `{"expires_at":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1,"recommendation_type":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer profile this recommendation snapshot is generated for.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.23 EmailOtp

#### Basic Information
- **Model Name**: EmailOtp
- **File Path**: [models/emailOtp.model.js](file:///models/emailOtp.model.js)
- **Collection Name**: `email_otps`
- **Module / Domain**: Auth
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `email` | `String` | Yes | No | None | N/A | N/A | Yes | trim, lowercase |  |
| `purpose` | `String` | Yes | No | None | "login", "email_verification" | N/A | Yes | None |  |
| `otp_code_hash` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `expires_at` | `Date` | Yes | No | None | N/A | N/A | Yes | None |  |
| `attempt_count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `account_id` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | Yes | None |  |
| `consumed_at` | `Date` | No | No | None | N/A | N/A | Yes | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"email":1}` | **Options**: `{"background":true}`
- **Fields**: `{"purpose":1}` | **Options**: `{"background":true}`
- **Fields**: `{"expires_at":1}` | **Options**: `{"background":true}`
- **Fields**: `{"account_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"consumed_at":1}` | **Options**: `{"background":true}`
- **Fields**: `{"email":1,"purpose":1,"consumed_at":1,"expires_at":1}` | **Options**: `{"background":true}`
- **Fields**: `{"email":1,"purpose":1,"created_at":-1}` | **Options**: `{"background":true}`

#### Relationships
- **`account_id`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for EmailOtp.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Sensitive Field**: Field `otp_code_hash` contains potentially sensitive data. Ensure it is serialized/deserialized securely (e.g. omitted in API responses).

---

### 3.24 GuestSession

#### Basic Information
- **Model Name**: GuestSession
- **File Path**: [models/guestSession.model.js](file:///models/guestSession.model.js)
- **Collection Name**: `guest_sessions`
- **Module / Domain**: Customers
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `guest_session_id` | `String` | Yes | Yes | None | N/A | N/A | Yes | trim |  |
| `status` | `String` | No | No | `"active"` | "active", "expired" | N/A | No | None |  |
| `last_seen_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `user_agent` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"guest_session_id":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.25 InventoryBalance

#### Basic Information
- **Model Name**: InventoryBalance
- **File Path**: [models/inventoryBalance.model.js](file:///models/inventoryBalance.model.js)
- **Collection Name**: `inventory_balances`
- **Module / Domain**: Inventory
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `warehouseId` | `SchemaObjectId` | Yes | No | None | N/A | [Warehouse](#3warehouse) | No | None |  |
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `onHandQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `reservedQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `blockedQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `availableQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `reorderPointQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `safetyStockQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `lastCountedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"variantId":1,"availableQty":1}` | **Options**: `{"background":true}`

#### Relationships
- **`warehouseId`** &rarr; [Warehouse](#3warehouse) (`Many-to-One`): Identifies the warehouse where stock is kept.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the product variant stocked.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `warehouseId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.26 InventoryTransaction

#### Basic Information
- **Model Name**: InventoryTransaction
- **File Path**: [models/inventoryTransaction.model.js](file:///models/inventoryTransaction.model.js)
- **Collection Name**: `inventory_transactions`
- **Module / Domain**: Inventory
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `warehouseId` | `SchemaObjectId` | Yes | No | None | N/A | [Warehouse](#3warehouse) | No | None |  |
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `transactionType` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `quantityChange` | `Number` | Yes | No | None | N/A | N/A | No | None |  |
| `referenceType` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `referenceId` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `reasonCode` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `note` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `performedByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`warehouseId`** &rarr; [Warehouse](#3warehouse) (`Many-to-One`): Identifies the warehouse where stock change happened.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the product variant for the transaction.
- **`performedByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for InventoryTransaction.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `warehouseId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `performedByAccountId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.27 LoyaltyAccount

#### Basic Information
- **Model Name**: LoyaltyAccount
- **File Path**: [models/loyaltyAccount.model.js](file:///models/loyaltyAccount.model.js)
- **Collection Name**: `loyalty_accounts`
- **Module / Domain**: Loyalty
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | No | None |  |
| `tierId` | `SchemaObjectId` | No | No | None | N/A | [LoyaltyTier](#3loyaltytier) | No | None |  |
| `pointsBalance` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `lifetimePointsEarned` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `lifetimePointsRedeemed` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `loyaltyStatus` | `String` | No | No | `"active"` | "active", "inactive", "suspended" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer profile this loyalty account belongs to.
- **`tierId`** &rarr; [LoyaltyTier](#3loyaltytier) (`Many-to-One`): Identifies the referenced LoyaltyTier for LoyaltyAccount.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `customer_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `tierId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.28 LoyaltyPointLedger

#### Basic Information
- **Model Name**: LoyaltyPointLedger
- **File Path**: [models/loyaltyPointLedger.model.js](file:///models/loyaltyPointLedger.model.js)
- **Collection Name**: `loyalty_point_ledger`
- **Module / Domain**: Loyalty
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `loyaltyAccountId` | `SchemaObjectId` | Yes | No | None | N/A | [LoyaltyAccount](#3loyaltyaccount) | No | None |  |
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | No | None |  |
| `order_id` | `SchemaObjectId` | No | No | None | N/A | [Order](#3order) | No | None |  |
| `transactionType` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `pointsDelta` | `Number` | Yes | No | None | N/A | N/A | No | None |  |
| `pointsBefore` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `pointsAfter` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `expiryDate` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `referenceType` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `referenceId` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `note` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`loyaltyAccountId`** &rarr; [LoyaltyAccount](#3loyaltyaccount) (`Many-to-One`): Identifies the parent loyalty account.
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer associated with this point ledger entry.
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the order associated with this point transaction.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `loyaltyAccountId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `customer_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.
- **Singular Collection Name**: Collection `loyalty_point_ledger` is named in the singular form, whereas most other collections use plural forms.

---

### 3.29 LoyaltyTier

#### Basic Information
- **Model Name**: LoyaltyTier
- **File Path**: [models/loyaltyTier.model.js](file:///models/loyaltyTier.model.js)
- **Collection Name**: `loyalty_tiers`
- **Module / Domain**: Loyalty
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `tierCode` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `tierName` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `minimumPoints` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `discountRate` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `priorityRank` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `tierStatus` | `String` | No | No | `"active"` | "active", "inactive" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"tierCode":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.30 Order

#### Basic Information
- **Model Name**: Order
- **File Path**: [models/order.model.js](file:///models/order.model.js)
- **Collection Name**: `orders`
- **Module / Domain**: Orders
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `owner_type` | `String` | No | No | `"customer"` | "customer", "guest" | N/A | Yes | None |  |
| `guest_session_id` | `String` | No | No | None | N/A | N/A | Yes | trim |  |
| `guest_email` | `String` | No | No | `""` | N/A | N/A | Yes | trim |  |
| `guest_phone` | `String` | No | No | `""` | N/A | N/A | Yes | trim |  |
| `guest_full_name` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `order_number` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `customer_id` | `SchemaObjectId` | No | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `checkout_session_id` | `SchemaObjectId` | No | No | None | N/A | [CheckoutSession](#3checkoutsession) | No | None |  |
| `currency_code` | `String` | No | No | `"VND"` | N/A | N/A | No | trim |  |
| `order_status` | `String` | No | No | `"pending"` | "pending", "confirmed", "processing", "completed", "cancelled", "returned" | N/A | No | None |  |
| `payment_status` | `String` | No | No | `"unpaid"` | "unpaid", "pending", "authorized", "paid", "failed", "partially_refunded", "refunded" | N/A | No | None |  |
| `fulfillment_status` | `String` | No | No | `"unfulfilled"` | "unfulfilled", "preparing", "partially_shipped", "shipped", "in_transit", "delivered", "return_requested", "return_approved", "partially_returned", "returned" | N/A | No | None |  |
| `customer_note` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `placed_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `confirmed_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `cancelled_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `completed_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `cancellation_reason` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"owner_type":1}` | **Options**: `{"background":true}`
- **Fields**: `{"guest_session_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"guest_email":1}` | **Options**: `{"background":true}`
- **Fields**: `{"guest_phone":1}` | **Options**: `{"background":true}`
- **Fields**: `{"order_number":1}` | **Options**: `{"unique":true,"background":true}`
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer profile who placed the order.
- **`checkout_session_id`** &rarr; [CheckoutSession](#3checkoutsession) (`Many-to-One`): Identifies the referenced CheckoutSession for Order.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `checkout_session_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.31 OrderAddress

#### Basic Information
- **Model Name**: OrderAddress
- **File Path**: [models/orderAddress.model.js](file:///models/orderAddress.model.js)
- **Collection Name**: `order_addresses`
- **Module / Domain**: Orders
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | Yes | None |  |
| `address_type` | `String` | Yes | No | None | "shipping", "billing" | N/A | No | None |  |
| `recipient_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `phone` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `address_line_1` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `address_line_2` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `ward` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `district` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `city` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `country_code` | `String` | No | No | `"VN"` | N/A | N/A | No | trim |  |
| `postal_code` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `created_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"order_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the order this address belongs to.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_address_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.32 OrderItem

#### Basic Information
- **Model Name**: OrderItem
- **File Path**: [models/orderItem.model.js](file:///models/orderItem.model.js)
- **Collection Name**: `order_items`
- **Module / Domain**: Orders
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | Yes | None |  |
| `product_id` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `variant_id` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `sku_snapshot` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `product_name_snapshot` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `variant_name_snapshot` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `quantity` | `Number` | Yes | No | None | N/A | N/A | No | min: 1 |  |
| `unit_list_price_amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0 |  |
| `unit_sale_price_amount` | `Number` | No | No | `0` | N/A | N/A | No | min: 0 |  |
| `unit_final_price_amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0 |  |
| `line_subtotal_amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0 |  |
| `line_discount_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `line_total_amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0 |  |
| `currency_code` | `String` | No | No | `"VND"` | N/A | N/A | No | trim |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"order_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the parent order for this line item.
- **`product_id`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the referenced Product for OrderItem.
- **`variant_id`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the product variant purchased.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_item_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `product_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `variant_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.33 OrderStatusHistory

#### Basic Information
- **Model Name**: OrderStatusHistory
- **File Path**: [models/orderStatusHistory.model.js](file:///models/orderStatusHistory.model.js)
- **Collection Name**: `order_status_history`
- **Module / Domain**: Orders
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | Yes | None |  |
| `old_order_status` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `new_order_status` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `old_payment_status` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `new_payment_status` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `old_fulfillment_status` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `new_fulfillment_status` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `changed_by_account_id` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `change_reason` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `changed_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"order_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the parent order.
- **`changed_by_account_id`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for OrderStatusHistory.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_status_history_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `changed_by_account_id` is not indexed, which may lead to slower query performance during populated reads.
- **Singular Collection Name**: Collection `order_status_history` is named in the singular form, whereas most other collections use plural forms.

---

### 3.34 OrderTotal

#### Basic Information
- **Model Name**: OrderTotal
- **File Path**: [models/orderTotal.model.js](file:///models/orderTotal.model.js)
- **Collection Name**: `order_totals`
- **Module / Domain**: Orders
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | Yes | None |  |
| `subtotal_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `item_discount_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `order_discount_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `shipping_fee_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `tax_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `grand_total_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `refunded_amount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `currency_code` | `String` | No | No | `"VND"` | N/A | N/A | No | trim |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"order_id":1}` | **Options**: `{"background":true}`

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the parent order.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `order_total_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.35 PaymentIntent

#### Basic Information
- **Model Name**: PaymentIntent
- **File Path**: [models/paymentIntent.model.js](file:///models/paymentIntent.model.js)
- **Collection Name**: `payment_intents`
- **Module / Domain**: Payments
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | No | None |  |
| `payment_method_id` | `SchemaObjectId` | No | No | None | N/A | [PaymentMethod](#3paymentmethod) | No | None |  |
| `providerCode` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `providerPaymentIntentId` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `requestedAmount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0 |  |
| `authorizedAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `capturedAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `currencyCode` | `String` | No | No | `"VND"` | N/A | N/A | No | None |  |
| `intentStatus` | `String` | No | No | `"pending"` | "pending", "authorized", "captured", "failed", "cancelled" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the order that this payment intent belongs to.
- **`payment_method_id`** &rarr; [PaymentMethod](#3paymentmethod) (`Many-to-One`): Identifies the referenced PaymentMethod for PaymentIntent.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `payment_method_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.36 PaymentMethod

#### Basic Information
- **Model Name**: PaymentMethod
- **File Path**: [models/paymentMethod.model.js](file:///models/paymentMethod.model.js)
- **Collection Name**: `payment_methods`
- **Module / Domain**: Payments
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `payment_method_code` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `payment_method_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `provider_code` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `method_type` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `is_active` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `sort_order` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"payment_method_code":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `payment_method_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.37 PaymentTransaction

#### Basic Information
- **Model Name**: PaymentTransaction
- **File Path**: [models/paymentTransaction.model.js](file:///models/paymentTransaction.model.js)
- **Collection Name**: `payment_transactions`
- **Module / Domain**: Payments
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `paymentIntentId` | `SchemaObjectId` | Yes | No | None | N/A | [PaymentIntent](#3paymentintent) | No | None |  |
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | No | None |  |
| `transactionType` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `providerTransactionId` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `transactionStatus` | `String` | No | No | `"pending"` | "pending", "success", "failed" | N/A | No | None |  |
| `amount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0 |  |
| `currencyCode` | `String` | No | No | `"VND"` | N/A | N/A | No | None |  |
| `processedAt` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `rawResponseJson` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`paymentIntentId`** &rarr; [PaymentIntent](#3paymentintent) (`Many-to-One`): Identifies the parent payment intent.
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the referenced Order for PaymentTransaction.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `paymentIntentId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.38 Permission

#### Basic Information
- **Model Name**: Permission
- **File Path**: [models/permission.model.js](file:///models/permission.model.js)
- **Collection Name**: `permissions`
- **Module / Domain**: Auth
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `permission_code` | `String` | Yes | Yes | None | N/A | N/A | No | trim |  |
| `permission_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `module_name` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `description` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"permission_code":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.39 PriceBook

#### Basic Information
- **Model Name**: PriceBook
- **File Path**: [models/priceBook.model.js](file:///models/priceBook.model.js)
- **Collection Name**: `price_books`
- **Module / Domain**: Pricing
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `priceBookCode` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `priceBookName` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `currencyCode` | `String` | Yes | No | `"VND"` | N/A | N/A | No | None |  |
| `isDefault` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `startAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `endAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `priceBookStatus` | `String` | No | No | `"active"` | "active", "inactive" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"priceBookCode":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.40 PriceBookEntry

#### Basic Information
- **Model Name**: PriceBookEntry
- **File Path**: [models/priceBookEntry.model.js](file:///models/priceBookEntry.model.js)
- **Collection Name**: `price_book_entries`
- **Module / Domain**: Pricing
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `priceBookId` | `SchemaObjectId` | Yes | No | None | N/A | [PriceBook](#3pricebook) | No | None |  |
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `listPriceAmount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0,List price must not be negative |  |
| `salePriceAmount` | `Number` | No | No | `0` | N/A | N/A | No | min: 0,Sale price must not be negative |  |
| `effectiveFrom` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `effectiveTo` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `isActive` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`priceBookId`** &rarr; [PriceBook](#3pricebook) (`Many-to-One`): Identifies the price book defining this entry.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the product variant priced.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `priceBookId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.41 PriceHistory

#### Basic Information
- **Model Name**: PriceHistory
- **File Path**: [models/priceHistory.model.js](file:///models/priceHistory.model.js)
- **Collection Name**: `price_histories`
- **Module / Domain**: Pricing
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `priceBookId` | `SchemaObjectId` | Yes | No | None | N/A | [PriceBook](#3pricebook) | No | None |  |
| `currencyCode` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `oldListPriceAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `oldSalePriceAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `newListPriceAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `newSalePriceAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `changeReason` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `changedByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `changedAt` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the product variant whose price changed.
- **`priceBookId`** &rarr; [PriceBook](#3pricebook) (`Many-to-One`): Identifies the referenced PriceBook for PriceHistory.
- **`changedByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for PriceHistory.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `priceBookId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `changedByAccountId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.42 Product

#### Basic Information
- **Model Name**: Product
- **File Path**: [models/product.model.js](file:///models/product.model.js)
- **Collection Name**: `products`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productName` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `productCode` | `String` | No | No | None | N/A | N/A | No | trim, uppercase |  |
| `slug` | `String` | No | Yes | None | N/A | N/A | No | trim, lowercase |  |
| `brandId` | `SchemaObjectId` | Yes | No | None | N/A | [Brand](#3brand) | No | None |  |
| `categoryId` | `SchemaObjectId` | Yes | No | None | N/A | [Category](#3category) | No | None |  |
| `price` | `Number` | Yes | No | None | N/A | N/A | No | min: 0,Price must not be negative |  |
| `compareAtPrice` | `Number` | No | No | None | N/A | N/A | No | None |  |
| `imageUrl` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `shortDescription` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `longDescription` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `stock` | `Number` | No | No | `0` | N/A | N/A | No | min: 0,Stock must not be negative |  |
| `bought` | `Number` | No | No | `0` | N/A | N/A | No | min: 0,Bought must not be negative |  |
| `averageRating` | `Number` | No | No | `0` | N/A | N/A | No | min: 0,Average rating must not be negative |  |
| `isActive` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `productStatus` | `String` | No | No | `"active"` | "active", "inactive" | N/A | No | None |  |
| `ingredientText` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `shades` | `Array<[object Object]>` | No | No | None | N/A | N/A | No | None |  |
| `skin_types_supported` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `concerns_targeted` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `ingredient_flags` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `key_ingredients` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `is_sensitive_friendly` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `tone_match_supported` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `finish_type` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `coverage_type` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `sales_count` | `Number` | No | No | `0` | N/A | N/A | No | min: 0,Sales count must not be negative |  |
| `is_best_seller` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `usageInstruction` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `createdByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `updatedByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"slug":1}` | **Options**: `{"sparse":true,"unique":true,"background":true}`
- **Fields**: `{"createdAt":-1}` | **Options**: `{"background":true}`
- **Fields**: `{"brandId":1,"createdAt":-1}` | **Options**: `{"background":true}`
- **Fields**: `{"categoryId":1,"createdAt":-1}` | **Options**: `{"background":true}`
- **Fields**: `{"bought":-1,"createdAt":-1}` | **Options**: `{"background":true}`
- **Fields**: `{"productCode":1}` | **Options**: `{"background":true}`
- **Fields**: `{"skin_types_supported":1}` | **Options**: `{"background":true}`
- **Fields**: `{"shades.hex":1}` | **Options**: `{"background":true}`
- **Fields**: `{"productStatus":1,"isActive":1,"categoryId":1,"price":1}` | **Options**: `{"background":true}`
- **Fields**: `{"productStatus":1,"isActive":1,"brandId":1,"price":1}` | **Options**: `{"background":true}`
- **Fields**: `{"productStatus":1,"isActive":1,"categoryId":1,"bought":-1}` | **Options**: `{"background":true}`
- **Fields**: `{"productStatus":1,"isActive":1,"averageRating":-1}` | **Options**: `{"background":true}`

#### Relationships
- **`brandId`** &rarr; [Brand](#3brand) (`Many-to-One`): Identifies the referenced Brand for Product.
- **`categoryId`** &rarr; [Category](#3category) (`Many-to-One`): Identifies the referenced Category for Product.
- **`createdByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for Product.
- **`updatedByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for Product.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `createdByAccountId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `updatedByAccountId` is not indexed, which may lead to slower query performance during populated reads.
- **Sensitive Field**: Field `key_ingredients` contains potentially sensitive data. Ensure it is serialized/deserialized securely (e.g. omitted in API responses).

---

### 3.43 ProductAttribute

#### Basic Information
- **Model Name**: ProductAttribute
- **File Path**: [models/productAttribute.model.js](file:///models/productAttribute.model.js)
- **Collection Name**: `product_attributes`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productId` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `attributeName` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `attributeValue` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `displayOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"productId":1,"displayOrder":1}` | **Options**: `{"background":true}`
- **Fields**: `{"attributeName":1,"attributeValue":1,"productId":1}` | **Options**: `{"background":true}`

#### Relationships
- **`productId`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the referenced Product for ProductAttribute.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.44 ProductBeautyProfile

#### Basic Information
- **Model Name**: ProductBeautyProfile
- **File Path**: [models/productBeautyProfile.model.js](file:///models/productBeautyProfile.model.js)
- **Collection Name**: `product_beauty_profiles`
- **Module / Domain**: Skin Journey
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `product_id` | `SchemaObjectId` | Yes | Yes | None | N/A | [Product](#3product) | Yes | None |  |
| `suitable_skin_types` | `Array<String>` | No | No | `[]` | N/A | N/A | Yes | None |  |
| `suitable_skin_concerns` | `Array<String>` | No | No | `[]` | N/A | N/A | Yes | None |  |
| `suitable_sensitivity_levels` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `suitable_skin_tones` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `suitable_undertones` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `supported_beauty_goals` | `Array<String>` | No | No | `[]` | N/A | N/A | Yes | None |  |
| `key_ingredients` | `Array<String>` | No | No | `[]` | N/A | N/A | Yes | None |  |
| `avoid_for_ingredients` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `texture` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `finish` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `fragrance_type` | `String` | No | No | `"no_preference"` | N/A | N/A | No | None |  |
| `product_tags` | `Array<String>` | No | No | `[]` | N/A | N/A | Yes | None |  |
| `recommendation_boost_score` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `recommendation_penalty_score` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `is_active` | `Boolean` | No | No | `true` | N/A | N/A | Yes | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"product_id":1}` | **Options**: `{"unique":true,"background":true}`
- **Fields**: `{"suitable_skin_types":1}` | **Options**: `{"background":true}`
- **Fields**: `{"suitable_skin_concerns":1}` | **Options**: `{"background":true}`
- **Fields**: `{"supported_beauty_goals":1}` | **Options**: `{"background":true}`
- **Fields**: `{"key_ingredients":1}` | **Options**: `{"background":true}`
- **Fields**: `{"product_tags":1}` | **Options**: `{"background":true}`
- **Fields**: `{"is_active":1}` | **Options**: `{"background":true}`

#### Relationships
- **`product_id`** &rarr; [Product](#3product) (`One-to-One`): Identifies the product that matches this beauty profile criteria.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Sensitive Field**: Field `key_ingredients` contains potentially sensitive data. Ensure it is serialized/deserialized securely (e.g. omitted in API responses).

---

### 3.45 ProductCategory

#### Basic Information
- **Model Name**: ProductCategory
- **File Path**: [models/productCategory.model.js](file:///models/productCategory.model.js)
- **Collection Name**: `product_categories`
- **Module / Domain**: Categories
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productId` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `categoryId` | `SchemaObjectId` | Yes | No | None | N/A | [Category](#3category) | No | None |  |
| `isPrimary` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `sortOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"productId":1,"categoryId":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`productId`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the product mapped to a category.
- **`categoryId`** &rarr; [Category](#3category) (`Many-to-One`): Identifies the category mapped to the product.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `categoryId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.46 ProductMedia

#### Basic Information
- **Model Name**: ProductMedia
- **File Path**: [models/productMedia.model.js](file:///models/productMedia.model.js)
- **Collection Name**: `product_media`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productId` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `mediaType` | `String` | No | No | `"image"` | "image", "video" | N/A | No | None |  |
| `mediaUrl` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `altText` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `sortOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `isPrimary` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"productId":1,"isPrimary":-1,"sortOrder":1,"createdAt":1}` | **Options**: `{"background":true}`

#### Relationships
- **`productId`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the product this media belongs to.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.47 ProductOption

#### Basic Information
- **Model Name**: ProductOption
- **File Path**: [models/productOption.model.js](file:///models/productOption.model.js)
- **Collection Name**: `product_options`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productId` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `optionName` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `displayOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"productId":1,"displayOrder":1}` | **Options**: `{"background":true}`
- **Fields**: `{"optionName":1,"productId":1}` | **Options**: `{"background":true}`

#### Relationships
- **`productId`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the parent product.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.48 ProductOptionValue

#### Basic Information
- **Model Name**: ProductOptionValue
- **File Path**: [models/productOptionValue.model.js](file:///models/productOptionValue.model.js)
- **Collection Name**: `product_option_values`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productOptionId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductOption](#3productoption) | No | None |  |
| `optionValue` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `displayOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"productOptionId":1,"displayOrder":1}` | **Options**: `{"background":true}`
- **Fields**: `{"optionValue":1,"productOptionId":1}` | **Options**: `{"background":true}`

#### Relationships
- **`productOptionId`** &rarr; [ProductOption](#3productoption) (`Many-to-One`): Identifies the parent product option.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.49 ProductVariant

#### Basic Information
- **Model Name**: ProductVariant
- **File Path**: [models/productVariant.model.js](file:///models/productVariant.model.js)
- **Collection Name**: `product_variants`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productId` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `sku` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `barcode` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `variantName` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `variantStatus` | `String` | No | No | `"active"` | "active", "inactive" | N/A | No | None |  |
| `weightGrams` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `volumeMl` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `costAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"sku":1}` | **Options**: `{"unique":true,"background":true}`
- **Fields**: `{"productId":1,"variantStatus":1}` | **Options**: `{"background":true}`

#### Relationships
- **`productId`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the parent product this variant belongs to.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.50 Promotion

#### Basic Information
- **Model Name**: Promotion
- **File Path**: [models/promotion.model.js](file:///models/promotion.model.js)
- **Collection Name**: `promotions`
- **Module / Domain**: Promotions
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `promotionCode` | `String` | No | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `promotionName` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `description` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `promotionType` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `discountType` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `discountValue` | `Number` | Yes | No | None | N/A | N/A | No | min: 0,Discount value must not be negative |  |
| `maxDiscountAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `startAt` | `Date` | Yes | No | None | N/A | N/A | No | None |  |
| `endAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `usageLimitTotal` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `usageLimitPerCustomer` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `isAutoApply` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `priority` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `stackableFlag` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `promotionStatus` | `String` | No | No | `"draft"` | "draft", "active", "inactive" | N/A | No | None |  |
| `createdByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"promotionCode":1}` | **Options**: `{"unique":true,"sparse":true,"background":true}`

#### Relationships
- **`createdByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for Promotion.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `createdByAccountId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.51 PromotionRule

#### Basic Information
- **Model Name**: PromotionRule
- **File Path**: [models/promotionRule.model.js](file:///models/promotionRule.model.js)
- **Collection Name**: `promotion_rules`
- **Module / Domain**: Promotions
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `promotionId` | `SchemaObjectId` | Yes | No | None | N/A | [Promotion](#3promotion) | No | None |  |
| `ruleType` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `operator` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `ruleValue` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `currencyCode` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `notes` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`promotionId`** &rarr; [Promotion](#3promotion) (`Many-to-One`): Identifies the parent promotion.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `promotionId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.52 PromotionTarget

#### Basic Information
- **Model Name**: PromotionTarget
- **File Path**: [models/promotionTarget.model.js](file:///models/promotionTarget.model.js)
- **Collection Name**: `promotion_targets`
- **Module / Domain**: Promotions
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `promotionId` | `SchemaObjectId` | Yes | No | None | N/A | [Promotion](#3promotion) | No | None |  |
| `targetType` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `targetRefId` | `SchemaObjectId` | No | No | None | N/A | N/A | No | None |  |
| `targetRefCode` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`promotionId`** &rarr; [Promotion](#3promotion) (`Many-to-One`): Identifies the parent promotion.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `promotionId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.53 RecommendationLog

#### Basic Information
- **Model Name**: RecommendationLog
- **File Path**: [models/recommendation-log.model.js](file:///models/recommendation-log.model.js)
- **Collection Name**: `recommendation_logs`
- **Module / Domain**: Skin Journey
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | No | No | None | N/A | [Customer](#3customer) | Yes | None |  |
| `product_id` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | Yes | None |  |
| `context` | `String` | No | No | `"unknown"` | "homepage", "profile_page", "category_page", "preview", "unknown" | N/A | Yes | None |  |
| `category_context` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `score` | `Number` | Yes | No | None | N/A | N/A | No | None |  |
| `reason_codes` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `reasons` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `badges` | `Array<String>` | No | No | `[]` | N/A | N/A | No | None |  |
| `score_breakdown` | `SchemaMixed` | No | No | `undefined` | N/A | N/A | No | None |  |
| `rank_position` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `generated_at` | `Date` | No | No | `undefined` | N/A | N/A | Yes | None |  |
| `algorithm_version` | `String` | No | No | `"rule_v1"` | N/A | N/A | Yes | None |  |
| `session_id` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `request_source` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"customer_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"product_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"context":1}` | **Options**: `{"background":true}`
- **Fields**: `{"generated_at":1}` | **Options**: `{"background":true}`
- **Fields**: `{"algorithm_version":1}` | **Options**: `{"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the referenced Customer for RecommendationLog.
- **`product_id`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the referenced Product for RecommendationLog.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.54 Refund

#### Basic Information
- **Model Name**: Refund
- **File Path**: [models/refund.model.js](file:///models/refund.model.js)
- **Collection Name**: `refunds`
- **Module / Domain**: Returns & Refunds
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | No | None |  |
| `paymentTransactionId` | `SchemaObjectId` | No | No | None | N/A | [PaymentTransaction](#3paymenttransaction) | No | None |  |
| `refundReason` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `refundStatus` | `String` | No | No | `"requested"` | "requested", "approved", "processing", "completed", "rejected" | N/A | No | None |  |
| `requestedAmount` | `Number` | Yes | No | None | N/A | N/A | No | min: 0 |  |
| `approvedAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `refundedAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `currencyCode` | `String` | No | No | `"VND"` | N/A | N/A | No | None |  |
| `requestedByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `approvedByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `requestedAt` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `approvedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `completedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `note` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the order this refund is issued for.
- **`paymentTransactionId`** &rarr; [PaymentTransaction](#3paymenttransaction) (`Many-to-One`): Identifies the transaction being refunded.
- **`requestedByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for Refund.
- **`approvedByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for Refund.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `paymentTransactionId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `requestedByAccountId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `approvedByAccountId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.55 Return

#### Basic Information
- **Model Name**: Return
- **File Path**: [models/return.model.js](file:///models/return.model.js)
- **Collection Name**: `returns`
- **Module / Domain**: Returns & Refunds
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | No | None |  |
| `shipmentId` | `SchemaObjectId` | No | No | None | N/A | [Shipment](#3shipment) | No | None |  |
| `returnNumber` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `returnReason` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `returnStatus` | `String` | No | No | `"requested"` | "requested", "approved", "received", "completed", "rejected" | N/A | No | None |  |
| `requested_by_customer_id` | `SchemaObjectId` | No | No | None | N/A | [Customer](#3customer) | No | None |  |
| `approvedByAccountId` | `SchemaObjectId` | No | No | None | N/A | [Account](#3account) | No | None |  |
| `requestedAt` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `approvedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `receivedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `completedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `note` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"returnNumber":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the order this return request is associated with.
- **`shipmentId`** &rarr; [Shipment](#3shipment) (`Many-to-One`): Identifies the referenced Shipment for Return.
- **`requested_by_customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the referenced Customer for Return.
- **`approvedByAccountId`** &rarr; [Account](#3account) (`Many-to-One`): Identifies the referenced Account for Return.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `shipmentId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `requested_by_customer_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `approvedByAccountId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.56 ReturnItem

#### Basic Information
- **Model Name**: ReturnItem
- **File Path**: [models/returnItem.model.js](file:///models/returnItem.model.js)
- **Collection Name**: `returnitems`
- **Module / Domain**: Returns & Refunds
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `returnId` | `SchemaObjectId` | Yes | No | None | N/A | [Return](#3return) | No | None |  |
| `orderItemId` | `SchemaObjectId` | Yes | No | None | N/A | [OrderItem](#3orderitem) | No | None |  |
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `requestedQty` | `Number` | Yes | No | None | N/A | N/A | No | min: 1 |  |
| `approvedQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `receivedQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `restockQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `rejectQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `restockStatus` | `String` | No | No | `"pending"` | "pending", "restocked", "disposed" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`returnId`** &rarr; [Return](#3return) (`Many-to-One`): Identifies the parent return request.
- **`orderItemId`** &rarr; [OrderItem](#3orderitem) (`Many-to-One`): Identifies the order item being returned.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the referenced ProductVariant for ReturnItem.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `returnId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `orderItemId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.
- **Inconsistent Casing**: Collection is named `returnitems` without an underscore, deviating from the snake_case naming style used elsewhere.

---

### 3.57 Review

#### Basic Information
- **Model Name**: Review
- **File Path**: [models/review.model.js](file:///models/review.model.js)
- **Collection Name**: `reviews`
- **Module / Domain**: Reviews
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | No | None |  |
| `orderItemId` | `SchemaObjectId` | No | No | None | N/A | [OrderItem](#3orderitem) | No | None |  |
| `productId` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `variantId` | `SchemaObjectId` | No | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `rating` | `Number` | Yes | No | None | N/A | N/A | No | min: 1, max: 5 |  |
| `reviewTitle` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `reviewContent` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `reviewStatus` | `String` | No | No | `"visible"` | "visible", "hidden" | N/A | No | None |  |
| `helpfulCount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `verifiedPurchaseFlag` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"productId":1,"reviewStatus":1}` | **Options**: `{"background":true}`
- **Fields**: `{"customer_id":1,"productId":1}` | **Options**: `{"background":true}`
- **Fields**: `{"orderItemId":1,"customer_id":1}` | **Options**: `{"unique":true,"sparse":true,"background":true}`
- **Fields**: `{"reviewStatus":1,"createdAt":-1}` | **Options**: `{"background":true}`

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer who wrote the review.
- **`orderItemId`** &rarr; [OrderItem](#3orderitem) (`Many-to-One`): Identifies the referenced OrderItem for Review.
- **`productId`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the product reviewed.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the referenced ProductVariant for Review.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.58 ReviewMedia

#### Basic Information
- **Model Name**: ReviewMedia
- **File Path**: [models/reviewMedia.model.js](file:///models/reviewMedia.model.js)
- **Collection Name**: `review_medias`
- **Module / Domain**: Reviews
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `reviewId` | `SchemaObjectId` | Yes | No | None | N/A | [Review](#3review) | No | None |  |
| `mediaType` | `String` | No | No | `"image"` | "image", "video" | N/A | No | None |  |
| `mediaUrl` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `sortOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"reviewId":1,"sortOrder":1}` | **Options**: `{"background":true}`

#### Relationships
- **`reviewId`** &rarr; [Review](#3review) (`Many-to-One`): Identifies the parent review.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.59 ReviewSummary

#### Basic Information
- **Model Name**: ReviewSummary
- **File Path**: [models/reviewSummary.model.js](file:///models/reviewSummary.model.js)
- **Collection Name**: `review_summary`
- **Module / Domain**: Reviews
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `productId` | `SchemaObjectId` | Yes | Yes | None | N/A | [Product](#3product) | No | None |  |
| `reviewCount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `averageRating` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `rating1Count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `rating2Count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `rating3Count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `rating4Count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `rating5Count` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"productId":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`productId`** &rarr; [Product](#3product) (`One-to-One`): Identifies the product that this summary tracks.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Singular Collection Name**: Collection `review_summary` is named in the singular form, whereas most other collections use plural forms.

---

### 3.60 ReviewVote

#### Basic Information
- **Model Name**: ReviewVote
- **File Path**: [models/reviewVote.model.js](file:///models/reviewVote.model.js)
- **Collection Name**: `review_votes`
- **Module / Domain**: Reviews
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `reviewId` | `SchemaObjectId` | Yes | No | None | N/A | [Review](#3review) | No | None |  |
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | No | None |  |
| `voteType` | `String` | Yes | No | None | "helpful", "not_helpful" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`reviewId`** &rarr; [Review](#3review) (`Many-to-One`): Identifies the review voted on.
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the customer who voted.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `reviewId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `customer_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.61 Role

#### Basic Information
- **Model Name**: Role
- **File Path**: [models/role.model.js](file:///models/role.model.js)
- **Collection Name**: `roles`
- **Module / Domain**: Auth
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `role_code` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `role_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `description` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `is_system_role` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"role_code":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.62 RolePermission

#### Basic Information
- **Model Name**: RolePermission
- **File Path**: [models/rolePermission.model.js](file:///models/rolePermission.model.js)
- **Collection Name**: `role_permissions`
- **Module / Domain**: Auth
- **Timestamps**: No
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `role_id` | `SchemaObjectId` | Yes | No | None | N/A | [Role](#3role) | Yes | None |  |
| `permission_id` | `SchemaObjectId` | Yes | No | None | N/A | [Permission](#3permission) | Yes | None |  |
| `created_at` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"role_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"permission_id":1}` | **Options**: `{"background":true}`
- **Fields**: `{"role_id":1,"permission_id":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`role_id`** &rarr; [Role](#3role) (`Many-to-One`): Identifies the RBAC role.
- **`permission_id`** &rarr; [Permission](#3permission) (`Many-to-One`): Identifies the RBAC permission.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.63 Shipment

#### Basic Information
- **Model Name**: Shipment
- **File Path**: [models/shipment.model.js](file:///models/shipment.model.js)
- **Collection Name**: `shipments`
- **Module / Domain**: Shipments
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `order_id` | `SchemaObjectId` | Yes | No | None | N/A | [Order](#3order) | No | None |  |
| `warehouseId` | `SchemaObjectId` | No | No | None | N/A | [Warehouse](#3warehouse) | No | None |  |
| `shipmentNumber` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `carrierCode` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `serviceName` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `trackingNumber` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `shipmentStatus` | `String` | No | No | `"pending"` | "pending", "ready_to_ship", "shipped", "in_transit", "delivered", "failed", "returned" | N/A | No | None |  |
| `shippedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `deliveredAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `failedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `shippingFeeAmount` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `currencyCode` | `String` | No | No | `"VND"` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"shipmentNumber":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the order being shipped.
- **`warehouseId`** &rarr; [Warehouse](#3warehouse) (`Many-to-One`): Identifies the referenced Warehouse for Shipment.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `warehouseId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.64 ShipmentEvent

#### Basic Information
- **Model Name**: ShipmentEvent
- **File Path**: [models/shipmentEvent.model.js](file:///models/shipmentEvent.model.js)
- **Collection Name**: `shipment_events`
- **Module / Domain**: Shipments
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `shipmentId` | `SchemaObjectId` | Yes | No | None | N/A | [Shipment](#3shipment) | No | None |  |
| `eventCode` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `eventStatus` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `eventDescription` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `eventTime` | `Date` | No | No | `undefined` | N/A | N/A | No | None |  |
| `locationText` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `rawPayloadJson` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`shipmentId`** &rarr; [Shipment](#3shipment) (`Many-to-One`): Identifies the parent shipment.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `shipmentId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.65 ShipmentItem

#### Basic Information
- **Model Name**: ShipmentItem
- **File Path**: [models/shipmentItem.model.js](file:///models/shipmentItem.model.js)
- **Collection Name**: `shipment_items`
- **Module / Domain**: Shipments
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `shipmentId` | `SchemaObjectId` | Yes | No | None | N/A | [Shipment](#3shipment) | No | None |  |
| `orderItemId` | `SchemaObjectId` | Yes | No | None | N/A | [OrderItem](#3orderitem) | No | None |  |
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `shippedQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `deliveredQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `returnedQty` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`shipmentId`** &rarr; [Shipment](#3shipment) (`Many-to-One`): Identifies the parent shipment.
- **`orderItemId`** &rarr; [OrderItem](#3orderitem) (`Many-to-One`): Identifies the order item shipped.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the referenced ProductVariant for ShipmentItem.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `shipmentId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `orderItemId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.66 ShippingMethod

#### Basic Information
- **Model Name**: ShippingMethod
- **File Path**: [models/shippingMethod.model.js](file:///models/shippingMethod.model.js)
- **Collection Name**: `shipping_methods`
- **Module / Domain**: Shipments
- **Timestamps**: Yes (createdAt: `created_at`, updatedAt: `updated_at`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `shipping_method_code` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `shipping_method_name` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `carrier_code` | `String` | Yes | No | None | N/A | N/A | No | trim |  |
| `service_level` | `String` | No | No | `""` | N/A | N/A | No | trim |  |
| `description` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `is_active` | `Boolean` | No | No | `true` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `created_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updated_at` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"shipping_method_code":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
- `shipping_method_id` virtual field.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.67 StockReservation

#### Basic Information
- **Model Name**: StockReservation
- **File Path**: [models/stockReservation.model.js](file:///models/stockReservation.model.js)
- **Collection Name**: `stock_reservations`
- **Module / Domain**: Checkout
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `warehouseId` | `SchemaObjectId` | Yes | No | None | N/A | [Warehouse](#3warehouse) | No | None |  |
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `cart_id` | `SchemaObjectId` | No | No | None | N/A | [Cart](#3cart) | No | None |  |
| `checkout_session_id` | `SchemaObjectId` | No | No | None | N/A | [CheckoutSession](#3checkoutsession) | No | None |  |
| `order_id` | `SchemaObjectId` | No | No | None | N/A | [Order](#3order) | No | None |  |
| `reservedQty` | `Number` | Yes | No | None | N/A | N/A | No | min: 1,Reserved quantity must be at least 1 |  |
| `reservationStatus` | `String` | No | No | `"active"` | "active", "released", "expired" | N/A | No | None |  |
| `expiresAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `releasedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`warehouseId`** &rarr; [Warehouse](#3warehouse) (`Many-to-One`): Identifies the referenced Warehouse for StockReservation.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the product variant reserved.
- **`cart_id`** &rarr; [Cart](#3cart) (`Many-to-One`): Identifies the referenced Cart for StockReservation.
- **`checkout_session_id`** &rarr; [CheckoutSession](#3checkoutsession) (`Many-to-One`): Identifies the checkout session holding this stock reservation.
- **`order_id`** &rarr; [Order](#3order) (`Many-to-One`): Identifies the referenced Order for StockReservation.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `warehouseId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `cart_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `checkout_session_id` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `order_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.68 VariantMedia

#### Basic Information
- **Model Name**: VariantMedia
- **File Path**: [models/variantMedia.model.js](file:///models/variantMedia.model.js)
- **Collection Name**: `variant_medias`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `mediaType` | `String` | No | No | `"image"` | "image", "video" | N/A | No | None |  |
| `mediaUrl` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `sortOrder` | `Number` | No | No | `0` | N/A | N/A | No | None |  |
| `isPrimary` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the variant linked to this media.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.69 VariantOptionValue

#### Basic Information
- **Model Name**: VariantOptionValue
- **File Path**: [models/variantOptionValue.model.js](file:///models/variantOptionValue.model.js)
- **Collection Name**: `variant_option_values`
- **Module / Domain**: Products
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `variantId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `productOptionValueId` | `SchemaObjectId` | Yes | No | None | N/A | [ProductOptionValue](#3productoptionvalue) | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"variantId":1,"productOptionValueId":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the variant.
- **`productOptionValueId`** &rarr; [ProductOptionValue](#3productoptionvalue) (`Many-to-One`): Identifies the option value (e.g. "Red").

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `productOptionValueId` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.70 Warehouse

#### Basic Information
- **Model Name**: Warehouse
- **File Path**: [models/warehouse.model.js](file:///models/warehouse.model.js)
- **Collection Name**: `warehouses`
- **Module / Domain**: Inventory
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `warehouseCode` | `String` | Yes | Yes | None | N/A | N/A | No | trim, uppercase |  |
| `warehouseName` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `contactName` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `phone` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `addressLine1` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `addressLine2` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `ward` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `district` | `String` | No | No | `""` | N/A | N/A | No | None |  |
| `city` | `String` | Yes | No | None | N/A | N/A | No | None |  |
| `countryCode` | `String` | No | No | `"VN"` | N/A | N/A | No | None |  |
| `warehouseStatus` | `String` | No | No | `"active"` | "active", "inactive" | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
- **Fields**: `{"warehouseCode":1}` | **Options**: `{"unique":true,"background":true}`

#### Relationships
No relationships defined in this schema.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
No notable risks or inconsistencies observed.

---

### 3.71 Wishlist

#### Basic Information
- **Model Name**: Wishlist
- **File Path**: [models/wishlist.model.js](file:///models/wishlist.model.js)
- **Collection Name**: `wishlists`
- **Module / Domain**: Wishlist
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Mongoose inferred

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `customer_id` | `SchemaObjectId` | Yes | No | None | N/A | [Customer](#3customer) | No | None |  |
| `wishlistName` | `String` | No | No | `"My Wishlist"` | N/A | N/A | No | None |  |
| `isDefault` | `Boolean` | No | No | `false` | N/A | N/A | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`customer_id`** &rarr; [Customer](#3customer) (`Many-to-One`): Identifies the referenced Customer for Wishlist.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `customer_id` is not indexed, which may lead to slower query performance during populated reads.

---

### 3.72 WishlistItem

#### Basic Information
- **Model Name**: WishlistItem
- **File Path**: [models/wishlistItem.model.js](file:///models/wishlistItem.model.js)
- **Collection Name**: `wishlist_items`
- **Module / Domain**: Wishlist
- **Timestamps**: Yes (createdAt: `createdAt`, updatedAt: `updatedAt`)
- **Version Key**: `__v`
- **Collection Naming Source**: Explicit

#### Field Details
| Field Name | Type | Required | Unique | Default | Enum | Ref | Index | Validation / Constraints | Description |
|---|---|---|---|---|---|---|---|---|---|
| `wishlistId` | `SchemaObjectId` | Yes | No | None | N/A | [Wishlist](#3wishlist) | No | None |  |
| `productId` | `SchemaObjectId` | Yes | No | None | N/A | [Product](#3product) | No | None |  |
| `variantId` | `SchemaObjectId` | No | No | None | N/A | [ProductVariant](#3productvariant) | No | None |  |
| `_id` | `ObjectId` | No | No | None | N/A | N/A | No | None |  |
| `createdAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `updatedAt` | `Date` | No | No | None | N/A | N/A | No | None |  |
| `__v` | `Number` | No | No | None | N/A | N/A | No | None |  |

#### Indexes
No explicit indexes found in this schema.

#### Relationships
- **`wishlistId`** &rarr; [Wishlist](#3wishlist) (`Many-to-One`): Identifies the parent wishlist.
- **`productId`** &rarr; [Product](#3product) (`Many-to-One`): Identifies the product added to wishlist.
- **`variantId`** &rarr; [ProductVariant](#3productvariant) (`Many-to-One`): Identifies the referenced ProductVariant for WishlistItem.

#### Hooks / Middleware
No schema middleware found.

#### Virtuals
No virtual fields found.

#### Plugins
- `saveSubdocs` plugin
- `shardingPlugin` plugin
- `trackTransaction` plugin
- `validateBeforeSave` plugin
- `addIdGetter` plugin

#### Notes / Risks
- **Missing Index**: Reference field `wishlistId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `productId` is not indexed, which may lead to slower query performance during populated reads.
- **Missing Index**: Reference field `variantId` is not indexed, which may lead to slower query performance during populated reads.

---

## 4. Cross-Model Relationship Map
The table below documents the references between models in the codebase:

| Source Model | Field | Target Model | Relationship Type | Business Meaning |
|---|---|---|---|---|
| AccountAuthProvider | `account_id` | Account | Many-to-One | Identifies the user account that owns this OAuth link. |
| AccountRole | `account_id` | Account | Many-to-One | Identifies the user account assigned to the role. |
| AccountRole | `assigned_by_account_id` | Account | Many-to-One | Identifies the referenced Account for AccountRole. |
| AccountRole | `role_id` | Role | Many-to-One | Identifies the role assigned to the account. |
| Address | `customer_id` | Customer | Many-to-One | Identifies the customer profile that owns this address. |
| AdminProfile | `account_id` | Account | One-to-One | Identifies the user account associated with this admin profile. |
| AdminProfile | `manager_account_id` | Account | Many-to-One | Identifies the referenced Account for AdminProfile. |
| AuditLog | `actor_account_id` | Account | Many-to-One | Identifies the admin/staff account performing the action. |
| Cart | `customer_id` | Customer | Many-to-One | Identifies the customer profile who owns the cart. |
| CartItem | `cart_id` | Cart | Many-to-One | Identifies the parent cart this item belongs to. |
| CartItem | `product_id` | Product | Many-to-One | Identifies the referenced Product for CartItem. |
| CartItem | `variant_id` | ProductVariant | Many-to-One | Identifies the specific product variant added to the cart. |
| Category | `parentCategoryId` | Category | Many-to-One | Identifies the referenced Category for Category. |
| CheckoutAddress | `checkout_session_id` | CheckoutSession | Many-to-One | Identifies the checkout session this address belongs to. |
| CheckoutSession | `applied_coupon_id` | Coupon | Many-to-One | Identifies the referenced Coupon for CheckoutSession. |
| CheckoutSession | `cart_id` | Cart | Many-to-One | Identifies the cart that initiated the checkout. |
| CheckoutSession | `customer_id` | Customer | Many-to-One | Identifies the customer account checking out. |
| CheckoutSession | `selected_billing_address_id` | CheckoutAddress | Many-to-One | Identifies the referenced CheckoutAddress for CheckoutSession. |
| CheckoutSession | `selected_payment_method_id` | PaymentMethod | Many-to-One | Identifies the referenced PaymentMethod for CheckoutSession. |
| CheckoutSession | `selected_shipping_address_id` | CheckoutAddress | Many-to-One | Identifies the referenced CheckoutAddress for CheckoutSession. |
| CheckoutSession | `selected_shipping_method_id` | CheckoutShippingMethod | Many-to-One | Identifies the referenced CheckoutShippingMethod for CheckoutSession. |
| CheckoutShippingMethod | `checkout_session_id` | CheckoutSession | Many-to-One | Identifies the checkout session this shipping method is selected for. |
| CheckoutShippingMethod | `shipping_method_id` | ShippingMethod | Many-to-One | Identifies the selected shipping method. |
| Coupon | `promotionId` | Promotion | Many-to-One | Identifies the referenced Promotion for Coupon. |
| CouponRedemption | `couponId` | Coupon | Many-to-One | Identifies the coupon being redeemed. |
| CouponRedemption | `customer_id` | Customer | Many-to-One | Identifies the customer who redeemed the coupon. |
| CouponRedemption | `order_id` | Order | Many-to-One | Identifies the order in which the coupon was redeemed. |
| Customer | `account_id` | Account | One-to-One | Identifies the authentication account associated with this customer profile. |
| CustomerBeautyProfile | `customer_id` | Customer | One-to-One | Identifies the customer profile this beauty profile belongs to. |
| CustomerConsent | `customer_id` | Customer | Many-to-One | Identifies the customer who gave the consent. |
| CustomerCoupon | `couponId` | Coupon | Many-to-One | Identifies the coupon associated with the customer. |
| CustomerCoupon | `customer_id` | Customer | Many-to-One | Identifies the customer eligible to use this coupon. |
| CustomerPreference | `customer_id` | Customer | Many-to-One | Identifies the customer who has these preferences. |
| CustomerRecommendationSnapshot | `customer_id` | Customer | Many-to-One | Identifies the customer profile this recommendation snapshot is generated for. |
| EmailOtp | `account_id` | Account | Many-to-One | Identifies the referenced Account for EmailOtp. |
| InventoryBalance | `variantId` | ProductVariant | Many-to-One | Identifies the product variant stocked. |
| InventoryBalance | `warehouseId` | Warehouse | Many-to-One | Identifies the warehouse where stock is kept. |
| InventoryTransaction | `performedByAccountId` | Account | Many-to-One | Identifies the referenced Account for InventoryTransaction. |
| InventoryTransaction | `variantId` | ProductVariant | Many-to-One | Identifies the product variant for the transaction. |
| InventoryTransaction | `warehouseId` | Warehouse | Many-to-One | Identifies the warehouse where stock change happened. |
| LoyaltyAccount | `customer_id` | Customer | Many-to-One | Identifies the customer profile this loyalty account belongs to. |
| LoyaltyAccount | `tierId` | LoyaltyTier | Many-to-One | Identifies the referenced LoyaltyTier for LoyaltyAccount. |
| LoyaltyPointLedger | `customer_id` | Customer | Many-to-One | Identifies the customer associated with this point ledger entry. |
| LoyaltyPointLedger | `loyaltyAccountId` | LoyaltyAccount | Many-to-One | Identifies the parent loyalty account. |
| LoyaltyPointLedger | `order_id` | Order | Many-to-One | Identifies the order associated with this point transaction. |
| Order | `checkout_session_id` | CheckoutSession | Many-to-One | Identifies the referenced CheckoutSession for Order. |
| Order | `customer_id` | Customer | Many-to-One | Identifies the customer profile who placed the order. |
| OrderAddress | `order_id` | Order | Many-to-One | Identifies the order this address belongs to. |
| OrderItem | `order_id` | Order | Many-to-One | Identifies the parent order for this line item. |
| OrderItem | `product_id` | Product | Many-to-One | Identifies the referenced Product for OrderItem. |
| OrderItem | `variant_id` | ProductVariant | Many-to-One | Identifies the product variant purchased. |
| OrderStatusHistory | `changed_by_account_id` | Account | Many-to-One | Identifies the referenced Account for OrderStatusHistory. |
| OrderStatusHistory | `order_id` | Order | Many-to-One | Identifies the parent order. |
| OrderTotal | `order_id` | Order | Many-to-One | Identifies the parent order. |
| PaymentIntent | `order_id` | Order | Many-to-One | Identifies the order that this payment intent belongs to. |
| PaymentIntent | `payment_method_id` | PaymentMethod | Many-to-One | Identifies the referenced PaymentMethod for PaymentIntent. |
| PaymentTransaction | `order_id` | Order | Many-to-One | Identifies the referenced Order for PaymentTransaction. |
| PaymentTransaction | `paymentIntentId` | PaymentIntent | Many-to-One | Identifies the parent payment intent. |
| PriceBookEntry | `priceBookId` | PriceBook | Many-to-One | Identifies the price book defining this entry. |
| PriceBookEntry | `variantId` | ProductVariant | Many-to-One | Identifies the product variant priced. |
| PriceHistory | `changedByAccountId` | Account | Many-to-One | Identifies the referenced Account for PriceHistory. |
| PriceHistory | `priceBookId` | PriceBook | Many-to-One | Identifies the referenced PriceBook for PriceHistory. |
| PriceHistory | `variantId` | ProductVariant | Many-to-One | Identifies the product variant whose price changed. |
| Product | `brandId` | Brand | Many-to-One | Identifies the referenced Brand for Product. |
| Product | `categoryId` | Category | Many-to-One | Identifies the referenced Category for Product. |
| Product | `createdByAccountId` | Account | Many-to-One | Identifies the referenced Account for Product. |
| Product | `updatedByAccountId` | Account | Many-to-One | Identifies the referenced Account for Product. |
| ProductAttribute | `productId` | Product | Many-to-One | Identifies the referenced Product for ProductAttribute. |
| ProductBeautyProfile | `product_id` | Product | One-to-One | Identifies the product that matches this beauty profile criteria. |
| ProductCategory | `categoryId` | Category | Many-to-One | Identifies the category mapped to the product. |
| ProductCategory | `productId` | Product | Many-to-One | Identifies the product mapped to a category. |
| ProductMedia | `productId` | Product | Many-to-One | Identifies the product this media belongs to. |
| ProductOption | `productId` | Product | Many-to-One | Identifies the parent product. |
| ProductOptionValue | `productOptionId` | ProductOption | Many-to-One | Identifies the parent product option. |
| ProductVariant | `productId` | Product | Many-to-One | Identifies the parent product this variant belongs to. |
| Promotion | `createdByAccountId` | Account | Many-to-One | Identifies the referenced Account for Promotion. |
| PromotionRule | `promotionId` | Promotion | Many-to-One | Identifies the parent promotion. |
| PromotionTarget | `promotionId` | Promotion | Many-to-One | Identifies the parent promotion. |
| RecommendationLog | `customer_id` | Customer | Many-to-One | Identifies the referenced Customer for RecommendationLog. |
| RecommendationLog | `product_id` | Product | Many-to-One | Identifies the referenced Product for RecommendationLog. |
| Refund | `approvedByAccountId` | Account | Many-to-One | Identifies the referenced Account for Refund. |
| Refund | `order_id` | Order | Many-to-One | Identifies the order this refund is issued for. |
| Refund | `paymentTransactionId` | PaymentTransaction | Many-to-One | Identifies the transaction being refunded. |
| Refund | `requestedByAccountId` | Account | Many-to-One | Identifies the referenced Account for Refund. |
| Return | `approvedByAccountId` | Account | Many-to-One | Identifies the referenced Account for Return. |
| Return | `order_id` | Order | Many-to-One | Identifies the order this return request is associated with. |
| Return | `requested_by_customer_id` | Customer | Many-to-One | Identifies the referenced Customer for Return. |
| Return | `shipmentId` | Shipment | Many-to-One | Identifies the referenced Shipment for Return. |
| ReturnItem | `orderItemId` | OrderItem | Many-to-One | Identifies the order item being returned. |
| ReturnItem | `returnId` | Return | Many-to-One | Identifies the parent return request. |
| ReturnItem | `variantId` | ProductVariant | Many-to-One | Identifies the referenced ProductVariant for ReturnItem. |
| Review | `customer_id` | Customer | Many-to-One | Identifies the customer who wrote the review. |
| Review | `orderItemId` | OrderItem | Many-to-One | Identifies the referenced OrderItem for Review. |
| Review | `productId` | Product | Many-to-One | Identifies the product reviewed. |
| Review | `variantId` | ProductVariant | Many-to-One | Identifies the referenced ProductVariant for Review. |
| ReviewMedia | `reviewId` | Review | Many-to-One | Identifies the parent review. |
| ReviewSummary | `productId` | Product | One-to-One | Identifies the product that this summary tracks. |
| ReviewVote | `customer_id` | Customer | Many-to-One | Identifies the customer who voted. |
| ReviewVote | `reviewId` | Review | Many-to-One | Identifies the review voted on. |
| RolePermission | `permission_id` | Permission | Many-to-One | Identifies the RBAC permission. |
| RolePermission | `role_id` | Role | Many-to-One | Identifies the RBAC role. |
| Shipment | `order_id` | Order | Many-to-One | Identifies the order being shipped. |
| Shipment | `warehouseId` | Warehouse | Many-to-One | Identifies the referenced Warehouse for Shipment. |
| ShipmentEvent | `shipmentId` | Shipment | Many-to-One | Identifies the parent shipment. |
| ShipmentItem | `orderItemId` | OrderItem | Many-to-One | Identifies the order item shipped. |
| ShipmentItem | `shipmentId` | Shipment | Many-to-One | Identifies the parent shipment. |
| ShipmentItem | `variantId` | ProductVariant | Many-to-One | Identifies the referenced ProductVariant for ShipmentItem. |
| StockReservation | `cart_id` | Cart | Many-to-One | Identifies the referenced Cart for StockReservation. |
| StockReservation | `checkout_session_id` | CheckoutSession | Many-to-One | Identifies the checkout session holding this stock reservation. |
| StockReservation | `order_id` | Order | Many-to-One | Identifies the referenced Order for StockReservation. |
| StockReservation | `variantId` | ProductVariant | Many-to-One | Identifies the product variant reserved. |
| StockReservation | `warehouseId` | Warehouse | Many-to-One | Identifies the referenced Warehouse for StockReservation. |
| VariantMedia | `variantId` | ProductVariant | Many-to-One | Identifies the variant linked to this media. |
| VariantOptionValue | `productOptionValueId` | ProductOptionValue | Many-to-One | Identifies the option value (e.g. "Red"). |
| VariantOptionValue | `variantId` | ProductVariant | Many-to-One | Identifies the variant. |
| Wishlist | `customer_id` | Customer | Many-to-One | Identifies the referenced Customer for Wishlist. |
| WishlistItem | `productId` | Product | Many-to-One | Identifies the product added to wishlist. |
| WishlistItem | `variantId` | ProductVariant | Many-to-One | Identifies the referenced ProductVariant for WishlistItem. |
| WishlistItem | `wishlistId` | Wishlist | Many-to-One | Identifies the parent wishlist. |

---

## 5. Collection Naming Check
The checklist below evaluates whether collection names match their expected snake_case plural standard:

| Model Name | Expected Collection Name | Actual Collection Name | Explicit or Inferred | Naming Status | Recommendation |
|---|---|---|---|---|---|
| Account | `accounts` | `accounts` | Explicit | OK | None (naming is clear and appropriate) |
| AccountAuthProvider | `account_auth_providers` | `account_auth_providers` | Explicit | OK | None (naming is clear and appropriate) |
| AccountRole | `account_roles` | `account_roles` | Explicit | OK | None (naming is clear and appropriate) |
| Address | `customer_addresses` | `customer_addresses` | Explicit | OK | None (naming is clear and appropriate) |
| AdminProfile | `admin_profiles` | `admin_profiles` | Explicit | OK | None (naming is clear and appropriate) |
| AuditLog | `audit_logs` | `audit_logs` | Explicit | OK | None (naming is clear and appropriate) |
| BeautyReference | `beauty_references` | `beauty_references` | Explicit | OK | None (naming is clear and appropriate) |
| Brand | `brands` | `brands` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| Cart | `carts` | `carts` | Explicit | OK | None (naming is clear and appropriate) |
| CartItem | `cart_items` | `cart_items` | Explicit | OK | None (naming is clear and appropriate) |
| Category | `categories` | `categories` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| CheckoutAddress | `checkout_address` | `checkout_addresses` | Explicit | Inferred by Mongoose | Ensure the inferred pluralization `checkout_addresses` matches developer expectations. |
| CheckoutSession | `checkout_sessions` | `checkout_sessions` | Explicit | OK | None (naming is clear and appropriate) |
| CheckoutShippingMethod | `checkout_shipping_methods` | `checkout_shipping_methods` | Explicit | OK | None (naming is clear and appropriate) |
| Coupon | `coupons` | `coupons` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| CouponRedemption | `coupon_redemptions` | `coupon_redemptions` | Explicit | OK | None (naming is clear and appropriate) |
| Customer | `customer_profiles` | `customer_profiles` | Explicit | OK | None (naming is clear and appropriate) |
| CustomerBeautyProfile | `customer_beauty_profiles` | `customer_beauty_profiles` | Explicit | OK | None (naming is clear and appropriate) |
| CustomerConsent | `customer_consents` | `customer_consents` | Explicit | OK | None (naming is clear and appropriate) |
| CustomerCoupon | `customer_coupons` | `customer_coupons` | Explicit | OK | None (naming is clear and appropriate) |
| CustomerPreference | `customer_preferences` | `customer_preferences` | Explicit | OK | None (naming is clear and appropriate) |
| CustomerRecommendationSnapshot | `customer_recommendation_snapshots` | `customer_recommendation_snapshots` | Explicit | OK | None (naming is clear and appropriate) |
| EmailOtp | `email_otps` | `email_otps` | Explicit | OK | None (naming is clear and appropriate) |
| GuestSession | `guest_sessions` | `guest_sessions` | Explicit | OK | None (naming is clear and appropriate) |
| InventoryBalance | `inventory_balances` | `inventory_balances` | Explicit | OK | None (naming is clear and appropriate) |
| InventoryTransaction | `inventory_transactions` | `inventory_transactions` | Explicit | OK | None (naming is clear and appropriate) |
| LoyaltyAccount | `loyalty_accounts` | `loyalty_accounts` | Explicit | OK | None (naming is clear and appropriate) |
| LoyaltyPointLedger | `loyalty_point_ledgers` | `loyalty_point_ledger` | Explicit | Needs Review | Rename collection to plural `loyalty_point_ledgers` to align with pluralization standards. |
| LoyaltyTier | `loyalty_tiers` | `loyalty_tiers` | Explicit | OK | None (naming is clear and appropriate) |
| Order | `orders` | `orders` | Explicit | OK | None (naming is clear and appropriate) |
| OrderAddress | `order_address` | `order_addresses` | Explicit | Inferred by Mongoose | Ensure the inferred pluralization `order_addresses` matches developer expectations. |
| OrderItem | `order_items` | `order_items` | Explicit | OK | None (naming is clear and appropriate) |
| OrderStatusHistory | `order_status_histories` | `order_status_history` | Explicit | Needs Review | Rename collection to plural `order_status_histories` to align with pluralization standards. |
| OrderTotal | `order_totals` | `order_totals` | Explicit | OK | None (naming is clear and appropriate) |
| PaymentIntent | `payment_intents` | `payment_intents` | Explicit | OK | None (naming is clear and appropriate) |
| PaymentMethod | `payment_methods` | `payment_methods` | Explicit | OK | None (naming is clear and appropriate) |
| PaymentTransaction | `payment_transactions` | `payment_transactions` | Explicit | OK | None (naming is clear and appropriate) |
| Permission | `permissions` | `permissions` | Explicit | OK | None (naming is clear and appropriate) |
| PriceBook | `price_books` | `price_books` | Explicit | OK | None (naming is clear and appropriate) |
| PriceBookEntry | `price_book_entries` | `price_book_entries` | Explicit | OK | None (naming is clear and appropriate) |
| PriceHistory | `price_histories` | `price_histories` | Explicit | OK | None (naming is clear and appropriate) |
| Product | `products` | `products` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| ProductAttribute | `product_attributes` | `product_attributes` | Explicit | OK | None (naming is clear and appropriate) |
| ProductBeautyProfile | `product_beauty_profiles` | `product_beauty_profiles` | Explicit | OK | None (naming is clear and appropriate) |
| ProductCategory | `product_categories` | `product_categories` | Explicit | OK | None (naming is clear and appropriate) |
| ProductMedia | `product_medias` | `product_media` | Explicit | Inferred by Mongoose | Ensure the inferred pluralization `product_media` matches developer expectations. |
| ProductOption | `product_options` | `product_options` | Explicit | OK | None (naming is clear and appropriate) |
| ProductOptionValue | `product_option_values` | `product_option_values` | Explicit | OK | None (naming is clear and appropriate) |
| ProductVariant | `product_variants` | `product_variants` | Explicit | OK | None (naming is clear and appropriate) |
| Promotion | `promotions` | `promotions` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| PromotionRule | `promotion_rules` | `promotion_rules` | Explicit | OK | None (naming is clear and appropriate) |
| PromotionTarget | `promotion_targets` | `promotion_targets` | Explicit | OK | None (naming is clear and appropriate) |
| RecommendationLog | `recommendation_logs` | `recommendation_logs` | Explicit | OK | None (naming is clear and appropriate) |
| Refund | `refunds` | `refunds` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| Return | `returns` | `returns` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| ReturnItem | `return_items` | `returnitems` | Mongoose inferred | Needs Review | Rename collection to `return_items` to follow snake_case spacing standards. |
| Review | `reviews` | `reviews` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| ReviewMedia | `review_medias` | `review_medias` | Explicit | OK | None (naming is clear and appropriate) |
| ReviewSummary | `review_summaries` | `review_summary` | Explicit | Needs Review | Rename collection to plural `review_summaries` to align with pluralization standards. |
| ReviewVote | `review_votes` | `review_votes` | Explicit | OK | None (naming is clear and appropriate) |
| Role | `roles` | `roles` | Explicit | OK | None (naming is clear and appropriate) |
| RolePermission | `role_permissions` | `role_permissions` | Explicit | OK | None (naming is clear and appropriate) |
| Shipment | `shipments` | `shipments` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| ShipmentEvent | `shipment_events` | `shipment_events` | Explicit | OK | None (naming is clear and appropriate) |
| ShipmentItem | `shipment_items` | `shipment_items` | Explicit | OK | None (naming is clear and appropriate) |
| ShippingMethod | `shipping_methods` | `shipping_methods` | Explicit | OK | None (naming is clear and appropriate) |
| StockReservation | `stock_reservations` | `stock_reservations` | Explicit | OK | None (naming is clear and appropriate) |
| VariantMedia | `variant_medias` | `variant_medias` | Explicit | OK | None (naming is clear and appropriate) |
| VariantOptionValue | `variant_option_values` | `variant_option_values` | Explicit | OK | None (naming is clear and appropriate) |
| Warehouse | `warehouses` | `warehouses` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| Wishlist | `wishlists` | `wishlists` | Mongoose inferred | OK | None (naming is clear and appropriate) |
| WishlistItem | `wishlist_items` | `wishlist_items` | Explicit | OK | None (naming is clear and appropriate) |

---

## 6. Field Naming Consistency Check
The following table summarizes issues where naming conventions deviate between models:

| Issue | Models Affected | Description | Recommendation |
|---|---|---|---|
| **Timestamp Naming Splitting** | 43 models (e.g. `Product`, `Review`) use camelCase (`createdAt`/`updatedAt`).<br>29 models (e.g. `Account`, `Order`) use snake_case (`created_at`/`updated_at`). | Models are split down the middle in timestamp conventions. | Standardize all models to use Mongoose default camelCase timestamps or explicit snake_case timestamps. |
| **Foreign Key Casing** | e.g. `CartItem`, `InventoryBalance`, `OrderItem`, `PriceBookEntry` | Inconsistent casing for product references (`product_id` vs `productId`) and variant references (`variant_id` vs `variantId`). | Standardize foreign key names. E.g., decide on snake_case (`product_id`, `variant_id`) or camelCase (`productId`, `variantId`) across all schemas. |
| **Status Field Case Casing** | e.g. `Account`, `Product`, `Coupon`, `CustomerCoupon` | Models mix snake_case (`account_status`, `order_status`) with camelCase (`productStatus`, `variantStatus`) and plain status (`status`). | Standardize status fields to a single format, e.g., `status` or `[entity]_status` in snake_case. |
| **Price / Currency Suffix** | e.g. `Cart`, `OrderItem`, `Product`, `Refund` | Models mix using `_amount` or `Amount` suffix (e.g., `subtotal_amount` vs `requestedAmount`) with raw field names (e.g., `price` in `Product`, `amount` in `PaymentTransaction`). | Adopt a consistent naming rule for money and currency fields (e.g. suffixing all currency properties with `_amount` in snake_case). |
| **Singular/Underscore Collection Names** | `LoyaltyPointLedger`, `OrderStatusHistory`, `ReturnItem`, `ReviewSummary` | Collections are named in singular form (`loyalty_point_ledger`, `order_status_history`, `review_summary`) or lack underscore (`returnitems`). | Adjust these collection names to plural form with appropriate snake_case separators. |

---

## 7. Recommended Final Entity Dictionary
This dictionary provides a unified vocabulary for the frontend and backend teams:

### 7.1 Account
- **Entity Name**: Account
- **Collection Name**: `accounts`
- **Main ID Field**: `_id` (API alias: `account_id`)
- **Display Name Field**: `email` or `username`
- **Important Status Field**: `account_status` (enum: "active", "inactive", "locked")
- **Important Relationship Fields**: None
- **Main Frontend Screens**: Login, Registration, Account Settings, Security Portal

### 7.2 Customer
- **Entity Name**: Customer Profile
- **Collection Name**: `customer_profiles`
- **Main ID Field**: `_id`
- **Display Name Field**: `first_name` + `last_name`
- **Important Status Field**: `customer_status`
- **Important Relationship Fields**: `account_id` &rarr; Account
- **Main Frontend Screens**: User Profile, Account Dashboard, Order Recipient Details

### 7.3 Product
- **Entity Name**: Product
- **Collection Name**: `products`
- **Main ID Field**: `_id`
- **Display Name Field**: `name`
- **Important Status Field**: `productStatus` (enum: "active", "inactive", "draft")
- **Important Relationship Fields**: `brandId` &rarr; Brand, `categoryId` &rarr; Category
- **Main Frontend Screens**: Home Feed, Catalog Search, Product Listing Page (PLP)

### 7.4 ProductVariant (SKU)
- **Entity Name**: Product Variant
- **Collection Name**: `product_variants`
- **Main ID Field**: `_id`
- **Display Name Field**: `sku`
- **Important Status Field**: `variantStatus`
- **Important Relationship Fields**: `productId` &rarr; Product
- **Main Frontend Screens**: Product Detail Page (PDP) selection, Add to Cart

### 7.5 Cart
- **Entity Name**: Cart
- **Collection Name**: `carts`
- **Main ID Field**: `_id`
- **Display Name Field**: N/A
- **Important Status Field**: `cart_status` (enum: "active", "converted", "abandoned")
- **Important Relationship Fields**: `customer_id` &rarr; Customer, `guest_session_id` &rarr; GuestSession
- **Main Frontend Screens**: Cart Details Page, Checkout Entry

### 7.6 Order
- **Entity Name**: Order
- **Collection Name**: `orders`
- **Main ID Field**: `_id`
- **Display Name Field**: `order_number`
- **Important Status Field**: `order_status`, `payment_status`, `fulfillment_status`
- **Important Relationship Fields**: `customer_id` &rarr; Customer
- **Main Frontend Screens**: Checkout Success, Order List, Order Tracking Details

### 7.7 Coupon
- **Entity Name**: Coupon
- **Collection Name**: `coupons`
- **Main ID Field**: `_id`
- **Display Name Field**: `couponCode`
- **Important Status Field**: `couponStatus`
- **Important Relationship Fields**: `promotionId` &rarr; Promotion
- **Main Frontend Screens**: Cart Voucher Input, Available Promotions List

### 7.8 LoyaltyAccount
- **Entity Name**: Loyalty Account
- **Collection Name**: `loyalty_accounts`
- **Main ID Field**: `_id`
- **Display Name Field**: N/A
- **Important Status Field**: `loyaltyStatus`
- **Important Relationship Fields**: `customer_id` &rarr; Customer, `tierId` &rarr; LoyaltyTier
- **Main Frontend Screens**: Rewards Hub, Profile Points Widget

### 7.9 Review
- **Entity Name**: Product Review
- **Collection Name**: `reviews`
- **Main ID Field**: `_id`
- **Display Name Field**: `reviewTitle`
- **Important Status Field**: `reviewStatus` (enum: "approved", "pending", "rejected")
- **Important Relationship Fields**: `customer_id` &rarr; Customer, `productId` &rarr; Product, `variantId` &rarr; ProductVariant
- **Main Frontend Screens**: Product Detail Reviews Section, Write a Review Form

### 7.10 Return
- **Entity Name**: Return Request
- **Collection Name**: `returns`
- **Main ID Field**: `_id`
- **Display Name Field**: `return_number`
- **Important Status Field**: `returnStatus`
- **Important Relationship Fields**: `order_id` &rarr; Order, `requested_by_customer_id` &rarr; Customer
- **Main Frontend Screens**: Order History Returns Portal, Return Status Tracking

---

## 8. Backend-to-Frontend Notes
For Android (Java/XML) client-side mobile developers, the following mapping defines which entities and model relationships are critical for each app screen flow:

1. **Auth & Profile Setup**:
   - Primary Models: `Account`, `AccountAuthProvider`, `EmailOtp`, `Customer`
   - Implementation Notes: EmailOTP is passwordless. Mobile must request OTP first, then verify to get account tokens. Profile must be linked to account via `account_id`.

2. **Product Catalog Browsing**:
   - Primary Models: `Product`, `ProductVariant`, `ProductMedia`, `Category`, `ProductCategory`, `Brand`
   - Implementation Notes: Catalog uses `Product` for base display, while buying actions require picking a specific `ProductVariant` (distinguished by options like size/color). Images are linked via `ProductMedia` or `VariantMedia`.

3. **Cart & Wishlist**:
   - Primary Models: `Cart`, `CartItem`, `Wishlist`, `WishlistItem`
   - Implementation Notes: Local carts must map to `guest_session_id` for guest flows, then merge with customer-linked `Cart` on login using `customer_id`.

4. **Checkout & Shipping**:
   - Primary Models: `CheckoutSession`, `CheckoutAddress`, `CheckoutShippingMethod`, `StockReservation`, `ShippingMethod`
   - Implementation Notes: Checkout sessions hold a temporary lock on inventory using `StockReservation`. Address must be populated to resolve eligible `ShippingMethod` entries.

5. **Order Management & Tracking**:
   - Primary Models: `Order`, `OrderAddress`, `OrderItem`, `OrderTotal`, `OrderStatusHistory`
   - Implementation Notes: Subtotal and discount totals are separated from main order object in `OrderTotal`. Status tracking relies on `OrderStatusHistory` to show chronological shipping milestones.

6. **Payments**:
   - Primary Models: `PaymentIntent`, `PaymentMethod`, `PaymentTransaction`
   - Implementation Notes: Mobile integrates Stripe/PayOS using tokenized inputs. Backend registers payments under `PaymentIntent` linked to the `Order`.

7. **Loyalty / Rewards**:
   - Primary Models: `LoyaltyAccount`, `LoyaltyPointLedger`, `LoyaltyTier`
   - Implementation Notes: Display customer loyalty points, current tier benefits, and point transaction logs from the point ledger.

8. **Reviews & Feedback**:
   - Primary Models: `Review`, `ReviewMedia`, `ReviewSummary`, `ReviewVote`
   - Implementation Notes: Display average ratings and review distribution counts from `ReviewSummary` on product details. Allow uploading images/videos which map to `ReviewMedia`.

---

## 9. Final Checklist
- [x] Every model has a clear collection name (explicitly defined or Mongoose inferred)
- [x] Every required field is documented
- [x] Every enum is documented
- [x] Every relationship is documented
- [x] Every index is documented
- [x] Sensitive fields are identified (e.g. OTP codes, account credentials)
- [x] Frontend-critical fields are identified
- [x] Potential naming inconsistencies are listed
