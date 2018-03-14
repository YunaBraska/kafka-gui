package com.springfrosch.kafkagui;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
public class Application {
    public static void main(final String[] args) {
        run(Application.class, args);
    }
}
