package com.springfrosch.kafkagui;

import com.springfrosch.kafkagui.logic.KafkaService;
import com.springfrosch.kafkagui.model.Message;
import com.springfrosch.kafkagui.util.SpringBootEmbeddedKafka;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaProducerComponentTest extends SpringBootEmbeddedKafka {

    @Autowired
    private KafkaService kafkaService;

    @Before
    public void setUp() {
        waitForEmbeddedKafka();
    }

    @Ignore
    @Test
    public void whenSendingMessage_consumerShouldReceiveIt() {
        assertThat(kafkaService.connect(HOST, GROUP_ID, DEFAULT_TOPIC), is(true));
        assertThat(kafkaService.sendMessage("testMessage"), is(true));
        assertThat(kafkaService.consumer().listTopics().keySet().contains(DEFAULT_TOPIC), is(true));
        assertThat(kafkaService.connect(HOST, GROUP_ID, DEFAULT_TOPIC), is(true));
        LinkedList<Message> messages = receiveMessages();
        assertThat(messages.size(), is(1));
        assertThat(messages.getLast().getContent(), is("testMessage"));
    }

    private LinkedList<Message> receiveMessages() {
        LinkedList<Message> messages;
        do {
            messages = kafkaService.receiveMessages(100);
        } while (messages.size() == 0);
        return messages;
    }

}