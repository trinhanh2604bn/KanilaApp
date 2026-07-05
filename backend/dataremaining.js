/**
 * dataremaining.js
 * Kanila Beauty Commerce - seed data for the remaining non-product business tables.
 *
 * Run after dataproduct.js so products/product_variants already exist.
 *
 * What this file does:
 * - Seeds realistic mapped data for Auth, Customer, Cart, Checkout, Order, Payment,
 *   Inventory, Promotion, Loyalty, Shipment, Return/Refund, Wishlist, Review,
 *   Recommendation and Audit collections.
 * - Uses deterministic ObjectId values and a seed_batch marker, so it is safe to run again.
 * - Does not reseed products/brands/categories. It reads existing products and variants and maps all dependent data to them.
 * - Skips password_reset_otps because the project audit marks it as deprecated.
 * - Skips email_otps by default because OTP rows are runtime-only. Add --include-runtime-otp only if you want consumed test OTP rows.
 *
 * Usage from backend root:
 *   npm install dotenv mongoose
 *   node dataremaining.js
 *
 * Optional:
 *   node dataremaining.js --reset
 *     Deletes only records created by this seed batch, then upserts again.
 *
 *   node dataremaining.js --include-runtime-otp
 *     Also seeds 50 consumed OTP rows in email_otps for database visual completeness.
 *
 * Requirements:
 *   .env must contain one of: MONGO_URI, MONGODB_URI, MONGODB_URL, DATABASE_URL
 */

'use strict';

require('dotenv').config();
const crypto = require('crypto');
const mongoose = require('mongoose');

const { ObjectId } = mongoose.Types;
const SEED_BATCH = 'kanila_remaining_v1';
const TARGET = 50;
const SHOULD_RESET = process.argv.includes('--reset');
const INCLUDE_RUNTIME_OTP = process.argv.includes('--include-runtime-otp');

const IMAGE_BASE_URL = process.env.KANILA_IMAGE_BASE_URL || 'https://example.com/kanila';

const COLLECTION = {
  roles: 'roles',
  permissions: 'permissions',
  rolePermissions: 'role_permissions',
  accounts: 'accounts',
  accountAuthProviders: 'account_auth_providers',
  accountRoles: 'account_roles',
  adminProfiles: 'admin_profiles',
  auditLogs: 'audit_logs',
  beautyReferences: 'beauty_references',
  customerProfiles: 'customer_profiles',
  customerAddresses: 'customer_addresses',
  customerConsents: 'customer_consents',
  customerPreferences: 'customer_preferences',
  customerBeautyProfiles: 'customer_beauty_profiles',
  customerRecommendationSnapshots: 'customer_recommendation_snapshots',
  emailOtps: 'email_otps',
  guestSessions: 'guest_sessions',
  carts: 'carts',
  cartItems: 'cart_items',
  checkoutSessions: 'checkout_sessions',
  checkoutAddresses: 'checkout_addresses',
  checkoutShippingMethods: 'checkout_shipping_methods',
  coupons: 'coupons',
  couponRedemptions: 'coupon_redemptions',
  customerCoupons: 'customer_coupons',
  inventoryBalances: 'inventory_balances',
  inventoryTransactions: 'inventory_transactions',
  loyaltyAccounts: 'loyalty_accounts',
  loyaltyPointLedger: 'loyalty_point_ledger',
  loyaltyTiers: 'loyalty_tiers',
  orders: 'orders',
  orderAddresses: 'order_addresses',
  orderItems: 'order_items',
  orderStatusHistory: 'order_status_history',
  orderTotals: 'order_totals',
  paymentIntents: 'payment_intents',
  paymentMethods: 'payment_methods',
  paymentTransactions: 'payment_transactions',
  priceBooks: 'price_books',
  priceBookEntries: 'price_book_entries',
  priceHistories: 'price_histories',
  promotions: 'promotions',
  promotionRules: 'promotion_rules',
  promotionTargets: 'promotion_targets',
  recommendationLogs: 'recommendation_logs',
  refunds: 'refunds',
  returns: 'returns',
  returnItems: 'returnitems',
  reviews: 'reviews',
  reviewMedias: 'review_medias',
  reviewSummaries: 'review_summaries',
  reviewVotes: 'review_votes',
  shipments: 'shipments',
  shipmentEvents: 'shipment_events',
  shipmentItems: 'shipment_items',
  shippingMethods: 'shipping_methods',
  stockReservations: 'stock_reservations',
  warehouses: 'warehouses',
  wishlists: 'wishlists',
  wishlistItems: 'wishlist_items',
};

const SEEDED_COLLECTIONS = [
  COLLECTION.roles,
  COLLECTION.permissions,
  COLLECTION.rolePermissions,
  COLLECTION.accounts,
  COLLECTION.accountAuthProviders,
  COLLECTION.accountRoles,
  COLLECTION.adminProfiles,
  COLLECTION.auditLogs,
  COLLECTION.beautyReferences,
  COLLECTION.customerProfiles,
  COLLECTION.customerAddresses,
  COLLECTION.customerConsents,
  COLLECTION.customerPreferences,
  COLLECTION.customerBeautyProfiles,
  COLLECTION.customerRecommendationSnapshots,
  COLLECTION.guestSessions,
  COLLECTION.carts,
  COLLECTION.cartItems,
  COLLECTION.checkoutSessions,
  COLLECTION.checkoutAddresses,
  COLLECTION.checkoutShippingMethods,
  COLLECTION.coupons,
  COLLECTION.couponRedemptions,
  COLLECTION.customerCoupons,
  COLLECTION.inventoryBalances,
  COLLECTION.inventoryTransactions,
  COLLECTION.loyaltyAccounts,
  COLLECTION.loyaltyPointLedger,
  COLLECTION.loyaltyTiers,
  COLLECTION.orders,
  COLLECTION.orderAddresses,
  COLLECTION.orderItems,
  COLLECTION.orderStatusHistory,
  COLLECTION.orderTotals,
  COLLECTION.paymentIntents,
  COLLECTION.paymentMethods,
  COLLECTION.paymentTransactions,
  COLLECTION.priceBooks,
  COLLECTION.priceBookEntries,
  COLLECTION.priceHistories,
  COLLECTION.promotions,
  COLLECTION.promotionRules,
  COLLECTION.promotionTargets,
  COLLECTION.recommendationLogs,
  COLLECTION.refunds,
  COLLECTION.returns,
  COLLECTION.returnItems,
  COLLECTION.reviews,
  COLLECTION.reviewMedias,
  COLLECTION.reviewSummaries,
  COLLECTION.reviewVotes,
  COLLECTION.shipments,
  COLLECTION.shipmentEvents,
  COLLECTION.shipmentItems,
  COLLECTION.shippingMethods,
  COLLECTION.stockReservations,
  COLLECTION.warehouses,
  COLLECTION.wishlists,
  COLLECTION.wishlistItems,
];

if (INCLUDE_RUNTIME_OTP) SEEDED_COLLECTIONS.push(COLLECTION.emailOtps);

function oid(key) {
  return new ObjectId(crypto.createHash('md5').update(`${SEED_BATCH}:${key}`).digest('hex').slice(0, 24));
}

function pad(n, width = 2) {
  return String(n).padStart(width, '0');
}

function daysFromNow(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  return d;
}

function minutesFromNow(minutes) {
  return new Date(Date.now() + minutes * 60 * 1000);
}

function pick(arr, index) {
  return arr[index % arr.length];
}

function money(value) {
  return Math.round(value / 1000) * 1000;
}

function normalizeId(value) {
  if (!value) return null;
  if (value instanceof ObjectId) return value;
  return new ObjectId(String(value));
}

function productName(product) {
  return product.productName || product.product_name || product.name || `Kanila Makeup Product ${String(product._id).slice(-4)}`;
}

function productPrice(product) {
  return Number(product.price || product.salePriceAmount || product.sale_price_amount || product.final_price || 199000);
}

function variantName(variant) {
  return variant.variantName || variant.variant_name || variant.sku || `Variant ${String(variant._id).slice(-4)}`;
}

function variantSku(variant, index) {
  return variant.sku || variant.SKU || `KNL-VAR-${pad(index + 1, 4)}`;
}

function getVariantProductId(variant) {
  return normalizeId(variant.productId || variant.product_id || variant.product || null);
}

function cleanPhone(i) {
  return `09${String(10000000 + i * 7391).slice(-8)}`;
}

function seedMeta(extra = {}) {
  return {
    seed_batch: SEED_BATCH,
    seeded_at: new Date(),
    ...extra,
  };
}

function docWithId(key, body) {
  return { _id: oid(key), ...body, ...seedMeta() };
}

function roleId(i) { return oid(`role-${i}`); }
function permissionId(i) { return oid(`permission-${i}`); }
function customerAccountId(i) { return oid(`account-customer-${i}`); }
function staffAccountId(i) { return oid(`account-staff-${i}`); }
function customerId(i) { return oid(`customer-${i}`); }
function adminProfileId(i) { return oid(`admin-profile-${i}`); }
function addressId(i) { return oid(`customer-address-${i}`); }
function cartId(i) { return oid(`cart-${i}`); }
function cartItemId(i) { return oid(`cart-item-${i}`); }
function checkoutSessionId(i) { return oid(`checkout-session-${i}`); }
function checkoutAddressId(i) { return oid(`checkout-address-${i}`); }
function checkoutShippingId(i) { return oid(`checkout-shipping-${i}`); }
function orderId(i) { return oid(`order-${i}`); }
function orderItemId(i) { return oid(`order-item-${i}`); }
function paymentIntentId(i) { return oid(`payment-intent-${i}`); }
function paymentTransactionId(i) { return oid(`payment-transaction-${i}`); }
function promotionId(i) { return oid(`promotion-${i}`); }
function couponId(i) { return oid(`coupon-${i}`); }
function warehouseId(i) { return oid(`warehouse-${i}`); }
function shippingMethodId(i) { return oid(`shipping-method-${i}`); }
function shipmentId(i) { return oid(`shipment-${i}`); }
function returnId(i) { return oid(`return-${i}`); }
function refundId(i) { return oid(`refund-${i}`); }
function reviewId(i) { return oid(`review-${i}`); }
function wishlistId(i) { return oid(`wishlist-${i}`); }
function loyaltyTierId(i) { return oid(`loyalty-tier-${i}`); }
function loyaltyAccountId(i) { return oid(`loyalty-account-${i}`); }
function priceBookId(i) { return oid(`price-book-${i}`); }

const vietnameseNames = [
  'Nguyễn Minh Anh', 'Trần Bảo Ngọc', 'Lê Phương Linh', 'Phạm Khánh Vy', 'Hoàng Gia Hân',
  'Đặng Thùy Dương', 'Bùi Ngọc Mai', 'Vũ Quỳnh Chi', 'Đỗ Hà My', 'Ngô Thanh Trúc',
  'Phan Yến Nhi', 'Mai Nhật Hạ', 'Dương Ánh Tuyết', 'Tạ Hồng Nhung', 'Đinh Bảo Trân',
  'Lý Kim Ngân', 'Cao Minh Thư', 'Tô Hải Yến', 'Hồ Thiên An', 'Võ Ngọc Diệp',
  'Nguyễn Khánh Linh', 'Trần Mỹ Duyên', 'Lê Ngọc Hân', 'Phạm Tường Vy', 'Hoàng Minh Châu',
  'Đặng Gia Linh', 'Bùi An Nhiên', 'Vũ Hoàng Yến', 'Đỗ Phương Anh', 'Ngô Diệu Linh',
  'Phan Thảo Vy', 'Mai Bảo Anh', 'Dương Tú Anh', 'Tạ Ngọc Ánh', 'Đinh Hà Phương',
  'Lý Uyên Nhi', 'Cao Bảo Vy', 'Tô Minh Khuê', 'Hồ Ngọc Lam', 'Võ Thanh Tâm',
  'Nguyễn Diệu Hương', 'Trần Hoài An', 'Lê Cẩm Tú', 'Phạm Ngân Hà', 'Hoàng Bảo Thy',
  'Đặng Minh Tâm', 'Bùi Khánh An', 'Vũ Ngọc Bích', 'Đỗ Thiên Thanh', 'Ngô Trà My',
];

const cities = [
  ['TP. Hồ Chí Minh', 'Quận 1', 'Phường Bến Nghé'],
  ['Hà Nội', 'Quận Hoàn Kiếm', 'Phường Hàng Trống'],
  ['Đà Nẵng', 'Quận Hải Châu', 'Phường Hải Châu 1'],
  ['Cần Thơ', 'Quận Ninh Kiều', 'Phường Cái Khế'],
  ['Hải Phòng', 'Quận Lê Chân', 'Phường An Biên'],
  ['Bình Dương', 'TP. Thủ Dầu Một', 'Phường Phú Cường'],
  ['Đồng Nai', 'TP. Biên Hòa', 'Phường Tân Phong'],
  ['Khánh Hòa', 'TP. Nha Trang', 'Phường Lộc Thọ'],
  ['Lâm Đồng', 'TP. Đà Lạt', 'Phường 1'],
  ['Gia Lai', 'TP. Pleiku', 'Phường Tây Sơn'],
];

const skinTypes = ['da dầu', 'da khô', 'da hỗn hợp', 'da thường', 'da nhạy cảm'];
const skinConcerns = ['mụn nhẹ', 'lỗ chân lông', 'thâm mụn', 'xỉn màu', 'khô môi', 'dễ xuống tone', 'nền cakey'];
const undertones = ['cool', 'neutral', 'warm', 'olive'];
const makeupStyles = ['tự nhiên', 'glowy Hàn Quốc', 'matte lâu trôi', 'clean girl', 'dự tiệc', 'công sở'];
const paymentProviders = ['COD', 'MOMO', 'ZALOPAY', 'VNPAY', 'PAYOS', 'CARD'];
const carriers = ['GHN', 'GHTK', 'Viettel Post', 'J&T Express', 'Ninja Van', 'Ahamove', 'GrabExpress'];
const orderStatuses = ['pending', 'confirmed', 'processing', 'completed', 'cancelled', 'returned'];
const paymentStatuses = ['unpaid', 'authorized', 'paid', 'failed', 'refunded'];
const fulfillmentStatuses = ['unfulfilled', 'packed', 'shipped', 'delivered', 'returned'];

async function connectMongo() {
  const uri = process.env.MONGODB_URI || process.env.MONGO_URI || process.env.MONGODB_URL || process.env.DATABASE_URL;
  if (!uri) throw new Error('Missing MongoDB connection string. Set MONGO_URI or MONGODB_URI in .env');
  const options = process.env.MONGODB_DB_NAME ? { dbName: process.env.MONGODB_DB_NAME } : undefined;
  await mongoose.connect(uri, options);
  console.log(`Connected to MongoDB database: ${mongoose.connection.name}`);
}

async function getExistingCollections(db) {
  const names = await db.listCollections({}, { nameOnly: true }).toArray();
  return new Set(names.map((item) => item.name));
}

async function getProductContext(db) {
  const products = await db.collection('products').find({}).sort({ productName: 1 }).limit(500).toArray();
  const variants = await db.collection('product_variants').find({}).sort({ sku: 1 }).limit(500).toArray();
  const brands = await db.collection('brands').find({}).limit(200).toArray();
  const categories = await db.collection('categories').find({}).limit(200).toArray();

  if (products.length < TARGET) {
    throw new Error(`products currently has ${products.length} docs. Run dataproduct.js first and make sure products >= ${TARGET}.`);
  }
  if (variants.length < TARGET) {
    throw new Error(`product_variants currently has ${variants.length} docs. Run dataproduct.js first and make sure product_variants >= ${TARGET}.`);
  }

  const productById = new Map(products.map((product) => [String(product._id), product]));
  const mappedVariants = variants
    .map((variant) => ({ variant, product_id: getVariantProductId(variant) }))
    .filter((item) => item.product_id && productById.has(String(item.product_id)));

  if (mappedVariants.length < TARGET) {
    throw new Error(`Only ${mappedVariants.length} product_variants are mapped to existing products. Check productId/product_id mapping.`);
  }

  return { products, variants: mappedVariants.map((item) => item.variant), productById, brands, categories };
}

async function resetSeedData(db, collections) {
  if (!SHOULD_RESET) return;
  console.log('Reset mode: deleting records from this seed batch only...');
  for (const name of collections) {
    const result = await db.collection(name).deleteMany({ seed_batch: SEED_BATCH });
    console.log(`  ${name.padEnd(36)} deleted ${String(result.deletedCount).padStart(4)} rows`);
  }
}

async function upsertMany(db, collectionName, docs) {
  if (!docs.length) return;
  const ops = docs.map((doc) => ({
    updateOne: {
      filter: { _id: doc._id },
      update: { $set: doc },
      upsert: true,
    },
  }));
  const result = await db.collection(collectionName).bulkWrite(ops, { ordered: false });
  const changed = (result.upsertedCount || 0) + (result.modifiedCount || 0) + (result.matchedCount || 0);
  console.log(`${collectionName.padEnd(36)} => ${String(docs.length).padStart(4)} prepared | upserted ${String(result.upsertedCount || 0).padStart(4)} | touched ${String(changed).padStart(4)}`);
}

function buildRoles() {
  const names = [
    'Customer', 'Beauty Advisor', 'Content Moderator', 'Order Operator', 'Warehouse Staff',
    'Inventory Manager', 'Customer Support', 'Marketing Executive', 'Campaign Manager', 'Product Admin',
    'Category Manager', 'Brand Manager', 'Finance Staff', 'Payment Reviewer', 'Return Specialist',
    'Refund Approver', 'Loyalty Manager', 'Community Manager', 'AR Content Admin', 'Chatbot Trainer',
    'Data Analyst', 'QA Tester', 'Product Owner', 'System Admin', 'Security Auditor',
  ];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`role-${i}`, {
    role_code: `KNL_ROLE_${pad(i + 1, 3)}`,
    role_name: names[i] || `Kanila Operations Role ${pad(i + 1, 2)}`,
    description: `Vai trò ${i + 1} dùng cho vận hành app Kanila makeup commerce: phân quyền đúng theo nghiệp vụ.`,
    is_system_role: i < 6,
    is_active: true,
    created_at: daysFromNow(-120 + i),
    updated_at: new Date(),
  }));
}

function buildPermissions() {
  const groups = ['auth', 'catalog', 'cart', 'checkout', 'order', 'payment', 'inventory', 'promotion', 'review', 'support'];
  const actions = ['read', 'create', 'update', 'delete', 'approve'];
  return Array.from({ length: TARGET }, (_, i) => {
    const group = pick(groups, i);
    const action = pick(actions, i);
    return docWithId(`permission-${i}`, {
      permission_code: `KNL_${group.toUpperCase()}_${action.toUpperCase()}_${pad(Math.floor(i / groups.length) + 1, 2)}`,
      permission_name: `${action} ${group}`,
      permission_group: group,
      description: `Cho phép ${action} dữ liệu thuộc module ${group} trong Kanila backend.`,
      is_active: true,
      created_at: daysFromNow(-130 + i),
    });
  });
}

function buildRolePermissions() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`role-permission-${i}`, {
    role_id: roleId(i),
    permission_id: permissionId(i),
    granted_at: daysFromNow(-100 + i),
  }));
}

function buildAccounts() {
  const customers = Array.from({ length: TARGET }, (_, i) => docWithId(`account-customer-${i}`, {
    account_type: 'customer',
    email: `kanila.customer${pad(i + 1, 3)}@seed.kanila.test`,
    phone: cleanPhone(i),
    username: `kanila_customer_${pad(i + 1, 3)}`,
    password_hash: 'seed_hash_not_for_real_login',
    account_status: 'active',
    email_verified_at: daysFromNow(-80 + i),
    phone_verified_at: daysFromNow(-79 + i),
    last_login_at: daysFromNow(-i % 20),
    failed_login_count: 0,
    locked_until: null,
    created_at: daysFromNow(-120 + i),
    updated_at: new Date(),
  }));

  const staff = Array.from({ length: TARGET }, (_, i) => docWithId(`account-staff-${i}`, {
    account_type: i % 5 === 0 ? 'admin' : 'staff',
    email: `kanila.staff${pad(i + 1, 3)}@seed.kanila.test`,
    phone: `08${String(20000000 + i * 5713).slice(-8)}`,
    username: `kanila_staff_${pad(i + 1, 3)}`,
    password_hash: 'seed_hash_not_for_real_login',
    account_status: 'active',
    email_verified_at: daysFromNow(-90 + i),
    phone_verified_at: daysFromNow(-88 + i),
    last_login_at: daysFromNow(-(i % 15)),
    failed_login_count: 0,
    locked_until: null,
    created_at: daysFromNow(-140 + i),
    updated_at: new Date(),
  }));

  return customers.concat(staff);
}

function buildAccountAuthProviders() {
  const providers = ['email_otp', 'google', 'apple', 'facebook'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`account-auth-provider-${i}`, {
    account_id: customerAccountId(i),
    provider_code: pick(providers, i),
    provider_subject: `seed-provider-${pick(providers, i)}-${pad(i + 1, 4)}`,
    provider_email: `kanila.customer${pad(i + 1, 3)}@seed.kanila.test`,
    linked_at: daysFromNow(-100 + i),
    last_used_at: daysFromNow(-(i % 14)),
    created_at: daysFromNow(-100 + i),
  }));
}

function buildAccountRoles() {
  return Array.from({ length: TARGET * 2 }, (_, i) => docWithId(`account-role-${i}`, {
    account_id: i < TARGET ? customerAccountId(i) : staffAccountId(i - TARGET),
    role_id: i < TARGET ? roleId(0) : roleId((i - TARGET) % TARGET),
    assigned_by_account_id: staffAccountId(0),
    assigned_at: daysFromNow(-100 + (i % TARGET)),
  }));
}

function buildAdminProfiles() {
  const departments = ['Operations', 'Customer Support', 'Marketing', 'Catalog', 'Inventory', 'Finance', 'Community', 'Beauty Advisory'];
  const jobs = ['Admin', 'Staff', 'Specialist', 'Supervisor', 'Manager', 'Coordinator'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`admin-profile-${i}`, {
    account_id: staffAccountId(i),
    employee_code: `KNL-EMP-${pad(i + 1, 4)}`,
    full_name: `${pick(vietnameseNames, i)} - Staff`,
    department: pick(departments, i),
    job_title: `${pick(jobs, i)} ${pick(departments, i)}`,
    manager_account_id: i === 0 ? null : staffAccountId(0),
    employment_status: 'active',
    created_at: daysFromNow(-110 + i),
    updated_at: new Date(),
  }));
}

function buildCustomerProfiles() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`customer-${i}`, {
    account_id: customerAccountId(i),
    customer_code: `KNL-CUS-${pad(i + 1, 5)}`,
    full_name: pick(vietnameseNames, i),
    display_name: pick(vietnameseNames, i).split(' ').slice(-2).join(' '),
    gender: i % 9 === 0 ? 'other' : 'female',
    date_of_birth: new Date(1992 + (i % 12), i % 12, (i % 26) + 1),
    avatar_url: `${IMAGE_BASE_URL}/avatars/customer-${pad(i + 1, 3)}.jpg`,
    customer_status: 'active',
    created_at: daysFromNow(-115 + i),
    updated_at: new Date(),
  }));
}

function buildCustomerAddresses() {
  return Array.from({ length: TARGET }, (_, i) => {
    const [city, district, ward] = pick(cities, i);
    return docWithId(`customer-address-${i}`, {
      customer_id: customerId(i),
      address_label: i % 3 === 0 ? 'Nhà riêng' : i % 3 === 1 ? 'Văn phòng' : 'Địa chỉ nhận mỹ phẩm',
      recipient_name: pick(vietnameseNames, i),
      phone: cleanPhone(i),
      address_line_1: `${12 + i} đường Makeup Beauty ${i + 1}`,
      address_line_2: i % 4 === 0 ? 'Gần cửa hàng tiện lợi' : '',
      ward,
      district,
      city,
      country_code: 'VN',
      postal_code: `7${pad(1000 + i, 5)}`,
      address_type: i % 3 === 0 ? 'home' : i % 3 === 1 ? 'office' : 'other',
      address_note: 'Gọi trước khi giao, ưu tiên giao giờ hành chính.',
      is_default_shipping: true,
      is_default_billing: i % 2 === 0,
      created_at: daysFromNow(-90 + i),
      updated_at: new Date(),
    });
  });
}

function buildCustomerConsents() {
  const types = ['terms', 'privacy', 'marketing_email', 'marketing_sms', 'beauty_profile_personalization'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`customer-consent-${i}`, {
    customer_id: customerId(i),
    consent_type: pick(types, i),
    consent_status: 'accepted',
    consent_version: `2026.${(i % 4) + 1}`,
    source: 'mobile_app',
    consented_at: daysFromNow(-85 + i),
    revoked_at: null,
    ip_address: `10.10.${Math.floor(i / 10)}.${20 + i}`,
    user_agent: 'KanilaAndroid/1.0 Seed',
  }));
}

function buildCustomerPreferences() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`customer-preference-${i}`, {
    customer_id: customerId(i),
    skin_type: pick(skinTypes, i),
    skin_concerns: [pick(skinConcerns, i), pick(skinConcerns, i + 2)],
    undertone: pick(undertones, i),
    shade_preference: pick(['fair', 'light', 'medium', 'tan', 'deep'], i),
    preferred_finish: pick(['matte', 'natural', 'dewy', 'satin', 'glossy'], i),
    preferred_categories: [pick(['foundation', 'lipstick', 'mascara', 'eyeshadow', 'blush'], i)],
    budget_range: pick(['under_200k', '200k_500k', '500k_1m', 'premium'], i),
    avoid_ingredients: i % 4 === 0 ? ['fragrance'] : i % 4 === 1 ? ['alcohol denat'] : [],
    favorite_brand_codes: ['MAYBELLINE', 'LOREAL', 'ROMAND', 'PERIPERA', 'MAC'].slice(0, (i % 5) + 1),
    notification_opt_in: i % 5 !== 0,
    updated_at: new Date(),
  }));
}

function buildCustomerBeautyProfiles() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`customer-beauty-profile-${i}`, {
    customer_id: customerId(i),
    profile_name: `Beauty Profile ${pad(i + 1, 2)}`,
    skin_type: pick(skinTypes, i),
    skin_tone: pick(['fair', 'light', 'medium', 'tan', 'deep'], i),
    undertone: pick(undertones, i),
    concerns: [pick(skinConcerns, i), pick(skinConcerns, i + 1)],
    makeup_style: pick(makeupStyles, i),
    lip_color_preference: pick(['mlbb', 'đỏ lạnh', 'cam đất', 'hồng đất', 'nude beige'], i),
    base_preference: pick(['mỏng nhẹ', 'che phủ cao', 'lâu trôi', 'kiềm dầu'], i),
    sensitivity_level: pick(['low', 'medium', 'high'], i),
    completed_score: 85 + (i % 15),
    is_active: true,
    created_at: daysFromNow(-60 + i),
    updated_at: new Date(),
  }));
}

function buildBeautyReferences() {
  const groups = ['skin_type', 'skin_concern', 'sensitivity_level', 'skin_tone', 'undertone', 'shade_preference', 'lip_color_preference', 'makeup_style', 'beauty_goal', 'texture_preference'];
  return Array.from({ length: TARGET }, (_, i) => {
    const group = pick(groups, i);
    return docWithId(`beauty-reference-${i}`, {
      reference_group: group,
      reference_code: `KNL_${group.toUpperCase()}_${pad(i + 1, 3)}`,
      display_name_vi: `Tiêu chí ${group.replace(/_/g, ' ')} ${i + 1}`,
      display_name_en: `Kanila ${group.replace(/_/g, ' ')} ${i + 1}`,
      description: `Dữ liệu tham chiếu phục vụ beauty profile và recommendation cho app makeup Kanila.`,
      helper_text: 'Chọn nếu phù hợp với nhu cầu trang điểm và chăm sóc da khi mua makeup.',
      parent_code: null,
      sort_order: i + 1,
      is_active: true,
      is_multi_select: !['skin_type', 'undertone', 'skin_tone'].includes(group),
      severity_enabled: group === 'skin_concern',
      recommendation_weight: 1 + (i % 5) / 10,
      boost_tags: [group, 'makeup'],
      avoid_tags: [],
      preferred_ingredients: [],
      avoid_ingredients: [],
      recommended_categories: ['foundation', 'lipstick', 'mascara'],
      warning_text: '',
      created_at: daysFromNow(-50 + i),
      updated_at: new Date(),
    });
  });
}

function buildGuestSessions() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`guest-session-${i}`, {
    guest_session_id: `guest_knl_${pad(i + 1, 5)}`,
    device_id: `android-seed-device-${pad(i + 1, 4)}`,
    device_fingerprint: crypto.createHash('sha1').update(`guest-${i}`).digest('hex'),
    platform: 'android',
    first_seen_at: daysFromNow(-45 + i),
    last_seen_at: daysFromNow(-(i % 7)),
    expires_at: daysFromNow(30 + i),
    metadata: { source: 'seed', app_version: '1.0.0' },
    created_at: daysFromNow(-45 + i),
    updated_at: new Date(),
  }));
}

function buildCarts(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    const qty = (i % 3) + 1;
    const subtotal = money(productPrice(product) * qty);
    const discount = i % 4 === 0 ? money(subtotal * 0.08) : 0;
    return docWithId(`cart-${i}`, {
      owner_type: 'customer',
      customer_id: customerId(i),
      guest_session_id: null,
      cart_status: i % 7 === 0 ? 'converted' : 'active',
      currency_code: 'VND',
      item_count: qty,
      subtotal_amount: subtotal,
      discount_amount: discount,
      total_amount: subtotal - discount,
      expires_at: daysFromNow(14 + i),
      created_at: daysFromNow(-20 + i),
      updated_at: new Date(),
    });
  });
}

function buildCartItems(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const productId = getVariantProductId(variant);
    const product = ctx.productById.get(String(productId));
    const qty = (i % 3) + 1;
    const unit = money(productPrice(product));
    const discount = i % 5 === 0 ? money(unit * 0.05) : 0;
    return docWithId(`cart-item-${i}`, {
      line_key: `cart-${pad(i + 1, 3)}:${variantSku(variant, i)}`,
      product_id: productId,
      productId: productId,
      cart_id: cartId(i),
      variant_id: variant._id,
      variantId: variant._id,
      sku_snapshot: variantSku(variant, i),
      product_name_snapshot: productName(product),
      variant_name_snapshot: variantName(variant),
      brand_name_snapshot: product.brandNameSnapshot || product.brandName || '',
      image_url_snapshot: `${IMAGE_BASE_URL}/products/product-${pad(i + 1, 3)}.jpg`,
      compare_at_price_amount: unit + money(unit * 0.15),
      stock_status: 'in_stock',
      quantity: qty,
      selected: true,
      unit_price_amount: unit,
      discount_amount: discount,
      final_unit_price_amount: unit - discount,
      line_total_amount: (unit - discount) * qty,
      added_at: daysFromNow(-10 + (i % 10)),
      updated_at: new Date(),
    });
  });
}

function buildShippingMethods() {
  const serviceNames = ['Tiêu chuẩn', 'Nhanh nội thành', 'Hỏa tốc', 'Tiết kiệm', 'COD an toàn'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`shipping-method-${i}`, {
    shipping_method_code: `KNL_SHIP_${pad(i + 1, 3)}`,
    shipping_method_name: `${pick(carriers, i)} ${pick(serviceNames, i)}`,
    carrier_code: pick(carriers, i).toUpperCase().replace(/[^A-Z0-9]/g, '_'),
    service_code: `SVC_${pad(i + 1, 3)}`,
    service_name: pick(serviceNames, i),
    description: 'Phương thức giao hàng cho đơn mỹ phẩm Kanila, hỗ trợ theo dõi vận đơn và COD khi đủ điều kiện.',
    base_fee_amount: money(18000 + (i % 8) * 5000),
    free_shipping_min_amount: money(399000 + (i % 5) * 100000),
    estimated_days_min: 1 + (i % 3),
    estimated_days_max: 2 + (i % 5),
    currency_code: 'VND',
    is_cod_supported: i % 4 !== 0,
    is_active: true,
    created_at: daysFromNow(-80 + i),
    updated_at: new Date(),
  }));
}

function buildCheckoutSessions(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    const subtotal = money(productPrice(product) * ((i % 2) + 1));
    const shipping = money(22000 + (i % 5) * 6000);
    const discount = i % 3 === 0 ? money(subtotal * 0.1) : 0;
    return docWithId(`checkout-session-${i}`, {
      customer_id: customerId(i),
      cart_id: cartId(i),
      checkout_status: i % 6 === 0 ? 'completed' : 'active',
      currency_code: 'VND',
      subtotal_amount: subtotal,
      item_discount_amount: 0,
      order_discount_amount: discount,
      shipping_fee_amount: shipping,
      tax_amount: 0,
      grand_total_amount: subtotal + shipping - discount,
      selected_coupon_code: i % 3 === 0 ? `KNLMAKEUP${pad(i + 1, 3)}` : '',
      payment_method_code: pick(paymentProviders, i),
      expires_at: daysFromNow(2 + (i % 5)),
      created_at: daysFromNow(-9 + (i % 8)),
      updated_at: new Date(),
    });
  });
}

function buildCheckoutAddresses() {
  return Array.from({ length: TARGET }, (_, i) => {
    const [city, district, ward] = pick(cities, i);
    return docWithId(`checkout-address-${i}`, {
      checkout_session_id: checkoutSessionId(i),
      address_type: 'shipping',
      recipient_name: pick(vietnameseNames, i),
      phone: cleanPhone(i),
      address_line_1: `${28 + i} đường Kanila Beauty ${i + 1}`,
      address_line_2: i % 2 === 0 ? 'Tòa nhà văn phòng, lễ tân nhận hàng' : '',
      ward,
      district,
      city,
      country_code: 'VN',
      postal_code: `7${pad(2000 + i, 5)}`,
      address_note: 'Mỹ phẩm dễ vỡ, vui lòng giao nhẹ tay.',
      created_at: daysFromNow(-8 + (i % 8)),
      updated_at: new Date(),
    });
  });
}

function buildCheckoutShippingMethods() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`checkout-shipping-${i}`, {
    checkout_session_id: checkoutSessionId(i),
    shipping_method_id: shippingMethodId(i),
    shipping_method_code: `KNL_SHIP_${pad(i + 1, 3)}`,
    carrier_code: pick(carriers, i).toUpperCase().replace(/[^A-Z0-9]/g, '_'),
    carrier_name: pick(carriers, i),
    service_name: i % 2 === 0 ? 'Giao tiêu chuẩn' : 'Giao nhanh',
    estimated_days_min: 1 + (i % 2),
    estimated_days_max: 2 + (i % 4),
    shipping_fee_amount: money(22000 + (i % 5) * 6000),
    currency_code: 'VND',
    is_selected: true,
    created_at: daysFromNow(-8 + (i % 8)),
  }));
}

function buildPromotions() {
  const names = ['Flash Sale Makeup', 'Lip Combo Deal', 'Base Makeup Week', 'Eye Makeup Festival', 'New Arrival Gift'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`promotion-${i}`, {
    promotionCode: `KNL_PROMO_${pad(i + 1, 3)}`,
    promotionName: `${pick(names, i)} ${pad(i + 1, 2)}`,
    description: 'Khuyến mãi thật cho ngành makeup: áp dụng cho sản phẩm trang điểm, combo quà tặng và mini size.',
    promotionType: pick(['percentage_discount', 'fixed_amount', 'free_shipping', 'gift_with_purchase', 'bundle'], i),
    priority: 10 + (i % 20),
    startAt: daysFromNow(-20 + i),
    endAt: daysFromNow(30 + i),
    promotionStatus: 'active',
    isActive: true,
    createdByAccountId: staffAccountId(i % TARGET),
    createdAt: daysFromNow(-30 + i),
    updatedAt: new Date(),
  }));
}

function buildPromotionRules() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`promotion-rule-${i}`, {
    promotionId: promotionId(i),
    ruleType: pick(['min_order_amount', 'customer_segment', 'category_match', 'brand_match', 'first_purchase'], i),
    operator: pick(['gte', 'eq', 'in'], i),
    ruleValue: i % 2 === 0 ? money(299000 + i * 10000) : pick(['makeup_lovers', 'new_customer', 'vip_customer'], i),
    minOrderAmount: money(199000 + (i % 10) * 50000),
    maxUsagePerCustomer: 1 + (i % 3),
    isActive: true,
    createdAt: daysFromNow(-29 + i),
    updatedAt: new Date(),
  }));
}

function buildPromotionTargets(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    const brand = ctx.brands[i % Math.max(ctx.brands.length, 1)];
    const category = ctx.categories[i % Math.max(ctx.categories.length, 1)];
    const targetType = pick(['product', 'brand', 'category', 'cart'], i);
    return docWithId(`promotion-target-${i}`, {
      promotionId: promotionId(i),
      targetType,
      productId: targetType === 'product' ? product._id : null,
      brandId: targetType === 'brand' && brand ? brand._id : null,
      categoryId: targetType === 'category' && category ? category._id : null,
      targetCode: targetType === 'cart' ? 'MAKEUP_CART' : '',
      discountType: pick(['percentage', 'fixed_amount', 'free_shipping'], i),
      discountValue: i % 2 === 0 ? 10 + (i % 20) : money(20000 + i * 1000),
      maxDiscountAmount: money(50000 + (i % 8) * 10000),
      isActive: true,
      createdAt: daysFromNow(-28 + i),
      updatedAt: new Date(),
    });
  });
}

function buildCoupons() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`coupon-${i}`, {
    promotionId: promotionId(i),
    couponCode: `KNLMAKEUP${pad(i + 1, 3)}`,
    couponName: `Voucher Makeup Kanila ${pad(i + 1, 2)}`,
    description: 'Voucher áp dụng cho đơn trang điểm Kanila, có điều kiện theo giá trị giỏ hàng và thời gian hiệu lực.',
    discountType: i % 2 === 0 ? 'percentage' : 'fixed_amount',
    discountValue: i % 2 === 0 ? 5 + (i % 20) : money(20000 + i * 1000),
    minOrderAmount: money(199000 + (i % 8) * 50000),
    maxDiscountAmount: money(50000 + (i % 10) * 5000),
    validFrom: daysFromNow(-15 + i),
    validTo: daysFromNow(45 + i),
    usageLimitTotal: 500 + i * 10,
    usageLimitPerCustomer: 1 + (i % 3),
    usedCount: i % 20,
    couponStatus: 'active',
    isActive: true,
    createdAt: daysFromNow(-20 + i),
    updatedAt: new Date(),
  }));
}

function buildCustomerCoupons() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`customer-coupon-${i}`, {
    customer_id: customerId(i),
    couponId: couponId(i),
    coupon_code_snapshot: `KNLMAKEUP${pad(i + 1, 3)}`,
    status: i % 5 === 0 ? 'used' : 'available',
    assigned_at: daysFromNow(-10 + i),
    claimed_at: daysFromNow(-8 + i),
    used_at: i % 5 === 0 ? daysFromNow(-2 + i) : null,
    expires_at: daysFromNow(45 + i),
    source: pick(['campaign', 'loyalty', 'cart_recovery', 'birthday'], i),
    createdAt: daysFromNow(-10 + i),
    updatedAt: new Date(),
  }));
}

function buildCouponRedemptions() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`coupon-redemption-${i}`, {
    couponId: couponId(i),
    customer_id: customerId(i),
    order_id: orderId(i),
    coupon_code_snapshot: `KNLMAKEUP${pad(i + 1, 3)}`,
    discount_amount: money(20000 + (i % 10) * 5000),
    currency_code: 'VND',
    redeemedAt: daysFromNow(-5 + (i % 5)),
    redemptionStatus: i % 8 === 0 ? 'reversed' : 'redeemed',
    createdAt: daysFromNow(-5 + (i % 5)),
    updatedAt: new Date(),
  }));
}

function buildOrders(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    const qty = (i % 3) + 1;
    const subtotal = money(productPrice(product) * qty);
    const shipping = money(22000 + (i % 5) * 6000);
    const discount = i % 4 === 0 ? money(subtotal * 0.1) : 0;
    const grand = subtotal + shipping - discount;
    return docWithId(`order-${i}`, {
      order_number: `KNL${new Date().getFullYear()}${pad(i + 1, 6)}`,
      customer_id: customerId(i),
      checkout_session_id: checkoutSessionId(i),
      cart_id: cartId(i),
      order_status: pick(orderStatuses, i),
      payment_status: pick(paymentStatuses, i),
      fulfillment_status: pick(fulfillmentStatuses, i),
      currency_code: 'VND',
      subtotal_amount: subtotal,
      discount_amount: discount,
      shipping_fee_amount: shipping,
      total_amount: grand,
      note: i % 4 === 0 ? 'Khách yêu cầu đóng gói kỹ hộp makeup.' : '',
      placed_at: daysFromNow(-25 + i),
      confirmed_at: i % 6 === 0 ? null : daysFromNow(-24 + i),
      completed_at: i % 4 === 0 ? daysFromNow(-18 + i) : null,
      created_at: daysFromNow(-25 + i),
      updated_at: new Date(),
    });
  });
}

function buildOrderItems(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const productId = getVariantProductId(variant);
    const product = ctx.productById.get(String(productId));
    const qty = (i % 3) + 1;
    const unit = money(productPrice(product));
    return docWithId(`order-item-${i}`, {
      order_id: orderId(i),
      product_id: productId,
      productId: productId,
      variant_id: variant._id,
      variantId: variant._id,
      sku_snapshot: variantSku(variant, i),
      product_name_snapshot: productName(product),
      variant_name_snapshot: variantName(variant),
      brand_name_snapshot: product.brandNameSnapshot || product.brandName || '',
      image_url_snapshot: `${IMAGE_BASE_URL}/orders/order-item-${pad(i + 1, 3)}.jpg`,
      quantity: qty,
      unit_price_amount: unit,
      discount_amount: i % 4 === 0 ? money(unit * 0.08) : 0,
      final_unit_price_amount: i % 4 === 0 ? money(unit * 0.92) : unit,
      line_total_amount: (i % 4 === 0 ? money(unit * 0.92) : unit) * qty,
      item_status: i % 8 === 0 ? 'returned' : 'fulfilled',
      created_at: daysFromNow(-25 + i),
      updated_at: new Date(),
    });
  });
}

function buildOrderAddresses() {
  return Array.from({ length: TARGET }, (_, i) => {
    const [city, district, ward] = pick(cities, i);
    return docWithId(`order-address-${i}`, {
      order_id: orderId(i),
      address_type: 'shipping',
      recipient_name: pick(vietnameseNames, i),
      phone: cleanPhone(i),
      address_line_1: `${36 + i} đường Beauty Commerce ${i + 1}`,
      address_line_2: '',
      ward,
      district,
      city,
      country_code: 'VN',
      postal_code: `7${pad(3000 + i, 5)}`,
      address_note: 'Địa chỉ snapshot khi đặt hàng.',
    });
  });
}

function buildOrderTotals(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    const subtotal = money(productPrice(product) * ((i % 3) + 1));
    const itemDiscount = i % 4 === 0 ? money(subtotal * 0.05) : 0;
    const orderDiscount = i % 3 === 0 ? money(subtotal * 0.08) : 0;
    const shipping = money(22000 + (i % 5) * 6000);
    return docWithId(`order-total-${i}`, {
      order_id: orderId(i),
      subtotal_amount: subtotal,
      item_discount_amount: itemDiscount,
      order_discount_amount: orderDiscount,
      shipping_fee_amount: shipping,
      tax_amount: 0,
      grand_total_amount: subtotal + shipping - itemDiscount - orderDiscount,
      refunded_amount: i % 9 === 0 ? money(subtotal * 0.5) : 0,
      currency_code: 'VND',
      created_at: daysFromNow(-25 + i),
      updated_at: new Date(),
    });
  });
}

function buildOrderStatusHistory() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`order-status-history-${i}`, {
    order_id: orderId(i),
    old_order_status: i % 2 === 0 ? 'pending' : 'confirmed',
    new_order_status: pick(orderStatuses, i),
    old_payment_status: i % 2 === 0 ? 'unpaid' : 'authorized',
    new_payment_status: pick(paymentStatuses, i),
    old_fulfillment_status: 'unfulfilled',
    new_fulfillment_status: pick(fulfillmentStatuses, i),
    changed_by_account_id: staffAccountId(i % TARGET),
    change_reason: 'Seed timeline cho màn theo dõi đơn hàng Kanila.',
    changed_at: daysFromNow(-24 + i),
  }));
}

function buildPaymentMethods() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`payment-method-${i}`, {
    customer_id: customerId(i),
    provider_code: pick(paymentProviders, i),
    payment_method_type: pick(['cod', 'wallet', 'bank_card', 'bank_transfer'], i),
    display_name: pick(paymentProviders, i) === 'COD' ? 'Thanh toán khi nhận hàng' : `${pick(paymentProviders, i)} đã lưu`,
    token_reference: `tok_seed_${crypto.createHash('sha1').update(`pm-${i}`).digest('hex').slice(0, 16)}`,
    last4: String(1000 + i).slice(-4),
    expiry_month: (i % 12) + 1,
    expiry_year: 2030 + (i % 5),
    billing_name: pick(vietnameseNames, i),
    is_default: true,
    payment_method_status: 'active',
    created_at: daysFromNow(-70 + i),
    updated_at: new Date(),
  }));
}

function buildPaymentIntents(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const total = money(productPrice(ctx.products[i % ctx.products.length]) + 30000);
    return docWithId(`payment-intent-${i}`, {
      order_id: orderId(i),
      provider_code: pick(paymentProviders, i),
      provider_intent_id: `pi_knl_seed_${pad(i + 1, 5)}`,
      payment_method_id: oid(`payment-method-${i}`),
      amount: total,
      amount_amount: total,
      currency_code: 'VND',
      intent_status: pick(['requires_payment_method', 'requires_confirmation', 'succeeded', 'failed', 'cancelled'], i),
      client_secret: `seed_client_secret_${pad(i + 1, 5)}`,
      createdAt: daysFromNow(-25 + i),
      updatedAt: new Date(),
    });
  });
}

function buildPaymentTransactions(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const total = money(productPrice(ctx.products[i % ctx.products.length]) + 30000);
    return docWithId(`payment-transaction-${i}`, {
      paymentIntentId: paymentIntentId(i),
      payment_intent_id: paymentIntentId(i),
      order_id: orderId(i),
      provider_code: pick(paymentProviders, i),
      provider_transaction_id: `txn_knl_seed_${pad(i + 1, 5)}`,
      transaction_type: i % 10 === 0 ? 'refund' : 'payment',
      transaction_status: i % 6 === 0 ? 'failed' : 'succeeded',
      amount: total,
      amount_amount: total,
      currency_code: 'VND',
      gateway_response_code: i % 6 === 0 ? 'PAYMENT_FAILED' : 'APPROVED',
      gateway_message: i % 6 === 0 ? 'Thanh toán chưa thành công, có thể thử lại hoặc đổi phương thức.' : 'Payment approved',
      processed_at: daysFromNow(-24 + i),
      createdAt: daysFromNow(-24 + i),
      updatedAt: new Date(),
    });
  });
}

function buildWarehouses() {
  return Array.from({ length: TARGET }, (_, i) => {
    const [city, district, ward] = pick(cities, i);
    return docWithId(`warehouse-${i}`, {
      warehouseCode: `KNL_WH_${pad(i + 1, 3)}`,
      warehouseName: `Kho Kanila ${city} ${pad(i + 1, 2)}`,
      warehouseType: pick(['main', 'retail', 'dark_store', 'return_center'], i),
      addressLine1: `${100 + i} đường Logistics Beauty`,
      ward,
      district,
      city,
      country_code: 'VN',
      phone: `028${String(7000000 + i * 123).slice(-7)}`,
      isActive: true,
      createdAt: daysFromNow(-100 + i),
      updatedAt: new Date(),
    });
  });
}

function buildInventoryBalances(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const onHand = 80 + (i % 70);
    const reserved = i % 12;
    return docWithId(`inventory-balance-${i}`, {
      warehouseId: warehouseId(i),
      warehouse_id: warehouseId(i),
      variantId: variant._id,
      variant_id: variant._id,
      sku_snapshot: variantSku(variant, i),
      onHandQty: onHand,
      availableQty: onHand - reserved,
      reservedQty: reserved,
      safetyStockQty: 10 + (i % 10),
      stockStatus: onHand - reserved > 20 ? 'in_stock' : 'low_stock',
      lastCountedAt: daysFromNow(-3 + (i % 3)),
      createdAt: daysFromNow(-70 + i),
      updatedAt: new Date(),
    });
  });
}

function buildInventoryTransactions(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const before = 70 + i;
    const delta = i % 5 === 0 ? -((i % 4) + 1) : 20 + (i % 10);
    return docWithId(`inventory-transaction-${i}`, {
      warehouseId: warehouseId(i),
      warehouse_id: warehouseId(i),
      variantId: variant._id,
      variant_id: variant._id,
      sku_snapshot: variantSku(variant, i),
      transactionType: delta > 0 ? 'receipt' : 'sale_reservation',
      quantityDelta: delta,
      quantityBefore: before,
      quantityAfter: before + delta,
      referenceType: delta > 0 ? 'purchase_receipt' : 'order',
      referenceId: delta > 0 ? `PO-SEED-${pad(i + 1, 5)}` : String(orderId(i)),
      note: 'Giao dịch tồn kho seed cho kiểm tra stock/reservation trong checkout.',
      createdByAccountId: staffAccountId(i % TARGET),
      createdAt: daysFromNow(-60 + i),
      updatedAt: new Date(),
    });
  });
}

function buildStockReservations(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    return docWithId(`stock-reservation-${i}`, {
      cart_id: cartId(i),
      checkout_session_id: checkoutSessionId(i),
      order_id: i % 6 === 0 ? orderId(i) : null,
      variantId: variant._id,
      variant_id: variant._id,
      warehouseId: warehouseId(i),
      warehouse_id: warehouseId(i),
      reservation_code: `RSV-KNL-${pad(i + 1, 5)}`,
      reservedQty: (i % 2) + 1,
      reservationStatus: i % 6 === 0 ? 'converted' : 'active',
      expiresAt: minutesFromNow(30 + i),
      createdAt: daysFromNow(-2 + (i % 2)),
      updatedAt: new Date(),
    });
  });
}

function buildPriceBooks() {
  const names = ['Retail VND', 'App Member', 'Flash Sale', 'VIP Beauty', 'Mini & Travel', 'Creator Affiliate', 'Bundle Routine'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`price-book-${i}`, {
    priceBookCode: `KNL_PRICE_${pad(i + 1, 3)}`,
    priceBookName: `${pick(names, i)} ${pad(i + 1, 2)}`,
    description: 'Bảng giá dùng cho app Kanila makeup, hỗ trợ giá bán lẻ, flash sale, VIP và bundle.',
    currencyCode: 'VND',
    customerSegment: pick(['all', 'new_customer', 'member', 'vip', 'creator'], i),
    effectiveFrom: daysFromNow(-30 + i),
    effectiveTo: daysFromNow(90 + i),
    isActive: true,
    createdAt: daysFromNow(-30 + i),
    updatedAt: new Date(),
  }));
}

function buildPriceBookEntries(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const product = ctx.productById.get(String(getVariantProductId(variant)));
    const list = money(productPrice(product));
    const sale = i % 3 === 0 ? money(list * 0.9) : list;
    return docWithId(`price-book-entry-${i}`, {
      priceBookId: priceBookId(i),
      price_book_id: priceBookId(i),
      variantId: variant._id,
      variant_id: variant._id,
      listPriceAmount: list,
      salePriceAmount: sale,
      effectiveFrom: daysFromNow(-30 + i),
      effectiveTo: daysFromNow(90 + i),
      isActive: true,
      createdAt: daysFromNow(-30 + i),
      updatedAt: new Date(),
    });
  });
}

function buildPriceHistories(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const product = ctx.productById.get(String(getVariantProductId(variant)));
    const oldPrice = money(productPrice(product) * 1.08);
    const newPrice = money(productPrice(product));
    return docWithId(`price-history-${i}`, {
      variantId: variant._id,
      variant_id: variant._id,
      priceBookId: priceBookId(i),
      price_book_id: priceBookId(i),
      oldPriceAmount: oldPrice,
      newPriceAmount: newPrice,
      changedByAccountId: staffAccountId(i % TARGET),
      changedAt: daysFromNow(-20 + i),
      reason: 'Cập nhật giá bán makeup theo campaign Kanila.',
      createdAt: daysFromNow(-20 + i),
      updatedAt: new Date(),
    });
  });
}

function buildLoyaltyTiers() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`loyalty-tier-${i}`, {
    tierCode: `KNL_TIER_${pad(i + 1, 3)}`,
    tierName: `Kanila Beauty Tier ${pad(i + 1, 2)}`,
    minPoints: i * 500,
    maxPoints: i === TARGET - 1 ? null : (i + 1) * 500 - 1,
    earnRate: 1 + (i % 10) / 10,
    birthdayBonusPoints: 50 + i * 5,
    benefits: [
      `Tích điểm ${1 + (i % 10) / 10}x cho đơn makeup`,
      i % 3 === 0 ? 'Ưu tiên voucher flash sale' : 'Quà sample theo hạng',
    ],
    tierStatus: 'active',
    isActive: true,
    createdAt: daysFromNow(-100 + i),
    updatedAt: new Date(),
  }));
}

function buildLoyaltyAccounts() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`loyalty-account-${i}`, {
    customer_id: customerId(i),
    loyaltyTierId: loyaltyTierId(i),
    tierId: loyaltyTierId(i),
    currentPoints: 300 + i * 37,
    lifetimePoints: 1200 + i * 91,
    pendingPoints: i % 4 === 0 ? 50 : 0,
    redeemedPoints: i * 11,
    accountStatus: 'active',
    joinedAt: daysFromNow(-80 + i),
    createdAt: daysFromNow(-80 + i),
    updatedAt: new Date(),
  }));
}

function buildLoyaltyPointLedger() {
  return Array.from({ length: TARGET }, (_, i) => {
    const before = 300 + i * 37;
    const delta = i % 5 === 0 ? -100 : 25 + (i % 10) * 5;
    return docWithId(`loyalty-point-ledger-${i}`, {
      loyaltyAccountId: loyaltyAccountId(i),
      customer_id: customerId(i),
      order_id: i % 5 === 0 ? null : orderId(i),
      transactionType: delta > 0 ? 'earn_order' : 'redeem_coupon',
      pointsDelta: delta,
      pointsBefore: before,
      pointsAfter: before + delta,
      expiryDate: delta > 0 ? daysFromNow(365) : null,
      referenceType: delta > 0 ? 'order' : 'coupon',
      referenceId: delta > 0 ? String(orderId(i)) : `KNLMAKEUP${pad(i + 1, 3)}`,
      note: delta > 0 ? 'Tích điểm từ đơn makeup Kanila.' : 'Đổi điểm lấy voucher makeup.',
      createdAt: daysFromNow(-20 + i),
      updatedAt: new Date(),
    });
  });
}

function buildShipments() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`shipment-${i}`, {
    shipment_number: `SHP-KNL-${pad(i + 1, 6)}`,
    order_id: orderId(i),
    warehouseId: warehouseId(i),
    carrier_code: pick(carriers, i).toUpperCase().replace(/[^A-Z0-9]/g, '_'),
    carrier_name: pick(carriers, i),
    tracking_number: `TRK${new Date().getFullYear()}${pad(i + 1, 7)}`,
    shipmentStatus: pick(['pending', 'packed', 'shipped', 'in_transit', 'delivered'], i),
    shippedAt: i % 5 === 0 ? null : daysFromNow(-15 + i),
    deliveredAt: i % 4 === 0 ? daysFromNow(-10 + i) : null,
    estimatedDeliveryAt: daysFromNow(3 + (i % 5)),
    shipping_fee_amount: money(22000 + (i % 5) * 6000),
    currency_code: 'VND',
    createdAt: daysFromNow(-16 + i),
    updatedAt: new Date(),
  }));
}

function buildShipmentItems(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const qty = (i % 3) + 1;
    return docWithId(`shipment-item-${i}`, {
      shipmentId: shipmentId(i),
      orderItemId: orderItemId(i),
      variantId: variant._id,
      shippedQty: qty,
      deliveredQty: i % 5 === 0 ? 0 : qty,
      returnedQty: i % 9 === 0 ? 1 : 0,
      createdAt: daysFromNow(-15 + i),
      updatedAt: new Date(),
    });
  });
}

function buildShipmentEvents() {
  const events = [
    ['created', 'Đơn giao hàng đã được tạo'],
    ['packed', 'Kho đã đóng gói sản phẩm makeup'],
    ['picked_up', 'Đơn vị vận chuyển đã lấy hàng'],
    ['in_transit', 'Đơn hàng đang được vận chuyển'],
    ['delivered', 'Khách đã nhận hàng'],
  ];
  return Array.from({ length: TARGET }, (_, i) => {
    const [code, desc] = pick(events, i);
    return docWithId(`shipment-event-${i}`, {
      shipmentId: shipmentId(i),
      eventCode: code,
      eventStatus: code,
      eventDescription: desc,
      locationText: pick(cities, i)[0],
      occurredAt: daysFromNow(-14 + i),
      rawPayload: { carrier: pick(carriers, i), seed: true },
      createdAt: daysFromNow(-14 + i),
      updatedAt: new Date(),
    });
  });
}

function buildReturns() {
  const reasons = ['wrong_shade', 'damaged_item', 'missing_item', 'changed_mind', 'allergy_concern'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`return-${i}`, {
    return_number: `RTN-KNL-${pad(i + 1, 6)}`,
    order_id: orderId(i),
    shipmentId: shipmentId(i),
    requested_by_customer_id: customerId(i),
    approvedByAccountId: i % 4 === 0 ? staffAccountId(i % TARGET) : null,
    returnStatus: pick(['requested', 'approved', 'in_review', 'received', 'completed'], i),
    reason_code: pick(reasons, i),
    reason_text: 'Yêu cầu đổi/trả liên quan sản phẩm makeup, shade hoặc tình trạng nhận hàng.',
    requested_at: daysFromNow(-8 + i),
    approved_at: i % 4 === 0 ? daysFromNow(-6 + i) : null,
    createdAt: daysFromNow(-8 + i),
    updatedAt: new Date(),
  }));
}

function buildReturnItems(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    return docWithId(`return-item-${i}`, {
      returnId: returnId(i),
      orderItemId: orderItemId(i),
      variantId: variant._id,
      requestedQty: 1,
      approvedQty: i % 4 === 0 ? 1 : 0,
      receivedQty: i % 5 === 0 ? 1 : 0,
      restockQty: i % 5 === 0 ? 1 : 0,
      rejectQty: i % 11 === 0 ? 1 : 0,
      restockStatus: i % 5 === 0 ? 'restocked' : 'pending',
      createdAt: daysFromNow(-8 + i),
      updatedAt: new Date(),
    });
  });
}

function buildRefunds(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    return docWithId(`refund-${i}`, {
      refund_number: `RFD-KNL-${pad(i + 1, 6)}`,
      order_id: orderId(i),
      returnId: returnId(i),
      paymentTransactionId: paymentTransactionId(i),
      requestedByAccountId: customerAccountId(i),
      approvedByAccountId: i % 4 === 0 ? staffAccountId(i % TARGET) : null,
      refundStatus: pick(['requested', 'approved', 'processing', 'refunded', 'rejected'], i),
      refundReason: 'Hoàn tiền cho quy trình return/refund của app makeup Kanila.',
      refundAmount: money(productPrice(product) * 0.5),
      currency_code: 'VND',
      requestedAt: daysFromNow(-7 + i),
      approvedAt: i % 4 === 0 ? daysFromNow(-6 + i) : null,
      refundedAt: i % 5 === 0 ? daysFromNow(-4 + i) : null,
      createdAt: daysFromNow(-7 + i),
      updatedAt: new Date(),
    });
  });
}

function buildWishlists() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`wishlist-${i}`, {
    customer_id: customerId(i),
    wishlistName: pick(['Makeup yêu thích', 'Son muốn mua', 'Base routine', 'Eye look', 'Quà tặng'], i),
    wishlistStatus: 'active',
    isDefault: true,
    itemCount: 1,
    createdAt: daysFromNow(-50 + i),
    updatedAt: new Date(),
  }));
}

function buildWishlistItems(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[(i + 7) % ctx.variants.length];
    const productId = getVariantProductId(variant);
    return docWithId(`wishlist-item-${i}`, {
      wishlistId: wishlistId(i),
      productId,
      variantId: variant._id,
      addedAt: daysFromNow(-40 + i),
      note: pick(['Chờ sale', 'Muốn thử màu này', 'Mua lại khi hết', 'Gợi ý từ Beauty Profile'], i),
      createdAt: daysFromNow(-40 + i),
      updatedAt: new Date(),
    });
  });
}

function buildReviews(ctx) {
  const titles = ['Màu đẹp và dễ dùng', 'Lớp nền mịn hơn mong đợi', 'Mascara giữ mi tốt', 'Son lên màu chuẩn', 'Phù hợp trang điểm hằng ngày'];
  return Array.from({ length: TARGET }, (_, i) => {
    const variant = ctx.variants[i % ctx.variants.length];
    const productId = getVariantProductId(variant);
    return docWithId(`review-${i}`, {
      customer_id: customerId(i),
      orderItemId: orderItemId(i),
      productId,
      variantId: variant._id,
      rating: 4 + (i % 2),
      reviewTitle: pick(titles, i),
      reviewText: `Sản phẩm makeup dùng thực tế ổn, ${pick(skinTypes, i)} vẫn dễ apply. Màu và chất sản phẩm phù hợp nhu cầu cá nhân.`,
      reviewStatus: 'approved',
      isVerifiedPurchase: true,
      skin_type_snapshot: pick(skinTypes, i),
      skin_tone_snapshot: pick(['fair', 'light', 'medium', 'tan', 'deep'], i),
      helpfulCount: 3 + (i % 20),
      createdAt: daysFromNow(-18 + i),
      updatedAt: new Date(),
    });
  });
}

function buildReviewMedias() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`review-media-${i}`, {
    reviewId: reviewId(i),
    mediaType: i % 7 === 0 ? 'video' : 'image',
    mediaUrl: `${IMAGE_BASE_URL}/reviews/review-${pad(i + 1, 3)}.${i % 7 === 0 ? 'mp4' : 'jpg'}`,
    thumbnailUrl: `${IMAGE_BASE_URL}/reviews/thumb-${pad(i + 1, 3)}.jpg`,
    altText: `Ảnh review makeup Kanila ${i + 1}`,
    sortOrder: 1,
    createdAt: daysFromNow(-18 + i),
    updatedAt: new Date(),
  }));
}

function buildReviewVotes() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`review-vote-${i}`, {
    reviewId: reviewId(i),
    customer_id: customerId((i + 5) % TARGET),
    voteType: i % 8 === 0 ? 'not_helpful' : 'helpful',
    createdAt: daysFromNow(-15 + i),
    updatedAt: new Date(),
  }));
}

function buildReviewSummaries(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    const count = 20 + (i % 80);
    return docWithId(`review-summary-${i}`, {
      productId: product._id,
      averageRating: Number((4.1 + (i % 9) / 10).toFixed(1)),
      reviewCount: count,
      rating1Count: i % 3,
      rating2Count: i % 4,
      rating3Count: 2 + (i % 7),
      rating4Count: Math.floor(count * 0.35),
      rating5Count: Math.floor(count * 0.55),
      photoReviewCount: 5 + (i % 20),
      verifiedPurchaseCount: count - (i % 5),
      lastReviewAt: daysFromNow(-5 + (i % 5)),
      createdAt: daysFromNow(-20 + i),
      updatedAt: new Date(),
    });
  });
}

function buildCustomerRecommendationSnapshots(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const selected = [0, 1, 2, 3, 4].map((offset) => ctx.products[(i + offset) % ctx.products.length]);
    return docWithId(`customer-recommendation-snapshot-${i}`, {
      customer_id: customerId(i),
      recommendation_type: 'makeup_profile_home',
      profile_hash: crypto.createHash('sha1').update(`profile-${i}-${SEED_BATCH}`).digest('hex'),
      product_ids: selected.map((product) => product._id),
      items: selected.map((product, rank) => ({
        product_id: product._id,
        rank: rank + 1,
        score: Number((0.94 - rank * 0.04).toFixed(2)),
        reason: `Phù hợp với ${pick(skinTypes, i)} và phong cách ${pick(makeupStyles, i)}.`,
      })),
      algorithm_version: 'kanila_makeup_rule_v1',
      generated_at: new Date(),
      expires_at: daysFromNow(7),
      invalidated_at: null,
      createdAt: daysFromNow(-2),
      updatedAt: new Date(),
    });
  });
}

function buildRecommendationLogs(ctx) {
  return Array.from({ length: TARGET }, (_, i) => {
    const product = ctx.products[i % ctx.products.length];
    return docWithId(`recommendation-log-${i}`, {
      customer_id: customerId(i),
      product_id: product._id,
      recommendation_type: pick(['home_feed', 'product_detail_related', 'cart_upsell', 'beauty_profile'], i),
      request_context: {
        skin_type: pick(skinTypes, i),
        undertone: pick(undertones, i),
        makeup_style: pick(makeupStyles, i),
      },
      score: Number((0.78 + (i % 20) / 100).toFixed(2)),
      reason_text: `Đề xuất vì sản phẩm hợp ${pick(skinTypes, i)}, finish ${pick(['matte', 'dewy', 'natural'], i)} và ngân sách của khách.`,
      algorithm_version: 'kanila_makeup_rule_v1',
      createdAt: daysFromNow(-10 + i),
      updatedAt: new Date(),
    });
  });
}

function buildAuditLogs() {
  const actions = ['CREATE_ORDER', 'UPDATE_STOCK', 'APPROVE_RETURN', 'CREATE_PROMOTION', 'UPDATE_PRICE', 'MODERATE_REVIEW'];
  const entities = ['Order', 'InventoryBalance', 'Return', 'Promotion', 'PriceBookEntry', 'Review'];
  return Array.from({ length: TARGET }, (_, i) => docWithId(`audit-log-${i}`, {
    actor_account_id: staffAccountId(i % TARGET),
    action_code: pick(actions, i),
    entity_name: pick(entities, i),
    entity_id: String(pick([orderId(i), warehouseId(i), returnId(i), promotionId(i), priceBookId(i), reviewId(i)], i)),
    old_values_json: { status: 'before_seed_state' },
    new_values_json: { status: 'after_seed_state', seed: true },
    ip_address: `10.20.${Math.floor(i / 10)}.${30 + i}`,
    user_agent: 'KanilaSeed/1.0 Node.js',
    created_at: daysFromNow(-30 + i),
  }));
}

function buildEmailOtps() {
  return Array.from({ length: TARGET }, (_, i) => docWithId(`email-otp-${i}`, {
    email: `kanila.customer${pad(i + 1, 3)}@seed.kanila.test`,
    purpose: i % 2 === 0 ? 'login' : 'email_verification',
    otp_code_hash: crypto.createHash('sha256').update(`consumed-otp-${i}-${SEED_BATCH}`).digest('hex'),
    expires_at: daysFromNow(1),
    attempt_count: 1,
    account_id: customerAccountId(i),
    consumed_at: new Date(),
    created_at: daysFromNow(-1),
    updated_at: new Date(),
  }));
}

async function seedAll() {
  await connectMongo();
  const db = mongoose.connection.db;
  const existingCollections = await getExistingCollections(db);
  if (existingCollections.has('review_summary') && !existingCollections.has(COLLECTION.reviewSummaries)) {
    COLLECTION.reviewSummaries = 'review_summary';
    const index = SEEDED_COLLECTIONS.indexOf('review_summaries');
    if (index >= 0) SEEDED_COLLECTIONS[index] = 'review_summary';
    console.log('Detected existing review_summary collection. Seeding review_summary to match your current database.');
  }

  const ctx = await getProductContext(db);
  await resetSeedData(db, SEEDED_COLLECTIONS);

  const seedPlan = [
    [COLLECTION.roles, buildRoles()],
    [COLLECTION.permissions, buildPermissions()],
    [COLLECTION.rolePermissions, buildRolePermissions()],
    [COLLECTION.accounts, buildAccounts()],
    [COLLECTION.accountAuthProviders, buildAccountAuthProviders()],
    [COLLECTION.accountRoles, buildAccountRoles()],
    [COLLECTION.adminProfiles, buildAdminProfiles()],
    [COLLECTION.customerProfiles, buildCustomerProfiles()],
    [COLLECTION.customerAddresses, buildCustomerAddresses()],
    [COLLECTION.customerConsents, buildCustomerConsents()],
    [COLLECTION.customerPreferences, buildCustomerPreferences()],
    [COLLECTION.customerBeautyProfiles, buildCustomerBeautyProfiles()],
    [COLLECTION.beautyReferences, buildBeautyReferences()],
    [COLLECTION.guestSessions, buildGuestSessions()],
    [COLLECTION.carts, buildCarts(ctx)],
    [COLLECTION.cartItems, buildCartItems(ctx)],
    [COLLECTION.shippingMethods, buildShippingMethods()],
    [COLLECTION.checkoutSessions, buildCheckoutSessions(ctx)],
    [COLLECTION.checkoutAddresses, buildCheckoutAddresses()],
    [COLLECTION.checkoutShippingMethods, buildCheckoutShippingMethods()],
    [COLLECTION.promotions, buildPromotions()],
    [COLLECTION.promotionRules, buildPromotionRules()],
    [COLLECTION.promotionTargets, buildPromotionTargets(ctx)],
    [COLLECTION.coupons, buildCoupons()],
    [COLLECTION.customerCoupons, buildCustomerCoupons()],
    [COLLECTION.orders, buildOrders(ctx)],
    [COLLECTION.orderItems, buildOrderItems(ctx)],
    [COLLECTION.orderAddresses, buildOrderAddresses()],
    [COLLECTION.orderTotals, buildOrderTotals(ctx)],
    [COLLECTION.orderStatusHistory, buildOrderStatusHistory()],
    [COLLECTION.paymentMethods, buildPaymentMethods()],
    [COLLECTION.paymentIntents, buildPaymentIntents(ctx)],
    [COLLECTION.paymentTransactions, buildPaymentTransactions(ctx)],
    [COLLECTION.couponRedemptions, buildCouponRedemptions()],
    [COLLECTION.warehouses, buildWarehouses()],
    [COLLECTION.inventoryBalances, buildInventoryBalances(ctx)],
    [COLLECTION.inventoryTransactions, buildInventoryTransactions(ctx)],
    [COLLECTION.stockReservations, buildStockReservations(ctx)],
    [COLLECTION.priceBooks, buildPriceBooks()],
    [COLLECTION.priceBookEntries, buildPriceBookEntries(ctx)],
    [COLLECTION.priceHistories, buildPriceHistories(ctx)],
    [COLLECTION.loyaltyTiers, buildLoyaltyTiers()],
    [COLLECTION.loyaltyAccounts, buildLoyaltyAccounts()],
    [COLLECTION.loyaltyPointLedger, buildLoyaltyPointLedger()],
    [COLLECTION.shipments, buildShipments()],
    [COLLECTION.shipmentItems, buildShipmentItems(ctx)],
    [COLLECTION.shipmentEvents, buildShipmentEvents()],
    [COLLECTION.returns, buildReturns()],
    [COLLECTION.returnItems, buildReturnItems(ctx)],
    [COLLECTION.refunds, buildRefunds(ctx)],
    [COLLECTION.wishlists, buildWishlists()],
    [COLLECTION.wishlistItems, buildWishlistItems(ctx)],
    [COLLECTION.reviews, buildReviews(ctx)],
    [COLLECTION.reviewMedias, buildReviewMedias()],
    [COLLECTION.reviewVotes, buildReviewVotes()],
    [COLLECTION.reviewSummaries, buildReviewSummaries(ctx)],
    [COLLECTION.customerRecommendationSnapshots, buildCustomerRecommendationSnapshots(ctx)],
    [COLLECTION.recommendationLogs, buildRecommendationLogs(ctx)],
    [COLLECTION.auditLogs, buildAuditLogs()],
  ];

  if (INCLUDE_RUNTIME_OTP) seedPlan.push([COLLECTION.emailOtps, buildEmailOtps()]);

  for (const [collectionName, docs] of seedPlan) {
    await upsertMany(db, collectionName, docs);
  }

  console.log('\nVerification counts for core active collections:');
  const verifyCollections = [
    'brands', 'categories', 'products', 'product_variants',
    ...SEEDED_COLLECTIONS,
  ];
  const uniqueVerifyCollections = Array.from(new Set(verifyCollections));
  for (const name of uniqueVerifyCollections) {
    const count = await db.collection(name).countDocuments();
    const status = count >= TARGET ? 'OK' : 'CHECK';
    console.log(`  ${name.padEnd(36)} ${String(count).padStart(5)} ${status}`);
  }

  if (!INCLUDE_RUNTIME_OTP) {
    console.log('\nNote: email_otps was intentionally skipped because OTP documents are runtime-only. Use --include-runtime-otp if you still want consumed seed OTP rows for visual completeness.');
  }
  console.log('Note: password_reset_otps is intentionally not seeded because it is deprecated and should be dropped.');
  console.log('\nKanila remaining business seed completed successfully.');
}

seedAll()
  .then(async () => {
    await mongoose.disconnect();
    process.exit(0);
  })
  .catch(async (error) => {
    console.error('\nKanila remaining business seed failed:', error);
    try { await mongoose.disconnect(); } catch (_) {}
    process.exit(1);
  });
