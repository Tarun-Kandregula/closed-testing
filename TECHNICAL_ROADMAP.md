# TwelveTesters (12 Testers 14 Days Solution)
## Project Specification & Technical Roadmap

TwelveTesters is a **Marketplace-as-a-Service (MaaS)** designed to help Android developers bypass the "20 Testers / 14 Days" hurdle for Google Play Store Production access. It connects developers with a verified pool of testers through a Peer-to-Peer (P2P) and Paid testing model.

---

## 🎨 Design System (Theme)
- **Primary Aesthetic:** Cyber-Premium / Dark Space
- **Colors:**
  - Background: `#020617` (Deep Indigo-Black)
  - Card/Surface: `rgba(30, 41, 59, 0.5)` with `12px` blur
  - Primary: `#8B5CF6` (Electric Purple)
  - Secondary: `#06B6D4` (Neon Cyan)
  - Accent/Success: `#10B981` (Emerald Green)
- **Typography:** Inter (Sans-serif), Font weights: 400, 600, 800.

---

## 🛠️ Software Architecture

### 1. Web App (Front-End)
- **Tech Stack:** HTML5, CSS3 (Vanilla), JavaScript, Vite.
- **Purpose:** Handling developer onboarding, app submissions, payment, and analytics.
- **Key Features:**
  - **Role Selector:** Persistent auth state (Developer vs Tester).
  - **Dev Dashboard:** App metadata form + Peer-to-Peer tester selection marketplace.
  - **Selection Bridge:** Copy-paste bridge for Google Console email lists.

### 2. Android App (The Verification Tool)
- **Tech Stack:** Kotlin, Jetpack Compose, Retrofit 2, OkHttp.
- **Purpose:** The actual testing vehicle used by the testers.
- **Key Features:**
  - **Mission List:** Local view of assigned apps from the backend.
  - **App-Link Integration:** Deep links to Play Store for one-tap install.
  - **Verification Engine:** Logic to check if `packageName` is installed and track daily opens.
  - **Wallet:** Native UI to track earnings (₹25/app).

### 3. Backend (API & Orchestration)
- **Tech Stack:** Node.js, Express, MongoDB/Supabase.
- **Purpose:** Central brain for user data, app assignments, and 14-day timers.
- **Key Features:**
  - **Assignment Engine:** Logic to randomly pair testers with apps based on availability.
  - **Reporting Engine:** Automated generation of Day 4 and Day 8 PDF/Email reports.
  - **Scheduler:** Cron jobs to check daily progress and flag dropouts.

---

## 📝 TODO List & Roadmap

### Phase 1: Foundation (COMPLETED ✅)
- [x] Initial premium Design System (CSS).
- [x] Web role-switching prototype.
- [x] Express Server boilerplate.
- [x] Android app skeleton with Jetpack Compose.

### Phase 2: Core Logic (NEXT STEPS 🚀)
- **Backend:**
  - [ ] Implement JWT Authentication (Signup/Login).
  - [ ] Connect Mongoose/Supabase models for `Users` and `Apps`.
  - [ ] Build `/api/verify-checkin` endpoint for the Android app.
- **Web App:**
  - [ ] Create the "Select Testers" grid (Filter testers by device/rating).
  - [ ] Add App Submission form (Submit package name, icon link, tester count).
  - [ ] Connect Fetch API to Backend.
- **Android App:**
  - [ ] Implement Retrofit service for Backend communication.
  - [ ] Build "Verification Logic": Use `packageManager` to check if app is installed.
  - [ ] Implement local SQLite (Room) to track time spent in app.

### Phase 3: Automation & Trust
- [ ] **Reporting:** Build the email auto-sender for the 4th/8th day.
- [ ] **Payments:** Integrate Stripe or Razorpay for developer payments.
- [ ] **Withdrawals:** Build the payout gateway for Testers (UPI/Bank).

---

## 🤖 AI Context (For Future Agents)
- **Root Dir:** Web Assets.
- **`/server` Dir:** Express API. Use `npm run dev` to start.
- **`/app` Dir:** Android Studio project. Uses Compose + Material3.
- **Critical File:** `app/src/main/java/com/tk/a12testers14days/MainActivity.kt` contains the core UI logic for the mobile app.
- **Backend Target:** The backend is currently using mock arrays. Transition these to `mongoose.Schema` next.
