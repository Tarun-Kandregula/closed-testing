const express = require('express');
const router = express.Router();
const Bug = require('../models/Bug');
const App = require('../models/App');
const User = require('../models/User'); // Assuming User model exists
const auth = require('../middleware/auth'); // Assuming auth middleware exists

// Middleware to check if user is opted-in to the app
const checkOptIn = async (req, res, next) => {
    try {
        const { appId } = req.body;
        const userId = req.user.id;

        const app = await App.findById(appId);
        if (!app) {
            return res.status(404).json({ message: 'App not found' });
        }

        const isTester = app.optedInTesters.some(t => t.testerId.toString() === userId);
        if (!isTester) {
            return res.status(403).json({ message: 'You must be an opted-in tester to report a bug for this app.' });
        }

        next();
    } catch (error) {
        res.status(500).json({ message: 'Server error during opt-in check', error: error.message });
    }
};

// @route   POST /api/bugs
// @desc    Create a new bug report
// @access  Private (Tester)
router.post('/', auth, checkOptIn, async (req, res) => {
    try {
        const { appId, title, description } = req.body;
        const userId = req.user.id;

        const newBug = new Bug({
            appId,
            testerId: userId,
            title,
            description,
            status: 'open'
        });

        await newBug.save();
        res.status(201).json(newBug);
    } catch (error) {
        console.error('Error reporting bug:', error);
        res.status(500).json({ message: 'Server error reporting bug', error: error.message });
    }
});

// @route   GET /api/bugs/app/:appId
// @desc    Get all bugs for a specific app (Developer view)
// @access  Private (Developer/Tester) - access control logic might be needed
router.get('/app/:appId', auth, async (req, res) => {
    try {
        const { appId } = req.params;
        // Ideally check if user is the developer of the app or a tester?
        // For simplicity allow authenticated users for now, or refine permissions later.

        const bugs = await Bug.find({ appId })
            .populate('testerId', 'displayName email') // Populate tester details
            .sort({ createdAt: -1 });

        res.json(bugs);
    } catch (error) {
        console.error('Error fetching bugs for app:', error);
        res.status(500).json({ message: 'Server error fetching bugs', error: error.message });
    }
});

// @route   GET /api/bugs/my-reports
// @desc    Get all bugs reported by the authenticated tester
// @access  Private (Tester)
router.get('/my-reports', auth, async (req, res) => {
    try {
        const userId = req.user.id;
        const bugs = await Bug.find({ testerId: userId })
            .populate('appId', 'appName icon packageId') // Populate app details
            .sort({ createdAt: -1 });

        res.json(bugs);
    } catch (error) {
        console.error('Error fetching user bugs:', error);
        res.status(500).json({ message: 'Server error fetching your bugs', error: error.message });
    }
});

// @route   PUT /api/bugs/:id/reply
// @desc    Developer adds a reply to a bug report
// @access  Private (Developer)
router.put('/:id/reply', auth, async (req, res) => {
    try {
        const { reply } = req.body;
        const bugId = req.params.id;

        // TODO: Verify that the requester is the developer of the app associated with this bug

        const bug = await Bug.findByIdAndUpdate(
            bugId,
            { developerReply: reply },
            { new: true }
        );

        if (!bug) {
            return res.status(404).json({ message: 'Bug not found' });
        }

        res.json(bug);
    } catch (error) {
        console.error('Error replying to bug:', error);
        res.status(500).json({ message: 'Server error replying to bug', error: error.message });
    }
});

// @route   PUT /api/bugs/:id/resolve
// @desc    Developer marks a bug as resolved
// @access  Private (Developer)
router.put('/:id/resolve', auth, async (req, res) => {
    try {
        const bugId = req.params.id;

        // TODO: Verify that the requester is the developer

        const bug = await Bug.findByIdAndUpdate(
            bugId,
            {
                status: 'resolved',
                resolvedAt: Date.now()
            },
            { new: true }
        );

        if (!bug) {
            return res.status(404).json({ message: 'Bug not found' });
        }

        res.json(bug);
    } catch (error) {
        console.error('Error resolving bug:', error);
        res.status(500).json({ message: 'Server error resolving bug', error: error.message });
    }
});

module.exports = router;
