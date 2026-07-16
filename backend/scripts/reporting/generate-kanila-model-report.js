const fs = require('fs');
const path = require('path');
const mongoose = require('mongoose');
const ExcelJS = require('exceljs');

const BASE_DIR = path.resolve(__dirname, '../../');
const DOCS_DIR = path.join(BASE_DIR, 'docs', 'database-model');

if (!fs.existsSync(DOCS_DIR)) {
  fs.mkdirSync(DOCS_DIR, { recursive: true });
}

const data = {
  summary: {},
  models: [],
  fields: [],
  relationships: [],
  indexes: [],
  enums: [],
  embedded_schemas: [],
  virtuals: [],
  hooks_plugins: [],
  domain_groups: [],
  audit_issues: [],
  source_coverage: []
};

// 1. Find Model Files
function findModelFiles(dir, fileList = []) {
  if (!fs.existsSync(dir)) return fileList;
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);
    if (stat.isDirectory()) {
      if (!['node_modules', '.git', 'public', 'tests', 'scripts', 'docs'].includes(file)) {
        findModelFiles(fullPath, fileList);
      }
    } else if (file.endsWith('.js')) {
      const normalizedPath = fullPath.replace(/\\/g, '/');
      if (normalizedPath.includes('/models/') || normalizedPath.includes('/schemas/')) {
        fileList.push(fullPath);
      }
    }
  }
  return fileList;
}

const allFiles = findModelFiles(BASE_DIR);

function determineDomain(modelName) {
  const name = modelName.toLowerCase();
  if (name.includes('account') || name.includes('customer') || name.includes('role') || name.includes('permission') || name.includes('admin') || name.includes('auth') || name.includes('otp')) {
    if (name.includes('role') || name.includes('permission') || name.includes('admin') || name.includes('auth') || name.includes('otp')) return 'Auth & Access Control';
    return 'Account & Customer';
  }
  if (name.includes('beautyprofile') || name.includes('skinmatch') || name.includes('recommendation')) return 'Beauty Profile & Recommendation';
  if (name.includes('product') || name.includes('variant') || name.includes('option') || name.includes('attribute') || name.includes('brand') || name.includes('category')) {
    if (name.includes('category') || name.includes('brand')) return 'Category & Brand';
    if (name.includes('variant') || name.includes('option')) return 'Product Variant & Option';
    return 'Product & Catalog';
  }
  if (name.includes('price') || name.includes('promotion') || name.includes('coupon')) {
    if (name.includes('price')) return 'Pricing';
    return 'Promotion & Coupon';
  }
  if (name.includes('inventory') || name.includes('warehouse') || name.includes('stock')) return 'Inventory & Warehouse';
  if (name.includes('cart')) return 'Cart';
  if (name.includes('checkout')) return 'Checkout';
  if (name.includes('order')) return 'Order';
  if (name.includes('payment') || name.includes('refund')) return 'Payment';
  if (name.includes('ship') || name.includes('return')) {
    if (name.includes('return')) return 'Return & Refund';
    return 'Shipping & Fulfillment';
  }
  if (name.includes('review') || name.includes('rating') || name.includes('vote')) return 'Review & Rating';
  if (name.includes('wishlist')) return 'Wishlist';
  if (name.includes('loyalty')) return 'Loyalty';
  if (name.includes('community') || name.includes('reels') || name.includes('challenge')) return 'Community';
  if (name.includes('support') || name.includes('ticket')) return 'Support & Ticket';
  if (name.includes('audit') || name.includes('log')) return 'Audit & System Logs';
  return 'OTHER_UNCLASSIFIED';
}

function parseSchema(schema, modelName, collectionName, sourceFile) {
  const domain = determineDomain(modelName);
  
  // Basic Fields
  const paths = schema.paths;
  let fieldCount = 0;
  let relCount = 0;
  
  for (const [pathName, pathObj] of Object.entries(paths)) {
    fieldCount++;
    const instance = pathObj.instance;
    const isArray = instance === 'Array';
    const isEmbedded = pathObj.schema != null;
    let elementType = null;
    let ref = pathObj.options && pathObj.options.ref;
    let refPath = pathObj.options && pathObj.options.refPath;
    
    if (isArray && pathObj.$isMongooseDocumentArray) {
       elementType = 'Embedded';
    } else if (isArray && pathObj.caster) {
       elementType = pathObj.caster.instance;
       ref = ref || pathObj.caster.options?.ref;
       refPath = refPath || pathObj.caster.options?.refPath;
    }

    const unique = !!(pathObj._index && pathObj._index.unique);
    const required = !!pathObj.isRequired;
    
    let sensitive = false;
    const lowerName = pathName.toLowerCase();
    if (lowerName.includes('password') || lowerName.includes('token') || lowerName.includes('secret') || lowerName.includes('otp')) {
      sensitive = true;
      if (pathObj.options && pathObj.options.select !== false) {
        data.audit_issues.push({
          issue_id: `ISSUE_${data.audit_issues.length + 1}`,
          severity: 'HIGH',
          category: 'SECURITY',
          model_name: modelName,
          collection_name: collectionName,
          field_name: pathName,
          description: 'Sensitive field without select: false',
          evidence: `Field ${pathName} appears to be sensitive but doesn't have select: false`,
          impact: 'Potential data leak in API responses',
          recommendation: 'Add select: false to schema definition',
          source_file: sourceFile,
          source_line: '',
          confidence: 'HIGH'
        });
      }
    }

    data.fields.push({
      domain,
      model_name: modelName,
      collection_name: collectionName,
      field_name: pathName,
      field_path: pathName,
      data_type: instance,
      element_type: elementType,
      is_array: isArray,
      is_embedded: isEmbedded,
      embedded_schema_name: isEmbedded ? `${modelName}_${pathName}_Schema` : '',
      required,
      nullable: !required,
      unique,
      sparse: !!(pathObj._index && pathObj._index.sparse),
      indexed: !!pathObj._index,
      immutable: !!pathObj.options?.immutable,
      select: pathObj.options?.select,
      default_value: typeof pathObj.defaultValue === 'function' ? 'function' : JSON.stringify(pathObj.defaultValue),
      enum_values: pathObj.enumValues && pathObj.enumValues.length > 0 ? pathObj.enumValues.join(' | ') : '',
      min_value: pathObj.options?.min,
      max_value: pathObj.options?.max,
      min_length: pathObj.options?.minlength,
      max_length: pathObj.options?.maxlength,
      match_pattern: pathObj.options?.match?.toString(),
      trim: !!pathObj.options?.trim,
      lowercase: !!pathObj.options?.lowercase,
      uppercase: !!pathObj.options?.uppercase,
      reference_model: ref,
      reference_path: '',
      ref_path: refPath,
      sensitive_data: sensitive ? 'YES' : 'NO',
      source_file: sourceFile,
    });
    
    if (pathObj.enumValues && pathObj.enumValues.length > 0) {
      data.enums.push({
        model_name: modelName,
        field_name: pathName,
        values: pathObj.enumValues.join(', ')
      });
    }

    if (ref || refPath) {
      relCount++;
      data.relationships.push({
        relationship_id: `REL_${data.relationships.length + 1}`,
        domain,
        source_model: modelName,
        source_collection: collectionName,
        source_field: pathName,
        target_model: ref || refPath,
        relationship_type: refPath ? 'POLYMORPHIC' : 'REFERENCE',
        cardinality: isArray ? 'ONE_TO_MANY' : (unique ? 'ONE_TO_ONE' : 'MANY_TO_ONE'),
        source_optional: !required,
        implementation_type: 'ObjectId',
        confidence: 'CONFIRMED',
        source_file: sourceFile
      });
    }
  }

  // Virtuals
  for (const [virtName, virtObj] of Object.entries(schema.virtuals)) {
    if (virtName === 'id') continue;
    data.virtuals.push({
      model_name: modelName,
      virtual_name: virtName,
      ref: virtObj.options?.ref,
      localField: virtObj.options?.localField,
      foreignField: virtObj.options?.foreignField,
      justOne: virtObj.options?.justOne
    });
    
    if (virtObj.options?.ref) {
      relCount++;
      data.relationships.push({
        relationship_id: `REL_${data.relationships.length + 1}`,
        domain,
        source_model: modelName,
        source_collection: collectionName,
        source_field: virtName,
        target_model: virtObj.options.ref,
        relationship_type: 'VIRTUAL_POPULATE',
        cardinality: virtObj.options.justOne ? 'ONE_TO_ONE' : 'ONE_TO_MANY',
        source_optional: true,
        implementation_type: 'Virtual',
        confidence: 'CONFIRMED',
        source_file: sourceFile
      });
    }
  }

  // Indexes
  const schemaIndexes = schema.indexes();
  for (const idx of schemaIndexes) {
    data.indexes.push({
      model_name: modelName,
      collection_name: collectionName,
      index_keys: JSON.stringify(idx[0]),
      options: JSON.stringify(idx[1])
    });
  }

  const ts = schema.options.timestamps;
  
  data.models.push({
    domain,
    model_name: modelName,
    model_file: sourceFile,
    collection_name: collectionName,
    collection_naming_source: schema.options.collection ? 'EXPLICIT' : 'MONGOOSE_INFERRED',
    status: 'ACTIVE',
    purpose: '',
    primary_key: '_id',
    field_count: fieldCount,
    relationship_count: relCount,
    index_count: schemaIndexes.length,
    timestamps: !!ts,
    created_field: ts && ts.createdAt ? ts.createdAt : (ts ? 'createdAt' : ''),
    updated_field: ts && ts.updatedAt ? ts.updatedAt : (ts ? 'updatedAt' : ''),
    version_key: schema.options.versionKey !== false ? schema.options.versionKey || '__v' : '',
    has_embedded_schema: Object.values(paths).some(p => p.schema != null),
    has_virtuals: Object.keys(schema.virtuals).length > 1,
    has_hooks: schema.s && schema.s.hooks ? Object.keys(schema.s.hooks._pres).length > 0 : false,
    has_plugins: schema.plugins && schema.plugins.length > 0,
    analysis_method: 'RUNTIME',
    notes: ''
  });
  
  data.domain_groups.push({
    domain,
    model: modelName,
    collection: collectionName
  });
}

function analyzeStatic(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');
  let modelNameMatch = content.match(/mongoose\.model\(['"]([^'"]+)['"]/);
  let modelName = modelNameMatch ? modelNameMatch[1] : path.basename(filePath, '.js');
  let collectionMatch = content.match(/collection:\s*['"]([^'"]+)['"]/);
  
  data.models.push({
    domain: determineDomain(modelName),
    model_name: modelName,
    model_file: filePath,
    collection_name: collectionMatch ? collectionMatch[1] : `${modelName.toLowerCase()}s`,
    collection_naming_source: collectionMatch ? 'EXPLICIT' : 'MONGOOSE_INFERRED',
    status: 'ACTIVE',
    analysis_method: 'STATIC_ANALYSIS_ONLY',
    notes: 'Failed to load module at runtime'
  });
  
  data.audit_issues.push({
    issue_id: `ISSUE_${data.audit_issues.length + 1}`,
    severity: 'MEDIUM',
    category: 'RUNTIME_ERROR',
    model_name: modelName,
    collection_name: '',
    field_name: '',
    description: 'Model file could not be required in isolation',
    evidence: filePath,
    impact: 'Schema may be partially analyzed',
    recommendation: 'Remove side-effects from model file initialization',
    source_file: filePath,
    source_line: '',
    confidence: 'HIGH'
  });
}

async function run() {
  console.log(`Found ${allFiles.length} potential model files.`);
  
  let successCount = 0;
  let failCount = 0;
  
  for (const file of allFiles) {
    try {
      const relativePath = path.relative(BASE_DIR, file);
      const mod = require(file);
      // Mongoose models export either directly or inside an object
      let model = mod;
      if (mod && mod.schema) {
        model = mod;
      } else if (mod && mod.default && mod.default.schema) {
        model = mod.default;
      } else if (mod && typeof mod === 'object') {
        const key = Object.keys(mod).find(k => mod[k] && mod[k].schema);
        if (key) model = mod[key];
      }
      
      if (model && model.schema && model.modelName) {
        parseSchema(model.schema, model.modelName, model.collection.name, relativePath);
        successCount++;
        data.source_coverage.push({ file: relativePath, status: 'LOADED' });
      } else {
        // Might be just exporting a schema
        if (model && model instanceof mongoose.Schema) {
          const modelName = path.basename(file, '.js').replace('.model', '');
          parseSchema(model, modelName, `${modelName}s`, relativePath);
          successCount++;
          data.source_coverage.push({ file: relativePath, status: 'SCHEMA_ONLY' });
        } else {
          // Fallback to static
          analyzeStatic(file);
          failCount++;
          data.source_coverage.push({ file: relativePath, status: 'STATIC_FALLBACK' });
        }
      }
    } catch (e) {
      console.warn(`Failed to require ${file}: ${e.message}`);
      analyzeStatic(file);
      failCount++;
      data.source_coverage.push({ file: path.relative(BASE_DIR, file), status: 'STATIC_FALLBACK', error: e.message });
    }
  }

  // Cross-reference issues
  const modelNames = data.models.map(m => m.model_name);
  for (const rel of data.relationships) {
    if (!modelNames.includes(rel.target_model) && rel.target_model && rel.relationship_type !== 'POLYMORPHIC') {
      data.audit_issues.push({
        issue_id: `ISSUE_${data.audit_issues.length + 1}`,
        severity: 'HIGH',
        category: 'MISSING_TARGET_MODEL',
        model_name: rel.source_model,
        collection_name: rel.source_collection,
        field_name: rel.source_field,
        description: `Reference to non-existent model: ${rel.target_model}`,
        evidence: `Ref in ${rel.source_model}.${rel.source_field} points to ${rel.target_model}`,
        impact: 'Populate will fail, referential integrity issues',
        recommendation: `Ensure model ${rel.target_model} is defined or fix spelling`,
        source_file: rel.source_file,
        source_line: '',
        confidence: 'HIGH'
      });
    }
  }

  // Generate JSON
  const jsonOut = path.join(DOCS_DIR, 'kanila-model-metadata.json');
  fs.writeFileSync(jsonOut, JSON.stringify(data, null, 2));
  console.log(`Wrote JSON to ${jsonOut}`);

  // Generate DBML
  const dbmlLines = [];
  data.models.forEach(m => {
    dbmlLines.push(`Table ${m.model_name} {`);
    const mFields = data.fields.filter(f => f.model_name === m.model_name);
    mFields.forEach(f => {
      let typeStr = f.data_type === 'ObjectID' ? 'ObjectId' : f.data_type;
      if (f.is_array) typeStr = `Array<${f.element_type || 'Mixed'}>`;
      let constraints = [];
      if (f.primary_key || f.field_name === '_id') constraints.push('primary key');
      if (f.unique) constraints.push('unique');
      if (f.reference_model) constraints.push(`ref: > ${f.reference_model}._id`);
      let note = f.is_embedded ? ' [note: "Embedded"]' : '';
      dbmlLines.push(`  ${f.field_name} ${typeStr} ${constraints.length ? '[' + constraints.join(', ') + ']' : ''}${note}`);
    });
    dbmlLines.push(`}`);
    dbmlLines.push('');
  });
  const dbmlOut = path.join(DOCS_DIR, 'KANILA_ERD.dbml');
  fs.writeFileSync(dbmlOut, dbmlLines.join('\n'));
  console.log(`Wrote DBML to ${dbmlOut}`);

  // Generate Mermaid
  const mmdLines = ['erDiagram'];
  data.models.forEach(m => {
    mmdLines.push(`  ${m.model_name} {`);
    const mFields = data.fields.filter(f => f.model_name === m.model_name && f.field_name !== '__v' && !f.field_name.includes('.'));
    mFields.forEach(f => {
      mmdLines.push(`    ${f.data_type} ${f.field_name}`);
    });
    mmdLines.push(`  }`);
  });
  data.relationships.forEach(r => {
    if (r.target_model && r.relationship_type !== 'POLYMORPHIC' && modelNames.includes(r.target_model)) {
      let link = r.cardinality === 'ONE_TO_MANY' ? '||--o{' : '||--o|';
      mmdLines.push(`  ${r.source_model} ${link} ${r.target_model} : "${r.source_field}"`);
    }
  });
  const mmdOut = path.join(DOCS_DIR, 'KANILA_ERD.mmd');
  fs.writeFileSync(mmdOut, mmdLines.join('\n'));
  console.log(`Wrote Mermaid to ${mmdOut}`);

  // Generate Excel
  const workbook = new ExcelJS.Workbook();
  const writeSheet = (name, rows) => {
    const sheet = workbook.addWorksheet(name);
    if (rows.length > 0) {
      sheet.columns = Object.keys(rows[0]).map(k => ({ header: k, key: k, width: 25 }));
      sheet.addRows(rows);
      sheet.autoFilter = {
        from: { row: 1, column: 1 },
        to: { row: 1, column: Object.keys(rows[0]).length }
      };
      sheet.getRow(1).font = { bold: true };
      sheet.views = [{ state: 'frozen', ySplit: 1 }];
    }
  };

  const readmeRows = [
    { Sheet: 'README', Description: 'This file contains Kanila Beauty Commerce DB schema definitions.' },
    { Sheet: 'MODEL_SUMMARY', Description: 'List of all MongoDB models and basic stats' },
    { Sheet: 'FIELD_DICTIONARY', Description: 'Comprehensive list of all fields across all models' },
    { Sheet: 'RELATIONSHIPS', Description: 'Cross-model references (refs, virtuals)' },
    { Sheet: 'INDEXES', Description: 'Database indexes declared in schemas' },
    { Sheet: 'ENUMS', Description: 'Allowed enum values for fields' },
    { Sheet: 'VIRTUALS', Description: 'Virtual fields in schemas' },
    { Sheet: 'AUDIT_ISSUES', Description: 'Issues identified during schema static & runtime analysis' },
    { Sheet: 'DOMAIN_GROUPS', Description: 'Categorization of models by business domain' },
    { Sheet: 'SOURCE_COVERAGE', Description: 'Log of files analyzed and success status' }
  ];

  writeSheet('README', readmeRows);
  writeSheet('MODEL_SUMMARY', data.models);
  writeSheet('FIELD_DICTIONARY', data.fields);
  writeSheet('RELATIONSHIPS', data.relationships);
  writeSheet('INDEXES', data.indexes);
  writeSheet('ENUMS', data.enums);
  writeSheet('VIRTUALS', data.virtuals);
  writeSheet('AUDIT_ISSUES', data.audit_issues);
  writeSheet('DOMAIN_GROUPS', data.domain_groups);
  writeSheet('SOURCE_COVERAGE', data.source_coverage);
  
  const excelOut = path.join(DOCS_DIR, 'KANILA_MODEL_RELATIONSHIP_MASTER.xlsx');
  await workbook.xlsx.writeFile(excelOut);
  console.log(`Wrote Excel to ${excelOut}`);

  // Generate MD Report
  const mdOut = path.join(DOCS_DIR, 'KANILA_MODEL_AUDIT_REPORT.md');
  const mdContent = `
# KanilaApp Model Audit Report

## 1. Executive Summary
This report summarizes the introspection of the KanilaApp MongoDB Mongoose models.

## 2. Summary Stats
- Total Model Files Scanned: ${allFiles.length}
- Models Successfully Analyzed (Runtime): ${successCount}
- Models Analyzed via Static Fallback: ${failCount}
- Total Models Discovered: ${data.models.length}
- Total Fields Discovered: ${data.fields.length}
- Total Relationships: ${data.relationships.length}
- Total Indexes: ${data.indexes.length}
- Total Issues Identified: ${data.audit_issues.length}

## 3. Issues by Severity
${data.audit_issues.map(i => `- [${i.severity}] ${i.model_name}.${i.field_name}: ${i.description}`).join('\n')}

## 4. Usage Instructions
- **ERD Drawing**: Import \`KANILA_ERD.dbml\` into [dbdiagram.io](https://dbdiagram.io).
- **Mermaid Graph**: Copy \`KANILA_ERD.mmd\` into any Markdown viewer that supports Mermaid or [mermaid.live](https://mermaid.live).
- **Detailed Schema**: View \`KANILA_MODEL_RELATIONSHIP_MASTER.xlsx\`.
  `;
  fs.writeFileSync(mdOut, mdContent.trim());
  console.log(`Wrote MD Report to ${mdOut}`);

  console.log('Successfully completed model report generation.');
}

run().catch(console.error);
