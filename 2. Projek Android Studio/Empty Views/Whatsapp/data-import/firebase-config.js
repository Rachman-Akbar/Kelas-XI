const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccount.json');

// Initialize Firebase Admin SDK
if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: `https://${serviceAccount.project_id}-default-rtdb.firebaseio.com/`,
  });
}

const db = admin.database();
const auth = admin.auth();

module.exports = { admin, db, auth };
