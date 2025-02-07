package org.example.sensor_server.repositories;

import org.springframework.stereotype.Repository;
import org.example.sensor_server.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

}
