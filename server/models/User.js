const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    role: { type: String, enum: ['developer', 'tester'], required: true },
    displayName: String,
    phoneNumber: String,
    country: String,
    deviceModel: String, // For testers to help developers choose
    androidVersion: String,
    walletBalance: { type: Number, default: 0 },
    appsBeingTested: [{ type: mongoose.Schema.Types.ObjectId, ref: 'App' }],
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('User', userSchema);
