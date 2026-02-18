const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const mongoose = require('mongoose');
const authRoutes = require('./routes/auth');
const appRoutes = require('./routes/apps');
const { MongoMemoryServer } = require('mongodb-memory-server');

const { saveData, loadData } = require('./utils/persistence');

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5001;

// Middleware
app.use(cors());
app.use(express.json());

// Database Connection Logic
const connectDB = async () => {
    try {
        let uri = process.env.MONGO_URI;

        console.log("ℹ️ Starting In-Memory MongoDB (Data will reset on restart)...");
        const mongod = await MongoMemoryServer.create();
        uri = mongod.getUri();
        console.log("✅ In-Memory MongoDB Started at:", uri);

        await mongoose.connect(uri);
        console.log("✅ MongoDB Connected");

        // Load persisted data
        await loadData();

        // Save data every 10 seconds
        setInterval(async () => {
            await saveData();
        }, 10000);

        // Save on exit
        process.on('SIGINT', async () => {
            console.log('🛑 Server stopping...');
            await saveData();
            process.exit(0);
        });

    } catch (err) {
        console.error("❌ MongoDB Connection Error:", err);
    }
};

connectDB();

// Basic Route for Testing
app.get('/', (req, res) => {
    res.json({ message: "TwelveTesters API is running! 🚀" });
});

// Routes
const bugRoutes = require('./routes/bugRoutes');

app.use('/api/auth', authRoutes);
app.use('/api/apps', appRoutes);
app.use('/api/bugs', bugRoutes);

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
