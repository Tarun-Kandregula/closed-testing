const mongoose = require('mongoose');
const User = require('./models/User'); // Adjust path as needed
const dotenv = require('dotenv');

dotenv.config();

// Connect to MongoDB
mongoose.connect(process.env.MONGO_URI)
    .then(() => console.log("✅ MongoDB Connected for Testing"))
    .catch(err => {
        console.error("❌ MongoDB Connection Error:", err);
        process.exit(1);
    });

const testAuth = async () => {
    try {
        const testUser = {
            email: `test_${Date.now()}@example.com`,
            password: 'password123',
            role: 'developer',
            displayName: 'Test Dev',
            deviceModel: 'Pixel 6'
        };

        console.log("Attempting to create user:", testUser.email);

        // Simulate Signup Logic directly (or use axios to hit the running server)
        // ideally we hit the endpoint using axios/fetch

        // For this script, let's just use axios to hit the local server
        const axios = require('axios');
        const API_URL = 'http://localhost:5001/api/auth';

        // Signup
        try {
            const signupRes = await axios.post(`${API_URL}/signup`, testUser);
            console.log("✅ Signup Successful:", signupRes.data);

            // Login
            const loginRes = await axios.post(`${API_URL}/login`, {
                email: testUser.email,
                password: testUser.password
            });
            console.log("✅ Login Successful:", loginRes.data);

            const token = loginRes.data.token;
            console.log("Token received:", token ? "Yes" : "No");

        } catch (apiError) {
            console.error("❌ API Error:", apiError.response ? apiError.response.data : apiError.message);
        }

    } catch (err) {
        console.error("❌ Test Script Error:", err);
    } finally {
        mongoose.connection.close();
    }
};

// We need axios for this script
// run: npm install axios
testAuth();
