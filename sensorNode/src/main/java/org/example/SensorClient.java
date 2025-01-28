package org.example;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class SensorClient {
    private final WebSocketStompClient stompClient;
    private StompSession session;
    private final String nodeId;

    public SensorClient(String nodeId) {
        this.nodeId = nodeId;
        this.stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
        this.stompClient.setMessageConverter(converter);
    }

    public void connect() throws Exception {
        String serverUrl = "ws://localhost:8080/sensors";
        try {
            StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("Connected! Session Id: " + session.getSessionId());

                    session.subscribe("/topic/sensor-updates", new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return SensorData.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            SensorData data = (SensorData) payload;
                            System.out.println("\n\nACK Received: " + data + "\n\n");
                        }
                    });

                }

                @Override
                public void handleException(StompSession session, StompCommand command,
                        StompHeaders headers, byte[] payload, Throwable exception) {
                    System.err.println("Error: " + exception.getMessage());
                    exception.printStackTrace();
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.err.println("Transport Error: " + exception.getMessage());
                    exception.printStackTrace();
                }
            };

            session = stompClient.connectAsync(serverUrl, sessionHandler).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            throw e;
        }
    }

    public void sendData() {
        if (session != null && session.isConnected()) {
            SensorData data = new SensorData();
            data.setNodeId(nodeId);
            data.setTemperature(20 + Math.random() * 10);
            data.setHumidity(40 + Math.random() * 20);
            data.setPressure(1000 + Math.random() * 100);
            data.setLight(50 + Math.random() * 50);

            session.send("/app/data", data);
            System.out.println("\n\nSent data: " + data + "\n\n");
        } else {
            System.err.println("Not connected!");
        }
    }

    public void requestData() {
        if (session != null && session.isConnected()) {
            // Send request to the server for data
            session.send("/app/request",
                    nodeId);
            System.out.println("Request sent to the server for node: " + nodeId);
            this.sendData();
        } else {
            System.err.println("Not connected!");
        }
    }

    public static void main(String[] args) {
        Map<SensorClient, Thread> clientThreads = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter the number of clients: ");
            int numberOfClients = Integer.parseInt(scanner.nextLine());

            for (int i = 0; i < numberOfClients; i++) {
                System.out.print("Enter node ID for client " + (i + 1) + ": ");
                String nodeId = scanner.nextLine();

                SensorClient client = new SensorClient(nodeId);
                Thread clientThread = new Thread(() -> {
                    try {
                        client.connect();
                    } catch (Exception e) {
                        System.err.println("Error connecting client: " + nodeId);
                        e.printStackTrace();
                    }
                });

                clientThreads.put(client, clientThread);
                clientThread.start();
            }

            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Send Data");
                System.out.println("2. Request Data");
                System.out.println("3. Exit");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter the client number to send data: ");
                        int sendClientIndex = Integer.parseInt(scanner.nextLine()) - 1;
                        SensorClient sendClient = getClientByIndex(clientThreads, sendClientIndex);
                        if (sendClient != null) {
                            sendClient.sendData();
                        }
                        break;

                    case "2":
                        System.out.print("Enter the client number to request data: ");
                        int requestClientIndex = Integer.parseInt(scanner.nextLine()) - 1;
                        SensorClient requestClient = getClientByIndex(clientThreads, requestClientIndex);
                        if (requestClient != null) {
                            requestClient.requestData();
                        }
                        break;

                    case "3":
                        System.out.println("Exiting...");
                        scanner.close();
                        clientThreads.values().forEach(Thread::interrupt);
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SensorClient getClientByIndex(Map<SensorClient, Thread> clientThreads, int index) {
        if (index < 0 || index >= clientThreads.size()) {
            System.err.println("Invalid client index.");
            return null;
        }
        return clientThreads.keySet().toArray(new SensorClient[0])[index];
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    @ToString
    static class SensorData {
        private String nodeId;
        private double temperature;
        private double humidity;
        private double pressure;
        private double light;
        private LocalDateTime timestamp;
    }
}
