<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Setup Environment</title>
    <link href="/css/main.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/css/materialize.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/js/materialize.min.js"></script>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="js/materialize.min.js"></script>
</head>
<body>

<div align="center"><label>Kafka Gui</label></div>
<table>
    <tr>
        <td style="vertical-align:bottom; width:30%;">
            <div>
                <label>Configuration</label>
                <form id="environment" name="environment" action="/setup" method="post">
                    <div class="input-field">
                        <input id="kafkaHost" name="kafkaHost" type="text" value="${user.kafkaHost}"/>
                        <label class="active" for="kafkaHost">KafkaHost</label>
                    </div>
                    <div class="input-field">
                        <input id="kafkaGroupId" name="kafkaGroupId" type="text" value="${user.kafkaGroupId}"/>
                        <label class="active" for="kafkaGroupId">KafkaGroupId</label>
                    </div>
                    <div class="input-field">
                        <input id="kafkaTopicSelected" name="kafkaTopicSelected" type="text" value="${user.kafkaTopicSelected}"/>
                        <label class="active" for="kafkaTopicSelected">KafkaTopicSelected</label>
                    </div>
                    <input class="btn waves-effect waves-light hoverable purple darken-4" type="submit" value="connect"/>
                </form>
            </div>
        </td>
        <td  style="vertical-align:bottom; width:70%;">
        <#if user.init>
            <div>
                <label for="produce_message">ProduceMessage</label>
                <textarea id="produce_message" name="produce_message" style="height: 200px;"></textarea>
            </div>
            <div>
                <input id="produce_btn" class="btn waves-effect waves-light hoverable purple darken-4" type="submit" value="send message"/>
                <script>
                    $(document).ready(function () {
                        $("#produce_btn").click(function () {
                            $.get("/post/message", $("#produce_message").serialize());
                        });
                    });
                </script>
            </div>
        </#if>
        </td>
    </tr>
    <tr>
        <td style="vertical-align:top; width:30%;">
            <#if user.init>
            <div class="collection">
                <label class="hello-title">TopicList:</label>
                <#list user.kafkaTopics as topic>
                    <div>
                        <a href="#" class="topic_btn collection-item" name="topic_btn">${topic}</a>
                    </div>
                </#list>
            </div>
            <script>
                $(document).ready(function () {
                    $(".topic_btn").click(function () {
                        $("#kafkaTopicSelected").val($(this).text());
                        $.get("/post/topic", $(this).text());
                    });
                });
            </script>
            </#if>
        </td>
        <td style="vertical-align:top; width:70%;">
            <#if user.init>
            <div>
                <label class="hello-title">Received Messages (limit 500):</label>
                <div>
                    <ul id="receivedMessages" class="collection"></ul>
                </div>
            </div>
            <script>
                function loadMessages() {
                    $('#receivedMessages').load('/receive/message');
                }

                setInterval(function () {
                    loadMessages()
                }, 1000);
            </script>
            </#if>
        </td>
    </tr>
</table>
</body>
</html>