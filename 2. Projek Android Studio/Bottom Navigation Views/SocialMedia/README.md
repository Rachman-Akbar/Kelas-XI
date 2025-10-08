# SocialMedia App - Android Kotlin with Firebase

Aplikasi media sosial lengkap yang dibangun dengan Android Kotlin dan Firebase sebagai backend.

## 🚀 Fitur Utama

### ✅ Sudah Diimplementasi:

- **Authentication System**

  - Login dengan email & password
  - Registrasi pengguna baru
  - Forgot password
  - Session management

- **Post Management**

  - Upload post dengan gambar
  - Caption untuk post
  - Like/Unlike posts
  - Delete posts (owner only)
  - Feed timeline

- **Story System**

  - Upload story dengan gambar
  - Story expiry (24 jam)
  - View stories
  - Mark stories as viewed

- **User Management**

  - User profiles
  - Follow/Unfollow users
  - User search
  - Block/Unblock users

- **UI/UX**
  - Material Design
  - Circular profile images dengan gradient border
  - Stories bar horizontal
  - Post items dengan like, comment, share buttons
  - Responsive layouts

## 🏗️ Arsitektur

### Data Layer:

- **Managers**: `AuthManager`, `PostManager`, `StoryManager`, `UserManager`
- **Models**: `User`, `Post`, `Story`, `Result`
- **Firebase Integration**: Firestore, Storage, Authentication

### UI Layer:

- **Activities**: `LoginActivity`, `RegisterActivity`, `MainActivity`, `UploadPostActivity`
- **Fragments**: `HomeFragment`, `DashboardFragment`, `NotificationsFragment`
- **Adapters**: `PostAdapter`, `StoryAdapter`

## 📱 Screenshots & Layout

### Layout Files:

- `fragment_home.xml` - Home feed dengan stories dan posts
- `item_story.xml` - Story item dengan circular image + gradient border
- `list_item_post.xml` - Post item lengkap dengan header, content, footer
- `activity_login.xml` - Login screen
- `activity_register.xml` - Registration screen
- `activity_upload_post.xml` - Upload post screen

## 🔧 Setup Firebase

1. **Firebase Project Setup**:

   - Buat project Firebase di [Firebase Console](https://console.firebase.google.com)
   - Tambahkan Android app dengan package name: `com.komputerkit.socialmedia`

2. **Services yang Diperlukan**:

   - **Authentication**: Email/Password
   - **Firestore**: Database untuk users, posts, stories
   - **Storage**: Untuk upload gambar

3. **Firestore Collections**:

   ```
   users/
   ├── {userId}/
   │   ├── username: string
   │   ├── email: string
   │   ├── profileImageUrl: string
   │   ├── bio: string
   │   ├── followersCount: number
   │   ├── followingCount: number
   │   └── postsCount: number

   posts/
   ├── {postId}/
   │   ├── userId: string
   │   ├── username: string
   │   ├── imageUrl: string
   │   ├── caption: string
   │   ├── likesCount: number
   │   ├── likedBy: array
   │   └── createdAt: timestamp

   stories/
   ├── {storyId}/
   │   ├── userId: string
   │   ├── username: string
   │   ├── imageUrl: string
   │   ├── createdAt: timestamp
   │   ├── expiresAt: timestamp
   │   └── viewedBy: array
   ```

4. **Storage Structure**:
   ```
   /posts/{userId}/{timestamp}/{imageId}.jpg
   /stories/{userId}/{timestamp}/{imageId}.jpg
   /profile_images/{userId}/{imageId}.jpg
   ```

## 🛠️ Dependencies

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")
implementation("com.google.firebase:firebase-analytics-ktx")

// UI
implementation("de.hdodenhof:circleimageview:3.1.0")
implementation("com.github.bumptech.glide:glide:4.16.0")

// Android Jetpack
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx")
implementation("androidx.lifecycle:lifecycle-livedata-ktx")
implementation("androidx.navigation:navigation-fragment-ktx")
implementation("androidx.navigation:navigation-ui-ktx")
```

## 📋 Cara Penggunaan

### 1. Login/Register:

- Buka aplikasi → Login screen
- Register akun baru atau login dengan akun existing
- Validasi email format dan password (min 6 karakter)

### 2. Home Feed:

- Lihat stories di bagian atas (horizontal scroll)
- Lihat posts di feed utama
- Like, comment, share posts
- Tap profile untuk melihat user profile

### 3. Upload Post:

- Tap FAB (Floating Action Button) di kanan bawah
- Pilih gambar dari Gallery atau Camera
- Tulis caption (opsional)
- Tap "Share Post"

### 4. Stories:

- Tap story di stories bar untuk melihat
- Stories otomatis expired setelah 24 jam

## 🎨 Design Features

### Material Design Elements:

- **Gradient Borders**: Stories menggunakan Instagram-style gradient
- **Ripple Effects**: Interactive feedback pada buttons
- **Circular Images**: Profile pictures dengan CircleImageView
- **Constraintlayout**: Responsive layouts
- **Material Components**: TextInputLayout, MaterialButton, etc.

### Color Scheme:

- Primary: Mengikuti Material Design
- Stories Gradient: Orange → Pink/Red (Instagram-style)
- Background: White/Light Gray
- Text: Black/Gray

## 🔐 Security & Permissions

### Permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### Security Rules (Firestore):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    match /posts/{postId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == resource.data.userId;
    }

    match /stories/{storyId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == resource.data.userId;
    }
  }
}
```

## 🚀 Next Steps

### Fitur yang Bisa Ditambahkan:

1. **Comments System** - Komentar pada posts
2. **Real-time Updates** - LiveData dengan Firestore listeners
3. **Push Notifications** - FCM untuk like, comment, follow
4. **Direct Messages** - Chat antar users
5. **Explore/Search** - Pencarian posts dan users
6. **Profile Editing** - Edit bio, profile picture
7. **Settings** - Privacy settings, logout
8. **Offline Support** - Caching dengan Room database

### Performance Optimizations:

1. **Pagination** - Load posts secara bertahap
2. **Image Compression** - Compress images sebelum upload
3. **Caching** - Cache images dengan Glide
4. **Lazy Loading** - Load content saat diperlukan

## 📧 Support

Untuk pertanyaan atau issue, silakan buka GitHub Issues atau hubungi developer.

## 📄 License

Aplikasi ini dibuat untuk keperluan learning dan development. Silakan gunakan dan modifikasi sesuai kebutuhan.
