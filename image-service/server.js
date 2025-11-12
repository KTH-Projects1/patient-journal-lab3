import express from 'express';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import jimp from 'jimp';
import cors from 'cors';
import { fileURLToPath } from 'url';


import { authenticateToken } from './authMiddleware.js';


const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const port = 3000;


const uploadDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir, { recursive: true });
}


const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, uploadDir);
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, file.fieldname + '-' + uniqueSuffix + path.extname(file.originalname));
    }
});
const upload = multer({ storage: storage });

app.use(cors());
app.use(express.json());
app.use('/images', express.static(uploadDir));

app.post('/upload', authenticateToken, upload.single('image'), (req, res) => {
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

app.post('/edit/:filename', authenticateToken, async (req, res) => {
    const filename = req.params.filename;
    const filePath = path.join(uploadDir, filename);

    if (!fs.existsSync(filePath)) {
        return res.status(404).send('File not found.');
    }

    try {
        const image = await jimp.read(filePath);
        const font = await jimp.loadFont(jimp.FONT_SANS_32_BLACK);

        if (req.body.text && req.body.x !== undefined && req.body.y !== undefined) {
            const { text, x, y } = req.body;
            image.print(font, parseInt(x), parseInt(y), text);

        } else if (req.body.actions && Array.isArray(req.body.actions)) {
            const { actions } = req.body;

            for (const action of actions) {
                if (action.type === 'text') {
                    const { text, x, y } = action;
                    if (text && x !== undefined && y !== undefined) {
                        image.print(font, parseInt(x), parseInt(y), text);
                    }
                } else if (action.type === 'draw') {
                    const { points, color } = action;
                    if (points && Array.isArray(points)) {
                        const hexColor = jimp.cssColorToHex(color || '#000000'); // Svart som default
                        for (const point of points) {
                            if (point.x !== undefined && point.y !== undefined) {
                                image.setPixelColor(hexColor, parseInt(point.x), parseInt(point.y));
                            }
                        }
                    }
                }
            }
        } else {
            return res.status(400).send('Missing "actions" array in body, or legacy "text", "x", "y" fields.');
        }

        await image.writeAsync(filePath); // Spara ändringarna

        res.status(200).send({
            message: 'Image edited successfully',
            filename: filename,
            path: `/images/${filename}`
        });
    } catch (err) {
        console.error(err);
        res.status(500).send('Error editing image.');
    }
});

app.listen(port, () => {
    console.log(`Image service listening on http://localhost:${port}`);
});