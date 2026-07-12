require("dotenv").config();
const app = require("./app");
const connectDB = require("./config/db");

const PORT = process.env.PORT || 5000;

// Connect to MongoDB and start the server
connectDB().then(async () => {
  // Remove uniqueness enforcement on productCode (see request to remove
  // "product code already exists" behavior).
  const dropProductCodeUniqueIndex = require("./config/dropProductCodeUniqueIndex");
  await dropProductCodeUniqueIndex();

  // Ensure default admin account exists
  const ensureAdminAccount = require("./config/initAdmin");
  await ensureAdminAccount();

  // [TEMPORARY MIGRATION] Fix products with empty/null slugs
  try {
    const Product = require("./models/product.model");
    const productsToFix = await Product.find({ $or: [{ slug: "" }, { slug: null }, { slug: { $exists: false } }] });
    if (productsToFix.length > 0) {
      console.log(`[MIGRATION] Fixing ${productsToFix.length} products with missing/empty slugs...`);
      for (const p of productsToFix) {
        try {
          p.slug = undefined; // Trigger generation in pre-save hook
          await p.save();
        } catch (err) {
          console.warn(`[MIGRATION] Failed to fix slug for product ${p._id}:`, err.message);
        }
      }
      console.log("[MIGRATION] Slug fix complete.");
    }
  } catch (err) {
    console.error("[MIGRATION] Failed during slug migration:", err.message);
  }

  // app.listen(PORT, () => {
  //   console.log(`Server is running on port ${PORT}`);
  // });

  const reviewAiWorker = require("./cron/reviewAiWorker");
  reviewAiWorker.start();


  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running at http://0.0.0.0:${PORT}`);
  });
});

