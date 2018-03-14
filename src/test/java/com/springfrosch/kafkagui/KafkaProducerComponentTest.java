package com.springfrosch.kafkagui;

import com.springfrosch.kafkagui.gateway.SimpleKafkaConsumer;
import com.springfrosch.kafkagui.gateway.SimpleKafkaProducer;
import com.springfrosch.kafkagui.util.SpringBootEmbeddedKafka;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaProducerComponentTest extends SpringBootEmbeddedKafka {
//public class KafkaProducerComponentTest {

//    private String HOST = "172.19.192.193";
//    private String PORT = "9092";
//    private String DEFAULT_TOPIC = "defaultTopic";
//    private String GROUP_ID = "local-YM-tester";

    //TODO: testing with embedded kafka
    private String HOST = "localhost";
    private String PORT = "9092";
    private String DEFAULT_TOPIC = "defaultTopic";
    private String GROUP_ID;

    @Before
    public void setUp() throws Exception {
        GROUP_ID = InetAddress.getLocalHost().getHostName();
    }

    @Test
    public void todo() {
        System.out.println("testing with embedded kafka");
    }

    @Ignore
    @Test
    public void sendMessage_shouldAutoCreateTopic() {
        Set<String> kafkaTopics = new SimpleKafkaConsumer(HOST, PORT, GROUP_ID, new String[]{DEFAULT_TOPIC}).listTopics().keySet();
        assertThat(kafkaTopics.contains(DEFAULT_TOPIC), is(true));
    }

    @Ignore
    @Test
    public void whenSendingMessage_consumerShouldReceiveIt() {
        SimpleKafkaProducer kafkaProducer = new SimpleKafkaProducer(HOST, PORT);
        kafkaProducer.send(DEFAULT_TOPIC, new String[]{"testMessage"});
        SimpleKafkaConsumer kafkaConsumer = new SimpleKafkaConsumer(HOST, PORT, GROUP_ID, new String[]{DEFAULT_TOPIC});
        List<String> receivedMessages = kafkaConsumer.receive(20000);
        assertThat(receivedMessages.contains("testMessage"), is(true));
    }


}