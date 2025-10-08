const { importPreloadedImages } = require('./scripts/import-preloaded-images');

async function main() {
  console.log('🖼️  Starting preloaded images import...');

  try {
    await importPreloadedImages();
    console.log('🎉 Preloaded images import completed successfully!');
    process.exit(0);
  } catch (error) {
    console.error('❌ Import failed:', error);
    process.exit(1);
  }
}

main();
