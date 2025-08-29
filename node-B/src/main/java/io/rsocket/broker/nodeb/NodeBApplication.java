package io.rsocket.broker.nodeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

@SpringBootApplication
public class NodeBApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodeBApplication.class, args);
    }
}
