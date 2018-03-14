package com.springfrosch.kafkagui.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaGuiConfig {

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        System.setProperty("spring.freemarker.suffix", ".ftl");
        System.setProperty("spring.freemarker.template-loader-path", "classpath:/templates");
    }
}
