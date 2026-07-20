# VERTEX AI SETUP — Kanila Backend

> **Ngày tạo**: 2026-07-17  
> **Phiên bản**: 1.0.0  
> **Áp dụng cho**: Kanila Node.js Backend (CommonJS, Express 4.x)

---

## 1. Kiến trúc

```
Kanila Android App
    │
    │  HTTPS  (Android không giữ API key)
    ▼
Kanila Node.js Backend
    │
    │  @google/genai SDK  (vertexai: true)
    ▼
Google Vertex AI
    │  Xác thực bằng VERTEX_API_KEY
    ▼
Gemini Model (VERTEX_GEMINI_MODEL)
```

**Tuyệt đối không:**
- Đặt API key trong Android (strings.xml, BuildConfig, assets)
- Gọi Gemini trực tiếp từ Android
- Dùng Google AI Studio / Gemini Developer API
- Dùng ADC (Application Default Credentials)
- Dùng service-account JSON
- Dùng Google Cloud CLI

---

## 2. Vì sao dùng Vertex AI API Key

| Phương thức | Phù hợp | Kanila |
|-------------|---------|--------|
| `apiKey` + `vertexai: true` | API key tĩnh, không cần Cloud CLI | ✅ **Đang dùng** |
| ADC | Môi trường Google Cloud có metadata server | ❌ Không dùng |
| Service Account JSON | CI/CD với file credential | ❌ Không dùng |

Với `vertexai: true`, SDK tự định tuyến request đến **Vertex AI endpoint**, không phải Google AI Studio.

---

## 3. Hai biến môi trường bắt buộc

| Biến | Mô tả | Ví dụ |
|------|-------|-------|
| `VERTEX_API_KEY` | API key từ Google Cloud Console | _(không hiển thị)_ |
| `VERTEX_GEMINI_MODEL` | Tên model Gemini trên Vertex AI | `gemini-2.5-flash` |

Thêm vào file `.env` (không commit):
```
VERTEX_API_KEY=your_key_here
VERTEX_GEMINI_MODEL=gemini-2.5-flash
```

**Không dùng:**
- `GEMINI_API_KEY` — Google AI Studio key, không phải Vertex AI
- `GOOGLE_API_KEY` — không đúng scope
- `GOOGLE_APPLICATION_CREDENTIALS` — ADC, không phải API key

---

## 4. Cài đặt dependency

Package đã có trong `package.json`:

```bash
npm install
```

Nếu cần cài thêm (trường hợp fresh clone):

```bash
npm install @google/genai
```

---

## 5. Chạy verify cấu hình (không gọi mạng)

```bash
npm run verify:vertex
```

Output khi thành công:
```
[PASS] VERTEX_API_KEY is configured
[PASS] VERTEX_GEMINI_MODEL is configured: gemini-2.5-flash
[PASS] @google/genai is available and exports GoogleGenAI
[PASS] Vertex client initialized (vertexai: true, apiKey: VERTEX_API_KEY)
[PASS] No forbidden Gemini environment variables detected

[OK] All Vertex AI configuration checks passed.
```

---

## 6. Chạy unit test (không gọi mạng, không cần key thật)

```bash
npm run test:vertex:unit
```

Hoặc toàn bộ test suite:
```bash
npm test
```

---

## 7. Chạy live test (một request thật đến Vertex AI)

> ⚠️ Yêu cầu `VERTEX_API_KEY` và `VERTEX_GEMINI_MODEL` hợp lệ.  
> ⚠️ Tiêu tốn một lượng nhỏ Vertex AI quota.  
> Chỉ chạy khi cần xác minh integration thật.

```bash
npm run test:vertex:live
```

---

## 8. Xem sanitized error

Backend trả các error code chuẩn, không bao gồm API key hay raw body:

| Code | Ý nghĩa |
|------|---------|
| `VERTEX_AUTHENTICATION_FAILED` | Key không hợp lệ hoặc hết hạn |
| `VERTEX_PERMISSION_DENIED` | API bị hạn chế hoặc billing vấn đề |
| `VERTEX_MODEL_NOT_FOUND` | Model không tồn tại trên Vertex AI |
| `VERTEX_RATE_LIMITED` | Vượt quota |
| `VERTEX_TIMEOUT` | Request quá thời gian chờ |
| `VERTEX_UPSTREAM_ERROR` | Lỗi từ phía Google |
| `VERTEX_EMPTY_RESPONSE` | Model trả về rỗng |
| `VERTEX_INVALID_JSON_RESPONSE` | JSON parse thất bại |

---

## 9. Deploy lên Render / Railway / VPS

**Render:**
1. Dashboard → Service → Environment → Add Secret File hoặc Environment Variables
2. Thêm `VERTEX_API_KEY` và `VERTEX_GEMINI_MODEL`
3. Không commit `.env` lên git

**Railway:**
1. Dashboard → Project → Variables
2. Thêm hai biến trên
3. Deploy sẽ tự nhận

**VPS / Ubuntu:**
```bash
# Tạo .env trên server (không version control)
nano /srv/kanila/.env
# Thêm VERTEX_API_KEY và VERTEX_GEMINI_MODEL

# Start service
pm2 restart kanila-backend
```

---

## 10. Restart service sau khi đổi env

```bash
# PM2
pm2 restart kanila-backend --update-env

# Docker
docker restart kanila-backend

# Render/Railway: push deploy mới hoặc manual redeploy
```

---

## 11. Xử lý lỗi

### 401 — VERTEX_AUTHENTICATION_FAILED
- Kiểm tra `VERTEX_API_KEY` bị copy thiếu (khoảng trắng đầu/cuối)
- Key có thể đã bị xóa hoặc vô hiệu hóa trong Google Cloud Console
- Xem: **Google Cloud Console → APIs & Services → Credentials**

### 403 — VERTEX_PERMISSION_DENIED
- Vertex AI API chưa được bật: **APIs & Services → Enable APIs → Vertex AI API**
- API key có restrictions chưa bao gồm Vertex AI
- Billing chưa được kích hoạt trên project

### 404 — VERTEX_MODEL_NOT_FOUND
- Kiểm tra `VERTEX_GEMINI_MODEL` trong `.env`
- Mô hình phải tồn tại trên Vertex AI (không phải tên model của Google AI Studio)
- Xem danh sách model hợp lệ: **Google Cloud Console → Vertex AI → Model Garden**
- **Không tự thay đổi `VERTEX_GEMINI_MODEL`** — báo cáo cho project owner

### 429 — VERTEX_RATE_LIMITED
- Kiểm tra quota tại: **Google Cloud Console → Vertex AI → Quotas**
- AI Review Worker có exponential backoff tự động — không retry thủ công liên tục

---

## 12. Rotate API Key

1. Tạo key mới tại: **Google Cloud Console → APIs & Services → Credentials → Create Credentials → API Key**
2. Cấu hình API restrictions cho Vertex AI API
3. Cập nhật `VERTEX_API_KEY` trong environment (Render/Railway secret hoặc VPS .env)
4. Restart service (xem mục 10)
5. Thu hồi key cũ tại Console → Credentials → Delete key cũ
6. **Không commit key mới vào git**

---

## 13. Vô hiệu hóa AI Review Worker

Trong `.env`:
```
AI_REVIEW_WORKER_INTERVAL_MS=0
```

Hoặc dừng worker programmatically:
```js
const reviewAiWorker = require('./cron/reviewAiWorker');
reviewAiWorker.stop();
```

---

## 14. Chi phí live test

Mỗi lần chạy `npm run test:vertex:live`:
- Gửi 1 request với prompt ~20 tokens
- Output ~20 tokens
- Chi phí ước tính: < $0.001 USD

---

## 15. Không commit `.env`

`.gitignore` đã cấu hình:
```
.env
.env.*
!.env.example
```

Kiểm tra file tracked:
```bash
git ls-files | grep .env
```

Chỉ `.env.example` (không có giá trị thật) được tracked. Nếu thấy `.env` trong output, chạy:
```bash
git rm --cached .env
```

---

## 16. Cấu trúc file liên quan

```
backend/
├── config/
│   └── vertex.config.js          # Client factory + validation
├── services/
│   └── ai/
│       └── vertexGemini.service.js   # Shared Vertex service
│   └── reviewAi/
│       ├── vertexReviewAi.provider.js  # Review AI provider (Vertex)
│       ├── reviewAiSummary.service.js  # Worker service (uses Vertex)
│       └── geminiReviewAi.provider.js  # Legacy (Google AI Studio) — kept for ref
├── scripts/
│   ├── verify-vertex-config.js   # Verify config (no network)
│   └── test-vertex-live.js       # Live smoke test
├── tests/unit/
│   └── vertexGemini.service.test.js  # Unit tests (no real calls)
├── .env.example                   # Template (no real values)
└── .gitignore                     # Ignores .env, .env.*
```
