const { initializeFirebase } = require('./firebase-config');
const { importProducts } = require('./import-products');
const { importCategories } = require('./import-categories');
const { importUsers } = require('./import-users');
const { testConnection } = require('./test-connection');

async function importAllData() {
  console.log('🚀 EasyShop Firebase Data Import Tool');
  console.log('=====================================\n');

  try {
    // Test connection first
    console.log('1️⃣ Testing Firebase connection...');
    await testConnection();
    console.log('\n' + '='.repeat(50) + '\n');

    // Import categories first
    console.log('2️⃣ Importing categories...');
    await importCategories();
    console.log('\n' + '='.repeat(50) + '\n');

    // Import products
    console.log('3️⃣ Importing products...');
    await importProducts();
    console.log('\n' + '='.repeat(50) + '\n');

    // Import sample users (optional)
    console.log('4️⃣ Importing sample user documents...');
    await importUsers();
    console.log('\n' + '='.repeat(50) + '\n');

    // Final summary
    console.log('🎉 DATA IMPORT COMPLETED SUCCESSFULLY!');
    console.log('=====================================');

    const { db } = initializeFirebase();

    // Get final counts
    const [categoriesSnapshot, productsSnapshot, usersSnapshot] = await Promise.all([db.collection('categories').get(), db.collection('products').get(), db.collection('users').get()]);

    console.log('📊 FINAL DATABASE SUMMARY:');
    console.log(`   📂 Categories: ${categoriesSnapshot.size}`);
    console.log(`   📦 Products: ${productsSnapshot.size}`);
    console.log(`   👥 User Documents: ${usersSnapshot.size}`);

    console.log('\n✅ Your EasyShop app is now ready with sample data!');
    console.log('🔥 Firebase Firestore collections populated successfully');
    console.log('\n📱 You can now test your Android app with this data');
  } catch (error) {
    console.error('❌ Import process failed:', error.message);
    console.log('\n🔧 Troubleshooting:');
    console.log('1. Check your service-account-key.json file');
    console.log('2. Verify Firebase project settings');
    console.log('3. Ensure you have proper permissions');
    process.exit(1);
  }
}

// Interactive menu function
async function showMenu() {
  const readline = require('readline').createInterface({
    input: process.stdin,
    output: process.stdout,
  });

  console.log('🚀 EasyShop Firebase Import Tool');
  console.log('=================================');
  console.log('Choose an option:');
  console.log('1. Import all data (recommended)');
  console.log('2. Test connection only');
  console.log('3. Import categories only');
  console.log('4. Import products only');
  console.log('5. Import users only');
  console.log('6. Exit');

  readline.question('\nEnter your choice (1-6): ', async (choice) => {
    console.log('');

    try {
      switch (choice) {
        case '1':
          await importAllData();
          break;
        case '2':
          await testConnection();
          break;
        case '3':
          await importCategories();
          break;
        case '4':
          await importProducts();
          break;
        case '5':
          await importUsers();
          break;
        case '6':
          console.log('👋 Goodbye!');
          break;
        default:
          console.log('❌ Invalid choice. Please run the script again.');
      }
    } catch (error) {
      console.error('❌ Error:', error.message);
    }

    readline.close();
  });
}

// Check if script is run directly or with arguments
if (require.main === module) {
  const args = process.argv.slice(2);

  if (args.length === 0) {
    // Show interactive menu
    showMenu();
  } else if (args[0] === '--all') {
    // Import all data directly
    importAllData();
  } else {
    console.log('Usage:');
    console.log('  node import-data.js        # Interactive menu');
    console.log('  node import-data.js --all  # Import all data directly');
  }
}

module.exports = { importAllData };
