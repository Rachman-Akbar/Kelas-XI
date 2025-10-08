const fs = require('fs');
const path = require('path');

function verifyServiceAccountKey() {
  console.log('🔍 Verifying service account key...\n');

  const keyPath = path.join(__dirname, 'service-account-key.json');

  // Check if file exists
  if (!fs.existsSync(keyPath)) {
    console.error('❌ Error: service-account-key.json not found!');
    console.log('\n💡 Solution:');
    console.log('1. Download the service account key from Firebase Console');
    console.log('2. Rename it to "service-account-key.json"');
    console.log('3. Place it in the firebase-import folder');
    console.log('\nExpected location:', keyPath);
    return false;
  }

  try {
    // Read and parse JSON
    const serviceAccount = JSON.parse(fs.readFileSync(keyPath, 'utf8'));

    console.log('✅ File found and readable');
    console.log('📄 File size:', (fs.statSync(keyPath).size / 1024).toFixed(1), 'KB');

    // Verify required fields
    const requiredFields = ['type', 'project_id', 'private_key_id', 'private_key', 'client_email', 'client_id'];

    const missingFields = [];
    requiredFields.forEach((field) => {
      if (!serviceAccount[field]) {
        missingFields.push(field);
      }
    });

    if (missingFields.length > 0) {
      console.error('❌ Missing required fields:', missingFields.join(', '));
      return false;
    }

    console.log('✅ All required fields present');
    console.log('\n📋 Service Account Info:');
    console.log('   🆔 Project ID:', serviceAccount.project_id);
    console.log('   📧 Client Email:', serviceAccount.client_email);
    console.log('   🔑 Key ID:', serviceAccount.private_key_id?.substring(0, 8) + '...');
    console.log('   📅 Type:', serviceAccount.type);

    // Verify private key format
    if (serviceAccount.private_key && serviceAccount.private_key.includes('BEGIN PRIVATE KEY')) {
      console.log('✅ Private key format looks correct');
    } else {
      console.warn('⚠️  Private key format might be incorrect');
    }

    console.log('\n🎉 Service account key verification successful!');
    console.log('👍 You can now run: npm test');

    return true;
  } catch (error) {
    console.error('❌ Error reading service account key:', error.message);
    console.log('\n💡 Solution:');
    console.log('1. Make sure the file is valid JSON');
    console.log('2. Re-download from Firebase Console if corrupted');
    console.log('3. Check file permissions');
    return false;
  }
}

// Run verification if called directly
if (require.main === module) {
  const success = verifyServiceAccountKey();
  process.exit(success ? 0 : 1);
}

module.exports = verifyServiceAccountKey;
