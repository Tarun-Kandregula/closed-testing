const express = require('express');
const router = express.Router();
const App = require('../models/App');
const auth = require('../middleware/auth');

// Update an app (Developer only)
router.put('/:id', auth, async (req, res) => {
    try {
        if (req.user.role !== 'developer') {
            return res.status(403).json({ message: "Only developers can update apps." });
        }

        const {
            appName,
            appVersion,
            appDescription,
            closedTestingLink,
            paymentAmount
        } = req.body;

        const app = await App.findById(req.params.id);
        if (!app) {
            return res.status(404).json({ message: "App not found." });
        }

        if (app.developerId.toString() !== req.user.id) {
            return res.status(403).json({ message: "You are not authorized to update this app." });
        }

        // Update fields
        if (appName) app.appName = appName;
        if (appVersion) app.appVersion = appVersion;
        if (appDescription) app.appDescription = appDescription;
        if (closedTestingLink) app.closedTestingLink = closedTestingLink;
        if (paymentAmount) app.paymentAmount = paymentAmount;
        if (req.body.packageName) app.packageName = req.body.packageName;

        if (req.body.maxTesters) {
            const mt = parseInt(req.body.maxTesters);
            if (mt < 1 || mt > 20) return res.status(400).json({ message: "Testers must be between 1 and 20." });
            app.maxTesters = mt;
        }

        if (req.body.durationDays) {
            const dd = parseInt(req.body.durationDays);
            if (dd < 1 || dd > 15) return res.status(400).json({ message: "Duration must be between 1 and 15 days." });
            app.durationDays = dd;
        }

        await app.save();
        await app.populate('developerId', 'displayName email');
        await app.populate('optedInTesters.testerId', 'displayName email role'); // Populate role for UserDto

        res.json({ success: true, message: "App updated successfully.", app });

    } catch (error) {
        console.error('Update App Error:', error);
        res.status(500).json({ message: "Error updating app." });
    }
});

// Submit a new app (Developer only)
router.post('/submit', auth, async (req, res) => {
    try {
        if (req.user.role !== 'developer') {
            return res.status(403).json({ message: "Only developers can submit apps." });
        }

        const {
            appName,
            appVersion,
            appDescription,
            closedTestingLink,
            appIcon,
            paymentAmount,
            maxTesters,
            packageName,
            durationDays
        } = req.body;

        // Validation
        if (!appName || !closedTestingLink || !packageName) {
            return res.status(400).json({ message: "App name, package name, and closed testing link are required." });
        }

        if (maxTesters && (maxTesters < 1 || maxTesters > 20)) {
            return res.status(400).json({ message: "Testers must be between 1 and 20." });
        }
        if (durationDays && (durationDays < 1 || durationDays > 15)) {
            return res.status(400).json({ message: "Duration must be between 1 and 15 days." });
        }

        // Create app in opt-in period
        const newApp = new App({
            developerId: req.user.id,
            appName,
            packageName,
            appVersion,
            appDescription,
            closedTestingLink,
            appIcon,
            paymentAmount: paymentAmount || 399,
            maxTesters: maxTesters || 20,
            durationDays: durationDays || 15,
            status: 'opt_in_period',
            optedInTesters: []
        });

        await newApp.save();
        await newApp.populate('developerId', 'displayName email');

        res.status(201).json({
            message: 'App submitted successfully! Testers can now opt-in.',
            app: newApp
        });

    } catch (error) {
        console.error('App Submission Error:', error);
        res.status(500).json({ message: 'Error submitting app.' });
    }
});

// Get apps for a developer
router.get('/my-apps', auth, async (req, res) => {
    try {
        if (req.user.role !== 'developer') {
            return res.status(403).json({ message: "Only developers can view their apps." });
        }

        const apps = await App.find({ developerId: req.user.id })
            .populate('optedInTesters.testerId', 'displayName deviceModel androidVersion trustScore email')
            .populate('developerId', 'displayName email')
            .sort({ createdAt: -1 });

        res.json(apps);
    } catch (error) {
        console.error('Fetch My Apps Error:', error);
        res.status(500).json({ message: "Server error fetching apps" });
    }
});

// Get available apps for opt-in (Testers)
router.get('/available', auth, async (req, res) => {
    try {
        if (req.user.role !== 'tester') {
            return res.status(403).json({ message: "Only testers can view available apps." });
        }

        // Find apps in opt_in_period that user hasn't opted into
        const apps = await App.find({
            status: 'opt_in_period',
            'optedInTesters.testerId': { $ne: req.user.id }
        }).populate('developerId', 'displayName email');

        res.json(apps);
    } catch (error) {
        console.error('Fetch Available Apps Error:', error);
        res.status(500).json({ message: "Server error fetching available apps" });
    }
});

// Get tester's opted-in apps (my tests)
router.get('/my-tests', auth, async (req, res) => {
    try {
        if (req.user.role !== 'tester') {
            return res.status(403).json({ message: "Only testers can view their tests." });
        }

        // Find all apps where this tester has opted in
        const tests = await App.find({
            'optedInTesters.testerId': req.user.id
        }).populate('developerId', 'displayName email');

        res.json(tests);
    } catch (error) {
        console.error("Fetch My Tests Error:", error);
        res.status(500).json({ message: "Server error fetching tests" });
    }
});

// Opt into an app (Tester)
router.post('/:id/opt-in', auth, async (req, res) => {
    try {
        if (req.user.role !== 'tester') {
            return res.status(403).json({ message: "Only testers can opt-in to apps." });
        }

        const app = await App.findById(req.params.id);
        if (!app) {
            return res.status(404).json({ message: "App not found" });
        }

        // Check if app is in opt-in period
        if (app.status !== 'opt_in_period') {
            return res.status(400).json({ message: "This app is no longer accepting opt-ins." });
        }

        // Check if already opted in
        const alreadyOptedIn = app.optedInTesters.some(
            t => t.testerId.toString() === req.user.id
        );
        if (alreadyOptedIn) {
            return res.status(400).json({ message: "You have already opted into this app." });
        }

        // Check if max testers reached
        if (app.optedInTesters.length >= app.maxTesters) {
            return res.status(400).json({ message: "Maximum testers reached for this app." });
        }

        // Add tester
        app.optedInTesters.push({
            testerId: req.user.id,
            optedInAt: new Date()
        });

        await app.save();

        res.json({
            message: "Successfully opted in! Wait for the developer to start the test.",
            app
        });

    } catch (error) {
        console.error("Opt-in Error:", error);
        res.status(500).json({ message: "Server error during opt-in" });
    }
});



// ⚠️ IMPORTANT: Keep parameterized routes (:id) at the END
// Otherwise Express will match /my-apps or /available to /:id

// Start testing period (Developer) - Must be before GET /:id
router.put('/:id/start', auth, async (req, res) => {
    try {
        if (req.user.role !== 'developer') {
            return res.status(403).json({ message: "Only developers can start tests" });
        }

        const app = await App.findById(req.params.id);

        if (!app) {
            return res.status(404).json({ message: "App not found" });
        }

        if (app.developerId.toString() !== req.user.id) {
            return res.status(403).json({ message: "Not authorized" });
        }

        if (app.status !== 'opt_in_period') {
            return res.status(400).json({ message: "App is not in opt-in period" });
        }

        if (!app.optedInTesters || app.optedInTesters.length < app.maxTesters) {
            return res.status(400).json({ message: `Waiting for all ${app.maxTesters} testers to opt in. Currently: ${app.optedInTesters.length}` });
        }

        // Update status to testing
        app.status = 'testing';
        app.startedAt = new Date();
        await app.save();

        res.json({ message: "Testing period started", app });
    } catch (error) {
        console.error("Start Test Error:", error);
        res.status(500).json({ message: "Server error starting test" });
    }
});

// Get single app details (Developer) - Keep this LAST
router.get('/:id', auth, async (req, res) => {
    try {
        const app = await App.findById(req.params.id)
            .populate('developerId', 'displayName email')
            .populate('optedInTesters.testerId', 'displayName email');

        if (!app) {
            return res.status(404).json({ message: "App not found" });
        }

        // Check if user is the developer
        if (req.user.role === 'developer' && app.developerId._id.toString() !== req.user.id) {
            return res.status(403).json({ message: "Not authorized to view this app" });
        }

        res.json(app);
    } catch (error) {
        console.error("Fetch App Detail Error:", error);
        res.status(500).json({ message: "Server error fetching app details" });
    }
});

// Opt-out of an app (Tester only)
router.post('/:id/opt-out', auth, async (req, res) => {
    try {
        if (req.user.role !== 'tester') {
            return res.status(403).json({ message: "Only testers can opt out." });
        }

        const app = await App.findById(req.params.id);
        if (!app) {
            return res.status(404).json({ message: "App not found." });
        }

        // Check if tester is opted in
        const isOptedIn = app.optedInTesters.some(
            t => t.testerId.toString() === req.user.id
        );

        if (!isOptedIn) {
            return res.status(400).json({ message: "You are not opted into this app." });
        }

        // Remove tester from list
        app.optedInTesters = app.optedInTesters.filter(
            t => t.testerId.toString() !== req.user.id
        );

        await app.save();

        res.json({ message: "Successfully opted out.", app });

    } catch (error) {
        console.error('Opt-Out Error:', error);
        res.status(500).json({ message: "Error opting out." });
    }
});

// Daily Check-in (Tester only - from Android App)
router.post('/:id/check-in', auth, async (req, res) => {
    try {
        if (req.user.role !== 'tester') {
            return res.status(403).json({ message: "Only testers can check in." });
        }

        const { installedPackageName } = req.body;
        const app = await App.findById(req.params.id);

        if (!app) {
            return res.status(404).json({ message: "App not found." });
        }

        // Verify status
        if (app.status !== 'testing') {
            return res.status(400).json({ message: "Testing has not started for this app yet." });
        }

        // Verify Package Name (Security Check)
        if (!installedPackageName || installedPackageName !== app.packageName) {
            return res.status(400).json({ message: "Package verification failed. Please check in from the correct app." });
        }

        // Verify Tester is Opted In
        const tester = app.optedInTesters.find(t => t.testerId.toString() === req.user.id);
        if (!tester) {
            return res.status(403).json({ message: "You are not opted into this app." });
        }

        // Check if already checked in today
        const now = new Date();
        const lastCheck = tester.lastCheckIn ? new Date(tester.lastCheckIn) : null;

        if (lastCheck) {
            const isSameDay = lastCheck.getDate() === now.getDate() &&
                lastCheck.getMonth() === now.getMonth() &&
                lastCheck.getFullYear() === now.getFullYear();

            if (isSameDay) {
                return res.status(400).json({ message: "You have already checked in today." });
            }
        }

        // Process Check-in
        tester.lastCheckIn = now;
        tester.daysCompleted += 1;

        // Check for completion
        if (tester.daysCompleted >= 15) {
            tester.completed = true;
        }

        await app.save();

        res.json({
            message: `Check-in successful! Day ${tester.daysCompleted}/15 completed.`,
            daysCompleted: tester.daysCompleted,
            completed: tester.completed
        });

    } catch (error) {
        console.error("Check-in Error:", error);
        res.status(500).json({ message: "Server error during check-in" });
    }
});

module.exports = router;
