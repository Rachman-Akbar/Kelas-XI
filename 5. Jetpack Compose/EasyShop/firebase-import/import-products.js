const { initializeFirebase } = require('./firebase-config');

// Sample product data yang sesuai dengan model Anda
const sampleProducts = [
  {
    name: 'iPhone 15 Pro Max',
    description: 'Smartphone flagship terbaru dari Apple dengan chip A17 Pro dan kamera canggih.',
    price: 21999000,
    imageUrl: 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=500',
    category: 'Electronics',
    rating: 4.8,
    reviewCount: 1250,
    stock: 25,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Samsung Galaxy S24 Ultra',
    description: 'Flagship Android dengan S Pen dan kamera 200MP yang menakjubkan.',
    price: 18999000,
    imageUrl: 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500',
    category: 'Electronics',
    rating: 4.7,
    reviewCount: 980,
    stock: 30,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'MacBook Air M3',
    description: 'Laptop ultra-tipis dengan performa luar biasa untuk produktivitas dan kreativitas.',
    price: 16999000,
    imageUrl: 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=500',
    category: 'Electronics',
    rating: 4.9,
    reviewCount: 750,
    stock: 15,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Nike Air Jordan 1',
    description: 'Sepatu basket klasik yang menjadi ikon fashion streetwear.',
    price: 2499000,
    imageUrl: 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b?w=500',
    category: 'Fashion',
    rating: 4.6,
    reviewCount: 2100,
    stock: 50,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Adidas Ultraboost 23',
    description: 'Sepatu lari dengan teknologi Boost untuk kenyamanan maksimal.',
    price: 2199000,
    imageUrl: 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500',
    category: 'Fashion',
    rating: 4.5,
    reviewCount: 1800,
    stock: 40,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Uniqlo Basic T-Shirt',
    description: 'Kaos polos berkualitas tinggi dengan bahan cotton premium.',
    price: 149000,
    imageUrl: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500',
    category: 'Fashion',
    rating: 4.4,
    reviewCount: 5200,
    stock: 100,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Kopi Arabica Premium',
    description: 'Biji kopi arabica pilihan dari pegunungan Jawa dengan cita rasa yang kaya.',
    price: 85000,
    imageUrl: 'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=500',
    category: 'Food & Beverage',
    rating: 4.7,
    reviewCount: 320,
    stock: 200,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Minyak Zaitun Extra Virgin',
    description: 'Minyak zaitun murni berkualitas tinggi untuk memasak dan kesehatan.',
    price: 125000,
    imageUrl: 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500',
    category: 'Food & Beverage',
    rating: 4.6,
    reviewCount: 890,
    stock: 75,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Skincare Set Vitamin C',
    description: 'Set perawatan wajah dengan vitamin C untuk kulit cerah dan sehat.',
    price: 299000,
    imageUrl: 'https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=500',
    category: 'Beauty',
    rating: 4.8,
    reviewCount: 1150,
    stock: 60,
    isAvailable: true,
    createdAt: Date.now(),
  },
  {
    name: 'Serum Hyaluronic Acid',
    description: 'Serum pelembab intensif untuk menjaga kelembaban kulit sepanjang hari.',
    price: 189000,
    imageUrl: 'https://images.unsplash.com/photo-1620756103048-dac0da6dc2ba?w=500',
    category: 'Beauty',
    rating: 4.7,
    reviewCount: 780,
    stock: 45,
    isAvailable: true,
    createdAt: Date.now(),
  },
];

async function importProducts() {
  console.log('🔄 Starting product import process...\n');

  try {
    const { db } = initializeFirebase();

    console.log(`📦 Importing ${sampleProducts.length} products...`);

    const batch = db.batch();

    sampleProducts.forEach((product, index) => {
      const docRef = db.collection('products').doc();
      batch.set(docRef, product);
      console.log(`➕ Added product ${index + 1}: ${product.name}`);
    });

    // Commit the batch
    await batch.commit();

    console.log('\n✅ Product import completed successfully!');
    console.log(`📊 Total products imported: ${sampleProducts.length}`);

    // Verify import
    const productsSnapshot = await db.collection('products').get();
    console.log(`🔍 Verification: ${productsSnapshot.size} products in database`);
  } catch (error) {
    console.error('❌ Error importing products:', error.message);
    process.exit(1);
  }
}

// Run import if this file is executed directly
if (require.main === module) {
  importProducts();
}

module.exports = { importProducts, sampleProducts };
