import { jest } from '@jest/globals';
import jwt from 'jsonwebtoken';

process.env.JWT_SECRET = 'test-secret-key-for-testing';

import { authenticateToken } from '../../authMiddleware.js';

describe('AuthMiddleware', () => {
    let req, res, next;

    beforeEach(() => {
        process.env.JWT_SECRET = 'test-secret-key-for-testing';

        req = {
            headers: {}
        };
        res = {
            sendStatus: jest.fn()
        };
        next = jest.fn();
    });

    test('should return 401 if no token provided', () => {
        authenticateToken(req, res, next);

        expect(res.sendStatus).toHaveBeenCalledWith(401);
        expect(next).not.toHaveBeenCalled();
    });

    test('should return 401 if authorization header is malformed', () => {
        req.headers['authorization'] = 'InvalidToken';

        authenticateToken(req, res, next);

        expect(res.sendStatus).toHaveBeenCalledWith(401);
        expect(next).not.toHaveBeenCalled();
    });

    test('should return 403 if token is invalid', () => {
        req.headers['authorization'] = 'Bearer invalid.token.here';

        authenticateToken(req, res, next);

        expect(res.sendStatus).toHaveBeenCalledWith(403);
        expect(next).not.toHaveBeenCalled();
    });

    test('should call next() if token is valid', () => {
        const validToken = jwt.sign({ username: 'testuser' }, process.env.JWT_SECRET);
        req.headers['authorization'] = `Bearer ${validToken}`;

        authenticateToken(req, res, next);

        expect(next).toHaveBeenCalled();
        expect(req.user).toBeDefined();
        expect(req.user.username).toBe('testuser');
    });

    test('should return 500 if JWT_SECRET is not set', () => {
        const originalSecret = process.env.JWT_SECRET;

        delete process.env.JWT_SECRET;

        const validToken = jwt.sign({ username: 'testuser' }, 'some-secret');
        req.headers['authorization'] = `Bearer ${validToken}`;

        authenticateToken(req, res, next);

        expect(res.sendStatus).toHaveBeenCalledWith(500);
        expect(next).not.toHaveBeenCalled();

        process.env.JWT_SECRET = originalSecret;
    });
});