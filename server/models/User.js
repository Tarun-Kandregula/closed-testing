const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    role: { type: String, enum: ['developer', 'tester'], required: true },
    displayName: String,
    phoneNumber: String,
    country: String,
    deviceModel: String,
    androidVersion: String,
    fcmToken: String, // For push notifications
    trustScore: { type: Number, default: 5.0 },
    walletBalance: { type: Number, default: 0 },
    appsBeingTested: [{ type: mongoose.Schema.Types.ObjectId, ref: 'App' }],
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('User', userSchema);
