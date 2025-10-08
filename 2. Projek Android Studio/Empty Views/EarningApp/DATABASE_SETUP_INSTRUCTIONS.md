# Firebase Setup Instructions

## Problem: CONFIGURATION_NOT_FOUND Error

The current error indicates that Firebase Authentication is not properly configured for this project. Here's how to fix it:

## Step 1: Create a New Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or use existing project
3. Project name: `EarningApp` (or similar)
4. Enable Google Analytics if desired

## Step 2: Add Android App to Firebase Project

1. In Firebase Console, click "Add app" → Android
2. Package name: `com.komputerkit.earningapp`
3. App nickname: `EarningApp Android`
4. Debug signing certificate SHA-1 (optional for now)

## Step 3: Download New google-services.json

1. Download the new `google-services.json` file
2. Replace the current file at: `app/google-services.json`

## Step 4: Enable Firebase Authentication

1. In Firebase Console, go to "Authentication"
2. Click "Get started"
3. Go to "Sign-in method" tab
4. Enable "Email/Password" authentication
5. Save changes

## Step 5: Enable Cloud Firestore

1. In Firebase Console, go to "Firestore Database"
2. Click "Create database"
3. Choose "Start in test mode" (for development)
4. Select a location (closest to your users)

## Step 6: Set Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Public read access for subjects and questions
    match /subjects/{document=**} {
      allow read: if true;
    }

    match /questions/{document=**} {
      allow read: if true;
    }
  }
}
```

## Alternative: Use Firebase Emulator (for Development)

If you want to test locally without setting up a real Firebase project:

1. Install Firebase CLI: `npm install -g firebase-tools`
2. Run: `firebase login`
3. In project root: `firebase init emulators`
4. Select "Authentication" and "Firestore" emulators
5. Run: `firebase emulators:start`

## Quick Fix for Current Issue

For immediate testing, you can temporarily disable Firebase validation in the code:

1. The current code has been modified to skip Firebase Auth tests
2. Registration should work with basic Firebase setup
3. Full features require proper Firebase configuration

## Testing Registration

After proper setup:

1. Build and install the app
2. Try registering with a valid email
3. Check Firebase Console for new users
4. Check Firestore for user documents
