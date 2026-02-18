const fs = require('fs');
const path = require('path');
const mongoose = require('mongoose');

// Import Models
const User = require('../models/User');
const App = require('../models/App');
const Bug = require('../models/Bug');
const Transaction = require('../models/Transaction');

const BACKUP_FILE = path.join(__dirname, '../data/backup.json');

// Ensure data directory exists
const dataDir = path.dirname(BACKUP_FILE);
if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir, { recursive: true });
}

/**
 * Save all collections to a JSON file
 */
const saveData = async () => {
    try {
        if (mongoose.connection.readyState !== 1) return;

        const data = {
            users: await User.find({}),
            apps: await App.find({}),
            bugs: await Bug.find({}),
            transactions: await Transaction.find({}),
            timestamp: new Date().toISOString()
        };

        fs.writeFileSync(BACKUP_FILE, JSON.stringify(data, null, 2));
        console.log(`💾 Data saved to ${BACKUP_FILE}`);
    } catch (err) {
        console.error('❌ Failed to save data:', err);
    }
};

/**
 * Load data from JSON file into MongoDB
 */
const loadData = async () => {
    try {
        if (!fs.existsSync(BACKUP_FILE)) {
            console.log('ℹ️ No backup file found. Starting with empty database.');
            return;
        }

        const rawData = fs.readFileSync(BACKUP_FILE);
        const data = JSON.parse(rawData);

        console.log(`ℹ️ Loading data from ${data.timestamp}...`);

        // Users
        if (data.users && data.users.length > 0) {
            await User.deleteMany({});
            await User.insertMany(data.users);
            console.log(`✅ Loaded ${data.users.length} users`);
        }

        // Apps
        if (data.apps && data.apps.length > 0) {
            await App.deleteMany({});
            await App.insertMany(data.apps);
            console.log(`✅ Loaded ${data.apps.length} apps`);
        }

        // Bugs
        if (data.bugs && data.bugs.length > 0) {
            await Bug.deleteMany({});
            await Bug.insertMany(data.bugs);
            console.log(`✅ Loaded ${data.bugs.length} bugs`);
        }

        // Transactions
        if (data.transactions && data.transactions.length > 0) {
            await Transaction.deleteMany({});
            await Transaction.insertMany(data.transactions);
            console.log(`✅ Loaded ${data.transactions.length} transactions`);
        }

        console.log('✅ Data restoration complete');

    } catch (err) {
        console.error('❌ Failed to load data:', err);
    }
};

module.exports = { saveData, loadData };
