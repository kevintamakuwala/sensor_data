package org.example.sensor_server.services;

import org.example.sensor_server.dtos.SensorDataRequest;
import org.example.sensor_server.entities.Sensor;
import org.example.sensor_server.repositories.SensorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SensorService {
    private SensorRepository sensorRepository;
    private final ModelMapper modelMapper;

    public SensorService(SensorRepository sensorRepository, ModelMapper mapper) {
        this.sensorRepository = sensorRepository;
        this.modelMapper = mapper;
    }

    public Sensor saveSensorData(SensorDataRequest data) {
        
        // Sensor sensor = modelMapper.map(data, Sensor.class);
        Sensor sensor = new Sensor(
                null,
                data.getNodeId(),
                data.getTemperature(),
                data.getHumidity(),
                data.getPressure(),
                data.getLight(),
                data.getTimestamp());
        return sensor = sensorRepository.save(sensor);
    }
}
