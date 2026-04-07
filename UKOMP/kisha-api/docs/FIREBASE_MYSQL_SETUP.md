# Firebase + MySQL Integration Setup

## What is already integrated

- MySQL stays the primary database through Laravel models and API.
- Firebase Firestore is used as a sync target for product documents.
- Sync runs automatically on product create/update/delete and image changes.

Implemented files:

- `config/firebase.php`
- `app/Services/Firebase/FirestoreProductSyncService.php`
- `app/Modules/Product/Observers/ProductObserver.php`
- `app/Modules/Product/Observers/ProductImageObserver.php`
- `app/Providers/AppServiceProvider.php`

## 1) Place Firebase service account

1. Download service account JSON from Firebase Console:
   - Project Settings -> Service accounts -> Generate new private key
2. Put the file at:

```text
storage/app/firebase/service-account.json
```

3. If you want another path, set `FIREBASE_CREDENTIALS` in `.env`.

Important:

- Never commit this JSON key to Git.
- Keep `FIREBASE_ENABLED=false` until file and config are ready.

## 2) Configure backend env (.env)

Add or fill these values in project root `.env`:

```env
FIREBASE_ENABLED=true
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_DATABASE_URL=https://your-project-id.firebaseio.com
FIREBASE_CREDENTIALS=storage/app/firebase/service-account.json
FIREBASE_FIRESTORE_PRODUCTS_COLLECTION=products
```

Notes:

- `FIREBASE_DATABASE_URL` is optional for Firestore sync, but safe to set.
- Collection default is `products`.

## 3) Configure frontend env (Next.js)

1. Copy this template:

```bash
frontend/.env.local.example -> frontend/.env.local
```

2. Fill values from Firebase Web App config:

```env
NEXT_PUBLIC_FIREBASE_API_KEY=
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=
NEXT_PUBLIC_FIREBASE_PROJECT_ID=
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=
NEXT_PUBLIC_FIREBASE_APP_ID=
NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID=
```

The frontend config is already consumed by `frontend/src/lib/firebase.ts`.

## 4) Clear config cache and run app

```bash
php artisan config:clear
php artisan optimize:clear
php artisan serve
```

For frontend:

```bash
cd frontend
npm run dev
```

## 5) Quick sync test

1. Login as seller/admin.
2. Create or update a product using API.
3. Open Firestore and check document in collection `products` with doc id = MySQL product id.
4. Delete product and confirm doc removed from Firestore.

## Troubleshooting

- If sync fails, check `storage/logs/laravel.log` for Firebase errors.
- Ensure service account has Firestore access for the same project id.
- Ensure `FIREBASE_ENABLED=true`.
