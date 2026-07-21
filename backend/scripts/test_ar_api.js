const mongoose = require('mongoose');

async function test() {
  try {
    const uri = "mongodb+srv://admin:admin@product.hzblpvl.mongodb.net/kanila?retryWrites=true&w=majority&appName=Product";
    await mongoose.connect(uri);
    const db = mongoose.connection.db;
    
    // Test 1: Check ar config for a lipstick product
    const lipsProduct = await db.collection('products').findOne({ ar_type: 'LIPS', hasAr: true });
    console.log('LIPS product sample:', lipsProduct?._id, lipsProduct?.productName);
    
    // Test 2: Query ar config using the product_id as string
    const arConfig = await db.collection('product_ar_configs').findOne({ 
      product_id: lipsProduct?._id?.toString() 
    });
    console.log('\nAR Config found:', arConfig ? 'YES' : 'NO');
    if (arConfig) {
      console.log('ar_type:', arConfig.ar_type);
      console.log('variants count:', arConfig.variants?.length);
      console.log('First variant:', JSON.stringify(arConfig.variants?.[0], null, 2));
    }
    
    // Test 3: List all ar configs
    const allConfigs = await db.collection('product_ar_configs').countDocuments();
    const lipsConfigs = await db.collection('product_ar_configs').countDocuments({ ar_type: 'LIPS' });
    const cheeksConfigs = await db.collection('product_ar_configs').countDocuments({ ar_type: 'CHEEKS' });
    const eyesConfigs = await db.collection('product_ar_configs').countDocuments({ ar_type: 'EYES' });
    console.log('\nTotal AR configs:', allConfigs);
    console.log('LIPS:', lipsConfigs, '| CHEEKS:', cheeksConfigs, '| EYES:', eyesConfigs);
    
    await mongoose.disconnect();
  } catch(err) {
    console.error(err);
  }
}
test();
