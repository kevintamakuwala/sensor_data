package org.example.sensor_server.controllers;

import java.util.logging.Logger;

import org.example.sensor_server.dtos.SensorDataRequest;
import org.example.sensor_server.entities.Sensor;
import org.example.sensor_server.services.SensorService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SensorDataController {

    private final SensorService sensorService;
    private final SimpMessagingTemplate template;
    Logger logger = Logger.getLogger(SensorDataController.class.getName());

    public SensorDataController(SensorService sensorService, SimpMessagingTemplate template) {
        this.sensorService = sensorService;
        this.template = template;
    }

    @Async
    @MessageMapping("/data")
    @SendTo("/topic/sensor-updates")
    public Sensor receiveSensorData(SensorDataRequest data) {
        logger.info("\n\nReceived Data " + data + "\n\n");
        return sensorService.saveSensorData(data);
    }

    @Async
    @MessageMapping("/request")
    @SendTo("/topic/requests/")
    public void requestSensorData(String nodeId) {
        logger.info("\n\nRequesting data from node: " + nodeId + "\n\n");
        template.convertAndSendToUser(nodeId, "/queue/request", "Server requesting data from node: " + nodeId);
    }
}
