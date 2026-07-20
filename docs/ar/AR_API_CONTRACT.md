# AR API Contract

## 1. GET AR Config
**Endpoint**: `GET /api/products/:productId/ar-config`

**Response format**:
```json
{
  "status": "READY | NOT_SUPPORTED | DISABLED",
  "product_id": "...",
  "ar_type": "LIP",
  "renderer_version": "lip_v1",
  "disclaimer": "Màu thực tế có thể thay đổi tùy ánh sáng, camera, màu môi tự nhiên và màn hình.",
  "variants": [
    {
      "variant_id": "...",
      "sku": "...",
      "variant_name": "...",
      "shade_hex": "#A74742",
      "finish_type": "MATTE",
      "opacity": 0.58,
      "price": 299000,
      "currency_code": "VND",
      "in_stock": true,
      "thumbnail_url": "..."
    }
  ]
}
```
**Rules**:
- `404 Not Found` if product does not exist.
- `400 Bad Request` if productId is not a valid ObjectId.
- HTTP `200` with `status: "NOT_SUPPORTED"` and `variants: []` if product exists but has no AR variants or AR is disabled.
- Returns only active variants.
- Pricing and inventory data are populated from their respective services.

## 2. POST Batch AR Events
**Endpoint**: `POST /api/ar/events/batch`

**Body**:
```json
{
  "session_uuid": "uuid",
  "product_id": "objectId",
  "events": [
    {
      "event_type": "SHADE_SELECTED",
      "variant_id": "objectId",
      "occurred_at": "ISO date",
      "metadata": {
        "source_screen": "PRODUCT_DETAIL"
      }
    }
  ]
}
```
**Rules**:
- Max 50 events per batch.
- Validates UUID, ObjectId, event_type enum, and ISO date.
- Strips object keys starting with `$`.
- Whitelisted metadata keys only, depth limit 2.
- Partial successes allowed.
- Response includes `accepted_count` and `rejected_count`.
- Uses auth context for customer identification.

## 3. ProductVariant Schema Extension (Database)
Added fields for AR logic:
```json
{
  "ar_config": {
    "enabled": true,
    "type": "LIP",
    "shade_hex": "#A74742",
    "finish_type": "MATTE",
    "opacity": 0.58,
    "renderer_version": "lip_v1",
    "calibration_status": "VERIFIED"
  }
}
```
