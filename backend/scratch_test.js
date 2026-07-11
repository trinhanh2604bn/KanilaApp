require('dotenv').config();
const { generateSkinAnalysis } = require('./services/gemini.provider');

(async () => {
  const profile = {
    skin_type: 'Da dầu',
    skin_concerns: ['Mụn', 'Thâm'],
    sensitivity_level: 'Nhạy cảm',
    beauty_goals: 'Giảm mụn, mờ thâm',
    avoid_ingredients: ['Cồn']
  };
  const products = [{ name: 'Toner BHA' }, { name: 'Serum Niacinamide' }];
  
  try {
    const result = await generateSkinAnalysis(profile, products);
    console.log('Result:', result);
  } catch(e) {
    console.error('Error:', e);
  }
})();
