package com.springfrosch.kafkagui.util;

import com.springfrosch.kafkagui.logic.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import static org.springframework.kafka.test.rule.KafkaEmbedded.SPRING_EMBEDDED_KAFKA_BROKERS;

//@DirtiesContext(methodMode = BEFORE_METHOD) //DirtiesContext is needed to recreate a clean Kafka for each test
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "PORT=9092"})
public abstract class SpringBootEmbeddedKafka {

    protected String HOST = "localhost:9092";
    protected String DEFAULT_TOPIC = "defaultTopic";
    protected String GROUP_ID;

    {
        try {
            GROUP_ID = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    protected KafkaEmbedded kafkaEmbedded;

    protected void waitForEmbeddedKafka() {
        String topic = UUID.randomUUID().toString();
        KafkaService kafkaService = new KafkaService();
        do {
            kafkaService.connect(System.getProperty(SPRING_EMBEDDED_KAFKA_BROKERS), GROUP_ID, topic);
            kafkaService.sendMessage(UUID.randomUUID().toString());
        } while (kafkaService.receiveMessages(100).size() == 0);
        kafkaService.close();
        HOST = System.getProperty(SPRING_EMBEDDED_KAFKA_BROKERS);
    }

//    @BeforeClass
//    public static void setUpClass() {
//        //FIXME: Couldn't find kafka server configurations - kafka server is listening on a random PORT so i overwrite the client config here should be other way around
//        System.setProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
//        System.setProperty("spring.cloud.stream.kafka.binder.zkNodes", embeddedKafka.getZookeeperConnectionString());
//    }

}
