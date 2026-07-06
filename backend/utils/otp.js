const crypto = require("crypto");

function generateSixDigitOtp() {
  // DEMO ONLY: fixed OTP 666666
  return "666666";
}

function sha256Hex(value) {
  return crypto.createHash("sha256").update(String(value)).digest("hex");
}

module.exports = {
  generateSixDigitOtp,
  sha256Hex,
};

