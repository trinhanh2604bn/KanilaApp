const { MongoClient } = require('mongodb');
async function run() {
  const client = new MongoClient('mongodb://127.0.0.1:27017');
  await client.connect();
  const db = client.db('kanila');
  const configs = await db.collection('productarconfigs').find({ ar_type: 'CHEEKS' }).toArray();
  for (const config of configs) {
    if (config.variants) {
      for (const v of config.variants) {
        v.opacity = 0.85;
      }
      await db.collection('productarconfigs').updateOne({ _id: config._id }, { $set: { variants: config.variants } });
    }
  }
  console.log('Updated CHEEKS opacity to 0.85 in database');
  await client.close();
}
run().catch(console.dir);
