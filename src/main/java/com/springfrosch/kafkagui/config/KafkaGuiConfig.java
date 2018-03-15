package com.springfrosch.kafkagui.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Component
public class KafkaGuiConfig {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaGuiConfig.class);

//    @EventListener
//    public void handleContextRefresh(ContextRefreshedEvent event) {
//        initConfig();
//    }

//    @PostConstruct
    private void initConfig() {
        System.setProperty("spring.freemarker.suffix", ".ftl");
        System.setProperty("spring.freemarker.template-loader-path", "classpath:/templates");
        setDefaultProperties("kafkagui.default.host", "localhost:9092");
        setDefaultProperties("kafkagui.default.groupid", getHostname());
        setDefaultProperties("kafkagui.default.topic", "defaultTopic");
        setDefaultProperties("kafkagui.default.autoconnect", "false");
    }

    private void setDefaultProperties(final String key, final String defaultValue) {
        String property = System.getProperty(key);
        if (property == null || property.isEmpty()) {
            System.setProperty(key, defaultValue);
            LOG.info("Custom config - changing [{}] from [{}] to [{}]", key, property, defaultValue);
        } else {
            LOG.info("Default config - [{}]: [{}]", key, defaultValue);
        }
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return UUID.randomUUID().toString();
        }
    }
}
