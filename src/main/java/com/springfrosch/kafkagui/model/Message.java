package com.springfrosch.kafkagui.model;

import java.util.Objects;

public class Message {

    private String content;

    private String date;

    public Message(String date, String content) {
        this.date = date;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(content, message1.content) &&
                Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(content, date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
