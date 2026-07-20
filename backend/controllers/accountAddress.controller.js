const AccountAddress = require("../models/accountAddress.model");
const Customer = require("../models/customer.model");
const validateObjectId = require("../utils/validateObjectId");

const getCustomerByAccount = async (accountId) => {
  if (!accountId || !validateObjectId(accountId)) return null;
  return await Customer.findOne({ account_id: accountId }).select("_id");
};

const getAccountAddresses = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    const customer = await getCustomerByAccount(accountId);
    if (!customer) return res.status(401).json({ success: false, message: "Unauthorized or Customer profile not found" });

    const addresses = await AccountAddress.find({ customer_id: customer._id }).sort({ is_default: -1, createdAt: -1 });
    const mapped = addresses.map(a => ({
      _id: a._id,
      address_label: a.address_label || "",
      recipient_name: a.recipient_name || "",
      phone: a.phone || "",
      address_line_1: a.address_line_1 || "",
      address_line_2: a.address_line_2 || "",
      ward: a.ward || "",
      district: a.district || "",
      city: a.city || "",
      country_code: a.country_code || "",
      postal_code: a.postal_code || "",
      address_type: a.address_type || "",
      address_note: a.address_note || "",
      is_default: a.is_default,
      is_default_shipping: a.is_default_shipping,
      is_default_billing: a.is_default_billing
    }));
    return res.status(200).json({ success: true, data: mapped });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const postAccountAddress = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    const customer = await getCustomerByAccount(accountId);
    if (!customer) return res.status(401).json({ success: false, message: "Unauthorized or Customer profile not found" });

    const {
      address_label,
      recipient_name,
      phone,
      address_line_1,
      address_line_2,
      ward,
      district,
      city,
      country_code,
      postal_code,
      address_type,
      address_note,
      is_default,
      is_default_shipping,
      is_default_billing
    } = req.body;

    // Nếu cái này là mặc định, reset các cái khác
    if (is_default) {
      await AccountAddress.updateMany(
        { customer_id: customer._id },
        { is_default: false, is_default_shipping: false, is_default_billing: false }
      );
    }

    const count = await AccountAddress.countDocuments({ customer_id: customer._id });
    const finalIsDefault = count === 0 ? true : !!is_default;

    const newAddress = await AccountAddress.create({
      customer_id: customer._id,
      address_label: address_label || "",
      recipient_name: recipient_name || "",
      phone: phone || "",
      address_line_1: address_line_1 || "",
      address_line_2: address_line_2 || "",
      ward: ward || "",
      district: district || "",
      city: city || "",
      country_code: country_code || "VN",
      postal_code: postal_code || "",
      address_type: address_type || "home",
      address_note: address_note || "",
      is_default: finalIsDefault,
      is_default_shipping: is_default_shipping !== undefined ? is_default_shipping : finalIsDefault,
      is_default_billing: is_default_billing !== undefined ? is_default_billing : finalIsDefault,
    });

    return res.status(201).json({
      success: true,
      message: "Address added successfully",
      data: newAddress
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const patchAccountAddress = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    const customer = await getCustomerByAccount(accountId);
    if (!customer) return res.status(401).json({ success: false, message: "Unauthorized or Customer profile not found" });

    const { id } = req.params;
    if (!validateObjectId(id)) return res.status(400).json({ success: false, message: "Invalid ID" });

    const {
      address_label,
      recipient_name,
      phone,
      address_line_1,
      address_line_2,
      ward,
      district,
      city,
      country_code,
      postal_code,
      address_type,
      address_note,
      is_default,
      is_default_shipping,
      is_default_billing
    } = req.body;

    if (is_default) {
      await AccountAddress.updateMany(
        { customer_id: customer._id, _id: { $ne: id } },
        { is_default: false, is_default_shipping: false, is_default_billing: false }
      );
    }

    const updated = await AccountAddress.findOneAndUpdate(
      { _id: id, customer_id: customer._id },
      {
        address_label,
        recipient_name,
        phone,
        address_line_1,
        address_line_2,
        ward,
        district,
        city,
        country_code,
        postal_code,
        address_type,
        address_note,
        is_default,
        is_default_shipping,
        is_default_billing
      },
      { new: true }
    );

    if (!updated) return res.status(404).json({ success: false, message: "Address not found" });

    return res.status(200).json({
      success: true,
      message: "Address updated successfully",
      data: updated
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message });
  }
};

const deleteAccountAddress = async (req, res) => {
  try {
    const accountId = req.user?.account_id || req.user?.accountId;
    const customer = await getCustomerByAccount(accountId);
    if (!customer) return res.status(401).json({ success: false, message: "Unauthorized or Customer profile not found" });

    const { id } = req.params;
    if (!validateObjectId(id)) return res.status(400).json({ success: false, message: "Invalid ID" });

    const address = await AccountAddress.findOne({ _id: id, customer_id: customer._id });
    if (!address) return res.status(404).json({ success: false, message: "Address not found" });

    const wasDefault = address.is_default;
    await AccountAddress.deleteOne({ _id: id });

    if (wasDefault) {
      const next = await AccountAddress.findOne({ customer_id: customer._id }).sort({ createdAt: -1 });
      if (next) {
        next.is_default = true;
        next.is_default_shipping = true;
        next.is_default_billing = true;
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
    const customer = await getCustomerByAccount(accountId);
    if (!customer) return res.status(401).json({ success: false, message: "Unauthorized or Customer profile not found" });

    const { id } = req.params;

    if (!validateObjectId(id)) {
      return res.status(400).json({ success: false, message: "Invalid ID" });
    }

    // 1. Set các flag mặc định = false cho TẤT CẢ địa chỉ của customer đó
    await AccountAddress.updateMany(
      { customer_id: customer._id },
      { is_default: false, is_default_shipping: false, is_default_billing: false }
    );

    // 2. Set các flag mặc định = true cho địa chỉ được chọn
    const updated = await AccountAddress.findOneAndUpdate(
      { _id: id, customer_id: customer._id },
      { is_default: true, is_default_shipping: true, is_default_billing: true },
      { new: true }
    );

    if (!updated) {
      return res.status(404).json({ success: false, message: "Address not found" });
    }

    // 3. Trả về địa chỉ vừa set
    return res.status(200).json({
      success: true,
      message: "Default address set successfully",
      data: updated
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
