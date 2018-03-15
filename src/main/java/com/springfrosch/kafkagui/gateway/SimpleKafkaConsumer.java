package com.springfrosch.kafkagui.gateway;

import com.springfrosch.kafkagui.model.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonList;

public class SimpleKafkaConsumer extends KafkaConsumer<String, String> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleKafkaConsumer.class);
    private List<String> topics;

    public SimpleKafkaConsumer(final String kafkaServers, final String groupId, final String... topics) {
        super(createConsumerConfig(kafkaServers, groupId));
        setTopics(topics);
    }

    public SimpleKafkaConsumer(final String host, final String port, final String groupId) {
        super(createConsumerConfig(host + ":" + port, groupId));
    }

    public SimpleKafkaConsumer(final String host, final String port, final String groupId, final String... topics) {
        super(createConsumerConfig(host + ":" + port, groupId));
        setTopics(topics);
    }

    public List<Message> receive() {
        return receive(new String[0]);
    }

    public List<Message> receive(final long timeoutMs) {
        return receive(timeoutMs, new String[0]);
    }

    public List<Message> receive(final String... topics) {
        return receive(10000L, topics);
    }

    public List<Message> receive(final long timeoutMs, final String... topics) {
        setTopics(topics);
        LinkedList<Message> buffer = new LinkedList<>();

        ConsumerRecords<String, String> records = poll(timeoutMs);
        for (ConsumerRecord<String, String> record : records) {
            buffer.addLast(new Message(new Date(record.timestamp()), record.topic(), record.value()));
        }

        commitSync();
        return buffer;
    }

    private void setTopics(final String... topics) {
        if (this.topics == null && (topics == null || topics.length == 0)) {
            throw new IllegalStateException("This consumer is not listening to any topic");
        } else if (topics != null && topics.length != 0) {
            this.topics = Arrays.asList(topics);
            unsubscribe();
            subscribe(this.topics);
            LOG.info("Listening on topics {}", Arrays.toString(topics));
        }
    }

    private static Map<String, Object> createConsumerConfig(final String kafkaServers, final String groupId) {
        if (kafkaServers == null || kafkaServers.contains("null")) {
            throw new RuntimeException("Invalid host [{" + kafkaServers + "}]");
        }
        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        consumerConfig.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        consumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        try {
            consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, groupId != null ? groupId : InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
            LOG.warn("Could not set group ID, falling back to random UUID [{}]" + consumerConfig.get(ConsumerConfig.GROUP_ID_CONFIG));
        }
        return consumerConfig;
    }

    //Unclear if this is the better way
    private boolean assignConsumerToTopic(final String topic, final long timeoutMs) {
        long finalTime = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < finalTime) {
            Map<String, List<PartitionInfo>> partitionInfos = listTopics();
            if (partitionInfos != null && !partitionInfos.isEmpty() && partitionInfos.get(topic) != null) {
                PartitionInfo partitionInfo = partitionInfos.get(topic).get(0);
                TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                assign(singletonList(topicPartition));
                LOG.info("Assigned to topic [{}]", topic);
                return true;
            }
            LOG.warn("Unable to assign to topic [{}]", topic);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
