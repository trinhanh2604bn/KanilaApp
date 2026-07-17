# Atlas Search Setup Guide

## 1. Create the Search Index

1. Log in to your MongoDB Atlas dashboard.
2. Select your cluster and go to the **Search** tab.
3. Click **Create Search Index**.
4. Choose the **JSON Editor** method.
5. Select the Database and the Collection `product_search_documents`.
6. Enter `product_search_v1` as the Index Name.
7. Paste the JSON definition from `atlas-product-search-index.json`.
8. Click **Next** and then **Create Search Index**.

Wait for the index build to finish (Status changes from *Initial Sync* to *Active*).

## 2. Environment Variables

Update your `.env` or deployment variables:

```env
SEARCH_PROVIDER=atlas
SEARCH_FALLBACK_ENABLED=true
SEARCH_ATLAS_INDEX_NAME=product_search_v1
SEARCH_AUTOCOMPLETE_MIN_CHARS=2
SEARCH_DEFAULT_LIMIT=20
SEARCH_MAX_LIMIT=40
SEARCH_IMAGE_ENABLED=false
```

## 3. Rebuild Search Documents

Atlas Search indexes the `product_search_documents` collection. If this collection is out of sync or empty, populate it:

```bash
cd backend
node scripts/rebuild-product-search-documents.js --remove-orphans
```

To test without affecting the database:
```bash
node scripts/rebuild-product-search-documents.js --dry-run
```

## 4. Fallback Mode Behavior

If Atlas Search is not configured or throws an error, the backend will gracefully catch the error and execute `_fallbackSearch`. The fallback search queries `product_search_documents` using anchored regex prefixes (e.g., `^kem`) and token matching for SKUs and Barcodes. This avoids full unanchored collection scans while maintaining basic autocomplete capabilities.
