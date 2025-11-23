export default {
    testEnvironment: 'node',
    transform: {},
    moduleNameMapper: {
        '^(\\.{1,2}/.*)\\.js$': '$1',
    },
    testMatch: ['**/test/**/*.test.js'],
    collectCoverageFrom: [
        '*.js',
        '!server.js',
        '!jest.config.js'
    ],
    coveragePathIgnorePatterns: [
        '/node_modules/',
        '/test/',
        '/uploads/'
    ]
};