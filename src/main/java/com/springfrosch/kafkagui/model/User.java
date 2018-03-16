package com.springfrosch.kafkagui.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "session")
public class User {

    private Boolean connected = false;

    private String error = null;

    private Integer maxMessages = 5000;

    private String kafkaHost;

    private String kafkaGroupId;

    private List<String> kafkaTopics = new ArrayList<>();

    private LinkedList<Message> kafkaReceivedMessages = new LinkedList<>();

    private String kafkaTopicSelected;

    public void addMessages(LinkedList<Message> messages) {
        for (Message message : messages) {
            addMessage(message);
        }
    }

    public void addMessage(Message message) {
        if (kafkaReceivedMessages.size() > maxMessages) {
            kafkaReceivedMessages.removeFirst();
        }
        kafkaReceivedMessages.addLast(message);
    }

    public List<Message> getKafkaReceivedMessages(String topic) {
        return getKafkaReceivedMessages().stream()
                .filter(message -> message.getTopic().equals(topic))
                .collect(Collectors.toList());
    }

    public String getKafkaHost() {
        return kafkaHost;
    }

    public void setKafkaHost(String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }

    public String getKafkaGroupId() {
        return kafkaGroupId;
    }

    public void setKafkaGroupId(String kafkaGroupId) {
        this.kafkaGroupId = kafkaGroupId;
    }

    public List<String> getKafkaTopics() {
        return kafkaTopics;
    }

    public void setKafkaTopics(List<String> kafkaTopics) {
        this.kafkaTopics = kafkaTopics;
    }

    public String getKafkaTopicSelected() {
        return kafkaTopicSelected;
    }

    public void setKafkaTopicSelected(String kafkaTopicSelected) {
        this.kafkaTopicSelected = kafkaTopicSelected;
    }

    public LinkedList<Message> getKafkaReceivedMessages() {
        return kafkaReceivedMessages;
    }

    public void setKafkaReceivedMessages(LinkedList<Message> kafkaReceivedMessages) {
        this.kafkaReceivedMessages = kafkaReceivedMessages;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Integer getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(Integer maxMessages) {
        this.maxMessages = maxMessages;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
