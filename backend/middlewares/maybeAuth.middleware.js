const jwt = require("jsonwebtoken");

/**
 * Optional authentication middleware.
 * If token is valid, req.user is set.
 * If token is missing or invalid, it continues without req.user.
 */
const maybeAuthMiddleware = (req, res, next) => {
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

    if (!decoded.account_id && decoded.accountId) {
      decoded.account_id = decoded.accountId;
    }

    req.user = decoded;
    next();
  } catch (error) {
    // If token is invalid, we just continue without user
    next();
  }
};

module.exports = maybeAuthMiddleware;
