package org.example.sensor_server.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorDataRequest {
    private String nodeId;
    private double temperature;
    private double humidity;
    private double pressure;
    private double light;
    private LocalDateTime timestamp;
}
