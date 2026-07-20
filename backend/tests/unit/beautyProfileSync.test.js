const request = require('supertest');
const express = require('express');

// Mock out database and services
jest.mock('../../models/customerBeautyProfile.model', () => ({
  findOne: jest.fn(),
  findById: jest.fn()
}));
jest.mock('../../models/customer.model', () => ({
  findOne: jest.fn()
}));
jest.mock('../../services/customerBeautyProfile.service', () => ({
  getProfileByCustomerId: jest.fn(),
  createProfile: jest.fn(),
  updateProfile: jest.fn()
}));
jest.mock('../../utils/validateObjectId', () => jest.fn(() => true));
jest.mock('../../validations/customerBeautyProfile.validation', () => ({
  validateCustomerBeautyProfile: jest.fn(() => [])
}));

const Customer = require('../../models/customer.model');
const customerBeautyProfileService = require('../../services/customerBeautyProfile.service');

const customerBeautyProfileController = require('../../controllers/customerBeautyProfile.controller');
const beautyProfileLegacyAdapter = require('../../middlewares/beautyProfileLegacyAdapter.middleware');

describe('Beauty Profile Synchronization Tests', () => {
  let app;

  beforeEach(() => {
    jest.clearAllMocks();
    process.env.BEAUTY_PROFILE_ACCEPT_LEGACY_FIELDS = 'true';
    app = express();
    app.use(express.json());
    
    // Mock authenticated user context
    app.use((req, res, next) => {
      req.user = { account_id: 'user-account-123', account_type: 'customer' };
      next();
    });

    // Test route with legacy adapter + controller
    app.patch('/api/beauty-profiles/:customer_id', beautyProfileLegacyAdapter, customerBeautyProfileController.updateProfile);
  });

  describe('Legacy Adapter Conflict Handling', () => {
    it('should return 400 Validation Error when both legacy and canonical fields are sent', async () => {
      const res = await request(app)
        .patch('/api/beauty-profiles/me')
        .send({
          skin_tone: 'LIGHT',       // Legacy
          skin_color: 'LIGHT'       // Canonical (conflict)
        });

      expect(res.statusCode).toBe(400);
      expect(res.body.success).toBe(false);
      expect(res.body.message).toMatch(/Validation error/);
      expect(typeof res.body.error).toBe('string');
      expect(res.body.error).toMatch(/Conflict: Cannot send both legacy 'skin_tone' and canonical 'skin_color'/);
    });

    it('should map legacy to canonical when only legacy is sent', async () => {
      Customer.findOne.mockReturnValue({ select: jest.fn().mockReturnValue({ lean: jest.fn().mockResolvedValue({ _id: 'cust-123' }) }) });
      customerBeautyProfileService.updateProfile.mockResolvedValue({ skin_color: 'LIGHT' });

      const res = await request(app)
        .patch('/api/beauty-profiles/me')
        .send({
          skin_tone: 'LIGHT' // Only legacy
        });

      expect(res.statusCode).toBe(200);
      // Ensure the service received the mapped canonical key
      expect(customerBeautyProfileService.updateProfile).toHaveBeenCalledWith(
        'cust-123',
        expect.objectContaining({ skin_color: 'LIGHT' }),
        expect.any(Object)
      );
    });
  });

  describe('IDOR Protection', () => {
    it('should allow access to "me" (resolves to own ID)', async () => {
      Customer.findOne.mockReturnValue({ select: jest.fn().mockReturnValue({ lean: jest.fn().mockResolvedValue({ _id: 'cust-123' }) }) });
      customerBeautyProfileService.updateProfile.mockResolvedValue({ skin_color: 'LIGHT' });

      const res = await request(app).patch('/api/beauty-profiles/me').send({ skin_color: 'LIGHT' });

      expect(res.statusCode).toBe(200);
      expect(customerBeautyProfileService.updateProfile).toHaveBeenCalledWith('cust-123', expect.any(Object), expect.any(Object));
    });

    it('should reject a non-admin accessing another user\'s profile via raw ID', async () => {
      // Customer.findOne returns null because account_id doesn't match the passed raw ID
      Customer.findOne.mockReturnValue({ select: jest.fn().mockReturnValue({ lean: jest.fn().mockResolvedValue(null) }) });

      const res = await request(app).patch('/api/beauty-profiles/other-guy-id').send({ skin_color: 'LIGHT' });

      expect(res.statusCode).toBe(403);
      expect(res.body.message).toMatch(/Forbidden/);
      expect(customerBeautyProfileService.updateProfile).not.toHaveBeenCalled();
    });
  });

  describe('Unknown Fields Rejection', () => {
    it('should reject unknown fields that are not in SAFE_FIELDS or READ_ONLY_FIELDS', async () => {
      Customer.findOne.mockReturnValue({ select: jest.fn().mockReturnValue({ lean: jest.fn().mockResolvedValue({ _id: 'cust-123' }) }) });

      const res = await request(app).patch('/api/beauty-profiles/me').send({
        skin_color: 'LIGHT',
        hacker_field: 'hacker',
        some_random_id: '12345'
      });

      expect(res.statusCode).toBe(400);
      expect(typeof res.body.error).toBe('string');
      expect(res.body.error).toMatch(/Unknown or read-only field: "hacker_field"/);
      expect(res.body.error).toMatch(/Unknown or read-only field: "some_random_id"/);
      expect(customerBeautyProfileService.updateProfile).not.toHaveBeenCalled();
    });
  });
});
