/* eslint-disable no-console */
const fs = require('fs');
const path = require('path');
const admin = require('firebase-admin');

function parseArgs(argv) {
  const args = {};
  for (let i = 2; i < argv.length; i += 1) {
    const key = argv[i];
    const next = argv[i + 1];
    if (key.startsWith('--')) {
      const normalized = key.replace(/^--/, '');
      if (!next || next.startsWith('--')) {
        args[normalized] = true;
      } else {
        args[normalized] = next;
        i += 1;
      }
    }
  }
  return args;
}

function resolveJsonFile(filePath) {
  const absolute = path.isAbsolute(filePath) ? filePath : path.resolve(process.cwd(), filePath);

  if (!fs.existsSync(absolute)) {
    throw new Error(`File tidak ditemukan: ${absolute}`);
  }

  const raw = fs.readFileSync(absolute, 'utf8');
  return JSON.parse(raw);
}

function initializeAdmin() {
  if (admin.apps.length > 0) return;

  const serviceAccountJson = process.env.FIREBASE_SERVICE_ACCOUNT_JSON;
  if (serviceAccountJson) {
    const credential = admin.credential.cert(JSON.parse(serviceAccountJson));
    const projectId = process.env.FIREBASE_PROJECT_ID;
    admin.initializeApp({
      credential,
      projectId,
    });
    return;
  }

  // Fallback to GOOGLE_APPLICATION_CREDENTIALS or gcloud ADC
  admin.initializeApp();
}

async function importCollection(db, collectionName, records, mode) {
  if (!Array.isArray(records)) {
    throw new Error(`Data untuk collection '${collectionName}' harus array.`);
  }

  const writeMode = mode === 'overwrite' ? 'overwrite' : 'merge';
  const batch = db.batch();

  for (const item of records) {
    if (!item || typeof item !== 'object') continue;

    const { id, ...payload } = item;
    const docRef = id ? db.collection(collectionName).doc(String(id)) : db.collection(collectionName).doc();

    const finalPayload = {
      ...payload,
      id: docRef.id,
      createdAt: payload.createdAt || admin.firestore.FieldValue.serverTimestamp(),
    };

    if (writeMode === 'overwrite') {
      batch.set(docRef, finalPayload);
    } else {
      batch.set(docRef, finalPayload, { merge: true });
    }
  }

  await batch.commit();
  return records.length;
}

async function main() {
  const args = parseArgs(process.argv);
  const filePath = args.file || 'seeds/products.json';
  const mode = args.mode || 'merge';

  if (!['merge', 'overwrite'].includes(mode)) {
    throw new Error('Mode hanya boleh: merge | overwrite');
  }

  initializeAdmin();
  const db = admin.firestore();

  const data = resolveJsonFile(filePath);

  let summary = [];

  if (Array.isArray(data)) {
    // Shortcut: array langsung dianggap collection products.
    const count = await importCollection(db, 'products', data, mode);
    summary.push({ collection: 'products', count });
  } else if (data && typeof data === 'object') {
    // Format fleksibel:
    // { "products": [...], "categories": [...] }
    for (const [collectionName, records] of Object.entries(data)) {
      const count = await importCollection(db, collectionName, records, mode);
      summary.push({ collection: collectionName, count });
    }
  } else {
    throw new Error('Format JSON tidak valid. Gunakan array atau object map collection.');
  }

  console.log('Import selesai.');
  for (const item of summary) {
    console.log(`- ${item.collection}: ${item.count} dokumen`);
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error('Import gagal:', error.message);
    process.exit(1);
  });
