# Marketplace Setup Guide

## 1) Project Folder Structure

```text
app/src/main/java/com/example/kishaapp/
  core/
    app/
      KishaApp.kt
      MainActivity.kt
    ui/
      components/
        AuthContainer.kt
        MarketplaceComponents.kt
      theme/
        Color.kt
        Theme.kt
        Type.kt
  feature/
    auth/
      ui/screens/
        LoginMvvmScreen.kt
        RegisterMvvmScreen.kt
        ForgotPasswordMvvmScreen.kt
      viewmodel/
        AuthViewModel.kt
    marketplace/
      ui/screens/
        SplashScreen.kt
        HomeScreen.kt
        SearchScreen.kt
        ProductDetailScreen.kt
      viewmodel/
        MarketplaceViewModel.kt
    seller/
      ui/screens/
        SellerDashboardScreen.kt
        AddEditProductScreen.kt
      viewmodel/
        SellerViewModel.kt
    profile/
      ui/screens/
        ProfileScreen.kt
  data/
    model/
      UserProfile.kt
      Category.kt
      Product.kt
    remote/
      FirebaseProvider.kt
    repository/
      AuthRepository.kt
      MarketplaceRepository.kt
      StorageRepository.kt
      RemoteConfigRepository.kt
  navigation/
    Routes.kt
    AppNavGraph.kt
  utils/
    Validators.kt
```

Struktur sudah dibersihkan: file auth/screen lama non-MVVM dihapus supaya tidak tercampur.

## 2) Firebase Configuration Steps (Spark / Free Tier)

1. Create Firebase project.
2. Add Android app with package: `com.example.kishaapp`.
3. Download `google-services.json` and place in `app/`.
4. Enable Authentication providers:
   - Email/Password
   - Google (optional if still needed)
5. Create Firestore database in production mode.
6. Create Storage bucket.
7. Add SHA-1 and SHA-256 from your debug/release keystore.
8. Create Firestore collections:

- `users`
- `categories`
- `products`

## 3) Firestore Data Models

### users

- uid
- name
- email
- role (admin | seller | customer)

### categories

- id
- name

### products

- id
- title
- description
- price
- imageUrl
- imageUrls
- category
- sellerId
- sellerName
- location
- type (product | service)
- createdAt

## Firestore Document Examples

```json
{
  "users/{uid_abc}": {
    "uid": "uid_abc",
    "name": "Reza",
    "email": "reza@mail.com",
    "role": "seller"
  },
  "categories/{cat_1}": { "id": "cat_1", "name": "Elektronik" },
  "products/{prod_1}": {
    "id": "prod_1",
    "title": "Laptop Gaming",
    "description": "RTX series",
    "price": 12000000,
    "imageUrl": "https://...",
    "imageUrls": ["https://..."],
    "category": "Elektronik",
    "sellerId": "uid_abc",
    "sellerName": "Reza",
    "location": "Bandung",
    "type": "product",
    "createdAt": "timestamp"
  }
}
```

## Spark-Friendly Rules (Starter)

Firestore Rules:

```text
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth != null;
    }

    match /categories/{categoryId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

Storage Rules (starter):

```text
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /product_images/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

## 4) Repository Layer

- `AuthRepository`: login/register/reset password, fetch profile.
- `MarketplaceRepository`: observe products, observe categories, add/update/delete.
- `StorageRepository`: upload image to Firebase Storage.

## 5) ViewModel Layer

- `AuthViewModel`: auth status + profile + auth actions.
- `MarketplaceViewModel`: public listing, search, category filtering.
- `SellerViewModel`: seller CRUD + image upload.

## 6) UI Screens (Flow)

1. SplashScreen
2. HomeScreen (public list, no login needed)
3. SearchScreen (real-time title filtering)
4. ProductDetailScreen (description, images, seller info)
5. ProfileScreen (login/logout + role)
6. Login/Register/ForgotPassword screens
7. SellerDashboardScreen (seller products)
8. AddEditProductScreen (upload image + create/edit product)

## Notes

- Home listing is public.
- Seller-only action: Add/Edit/Delete product.
- Role check is enforced in UI flow.
