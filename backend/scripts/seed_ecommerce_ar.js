const mongoose = require('mongoose');

async function seed() {
  try {
    await mongoose.connect('mongodb://127.0.0.1:27017/kanilabeauty');
    console.log('Connected to MongoDB');
    const db = mongoose.connection.db;

    // 1. Create a Product
    const productId = new mongoose.Types.ObjectId();
    const product = {
      _id: productId,
      name: 'AR Try-On Matte Lipstick',
      category: 'makeup',
      subcategory: 'lipstick',
      brand: 'Kanila',
      hasAr: true,
      price: 250000,
      variants: [
        {
          _id: new mongoose.Types.ObjectId(),
          name: 'Ruby Red',
          sku: 'AR-LIP-01',
          price: 250000,
          stock: 100,
          colorCode: '#E0115F',
          imageUrl: 'https://placehold.co/100x100/E0115F/FFFFFF?text=Ruby'
        },
        {
          _id: new mongoose.Types.ObjectId(),
          name: 'Coral Pink',
          sku: 'AR-LIP-02',
          price: 250000,
          stock: 50,
          colorCode: '#F88379',
          imageUrl: 'https://placehold.co/100x100/F88379/FFFFFF?text=Coral'
        },
        {
          _id: new mongoose.Types.ObjectId(),
          name: 'Plum Wine',
          sku: 'AR-LIP-03',
          price: 260000,
          stock: 0,
          colorCode: '#8E4585',
          imageUrl: 'https://placehold.co/100x100/8E4585/FFFFFF?text=Plum'
        }
      ],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    
    await db.collection('products').insertOne(product);
    console.log('Inserted product: ' + productId);

    // 2. Create an AR Config for the product
    const arConfig = {
      product_id: productId,
      status: 'active',
      ar_type: 'LIPS',
      renderer_version: 'v2',
      variants: product.variants.map(v => ({
        variant_id: v._id.toString(),
        sku: v.sku,
        variant_name: v.name,
        shade_hex: v.colorCode,
        finish_type: 'MATTE',
        opacity: 0.7,
        price: v.price,
        currency_code: 'VND',
        in_stock: v.stock > 0,
        thumbnail_url: v.imageUrl,
        enabled: true
      })),
      createdAt: new Date(),
      updatedAt: new Date()
    };

    await db.collection('product_ar_configs').insertOne(arConfig);
    console.log('Inserted AR config for product: ' + productId);

    await mongoose.disconnect();
    console.log('Done.');
  } catch (err) {
    console.error(err);
  }
}

seed();
