const mongoose = require("mongoose");

const connectDB = async () => {
  try {
    const mongoUri = process.env.MONGO_URI || process.env.MONGODB_URI;
    if (!mongoUri) {
      throw new Error("MONGO_URI is missing in .env file");
    }

    const conn = await mongoose.connect(mongoUri, {
      dbName: process.env.MONGO_DB_NAME || "kanila",
      connectTimeoutMS: 10000, // 10 seconds timeout
    });
    console.log(`Connected to MongoDB: ${mongoose.connection.name}`);
    console.log(`Host: ${mongoose.connection.host}`);

    // Graceful shutdown handling
    process.on("SIGINT", async () => {
      await mongoose.connection.close();
      console.log("MongoDB connection closed through app termination (SIGINT)");
      process.exit(0);
    });

    process.on("SIGTERM", async () => {
      await mongoose.connection.close();
      console.log("MongoDB connection closed through app termination (SIGTERM)");
      process.exit(0);
    });

    return conn;
  } catch (error) {
    let message = "MongoDB connection error";
    if (error.message.includes("ETIMEOUT")) message += ": Connection timed out";
    else if (error.message.includes("ECONNREFUSED")) message += ": Connection refused";
    else if (error.message.includes("Authentication failed")) message += ": Invalid credentials";

    console.error(`${message}. Ensure MONGO_URI is correct and IP is whitelisted.`);
    process.exit(1);
  }
};

module.exports = connectDB;
