const express = require("express");
const cors = require("cors");
const path = require("path");
const routes = require("./routes");
const notFound = require("./middlewares/notFound.middleware");
const errorHandler = require("./middlewares/error.middleware");

const app = express();

// Common middlewares
app.use(cors());
// Allow larger payloads for base64 imageUrl submitted from the admin product form.
// The product form sends base64 in JSON; large images can exceed Express' default limit.
app.use(express.json({ limit: "50mb" }));
app.use(express.urlencoded({ extended: true, limit: "50mb" }));

// Static files
app.use("/kanila", express.static(path.join(__dirname, "public/kanila")));

// Fallback for missing product images to avoid 404 in Glide
app.get("/kanila/products/*", (req, res) => {
  // Return a generic placeholder if the specific image is not found
  // You can also send a local file: res.sendFile(path.join(__dirname, "public/placeholder.jpg"));
  res.redirect("https://placehold.co/600x600?text=Kanila+Product");
});

// Health check endpoint
app.get("/api/health", async (req, res) => {
  const mongoose = require("mongoose");
  const isConnected = mongoose.connection.readyState === 1;
  let diagnostics = {
    database: isConnected ? "connected" : "disconnected",
    dbName: isConnected ? mongoose.connection.name : null,
    productCount: 0,
    brandCount: 0,
    categoryCount: 0,
    error: null
  };

  if (isConnected) {
    try {
      diagnostics.productCount = await mongoose.connection.db.collection("products").countDocuments();
      diagnostics.brandCount = await mongoose.connection.db.collection("brands").countDocuments();
      diagnostics.categoryCount = await mongoose.connection.db.collection("categories").countDocuments();
    } catch (e) {
      diagnostics.error = e.message;
    }
  }

  res.json({
    status: "ok",
    diagnostics,
    timestamp: new Date().toISOString()
  });
});

// Direct POST handler for accounts (bypasses router chain)
const { createAccount: createAccountHandler } = require("./controllers/account.controller");
app.post("/api/accounts", createAccountHandler);

// Mount routes
app.use("/api", routes);

// Not found middleware
app.use(notFound);

// Error handling middleware
app.use(errorHandler);

module.exports = app;
