/**
 * audit-db-collections.js
 * Lists all MongoDB Atlas collections, document counts,
 * and maps them to current Mongoose model files.
 * Run: node scripts/audit-db-collections.js
 */

require('dotenv').config({ path: require('path').join(__dirname, '..', '.env') });
const mongoose = require('mongoose');
const path = require('path');
const fs = require('fs');

const MONGO_URI = process.env.MONGO_URI || process.env.MONGODB_URI;

// ── 1. Build model registry from model files ─────────────────────────────────
function buildModelRegistry() {
  const modelsDir = path.join(__dirname, '..', 'models');
  const files = fs.readdirSync(modelsDir).filter(f => f.endsWith('.js'));
  const registry = [];

  for (const f of files) {
    const content = fs.readFileSync(path.join(modelsDir, f), 'utf8');
    const modelNameMatch = content.match(/mongoose\.model\(['"]([^'"]+)['"]/);
    const collectionMatch = content.match(/collection:\s*['"]([^'"]+)['"]/);

    let inferredCollection = null;
    if (modelNameMatch) {
      // Mongoose default pluralization (simplified)
      const name = modelNameMatch[1];
      inferredCollection = name.toLowerCase() + 's';
      // Basic irregular plural rules
      if (name.endsWith('y') && !name.endsWith('ey') && !name.endsWith('ay') && !name.endsWith('oy')) {
        inferredCollection = name.slice(0, -1).toLowerCase() + 'ies';
      }
      if (name.endsWith('s') || name.endsWith('x') || name.endsWith('z')) {
        inferredCollection = name.toLowerCase() + 'es';
      }
      // Mongoose also converts camelCase to lowercase plural
      // e.g. OrderItem -> orderitems (Mongoose default), OrderItem with explicit -> order_items
    }

    registry.push({
      file: f,
      modelName: modelNameMatch ? modelNameMatch[1] : null,
      explicitCollection: collectionMatch ? collectionMatch[1] : null,
      canonicalCollection: collectionMatch ? collectionMatch[1] : null, // will fall back to Mongoose inferred
    });
  }
  return registry;
}

// ── 2. Get actual Mongoose inferred collection for each model ─────────────────
async function getMongooseCollections(registry) {
  // Load each model and ask mongoose what collection it uses
  const modelsDir = path.join(__dirname, '..', 'models');
  const results = [];

  for (const entry of registry) {
    if (!entry.modelName) {
      results.push({ ...entry, mongooseCollection: null, error: 'No model name found' });
      continue;
    }
    try {
      const modelPath = path.join(modelsDir, entry.file);
      const model = require(modelPath);
      if (model && model.collection) {
        entry.mongooseCollection = model.collection.collectionName;
      } else {
        entry.mongooseCollection = null;
      }
    } catch (err) {
      entry.mongooseCollection = null;
      entry.error = err.message;
    }
    results.push(entry);
  }
  return results;
}

async function main() {
  console.log('Connecting to MongoDB Atlas...');
  await mongoose.connect(MONGO_URI, { serverSelectionTimeoutMS: 15000 });
  console.log('Connected.\n');

  const db = mongoose.connection.db;

  // ── 3. Get all actual collections from the database ──────────────────────────
  const rawCollections = await db.listCollections().toArray();
  const collectionNames = rawCollections.map(c => c.name).sort();

  console.log(`=== ACTUAL MONGODB COLLECTIONS (${collectionNames.length} total) ===\n`);

  // Count documents in each collection
  const collectionData = [];
  for (const name of collectionNames) {
    const count = await db.collection(name).countDocuments();
    collectionData.push({ name, count });
    console.log(`  ${name}: ${count} documents`);
  }

  // ── 4. Build model registry ───────────────────────────────────────────────────
  console.log('\n=== LOADING MONGOOSE MODELS ===\n');
  const registry = buildModelRegistry();
  const modelEntries = await getMongooseCollections(registry);

  console.log(`Total model files: ${registry.length}`);
  console.log('\nModel → Collection mapping:');
  for (const e of modelEntries) {
    const col = e.mongooseCollection || e.explicitCollection || '(unknown)';
    const explicit = e.explicitCollection ? '[explicit]' : '[inferred]';
    if (e.error) {
      console.log(`  ${e.file}: ERROR - ${e.error}`);
    } else {
      console.log(`  ${e.modelName || '(no model)'} → ${col} ${explicit}`);
    }
  }

  // ── 5. Cross-reference ────────────────────────────────────────────────────────
  console.log('\n=== CROSS-REFERENCE ANALYSIS ===\n');

  const modelCollectionNames = new Set(
    modelEntries
      .filter(e => e.mongooseCollection)
      .map(e => e.mongooseCollection)
  );

  // Collections in DB but not in any model
  const orphanCollections = collectionData.filter(c => !modelCollectionNames.has(c.name));
  console.log(`Collections in DB with NO matching Mongoose model (${orphanCollections.length}):`);
  for (const c of orphanCollections) {
    console.log(`  !! ORPHAN: ${c.name} (${c.count} docs)`);
  }

  // Models with collections not yet in DB (no data)
  console.log(`\nMongoose models whose collections are NOT yet in DB (empty/uncreated):`);
  for (const e of modelEntries) {
    if (e.mongooseCollection && !collectionNames.includes(e.mongooseCollection)) {
      console.log(`  -- MISSING IN DB: ${e.modelName} → ${e.mongooseCollection}`);
    }
  }

  // Models with collections in DB but empty
  console.log(`\nMongoose models whose DB collection exists but is EMPTY (0 docs):`);
  for (const e of modelEntries) {
    if (e.mongooseCollection) {
      const found = collectionData.find(c => c.name === e.mongooseCollection);
      if (found && found.count === 0) {
        console.log(`  00 EMPTY: ${e.modelName} → ${e.mongooseCollection}`);
      }
    }
  }

  // ── 6. Output full JSON for further analysis ──────────────────────────────────
  const fullReport = {
    databaseName: db.databaseName,
    totalCollectionsInDB: collectionData.length,
    totalModelFiles: registry.length,
    collectionData,
    modelEntries,
    orphanCollections,
  };

  const reportPath = path.join(__dirname, '..', 'scripts', 'db-audit-raw.json');
  fs.writeFileSync(reportPath, JSON.stringify(fullReport, null, 2));
  console.log(`\nFull raw report saved to: ${reportPath}`);

  await mongoose.disconnect();
  console.log('Done.');
}

main().catch(err => {
  console.error('Audit failed:', err.message);
  process.exit(1);
});
