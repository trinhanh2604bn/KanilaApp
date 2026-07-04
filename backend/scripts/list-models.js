const fs = require('fs');
const path = require('path');

const modelsDir = path.join(__dirname, '..', 'models');
const files = fs.readdirSync(modelsDir).filter(f => f.endsWith('.js'));
const results = [];

for (const f of files) {
  const content = fs.readFileSync(path.join(modelsDir, f), 'utf8');
  const modelNameMatch = content.match(/mongoose\.model\(['"]([^'"]+)['"]/);
  const collectionMatch = content.match(/collection:\s*['"]([^'"]+)['"]/);
  const refFields = [];
  const refMatches = content.matchAll(/ref:\s*['"]([^'"]+)['"]/g);
  for (const m of refMatches) refFields.push(m[1]);

  results.push({
    file: f,
    modelName: modelNameMatch ? modelNameMatch[1] : 'NOT_FOUND',
    explicitCollection: collectionMatch ? collectionMatch[1] : null,
    refsTo: refFields
  });
}

console.log(JSON.stringify(results, null, 2));
