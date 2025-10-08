const admin = require('firebase-admin');

// Initialize Firebase Admin
const serviceAccount = require('./service-account-key.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

async function importSampleOrders() {
  try {
    // Get a sample user ID from users collection
    const usersSnapshot = await db.collection('users').limit(1).get();

    if (usersSnapshot.empty) {
      console.log('No users found. Please create a user first.');
      return;
    }

    const sampleUser = usersSnapshot.docs[0];
    const userId = sampleUser.id;
    const userData = sampleUser.data();

    console.log(`Creating sample orders for user: ${userData.email || 'Unknown'}`);

    // Sample orders data
    const sampleOrders = [
      {
        userId: userId,
        userEmail: userData.email || 'user@example.com',
        userName: userData.name || 'Test User',
        items: [
          {
            productId: 'prod_001',
            productName: 'Kemeja Kasual Pria',
            productPrice: 125000,
            productImageUrl: 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=500',
            quantity: 2,
            totalPrice: 250000,
          },
          {
            productId: 'prod_002',
            productName: 'Celana Jeans Slim Fit',
            productPrice: 180000,
            productImageUrl: 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=500',
            quantity: 1,
            totalPrice: 180000,
          },
        ],
        shippingAddress: {
          recipientName: userData.name || 'Test User',
          fullAddress: 'Jl. Merdeka No. 123, RT 05/RW 08, Kelurahan Kebon Jeruk',
          phoneNumber: '081234567890',
          city: 'Jakarta Barat',
          postalCode: '11530',
          notes: 'Rumah cat hijau, di samping warung Pak Budi',
        },
        subtotal: 430000,
        tax: 47300, // 11% PPN
        shippingCost: 15000,
        total: 492300,
        status: 'Processing',
        paymentMethod: 'COD',
        paymentStatus: 'Pending',
        createdAt: Date.now() - 2 * 24 * 60 * 60 * 1000, // 2 days ago
        updatedAt: Date.now() - 2 * 24 * 60 * 60 * 1000,
      },
      {
        userId: userId,
        userEmail: userData.email || 'user@example.com',
        userName: userData.name || 'Test User',
        items: [
          {
            productId: 'prod_003',
            productName: 'Sepatu Sneakers Putih',
            productPrice: 299000,
            productImageUrl: 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500',
            quantity: 1,
            totalPrice: 299000,
          },
        ],
        shippingAddress: {
          recipientName: userData.name || 'Test User',
          fullAddress: 'Jl. Sudirman No. 456, Apartemen Green View, Tower A Lt. 15',
          phoneNumber: '081234567890',
          city: 'Jakarta Selatan',
          postalCode: '12190',
          notes: 'Lobby apartemen, serahkan ke security',
        },
        subtotal: 299000,
        tax: 32890, // 11% PPN
        shippingCost: 15000,
        total: 346890,
        status: 'Shipped',
        paymentMethod: 'Transfer Bank',
        paymentStatus: 'Paid',
        createdAt: Date.now() - 5 * 24 * 60 * 60 * 1000, // 5 days ago
        updatedAt: Date.now() - 1 * 24 * 60 * 60 * 1000, // 1 day ago
      },
      {
        userId: userId,
        userEmail: userData.email || 'user@example.com',
        userName: userData.name || 'Test User',
        items: [
          {
            productId: 'prod_004',
            productName: 'Tas Ransel Laptop',
            productPrice: 245000,
            productImageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500',
            quantity: 1,
            totalPrice: 245000,
          },
          {
            productId: 'prod_005',
            productName: 'Jam Tangan Digital',
            productPrice: 350000,
            productImageUrl: 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=500',
            quantity: 1,
            totalPrice: 350000,
          },
        ],
        shippingAddress: {
          recipientName: userData.name || 'Test User',
          fullAddress: 'Jl. Gatot Subroto No. 789, Komplek Perkantoran Blok C-12',
          phoneNumber: '081234567890',
          city: 'Jakarta Pusat',
          postalCode: '10270',
          notes: 'Kantor lantai 3, bagian IT',
        },
        subtotal: 595000,
        tax: 65450, // 11% PPN
        shippingCost: 15000,
        total: 675450,
        status: 'Delivered',
        paymentMethod: 'COD',
        paymentStatus: 'Paid',
        createdAt: Date.now() - 10 * 24 * 60 * 60 * 1000, // 10 days ago
        updatedAt: Date.now() - 7 * 24 * 60 * 60 * 1000, // 7 days ago
      },
    ];

    // Import orders to Firestore
    const batch = db.batch();

    sampleOrders.forEach((order, index) => {
      const orderRef = db.collection('orders').doc();
      batch.set(orderRef, order);
      console.log(`Prepared order ${index + 1}: ${order.items.map((item) => item.productName).join(', ')}`);
    });

    await batch.commit();
    console.log(`✅ Successfully imported ${sampleOrders.length} sample orders!`);

    // Verify import (without orderBy to avoid index requirement)
    const ordersSnapshot = await db.collection('orders').where('userId', '==', userId).get();

    console.log(`📋 Total orders for user: ${ordersSnapshot.size}`);

    ordersSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`- Order ${doc.id}: ${data.status} - Rp ${data.total.toLocaleString('id-ID')} (${data.items.length} items)`);
    });
  } catch (error) {
    console.error('Error importing sample orders:', error);
  }
}

// Run the import
importSampleOrders();
