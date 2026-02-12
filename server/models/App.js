const mongoose = require('mongoose');

const appSchema = new mongoose.Schema({
    developerId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    appName: { type: String, required: true },
    packageName: { type: String, required: true },
    playStoreLink: String,
    status: { type: String, enum: ['pending', 'testing', 'completed'], default: 'pending' },
    currentTesters: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    dayCount: { type: Number, default: 0 }, // Tracks 0 to 14 days
    reportsSent: {
        day4: { type: Boolean, default: false },
        day8: { type: Boolean, default: false }
    },
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('App', appSchema);
