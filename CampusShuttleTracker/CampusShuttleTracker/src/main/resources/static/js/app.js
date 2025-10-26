// Campus Shuttle Tracker JavaScript

document.addEventListener('DOMContentLoaded', function() {
    console.log('Campus Shuttle Tracker loaded');
    
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Auto-refresh active trips every 30 seconds
    if (window.location.pathname === '/dashboard' || window.location.pathname === '/') {
        setInterval(function() {
            refreshActiveTrips();
        }, 30000);
    }
    
    // Add fade-in animation to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        setTimeout(() => {
            card.classList.add('fade-in');
        }, index * 100);
    });
    
    // Handle form submissions
    setupFormHandlers();
    
    // Setup real-time updates
    setupRealTimeUpdates();
});

// Refresh active trips data
function refreshActiveTrips() {
    const activeTripsContainer = document.querySelector('.active-trips-container');
    if (activeTripsContainer) {
        // Add loading indicator
        const loadingHtml = `
            <div class="text-center py-3">
                <div class="loading"></div>
                <span class="ms-2">Refreshing trips...</span>
            </div>
        `;
        
        // This would typically make an AJAX call to refresh data
        // For now, we'll just show the loading indicator briefly
        const originalContent = activeTripsContainer.innerHTML;
        activeTripsContainer.innerHTML = loadingHtml;
        
        setTimeout(() => {
            activeTripsContainer.innerHTML = originalContent;
            showNotification('Trips refreshed successfully', 'success');
        }, 1000);
    }
}

// Setup form handlers
function setupFormHandlers() {
    // Handle trip creation form
    const tripForm = document.getElementById('tripForm');
    if (tripForm) {
        tripForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleTripCreation(this);
        });
    }
    
    // Handle shuttle creation form
    const shuttleForm = document.getElementById('shuttleForm');
    if (shuttleForm) {
        shuttleForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleShuttleCreation(this);
        });
    }
    
    // Handle user creation form
    const userForm = document.getElementById('userForm');
    if (userForm) {
        userForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleUserCreation(this);
        });
    }
}

// Handle trip creation
function handleTripCreation(form) {
    const formData = new FormData(form);
    const tripData = {
        shuttleId: formData.get('shuttleId'),
        tripDate: formData.get('tripDate'),
        startTime: formData.get('startTime'),
        endTime: formData.get('endTime')
    };
    
    // Validate form data
    if (!tripData.shuttleId || !tripData.tripDate || !tripData.startTime) {
        showNotification('Please fill in all required fields', 'error');
        return;
    }
    
    // Show loading state
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<div class="loading"></div> Creating...';
    submitBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // Reset button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
        
        // Close modal
        const modal = bootstrap.Modal.getInstance(form.closest('.modal'));
        if (modal) {
            modal.hide();
        }
        
        // Reset form
        form.reset();
        
        // Show success notification
        showNotification('Trip created successfully!', 'success');
        
        // Refresh page or update UI
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 2000);
}

// Handle shuttle creation
function handleShuttleCreation(form) {
    const formData = new FormData(form);
    const shuttleData = {
        shuttleNumber: formData.get('shuttleNumber'),
        capacity: formData.get('capacity'),
        route: formData.get('route'),
        status: formData.get('status')
    };
    
    // Validate form data
    if (!shuttleData.shuttleNumber || !shuttleData.capacity) {
        showNotification('Please fill in all required fields', 'error');
        return;
    }
    
    // Show loading state
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<div class="loading"></div> Creating...';
    submitBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // Reset button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
        
        // Close modal
        const modal = bootstrap.Modal.getInstance(form.closest('.modal'));
        if (modal) {
            modal.hide();
        }
        
        // Reset form
        form.reset();
        
        // Show success notification
        showNotification('Shuttle created successfully!', 'success');
        
        // Refresh page or update UI
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 2000);
}

// Handle user creation
function handleUserCreation(form) {
    const formData = new FormData(form);
    const userData = {
        name: formData.get('name'),
        email: formData.get('email'),
        role: formData.get('role')
    };
    
    // Validate form data
    if (!userData.name || !userData.email || !userData.role) {
        showNotification('Please fill in all required fields', 'error');
        return;
    }
    
    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(userData.email)) {
        showNotification('Please enter a valid email address', 'error');
        return;
    }
    
    // Show loading state
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<div class="loading"></div> Creating...';
    submitBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // Reset button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
        
        // Close modal
        const modal = bootstrap.Modal.getInstance(form.closest('.modal'));
        if (modal) {
            modal.hide();
        }
        
        // Reset form
        form.reset();
        
        // Show success notification
        showNotification('User created successfully!', 'success');
        
        // Refresh page or update UI
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 2000);
}

// Show notification toast
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingToasts = document.querySelectorAll('.toast');
    existingToasts.forEach(toast => toast.remove());
    
    // Create toast container if it doesn't exist
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '1055';
        document.body.appendChild(toastContainer);
    }
    
    // Create toast
    const toastId = 'toast-' + Date.now();
    const iconClass = {
        'success': 'bi-check-circle-fill text-success',
        'error': 'bi-exclamation-triangle-fill text-danger',
        'warning': 'bi-exclamation-triangle-fill text-warning',
        'info': 'bi-info-circle-fill text-info'
    }[type] || 'bi-info-circle-fill text-info';
    
    const toastHtml = `
        <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header">
                <i class="bi ${iconClass} me-2"></i>
                <strong class="me-auto">Campus Shuttle Tracker</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    // Show toast
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 5000
    });
    toast.show();
    
    // Remove toast element after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Setup real-time updates (WebSocket simulation)
function setupRealTimeUpdates() {
    // Simulate real-time updates
    setInterval(() => {
        updateLastSeen();
    }, 60000); // Update every minute
}

// Update "last seen" timestamps
function updateLastSeen() {
    const timeElements = document.querySelectorAll('.last-seen');
    timeElements.forEach(element => {
        const timestamp = element.getAttribute('data-timestamp');
        if (timestamp) {
            element.textContent = formatRelativeTime(new Date(timestamp));
        }
    });
}

// Format relative time (e.g., "2 minutes ago")
function formatRelativeTime(date) {
    const now = new Date();
    const diffMs = now - date;
    const diffSeconds = Math.floor(diffMs / 1000);
    const diffMinutes = Math.floor(diffSeconds / 60);
    const diffHours = Math.floor(diffMinutes / 60);
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffSeconds < 60) {
        return 'Just now';
    } else if (diffMinutes < 60) {
        return `${diffMinutes} minute${diffMinutes > 1 ? 's' : ''} ago`;
    } else if (diffHours < 24) {
        return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    } else {
        return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    }
}

// Utility functions
function formatTime(timeString) {
    if (!timeString) return 'N/A';
    const [hours, minutes] = timeString.split(':');
    const hour12 = hours % 12 || 12;
    const ampm = hours < 12 ? 'AM' : 'PM';
    return `${hour12}:${minutes} ${ampm}`;
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Export functions for use in templates
window.CampusShuttleTracker = {
    refreshActiveTrips,
    showNotification,
    formatTime,
    formatDate
};