const { initializeFirebase } = require('./firebase-config');

// Sample banner data
const sampleBanners = [
  {
    title: 'Flash Sale 70% Off',
    subtitle: 'Hemat hingga 70% untuk semua kategori elektronik',
    imageUrl: 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800',
    actionText: 'Belanja Sekarang',
    actionUrl: '/products?category=Electronics',
    isActive: true,
    sortOrder: 1,
    createdAt: Date.now(),
  },
  {
    title: 'New Fashion Collection',
    subtitle: 'Temukan koleksi fashion terbaru dari brand ternama',
    imageUrl: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800',
    actionText: 'Lihat Koleksi',
    actionUrl: '/products?category=Fashion',
    isActive: true,
    sortOrder: 2,
    createdAt: Date.now(),
  },
  {
    title: 'Beauty & Care',
    subtitle: 'Produk kecantikan terbaik untuk perawatan harian Anda',
    imageUrl: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=800',
    actionText: 'Shop Beauty',
    actionUrl: '/products?category=Beauty',
    isActive: true,
    sortOrder: 3,
    createdAt: Date.now(),
  },
];

async function importBanners() {
  console.log('🔄 Starting banner import process...\n');

  try {
    const { db } = initializeFirebase();

    console.log(`🖼️ Importing ${sampleBanners.length} banners...`);

    const batch = db.batch();

    sampleBanners.forEach((banner, index) => {
      const docRef = db.collection('banners').doc();
      batch.set(docRef, banner);
      console.log(`➕ Added banner ${index + 1}: ${banner.title}`);
    });

    // Commit the batch
    await batch.commit();

    console.log('\n✅ Banner import completed successfully!');
    console.log(`📊 Total banners imported: ${sampleBanners.length}`);

    // Verify import
    const bannersSnapshot = await db.collection('banners').get();
    console.log(`🔍 Verification: ${bannersSnapshot.size} banners in database`);
  } catch (error) {
    console.error('❌ Error importing banners:', error.message);
    process.exit(1);
  }
}

// Run import if this file is executed directly
if (require.main === module) {
  importBanners();
}

module.exports = { importBanners, sampleBanners };
