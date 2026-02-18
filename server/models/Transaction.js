const mongoose = require('mongoose');

const transactionSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    appId: { type: mongoose.Schema.Types.ObjectId, ref: 'App' },
    type: { type: String, enum: ['payment', 'refund', 'earning'], required: true },
    amount: { type: Number, required: true },
    status: { type: String, enum: ['pending', 'completed', 'failed'], default: 'pending' },
    razorpayOrderId: String,
    razorpayPaymentId: String,
    createdAt: { type: Date, default: Date.now },
    completedAt: Date
});

module.exports = mongoose.model('Transaction', transactionSchema);
