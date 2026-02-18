const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');

const router = express.Router();

// Signup Route
router.post('/signup', async (req, res) => {
    try {
        const { email, password, role, displayName, deviceModel } = req.body;

        // Check if user exists
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ message: "User already exists with this email." });
        }

        // Hash Password
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        // Create User
        const newUser = new User({
            email,
            password: hashedPassword,
            role,
            displayName,
            deviceModel
        });

        await newUser.save();

        // Create Token
        const token = jwt.sign(
            { id: newUser._id, role: newUser.role },
            process.env.JWT_SECRET,
            { expiresIn: '7d' } // Token valid for 7 days
        );

        res.status(201).json({
            message: "User registered successfully",
            token,
            user: {
                id: newUser._id,
                email: newUser.email,
                role: newUser.role,
                displayName: newUser.displayName
            }
        });

    } catch (error) {
        console.error("Signup Error:", error);
        res.status(500).json({ message: "Server error during signup" });
    }
});

// Login Route
router.post('/login', async (req, res) => {
    try {
        const { email, password, role } = req.body;

        // Check for user
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({ message: "Invalid credentials" });
        }

        // Check Password
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ message: "Invalid credentials" });
        }

        // Validate Role (developer can't login as tester and vice versa)
        if (role && user.role !== role) {
            return res.status(403).json({
                message: `This account is registered as a ${user.role}, not a ${role}`
            });
        }

        // Create Token
        const token = jwt.sign(
            { id: user._id, role: user.role },
            process.env.JWT_SECRET,
            { expiresIn: '7d' }
        );

        res.json({
            message: "Login successful",
            token,
            user: {
                id: user._id,
                email: user.email,
                role: user.role,
                displayName: user.displayName
            }
        });

    } catch (error) {
        console.error("Login Error:", error);
        res.status(500).json({ message: "Server error during login" });
    }
});

module.exports = router;
