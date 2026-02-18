const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    role: { type: String, enum: ['developer', 'tester'], required: true },
    displayName: String,

    // Wallet
    walletBalance: { type: Number, default: 0 },

    // Developer-specific
    appsUploaded: [{ type: mongoose.Schema.Types.ObjectId, ref: 'App' }],

    // Tester-specific
    phoneNumber: String,
    country: String,
    deviceModel: String,
    androidVersion: String,
    fcmToken: String,
    trustScore: { type: Number, default: 100 },
    appsOptedIn: [{ type: mongoose.Schema.Types.ObjectId, ref: 'App' }],
    activeTests: { type: Number, default: 0 },

    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('User', userSchema);
