const fs = require('fs');
const path = require('path');
const expressListEndpoints = require('express-list-endpoints');
const app = require('../app.js'); // Assuming app.js exports the express app

const endpoints = expressListEndpoints(app);

let markdown = '# API Endpoints\n\n';
markdown += '| Method | Path | Middlewares |\n';
markdown += '|---|---|---|\n';

endpoints.forEach(endpoint => {
  endpoint.methods.forEach(method => {
    // endpoint.middlewares is an array of middleware function names
    const middlewares = endpoint.middlewares.join(', ');
    markdown += `| ${method} | \`${endpoint.path}\` | ${middlewares} |\n`;
  });
});

const outputPath = path.join(__dirname, '..', 'api-docs.md');
fs.writeFileSync(outputPath, markdown);
console.log('API documentation generated at:', outputPath);
