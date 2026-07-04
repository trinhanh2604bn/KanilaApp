# KANILA PROJECT RULES FOR AI AGENTS

> This file is the mandatory rulebook for the Kanila project. Every AI agent, Android Studio AI assistant, developer, reviewer, or code generator must read this file before generating, modifying, refactoring, or documenting any code.
>
> The primary goal is consistency: reusable UI, clean Android Java/XML implementation, strict use of existing `res/` resources, ecommerce-grade UX, and maintainable frontend/backend architecture.

---

## 0. Agent Operating Protocol

Before writing any code, the agent must follow this order:

1. **Read this file first.**
2. **Inspect the existing project structure.**
3. **Inspect `res/` before coding anything.**
4. **Reuse existing resources/components whenever possible.**
5. **Only create new resources when no suitable resource exists.**
6. **State exactly which files will be created or modified.**
7. **Generate code only inside the approved scope.**
8. **Do not rewrite the whole project when the task only asks for one component, screen, or feature.**

### Mandatory pre-coding resource audit

For every Android UI task, the agent must inspect these folders/files first:

```text
app/src/main/res/values/colors.xml
app/src/main/res/values/dimens.xml
app/src/main/res/values/strings.xml
app/src/main/res/values/styles.xml
app/src/main/res/values/themes.xml
app/src/main/res/font/
app/src/main/res/drawable/
app/src/main/res/layout/
app/src/main/res/mipmap/
app/src/main/res/navigation/
```

The agent must check whether the project already contains:

```text
colors
font family
text styles
button styles
input backgrounds
card backgrounds
chip/badge drawables
spacing/dimension tokens
icons
common item layouts
common bottom sheets
empty/error/loading states
```

### Resource reuse rule

The agent must not hardcode or recreate resources that already exist.

Correct behavior:

```xml
android:textColor="@color/text_main"
android:background="@drawable/bg_primary_button"
android:fontFamily="@font/nunito_semibold"
android:paddingStart="@dimen/spacing_l"
```

Incorrect behavior:

```xml
android:textColor="#372B2B"
android:background="#FFADBE"
android:fontFamily="sans-serif"
android:paddingStart="16dp"
```

If a needed token does not exist, add it to the correct `res/values` file with a clear name and minimal scope. Do not create duplicate color names, duplicate font styles, or duplicate background drawables.

---

## 1. Project Identity

**Product name:** Kanila App  
**Product type:** Beauty Commerce Mobile App  
**Frontend:** Android Studio, Java, XML  
**Backend:** Node.js, Express.js, MongoDB Atlas, Mongoose  
**Design direction:** Clean Beauty, Soft Pink, Rounded Card, Product-first Layout, Mobile-first  
**Core business domain:** cosmetics ecommerce, personalized beauty experience, AR try-on, community, chatbot, support, order/return/refund.

Kanila is not a simple mobile copy of the website. The app must be designed around the mobile customer journey:

```text
Discover product
-> understand product fit
-> try AR or check ingredients when available
-> add to cart
-> checkout
-> track order
-> get support / return / refund
-> review / post community content
-> earn loyalty / rewards
-> repurchase
```

The app has four strategic layers:

| Layer | Scope |
|---|---|
| Core Commerce | Auth, Home, Category, Product Listing, Product Detail, Cart, Checkout, Order, Voucher, Wishlist |
| Beauty Experience | Beauty Profile, Skin Match, Ingredient Checker, Skin Journey, Routine Reminder |
| Social Commerce | Community, Reels, Challenge, Review Reward, Leaderboard |
| AI / AR / Support | AR Try-On, AI Assistant, Chatbot, Support Ticket, Return / Refund |

---

## 2. Golden Rules

These rules are mandatory.

1. **Always inspect `res/` before coding.** Reuse existing colors, fonts, dimensions, styles, drawables, icons, and layouts.
2. **Never hardcode colors.** Use `@color/...` from `res/values/colors.xml`.
3. **Never hardcode typography.** Use existing Nunito fonts from `res/font/` and text styles from `styles.xml` or `themes.xml`.
4. **Never hardcode spacing if a dimension token exists.** Use `@dimen/...` from `dimens.xml`.
5. **Never create a new drawable if an existing reusable drawable fits the case.** Search `res/drawable/` first.
6. **Never create one-off UI if a Module 0 component already exists.** Reuse shared button, input, chip, badge, product card, bottom sheet, state view, or support component.
7. **Never use Jetpack Compose.** The Android frontend uses Java + XML.
8. **Never use Kotlin unless the project owner explicitly changes the stack.**
9. **Never put heavy business logic inside XML or Fragment.** Fragment handles UI binding and navigation. ViewModel, Repository, Helper, or Validator handles logic.
10. **Never skip UI states.** Any screen using API/data must support loading, success, empty, error, and no-internet states.
11. **Never remove support entry points from critical ecommerce screens.** Product Detail, Cart, Checkout, Order Detail, Return/Refund, AR Try-On, and Skin Journey must have chatbot/support access.
12. **Never modify files outside the task scope.** The agent must list touched files before coding.
13. **Never change shared files casually.** `colors.xml`, `dimens.xml`, `strings.xml`, `styles.xml`, `themes.xml`, `nav_graph.xml`, `build.gradle`, and `AndroidManifest.xml` are sensitive files.
14. **Never commit secrets.** Do not commit `.env`, `local.properties`, real API keys, tokens, `node_modules/`, or build folders.
15. **Never break naming convention.** File names, ids, style names, drawable names, class names, and API names must be meaningful and module-aware.

---

## 3. Resource-First Android Rule

Kanila already has UI resources configured in `res/`. The agent must treat `res/` as the source of truth.

### 3.1 Resource priority order

When implementing UI, choose resources in this order:

```text
1. Existing XML layout/component in res/layout/
2. Existing drawable background/icon in res/drawable/
3. Existing style in styles.xml/themes.xml
4. Existing color in colors.xml
5. Existing dimension in dimens.xml
6. Existing string in strings.xml
7. New resource only if no existing resource can be reused
```

### 3.2 How to inspect before coding

Use search patterns like:

```text
Search colors.xml for: primary, secondary, accent, background, text, border, success, warning, error, info
Search dimens.xml for: spacing, radius, margin, padding, height, icon, text
Search styles.xml for: Text, Button, Input, Chip, Badge, Card, BottomSheet
Search drawable/ for: bg_primary_button, bg_secondary_button, bg_input, bg_card, bg_search, bg_chip, bg_badge
Search layout/ for: item_product_card, item_cart, item_voucher, view_status_chip, view_empty_state
```

The agent should only add missing resources after confirming they do not already exist.

### 3.3 No duplicate resource rule

Do not create duplicate tokens such as:

```xml
<color name="primary_pink">#FFADBE</color>
<color name="primary">#FFADBE</color>
```

If `@color/primary` already exists, use it. Do not create `primaryPink`, `pink_primary`, `brand_pink`, or similar duplicates.

Do not create multiple XML declarations or multiple `<resources>` blocks in one resource file.

Correct:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary">#FFADBE</color>
</resources>
```

Incorrect:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>...</resources>
<?xml version="1.0" encoding="utf-8"?>
<resources>...</resources>
```

---

## 4. Design Tokens

Use existing tokens in `res/` whenever available. Do not hardcode these values in layout files.

### 4.1 Brand colors

Expected Kanila color roles:

| Role | Preferred resource name | Hex | Usage |
|---|---:|---:|---|
| Primary Pink | `@color/primary` | `#FFADBE` | Primary CTA, active icon, selected tab, main brand highlight |
| Secondary Pink | `@color/secondary` | `#FFD6DE` | Chip, tag, voucher background, selected soft background |
| Accent Dark Rose | `@color/accent_dark` | `#6B1E2E` | Product price, important heading, premium emphasis |
| Button Accent | `@color/button` | `#E83E72` | Strong CTA if already used by current theme |
| Background Main | `@color/background_main` | `#FFFFFF` | Main screen background |
| Background Soft | `@color/background_sub` | `#FDF9FB` | Soft card background, recommendation block |
| Text Main | `@color/text_main` | `#372B2B` | Main heading/body text |
| Border / Divider | `@color/border_divider` | `#D9D9D9` | Input border, divider, card border |
| Success | `@color/success` | `#2E9D62` | Success state, voucher applied, order success |
| Error | `@color/error` | `#D64545` | Error state, failed payment, invalid input |

If the project also defines expanded semantic tokens such as `text_secondary`, `text_tertiary`, `warning`, `info`, `border_soft`, `background_section`, or `background_disabled`, use those existing names instead of inventing new ones.

### 4.2 Typography

Primary font: **Nunito**.  
Optional secondary font for long Vietnamese content: **Be Vietnam Pro**, only if already configured in `res/font/`.

Expected typography roles:

| Style | Size | Weight | Usage |
|---|---:|---|---|
| H1 | 24sp | Bold | Onboarding title, campaign title, special screen title |
| H2 | 20sp | Bold / SemiBold | Screen title, product detail name, major section title |
| H3 | 16sp | SemiBold | Card title, button text, tab text, form group label |
| Body | 14sp | Regular | Main content, product description, review content |
| Body Medium | 14sp | Medium | Product card name, highlighted secondary info |
| Caption | 12sp | Regular / Medium | Order status, voucher condition, helper text |
| Micro | 10sp | Medium | Badge, reward label, AR tag |

Rules:

```text
- Product card name: max 2 lines, ellipsize if longer.
- Price text: use Accent Dark Rose or existing price style.
- CTA text: must be readable, never too thin.
- Small card: do not use more than 3 typography levels.
- Body text should usually be at least 14sp.
```

### 4.3 Spacing, radius, and size

Use existing `@dimen/...` tokens. If the project follows the Kanila standard, expected values are:

| Token role | Value | Usage |
|---|---:|---|
| Spacing XS | 4dp | Icon + micro text, badge content |
| Spacing S | 8dp | Label to input, chip inner spacing |
| Spacing M | 12dp | Small item gap, inner card content |
| Spacing L | 16dp | Screen margin, card padding, form spacing |
| Spacing XL | 24dp | Section gap |
| Spacing XXL | 32dp | Large content group gap |
| Radius S | 8dp | Badge, small chip |
| Radius M | 12dp | Input, small card |
| Radius L | 16dp | Product card, review card, voucher card |
| Radius Pill | 24dp | Button, search bar |
| Radius Circle | 999dp | Avatar, floating chatbot, round icon button |

Do not write raw `16dp`, `24dp`, `12dp`, etc. when equivalent `@dimen/...` exists.

---

## 5. Layout System

Default mobile frame target:

```text
390 x 844 px or 393 x 852 px
```

Android implementation rules:

```text
- Default horizontal screen margin: 16dp through @dimen.
- Touch target: minimum 44 x 44dp.
- Bottom navigation height: 64-72dp.
- Product listing: 2-column grid, 12dp gap if configured.
- Sticky CTA: use on Product Detail, Checkout, Return Request.
- Use bottom sheets for filter, sort, variant, voucher, address, shipping, support.
- Avoid large blocking popups unless confirmation or destructive action is needed.
```

Use `ConstraintLayout` for complex screens and `LinearLayout` for simple reusable items when appropriate. If the user explicitly asks for `LinearLayout`, use `LinearLayout`.

---

## 6. Core UI Component Rules

Module 0 is the design system and reusable component layer. Other modules must inherit from Module 0 instead of creating their own isolated UI.

### 6.1 Basic components

| Component | Expected implementation |
|---|---|
| Primary Button | Use existing primary button background/style; height around 48dp; pill radius; white text |
| Secondary Button | White background, primary border, dark rose text |
| Ghost Button | Transparent background, text-only or icon + text |
| Input Field | 48dp height, 12dp radius, border default/focus/error states |
| Search Bar | 44-48dp height, pill radius, soft background, search icon, optional voice/camera/QR |
| Chip | 32-36dp height, pill radius, selected/default/disabled states |
| Badge | 20-24dp height, pill radius, micro text |
| Icon Button | Minimum 44dp touch target, icon usually 24dp |

### 6.2 Commerce components

Reuse existing layouts when available:

```text
item_product_card.xml
item_cart.xml
item_voucher.xml
item_order.xml
view_price_summary.xml
view_status_chip.xml
view_product_sticky_cta.xml
```

Rules:

```text
- Product image is always visually dominant.
- Product card image ratio should be 1:1.
- Product name max 2 lines.
- Price must be visually stronger than secondary metadata.
- Rating and review count should be clear but not overpower price.
- Out-of-stock products must be visually clear and not look selectable as normal.
- Voucher disabled state must explain why it cannot be applied.
- Order status must use user-friendly language.
```

### 6.3 Community components

Reuse existing layouts when available:

```text
item_post.xml
item_review.xml
item_comment.xml
view_reels_product_tag.xml
view_reels_product_mini_card.xml
item_reward.xml
item_challenge.xml
```

Rules:

```text
- Product tag must not cover important video/content area.
- Community post card must show avatar, user name, post type, media, product tag, and reaction bar.
- Review content must clearly show verified purchase if available.
- Reward status must be visible: pending, earned, used, expired.
- Challenge card must show time, reward, participant count, and CTA.
```

### 6.4 Chatbot and support components

Reuse existing layouts when available:

```text
view_floating_chatbot.xml
bottom_sheet_chatbot_quick_menu.xml
item_chat_user.xml
item_chat_bot.xml
item_chat_agent.xml
item_ticket.xml
item_faq.xml
view_support_status_chip.xml
```

Rules:

```text
- Floating chatbot button: 56x56dp, bottom-right, primary brand color.
- Chatbot should never open as an empty screen; show quick action chips.
- User bubble aligns right; bot/agent bubble aligns left.
- Ticket card must show ticket code, issue type, status, last update, and CTA.
- Human handoff must preserve chat context.
```

### 6.5 AR and Skin Journey components

Reuse existing layouts when available:

```text
fragment_ar_permission.xml
fragment_ar_face_guide.xml
fragment_ar_try_on_camera.xml
view_ar_before_after.xml
view_ar_product_mini_card.xml
item_ar_color.xml
fragment_skin_journey_intro.xml
item_journey_timeline.xml
item_routine_step.xml
```

Rules:

```text
- Camera permission must explain why camera is needed.
- AR photo/snapshot is private by default.
- If camera permission is denied, show swatch/review alternative.
- Face guide text must be short and must not cover the main face area.
- Skin Journey photos are private unless user explicitly posts them.
- Irritation alert must recommend stopping use and contacting support/beauty advisor; do not provide medical diagnosis.
```

---

## 7. Form and Input Rules

Forms must reduce user effort.

```text
- Use label above input.
- Label and input gap: 6-8dp through @dimen if available.
- Field-to-field gap: 16dp through @dimen if available.
- Validate on blur or submit.
- Error text appears below input, uses @color/error and caption style.
- Required fields must be clear.
- Long forms should use sticky CTA at the bottom.
- Prefer chips, selectors, bottom sheets, saved values, and suggestions over manual typing.
```

Good error messages:

| Bad | Good |
|---|---|
| Error. | Phone number must contain 10 digits. |
| Invalid. | This address is not supported for delivery yet. |
| Voucher error. | This voucher has expired. |
| Payment failed. | Payment was not successful. You can try again or choose another method. |

For text input components:

```text
- Use flexible hint text through @string/... when reusable.
- Support states: default, focused, filled, error, success, disabled.
- Do not hardcode hint strings in XML if strings.xml already has or should store them.
- Password input must support visible/hidden state if used in auth.
- Search input should support optional icons: search, voice, camera, QR.
```

---

## 8. State System

Every screen with dynamic data must handle these states:

| State | Required behavior |
|---|---|
| Loading | Skeleton/loading view; avoid blank screen |
| Success | Render normal content |
| Empty | Explain what is empty and give a useful CTA |
| Error | Explain the issue and provide retry/support action |
| No Internet | Show retry and cached content if available |
| Permission Denied | Explain permission value and provide settings/alternative path |

Reusable state layouts should be used when available:

```text
view_empty_state.xml
view_error_state.xml
view_loading_state.xml
view_no_internet.xml
```

State examples:

| Screen | Empty CTA |
|---|---|
| Cart | Continue shopping |
| Wishlist | Discover products |
| Order list | Start shopping |
| Support ticket | Create ticket |
| Search result | Ask Kanila AI / Clear filters |
| Community | Create first post / Explore challenge |

---

## 9. Accessibility Rules

```text
- Minimum touch target: 44 x 44dp.
- Body text should be at least 14sp.
- Text/background contrast must be readable.
- Error states must use text or icon, not color only.
- Important icons must have contentDescription.
- Disabled state must be recognizable but not unreadable.
- CTA must not overlap bottom safe area or bottom navigation.
- Do not rely on color alone for order/ticket/payment status.
```

---

## 10. Motion and Interaction Rules

Use subtle interactions only.

| Interaction | Standard |
|---|---|
| Screen navigation | Slide left/right for main navigation |
| Bottom sheet | Slide up |
| Popup | Fade + light scale |
| Loading | Skeleton shimmer if available |
| Button pressed | Slight opacity/background state change |
| Wishlist | Small heart animation |
| Add to cart | Toast: `Added to cart` or localized string |
| Chatbot | Typing indicator |
| Community like | Small animation, not too flashy |

Toast rules:

```text
- Position near bottom but do not cover bottom navigation.
- Duration: 2-3 seconds.
- Use existing ToastHelper if available.
```

---

## 11. Android Architecture Rules

### 11.1 Technology

```text
Language: Java
UI: XML layouts
IDE: Android Studio
Architecture: MVVM-style separation
Networking: Retrofit or current project API stack if already configured
Image loading: use existing dependency; do not add Glide/Picasso/Coil unless approved
Navigation: use existing navigation structure if configured
```

### 11.2 Suggested package structure

```text
app/src/main/java/com/kanila/app/
├── common/
│   ├── base/
│   ├── helper/
│   ├── ui/
│   └── util/
├── data/
│   ├── api/
│   ├── model/
│   └── repository/
├── ui/
│   ├── module0_component/
│   ├── auth/
│   ├── beauty_profile/
│   ├── home/
│   ├── category/
│   ├── product/
│   ├── commerce/
│   ├── order/
│   ├── community/
│   ├── ar/
│   ├── support/
│   └── account/
└── MainActivity.java
```

### 11.3 Fragment rules

Fragments should:

```text
- Inflate/bind XML layout.
- Initialize RecyclerView adapters.
- Observe ViewModel state.
- Handle simple click/navigation events.
- Delegate validation/business rules to ViewModel/Helper/Validator.
```

Fragments should not:

```text
- Contain large API logic.
- Build complex business objects inline.
- Hardcode fake data permanently.
- Directly manipulate unrelated module state.
```

### 11.4 ViewModel rules

ViewModels should:

```text
- Hold UI state.
- Validate form input through Validator/Helper when needed.
- Call Repository.
- Expose state to Fragment.
- Keep UI-independent logic.
```

### 11.5 Adapter rules

RecyclerView adapters should:

```text
- Use ViewHolder pattern.
- Keep binding logic clean.
- Accept click listeners from Fragment.
- Not call API directly.
- Not own navigation logic.
```

---

## 12. Android Naming Convention

### 12.1 Java classes

| Type | Pattern | Example |
|---|---|---|
| Fragment | `{Feature}Fragment` | `LoginFragment`, `ProductDetailFragment` |
| ViewModel | `{Feature}ViewModel` | `CartViewModel` |
| UI State | `{Feature}UiState` | `CheckoutUiState` |
| Adapter | `{Item}Adapter` | `ProductGridAdapter` |
| Bottom Sheet | `{Purpose}BottomSheet` or `{Purpose}BottomSheetDialog` | `FilterBottomSheetDialog` |
| Helper | `{Purpose}Helper` | `ToastHelper` |
| Validator | `{Feature}Validator` | `CheckoutValidator` |
| Repository | `{Domain}Repository` | `ProductRepository` |

### 12.2 XML layout files

| Type | Pattern | Example |
|---|---|---|
| Fragment layout | `fragment_{feature}.xml` | `fragment_login.xml` |
| RecyclerView item | `item_{entity}.xml` | `item_product_card.xml` |
| Reusable view | `view_{component}.xml` | `view_status_chip.xml` |
| Bottom sheet | `bottom_sheet_{purpose}.xml` | `bottom_sheet_filter.xml` |
| Dialog | `dialog_{purpose}.xml` | `dialog_confirm.xml` |

### 12.3 Drawable files

| Type | Pattern | Example |
|---|---|---|
| Background | `bg_{component}_{state}.xml` | `bg_input_error.xml` |
| Icon | `ic_{name}.xml` | `ic_cart.xml` |
| Selector | `selector_{component}.xml` | `selector_primary_button.xml` |
| Shape | `shape_{purpose}.xml` | `shape_bottom_sheet_handle.xml` |

### 12.4 View IDs

Use clear camelCase ids:

```xml
android:id="@+id/tvProductName"
android:id="@+id/tvProductPrice"
android:id="@+id/ivProductImage"
android:id="@+id/btnAddToCart"
android:id="@+id/rvProductList"
android:id="@+id/chipSkinType"
```

Recommended prefixes:

| Prefix | View |
|---|---|
| `tv` | TextView |
| `iv` | ImageView |
| `btn` | Button |
| `edt` | EditText |
| `rv` | RecyclerView |
| `cb` | CheckBox |
| `rb` | RadioButton |
| `chip` | Chip-like view |
| `layout` | Container layout |
| `toolbar` | Toolbar |

---

## 13. Module Scope

### Module 0 — Design System & Components

Purpose: shared design tokens and reusable UI.

Typical files:

```text
res/values/colors.xml
res/values/dimens.xml
res/values/styles.xml
res/values/themes.xml
res/values/strings.xml
res/font/
res/drawable/bg_*.xml
res/layout/item_product_card.xml
res/layout/item_cart.xml
res/layout/item_voucher.xml
res/layout/item_order.xml
res/layout/view_status_chip.xml
res/layout/view_empty_state.xml
res/layout/view_error_state.xml
res/layout/view_loading_state.xml
res/layout/view_floating_chatbot.xml
```

Rule: Module 0 is the foundation. Other modules inherit from it.

### Module 1 — Authentication & Onboarding

Typical screens:

```text
Splash
Onboarding
Welcome
Login
Register
OTP Verification
Forgot Password
Reset Password
Guest Mode Prompt
```

UX rules:

```text
- Allow guest browsing.
- Reduce typing.
- Show clear validation.
- Social login can be shown if supported by backend.
- Wrong password/OTP errors must be specific but secure.
```

### Module 2 — Beauty Profile

Typical screens:

```text
Beauty Profile Intro
Skin Type
Skin Concern
Skin Tone
Beauty Budget
Avoid Ingredients
Favorite Brand
Completed
```

UX rules:

```text
- Use chips instead of long forms.
- Allow skip when appropriate.
- Explain unfamiliar beauty terms.
- Store choices for personalization.
```

### Module 3 — Home & Discovery

Typical screens:

```text
Home
Guest Home
Search
Search Result
Voice Search
Image Search
QR / Barcode Scan
Search No Result
```

UX rules:

```text
- Home should show personalized content when Beauty Profile exists.
- Search should support product, brand, ingredient, voice, image, QR/barcode where planned.
- No-result state must suggest alternatives and chatbot help.
```

### Module 4 — Category & Product Detail

Typical screens:

```text
Category Home
Brand Listing
Product Listing
Filter Bottom Sheet
Sort Bottom Sheet
Product Detail
Out of Stock
Variant Selector
Skin Match Score
Ingredient Checker
Review Summary
Sticky CTA
```

UX rules:

```text
- Product image is central.
- Filter must be bottom sheet.
- Default sort can be personalized if Beauty Profile exists.
- Product Detail should show support entry and AR entry if supported.
- Variant must be selected before add-to-cart.
```

### Module 5 — Commerce / Cart / Checkout

Typical screens:

```text
Cart
Empty Cart
Cart Price Changed
Cart Out Of Stock
Voucher Wallet
Checkout
Address Selector
Shipping Method
Payment Method
Payment Processing
Payment Failed
Order Success
```

UX rules:

```text
- Cart must show price, quantity, stock, voucher, and total clearly.
- Checkout should be one vertical flow with completed/uncompleted blocks.
- Payment failed must allow retry or method change.
- Voucher disabled state must explain why.
```

### Module 6 — Order / Return / Review

Typical screens:

```text
Order List
Order Detail
Order Timeline
Shipping Tracking
Cancel Order
Return Request
Select Return Items
Return Reason
Upload Evidence
Return Tracking
Refund Status
Review Form
Review Approved
```

UX rules:

```text
- Order timeline must be easy to understand.
- Only show valid actions for current order state.
- Return/refund must show request code and SLA.
- Review from purchased item should show verified purchase.
```

### Module 7 — Community & Reels

Typical screens:

```text
Community Home
Feed List
Post Detail
Comments
Report Post
Reels Feed
Create Post
Create Reels
Tag Product
Challenge
Leaderboard
Reward Center
```

UX rules:

```text
- Product tags must support commerce without blocking content.
- Content moderation/reporting must be visible.
- Reward eligibility must be transparent.
- User should see pending/approved/rejected status for posts/rewards.
```

### Module 8 — AR Try-On & Beauty Experience

Typical screens:

```text
AR Permission
AR Face Guide
AR Camera
AR No Permission
AR Face Not Detected
AR Save Look
Skin Journey Intro
Create Skin Journey
Log Skin Progress
Journey Timeline
Routine Builder
```

UX rules:

```text
- Ask camera permission with privacy explanation.
- Provide fallback if permission denied.
- Snapshot is private by default.
- Do not overclaim AR color accuracy; lighting and screen can affect color.
```

### Module 9 — Chatbot & Support

Typical screens:

```text
Floating Chatbot
Chatbot Quick Menu
Chat Conversation
Product Recommendation Result
Routine Recommendation
Ingredient Check Result
Order Tracking Result
Human Handoff
Support Center
Ticket List
Ticket Detail
Create Ticket
CSAT Survey
```

UX rules:

```text
- Chatbot must show quick menu chips.
- Order tracking must protect personal data.
- Ticket must show code, status, SLA, and timeline.
- Human handoff must summarize previous chat context.
```

### Module 10 — Account & Notification

Typical screens:

```text
Account Hub
Edit Profile
Beauty Profile Detail
Address Book
Wishlist
Loyalty Dashboard
Point History
Voucher Wallet
Notification Center
Notification Settings
Quiet Hours
Security Settings
Privacy Settings
```

UX rules:

```text
- Account Hub is the user control center.
- Notification categories should include order, promotion, community, routine, and support.
- Support/order notifications have higher priority than sale notifications.
- Privacy settings must cover Beauty Profile, Skin Journey, AR images, and chat/order data where relevant.
```

---

## 14. Ecommerce UX Rules

Kanila must behave like a serious ecommerce app, not only a static prototype.

### 14.1 Product listing

```text
- Show product image, brand, name, price, rating, review count, wishlist.
- Show sale/new/AR/out-of-stock badges when relevant.
- Keep filter and sort accessible.
- Preserve scroll position when returning from Product Detail.
- If filter returns too few products, suggest removing filters.
```

### 14.2 Product detail

```text
- Show image gallery first.
- Show product name, brand, price, rating, stock/variant.
- Use accordion for long content.
- Show Skin Match Score if Beauty Profile exists.
- Show Ingredient Checker when ingredient data exists.
- Show AI Review Summary if available.
- Sticky CTA must include Add to Cart / Buy Now / Try AR when supported.
- If product may not fit the profile, warn softly and suggest alternatives; do not block purchase.
```

### 14.3 Cart

```text
- Show product variant, quantity, price, stock status.
- Allow undo after delete if helper exists.
- Suggest best voucher; do not force user to try every code manually.
- Clearly separate out-of-stock or price-changed items.
```

### 14.4 Checkout

```text
- Use a single vertical checkout flow.
- Blocks: address, shipping, payment, voucher, price summary.
- Validate before placing order.
- Highlight exactly which block needs fixing.
- If payment fails, do not lose the order/session immediately.
```

### 14.5 Order / return / refund

```text
- Show order status timeline.
- Only show actions valid for current order state.
- Return/refund flow must collect reason and evidence.
- Show ticket/request code and expected response time.
- Refund status must explain whether money was not charged, charged and pending refund, or refunded.
```

---

## 15. Beauty Domain Rules

Kanila is a cosmetics app. Beauty-specific UX matters.

```text
- Always support skin type, skin concern, tone, undertone, budget, ingredient preferences where relevant.
- Do not make medical claims.
- Ingredient warnings should be phrased as beauty guidance, not diagnosis.
- If user reports irritation, recommend stopping use and contacting support/beauty advisor; avoid medical diagnosis.
- AR Try-On should include a disclaimer that color may vary due to lighting and screen.
- Reviews should be filterable by skin type, tone, age range, verified purchase, photo/video where possible.
```

---

## 16. Backend Rules

### 16.1 Technology

```text
Backend: Node.js
Framework: Express.js
Database: MongoDB Atlas
ODM: Mongoose
```

### 16.2 Suggested backend structure

```text
backend-nodejs/
├── server.js
├── package.json
├── .env.example
└── src/
    ├── app.js
    ├── config/
    ├── middleware/
    ├── modules/
    │   ├── auth/
    │   ├── users/
    │   ├── products/
    │   ├── categories/
    │   ├── cart/
    │   ├── checkout/
    │   ├── orders/
    │   ├── vouchers/
    │   ├── community/
    │   ├── support/
    │   ├── chatbot/
    │   ├── ar/
    │   ├── skinJourney/
    │   └── notifications/
    └── utils/
```

### 16.3 Backend module file pattern

For a backend module such as products:

```text
src/modules/products/product.model.js
src/modules/products/product.controller.js
src/modules/products/product.service.js
src/modules/products/product.routes.js
src/modules/products/product.validation.js
```

Rules:

```text
- Controller handles request/response only.
- Service handles business logic.
- Model defines Mongoose schema.
- Validation file handles request validation.
- Routes file maps endpoints.
- Do not expose raw errors to client.
- Do not change API response format without informing frontend.
```

### 16.4 Backend collection direction

Kanila may need collections for:

```text
community_posts
community_comments
community_reactions
community_reports
community_challenges
community_rewards
ar_supported_products
ar_assets
ar_tryon_sessions
ar_tryon_events
chatbot_sessions
chatbot_messages
knowledge_base_articles
support_tickets
support_ticket_events
skin_profiles
skin_journeys
skin_journey_logs
customer_routines
routine_reminders
notifications
push_tokens
notification_preferences
```

Do not implement all collections unless the task asks for them. Add only what the current feature needs.

---

## 17. API Contract Rules

Frontend and backend must agree on contracts.

Standard response shape should be consistent if the project already has one. If not defined yet, prefer:

```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "error": null
}
```

Error example:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "fields": {
      "phone": "Phone number must contain 10 digits"
    }
  }
}
```

Rules:

```text
- Backend must not return inconsistent field names for the same entity.
- Frontend must not assume fields that backend does not provide.
- If frontend needs a field, document it before coding around fake assumptions.
- API changes must be documented in README or API docs.
```

---

## 18. Git and Collaboration Rules

### 18.1 Branches

```text
main       = stable submission branch
develop    = integration branch
feature/*  = feature work branch
fix/*      = bug fix branch
```

Examples:

```text
feature/KAN-01-login-screen
feature/KAN-03-product-listing
feature/KAN-05-cart-api
fix/KAN-07-payment-error-state
```

Rules:

```text
- Do not push directly to main.
- Prefer not to push directly to develop.
- Create Pull Request from feature branch to develop.
- At least one reviewer should review before merge.
- Main must always be runnable.
```

### 18.2 Commit message format

```text
type: short description
```

Allowed types:

```text
feat: new feature
fix: bug fix
ui: UI update
refactor: code change without behavior change
docs: documentation
config: configuration/dependency
style: formatting only
test: test code
```

Good examples:

```text
feat: create login screen
ui: update product card component
fix: handle empty cart state
config: add retrofit dependency
docs: update setup guide
```

Bad examples:

```text
update
fix bug
abc
test
final version
```

### 18.3 Sensitive files

Only modify these when necessary and after checking project impact:

```text
mobile-android/KanilaApp/app/build.gradle
mobile-android/KanilaApp/build.gradle
mobile-android/KanilaApp/settings.gradle
mobile-android/KanilaApp/app/src/main/AndroidManifest.xml
mobile-android/KanilaApp/app/src/main/res/navigation/nav_graph.xml
mobile-android/KanilaApp/app/src/main/res/values/colors.xml
mobile-android/KanilaApp/app/src/main/res/values/dimens.xml
mobile-android/KanilaApp/app/src/main/res/values/strings.xml
mobile-android/KanilaApp/app/src/main/res/values/styles.xml
mobile-android/KanilaApp/app/src/main/res/values/themes.xml
backend-nodejs/package.json
backend-nodejs/package-lock.json
backend-nodejs/server.js
backend-nodejs/src/app.js
backend-nodejs/.env.example
```

---

## 19. Pull Request Checklist

Every PR should include:

```markdown
## Summary
- What was implemented

## Main files changed
- List key files

## How to test
1. Step 1
2. Step 2
3. Expected result

## Screenshots / video
- Required for frontend UI changes

## API changes
- Required for backend/API changes

## Notes
- Known limitations or unfinished items
```

Do not merge if:

```text
- Android project does not build.
- Backend does not start.
- Layout ignores design system.
- UI uses hardcoded colors/font/spacing despite existing resources.
- Feature skips loading/empty/error states.
- PR modifies unrelated files.
```

---

## 20. File Scope Declaration Template for AI Agents

Before generating code, the agent should output a short scope declaration:

```markdown
## Files to create/modify

### Read first
- app/src/main/res/values/colors.xml
- app/src/main/res/values/dimens.xml
- app/src/main/res/values/styles.xml
- app/src/main/res/values/themes.xml
- app/src/main/res/font/
- app/src/main/res/drawable/
- app/src/main/res/layout/

### Create
- app/src/main/res/layout/item_example.xml
- app/src/main/java/com/kanila/app/ui/example/ExampleAdapter.java

### Modify
- app/src/main/res/values/strings.xml

### Do not touch
- build.gradle
- AndroidManifest.xml
- nav_graph.xml
```

If a task only asks for UI XML, do not generate backend files. If a task only asks for backend API, do not modify Android resources.

---

## 21. Android Studio AI Prompt Template

Use this when asking Android Studio AI or another coding agent to generate code:

```text
Read KANILA_AGENT_RULES.md first.
Before coding, inspect the existing Android res/ folder: colors.xml, dimens.xml, strings.xml, styles.xml, themes.xml, res/font, res/drawable, and res/layout.
Reuse existing resources, drawables, styles, fonts, spacing tokens, and Module 0 components whenever possible.
Do not hardcode colors, fonts, dimensions, strings, or component styles.
Do not recreate resources that already exist.
Use Android Java + XML only, not Compose and not Kotlin.
Limit the scope to the files listed below.
Generate clean, reusable, ecommerce-grade UI that follows Kanila design system.
```

Then add task-specific requirements and file list.

---

## 22. Component Priority

| Priority | Components |
|---|---|
| P0 | Color System, Typography, Button, Input, Search Bar, Product Card, Bottom Navigation, Header, Bottom Sheet, Product Detail CTA Bar, Cart Item, Voucher Card, Checkout Summary, Order Card, Status Chip, Floating Chatbot Button, Empty/Error/Loading State |
| P1 | Review Card, Product Mini Card, Order Timeline, Ticket Card, Chat Bubble, Community Post Card, Reels Product Tag, Filter Component, Variant Selector, Skin Match Score Card, Ingredient Checker Card, Notification Card |
| P2 | AR Face Guide, AR Color Selector, AR Before/After, Skin Journey Timeline, Routine Builder Card, Reward Card, Leaderboard Item, Challenge Card, AI Recommendation Card, Privacy Consent Card |

---

## 23. Final Quality Checklist

Before finishing any task, verify:

```text
[ ] The agent read KANILA_AGENT_RULES.md.
[ ] The agent inspected existing res/ resources before coding.
[ ] No hardcoded colors were added.
[ ] No hardcoded fonts were added.
[ ] No avoidable hardcoded dimensions were added.
[ ] Existing Module 0 components were reused where possible.
[ ] New resource names are clear and non-duplicated.
[ ] UI follows Clean Beauty / Soft Pink / Rounded Card / Product-first style.
[ ] Product image and CTA hierarchy are clear in commerce screens.
[ ] Loading, empty, error, and no-internet states exist where needed.
[ ] Input fields have clear labels, hints, and validation states.
[ ] Critical screens have chatbot/support entry where relevant.
[ ] Accessibility basics are respected.
[ ] Java code is separated into Fragment, ViewModel, Repository, Adapter, Helper as appropriate.
[ ] Backend code is separated into model, controller, service, routes, validation as appropriate.
[ ] The task did not modify unrelated files.
[ ] Sensitive files were not changed without reason.
[ ] No secrets or local build files were added.
[ ] The code can be built/run after changes.
```

---

## 24. Absolute Do-Not-Do List

```text
Do not hardcode #FFADBE, #FFD6DE, #6B1E2E, #372B2B, #D9D9D9, or any other color in layout code.
Do not write android:fontFamily="sans-serif" when Nunito exists in res/font.
Do not create a new pink button background if bg_primary_button.xml already exists.
Do not create a new input style if input styles already exist.
Do not create duplicate colors or duplicate resources.
Do not add a second <resources> block to the same XML resource file.
Do not generate Compose code.
Do not generate Kotlin code.
Do not change backend API response without documenting it.
Do not remove privacy notice from AR or Skin Journey.
Do not remove support entry from Product Detail, Cart, Checkout, Order Detail, Return/Refund, or AR.
Do not make medical claims about skincare results.
Do not commit build outputs or environment secrets.
```

---

## 25. Short Version for Agents

When in doubt, follow this rule:

> **Read existing `res/` first. Reuse before creating. Inherit Module 0 before designing new UI. Use Java + XML. Keep Kanila soft, clean, rounded, product-first, and ecommerce-ready.**
