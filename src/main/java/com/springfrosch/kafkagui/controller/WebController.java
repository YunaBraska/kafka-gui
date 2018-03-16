package com.springfrosch.kafkagui.controller;

import com.springfrosch.kafkagui.logic.KafkaService;
import com.springfrosch.kafkagui.model.Message;
import com.springfrosch.kafkagui.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Value("${kafkagui.default.maxmessages:5000}")
    private Integer DEFAULT_MAX_MESSAGES;

    @Value("${kafkagui.default.displaymessages:256}")
    private Integer DEFAULT_MAX_DISPLAY_MESSAGES;

    @Value("${kafkagui.default.host:localhost:9092}")
    private String DEFAULT_HOST;

    @Value("${kafkagui.default.topic:defaultTopic}")
    private String DEFAULT_TOPIC;

    @Value("${kafkagui.default.groupid:#{defaults.getHostname()}}")
    private String DEFAULT_GROUP_ID;

    @Value("${kafkagui.default.autoconnect:false}")
    private Boolean AUTO_CONNECT;

    @Autowired
    private User user;

    @Autowired
    private KafkaService kafkaService;

    @GetMapping("/")
    public String index() {
        return "redirect:/setup";
    }

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    @GetMapping(value = "/setup")
    public String setupInit(Model model) {
        LOG.info("Initialing Session");
        user.setKafkaHost(DEFAULT_HOST);
        user.setKafkaTopicSelected(DEFAULT_TOPIC);
        user.setKafkaGroupId(DEFAULT_GROUP_ID);
        user.setMaxMessages(DEFAULT_MAX_MESSAGES);
        if (AUTO_CONNECT) {
            connectToKafka();
        }
        model.addAttribute("user", user);
        return "setup";
    }

    @PostMapping("/setup")
    public String setupAction(Model model, @RequestParam Map<String, String> params) {
        user.setKafkaHost(params.get("kafkaHost"));
        user.setKafkaGroupId(params.get("kafkaGroupId"));
        user.setKafkaTopicSelected(params.get("kafkaTopicSelected"));

        connectToKafka();
        model.addAttribute("user", user);
        return "setup";
    }

    @RequestMapping(value = "/receive/message", method = RequestMethod.GET)
    public String receiveMessage(Model model) {
        user.addMessages(kafkaService.receiveMessages());

        List<Message> messageList =  user.getKafkaReceivedMessages(user.getKafkaTopicSelected());
        Collections.reverse(messageList);
        messageList = messageList.stream().limit(DEFAULT_MAX_DISPLAY_MESSAGES).collect(Collectors.toList());

        model.addAttribute("messages", messageList);
        model.addAttribute("kafkaTopicSelected", user.getKafkaTopicSelected());
        return "message";
    }

    @RequestMapping(value = "/post/message", method = RequestMethod.POST)
    public String postMessage(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("messageSend", kafkaService.sendMessage(params.get("produce_message")));
        return "response";
    }

    @RequestMapping(value = "/post/topic", method = RequestMethod.POST)
    public String postTopic(Model model, @RequestParam Map<String, String> params) {
        if (!params.isEmpty() && setNewTopic(params.keySet().iterator().next())) {
            connectToKafka();
        }
        model.addAttribute("kafkaTopicSelected", user.getKafkaTopicSelected());
        return "response";
    }

    private void connectToKafka() {
        user.setConnected(kafkaService.connect(user.getKafkaHost(), user.getKafkaGroupId(), user.getKafkaTopicSelected()));
        user.setError(kafkaService.getError());
        if (user.getConnected()) {
            user.setKafkaTopics(new ArrayList<>(kafkaService.consumer().listTopics().keySet()));
        } else {
            user.setKafkaTopics(new ArrayList<>());
        }
    }

    private boolean setNewTopic(String newTopic) {
        if(newTopic != null && !newTopic.isEmpty() && !newTopic.equals(user.getKafkaTopicSelected())){
            user.setKafkaTopicSelected(newTopic);
            return true;
        }
        return false;
    }

    @Component("defaults")
    public class Defaults {
        public String getHostname() {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                return UUID.randomUUID().toString();
            }
        }
    }


}
