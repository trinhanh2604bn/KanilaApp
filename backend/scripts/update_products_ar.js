const mongoose = require('mongoose');

async function seed() {
  try {
    await mongoose.connect('mongodb://127.0.0.1:27017/kanilabeauty');
    console.log('Connected to MongoDB');
    
    // update products
    const db = mongoose.connection.db;
    const result = await db.collection('products').updateMany(
      {},
      { $set: { hasAr: true } }
    );
    console.log('Updated products: ' + result.modifiedCount);
    
    await mongoose.disconnect();
    console.log('Disconnected');
  } catch (err) {
    console.error(err);
  }
}
seed();
