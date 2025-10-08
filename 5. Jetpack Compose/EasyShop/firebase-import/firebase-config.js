const admin = require('firebase-admin');
const path = require('path');

// Initialize Firebase Admin SDK
function initializeFirebase() {
  try {
    // Path ke service account key
    const serviceAccountPath = path.join(__dirname, 'service-account-key.json');

    // Initialize Firebase Admin
    const serviceAccount = require(serviceAccountPath);

    if (!admin.apps.length) {
      admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        // Ganti dengan project ID Anda
        projectId: serviceAccount.project_id,
      });
    }

    const db = admin.firestore();
    const auth = admin.auth();

    console.log('✅ Firebase Admin SDK initialized successfully!');
    console.log(`📱 Project ID: ${serviceAccount.project_id}`);

    return { db, auth, admin };
  } catch (error) {
    console.error('❌ Error initializing Firebase:', error.message);
    process.exit(1);
  }
}

module.exports = { initializeFirebase };
