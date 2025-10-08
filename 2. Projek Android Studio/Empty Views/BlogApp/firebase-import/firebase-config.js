const admin = require('firebase-admin');
const path = require('path');

// Initialize Firebase Admin SDK
const initializeFirebase = () => {
  try {
    // Load service account key directly from JSON file
    const serviceAccount = require('./service-key-account.json');

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      projectId: serviceAccount.project_id,
    });

    console.log('✅ Firebase Admin SDK initialized successfully');
    return admin.firestore();
  } catch (error) {
    console.error('❌ Error initializing Firebase:', error);
    process.exit(1);
  }
};

module.exports = { initializeFirebase, admin };
