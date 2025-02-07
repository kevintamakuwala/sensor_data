class UIManager {
    static init() {
        this.bindEvents();
    }

    static bindEvents() {
        document.getElementById('connectButton').onclick = () => this.handleConnect();
        document.getElementById('disconnectButton').onclick = () => this.handleDisconnect();
        document.getElementById('requestDataButton').onclick = () => this.handleDataRequest();
        document.getElementById('startPeriodicButton').onclick = () => this.handleStartPeriodic();
        document.getElementById('stopPeriodicButton').onclick = () => this.handleStopPeriodic();
    }

    static toggleConnectionUI(connected) {
        document.getElementById('connectButton').classList.toggle('hidden', connected);
        document.getElementById('disconnectButton').classList.toggle('hidden', !connected);
        document.getElementById('controlPanel').classList.toggle('hidden', !connected);
        document.getElementById('clientId').disabled = connected;
    }

    static handleConnect() {
        const clientId = document.getElementById('clientId').value;
        if (!clientId) {
            alert('Please enter a Client ID');
            return;
        }
        sensorClient.connect(clientId, () => this.toggleConnectionUI(true));
    }

    static handleDisconnect() {
        sensorClient.disconnect();
        this.toggleConnectionUI(false);
    }

    static handleDataRequest() {
        const nodeId = document.getElementById('nodeIdInput').value;
        sensorClient.requestData(nodeId);
    }

    static handleStartPeriodic() {
        sensorClient.startPeriodicData();
        document.getElementById('startPeriodicButton').classList.add('hidden');
        document.getElementById('stopPeriodicButton').classList.remove('hidden');
    }

    static handleStopPeriodic() {
        sensorClient.stopPeriodicData();
        document.getElementById('stopPeriodicButton').classList.add('hidden');
        document.getElementById('startPeriodicButton').classList.remove('hidden');
    }

    static showSensorData(data) {
        const dataElement = document.createElement('div');
        dataElement.className = 'bg-gray-100 p-4 rounded';
        dataElement.innerHTML = `
            <p class="font-bold">Node ID: ${data.nodeId}</p>
            <p>Temperature: ${data.temperature.toFixed(2)}°C</p>
            <p>Humidity: ${data.humidity.toFixed(2)}%</p>
            <p>Pressure: ${data.pressure.toFixed(2)} hPa</p>
            <p>Light: ${data.light.toFixed(2)} lux</p>
            <p>Timestamp: ${new Date(data.timestamp).toLocaleString()}</p>
        `;

        const container = document.getElementById('sensorData');
        container.insertBefore(dataElement, container.firstChild);
    }
}

document.addEventListener('DOMContentLoaded', () => UIManager.init());