class SensorClient {
    constructor() {
        this.stompClient = null;
        this.clientId = null;
        this.periodicInterval = null;
    }

    connect(clientId, onConnect) {
        this.clientId = clientId;
        const socket = new SockJS('/sensors');
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect({}, frame => {
            console.log('Connected:', frame);
            this.subscribeToTopics();
            onConnect();
        });
    }

    disconnect() {
        this.stompClient?.disconnect();
        this.stopPeriodicData();
    }

    subscribeToTopics() {
        this.stompClient.subscribe('/topic/sensor-updates', message => {
            UIManager.showSensorData(JSON.parse(message.body));
        });

        this.stompClient.subscribe(`/user/${this.clientId}/request`, () => {
            this.sendData();
        });
    }

    sendData() {
        if (!this.stompClient?.connected) return;

        const sensorData = {
            nodeId: this.clientId,
            temperature: 20 + Math.random() * 10,
            humidity: 40 + Math.random() * 20,
            pressure: 1000 + Math.random() * 100,
            light: 50 + Math.random() * 50,
            timestamp: new Date().toISOString()
        };

        this.stompClient.send("/app/data", {}, JSON.stringify(sensorData));
    }

    requestData(nodeId) {
        if (!this.stompClient?.connected || !nodeId) return;
        this.stompClient.send("/app/request", {}, nodeId);
    }

    startPeriodicData() {
        this.periodicInterval = setInterval(() => this.sendData(), 5000);
    }

    stopPeriodicData() {
        clearInterval(this.periodicInterval);
    }
}

const sensorClient = new SensorClient();