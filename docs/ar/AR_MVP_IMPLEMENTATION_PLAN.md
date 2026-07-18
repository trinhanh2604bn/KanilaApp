# AR MVP Implementation Plan

## Phase 1: Technical POC Camera and Lip Render
**Goal**: Integrate CameraX and ML Kit Face Mesh, and render a basic lip mask using Android Canvas.
- Add CameraX and ML Kit dependencies to `libs.versions.toml` and `app/build.gradle.kts`.
- Implement `FaceLandmarkProvider`, `MlKitFaceMeshProvider`.
- Implement `LipPathBuilder`, `LipGeometry`, `LipOverlayView`.
- Create a basic POC UI with camera preview and debug toggle.
- Add unit tests for smoothing and mapping logic.

## Phase 2: Backend and Data Contract
**Goal**: Expand the database schema and implement the AR configuration endpoints and analytics.
- Update `ProductVariant` schema to include AR config.
- Create `ArTryOnEvent` model for analytics.
- Implement `GET /api/products/:productId/ar-config`.
- Implement `POST /api/ar/events/batch`.
- Create `seed-ar-lipstick-config.js` to seed initial data.
- Write backend tests for AR functionality.

## Phase 3: Android MVP Complete
**Goal**: Build the full AR try-on feature integrated with the existing product detail screen and cart.
- Create `ArTryOnFragment` or `ArTryOnActivity`.
- Implement ViewModel with state machine.
- Integrate Camera permission UX.
- Build Shade carousel and integrate pricing/inventory.
- Implement Before/After functionality.
- Integrate Add to Cart logic with the correct variant.
- Offline cache for AR config.
- Add UI instrumentation tests and unit tests.

## Phase 4: Hardening and Sign-off
**Goal**: Performance tuning, lifecycle handling, security checks, and final testing.
- Ensure ML processing is off the main thread.
- Handle process recreation, camera interruptions, and offline scenarios.
- Verify security and privacy (no data uploaded/saved).
- Produce the final Verification Report and checklist.
