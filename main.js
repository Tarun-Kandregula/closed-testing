// State Management
let currentUser = {
    role: null,
    isLoggedIn: false
};

// Mock Data
const mockTesters = [
    { id: 1, name: "Rahul S.", device: "Samsung S23", rating: 4.8 },
    { id: 2, name: "Anita K.", device: "Pixel 7 Pro", rating: 4.9 },
    { id: 3, name: "Kevin D.", device: "OnePlus 11", rating: 4.7 }
];

// UI Functions
function selectRole(role) {
    currentUser.role = role;
    currentUser.isLoggedIn = true;
    
    // Update UI
    document.getElementById('role-selection').classList.add('hidden');
    document.getElementById('user-info').innerText = `Logged in as ${role.toUpperCase()}`;
    
    if (role === 'developer') {
        document.getElementById('dev-dashboard').classList.remove('hidden');
        renderDeveloperApps();
    } else {
        document.getElementById('tester-dashboard').classList.remove('hidden');
        renderTesterMissions();
    }
}

function renderDeveloperApps() {
    const container = document.getElementById('dev-apps');
    // For now, it stays empty or shows mock apps
}

function showAppForm() {
    alert("This would open a premium modal to enter: \n1. App Name \n2. Package Name \n3. Tester Selection list");
}

function renderTesterMissions() {
    const container = document.getElementById('available-missions');
    container.innerHTML = `
        <div class="glass-card" style="padding: 1.5rem; display: flex; justify-content: space-between; align-items: center; margin-top: 1rem;">
            <div>
                <h4 style="margin: 0;">Super Runner 3D</h4>
                <p class="text-dim" style="font-size: 0.8rem;">Reward: ₹25.00 | Duration: 14 Days</p>
            </div>
            <button class="btn btn-primary" style="padding: 8px 16px; font-size: 0.8rem;">Start Mission</button>
        </div>
    `;
}

// Initial Lucide call for dynamic content
window.addEventListener('load', () => {
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});
