### KAFKA GUI
##### Just a simple application that can send and listen on topics

##### Run
* Open terminal
* go to the folder containing the kafka-gui.jar
* run following command: "java -jar kafka-gui.jar"
* Open browser "localhost:8080"

##### Setup
* you can simply set the spring boot configurations by your own while placing a properties or yml config next to the jar file [see spring boot external config paths](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
* same thing with log configuration

##### Properties (optional)
* kafkagui.default.host <= default ip/hostname with port
* kafkagui.default.groupid <= default groupId
* kafkagui.default.topic  <= default topic
* kafkagui.default.autoconnect <= [true/false] if true it will automatically start with the default connection
* kafkagui.default.maxmessages <= max of messages to store in cache (INTEGER)
* kafkagui.default.displaymessages <= limit of messages on frontend output

##### Known issues
* its just a beta
* consumer & producer is singleton
* listen on multiple topic and other non default actions causes errors
* duplicated dependency problems
* embedded kafka in test causes errors
* no timeout at connect
* JSON formatter receiving messages missing

![screenshot](screenshot.png "screenshot")
![franz.kafka](franz.kafka.jpg "fanz.kafka")