package org.rifaii.tuum;

import org.springframework.boot.SpringApplication;

public class TestTuumApplication {

    public static void main(String[] args) {
        SpringApplication.from(TuumApplication::main)
            .with(TestcontainersConfiguration.class)
            .run(args);
    }

}
