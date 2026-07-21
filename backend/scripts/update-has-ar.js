const mongoose = require('mongoose');
const path = require('path');
require('dotenv').config({path: '../.env'});
const Product = require('../models/product.model.js');
mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/kanila').then(async () => {
    const res = await Product.updateMany({}, { $set: { hasAr: true } });
    console.log('Updated', res.modifiedCount, 'products');
    mongoose.disconnect();
});
