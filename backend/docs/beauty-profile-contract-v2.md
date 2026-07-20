# Canonical Beauty Profile Contract (v2)

This document defines the synchronized contract between the Kanila Android frontend, Backend API, Recommendation Engine, and MongoDB persistence layer. It resolves previous ambiguities regarding field names, nullability, array serialization, and legacy aliases.

## 1. Field Mapping Contract

| JSON Field / SerializedName | Backend Mongoose Field | Android Java Field | Type | Nullable | Multi-Select | Reference Group | User Editable | PATCH Semantics |
|-----------------------------|------------------------|--------------------|------|----------|--------------|-----------------|---------------|-----------------|
| `skin_type` | `skin_type` | `skinType` | String | Yes | No | `skin_type` | Yes | omitted=preserve, null=clear |
| `sensitivity_level` | `sensitivity_level` | `sensitivityLevel` | String | Yes | No | `sensitivity_level` | Yes | omitted=preserve, null=clear |
| `skin_color` | `skin_color` | `skinColor` | String | Yes | No | `skin_tone` | Yes | omitted=preserve, null=clear |
| `skin_undertone` | `skin_undertone` | `skinUndertone` | String | Yes | No | `undertone` | Yes | omitted=preserve, null=clear |
| `foundation_finish` | `foundation_finish` | `foundationFinish` | String | Yes | No | `finish_preference` | Yes | omitted=preserve, null=clear |
| `budget` | `budget` | `budget` | String | Yes | No | `budget_range` | Yes | omitted=preserve, null=clear |
| `fragrance_preference` | `fragrance_preference` | `fragrancePreference`| String | Yes | No | `fragrance_preference` | Yes | omitted=preserve, null=clear |
| `skin_concerns` | `skin_concerns` | `skinConcerns` | String[] | No | Yes | `skin_concern` | Yes | omitted=preserve, []=clear |
| `lipstick_colors` | `lipstick_colors` | `lipstickColors` | String[] | No | Yes | `lip_color_preference` | Yes | omitted=preserve, []=clear |
| `makeup_styles` | `makeup_styles` | `makeupStyles` | String[] | No | Yes | `makeup_style` | Yes | omitted=preserve, []=clear |
| `avoid_ingredients` | `avoid_ingredients` | `avoidIngredients` | String[] | No | Yes | `avoid_ingredient` | Yes | omitted=preserve, []=clear |
| `beauty_goals` | `beauty_goals` | `beautyGoals` | String[] | No | Yes | `beauty_goal` | Yes | omitted=preserve, []=clear |
| `preferred_ingredients` | `preferred_ingredients` | `preferredIngredients`| String[] | No | Yes | `preferred_ingredient` | Yes | omitted=preserve, []=clear |
| `preferred_brands` | `preferred_brands` | `preferredBrands` | String[] | No | Yes | *(Brands DB)* | Yes | omitted=preserve, []=clear |
| `disliked_brands` | `disliked_brands` | `dislikedBrands` | String[] | No | Yes | *(Brands DB)* | Yes | omitted=preserve, []=clear |
| `preferred_categories`| `preferred_categories` | `preferredCategories`| String[] | No | Yes | *(Categories)* | Yes | omitted=preserve, []=clear |
| `texture_preference` | `texture_preference` | `texturePreference` | String[] | No | Yes | `texture_preference` | Yes | omitted=preserve, []=clear |
| `purchase_intent` | `purchase_intent` | `purchaseIntent` | String[] | No | Yes | `purchase_intent` | Yes | omitted=preserve, []=clear |
| `profile_completion_rate`| `profile_completion_rate`| `profileCompletionRate`| Number | No | No | N/A | No | Read-only |
| `profile_hash` | `profile_hash` | `profileHash` | String | No | No | N/A | No | Read-only |
| `source` | `source` | `source` | String | No | No | N/A | No | Read-only |

## 2. Active Beauty Reference Codes (Source of Truth)

The backend and frontend must use the exact string codes defined below. The backend validates against these codes.

- **`skin_type`**: `oily`, `dry`, `combination`, `normal`, `sensitive`, `unknown`
- **`skin_concern`**: `acne`, `dark_spots`, `melasma`, `dullness`, `large_pores`, `blackheads`, `redness`, `dehydrated`, `wrinkles`, `uneven_texture`, `damaged_barrier`, `sun_damage`
- **`sensitivity_level`**: `low`, `medium`, `high`, `reactive`, `unknown`
- **`skin_tone`**: `fair`, `light`, `medium`, `tan`, `deep`, `unknown` *(Maps to `skin_color` field)*
- **`undertone`**: `cool`, `warm`, `neutral`, `olive`, `unknown` *(Maps to `skin_undertone` field)*
- **`finish_preference`**: `matte`, `dewy`, `natural`, `glowy` *(Maps to `foundation_finish` field)*
- **`lip_color_preference`**: `nude`, `pink`, `coral`, `red`, `brown`, `mlbb`, `bold` *(Maps to `lipstick_colors` field)*
- **`makeup_style`**: `natural`, `korean`, `glam`, `office`, `party`, `daily` *(Maps to `makeup_styles` field)*
- **`budget_range`**: `under_200k`, `200_500k`, `500_1000k`, `premium` *(Maps to `budget` field)*
- **`avoid_ingredient`**: `fragrance`, `alcohol_denat`, `essential_oil`, `paraben`, `mineral_oil`, `silicone`, `sulfate`, `lanolin`, `retinoid`, `aha_bha_high`
- **`beauty_goal`**: `hydration`, `brightening`, `acne_care`, `oil_control`, `barrier_repair`, `anti_aging`, `pore_care`, `soothing`, `sun_protection`, `even_tone`
- **`preferred_ingredient`**: `niacinamide`, `hyaluronic_acid`, `ceramide`, `centella`, `panthenol`, `vitamin_c`, `bha`, `aha`, `retinol`, `peptide`, `zinc_pca`, `tranexamic_acid`
- **`texture_preference`**: `gel`, `cream`, `lotion`, `serum`, `oil`, `balm`
- **`fragrance_preference`**: `fragrance_free`, `light_fragrance`, `no_preference`
- **`purchase_intent`**: `daily_use`, `treatment`, `gift`, `try_new`, `repurchase`

## 3. PATCH Update Semantics

- **Omitted fields**: Treated as "do not change" and will retain their current database value.
- **Explicit `null`**: Used to clear a nullable single-select field. **(Requires Android Gson serialization fix)**.
- **Explicit `[]` (empty array)**: Used to explicitly clear all selections from a multi-select field.

## 4. Frontend Release Blockers & Status

- 🚨 **Gson Null Serialization**: Android `ApiClient.java` currently uses default `GsonConverterFactory.create()`. This configuration *drops null values from the JSON string*. Consequently, the Android app currently cannot explicitly clear a single-select field (like `budget`) by setting it to `null`, because Retrofit won't send the `null` over the network. **Resolution needed**: The Android team must enable `.serializeNulls()` on the Gson instance used for `UpdateBeautyProfileRequest`, or use a custom serializer.
- ✅ **DTO Validation**: Verified `CustomerBeautyProfileDto.java` and `UpdateBeautyProfileRequest.java` use correct `@SerializedName` fields matching the table above.
- ✅ **Recommendation Response Compatibility**: Verified `RecommendationData.java` matches the proposed nested structure (`recommendation_type`, `profile_source`, `from_snapshot`, `algorithm_version`, `snapshot_generated_at`, `products` list containing `RecommendedProduct.java`).
