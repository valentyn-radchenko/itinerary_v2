// Check authentication on page load
document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('authToken');
    const username = localStorage.getItem('username');
    
    if (!token) {
        window.location.href = '/web/login';
        return;
    }
    
    if (username) {
        document.getElementById('usernameDisplay').textContent = username;
    }
});

// Logout
function handleLogout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    window.location.href = '/web/login';
}

// Modal functions
function showCreateTicket() {
    document.getElementById('createTicketModal').style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
}

// Create Ticket (Operation 1: Itinerary → Payments via JMS)
async function handleCreateTicket(event) {
    event.preventDefault();
    
    const routeId = document.getElementById('routeId').value;
    const token = localStorage.getItem('authToken');
    const errorDiv = document.getElementById('ticketError');
    const successDiv = document.getElementById('ticketSuccess');
    
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';
    
    try {
        const response = await fetch(`http://localhost:8762/tickets?routeId=${routeId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            successDiv.textContent = '✅ Квиток створено! Запит на оплату відправлено в Payments сервіс через JMS';
            successDiv.style.display = 'block';
            
            setTimeout(() => {
                closeModal('createTicketModal');
                document.getElementById('createTicketForm').reset();
                successDiv.style.display = 'none';
            }, 3000);
        } else {
            errorDiv.textContent = data.error || 'Помилка створення квитка';
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        errorDiv.textContent = 'Помилка з\'єднання з сервером';
        errorDiv.style.display = 'block';
    }
}

// Show My Tickets (Operation 2)
async function showMyTickets() {
    const modal = document.getElementById('ticketsModal');
    const listDiv = document.getElementById('ticketsList');
    const token = localStorage.getItem('authToken');
    
    modal.style.display = 'block';
    listDiv.innerHTML = '<div class="loading">Завантаження...</div>';
    
    try {
        const response = await fetch('http://localhost:8762/tickets', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const tickets = await response.json();
        
        if (tickets.length === 0) {
            listDiv.innerHTML = '<div class="no-data">У вас ще немає квитків</div>';
            return;
        }
        
        listDiv.innerHTML = tickets.map(ticket => `
            <div class="data-item">
                <h4>Квиток #${ticket.id}</h4>
                <p><strong>Маршрут ID:</strong> ${ticket.routeId}</p>
                <p><strong>Дата покупки:</strong> ${new Date(ticket.purchaseTime).toLocaleString('uk-UA')}</p>
                <span class="status ${ticket.status.toLowerCase()}">${ticket.status}</span>
                <div style="margin-top: 10px;">
                    <a href="http://localhost:8762/tickets/${ticket.id}/pdf" 
                       target="_blank" class="btn btn-secondary" style="padding: 6px 12px; font-size: 12px;">
                       📄 Завантажити PDF
                    </a>
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        listDiv.innerHTML = '<div class="error-message">Помилка завантаження квитків</div>';
    }
}

// Show My Payments (Operation 3: Itinerary → Payments via gRPC)
async function showMyPayments() {
    const modal = document.getElementById('paymentsModal');
    const listDiv = document.getElementById('paymentsList');
    const token = localStorage.getItem('authToken');
    const userId = localStorage.getItem('userId');
    
    modal.style.display = 'block';
    listDiv.innerHTML = '<div class="loading">Завантаження через gRPC...</div>';
    
    try {
        // This endpoint uses gRPC to communicate with Payments service
        const response = await fetch(`http://localhost:8762/users/${userId}/list-payments`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            throw new Error('Failed to fetch payments');
        }
        
        // Also fetch from REST API to display
        const paymentsResponse = await fetch('http://localhost:8763/payments', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        const payments = await paymentsResponse.json();
        
        if (payments.length === 0) {
            listDiv.innerHTML = '<div class="no-data">У вас ще немає платежів</div>';
            return;
        }
        
        listDiv.innerHTML = `
            <div class="info-box" style="margin-bottom: 20px;">
                <p>ℹ️ Дані отримано через gRPC взаємодію між Itinerary та Payments сервісами</p>
            </div>
        ` + payments.map(payment => `
            <div class="data-item">
                <h4>Платіж #${payment.id}</h4>
                <p><strong>Сума:</strong> ${payment.amount} грн</p>
                <p><strong>Опис:</strong> ${payment.description}</p>
                <p><strong>Метод:</strong> ${payment.paymentMethod}</p>
                <p><strong>Дата:</strong> ${new Date(payment.timestamp).toLocaleString('uk-UA')}</p>
                <span class="status ${payment.status.toLowerCase()}">${payment.status}</span>
            </div>
        `).join('');
        
    } catch (error) {
        listDiv.innerHTML = '<div class="error-message">Помилка завантаження платежів через gRPC</div>';
    }
}

