package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MulClientRunner {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executor.submit(
                    () -> {
                        try {
                            SensorClient.init();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    }
}
