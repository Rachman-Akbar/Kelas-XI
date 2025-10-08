// Standalone script to import user data
require('./firebase-config');
const { importUserData } = require('./import-users');

// Run user data import
importUserData()
  .then(() => {
    console.log('\n🎉 User data import completed successfully!');
    console.log('✅ Database is ready for user authentication and management.');
    process.exit(0);
  })
  .catch((error) => {
    console.error('💥 User data import failed:', error);
    process.exit(1);
  });
