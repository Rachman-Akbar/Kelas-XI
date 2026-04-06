// Legacy Firebase reference file.
// The actual web app configuration lives in frontend/src/lib/firebase.ts.
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyDWg2pEOGEhQ_plFceG9sUgcR0OgjnvY8c",
  authDomain: "khisaapp.firebaseapp.com",
  projectId: "khisaapp",
  storageBucket: "khisaapp.firebasestorage.app",
  messagingSenderId: "849684754606",
  appId: "1:849684754606:web:53e6628bb6a0cc3df0bd3f",
  measurementId: "G-40MTC4RRQJ"
};

const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);

export const db = getFirestore(app);