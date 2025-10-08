const { initializeFirebase } = require('./firebase-config');

async function debugProductData() {
  console.log('🔍 Debugging Product Data in Firestore...\n');

  try {
    const { db } = initializeFirebase();

    // Get first few products to see structure
    const querySnapshot = await db.collection('products').limit(3).get();

    console.log(`📊 Found ${querySnapshot.size} products`);

    querySnapshot.forEach((doc) => {
      console.log('\n📦 Product ID:', doc.id);
      console.log('📄 Data:', JSON.stringify(doc.data(), null, 2));

      const data = doc.data();
      console.log('🔍 Field types:');
      console.log(`  - name: ${typeof data.name} (${data.name})`);
      console.log(`  - price: ${typeof data.price} (${data.price})`);
      console.log(`  - rating: ${typeof data.rating} (${data.rating})`);
      console.log(`  - isAvailable: ${typeof data.isAvailable} (${data.isAvailable})`);
      console.log(`  - createdAt: ${typeof data.createdAt} (${data.createdAt})`);
      console.log('---');
    });
  } catch (error) {
    console.error('❌ Error:', error.message);
  }
}

debugProductData();
