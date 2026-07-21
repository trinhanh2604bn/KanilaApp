const Jimp = require('jimp');
async function run() {
    const imgPath = 'd:\\KanilaApp\\frontend\\app\\src\\main\\assets\\models\\blush.png';
    const image = await Jimp.read(imgPath);
    const w = image.bitmap.width;
    const h = image.bitmap.height;
    
    let maxAlpha = 0;
    image.scan(0, 0, w, h, function(x, y, idx) {
        const a = this.bitmap.data[idx + 3];
        if (a > maxAlpha) maxAlpha = a;
    });
    console.log(`Dimensions: ${w}x${h}, Max Alpha: ${maxAlpha}`);
    
    // Fade out the lower part to create a higher, more "Asian" blush style
    // Boost the alpha by 2.0 to make it more visible
    image.scan(0, 0, w, h, function(x, y, idx) {
        let a = this.bitmap.data[idx + 3];
        if (a > 0) {
            const relY = y / h;
            let multiplier = 1.0;
            
            if (relY > 0.52) {
                // Fade out from y=0.52 to y=0.62
                multiplier = Math.max(0, 1.0 - (relY - 0.52) / 0.10);
                // Apply a curved fade to make it smoother
                multiplier = multiplier * multiplier; 
            }
            
            // Shift the center of gravity up by boosting upper parts
            if (relY < 0.52 && relY > 0.4) {
                multiplier *= 1.2;
            }
            
            a = Math.min(255, a * multiplier * 1.8);
            this.bitmap.data[idx + 3] = a;
        }
    });
    
    await image.writeAsync(imgPath);
    console.log('Modified blush.png saved.');
}
run();
