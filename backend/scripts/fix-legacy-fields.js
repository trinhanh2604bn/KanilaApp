const fs = require('fs');
const path = require('path');

const servicesDir = path.join(__dirname, '../services');

const replacements = [
  { from: /beautyProfile\.budget_range/g, to: "beautyProfile.budget" },
  { from: /beautyProfile\.skin_tone/g, to: "beautyProfile.skin_color" },
  { from: /beautyProfile\.undertone/g, to: "beautyProfile.skin_undertone" },
  { from: /beautyProfile\.finish_preference/g, to: "beautyProfile.foundation_finish" },
  { from: /beautyProfile\.lip_color_preference/g, to: "beautyProfile.lipstick_colors" },
  { from: /beautyProfile\.makeup_style/g, to: "beautyProfile.makeup_styles" },

  // Profile object fields in chatbotRecommendation.service.js
  { from: /profile\.budget_range/g, to: "profile.budget" },
  { from: /profile\.skin_tone/g, to: "profile.skin_color" },
  { from: /profile\.undertone/g, to: "profile.skin_undertone" },
  { from: /profile\.finish_preference/g, to: "profile.foundation_finish" },

  // Object keys in recommendationSnapshot.service.js
  { from: /budget_range:/g, to: "budget:" },
  { from: /skin_tone:/g, to: "skin_color:" },
  { from: /undertone:/g, to: "skin_undertone:" },
  { from: /finish_preference:/g, to: "foundation_finish:" },

  // For missing_info array checks
  { from: /"budget_range"/g, to: '"budget"' },
  { from: /"skin_tone"/g, to: '"skin_color"' },
  { from: /"undertone"/g, to: '"skin_undertone"' },
  { from: /"finish_preference"/g, to: '"foundation_finish"' },
];

function walk(dir) {
  let results = [];
  const list = fs.readdirSync(dir);
  list.forEach((file) => {
    file = path.join(dir, file);
    const stat = fs.statSync(file);
    if (stat && stat.isDirectory()) {
      results = results.concat(walk(file));
    } else if (file.endsWith('.js')) {
      results.push(file);
    }
  });
  return results;
}

const files = walk(servicesDir);

files.forEach(file => {
  let content = fs.readFileSync(file, 'utf8');
  let original = content;

  replacements.forEach(r => {
    content = content.replace(r.from, r.to);
  });

  if (content !== original) {
    fs.writeFileSync(file, content, 'utf8');
    console.log(`Updated ${file}`);
  }
});
