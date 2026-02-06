// API Base URL - Change this if deploying to different server
const API_BASE = '';

// Utility Functions
function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.classList.add('show');
        setTimeout(() => errorDiv.classList.remove('show'), 5000);
    }
}

function showSuccess(message) {
    const successDiv = document.getElementById('successMessage');
    if (successDiv) {
        successDiv.textContent = message;
        successDiv.classList.add('show');
        setTimeout(() => successDiv.classList.remove('show'), 3000);
    }
}

// Authentication Functions
async function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch(`${API_BASE}/api/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
        });
        
        const data = await response.json();
        
        if (data.success) {
            window.location.href = 'dashboard.html';
        } else {
            showError(data.message || 'Login failed');
        }
    } catch (error) {
        showError('Network error. Please try again.');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    if (password.length < 4) {
        showError('Password must be at least 4 characters');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/api/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `username=${encodeURIComponent(username)}&email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
        });
        
        const data = await response.json();
        
        if (data.success) {
            showSuccess('Registration successful! Redirecting to login...');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1500);
        } else {
            showError(data.message || 'Registration failed');
        }
    } catch (error) {
        showError('Network error. Please try again.');
    }
}

async function logout() {
    try {
        await fetch(`${API_BASE}/api/logout`, {
            method: 'POST'
        });
        window.location.href = 'index.html';
    } catch (error) {
        window.location.href = 'index.html';
    }
}

// Dashboard Functions
async function loadDashboard() {
    await loadTasks();
    updateStats();
}

async function loadTasks(category = 'all') {
    try {
        const response = await fetch(`${API_BASE}/api/tasks`);
        const data = await response.json();
        
        if (data.success) {
            displayTasks(data.tasks, category);
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
    }
}

function displayTasks(tasks, filter = 'all') {
    const container = document.getElementById('tasksContainer');
    
    if (!tasks || tasks.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No tasks yet. Add your first task above!</p>
            </div>
        `;
        updateStats(0, 0, 0);
        return;
    }
    
    let filteredTasks = tasks;
    if (filter !== 'all') {
        filteredTasks = tasks.filter(t => t.category === filter);
    }
    
    // Sort by date and time
    filteredTasks.sort((a, b) => {
        const dateA = new Date(a.taskDate + ' ' + a.taskTime);
        const dateB = new Date(b.taskDate + ' ' + b.taskTime);
        return dateA - dateB;
    });
    
    const html = filteredTasks.map(task => {
        const priorityClass = task.priority === 'High' ? 'high-priority' : 
                             task.priority === 'Medium' ? 'medium-priority' : '';
        const completedClass = task.completed ? 'completed' : '';
        const categoryIcons = {
            'Study': '📚',
            'Work': '💼',
            'Travel': '✈️',
            'Market': '🛒',
            'Daily': '📅'
        };
        
        return `
            <div class="task-card ${priorityClass} ${completedClass}" data-id="${task.id}">
                <div class="task-header">
                    <h3 class="task-title">${escapeHtml(task.title)}</h3>
                    <span class="task-category">${categoryIcons[task.category] || '📋'} ${task.category}</span>
                </div>
                <p>${escapeHtml(task.description || 'No description')}</p>
                <div class="task-meta">
                    <span>📅 ${formatDate(task.taskDate)}</span>
                    <span>⏰ ${task.taskTime}</span>
                    <span>${getPriorityIcon(task.priority)} ${task.priority}</span>
                </div>
                <div class="task-actions">
                    ${!task.completed ? 
                        `<button onclick="completeTask(${task.id})" class="btn-complete">✓ Complete</button>` : 
                        '<span style="color: var(--success);">✓ Completed</span>'
                    }
                    <button onclick="deleteTask(${task.id})" class="btn-delete">🗑 Delete</button>
                </div>
            </div>
        `;
    }).join('');
    
    container.innerHTML = html;
    
    // Update stats
    const total = tasks.length;
    const completed = tasks.filter(t => t.completed).length;
    const pending = total - completed;
    updateStats(total, pending, completed);
}

function getPriorityIcon(priority) {
    const icons = {
        'High': '🔴',
        'Medium': '🟡',
        'Low': '🟢'
    };
    return icons[priority] || '⚪';
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    if (date.toDateString() === today.toDateString()) {
        return 'Today';
    } else if (date.toDateString() === tomorrow.toDateString()) {
        return 'Tomorrow';
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function updateStats(total = 0, pending = 0, completed = 0) {
    const totalEl = document.getElementById('totalTasks');
    const pendingEl = document.getElementById('pendingTasks');
    const completedEl = document.getElementById('completedTasks');
    
    if (totalEl) totalEl.textContent = total;
    if (pendingEl) pendingEl.textContent = pending;
    if (completedEl) completedEl.textContent = completed;
}

function filterTasks(category) {
    // Update active button
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.textContent.includes(category) || (category === 'all' && btn.textContent.includes('All'))) {
            btn.classList.add('active');
        }
    });
    
    loadTasks(category);
}

async function handleAddTask(e) {
    e.preventDefault();
    
    const taskData = {
        title: document.getElementById('taskTitle').value,
        description: document.getElementById('taskDescription').value,
        category: document.getElementById('taskCategory').value,
        taskDate: document.getElementById('taskDate').value,
        taskTime: document.getElementById('taskTime').value,
        priority: document.getElementById('taskPriority').value
    };
    
    try {
        const params = new URLSearchParams(taskData);
        const response = await fetch(`${API_BASE}/api/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        });
        
        const data = await response.json();
        
        if (data.success) {
            // Reset form
            document.getElementById('taskForm').reset();
            document.getElementById('taskDate').valueAsDate = new Date();
            
            // Reload tasks
            loadTasks();
            
            // Show success
            const btn = e.target.querySelector('button[type="submit"]');
            const originalText = btn.textContent;
            btn.textContent = '✓ Added!';
            btn.style.background = 'var(--success)';
            setTimeout(() => {
                btn.textContent = originalText;
                btn.style.background = '';
            }, 1500);
        } else {
            alert('Failed to add task: ' + data.message);
        }
    } catch (error) {
        alert('Error adding task');
    }
}

async function completeTask(taskId) {
    try {
        const response = await fetch(`${API_BASE}/api/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id: taskId, completed: true })
        });
        
        const data = await response.json();
        
        if (data.success) {
            loadTasks();
        }
    } catch (error) {
        console.error('Error completing task:', error);
    }
}

async function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/api/tasks/${taskId}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.success) {
            loadTasks();
        }
    } catch (error) {
        console.error('Error deleting task:', error);
    }
}

// Reminder System
async function checkReminders() {
    try {
        const response = await fetch(`${API_BASE}/api/reminders`);
        const data = await response.json();
        
        if (data.success && data.reminders && data.reminders.length > 0) {
            showReminders(data.reminders);
        }
    } catch (error) {
        console.error('Error checking reminders:', error);
    }
}

function showReminders(reminders) {
    const modal = document.getElementById('reminderModal');
    const list = document.getElementById('reminderList');
    
    const categoryIcons = {
        'Study': '📚',
        'Work': '💼',
        'Travel': '✈️',
        'Market': '🛒',
        'Daily': '📅'
    };
    
    list.innerHTML = reminders.map(r => `
        <div class="reminder-item">
            <div>
                <strong>${categoryIcons[r.category] || '📋'} ${escapeHtml(r.title)}</strong>
                <div style="font-size: 0.875rem; color: #6b7280;">
                    ⏰ Due at ${r.taskTime} • ${r.priority} Priority
                </div>
            </div>
            <button onclick="completeTask(${r.id}); closeModal();" class="btn-complete">Done</button>
        </div>
    `).join('');
    
    modal.classList.add('show');
    
    // Browser notification if supported
    if ('Notification' in window && Notification.permission === 'granted') {
        reminders.forEach(r => {
            new Notification('⏰ Task Reminder', {
                body: `${r.title} is due now!`,
                icon: '🔔'
            });
        });
    }
}

function closeModal() {
    document.getElementById('reminderModal').classList.remove('show');
}

// Close modal when clicking X
document.addEventListener('DOMContentLoaded', function() {
    const closeBtn = document.querySelector('.close');
    if (closeBtn) {
        closeBtn.addEventListener('click', closeModal);
    }
    
    // Request notification permission
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission();
    }
    
    // Check if user is logged in on dashboard
    if (window.location.pathname.includes('dashboard.html')) {
        fetch(`${API_BASE}/api/tasks`)
            .then(r => {
                if (r.status === 401) {
                    window.location.href = 'login.html';
                }
            })
            .catch(() => {
                // If server not running, still show demo
                document.getElementById('welcomeUser').textContent = 'Welcome, Demo User';
            });
    }
});

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('reminderModal');
    if (event.target === modal) {
        closeModal();
    }
}
