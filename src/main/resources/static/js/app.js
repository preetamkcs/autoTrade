document.addEventListener('DOMContentLoaded', () => {
    const app = document.getElementById('app');
    const loginButton = document.getElementById('login-button');
    const loadStrategiesButton = document.getElementById('load-strategies-button');
    const strategiesList = document.getElementById('strategies-list');

    app.innerHTML = ''; // Clear the "Loading..." message

    loginButton.addEventListener('click', async () => {
        try {
            const response = await fetch('/api/trading/login');
            if (response.ok) {
                const loginUrl = await response.text();
                window.location.href = loginUrl;
            } else {
                app.innerHTML = '<p>Error getting login URL.</p>';
            }
        } catch (error) {
            console.error('Error:', error);
            app.innerHTML = '<p>Error getting login URL.</p>';
        }
    });

    loadStrategiesButton.addEventListener('click', async () => {
        try {
            const response = await fetch('/api/trading/strategies');
            if (response.ok) {
                const strategies = await response.json();
                strategiesList.innerHTML = ''; // Clear previous list
                strategies.forEach(strategy => {
                    const li = document.createElement('li');
                    li.textContent = strategy;
                    strategiesList.appendChild(li);
                });
            } else {
                strategiesList.innerHTML = '<li>Error loading strategies.</li>';
            }
        } catch (error) {
            console.error('Error:', error);
            strategiesList.innerHTML = '<li>Error loading strategies.</li>';
        }
    });
});