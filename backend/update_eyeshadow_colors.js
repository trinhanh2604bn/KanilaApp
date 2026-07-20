const fs = require('fs');
let data = fs.readFileSync('d:/KanilaApp/backend/dataAR_mock_real_ids.js', 'utf8');

let lines = data.split('\n');
let inEyes = false;
let colorIndex = 0;
const colors = ['#FF6B6B', '#FF8C94', '#E84A5F', '#FF7F50', '#FF4500']; 

for(let i=0; i<lines.length; i++) {
    if (lines[i].includes('"ar_type": "EYES"')) {
        inEyes = true;
    } else if (lines[i].includes('"ar_type":')) {
        inEyes = false;
    }
    
    if (inEyes && lines[i].includes('"shade_hex":')) {
        let currentHex = lines[i].match(/"shade_hex":\s*"([^"]+)"/)[1];
        let newColor = colors[colorIndex % colors.length];
        lines[i] = lines[i].replace(currentHex, newColor);
        colorIndex++;
    }
}
fs.writeFileSync('d:/KanilaApp/backend/dataAR_mock_real_ids.js', lines.join('\n'));
console.log('Done updating eyeshadow colors');
