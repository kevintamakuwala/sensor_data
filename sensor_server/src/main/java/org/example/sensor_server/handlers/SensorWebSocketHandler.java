package org.example.sensor_server.handlers;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SensorWebSocketHandler extends TextWebSocketHandler {

    // Store client sessions
    private static final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clients.put(session.getId(), session);
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received from client (" + session.getId() + "): " + payload);

        // Send acknowledgment
        session.sendMessage(new TextMessage("ACK: " + payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session.getId());
        System.out.println("Client disconnected: " + session.getId());
    }

    // Explicitly request data from a client
    public void requestData(String clientId, String request) throws Exception {
        WebSocketSession session = clients.get(clientId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage("REQUEST: " + request));
        } else {
            System.out.println("Client not connected: " + clientId);
        }
    }
}