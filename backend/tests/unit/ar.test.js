const request = require('supertest');
const express = require('express');

// We'll mock the models
jest.mock('../../models/product.model');
jest.mock('../../models/productVariant.model');
jest.mock('../../models/arTryOnEvent.model');

const Product = require('../../models/product.model');
const ProductVariant = require('../../models/productVariant.model');
const ArTryOnEvent = require('../../models/arTryOnEvent.model');

const arRoutes = require('../../routes/ar.route');

const app = express();
app.use(express.json());
app.use('/api', arRoutes);

describe('AR Controller Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/products/:productId/ar-config', () => {
    it('should return 400 for invalid product ID', async () => {
      const res = await request(app).get('/api/products/invalidId/ar-config');
      expect(res.statusCode).toBe(400);
    });

    it('should return 404 for missing product', async () => {
      Product.findById.mockResolvedValue(null);
      
      const res = await request(app).get('/api/products/5f9d7a3a8f1b2c3d4e5f6a7b/ar-config');
      expect(res.statusCode).toBe(404);
    });

    it('should return NOT_SUPPORTED if product has no AR variants', async () => {
      Product.findById.mockResolvedValue({ _id: '5f9d7a3a8f1b2c3d4e5f6a7b', productName: "Test" });
      ProductVariant.find.mockResolvedValue([]);

      const res = await request(app).get('/api/products/5f9d7a3a8f1b2c3d4e5f6a7b/ar-config');
      
      expect(res.statusCode).toBe(200);
      expect(res.body.status).toBe('NOT_SUPPORTED');
      expect(res.body.variants.length).toBe(0);
    });

    it('should return READY and active variants with AR config', async () => {
      Product.findById.mockResolvedValue({ _id: '5f9d7a3a8f1b2c3d4e5f6a7b', productName: "Test" });
      ProductVariant.find.mockResolvedValue([
        {
          _id: '123',
          sku: 'SKU-01',
          variantName: 'Red',
          costAmount: 150000,
          ar_config: {
            shade_hex: '#FF0000',
            finish_type: 'MATTE',
            opacity: 0.8
          }
        }
      ]);

      const res = await request(app).get('/api/products/5f9d7a3a8f1b2c3d4e5f6a7b/ar-config');
      
      expect(res.statusCode).toBe(200);
      expect(res.body.status).toBe('READY');
      expect(res.body.variants.length).toBe(1);
      expect(res.body.variants[0].shade_hex).toBe('#FF0000');
    });
  });

  describe('POST /api/ar/events/batch', () => {
    it('should insert valid events and strip NoSQL operators', async () => {
      ArTryOnEvent.create.mockResolvedValue(true);

      const res = await request(app)
        .post('/api/ar/events/batch')
        .send({
          session_uuid: "uuid123",
          product_id: "5f9d7a3a8f1b2c3d4e5f6a7b",
          events: [
            {
              event_type: "AR_OPENED",
              occurred_at: new Date().toISOString()
            },
            {
              event_type: "SHADE_SELECTED",
              occurred_at: new Date().toISOString(),
              metadata: { "$where": "sleep(5000)" } // rejected
            }
          ]
        });
      
      expect(res.statusCode).toBe(200);
      expect(res.body.accepted_count).toBe(1);
      expect(res.body.rejected_count).toBe(1);
    });

    it('should reject batch larger than 50', async () => {
      const events = Array(51).fill({ event_type: "AR_OPENED", occurred_at: new Date() });
      
      const res = await request(app)
        .post('/api/ar/events/batch')
        .send({
          session_uuid: "uuid123",
          product_id: "5f9d7a3a8f1b2c3d4e5f6a7b",
          events: events
        });
      
      expect(res.statusCode).toBe(400);
    });
  });
});
