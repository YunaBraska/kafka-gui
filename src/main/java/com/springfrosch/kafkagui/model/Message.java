package com.springfrosch.kafkagui.model;

import java.util.Date;
import java.util.Objects;

public class Message {

    private String content;

    private String topic;

    private Date date;

    public Message(Date date, String topic, String content) {
        this.date = date;
        this.content = content;
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(content, message.content) &&
                Objects.equals(topic, message.topic) &&
                Objects.equals(date, message.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(content, topic, date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", topic=" + topic +
                ", date=" + date +
                '}';
    }
}
