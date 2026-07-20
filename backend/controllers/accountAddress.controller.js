const AccountAddress = require("../models/accountAddress.model");
const validateObjectId = require("../utils/validateObjectId");

const getAccountAddresses = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    if (!accountId) return res.status(401).json({ success: false, message: "Unauthorized" });

    const addresses = await AccountAddress.find({ account_id: accountId }).sort({ is_default: -1, createdAt: -1 });
    const mapped = addresses.map(a => ({
      _id: a._id,
      recipient_name: a.full_name || "",
      phone: a.phone || "",
      address_line_1: a.address_line || "",
      city: "", // Để trống vì chúng ta lưu vào address_line_1 toàn bộ rồi
      is_default_shipping: a.is_default
    }));
    return res.status(200).json({ success: true, data: mapped });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const postAccountAddress = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    if (!accountId) return res.status(401).json({ success: false, message: "Unauthorized" });

    const { full_name, phone, address_line, is_default } = req.body;

    if (is_default) {
      await AccountAddress.updateMany({ account_id: accountId }, { is_default: false });
    }

    const count = await AccountAddress.countDocuments({ account_id: accountId });
    const finalIsDefault = count === 0 ? true : !!is_default;

    const newAddress = await AccountAddress.create({
      account_id: accountId,
      full_name: full_name || "",
      phone: phone || "",
      address_line: address_line || "",
      is_default: finalIsDefault,
    });

    return res.status(201).json({
      success: true,
      message: "Address added successfully",
      data: {
        _id: newAddress._id,
        recipient_name: newAddress.full_name,
        phone: newAddress.phone,
        address_line_1: newAddress.address_line,
        is_default_shipping: newAddress.is_default
      }
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const patchAccountAddress = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    const { id } = req.params;
    if (!validateObjectId(id)) return res.status(400).json({ success: false, message: "Invalid ID" });

    const { full_name, phone, address_line, is_default } = req.body;

    if (is_default) {
      await AccountAddress.updateMany({ account_id: accountId, _id: { $ne: id } }, { is_default: false });
    }

    const updated = await AccountAddress.findOneAndUpdate(
      { _id: id, account_id: accountId },
      { full_name, phone, address_line, is_default },
      { new: true }
    );

    if (!updated) return res.status(404).json({ success: false, message: "Address not found" });

    return res.status(200).json({
      success: true,
      message: "Address updated successfully",
      data: {
        _id: updated._id,
        recipient_name: updated.full_name,
        phone: updated.phone,
        address_line_1: updated.address_line,
        is_default_shipping: updated.is_default
      }
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const deleteAccountAddress = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    const { id } = req.params;
    if (!validateObjectId(id)) return res.status(400).json({ success: false, message: "Invalid ID" });

    const address = await AccountAddress.findOne({ _id: id, account_id: accountId });
    if (!address) return res.status(404).json({ success: false, message: "Address not found" });

    const wasDefault = address.is_default;
    await AccountAddress.deleteOne({ _id: id });

    if (wasDefault) {
      const next = await AccountAddress.findOne({ account_id: accountId }).sort({ createdAt: -1 });
      if (next) {
        next.is_default = true;
        await next.save();
      }
    }

    return res.status(200).json({ success: true, message: "Address deleted successfully" });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const setDefaultAccountAddress = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    const { id } = req.params;
    if (!validateObjectId(id)) return res.status(400).json({ success: false, message: "Invalid ID" });

    await AccountAddress.updateMany({ account_id: accountId }, { is_default: false });
    const updated = await AccountAddress.findOneAndUpdate(
      { _id: id, account_id: accountId },
      { is_default: true },
      { new: true }
    );

    if (!updated) return res.status(404).json({ success: false, message: "Address not found" });

    return res.status(200).json({
      success: true,
      message: "Default address set successfully",
      data: {
        _id: updated._id,
        recipient_name: updated.full_name,
        phone: updated.phone,
        address_line_1: updated.address_line,
        is_default_shipping: updated.is_default
      }
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

module.exports = {
  getAccountAddresses,
  postAccountAddress,
  patchAccountAddress,
  deleteAccountAddress,
  setDefaultAccountAddress,
};
