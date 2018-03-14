package com.springfrosch.kafkagui.util;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@DirtiesContext(methodMode = BEFORE_METHOD) //DirtiesContext is needed to recreate a clean Kafka for each test
@ContextConfiguration(classes = {SpringBootEmbeddedKafka2.TestConfiguration.class})
public abstract class SpringBootEmbeddedKafka2 {

    public final static String HOST = "localhost";
    public final static String PORT = "9092";
    public final static String GROUP_ID = UUID.randomUUID().toString();
    public final static String DEFAULT_TOPIC = "defaultTopic";

    @Configuration
    public static class TestConfiguration {

        @Bean
        private static KafkaEmbedded kafkaEmbedded() {
            KafkaEmbedded kafkaEmbedded = new KafkaEmbedded(1, true, 0);
            Map<String, String> brokerProperties = new HashMap<>();
            brokerProperties.put("listeners", "PLAINTEXT://" + HOST + ":" + PORT);
            brokerProperties.put("PORT", PORT);
            kafkaEmbedded.brokerProperties(brokerProperties);
            return kafkaEmbedded;
        }
    }

}
