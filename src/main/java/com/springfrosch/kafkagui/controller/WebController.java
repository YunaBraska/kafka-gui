package com.springfrosch.kafkagui.controller;

import com.springfrosch.kafkagui.gateway.SimpleKafkaConsumer;
import com.springfrosch.kafkagui.gateway.SimpleKafkaProducer;
import com.springfrosch.kafkagui.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class WebController {

    @Autowired
    private User user;

    @GetMapping("/")
    public String index() {
        return "redirect:/setup";
    }

    private static SimpleKafkaConsumer simpleKafkaConsumer;
    private static SimpleKafkaProducer simpleKafkaProducer;

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    @GetMapping(value = "/setup")
    public String setupInit(Model model, @RequestAttribute(value = "kafkaHost", required = false) String kafkaHost) {
        LOG.info("Initialing Session");
        user.setKafkaHost(kafkaHost == null ? "172.19.192.193:9092" : kafkaHost);
        user.setKafkaTopicSelected("defaultTopic");
        try {
            user.setKafkaGroupId(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            user.setKafkaGroupId(UUID.randomUUID().toString());
        }
        model.addAttribute("user", user);
        return "setup";
    }

    @PostMapping("/setup")
    public String setupAction(Model model, @RequestParam Map<String, String> params) {
//        if (user.getKafkaConsumer() == null || connectionChanged(params)) {
//            LOG.info("Create new connection");
//            createNewConnection();
//        }
        String newTopic = params.get("kafkaTopicSelected");
        String kafkaHost = params.get("kafkaHost");
        String kafkaGroupId = params.get("kafkaGroupId");
        if (hasChanged(newTopic, user.getKafkaTopicSelected())) {
            LOG.info("Change topic from [{}] to [{}]", user.getKafkaTopicSelected(), newTopic);
            user.setKafkaTopicSelected(newTopic);
            user.setKafkaReceivedMessages(new LinkedList<>());
        }
        if (hasChanged(kafkaHost, user.getKafkaHost())) {
            LOG.info("Change host from [{}] to [{}]", user.getKafkaHost(), kafkaHost);
            user.setKafkaHost(kafkaHost);
        }
        if (hasChanged(kafkaGroupId, user.getKafkaGroupId())) {
            LOG.info("Change topic from [{}] to [{}]", user.getKafkaGroupId(), kafkaGroupId);
            user.setKafkaGroupId(kafkaGroupId);
        }
        reset();
        user.setKafkaTopics(new ArrayList<>(getSimpleKafkaConsumer().listTopics().keySet()));
        user.setInit(true);
        model.addAttribute("user", user);
        return "setup";
    }

    @RequestMapping(value = "/receive/message", method = RequestMethod.GET)
    public String receiveMessage(Model model, @RequestParam Map<String, String> params) {
        if (user.getInit()) {
            List<String> messages = getSimpleKafkaConsumer().receive(100, user.getKafkaTopicSelected());
            user.addMessages(messages.toArray(new String[messages.size()]));
        }
        model.addAttribute("messages", user.getKafkaReceivedMessages());
        return "message";
    }

    @RequestMapping(value = "/post/message", method = RequestMethod.GET)
    public String postMessage(Model model, @RequestParam Map<String, String> params) {
        String sendToTopic = params.get("produce_message");
        if (sendToTopic != null && !sendToTopic.isEmpty()) {
            LOG.info("Sending message topic");
            getSimpleKafkaProducer().send(user.getKafkaTopicSelected(), new String[]{sendToTopic.replace("\n", "").replace("\r", "")});
        }
        model.addAttribute("response", user.getKafkaTopicSelected());
        return "response";
    }

    @RequestMapping(value = "/post/topic", method = RequestMethod.GET)
    public String postTopic(Model model, @RequestParam Map<String, String> params) {
        String newTopic = null;
        if (!params.isEmpty()) {
            newTopic = params.keySet().iterator().next();
        }
        if (hasChanged(newTopic, user.getKafkaTopicSelected())) {
            LOG.info("Change topic from [{}] to [{}]", user.getKafkaTopicSelected(), newTopic);
            user.setKafkaTopicSelected(newTopic);
            user.setKafkaReceivedMessages(new LinkedList<>());
            reset();
//            createNewConnection();
        } else {
            LOG.info("No topic changed [{}]", user.getKafkaTopicSelected());
        }
        model.addAttribute("response", user.getKafkaTopicSelected());
        return "response";
    }

    private void reset() {
        if (simpleKafkaConsumer != null) {
            simpleKafkaConsumer.close();
        }
        if (simpleKafkaProducer != null) {
            simpleKafkaProducer.close();
        }
        simpleKafkaConsumer = null;
        simpleKafkaProducer = null;
    }

    private SimpleKafkaConsumer getSimpleKafkaConsumer() {
        if (simpleKafkaConsumer == null) {
            simpleKafkaConsumer = new SimpleKafkaConsumer(user.getKafkaHost(), user.getKafkaGroupId(), new String[]{user.getKafkaTopicSelected()});
        }
        return simpleKafkaConsumer;
    }

    private SimpleKafkaProducer getSimpleKafkaProducer() {
        if (simpleKafkaProducer == null) {
            simpleKafkaProducer = new SimpleKafkaProducer(user.getKafkaHost(), user.getKafkaTopicSelected());
        }
        return simpleKafkaProducer;
    }

//    private void createNewConnection() {
//        SimpleKafkaConsumer kafkaConsumer = user.getKafkaConsumer();
//        SimpleKafkaProducer kafkaProducer = user.getKafkaProducer();
//        if (kafkaConsumer != null) {
//            kafkaConsumer.close();
//        }
//        if (kafkaProducer != null) {
//            kafkaProducer.close();
//        }
//        user.setKafkaConsumer(new SimpleKafkaConsumer(user.getKafkaHost(), user.getKafkaGroupId(), new String[]{user.getKafkaTopicSelected()}));
//        user.setKafkaProducer(new SimpleKafkaProducer(user.getKafkaHost(), user.getKafkaTopicSelected()));
//        user.setKafkaTopics(new ArrayList<>(user.getKafkaConsumer().listTopics().keySet()));
//    }

//    private boolean connectionChanged(Map<String, String> params) {
//        return (hasChanged(params.get("kafkaHost"), user.getKafkaHost()) || hasChanged(params.get("kafkaGroupId"), user.getKafkaGroupId()) || hasChanged(params.get("kafkaTopicSelected"), user.getKafkaTopicSelected()));
//    }

    private boolean hasChanged(String check, String against) {
        return check != null && !check.isEmpty() && !check.equals(against);
    }


}
