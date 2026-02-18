
/* ============================================
   THEME MANAGEMENT
   ============================================ */
let currentTheme = localStorage.getItem('theme') || 'dark';
let sidebarCollapsed = false;

// Apply theme from localStorage on page load
function initTheme() {
    document.documentElement.setAttribute('data-theme', currentTheme);
    updateThemeIcon();
}

function toggleTheme() {
    currentTheme = currentTheme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', currentTheme);
    localStorage.setItem('theme', currentTheme);
    updateThemeIcon();
}

function updateThemeIcon() {
    const themeIcon = document.getElementById('theme-icon');
    if (themeIcon) {
        themeIcon.setAttribute('data-lucide', currentTheme === 'dark' ? 'sun' : 'moon');
        lucide.createIcons();
    }
}

/* ============================================
   NAVIGATION MANAGEMENT
   ============================================ */
function toggleSidebar() {
    const appContainer = document.getElementById('app-container');
    sidebarCollapsed = !sidebarCollapsed;

    if (sidebarCollapsed) {
        appContainer.classList.add('sidebar-collapsed');
    } else {
        appContainer.classList.remove('sidebar-collapsed');
    }
}

function navigateTo(page) {
    // Update page title
    const pageTitles = {
        'my-apps': 'My Apps',
        'add-app': 'Add New App',
        'my-tests': 'My Tests',
        'browse-apps': 'Browse Apps',
        'analytics': 'Analytics',
        'wallet': 'Wallet',
        'stats': 'Statistics'
    };

    document.getElementById('page-title').textContent = pageTitles[page] || '';

    // Update active nav item (sidebar)
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('data-page') === page) {
            item.classList.add('active');
        }
    });

    // Update active nav item (bottom nav for mobile)
    document.querySelectorAll('.bottom-nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('data-page') === page) {
            item.classList.add('active');
        }
    });

    // Hide all sections
    document.querySelectorAll('.main-content > section').forEach(section => {
        section.classList.add('hidden');
    });

    // Explicitly hide edit view and detail views
    document.getElementById('edit-app-view').classList.add('hidden');
    document.getElementById('app-detail-view').classList.add('hidden');
    document.getElementById('tester-app-detail-view').classList.add('hidden');

    // Show appropriate section based on page
    if (page === 'apps' || page === 'my-apps') {
        document.getElementById('dev-dashboard').classList.remove('hidden');
        document.getElementById('app-list-view').classList.remove('hidden');
    } else if (page === 'add-app') {
        document.getElementById('add-app-view').classList.remove('hidden');
    } else if (page === 'my-tests') {
        document.getElementById('tester-dashboard').classList.remove('hidden');
        document.getElementById('my-tests-view').classList.remove('hidden');
        document.getElementById('browse-apps-view').classList.add('hidden');
        renderTesterMissions();
    } else if (page === 'browse-apps') {
        document.getElementById('tester-dashboard').classList.remove('hidden');
        document.getElementById('my-tests-view').classList.add('hidden');
        document.getElementById('browse-apps-view').classList.remove('hidden');
        renderTesterMissions();
    } else if (page === 'analytics') {
        document.getElementById('analytics-section').classList.remove('hidden');
    } else if (page === 'wallet') {
        document.getElementById('wallet-section').classList.remove('hidden');
    } else if (page === 'stats') {
        document.getElementById('stats-section').classList.remove('hidden');
    }
}

const API_URL = 'http://192.168.1.21:5001/api';

// State Management
let currentUser = {
    role: null,
    isLoggedIn: false,
    token: localStorage.getItem('token') || null,
    details: JSON.parse(localStorage.getItem('user')) || null
};

let selectedTesters = new Set();
const TESTER_PRICE = 25; // ₹25 per tester

// Helper function to generate status badges with consistent styling
function getStatusBadge(status, customText = null) {
    const text = customText || status.replace(/_/g, ' ');
    const className = `status-badge status-${status.toLowerCase().replace(/ /g, '-')}`;
    return `<span class="${className}">${text}</span>`;
}


// Mock Data (still used for tester list for now)
const mockTesters = [
    { id: 1, name: "Rahul S.", device: "Samsung S23", rating: 4.8, androidVer: "13" },
    { id: 2, name: "Anita K.", device: "Pixel 7 Pro", rating: 4.9, androidVer: "14" },
    { id: 3, name: "Kevin D.", device: "OnePlus 11", rating: 4.7, androidVer: "13" },
    { id: 4, name: "Priya M.", device: "Vivo X90", rating: 4.6, androidVer: "12" },
    { id: 5, name: "Arjun R.", device: "Xiaomi 13", rating: 4.8, androidVer: "13" },
    { id: 6, name: "Sneha P.", device: "Pixel 6a", rating: 4.9, androidVer: "13" },
    { id: 7, name: "Vikram S.", device: "Nothing Phone 2", rating: 4.7, androidVer: "14" },
    { id: 8, name: "Meera J.", device: "Samsung S21", rating: 4.5, androidVer: "12" },
    { id: 9, name: "Rohan B.", device: "Realme GT", rating: 4.6, androidVer: "11" },
    { id: 10, name: "Kavya L.", device: "Oppo Reno 10", rating: 4.8, androidVer: "13" },
    { id: 11, name: "Amit T.", device: "Moto Edge 40", rating: 4.7, androidVer: "13" },
    { id: 12, name: "Deepa N.", device: "Samsung A54", rating: 4.6, androidVer: "13" }
];

/* --- Auth Functions --- */
function checkAuth() {
    if (currentUser.token && currentUser.details) {
        currentUser.isLoggedIn = true;
        currentUser.role = currentUser.details.role;
        updateUIForLogin();
    }
}

function selectRole(role) {
    // If not logged in, show auth modal
    if (!currentUser.isLoggedIn) {
        showAuthModal(role);
        return;
    }

    // Logic if already logged in but maybe switching roles (not supported yet really)
    updateUIForLogin();
}

let isSignup = false;
let pendingRole = null;

function showAuthModal(role) {
    pendingRole = role;
    const modal = document.getElementById('auth-modal');
    modal.classList.remove('hidden'); // Fix: Remove hidden class
    modal.classList.add('active');
    isSignup = false; // Default to login
    updateAuthModalUI();
}

function closeAuthModal() {
    const modal = document.getElementById('auth-modal');
    modal.classList.remove('active');
    modal.classList.add('hidden'); // Add hidden class back
}

function toggleAuthMode() {
    isSignup = !isSignup;
    updateAuthModalUI();
}

function updateAuthModalUI() {
    const title = document.getElementById('auth-title');
    const nameGroup = document.getElementById('name-group');
    const switchText = document.getElementById('auth-switch-text');
    const btn = document.querySelector('#auth-form button');

    if (isSignup) {
        title.innerText = `Sign Up as ${pendingRole.charAt(0).toUpperCase() + pendingRole.slice(1)}`;
        nameGroup.style.display = 'block';
        btn.innerText = 'Create Account';
        switchText.innerText = 'Already have an account? Login';
    } else {
        title.innerText = 'Login';
        nameGroup.style.display = 'none';
        btn.innerText = 'Login';
        switchText.innerText = 'New here? Create an account';
    }
}

async function handleAuth(event) {
    event.preventDefault();
    const email = document.getElementById('auth-email').value;
    const password = document.getElementById('auth-password').value;
    const name = document.getElementById('auth-name').value;


    const endpoint = isSignup ? '/auth/signup' : '/auth/login';
    const body = { email, password, role: pendingRole };

    if (isSignup) {
        body.displayName = name;
        // Default device for demo
        body.deviceModel = "Web Browser";
    }

    try {
        const res = await fetch(`${API_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        const data = await res.json();

        if (!res.ok) throw new Error(data.message);

        // Success
        currentUser.token = data.token;
        currentUser.details = data.user;
        currentUser.isLoggedIn = true;
        currentUser.role = data.user.role;

        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));

        closeAuthModal();
        updateUIForLogin();
        alert(`Welcome, ${data.user.displayName}!`);

    } catch (err) {
        alert(err.message);
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    location.reload();
}


/* --- App Functions --- */

async function renderDeveloperApps() {
    const tbody = document.getElementById('dev-apps-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-dim" style="text-align: center; padding: 2rem;">Loading apps...</td></tr>';

    try {
        const res = await fetch(`${API_URL}/apps/my-apps`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const apps = await res.json();

        if (apps.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center; padding: 3rem;">
                        <p class="text-dim">You haven't submitted any apps yet.</p>
                        <button class="btn btn-primary" onclick="showAppForm()" style="margin-top: 1rem;">
                            Submit First App
                        </button>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = '';
        apps.forEach(app => {
            const testersInfo = app.currentTesters && app.currentTesters.length > 0
                ? `${app.currentTesters.length}/${app.testersRequired} assigned`
                : `${app.optedInTesters ? app.optedInTesters.length : 0} opted in`;

            tbody.innerHTML += `
                <tr onclick="showAppDetail('${app._id}')" style="cursor: pointer;">
                    <td>
                        <div style="display: flex; align-items: center; gap: 0.75rem;">
                            ${app.appIcon ? `<img src="${app.appIcon}" style="width: 32px; height: 32px; border-radius: 6px;" />` : ''}
                            <span style="font-weight: 500;">${app.appName}</span>
                        </div>
                    </td>
                    <td class="text-dim">v${app.appVersion || '1.0'}</td>
                    <td>${getStatusBadge(app.status)}</td>
                    <td>
                        <div style="display: flex; flex-direction: column; gap: 4px; min-width: 120px;">
                            <span style="font-weight: 500;">${app.status === 'testing' ? `Day ${app.testingStartedAt ? Math.floor((Date.now() - new Date(app.testingStartedAt)) / (1000 * 60 * 60 * 24)) : 0}` : 'Day 0'} / ${app.durationDays || 15}</span>
                            <div style="width: 100%; height: 6px; background: var(--border); border-radius: 3px; overflow: hidden;">
                                <div style="width: ${app.status === 'testing' && app.testingStartedAt ? Math.min((Math.floor((Date.now() - new Date(app.testingStartedAt)) / (1000 * 60 * 60 * 24)) / (app.durationDays || 15)) * 100, 100) : 0}%; height: 100%; background: var(--primary);"></div>
                            </div>
                        </div>
                    </td>
                    <td class="text-dim">${testersInfo}</td>
                    <td>
                        <button class="btn btn-outline" style="padding: 6px 16px; font-size: 0.85rem;" 
                            onclick="event.stopPropagation(); openEditAppView('${app._id}')">
                            View
                        </button>
                    </td>
                </tr>
            `;
        });

        lucide.createIcons();

    } catch (err) {
        console.error(err);
        tbody.innerHTML = '<tr><td colspan="6" style="color: red; text-align: center;">Failed to load apps.</td></tr>';
    }
}

async function renderTesterMissions() {
    const availableBody = document.getElementById('available-apps-body');
    const myTestsBody = document.getElementById('my-tests-body');

    // Add loading states
    if (availableBody) availableBody.innerHTML = '<tr><td colspan="6" class="text-dim" style="text-align: center; padding: 2rem;">Loading available apps...</td></tr>';
    if (myTestsBody) myTestsBody.innerHTML = '<tr><td colspan="6" class="text-dim" style="text-align: center; padding: 2rem;">Loading your tests...</td></tr>';

    try {
        // Parallel fetch for better performance
        const [myTestsRes, availableRes] = await Promise.all([
            fetch(`${API_URL}/apps/my-tests`, { headers: { 'Authorization': `Bearer ${currentUser.token}` } }),
            fetch(`${API_URL}/apps/available`, { headers: { 'Authorization': `Bearer ${currentUser.token}` } })
        ]);

        const myTests = await myTestsRes.json();
        const availableApps = await availableRes.json();

        // Render My Tests
        if (myTests.length === 0) {
            myTestsBody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: var(--text-dim); padding: 2rem;">You haven\'t opted into any apps yet.</td></tr>';
        } else {
            myTestsBody.innerHTML = '';
            myTests.forEach(app => {
                const myTester = app.optedInTesters.find(t => t.testerId._id === currentUser.details.id);
                const daysCompleted = myTester?.daysCompleted || 0;
                const statusText = app.status === 'testing' ? 'Active' : 'Waiting';

                myTestsBody.innerHTML += `
                    <tr>
                        <td style="font-weight: 500; color: var(--text-primary);">${app.appName}</td>
                        <td>v${app.appVersion || '1.0'}</td>
                        <td>${app.developerId?.displayName || 'Unknown Developer'}</td>
                        <td>${getStatusBadge(app.status === 'testing' ? 'active' : 'waiting', statusText)}</td>
                        <td>
                            <div style="display: flex; flex-direction: column; gap: 4px;">
                                <span style="font-weight: 500;">Day ${daysCompleted || 0} / ${app.durationDays || 15}</span>
                                <div style="width: 100%; height: 6px; background: var(--border); border-radius: 3px; overflow: hidden;">
                                    <div style="width: ${(daysCompleted / (app.durationDays || 15)) * 100}%; height: 100%; background: var(--primary);"></div>
                                </div>
                            </div>
                        </td>
                        <td>
                            <button class="btn btn-outline" onclick="openTesterAppDetail('${app._id}', 'my-test')" style="padding: 6px 12px; font-size: 0.85rem;">
                                View
                            </button>
                        </td>
                    </tr>
                `;
            });
        }

        // Render Available Apps
        if (availableApps.length === 0) {
            availableBody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: var(--text-dim); padding: 2rem;">No apps available for opt-in right now.</td></tr>';
        } else {
            availableBody.innerHTML = '';
            availableApps.forEach(app => {
                const slotsRemaining = (app.maxTesters || 20) - (app.optedInTesters?.length || 0);
                const paymentPerTester = calculatePaymentPerTester(app.paymentAmount || 399, app.maxTesters || 20);

                availableBody.innerHTML += `
                    <tr>
                        <td style="font-weight: 500; color: var(--text-primary);">${app.appName}</td>
                        <td>v${app.appVersion || '1.0'}</td>
                        <td>${app.developerId?.displayName || 'Unknown Developer'}</td>
                        <td style="color: var(--success); font-weight: bold;">₹${paymentPerTester.toFixed(0)}</td>
                        <td>${slotsRemaining} left</td>
                        <td>
                            <button class="btn btn-outline" onclick="openTesterAppDetail('${app._id}', 'browse')" style="padding: 6px 12px; font-size: 0.85rem;">
                                View
                            </button>
                        </td>
                    </tr>
                `;
            });
        }

    } catch (err) {
        console.error(err);
        if (availableBody) {
            availableBody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: red; padding: 2rem;">Failed to load available apps.</td></tr>';
        }
    }
}

function calculatePaymentPerTester(totalPayment, maxTesters) {
    const commission = 50; // Platform commission
    const testerPool = totalPayment - commission;
    return testerPool / maxTesters;
}

async function optIntoApp(appId, appName) {
    const confirmed = await showConfirm(`Opt in to test "${appName}" for 15 days?\n\nYou'll need to install and use the app daily for 15 days.`, '🎯 Opt-In Confirmation');
    if (!confirmed) return;

    try {
        const res = await fetch(`${API_URL}/apps/${appId}/opt-in`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            }
        });

        const data = await res.json();

        if (!res.ok) throw new Error(data.message);

        await showAlert('Successfully opted in! This app has been moved to "My Tests".', '✅ Opt-In Successful');

        // Refresh data
        await renderTesterMissions();

        // Navigate back to dashboard
        showTesterDashboard();
        window.scrollTo(0, 0);

    } catch (err) {
        await showAlert(err.message, '❌ Opt-In Failed');
    }
}

let currentTesterAppId = null;

async function openTesterAppDetail(appId, mode) {
    currentTesterAppId = appId;

    // Hide dashboards, Show Detail
    document.getElementById('my-tests-view').classList.add('hidden');
    document.getElementById('browse-apps-view').classList.add('hidden');
    document.getElementById('tester-app-detail-view').classList.remove('hidden');

    // Update Header
    document.getElementById('page-title').textContent = 'App Details';

    try {
        let app;
        if (mode === 'my-test') {
            const res = await fetch(`${API_URL}/apps/my-tests`, { headers: { 'Authorization': `Bearer ${currentUser.token}` } });
            const apps = await res.json();
            app = apps.find(a => a._id === appId);
        } else {
            const res = await fetch(`${API_URL}/apps/available`, { headers: { 'Authorization': `Bearer ${currentUser.token}` } });
            const apps = await res.json();
            app = apps.find(a => a._id === appId);
        }

        if (!app) throw new Error("App not found");

        // Populate Fields
        document.getElementById('tester-detail-name').value = app.appName;
        document.getElementById('tester-detail-version').value = app.appVersion || '1.0';
        document.getElementById('tester-detail-description').value = app.appDescription || 'No description provided.';

        const payment = calculatePaymentPerTester(app.paymentAmount || 399, app.maxTesters || 20);
        document.getElementById('tester-detail-reward').value = `₹${payment.toFixed(0)}`;
        document.getElementById('tester-detail-duration').value = `${app.durationDays || 14} Days`;
        document.getElementById('tester-detail-instructions').value = app.testingInstructions || 'No specific instructions.';

        // Google Play Link
        const linkGroup = document.getElementById('tester-play-link-group');
        const linkAnchor = document.getElementById('tester-detail-link');

        if (mode === 'my-test') {
            linkGroup.style.display = 'block';
            if (app.status === 'testing' && app.closedTestingLink) {
                linkAnchor.href = app.closedTestingLink;
                linkAnchor.textContent = app.closedTestingLink;
                linkAnchor.style.pointerEvents = 'auto';
                linkAnchor.style.color = 'var(--primary)';
            } else {
                linkAnchor.textContent = "Link will be available when testing starts.";
                linkAnchor.removeAttribute('href');
                linkAnchor.style.pointerEvents = 'none';
                linkAnchor.style.color = 'var(--text-dim)';
            }
        } else {
            linkGroup.style.display = 'none';
        }

        // Action Button
        const btnContainer = document.getElementById('tester-action-container');
        btnContainer.innerHTML = ''; // Clear previous buttons

        if (mode === 'my-test') {
            const myTester = app.optedInTesters.find(t => t.testerId._id === currentUser.details.id);
            const daysCompleted = myTester?.daysCompleted || 0;
            const completed = myTester?.completed || false;

            // Progress Display
            const progress = document.createElement('div');
            progress.style.marginBottom = '1rem';
            progress.style.width = '100%';
            progress.innerHTML = `
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                    <strong>Progress: Day ${daysCompleted} / ${app.durationDays || 15}</strong>
                    <span class="status-badge status-${completed ? 'completed' : 'testing'}">${completed ? 'Completed' : 'Active'}</span>
                </div>
                <div style="width: 100%; height: 8px; background: var(--border); border-radius: 4px; overflow: hidden;">
                    <div style="width: ${(daysCompleted / (app.durationDays || 15)) * 100}%; height: 100%; background: var(--success);"></div>
                </div>
            `;
            // Insert progress before buttons if not already there (this is hacky, better to have a dedicated container)
            // But since we are clearing btnContainer which is just a flex div at bottom, maybe we should prepend it?
            // Actually, btnContainer is `div style="display: flex; ..."` at bottom. 
            // Let's just add the progress bar above the buttons inside this container but make the container column?
            // No, the container is row.

            // To be safe, let's just add the Check In button to the container.

            // Check In Button (Disabled)
            const checkInBtn = document.createElement('button');
            checkInBtn.className = 'btn btn-secondary';
            checkInBtn.style.marginRight = '1rem';
            checkInBtn.style.opacity = '0.6';
            checkInBtn.style.cursor = 'not-allowed';
            checkInBtn.innerHTML = '<i data-lucide="smartphone"></i> Check In';
            checkInBtn.title = "Open the 12Testers Android App to verify installation and check in.";
            checkInBtn.onclick = () => alert("Please open the 12Testers Android App to verify installation and check in.");

            btnContainer.appendChild(checkInBtn);

            // Opt Out Button
            const optOutBtn = document.createElement('button');
            optOutBtn.textContent = 'Opt Out';
            optOutBtn.className = 'btn btn-danger';
            optOutBtn.onclick = () => showCustomDialog(appId, app.appName);
            btnContainer.appendChild(optOutBtn);

        } else {
            // Opt In Button
            const optInBtn = document.createElement('button');
            optInBtn.textContent = 'Opt In';
            optInBtn.className = 'btn btn-primary';
            optInBtn.id = 'tester-action-btn'; // Restore ID for next time? logic relies on ID to find parent.
            optInBtn.onclick = () => optIntoApp(appId, app.appName);
            btnContainer.appendChild(optInBtn);
        }

        // Re-assign ID to one button so next call can find parent? 
        // Or just find parent by ID of the container if it had one. 
        // The container doesn't have an ID in previous view, just style.
        // Let's check index.html to see if we can give the container an ID.

    } catch (err) {
        console.error(err);
        showTesterDashboard();
    }
}

function showTesterDashboard() {
    navigateTo('my-tests');
}

/* --- Edit App Functions (Full Screen) --- */

let currentEditAppId = null;

async function openEditAppView(appId) {
    currentEditAppId = appId;

    // Hide other views
    document.getElementById('dev-dashboard').classList.add('hidden');
    document.getElementById('app-list-view').classList.add('hidden');
    document.getElementById('edit-app-view').classList.remove('hidden');

    // Ensure detail view is hidden
    const detailView = document.getElementById('app-detail-view');
    if (detailView) detailView.classList.add('hidden');

    // Update Header Title
    document.getElementById('page-title').textContent = 'Edit App';

    try {
        const res = await fetch(`${API_URL}/apps/my-apps`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const apps = await res.json();
        const app = apps.find(a => a._id === appId);

        if (!app) throw new Error("App not found");

        // If app is in testing status, show testing dashboard instead of edit form
        if (app.status === 'testing') {
            showDeveloperTestingDashboard(app);
            return;
        }

        // Otherwise, show edit form (for opt_in_period status)

        document.getElementById('edit-app-id').value = app._id;
        document.getElementById('edit-app-name').value = app.appName;
        if (document.getElementById('edit-app-package-name')) {
            document.getElementById('edit-app-package-name').value = app.packageName || '';
        }
        document.getElementById('edit-app-version').value = app.appVersion || '';
        document.getElementById('edit-app-description').value = app.appDescription || '';
        document.getElementById('edit-closed-testing-link').value = app.closedTestingLink || '';
        document.getElementById('edit-payment-amount').value = app.paymentAmount || 399;

        // New layout keys
        if (document.getElementById('edit-testers-needed')) {
            document.getElementById('edit-testers-needed').value = app.maxTesters || 20;
        }
        if (document.getElementById('edit-testing-duration')) {
            document.getElementById('edit-testing-duration').value = app.durationDays || 15;
        }
        if (document.getElementById('edit-testing-instructions')) {
            document.getElementById('edit-testing-instructions').value = app.testingInstructions || '';
        }

        // Render Start Test Button
        const btnContainer = document.getElementById('start-test-btn-container');
        if (btnContainer && app.status) {
            if (app.status === 'opt_in_period') {
                const currentTesters = app.optedInTesters ? app.optedInTesters.length : 0;
                const maxTesters = app.maxTesters || 20;
                const canStartTest = currentTesters >= maxTesters;

                if (canStartTest) {
                    btnContainer.innerHTML = `
                        <button class="btn btn-primary" onclick="startTest('${app._id}')" style="padding: 1rem 2rem; background: var(--success); border-color: var(--success);">
                            Start Testing Period
                        </button>
                    `;
                } else {
                    btnContainer.innerHTML = `
                        <button class="btn" disabled style="padding: 1rem 2rem; opacity: 0.5; cursor: not-allowed; background: var(--surface); color: var(--text-dim); border: 1px solid var(--border);">
                            Waiting for Testers (${currentTesters}/${maxTesters})
                        </button>
                    `;
                }
            } else if (app.status === 'testing') {
                btnContainer.innerHTML = `
                    <div style="padding: 1rem 2rem; background: rgba(16, 185, 129, 0.1); color: var(--success); border-radius: 8px; font-weight: bold;">
                        Status: TESTING
                    </div>
                `;
            } else {
                btnContainer.innerHTML = '';
            }
        }

    } catch (err) {
        await showAlert("Failed to load app details for editing.");
        showAppList();
    }
}

async function showDeveloperTestingDashboard(app) {
    // Hide other views
    document.getElementById('dev-dashboard').classList.add('hidden');
    document.getElementById('app-list-view').classList.add('hidden');
    document.getElementById('edit-app-view').classList.add('hidden');
    document.getElementById('developer-testing-dashboard').classList.remove('hidden');

    // Ensure detail view is hidden
    const detailView = document.getElementById('app-detail-view');
    if (detailView) detailView.classList.add('hidden');

    // Update Header Title
    document.getElementById('page-title').textContent = 'Testing Dashboard';

    // Populate app info
    document.getElementById('dashboard-app-name').textContent = app.appName;
    document.getElementById('dashboard-package-name').textContent = app.packageName || '-';
    document.getElementById('dashboard-version').textContent = `v${app.appVersion || '1.0'}`;
    document.getElementById('dashboard-duration').textContent = `${app.durationDays || 15} days`;
    document.getElementById('dashboard-payment').textContent = `₹${app.paymentAmount || 399}`;
    document.getElementById('dashboard-description').textContent = app.appDescription || 'No description provided';

    const testingLink = document.getElementById('dashboard-testing-link');
    testingLink.href = app.closedTestingLink || '#';
    testingLink.textContent = app.closedTestingLink || '-';

    // Populate testers list
    const testersList = document.getElementById('dashboard-testers-list');
    const testerCount = document.getElementById('dashboard-tester-count');
    const testers = app.optedInTesters || [];

    testerCount.textContent = `(${testers.length})`;

    if (testers.length === 0) {
        testersList.innerHTML = '<p class="text-dim" style="text-align: center; padding: 2rem;">No testers have opted in yet.</p>';
    } else {
        testersList.innerHTML = testers.map(tester => {
            const daysCompleted = tester.daysCompleted || 0;
            const totalDays = app.durationDays || 15;
            const progress = Math.min((daysCompleted / totalDays) * 100, 100);
            const hasCheckedInToday = tester.lastCheckIn &&
                new Date(tester.lastCheckIn).toDateString() === new Date().toDateString();

            return `
                <div style="padding: 1.5rem; background: rgba(139, 92, 246, 0.05); border-radius: 8px; border: 1px solid var(--border);">
                    <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 1rem;">
                        <div>
                            <p style="margin: 0; font-weight: 600; font-size: 1.1rem;">${tester.testerId.displayName || 'Unknown'}</p>
                            <p class="text-dim" style="margin: 0.25rem 0 0 0; font-size: 0.9rem;">${tester.testerId.email}</p>
                        </div>
                        <div style="text-align: right;">
                            <span style="display: inline-block; padding: 0.25rem 0.75rem; background: ${hasCheckedInToday ? 'var(--success)' : 'var(--border)'}; color: ${hasCheckedInToday ? 'white' : 'var(--text-dim)'}; border-radius: 12px; font-size: 0.75rem; font-weight: 600;">
                                ${hasCheckedInToday ? '✓ Checked In' : 'Not Checked In'}
                            </span>
                        </div>
                    </div>
                    
                    <div style="margin-top: 1rem;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                            <span class="text-dim" style="font-size: 0.85rem;">Progress</span>
                            <span style="font-weight: 600; color: var(--primary);">Day ${daysCompleted} / ${totalDays}</span>
                        </div>
                        <div style="width: 100%; height: 8px; background: var(--border); border-radius: 4px; overflow: hidden;">
                            <div style="width: ${progress}%; height: 100%; background: var(--primary); transition: width 0.3s ease;"></div>
                        </div>
                    </div>
                    
                    ${tester.testerId.deviceModel ? `
                        <div style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--border);">
                            <p class="text-dim" style="margin: 0; font-size: 0.85rem;">Device: ${tester.testerId.deviceModel} (Android ${tester.testerId.androidVersion || 'Unknown'})</p>
                        </div>
                    ` : ''}
                </div>
            `;
        }).join('');
    }

    // Fetch and Render Bug Reports
    try {
        const bugRes = await fetch(`${API_URL}/bugs/app/${app._id}`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const bugs = await bugRes.json();
        renderBugReports(bugs, app._id);
    } catch (err) {
        console.error("Failed to fetch bugs", err);
        const bugContainer = document.querySelector('#developer-testing-dashboard .glass-card:nth-of-type(3)'); // Assuming 3rd card is bugs
        if (bugContainer) bugContainer.innerHTML = '<p style="color:red; text-align:center;">Failed to load bug reports.</p>';
    }
}

function showAppList() {
    navigateTo('apps');
}

async function submitAppUpdate(e) {
    e.preventDefault();
    if (!currentEditAppId) return;

    const updates = {
        appName: document.getElementById('edit-app-name').value,
        packageName: document.getElementById('edit-app-package-name').value,
        appVersion: document.getElementById('edit-app-version').value,
        appDescription: document.getElementById('edit-app-description').value,
        closedTestingLink: document.getElementById('edit-closed-testing-link').value,
        paymentAmount: parseInt(document.getElementById('edit-payment-amount').value),
        maxTesters: parseInt(document.getElementById('edit-testers-needed').value),
        durationDays: parseInt(document.getElementById('edit-testing-duration').value)
    };

    // Capture new fields
    const instructions = document.getElementById('edit-testing-instructions').value;
    if (instructions) updates.testingInstructions = instructions;

    try {
        const res = await fetch(`${API_URL}/apps/${currentEditAppId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            },
            body: JSON.stringify(updates)
        });

        if (!res.ok) {
            const data = await res.json();
            throw new Error(data.message || 'Update failed');
        }

        await showAlert('App updated successfully!', 'Success');
        navigateTo('my-apps');
        if (window.renderDeveloperApps) renderDeveloperApps();

    } catch (err) {
        await showAlert(err.message, 'Update Failed');
    }
}

/* --- App Submission Modal Functions --- */

function showAppForm() {
    navigateTo('add-app');
}

function closeAppForm() {
    const modal = document.getElementById('app-modal');
    modal.classList.remove('active');
    modal.classList.add('hidden');
    // Reset form
    document.getElementById('app-submission-form').reset();
}



async function submitApp(event) {
    event.preventDefault();
    const appName = document.getElementById('app-name').value;
    const packageName = document.getElementById('app-package-name').value;
    const appVersion = document.getElementById('app-version').value;
    const closedTestingLink = document.getElementById('closed-testing-link').value;
    const appDescription = document.getElementById('app-description').value;
    // const appIcon = document.getElementById('app-icon').value; // Removed from form? If not present, ignore.
    const paymentAmount = parseInt(document.getElementById('total-cost').value);
    const maxTesters = parseInt(document.getElementById('testers-needed').value);
    const durationDays = parseInt(document.getElementById('testing-duration').value);

    try {
        // Validate minimums
        if (paymentAmount < 399) throw new Error("Minimum cost is 399");
        if (maxTesters < 1 || maxTesters > 20) throw new Error("Testers must be between 1 and 20");
        if (durationDays < 1 || durationDays > 15) throw new Error("Duration must be between 1 and 15 days");

        const res = await fetch(`${API_URL}/apps/submit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            },
            body: JSON.stringify({
                appName,
                packageName,
                appVersion,
                closedTestingLink,
                appDescription,
                paymentAmount,
                maxTesters,
                durationDays
            })
        });

        const data = await res.json();

        if (!res.ok) throw new Error(data.message);

        await showAlert(`App "${appName}" submitted successfully! Payment of ${paymentAmount} recorded.`, '✅ App Submitted');

        // Clear Form
        document.getElementById('app-submission-form').reset();

        // Navigate
        navigateTo('my-apps');
        if (window.renderDeveloperApps) renderDeveloperApps();

    } catch (err) {
        await showAlert(err.message, '❌ Submission Failed');
    }
}

// Initial Lucide call for dynamic content
window.addEventListener('load', () => {
    checkAuth(); // Check for existing details
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});

// Expose functions to window for HTML onclick handlers
window.selectRole = selectRole;
window.handleAuth = handleAuth;
window.closeAuthModal = closeAuthModal;
window.toggleAuthMode = toggleAuthMode;
window.logout = logout;
window.showAppForm = showAppForm;
window.closeAppForm = closeAppForm;
window.submitApp = submitApp;
window.optIntoApp = optIntoApp;
window.showAppDetail = showAppDetail;
window.showAppList = showAppList;
window.openTesterAppDetail = openTesterAppDetail;
window.showTesterDashboard = showTesterDashboard;
window.startTest = startTest;
window.closeCustomDialog = closeCustomDialog;
window.openEditAppView = openEditAppView;
window.submitAppUpdate = submitAppUpdate;
window.toggleTheme = toggleTheme;
window.toggleSidebar = toggleSidebar;
window.navigateTo = navigateTo;

// App Detail Functions
async function showAppDetail(appId) {
    const listView = document.getElementById('app-list-view');
    const detailView = document.getElementById('app-detail-view');
    const detailContent = document.getElementById('app-detail-content');

    listView.classList.add('hidden');
    detailView.classList.remove('hidden');

    // Ensure edit view is hidden
    const editView = document.getElementById('edit-app-view');
    if (editView) editView.classList.add('hidden');

    try {
        const res = await fetch(`${API_URL}/apps/${appId}`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const app = await res.json();

        if (!res.ok) throw new Error(app.message);

        const statusColor = app.status === 'testing' ? 'var(--success)' : app.status === 'opt_in_period' ? 'var(--primary)' : 'var(--warning)';
        const currentTesters = app.optedInTesters ? app.optedInTesters.length : 0;
        const maxTesters = app.maxTesters || 20;
        const canStartTest = app.status === 'opt_in_period' && currentTesters >= maxTesters;

        detailContent.innerHTML = `
            <div class="glass-card" style="padding: 2rem;">
                <!-- Header -->
                <div style="display: flex; align-items: start; gap: 1.5rem;margin-bottom: 2rem;">
                    ${app.appIcon ? `<img src="${app.appIcon}" style="width: 80px; height: 80px; border-radius: 12px;" />` : ''}
                    <div style="flex: 1;">
                        <h2 style="margin: 0 0 0.5rem 0;">${app.appName}</h2>
                        <p class="text-dim" style="margin: 0.25rem 0;">Version: ${app.appVersion || '1.0'}</p>
                        <p class="text-dim" style="margin: 0.25rem 0;">
                            Status: <span style="color: ${statusColor}; font-weight: bold;">${app.status.replace('_', ' ').toUpperCase()}</span>
                        </p>
                        <p class="text-dim" style="margin: 0.25rem 0;">
                            <a href="${app.closedTestingLink}" target="_blank" style="color: var(--secondary);">🔗 Testing Link</a>
                        </p>
                    </div>
                    ${app.status === 'opt_in_period' ? `
                        <div style="text-align: right;">
                             ${canStartTest ? `
                                <button class="btn btn-primary" onclick="startTest('${app._id}')" style="padding: 12px 24px;">
                                    Start Testing Period
                                </button>
                             ` : `
                                <button class="btn" disabled style="padding: 12px 24px; opacity: 0.5; cursor: not-allowed; background: var(--surface); color: var(--text-dim); border: 1px solid var(--border);">
                                    Waiting for Testers (${currentTesters}/${maxTesters})
                                </button>
                             `}
                        </div>
                    ` : ''}
                </div>

                <!-- Description -->
                ${app.appDescription ? `
                    <div style="margin-bottom: 2rem;">
                        <h3 style="margin: 0 0 0.5rem 0;">Description</h3>
                        <p class="text-dim" style="margin: 0;">${app.appDescription}</p>
                    </div>
                ` : ''}

                <!-- Stats -->
                <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 2rem;">
                    <div style="padding: 1rem; background: rgba(139, 92, 246, 0.1); border-radius: 8px;">
                        <p class="text-dim" style="margin: 0; font-size: 0.85rem;">Payment Amount</p>
                        <p style="margin: 0.25rem 0 0 0; font-size: 1.5rem; font-weight: bold; color: var(--success);">₹${app.paymentAmount || 399}</p>
                    </div>
                    <div style="padding: 1rem; background: rgba(6, 182, 212, 0.1); border-radius: 8px;">
                        <p class="text-dim" style="margin: 0; font-size: 0.85rem;">Opted-In Testers</p>
                        <p style="margin: 0.25rem 0 0 0; font-size: 1.5rem; font-weight: bold; color: var(--primary);">${app.optedInTesters?.length || 0}/${app.maxTesters || 20}</p>
                    </div>
                    <div style="padding: 1rem; background: rgba(16, 185, 129, 0.1); border-radius: 8px;">
                        <p class="text-dim" style="margin: 0; font-size: 0.85rem;">Created</p>
                        <p style="margin: 0.25rem 0 0 0; font-size: 1rem; font-weight: bold; color: var(--text);">${new Date(app.createdAt).toLocaleDateString()}</p>
                    </div>
                </div>

                <!-- Opted-In Testers -->
                <div>
                    <h3 style="margin: 0 0 1rem 0;">Opted-In Testers ${app.optedInTesters?.length ? `(${app.optedInTesters.length})` : ''}</h3>
                    ${app.optedInTesters && app.optedInTesters.length > 0 ? `
                        <div style="display: grid; gap: 0.75rem;">
                            ${app.optedInTesters.map(tester => `
                                <div style="padding: 1rem; background: rgba(139, 92, 246, 0.05); border-radius: 8px; display: flex; justify-content: space-between; align-items: center;">
                                    <div>
                                        <p style="margin: 0; font-weight: bold;">${tester.testerId.displayName || 'Unknown'}</p>
                                        <p class="text-dim" style="margin: 0.25rem 0 0 0; font-size: 0.85rem;">${tester.testerId.email}</p>
                                    </div>
                                    <div style="text-align: right;">
                                        <p style="margin: 0; color: var(--primary); font-weight: bold;">Day ${tester.daysCompleted}/15</p>
                                        <p class="text-dim" style="margin: 0.25rem 0 0 0; font-size: 0.85rem;">Opted in: ${new Date(tester.optedInAt).toLocaleDateString()}</p>
                                    </div>
                                </div>
                            `).join('')}
                        </div>
                    ` : '<p class="text-dim">No testers have opted in yet. Share your app to attract testers!</p>'}
                </div>
            </div>
        `;

    } catch (err) {
        detailContent.innerHTML = `<p style="color: red;">Failed to load app details: ${err.message}</p>`;
    }
}



async function showTesterAppDetail(appId) {
    try {
        // Find app in local list first for speed (optimization) or fetch
        // For now, let's just fetch my-tests again or pass data. Fetching my-tests is easiest to get fresh data.
        const res = await fetch(`${API_URL}/apps/my-tests`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const myTests = await res.json();
        const app = myTests.find(a => a._id === appId);

        if (!app) throw new Error("App details not found");

        const myTester = app.optedInTesters.find(t => t.testerId._id === currentUser.details.id);
        const daysCompleted = myTester?.daysCompleted || 0;
        const paymentAmount = app.paymentAmount || 399;
        const maxTesters = app.maxTesters || 20;
        const paymentPerTester = Math.floor(paymentAmount / maxTesters); // Simple logic for display

        // Populate UI
        document.getElementById('tester-detail-name').textContent = app.appName;
        document.getElementById('tester-detail-version').textContent = `v${app.appVersion || '1.0'}`;
        document.getElementById('tester-detail-status').textContent = app.status === 'testing' ? 'Active' : 'Waiting for Dev';
        document.getElementById('tester-detail-description').textContent = app.appDescription || 'No description provided.';
        document.getElementById('tester-detail-reward').textContent = `₹${paymentPerTester}`;
        document.getElementById('tester-detail-progress').textContent = `Day ${daysCompleted}/15`;

        const linkEl = document.getElementById('tester-detail-link');
        linkEl.href = app.closedTestingLink;
        linkEl.textContent = app.closedTestingLink;

        const noteEl = document.getElementById('tester-link-note');
        if (app.status === 'testing') {
            linkEl.style.pointerEvents = 'auto';
            linkEl.style.opacity = '1';
            noteEl.style.display = 'none';
        } else {
            linkEl.style.pointerEvents = 'none';
            linkEl.style.opacity = '0.5';
            noteEl.style.display = 'block';
            noteEl.textContent = "Link will work when developer starts the test.";
        }

        // Set Opt-Out Button
        const optOutBtn = document.getElementById('opt-out-btn');
        optOutBtn.onclick = () => optOutApp(app._id, app.appName);

        // Show View
        document.getElementById('my-tests-view').classList.add('hidden');
        document.getElementById('browse-apps-view').classList.add('hidden');
        document.getElementById('tester-app-detail-view').classList.remove('hidden');

        // Update Nav
        // navigateTo('tester-app-detail'); // Optional, or just handle view manually

    } catch (err) {
        console.error(err);
        showAlert("Failed to load app details");
    }
}

async function optOutApp(appId, appName) {
    if (!await showConfirm(`Are you sure you want to opt out of "${appName}"?`)) return;

    try {
        const res = await fetch(`${API_URL}/apps/${appId}/opt-out`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${currentUser.token}`
            }
        });

        if (!res.ok) {
            const data = await res.json();
            throw new Error(data.message || 'Opt-out failed');
        }

        await showAlert(`You have opted out of "${appName}".`, 'Opt-Out Successful');

        // Return to list
        document.getElementById('tester-app-detail-view').classList.add('hidden');
        document.getElementById('my-tests-view').classList.remove('hidden');
        renderTesterMissions(); // Refresh list

    } catch (err) {
        showAlert(err.message, 'Opt-Out Failed');
    }
}

async function startTest(appId) {
    if (!await showConfirm('Start the testing period now? Testers will be able to install the app.')) return;

    try {
        const res = await fetch(`${API_URL}/apps/${appId}/start`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            }
        });

        const data = await res.json();

        if (!res.ok) throw new Error(data.message);

        await showAlert('Testing period started! Testers can now install your app.');

        // Fetch updated app data and show testing dashboard
        const appsRes = await fetch(`${API_URL}/apps/my-apps`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const apps = await appsRes.json();
        const updatedApp = apps.find(a => a._id === appId);

        if (updatedApp) {
            showDeveloperTestingDashboard(updatedApp);
        }

    } catch (err) {
        await showAlert('Failed to start test: ' + err.message);
    }
}
// Custom Dialog System
let dialogResolver = null;

async function showAlert(message, title = 'Notification') {
    return new Promise((resolve) => {
        const dialog = document.getElementById('custom-dialog');
        const titleEl = document.getElementById('dialog-title');
        const messageEl = document.getElementById('dialog-message');
        const cancelBtn = document.getElementById('dialog-cancel-btn');
        const confirmBtn = document.getElementById('dialog-confirm-btn');

        titleEl.textContent = title;
        messageEl.textContent = message;
        cancelBtn.style.display = 'none';
        confirmBtn.textContent = 'OK';

        dialogResolver = resolve;
        dialog.classList.remove('hidden');
        dialog.classList.add('active');
    });
}

async function showConfirm(message, title = 'Confirm') {
    return new Promise((resolve) => {
        const dialog = document.getElementById('custom-dialog');
        const titleEl = document.getElementById('dialog-title');
        const messageEl = document.getElementById('dialog-message');
        const cancelBtn = document.getElementById('dialog-cancel-btn');
        const confirmBtn = document.getElementById('dialog-confirm-btn');

        titleEl.textContent = title;
        messageEl.textContent = message;
        cancelBtn.style.display = 'inline-block';
        confirmBtn.textContent = 'Confirm';

        dialogResolver = resolve;
        dialog.classList.remove('hidden');
        dialog.classList.add('active');
    });
}

function closeCustomDialog(result) {
    const dialog = document.getElementById('custom-dialog');
    dialog.classList.remove('active');
    dialog.classList.add('hidden');

    if (dialogResolver) {
        dialogResolver(result);
        dialogResolver = null;
    }
}
// Updated updateUIForLogin function
function updateUIForLogin() {
    // Hide landing screen
    const landingScreen = document.getElementById('landing-screen');
    if (landingScreen) landingScreen.classList.add('hidden');

    // Ensure detail view is hidden on login/refresh
    const detailView = document.getElementById('app-detail-view');
    if (detailView) detailView.classList.add('hidden');

    // Remove before-login class to show sidebar
    const appContainer = document.getElementById('app-container');
    if (appContainer) appContainer.classList.remove('before-login');

    // Update top bar user info
    const userMenuContainer = document.getElementById('user-menu-container');
    const logoutBtn = document.getElementById('logout-btn');
    const userAvatar = document.getElementById('user-avatar');
    const userDisplayName = document.getElementById('user-display-name');

    if (currentUser.details) {
        userMenuContainer.style.display = 'flex';
        logoutBtn.style.display = 'block';

        // Set avatar initial
        const initial = currentUser.details.displayName ? currentUser.details.displayName.charAt(0).toUpperCase() : 'U';
        userAvatar.textContent = initial;
        userDisplayName.textContent = currentUser.details.displayName || currentUser.details.email;
    }

    // Show appropriate navigation and dashboard
    if (currentUser.role === 'developer') {
        document.getElementById('dev-nav').style.display = 'block';
        document.getElementById('tester-nav').style.display = 'none';
        document.getElementById('dev-bottom-nav').style.display = 'flex';
        document.getElementById('tester-bottom-nav').style.display = 'none';
        document.getElementById('dev-dashboard').classList.remove('hidden');
        document.getElementById('tester-dashboard').classList.add('hidden');
        document.getElementById('page-title').textContent = 'My Apps';
        renderDeveloperApps();
    } else if (currentUser.role === 'tester') {
        document.getElementById('dev-nav').style.display = 'none';
        document.getElementById('tester-nav').style.display = 'block';
        document.getElementById('dev-bottom-nav').style.display = 'none';
        document.getElementById('tester-bottom-nav').style.display = 'flex';
        document.getElementById('dev-dashboard').classList.add('hidden');
        document.getElementById('tester-dashboard').classList.remove('hidden');
        document.getElementById('my-tests-view').classList.remove('hidden');
        document.getElementById('browse-apps-view').classList.add('hidden');
        document.getElementById('page-title').textContent = 'My Tests';
        renderTesterMissions();
    }
}

// NEW Window exports for theme and navigation
// (Re-exported at bottom to ensure no conflicts if defined earlier, but I already did it above)
// I will keep these as per original file structure but remove duplicates if any.
// Actually, I already exported everything cleanly above. I can skip this or keep it minimal.
// I will just keep the initTheme call.

// Initialize theme on page load
document.addEventListener('DOMContentLoaded', function () {
    initTheme();
});

// ==========================================
// Global State Refresh Helpers
// ==========================================
window.refreshDeveloperApps = renderDeveloperApps;
window.refreshTesterApps = renderTesterMissions;
window.navigateTo = navigateTo;

// ==========================================
// Bug Reporting Functions
// ==========================================

function renderBugReports(bugs, appId) {
    // Find the bug reports container (3rd glass card in dashboard)
    // To be more robust, we should probably add an ID to the HTML in index.html, 
    // but for now we'll select by order or generic selector contextualized to the dashboard.
    // Actually, let's use a specific ID injection via JS to be safe if I can't edit HTML easily now.
    // I will look for the h3 containing "Bug Reports"
    const headings = Array.from(document.querySelectorAll('#developer-testing-dashboard h3'));
    const bugHeading = headings.find(h => h.textContent.includes('Bug Reports'));

    if (!bugHeading) {
        console.error("Bug Reports section not found");
        return;
    }

    const container = bugHeading.parentElement;

    if (!bugs || bugs.length === 0) {
        container.innerHTML = `
            <h3 style="margin: 0 0 1.5rem 0;">Bug Reports (0)</h3>
            <p class="text-dim" style="text-align: center; padding: 2rem;">No bug reports yet.</p>
        `;
        return;
    }

    container.innerHTML = `
        <h3 style="margin: 0 0 1.5rem 0;">Bug Reports (${bugs.length})</h3>
        <div style="display: grid; gap: 1rem;">
            ${bugs.map(bug => {
        const isResolved = bug.status === 'resolved';
        const statusColor = isResolved ? 'var(--success)' : 'var(--warning)';
        const date = new Date(bug.reportedAt).toLocaleDateString();

        return `
                <div class="glass-card" style="padding: 1.5rem; background: rgba(255, 255, 255, 0.03); border: 1px solid var(--border);">
                    <div style="display: flex; justify-content: space-between; align-items: start; gap: 1rem;">
                        <div style="flex: 1;">
                            <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.5rem;">
                                <span style="font-weight: 600; font-size: 1.1rem;">${bug.title}</span>
                                <span style="font-size: 0.75rem; padding: 0.25rem 0.6rem; border-radius: 12px; background: ${statusColor}; color: white; font-weight: bold;">
                                    ${bug.status.toUpperCase()}
                                </span>
                            </div>
                            <p class="text-dim" style="font-size: 0.9rem; margin: 0 0 0.5rem 0;">
                                Reported by <strong>${bug.testerId?.displayName || 'Unknown'}</strong> on ${date}
                            </p>
                            <p style="margin: 0; line-height: 1.5;">${bug.description}</p>
                            
                            <!-- Developer Reply Display -->
                            ${bug.developerReply ? `
                                <div style="margin-top: 1rem; padding: 1rem; background: rgba(59, 130, 246, 0.1); border-left: 3px solid var(--primary); border-radius: 4px;">
                                    <p style="margin: 0; font-weight: bold; font-size: 0.85rem; color: var(--primary);">Developer Reply:</p>
                                    <p style="margin: 0.5rem 0 0 0;">${bug.developerReply}</p>
                                </div>
                            ` : ''}

                            <!-- Reply Form (Hidden by default) -->
                            <div id="reply-form-${bug._id}" style="display: none; margin-top: 1rem;">
                                <textarea id="reply-text-${bug._id}" rows="3" placeholder="Write a reply to the tester..." 
                                    style="width: 100%; padding: 0.75rem; background: rgba(0,0,0,0.2); border: 1px solid var(--border); border-radius: 6px; color: var(--text); margin-bottom: 0.5rem;"></textarea>
                                <div style="display: flex; gap: 0.5rem;">
                                    <button class="btn btn-primary" onclick="submitBugReply('${bug._id}', '${appId}')" style="padding: 0.5rem 1rem; font-size: 0.9rem;">Send Reply</button>
                                    <button class="btn" onclick="toggleBugReplyForm('${bug._id}')" style="padding: 0.5rem 1rem; font-size: 0.9rem;">Cancel</button>
                                </div>
                            </div>
                        </div>
                        
                        <div style="display: flex; flex-direction: column; gap: 0.5rem; min-width: 100px;">
                            ${!isResolved ? `
                                <button class="btn btn-outline" onclick="resolveBug('${bug._id}', '${appId}')" style="padding: 0.5rem; font-size: 0.85rem; border-color: var(--success); color: var(--success);">
                                    Resolve
                                </button>
                            ` : ''}
                            ${!bug.developerReply ? `
                                <button class="btn btn-outline" onclick="toggleBugReplyForm('${bug._id}')" style="padding: 0.5rem; font-size: 0.85rem;">
                                    Reply
                                </button>
                            ` : ''}
                        </div>
                    </div>
                </div>
                `;
    }).join('')}
        </div>
    `;
}

window.toggleBugReplyForm = function (bugId) {
    const form = document.getElementById(`reply-form-${bugId}`);
    if (form) {
        form.style.display = form.style.display === 'none' ? 'block' : 'none';
    }
};

window.submitBugReply = async function (bugId, appId) {
    const text = document.getElementById(`reply-text-${bugId}`).value;
    if (!text) return;

    try {
        const res = await fetch(`${API_URL}/bugs/${bugId}/reply`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentUser.token}`
            },
            body: JSON.stringify({ reply: text })
        });

        if (!res.ok) {
            const data = await res.json();
            throw new Error(data.message || 'Failed to send reply');
        }

        await showAlert('Reply sent successfully!', '✅ Replied');

        // Refresh bugs
        const bugsRes = await fetch(`${API_URL}/bugs/app/${appId}`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const bugs = await bugsRes.json();
        renderBugReports(bugs, appId);

    } catch (err) {
        await showAlert(err.message, '❌ Failed');
    }
};

window.resolveBug = async function (bugId, appId) {
    if (!await showConfirm('Mark this bug as resolved?')) return;

    try {
        const res = await fetch(`${API_URL}/bugs/${bugId}/resolve`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });

        if (!res.ok) {
            const data = await res.json();
            throw new Error(data.message || 'Failed to resolve bug');
        }

        await showAlert('Bug marked as resolved.', '✅ Resolved');

        // Refresh bugs
        const bugsRes = await fetch(`${API_URL}/bugs/app/${appId}`, {
            headers: { 'Authorization': `Bearer ${currentUser.token}` }
        });
        const bugs = await bugsRes.json();
        renderBugReports(bugs, appId);

    } catch (err) {
        await showAlert(err.message, '❌ Failed');
    }
};
