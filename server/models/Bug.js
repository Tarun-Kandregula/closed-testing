const mongoose = require('mongoose');

const bugSchema = new mongoose.Schema({
    appId: { type: mongoose.Schema.Types.ObjectId, ref: 'App', required: true },
    testerId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    title: { type: String, required: true },
    description: String,
    status: { type: String, enum: ['open', 'resolved'], default: 'open' },
    developerReply: String,
    chat: [{
        senderId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
        role: { type: String, required: true },
        message: { type: String, required: true },
        timestamp: { type: Date, default: Date.now }
    }],
    createdAt: { type: Date, default: Date.now },
    resolvedAt: Date
});

module.exports = mongoose.model('Bug', bugSchema);
