const jwt = require("jsonwebtoken");

const optionalAuthMiddleware = (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      return next();
    }

    const token = authHeader.split(" ")[1];
    const decoded = jwt.verify(
      token,
      process.env.JWT_ACCESS_SECRET || process.env.JWT_SECRET || "access_secret"
    );

    // Normalize JWT payload
    if (!decoded.account_id && decoded.accountId) {
      decoded.account_id = decoded.accountId;
    }
    if (!decoded.account_type && decoded.accountType) {
      decoded.account_type = decoded.accountType;
    }

    req.user = decoded;
    next();
  } catch (error) {
    // On error (expired or invalid token), we just proceed as guest
    next();
  }
};

module.exports = optionalAuthMiddleware;
