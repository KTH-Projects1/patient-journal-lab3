import { jest } from '@jest/globals';
import request from 'supertest';
import express from 'express';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import { fileURLToPath } from 'url';
import jwt from 'jsonwebtoken';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

process.env.JWT_SECRET = 'test-secret-key-for-testing';

const app = express();
const uploadDir = path.join(__dirname, '../temp-uploads');

if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir, { recursive: true });
}

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, uploadDir);
    },
    filename: (req, file, cb) => {
        cb(null, 'test-' + Date.now() + path.extname(file.originalname));
    }
});

const upload = multer({ storage: storage });

app.use(express.json());
app.use('/images', express.static(uploadDir));

const mockAuthMiddleware = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.sendStatus(401);
    }

    try {
        req.user = jwt.verify(token, process.env.JWT_SECRET);
        next();
    } catch {
        return res.sendStatus(403);
    }
};

app.post('/upload', mockAuthMiddleware, upload.single('image'), (req, res) => {
    if (!req.file) {
        return res.status(400).send('No file uploaded.');
    }
    res.status(201).send({
        message: 'File uploaded successfully',
        filename: req.file.filename,
        path: `/images/${req.file.filename}`
    });
});

app.get('/images/:filename', (req, res) => {
    const filePath = path.join(uploadDir, req.params.filename);
    if (fs.existsSync(filePath)) {
        res.sendFile(filePath);
    } else {
        res.status(404).send('File not found.');
    }
});

describe('Image Service Integration Tests', () => {
    let validToken;

    beforeAll(() => {
        validToken = jwt.sign({ username: 'testuser' }, process.env.JWT_SECRET);
    });

    afterAll(() => {
        if (fs.existsSync(uploadDir)) {
            fs.rmSync(uploadDir, { recursive: true, force: true });
        }
    });

    describe('POST /upload', () => {
        test('should return 401 without token', async () => {
            const response = await request(app)
                .post('/upload')
                .attach('image', Buffer.from('test'), 'test.jpg');

            expect(response.status).toBe(401);
        });

        test('should return 400 if no file provided', async () => {
            const response = await request(app)
                .post('/upload')
                .set('Authorization', `Bearer ${validToken}`);

            expect(response.status).toBe(400);
            expect(response.text).toContain('No file uploaded');
        });

        test('should upload file successfully with valid token', async () => {
            const testImageBuffer = Buffer.from('fake-image-data');

            const response = await request(app)
                .post('/upload')
                .set('Authorization', `Bearer ${validToken}`)
                .attach('image', testImageBuffer, 'test-image.jpg');

            expect(response.status).toBe(201);
            expect(response.body).toHaveProperty('message', 'File uploaded successfully');
            expect(response.body).toHaveProperty('filename');
            expect(response.body).toHaveProperty('path');
        });
    });

    describe('GET /images/:filename', () => {
        test('should return 404 for non-existent file', async () => {
            const response = await request(app)
                .get('/images/nonexistent.jpg');

            expect(response.status).toBe(404);
            expect(response.text).toContain('File not found');
        });

        test('should retrieve existing file', async () => {
            const testImageBuffer = Buffer.from('test-file-content');
            const uploadResponse = await request(app)
                .post('/upload')
                .set('Authorization', `Bearer ${validToken}`)
                .attach('image', testImageBuffer, 'retrieve-test.jpg');

            const filename = uploadResponse.body.filename;

            const response = await request(app)
                .get(`/images/${filename}`);

            expect(response.status).toBe(200);
        });
    });
});