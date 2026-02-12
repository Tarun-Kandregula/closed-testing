const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5001;

// Middleware
app.use(cors());
app.use(express.json());

// Basic Route for Testing
app.get('/', (req, res) => {
    res.json({ message: "TwelveTesters API is running! 🚀" });
});

// Demo Data (Later will be in Database)
let apps = [];
let users = [];

// API Endpoints
app.post('/api/auth/signup', (req, res) => {
    const { email, password, role } = req.body;
    users.push({ email, password, role, id: Date.now() });
    res.status(201).json({ message: "User created", role });
});

app.post('/api/apps/submit', (req, res) => {
    const { name, packageName, developerId, testerEmails } = req.body;
    const newApp = { id: Date.now(), name, packageName, developerId, testerEmails, status: 'pending' };
    apps.push(newApp);
    res.status(201).json(newApp);
});

app.get('/api/apps/tester/:email', (req, res) => {
    const testerEmail = req.params.email;
    const assignedApps = apps.filter(app => app.testerEmails.includes(testerEmail));
    res.json(assignedApps);
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
