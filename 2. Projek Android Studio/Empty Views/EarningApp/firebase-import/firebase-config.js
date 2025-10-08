const admin = require('firebase-admin');
const path = require('path');

// Initialize Firebase Admin SDK using service account key file
const serviceAccount = require('./service-account-key.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: serviceAccount.project_id,
});

const db = admin.firestore();

module.exports = { admin, db };
