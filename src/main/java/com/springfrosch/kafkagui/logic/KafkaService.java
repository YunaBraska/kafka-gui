package com.springfrosch.kafkagui.logic;

import com.springfrosch.kafkagui.gateway.SimpleKafkaConsumer;
import com.springfrosch.kafkagui.gateway.SimpleKafkaProducer;
import com.springfrosch.kafkagui.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class KafkaService {

    private SimpleKafkaConsumer simpleKafkaConsumer;
    private SimpleKafkaProducer simpleKafkaProducer;
    private String error;
    private boolean connected;
    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);

    public boolean connect(final String kafkaHost, final String kafkaGroupId, final String... topics) {
        try {
            close();
            simpleKafkaConsumer = new SimpleKafkaConsumer(kafkaHost, kafkaGroupId, topics);
            simpleKafkaProducer = new SimpleKafkaProducer(kafkaHost, topics[0]);
            LOG.info("Connected to server [{}] with topic [{}] and group [{}]", kafkaHost, topics, kafkaGroupId);
        } catch (Exception e) {
            LOG.error("Ignored error [{}]", e.getMessage(), e);
            error = e.getMessage();
            connected = false;
            return false;
        }
        connected = true;
        return true;
    }

    //TODO: implement locks
    public LinkedList<Message> receiveMessages() {
        if (isConnected()) {
            return consumer().receive(100);
        } else {
            return new LinkedList<>();
        }
    }

    public boolean sendMessage(String message) {
        if (isConnected() && message != null && !message.isEmpty()) {
            LOG.debug("Sending message [{}]", message);
            producer().send(new String[]{message.replace("\n", "").replace("\r", "")});
            return true;
        } else {
            LOG.debug("Failed to send message [{}]", message);
            return false;
        }
    }

    private void close() {
        try {
            if (simpleKafkaConsumer != null) {
                simpleKafkaConsumer.close();
            }
            if (simpleKafkaProducer != null) {
                simpleKafkaProducer.close();
            }
            simpleKafkaConsumer = null;
            simpleKafkaProducer = null;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            error = e.getMessage();
        }
    }

    public SimpleKafkaConsumer consumer() {
        return simpleKafkaConsumer;
    }

    public SimpleKafkaProducer producer() {
        return simpleKafkaProducer;
    }

    public String getError() {
        return error;
    }

    public boolean isConnected() {
        return connected;
    }
}
