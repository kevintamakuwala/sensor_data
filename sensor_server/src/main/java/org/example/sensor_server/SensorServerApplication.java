package org.example.sensor_server;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class SensorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SensorServerApplication.class, args);
    }

    @Bean
    CommandLineRunner run(SimpMessagingTemplate template) {
        return args -> {
            template.convertAndSend("/topic/sensor-updates", "Server started...");
        };
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
