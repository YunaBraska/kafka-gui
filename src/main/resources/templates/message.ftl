<#setting date_format="dd-MM-yyyy">
<#setting locale="de_DE">
<#--<li class="collection-item" style="opacity: 0.5; font-size: 10px;">-->
<#list messages as message>
    <#if message.topic == kafkaTopicSelected>
        <li class="collection-item"><span>${message.date?datetime}</span>&nbsp;|&nbsp;<span>${message.topic}</span><br />&nbsp;->&nbsp;${message.content}</li>
    </#if>
</#list>