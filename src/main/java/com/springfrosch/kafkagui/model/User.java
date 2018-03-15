package com.springfrosch.kafkagui.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "session")
public class User {

    private Boolean init = false;

    private Integer maxMessages = 5000;

    private String kafkaHost;

    private String kafkaGroupId;

    private List<String> kafkaTopics = new ArrayList<>();

    private LinkedList<Message> kafkaReceivedMessages = new LinkedList<>();

    private String kafkaTopicSelected;

    public void addMessages(Message... messages) {
        for (Message message : messages) {
            if (kafkaReceivedMessages.size() > maxMessages) {
                kafkaReceivedMessages.removeFirst();
                kafkaReceivedMessages.add(message);
            } else {
                kafkaReceivedMessages.add(message);
            }
        }
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

    public Boolean getInit() {
        return init;
    }

    public void setInit(Boolean init) {
        this.init = init;
    }

    public Integer getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(Integer maxMessages) {
        this.maxMessages = maxMessages;
    }
}
