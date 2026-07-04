const BeautyReference = require("../models/beautyReference.model");

class BeautyReferenceService {
  async getReferences(query) {
    const { reference_group, is_active, search } = query;
    const filter = {};

    if (reference_group) {
      filter.reference_group = reference_group;
    }
    
    if (is_active !== undefined) {
      filter.is_active = is_active === "true" || is_active === true;
    }

    if (search) {
      filter.$or = [
        { display_name_vi: { $regex: search, $options: "i" } },
        { reference_code: { $regex: search, $options: "i" } },
      ];
    }

    const items = await BeautyReference.find(filter).sort({ reference_group: 1, sort_order: 1 });
    return items;
  }

  async getReferenceGroup(reference_group) {
    const items = await BeautyReference.find({ 
      reference_group,
      is_active: true 
    }).sort({ sort_order: 1 });
    return items;
  }

  async createReference(data) {
    const newRef = new BeautyReference(data);
    await newRef.save();
    return newRef;
  }

  async updateReference(id, data) {
    const ref = await BeautyReference.findByIdAndUpdate(id, data, { new: true, runValidators: true });
    return ref;
  }

  async deleteReference(id) {
    const ref = await BeautyReference.findByIdAndUpdate(id, { is_active: false }, { new: true });
    return ref;
  }
}

module.exports = new BeautyReferenceService();
