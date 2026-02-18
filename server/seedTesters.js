const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const dotenv = require('dotenv');
const User = require('./models/User');
const { MongoMemoryServer } = require('mongodb-memory-server');

dotenv.config();

/**
 * Seed Script - Populate database with test testers
 * Run with: node server/seedTesters.js
 */

const testTesters = [
    { name: "Rahul Sharma", device: "Samsung Galaxy S23", androidVer: "13", email: "rahul@test.com" },
    { name: "Anita Kumar", device: "Google Pixel 7 Pro", androidVer: "14", email: "anita@test.com" },
    { name: "Kevin Dsouza", device: "OnePlus 11", androidVer: "13", email: "kevin@test.com" },
    { name: "Priya Mehta", device: "Vivo X90", androidVer: "12", email: "priya@test.com" },
    { name: "Arjun Reddy", device: "Xiaomi 13 Pro", androidVer: "13", email: "arjun@test.com" },
    { name: "Sneha Patel", device: "Google Pixel 6a", androidVer: "13", email: "sneha@test.com" },
    { name: "Vikram Singh", device: "Nothing Phone 2", androidVer: "14", email: "vikram@test.com" },
    { name: "Meera Joshi", device: "Samsung Galaxy S21", androidVer: "12", email: "meera@test.com" },
    { name: "Rohan Bhatt", device: "Realme GT 2 Pro", androidVer: "11", email: "rohan@test.com" },
    { name: "Kavya Lal", device: "Oppo Reno 10", androidVer: "13", email: "kavya@test.com" },
    { name: "Amit Trivedi", device: "Motorola Edge 40", androidVer: "13", email: "amit@test.com" },
    { name: "Deepa Nair", device: "Samsung Galaxy A54", androidVer: "13", email: "deepa@test.com" },
    { name: "Sanjay Gupta", device: "Poco F5", androidVer: "12", email: "sanjay@test.com" },
    { name: "Neha Verma", device: "iQOO 11", androidVer: "13", email: "neha@test.com" },
    { name: "Karan Malhotra", device: "Asus ROG Phone 7", androidVer: "13", email: "karan@test.com" },
    { name: "Pooja Iyer", device: "Sony Xperia 1 V", androidVer: "13", email: "pooja@test.com" },
    { name: "Aditya Rao", device: "Lenovo Legion Y90", androidVer: "12", email: "aditya@test.com" },
    { name: "Ritu Kapoor", device: "Honor Magic 5 Pro", androidVer: "13", email: "ritu@test.com" },
    { name: "Manish Jain", device: "Tecno Phantom X2", androidVer: "12", email: "manish@test.com" },
    { name: "Divya Pillai", device: "Infinix Zero Ultra", androidVer: "12", email: "divya@test.com" },
    { name: "Suresh Menon", device: "Micromax In Note 2", androidVer: "11", email: "suresh@test.com" },
    { name: "Lakshmi Bhat", device: "Lava Agni 2", androidVer: "13", email: "lakshmi@test.com" },
    { name: "Rajesh Nambiar", device: "Nokia G60", androidVer: "12", email: "rajesh@test.com" },
    { name: "Swati Desai", device: "Motorola Razr 40", androidVer: "13", email: "swati@test.com" },
    { name: "Varun Khanna", device: "Samsung Galaxy Z Fold 5", androidVer: "13", email: "varun@test.com" }
];

async function seedTesters() {
    try {
        // Connect to database (same logic as server)
        console.log("ℹ️ Starting In-Memory MongoDB...");
        const mongod = await MongoMemoryServer.create();
        const uri = mongod.getUri();
        await mongoose.connect(uri);
        console.log("✅ MongoDB Connected");

        // Clear existing testers (optional)
        await User.deleteMany({ role: 'tester' });
        console.log("🗑️  Cleared existing testers");

        // Hash password
        const hashedPassword = await bcrypt.hash('password123', 10);

        // Create testers
        const testerPromises = testTesters.map(async (tester) => {
            const trustScore = (Math.random() * 2 + 3.5).toFixed(1); // Random between 3.5 and 5.5

            return User.create({
                email: tester.email,
                password: hashedPassword,
                role: 'tester',
                displayName: tester.name,
                deviceModel: tester.device,
                androidVersion: tester.androidVer,
                trustScore: parseFloat(trustScore),
                walletBalance: 0,
                activeAssignments: 0,
                maxConcurrentApps: 20,
                appsBeingTested: []
            });
        });

        await Promise.all(testerPromises);

        console.log(`✅ Successfully created ${testTesters.length} test testers!`);
        console.log("📧 All testers have password: password123");

        // Display summary
        const allTesters = await User.find({ role: 'tester' }).select('displayName deviceModel trustScore');
        console.log("\n📋 Tester Summary:");
        allTesters.forEach((t, i) => {
            console.log(`${i + 1}. ${t.displayName} - ${t.deviceModel} (★ ${t.trustScore})`);
        });

        process.exit(0);

    } catch (error) {
        console.error("❌ Seed Error:", error);
        process.exit(1);
    }
}

seedTesters();
