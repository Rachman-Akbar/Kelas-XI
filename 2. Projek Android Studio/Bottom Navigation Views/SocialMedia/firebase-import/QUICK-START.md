# 🚀 Quick Start Guide

## Step-by-Step Import Data Firebase

### 1️⃣ Install Dependencies

```bash
cd firebase-import
npm install
```

### 2️⃣ Verify Service Account Key

```bash
npm run verify
```

**Expected output:**

```
✅ File found and readable
✅ All required fields present
📋 Service Account Info:
   🆔 Project ID: your-project-id
   📧 Client Email: firebase-adminsdk-xxx@your-project.iam.gserviceaccount.com
🎉 Service account key verification successful!
```

### 3️⃣ Test Firebase Connection

```bash
npm test
```

**Expected output:**

```
✅ Firestore connection: OK
✅ Auth connection: OK
🎉 All connections successful!
```

### 4️⃣ Import All Data

```bash
npm run import-all
```

**Expected output:**

```
==========================================
STEP 1: IMPORTING USERS
==========================================
📝 Creating user 1/10: john_doe
✅ User created successfully: john_doe (abc123def456)
...

==========================================
🎉 IMPORT COMPLETED SUCCESSFULLY!
==========================================
📊 SUMMARY:
   👥 Users: 10
   📸 Posts: 30
   📱 Stories: 6
```

## 🎯 Success Indicators

✅ **Files Created:**

- `imported-users.json` (10 users)
- `imported-posts.json` (~30 posts)
- `imported-stories.json` (6 stories)
- `import-summary.json` (complete summary)

✅ **Firebase Console Check:**

- Authentication → Users: 10 new users
- Firestore → Collections: `users`, `posts`, `stories`

✅ **Test Login:**

```
Email: john.doe@example.com
Password: password123
```

## 🔧 Troubleshooting

### ❌ "service-account-key.json not found"

**Solution:**

1. Download service account key from Firebase Console
2. Rename to `service-account-key.json`
3. Place in `firebase-import` folder

### ❌ "Permission denied"

**Solution:**

1. Check service account has correct permissions
2. Ensure Firebase project is active
3. Verify project ID matches

### ❌ "Email already exists"

**Solution:**

- This is normal! Script handles existing users
- Will update existing user data instead

## 🎊 Next Steps

After successful import:

1. Open Android app
2. Login with any test user
3. See posts and stories in feed
4. Test like, comment, share features

**Ready to test your SocialMedia app!** 🚀
