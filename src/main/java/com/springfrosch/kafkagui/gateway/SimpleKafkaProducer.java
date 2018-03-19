package com.springfrosch.kafkagui.gateway;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class SimpleKafkaProducer extends KafkaProducer<String, String> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleKafkaProducer.class);

    private String topic;

    public SimpleKafkaProducer(final String kafkaServers) {
        super(createProducerConfig(kafkaServers));
    }

    public SimpleKafkaProducer(final String kafkaServers, final String topic) {
        super(createProducerConfig(kafkaServers));
        this.topic = topic;
    }

    public SimpleKafkaProducer(final String host, final String port, final String topic) {
        super(createProducerConfig(host + ":" + port));
    }

    public synchronized List<RecordMetadata> send(final String... messages) {
        return send(topic, messages);
    }

    public synchronized List<RecordMetadata> send(final String topic, final String... messages) {
        if (topic == null || topic.isEmpty()) {
            throw new IllegalStateException("This consumer is not listening to any topic");
        }
        List<RecordMetadata> recordMetadataList = new ArrayList<>();
        for (String message : messages) {
            try {
                recordMetadataList.add(send(new ProducerRecord<>(topic, message)).get());
            } catch (InterruptedException | ExecutionException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return recordMetadataList;
    }

    private static Properties createProducerConfig(final String kafkaServers) {
        if (kafkaServers == null || kafkaServers.contains("null")) {
            throw new RuntimeException("Invalid host [{" + kafkaServers + "}]");
        }
        Properties producerConfig = new Properties();
        producerConfig.put(BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        producerConfig.put(ACKS_CONFIG, "all");
        producerConfig.put(LINGER_MS_CONFIG, 1);
        producerConfig.put(BATCH_SIZE_CONFIG, 16384);
        producerConfig.put(BUFFER_MEMORY_CONFIG, 33554432);
        producerConfig.put(CLIENT_ID_CONFIG, SimpleKafkaProducer.class.getSimpleName() + "-" + System.nanoTime());
        producerConfig.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put("retries", 1);
        return producerConfig;
    }
}
