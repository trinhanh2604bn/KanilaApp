# Kanila Backend API Endpoints

| Method | Path | Middlewares / Handler Function |
|---|---|---|
| POST | `/api/accounts` | createAccount |
| GET | `/api/accounts` | createAccount |
| GET | `/api/brands` | getAllBrands |
| POST | `/api/brands` | getAllBrands |
| GET | `/api/brands/:id` | getBrandById |
| PUT | `/api/brands/:id` | getBrandById |
| DELETE | `/api/brands/:id` | getBrandById |
| GET | `/api/categories` | getAllCategories |
| POST | `/api/categories` | getAllCategories |
| GET | `/api/categories/:id` | getCategoryById |
| PUT | `/api/categories/:id` | getCategoryById |
| DELETE | `/api/categories/:id` | getCategoryById |
| GET | `/api/products` | getAllProducts |
| POST | `/api/products` | getAllProducts |
| GET | `/api/products/slug/:slug` | getProductBySlug |
| GET | `/api/products/:id` | getProductById |
| PUT | `/api/products/:id` | getProductById |
| PATCH | `/api/products/:id` | getProductById |
| DELETE | `/api/products/:id` | getProductById |
| GET | `/api/catalog/facets` | getCatalogFacets |
| GET | `/api/catalog/shades` | getDistinctShades |
| GET | `/api/catalog/skin-types` | getDistinctSkinTypes |
| GET | `/api/catalog` | getCatalogBundle |
| GET | `/api/product-categories` | getAllProductCategories |
| POST | `/api/product-categories` | getAllProductCategories |
| GET | `/api/product-categories/product/:productId` | getByProductId |
| GET | `/api/product-categories/:id` | getProductCategoryById |
| PUT | `/api/product-categories/:id` | getProductCategoryById |
| DELETE | `/api/product-categories/:id` | getProductCategoryById |
| POST | `/api/auth/register` | register |
| POST | `/api/auth/login` | login |
| POST | `/api/auth/verify-otp` | verifyOtp |
| GET | `/api/auth/me` | authMiddleware, getMe |
| POST | `/api/auth/check-email` | deprecated410 |
| POST | `/api/auth/verify-reset-otp` | deprecated410 |
| POST | `/api/auth/reset-password` | deprecated410 |
| GET | `/api/accounts/profile-hub` | authMiddleware, getProfileHub |
| PATCH | `/api/accounts/profile` | authMiddleware, patchMyProfile |
| GET | `/api/accounts/skin-profile` | authMiddleware, getMySkinProfile |
| PATCH | `/api/accounts/skin-profile` | authMiddleware, getMySkinProfile |
| POST | `/api/accounts/addresses` | authMiddleware, postMyAddress |
| GET | `/api/accounts/addresses` | authMiddleware, postMyAddress |
| PATCH | `/api/accounts/addresses/:id/default` | authMiddleware, patchMyDefaultAddress |
| PATCH | `/api/accounts/addresses/:id` | authMiddleware, patchMyAddress |
| DELETE | `/api/accounts/addresses/:id` | authMiddleware, patchMyAddress |
| GET | `/api/accounts/providers` | authMiddleware, getMyProviders |
| GET | `/api/accounts/security-status` | authMiddleware, getMySecurityStatus |
| DELETE | `/api/accounts/providers/:provider` | authMiddleware, unlinkMyProvider |
| GET | `/api/accounts/reviews` | authMiddleware, getMyReviews |
| PATCH | `/api/accounts/reviews/:id` | authMiddleware, patchMyReview |
| DELETE | `/api/accounts/reviews/:id` | authMiddleware, patchMyReview |
| GET | `/api/accounts/:id` | getAccountById |
| PUT | `/api/accounts/:id` | getAccountById |
| PATCH | `/api/accounts/:id` | getAccountById |
| DELETE | `/api/accounts/:id` | getAccountById |
| GET | `/api/account/profile-hub` | authMiddleware, getProfileHub |
| PATCH | `/api/account/profile` | authMiddleware, patchMyProfile |
| GET | `/api/account/skin-profile` | authMiddleware, getMySkinProfile |
| PATCH | `/api/account/skin-profile` | authMiddleware, getMySkinProfile |
| POST | `/api/account/addresses` | authMiddleware, postMyAddress |
| GET | `/api/account/addresses` | authMiddleware, postMyAddress |
| PATCH | `/api/account/addresses/:id/default` | authMiddleware, patchMyDefaultAddress |
| PATCH | `/api/account/addresses/:id` | authMiddleware, patchMyAddress |
| DELETE | `/api/account/addresses/:id` | authMiddleware, patchMyAddress |
| GET | `/api/account/providers` | authMiddleware, getMyProviders |
| GET | `/api/account/security-status` | authMiddleware, getMySecurityStatus |
| DELETE | `/api/account/providers/:provider` | authMiddleware, unlinkMyProvider |
| GET | `/api/account/reviews` | authMiddleware, getMyReviews |
| PATCH | `/api/account/reviews/:id` | authMiddleware, patchMyReview |
| DELETE | `/api/account/reviews/:id` | authMiddleware, patchMyReview |
| GET | `/api/account` | getAllAccounts |
| POST | `/api/account` | getAllAccounts |
| GET | `/api/account/:id` | getAccountById |
| PUT | `/api/account/:id` | getAccountById |
| PATCH | `/api/account/:id` | getAccountById |
| DELETE | `/api/account/:id` | getAccountById |
| GET | `/api/customers/me` | authMiddleware, getMyCustomerProfile |
| GET | `/api/customers` | getAllCustomers |
| GET | `/api/customers/account/:account_id` | getCustomerByAccountId |
| GET | `/api/customers/:id` | getCustomerById |
| PUT | `/api/customers/:id` | getCustomerById |
| PATCH | `/api/customers/:id` | getCustomerById |
| DELETE | `/api/customers/:id` | getCustomerById |
| GET | `/api/customer/me` | authMiddleware, getMyCustomerProfile |
| GET | `/api/customer` | getAllCustomers |
| GET | `/api/customer/account/:account_id` | getCustomerByAccountId |
| GET | `/api/customer/:id` | getCustomerById |
| PUT | `/api/customer/:id` | getCustomerById |
| PATCH | `/api/customer/:id` | getCustomerById |
| DELETE | `/api/customer/:id` | getCustomerById |
| GET | `/api/addresses/me` | authMiddleware, getMyAddresses |
| GET | `/api/addresses` | getAllAddresses |
| POST | `/api/addresses` | getAllAddresses |
| GET | `/api/addresses/customer/:customer_id` | getAddressesByCustomerId |
| GET | `/api/addresses/:id` | getAddressById |
| PUT | `/api/addresses/:id` | getAddressById |
| DELETE | `/api/addresses/:id` | getAddressById |
| GET | `/api/customer-consents` | getAll |
| POST | `/api/customer-consents` | getAll |
| GET | `/api/customer-consents/customer/:customer_id` | getByCustomer |
| GET | `/api/customer-consents/:id` | getById |
| PUT | `/api/customer-consents/:id` | getById |
| DELETE | `/api/customer-consents/:id` | getById |
| GET | `/api/customer-preferences` | getAll |
| POST | `/api/customer-preferences` | getAll |
| GET | `/api/customer-preferences/customer/:customer_id` | getByCustomer |
| GET | `/api/customer-preferences/:id` | getById |
| DELETE | `/api/customer-preferences/:id` | getById |
| GET | `/api/beauty-references` | getReferences |
| POST | `/api/beauty-references` | getReferences |
| GET | `/api/beauty-references/group/:reference_group` | getReferenceGroup |
| PUT | `/api/beauty-references/:id` | updateReference |
| DELETE | `/api/beauty-references/:id` | updateReference |
| GET | `/api/customers/:customer_id/beauty-profile` | getProfile |
| POST | `/api/customers/:customer_id/beauty-profile` | getProfile |
| PUT | `/api/customers/:customer_id/beauty-profile` | getProfile |
| PATCH | `/api/customers/:customer_id/beauty-profile` | getProfile |
| GET | `/api/product-media` | getAllProductMedia |
| POST | `/api/product-media` | getAllProductMedia |
| GET | `/api/product-media/product/:productId` | getMediaByProductId |
| GET | `/api/product-media/:id` | getProductMediaById |
| PUT | `/api/product-media/:id` | getProductMediaById |
| DELETE | `/api/product-media/:id` | getProductMediaById |
| GET | `/api/product-attributes` | getAllProductAttributes |
| POST | `/api/product-attributes` | getAllProductAttributes |
| GET | `/api/product-attributes/product/:productId` | getAttributesByProductId |
| GET | `/api/product-attributes/:id` | getProductAttributeById |
| PUT | `/api/product-attributes/:id` | getProductAttributeById |
| DELETE | `/api/product-attributes/:id` | getProductAttributeById |
| GET | `/api/product-options` | getAllProductOptions |
| POST | `/api/product-options` | getAllProductOptions |
| GET | `/api/product-options/product/:productId` | getOptionsByProductId |
| GET | `/api/product-options/:id` | getProductOptionById |
| PUT | `/api/product-options/:id` | getProductOptionById |
| DELETE | `/api/product-options/:id` | getProductOptionById |
| GET | `/api/product-option-values` | getAllProductOptionValues |
| POST | `/api/product-option-values` | getAllProductOptionValues |
| GET | `/api/product-option-values/option/:productOptionId` | getValuesByOptionId |
| GET | `/api/product-option-values/:id` | getProductOptionValueById |
| PUT | `/api/product-option-values/:id` | getProductOptionValueById |
| DELETE | `/api/product-option-values/:id` | getProductOptionValueById |
| GET | `/api/product-variants` | getAllProductVariants |
| POST | `/api/product-variants` | getAllProductVariants |
| GET | `/api/product-variants/product/:productId` | getVariantsByProductId |
| GET | `/api/product-variants/:id` | getProductVariantById |
| PUT | `/api/product-variants/:id` | getProductVariantById |
| DELETE | `/api/product-variants/:id` | getProductVariantById |
| GET | `/api/variant-option-values` | getAllVariantOptionValues |
| POST | `/api/variant-option-values` | getAllVariantOptionValues |
| GET | `/api/variant-option-values/variant/:variantId` | getValuesByVariantId |
| GET | `/api/variant-option-values/:id` | getVariantOptionValueById |
| PUT | `/api/variant-option-values/:id` | getVariantOptionValueById |
| DELETE | `/api/variant-option-values/:id` | getVariantOptionValueById |
| GET | `/api/variant-media` | getAllVariantMedia |
| POST | `/api/variant-media` | getAllVariantMedia |
| GET | `/api/variant-media/variant/:variantId` | getMediaByVariantId |
| GET | `/api/variant-media/:id` | getVariantMediaById |
| PUT | `/api/variant-media/:id` | getVariantMediaById |
| DELETE | `/api/variant-media/:id` | getVariantMediaById |
| GET | `/api/price-books` | getAllPriceBooks |
| POST | `/api/price-books` | getAllPriceBooks |
| GET | `/api/price-books/:id` | getPriceBookById |
| PUT | `/api/price-books/:id` | getPriceBookById |
| DELETE | `/api/price-books/:id` | getPriceBookById |
| GET | `/api/price-book-entries` | getAllPriceBookEntries |
| POST | `/api/price-book-entries` | getAllPriceBookEntries |
| GET | `/api/price-book-entries/price-book/:priceBookId` | getEntriesByPriceBookId |
| GET | `/api/price-book-entries/variant/:variantId` | getEntriesByVariantId |
| GET | `/api/price-book-entries/:id` | getPriceBookEntryById |
| PUT | `/api/price-book-entries/:id` | getPriceBookEntryById |
| DELETE | `/api/price-book-entries/:id` | getPriceBookEntryById |
| GET | `/api/price-history` | getAllPriceHistory |
| POST | `/api/price-history` | getAllPriceHistory |
| GET | `/api/price-history/variant/:variantId` | getHistoryByVariantId |
| GET | `/api/price-history/:id` | getPriceHistoryById |
| DELETE | `/api/price-history/:id` | getPriceHistoryById |
| GET | `/api/promotions` | getAllPromotions |
| POST | `/api/promotions` | getAllPromotions |
| GET | `/api/promotions/:id` | getPromotionById |
| PUT | `/api/promotions/:id` | getPromotionById |
| PATCH | `/api/promotions/:id` | getPromotionById |
| DELETE | `/api/promotions/:id` | getPromotionById |
| GET | `/api/promotion-rules` | getAllPromotionRules |
| POST | `/api/promotion-rules` | getAllPromotionRules |
| GET | `/api/promotion-rules/promotion/:promotionId` | getRulesByPromotionId |
| GET | `/api/promotion-rules/:id` | getPromotionRuleById |
| PUT | `/api/promotion-rules/:id` | getPromotionRuleById |
| DELETE | `/api/promotion-rules/:id` | getPromotionRuleById |
| GET | `/api/promotion-targets` | getAllPromotionTargets |
| POST | `/api/promotion-targets` | getAllPromotionTargets |
| GET | `/api/promotion-targets/promotion/:promotionId` | getTargetsByPromotionId |
| GET | `/api/promotion-targets/:id` | getPromotionTargetById |
| PUT | `/api/promotion-targets/:id` | getPromotionTargetById |
| DELETE | `/api/promotion-targets/:id` | getPromotionTargetById |
| GET | `/api/coupons/me` | authMiddleware, getMyCoupons |
| GET | `/api/coupons/available` | getAvailableCoupons |
| POST | `/api/coupons/save/:couponId` | authMiddleware, saveCouponForMe |
| POST | `/api/coupons/apply` | applyCoupon |
| GET | `/api/coupons/:id/usage` | getCouponUsage |
| POST | `/api/coupons/:id/assign` | assignCouponToUsers |
| GET | `/api/coupons` | getAllCoupons |
| POST | `/api/coupons` | getAllCoupons |
| GET | `/api/coupons/code/:couponCode` | getCouponByCode |
| GET | `/api/coupons/:id` | getCouponById |
| PUT | `/api/coupons/:id` | getCouponById |
| PATCH | `/api/coupons/:id` | getCouponById |
| DELETE | `/api/coupons/:id` | getCouponById |
| GET | `/api/coupon-redemptions` | getAllCouponRedemptions |
| POST | `/api/coupon-redemptions` | getAllCouponRedemptions |
| GET | `/api/coupon-redemptions/coupon/:couponId` | getRedemptionsByCouponId |
| GET | `/api/coupon-redemptions/customer/:customer_id` | getRedemptionsByCustomerId |
| GET | `/api/coupon-redemptions/:id` | getCouponRedemptionById |
| PUT | `/api/coupon-redemptions/:id` | getCouponRedemptionById |
| DELETE | `/api/coupon-redemptions/:id` | getCouponRedemptionById |
| GET | `/api/warehouses` | getAllWarehouses |
| POST | `/api/warehouses` | getAllWarehouses |
| GET | `/api/warehouses/:id` | getWarehouseById |
| PUT | `/api/warehouses/:id` | getWarehouseById |
| DELETE | `/api/warehouses/:id` | getWarehouseById |
| GET | `/api/inventory-balances` | getAllInventoryBalances |
| POST | `/api/inventory-balances` | getAllInventoryBalances |
| GET | `/api/inventory-balances/warehouse/:warehouseId` | getBalancesByWarehouseId |
| GET | `/api/inventory-balances/product/:productId` | getBalancesByProductId |
| GET | `/api/inventory-balances/variant/:variantId` | getBalancesByVariantId |
| GET | `/api/inventory-balances/:id` | getInventoryBalanceById |
| PUT | `/api/inventory-balances/:id` | getInventoryBalanceById |
| DELETE | `/api/inventory-balances/:id` | getInventoryBalanceById |
| GET | `/api/inventory-transactions` | getAllInventoryTransactions |
| POST | `/api/inventory-transactions` | getAllInventoryTransactions |
| GET | `/api/inventory-transactions/warehouse/:warehouseId` | getTransactionsByWarehouseId |
| GET | `/api/inventory-transactions/variant/:variantId` | getTransactionsByVariantId |
| GET | `/api/inventory-transactions/:id` | getInventoryTransactionById |
| DELETE | `/api/inventory-transactions/:id` | getInventoryTransactionById |
| GET | `/api/stock-reservations` | getAllStockReservations |
| POST | `/api/stock-reservations` | getAllStockReservations |
| GET | `/api/stock-reservations/warehouse/:warehouseId` | getReservationsByWarehouseId |
| GET | `/api/stock-reservations/variant/:variantId` | getReservationsByVariantId |
| GET | `/api/stock-reservations/:id` | getStockReservationById |
| PUT | `/api/stock-reservations/:id` | getStockReservationById |
| DELETE | `/api/stock-reservations/:id` | getStockReservationById |
| GET | `/api/carts/me` | authMiddleware, getMyCart |
| POST | `/api/carts/me/merge-guest` | authMiddleware, mergeGuestCartOnLogin |
| GET | `/api/carts/me/checkout-prepare` | authMiddleware, prepareMyCartCheckout |
| POST | `/api/carts/me/items` | authMiddleware, addItemToMyCart |
| PATCH | `/api/carts/me/items/:itemId/quantity` | authMiddleware, updateMyCartItemQuantity |
| PATCH | `/api/carts/me/items/:itemId/selection` | authMiddleware, toggleMyCartItemSelection |
| PATCH | `/api/carts/me/selection` | authMiddleware, toggleMyCartSelectionAll |
| DELETE | `/api/carts/me/items/:itemId` | authMiddleware, removeItemFromMyCart |
| DELETE | `/api/carts/me/items-selected` | authMiddleware, removeSelectedFromMyCart |
| GET | `/api/carts/guest/me` | getGuestCart |
| GET | `/api/carts/guest/checkout-prepare` | prepareGuestCartCheckout |
| POST | `/api/carts/guest/items` | addItemToGuestCart |
| PATCH | `/api/carts/guest/items/:itemId/quantity` | updateGuestCartItemQuantity |
| PATCH | `/api/carts/guest/items/:itemId/selection` | toggleGuestCartItemSelection |
| PATCH | `/api/carts/guest/selection` | toggleGuestCartSelectionAll |
| DELETE | `/api/carts/guest/items/:itemId` | removeItemFromGuestCart |
| DELETE | `/api/carts/guest/items-selected` | removeSelectedFromGuestCart |
| GET | `/api/carts` | getAllCarts |
| POST | `/api/carts` | getAllCarts |
| GET | `/api/carts/customer/:customer_id` | getCartsByCustomerId |
| GET | `/api/carts/:id` | getCartById |
| PUT | `/api/carts/:id` | getCartById |
| DELETE | `/api/carts/:id` | getCartById |
| GET | `/api/cart-items` | getAllCartItems |
| POST | `/api/cart-items` | getAllCartItems |
| GET | `/api/cart-items/cart/:cart_id` | getItemsByCartId |
| GET | `/api/cart-items/:id` | getCartItemById |
| PUT | `/api/cart-items/:id` | getCartItemById |
| DELETE | `/api/cart-items/:id` | getCartItemById |
| POST | `/api/checkout-sessions/guest/prepare` | prepareGuestCheckoutSession |
| POST | `/api/checkout-sessions/guest/buy-now` | createGuestBuyNowCheckoutSession |
| POST | `/api/checkout-sessions/guest/me` | createGuestCheckoutSession |
| GET | `/api/checkout-sessions/guest/me/:id` | getGuestCheckoutSessionById |
| PATCH | `/api/checkout-sessions/guest/:id` | updateGuestCheckoutSession |
| POST | `/api/checkout-sessions/guest/:id/place-order` | placeGuestCheckoutSessionOrder |
| POST | `/api/checkout-sessions/me` | authMiddleware, createMyCheckoutSession |
| POST | `/api/checkout-sessions/me/buy-now` | authMiddleware, createMyBuyNowCheckoutSession |
| GET | `/api/checkout-sessions/me/:id` | authMiddleware, getMyCheckoutSessionById |
| PATCH | `/api/checkout-sessions/:id` | authMiddleware, updateMyCheckoutSession |
| GET | `/api/checkout-sessions/:id` | authMiddleware, updateMyCheckoutSession |
| PUT | `/api/checkout-sessions/:id` | authMiddleware, updateMyCheckoutSession |
| DELETE | `/api/checkout-sessions/:id` | authMiddleware, updateMyCheckoutSession |
| POST | `/api/checkout-sessions/:id/place-order` | authMiddleware, placeMyCheckoutSessionOrder |
| GET | `/api/checkout-sessions` | getAllCheckoutSessions |
| POST | `/api/checkout-sessions` | getAllCheckoutSessions |
| GET | `/api/checkout-sessions/cart/:cart_id` | getSessionsByCartId |
| GET | `/api/checkout-addresses` | getAllCheckoutAddresses |
| POST | `/api/checkout-addresses` | getAllCheckoutAddresses |
| GET | `/api/checkout-addresses/session/:checkout_session_id` | getAddressesBySessionId |
| GET | `/api/checkout-addresses/:id` | getCheckoutAddressById |
| PUT | `/api/checkout-addresses/:id` | getCheckoutAddressById |
| DELETE | `/api/checkout-addresses/:id` | getCheckoutAddressById |
| GET | `/api/shipping-methods` | getAllShippingMethods |
| POST | `/api/shipping-methods` | getAllShippingMethods |
| GET | `/api/shipping-methods/:id` | getShippingMethodById |
| PUT | `/api/shipping-methods/:id` | getShippingMethodById |
| DELETE | `/api/shipping-methods/:id` | getShippingMethodById |
| GET | `/api/checkout-shipping-methods` | getAllCheckoutShippingMethods |
| POST | `/api/checkout-shipping-methods` | getAllCheckoutShippingMethods |
| GET | `/api/checkout-shipping-methods/session/:checkout_session_id` | getMethodsBySessionId |
| GET | `/api/checkout-shipping-methods/:id` | getCheckoutShippingMethodById |
| PUT | `/api/checkout-shipping-methods/:id` | getCheckoutShippingMethodById |
| DELETE | `/api/checkout-shipping-methods/:id` | getCheckoutShippingMethodById |
| GET | `/api/payment-methods` | getAllPaymentMethods |
| POST | `/api/payment-methods` | getAllPaymentMethods |
| GET | `/api/payment-methods/:id` | getPaymentMethodById |
| PUT | `/api/payment-methods/:id` | getPaymentMethodById |
| DELETE | `/api/payment-methods/:id` | getPaymentMethodById |
| GET | `/api/orders/me` | authMiddleware, getMyOrders |
| GET | `/api/orders/me/summary` | authMiddleware, getMyOrderSummary |
| GET | `/api/orders/me/:id/tracking` | authMiddleware, getMyOrderTracking |
| GET | `/api/orders/me/:id` | authMiddleware, getMyOrderById |
| POST | `/api/orders/:id/reorder` | authMiddleware, reorderMyOrder |
| PATCH | `/api/orders/:id/cancel` | authMiddleware, cancelMyOrder |
| POST | `/api/orders/:id/return` | authMiddleware, requestReturnMyOrder |
| POST | `/api/orders/guest/lookup` | lookupGuestOrder |
| GET | `/api/orders/guest/:id/tracking` | getGuestOrderTracking |
| GET | `/api/orders/guest/:id/summary` | getGuestOrderSummary |
| GET | `/api/orders` | getAllOrders |
| POST | `/api/orders` | getAllOrders |
| GET | `/api/orders/customer/:customer_id` | getOrdersByCustomerId |
| GET | `/api/orders/:id` | getOrderById |
| PUT | `/api/orders/:id` | getOrderById |
| PATCH | `/api/orders/:id` | getOrderById |
| DELETE | `/api/orders/:id` | getOrderById |
| GET | `/api/order-items` | getAllOrderItems |
| POST | `/api/order-items` | getAllOrderItems |
| GET | `/api/order-items/order/:order_id` | getItemsByOrderId |
| GET | `/api/order-items/:id` | getOrderItemById |
| PUT | `/api/order-items/:id` | getOrderItemById |
| DELETE | `/api/order-items/:id` | getOrderItemById |
| GET | `/api/order-addresses` | getAllOrderAddresses |
| POST | `/api/order-addresses` | getAllOrderAddresses |
| GET | `/api/order-addresses/order/:order_id` | getAddressesByOrderId |
| GET | `/api/order-addresses/:id` | getOrderAddressById |
| PUT | `/api/order-addresses/:id` | getOrderAddressById |
| DELETE | `/api/order-addresses/:id` | getOrderAddressById |
| GET | `/api/order-totals` | getAllOrderTotals |
| POST | `/api/order-totals` | getAllOrderTotals |
| GET | `/api/order-totals/order/:order_id` | getTotalsByOrderId |
| GET | `/api/order-totals/:id` | getOrderTotalById |
| PUT | `/api/order-totals/:id` | getOrderTotalById |
| DELETE | `/api/order-totals/:id` | getOrderTotalById |
| GET | `/api/order-status-history` | getAllOrderStatusHistory |
| POST | `/api/order-status-history` | getAllOrderStatusHistory |
| GET | `/api/order-status-history/order/:order_id` | getHistoryByOrderId |
| GET | `/api/order-status-history/:id` | getOrderStatusHistoryById |
| DELETE | `/api/order-status-history/:id` | getOrderStatusHistoryById |
| GET | `/api/payment-intents` | getAllPaymentIntents |
| POST | `/api/payment-intents` | getAllPaymentIntents |
| GET | `/api/payment-intents/order/:order_id` | getIntentsByOrderId |
| GET | `/api/payment-intents/:id` | getPaymentIntentById |
| PUT | `/api/payment-intents/:id` | getPaymentIntentById |
| DELETE | `/api/payment-intents/:id` | getPaymentIntentById |
| GET | `/api/payment-transactions` | getAllPaymentTransactions |
| POST | `/api/payment-transactions` | getAllPaymentTransactions |
| GET | `/api/payment-transactions/order/:order_id` | getTransactionsByOrderId |
| GET | `/api/payment-transactions/:id` | getPaymentTransactionById |
| PUT | `/api/payment-transactions/:id` | getPaymentTransactionById |
| DELETE | `/api/payment-transactions/:id` | getPaymentTransactionById |
| GET | `/api/refunds` | getAllRefunds |
| POST | `/api/refunds` | getAllRefunds |
| GET | `/api/refunds/order/:order_id` | getRefundsByOrderId |
| GET | `/api/refunds/:id` | getRefundById |
| PUT | `/api/refunds/:id` | getRefundById |
| DELETE | `/api/refunds/:id` | getRefundById |
| GET | `/api/shipments` | getAllShipments |
| POST | `/api/shipments` | getAllShipments |
| GET | `/api/shipments/order/:order_id` | getShipmentsByOrderId |
| GET | `/api/shipments/:id` | getShipmentById |
| PUT | `/api/shipments/:id` | getShipmentById |
| PATCH | `/api/shipments/:id` | getShipmentById |
| DELETE | `/api/shipments/:id` | getShipmentById |
| GET | `/api/shipment-items` | getAllShipmentItems |
| POST | `/api/shipment-items` | getAllShipmentItems |
| GET | `/api/shipment-items/shipment/:shipmentId` | getItemsByShipmentId |
| GET | `/api/shipment-items/:id` | getShipmentItemById |
| PUT | `/api/shipment-items/:id` | getShipmentItemById |
| DELETE | `/api/shipment-items/:id` | getShipmentItemById |
| GET | `/api/shipment-events` | getAllShipmentEvents |
| POST | `/api/shipment-events` | getAllShipmentEvents |
| GET | `/api/shipment-events/shipment/:shipmentId` | getEventsByShipmentId |
| GET | `/api/shipment-events/:id` | getShipmentEventById |
| DELETE | `/api/shipment-events/:id` | getShipmentEventById |
| GET | `/api/returns` | getAllReturns |
| POST | `/api/returns` | getAllReturns |
| GET | `/api/returns/order/:order_id` | getReturnsByOrderId |
| GET | `/api/returns/:id` | getReturnById |
| PUT | `/api/returns/:id` | getReturnById |
| DELETE | `/api/returns/:id` | getReturnById |
| GET | `/api/return-items` | getAllReturnItems |
| POST | `/api/return-items` | getAllReturnItems |
| GET | `/api/return-items/return/:returnId` | getItemsByReturnId |
| GET | `/api/return-items/:id` | getReturnItemById |
| PUT | `/api/return-items/:id` | getReturnItemById |
| DELETE | `/api/return-items/:id` | getReturnItemById |
| GET | `/api/wishlists/me` | authMiddleware, getMyWishlist |
| GET | `/api/wishlists/me/items` | authMiddleware, getMyWishlistItems |
| DELETE | `/api/wishlists/me/items/:id` | authMiddleware, deleteMyWishlistItem |
| POST | `/api/wishlists` | authMiddleware, addMyWishlistProduct |
| GET | `/api/wishlists` | authMiddleware, addMyWishlistProduct |
| DELETE | `/api/wishlists/:productId` | authMiddleware, deleteMyWishlistProductByProductId |
| GET | `/api/wishlists/customer/:customer_id` | getWishlistsByCustomerId |
| GET | `/api/wishlists/:id` | getWishlistById |
| PUT | `/api/wishlists/:id` | getWishlistById |
| DELETE | `/api/wishlists/admin/:id` | deleteWishlist |
| POST | `/api/wishlists/admin/create` | createWishlist |
| GET | `/api/wishlist/me` | authMiddleware, getMyWishlist |
| GET | `/api/wishlist/me/items` | authMiddleware, getMyWishlistItems |
| DELETE | `/api/wishlist/me/items/:id` | authMiddleware, deleteMyWishlistItem |
| POST | `/api/wishlist` | authMiddleware, addMyWishlistProduct |
| GET | `/api/wishlist` | authMiddleware, addMyWishlistProduct |
| DELETE | `/api/wishlist/:productId` | authMiddleware, deleteMyWishlistProductByProductId |
| GET | `/api/wishlist/customer/:customer_id` | getWishlistsByCustomerId |
| GET | `/api/wishlist/:id` | getWishlistById |
| PUT | `/api/wishlist/:id` | getWishlistById |
| DELETE | `/api/wishlist/admin/:id` | deleteWishlist |
| POST | `/api/wishlist/admin/create` | createWishlist |
| GET | `/api/wishlist-items` | getAllWishlistItems |
| POST | `/api/wishlist-items` | getAllWishlistItems |
| GET | `/api/wishlist-items/wishlist/:wishlistId` | getItemsByWishlistId |
| GET | `/api/wishlist-items/:id` | getWishlistItemById |
| DELETE | `/api/wishlist-items/:id` | getWishlistItemById |
| GET | `/api/reviews` | getAllReviews |
| POST | `/api/reviews` | getAllReviews |
| GET | `/api/reviews/product/:productId` | getReviewsByProductId |
| PUT | `/api/reviews/:id` | updateReview |
| DELETE | `/api/reviews/:id` | updateReview |
| GET | `/api/reviews/:id` | updateReview |
| POST | `/api/reviews/:reviewId/vote` | authMiddleware, voteOnReview |
| GET | `/api/reviews/me` | authMiddleware, getMyReviews |
| GET | `/api/reviews/write-eligibility/:orderItemId` | authMiddleware, getReviewWriteEligibility |
| GET | `/api/reviews/reviewable-items/:productId` | authMiddleware, getReviewableItems |
| POST | `/api/reviews/submit` | authMiddleware, submitReviewFromOrderItem |
| POST | `/api/reviews/submit-direct` | authMiddleware, submitReviewDirect |
| PATCH | `/api/reviews/me/:id` | authMiddleware, patchMyReview |
| DELETE | `/api/reviews/me/:id` | authMiddleware, patchMyReview |
| GET | `/api/review-media` | getAllReviewMedia |
| POST | `/api/review-media` | getAllReviewMedia |
| GET | `/api/review-media/product/:productId` | getMediaByProductId |
| GET | `/api/review-media/review/:reviewId` | getMediaByReviewId |
| GET | `/api/review-media/:id` | getReviewMediaById |
| DELETE | `/api/review-media/:id` | getReviewMediaById |
| GET | `/api/review-votes` | getAllReviewVotes |
| POST | `/api/review-votes` | getAllReviewVotes |
| GET | `/api/review-votes/review/:reviewId` | getVotesByReviewId |
| GET | `/api/review-votes/:id` | getReviewVoteById |
| DELETE | `/api/review-votes/:id` | getReviewVoteById |
| GET | `/api/review-summary` | getAllReviewSummaries |
| POST | `/api/review-summary` | getAllReviewSummaries |
| GET | `/api/review-summary/product/:productId` | getSummaryByProductId |
| GET | `/api/review-summary/:id` | getReviewSummaryById |
| DELETE | `/api/review-summary/:id` | getReviewSummaryById |
| GET | `/api/loyalty-tiers` | getAllLoyaltyTiers |
| POST | `/api/loyalty-tiers` | getAllLoyaltyTiers |
| GET | `/api/loyalty-tiers/:id` | getLoyaltyTierById |
| PUT | `/api/loyalty-tiers/:id` | getLoyaltyTierById |
| DELETE | `/api/loyalty-tiers/:id` | getLoyaltyTierById |
| GET | `/api/loyalty-accounts/me` | authMiddleware, getMyLoyaltyAccount |
| GET | `/api/loyalty-accounts` | getAllLoyaltyAccounts |
| POST | `/api/loyalty-accounts` | getAllLoyaltyAccounts |
| GET | `/api/loyalty-accounts/customer/:customer_id` | getAccountByCustomerId |
| GET | `/api/loyalty-accounts/:id` | getLoyaltyAccountById |
| PUT | `/api/loyalty-accounts/:id` | getLoyaltyAccountById |
| DELETE | `/api/loyalty-accounts/:id` | getLoyaltyAccountById |
| GET | `/api/loyalty/me` | authMiddleware, getMyLoyaltyAccount |
| GET | `/api/loyalty` | getAllLoyaltyAccounts |
| POST | `/api/loyalty` | getAllLoyaltyAccounts |
| GET | `/api/loyalty/customer/:customer_id` | getAccountByCustomerId |
| GET | `/api/loyalty/:id` | getLoyaltyAccountById |
| PUT | `/api/loyalty/:id` | getLoyaltyAccountById |
| DELETE | `/api/loyalty/:id` | getLoyaltyAccountById |
| GET | `/api/loyalty-point-ledger` | getAllLoyaltyPointLedger |
| POST | `/api/loyalty-point-ledger` | getAllLoyaltyPointLedger |
| GET | `/api/loyalty-point-ledger/customer/:customer_id` | getLedgerByCustomerId |
| GET | `/api/loyalty-point-ledger/account/:loyaltyAccountId` | getLedgerByAccountId |
| GET | `/api/loyalty-point-ledger/:id` | getLoyaltyPointLedgerById |
| DELETE | `/api/loyalty-point-ledger/:id` | getLoyaltyPointLedgerById |
| GET | `/api/admin/dashboard-summary` | authMiddleware, getDashboardSummary |
| GET | `/api/admin/recommendations/analytics/overview` | authMiddleware, getOverview |
| GET | `/api/admin/recommendations/analytics/top-reasons` | authMiddleware, getTopReasons |
| GET | `/api/admin/recommendations/analytics/top-products` | authMiddleware, getTopProducts |
| GET | `/api/admin/recommendations/analytics/top-brands` | authMiddleware, getTopBrands |
| GET | `/api/admin/recommendations/analytics/by-context` | authMiddleware, getByContext |
| GET | `/api/admin/recommendations/analytics/timeline` | authMiddleware, getTimeline |
| PATCH | `/api/admin/reviews/:id/hide` | authMiddleware, hideReview |
| PATCH | `/api/admin/reviews/:id/unhide` | authMiddleware, unhideReview |
| PATCH | `/api/admin/orders/:id/confirm` | authMiddleware, confirmOrder |
| PATCH | `/api/admin/orders/:id/processing` | authMiddleware, markProcessing |
| PATCH | `/api/admin/orders/:id/cancel` | authMiddleware, cancelOrder |
| PATCH | `/api/admin/orders/:id/mark-cod-paid` | authMiddleware, markCodPaid |
| POST | `/api/admin/orders/:id/shipments` | authMiddleware, createShipment |
| PATCH | `/api/admin/shipments/:id/ready-to-ship` | authMiddleware, shipmentReadyToShip |
| PATCH | `/api/admin/shipments/:id/ship` | authMiddleware, shipShipment |
| PATCH | `/api/admin/shipments/:id/in-transit` | authMiddleware, shipmentInTransit |
| PATCH | `/api/admin/shipments/:id/deliver` | authMiddleware, deliverShipment |
| PATCH | `/api/admin/shipments/:id/fail` | authMiddleware, failShipment |
| POST | `/api/admin/shipments/:id/events` | authMiddleware, addShipmentEvent |
| POST | `/api/admin/orders/:id/returns` | authMiddleware, createReturn |
| PATCH | `/api/admin/returns/:id/approve` | authMiddleware, approveReturn |
| PATCH | `/api/admin/returns/:id/reject` | authMiddleware, rejectReturn |
| PATCH | `/api/admin/returns/:id/receive` | authMiddleware, receiveReturn |
| PATCH | `/api/admin/returns/:id/complete` | authMiddleware, completeReturn |
| POST | `/api/admin/orders/:id/refunds` | authMiddleware, createRefund |
| PATCH | `/api/admin/refunds/:id/approve` | authMiddleware, approveRefund |
| PATCH | `/api/admin/refunds/:id/complete` | authMiddleware, completeRefund |
| GET | `/api/setup/admin` | anonymous |
| POST | `/api/guest-sessions/bootstrap` | bootstrapGuestSession |
| GET | `/api/recommendations/me` | authMiddleware, getMyRecommendations |
| POST | `/api/recommendations/preview` | previewRecommendations |
| GET | `/api/recommendations/me/homepage` | authMiddleware, getMyHomepageRecommendations |
| GET | `/api/recommendations/me/all` | authMiddleware, getMyAllRecommendations |
| GET | `/api/migrate-fields/seed-admin` | anonymous |
| GET | `/api/migrate-fields` | anonymous |
