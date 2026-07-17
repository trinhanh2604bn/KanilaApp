const express = require("express");
const router = express.Router();
const accountAddressController = require("../controllers/accountAddress.controller");
const authMiddleware = require("../middlewares/auth.middleware");

router.use(authMiddleware);

router.get("/", accountAddressController.getAccountAddresses);
router.post("/", accountAddressController.postAccountAddress);
router.patch("/:id", accountAddressController.patchAccountAddress);
router.delete("/:id", accountAddressController.deleteAccountAddress);
router.patch("/:id/default", accountAddressController.setDefaultAccountAddress);

module.exports = router;
