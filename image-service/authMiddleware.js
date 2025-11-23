import jwt from 'jsonwebtoken';

export const authenticateToken = (req, res, next) => {
    const JWT_SECRET = process.env.JWT_SECRET;

    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (token == null) {
        return res.sendStatus(401);
    }

    if (!JWT_SECRET) {
        console.error("JWT_SECRET is not set. Server configuration error.");
        return res.sendStatus(500);
    }

    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) {
            console.log("JWT verification error:", err.message);
            return res.sendStatus(403);
        }
        req.user = user;

        next();
    });
};