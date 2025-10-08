const admin = require('firebase-admin');
const path = require('path');
const fs = require('fs');
require('dotenv').config();

class FirebaseConfig {
  constructor() {
    this.initialized = false;
  }

  initialize() {
    if (this.initialized || admin.apps.length > 0) {
      return admin.firestore();
    }

    try {
      // Path to service account key file
      const keyPath = path.join(__dirname, '..', 'service-account-key.json');

      // Check if service account key file exists
      if (!fs.existsSync(keyPath)) {
        throw new Error(`Service account key file not found at: ${keyPath}`);
      }

      // Read and parse service account key
      const serviceAccountKey = JSON.parse(fs.readFileSync(keyPath, 'utf8'));

      console.log(`🔑 Using service account key from: service-account-key.json`);
      console.log(`📁 Project ID: ${serviceAccountKey.project_id}`);
      console.log(`📧 Client Email: ${serviceAccountKey.client_email}`);

      admin.initializeApp({
        credential: admin.credential.cert(serviceAccountKey),
        projectId: serviceAccountKey.project_id,
        storageBucket: `${serviceAccountKey.project_id}.appspot.com`,
      });

      this.initialized = true;
      console.log('✅ Firebase Admin SDK initialized successfully');
      return admin.firestore();
    } catch (error) {
      console.error('❌ Error initializing Firebase Admin SDK:', error);
      throw error;
    }
  }

  getFirestore() {
    if (!this.initialized) {
      return this.initialize();
    }
    return admin.firestore();
  }

  getAuth() {
    return admin.auth();
  }

  getStorage() {
    return admin.storage();
  }
}

module.exports = FirebaseConfig;
