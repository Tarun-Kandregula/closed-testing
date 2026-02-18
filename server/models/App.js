const mongoose = require('mongoose');

const appSchema = new mongoose.Schema({
    developerId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    appName: { type: String, required: true },
    packageName: { type: String, required: true },
    appVersion: String,
    appIcon: String,
    appDescription: String,
    closedTestingLink: String,

    // Payment & Testers
    paymentAmount: { type: Number, default: 399 },
    maxTesters: { type: Number, default: 20 },
    durationDays: { type: Number, default: 15 },

    // Status: opt_in_period → ready_to_start → testing → completed
    status: {
        type: String,
        enum: ['opt_in_period', 'ready_to_start', 'testing', 'completed', 'cancelled'],
        default: 'opt_in_period'
    },

    // Opted-in testers
    optedInTesters: [{
        testerId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
        optedInAt: { type: Date, default: Date.now },
        installedAt: Date,
        daysCompleted: { type: Number, default: 0 },
        lastCheckIn: Date,
        completed: { type: Boolean, default: false }
    }],

    startedAt: Date,
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('App', appSchema);
