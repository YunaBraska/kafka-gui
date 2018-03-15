<#setting date_format="dd-MM-yyyy">
<#setting locale="de_DE">
<#list messages as message>
        <li class="collection-item"><span>${message.date?datetime}</span>&nbsp;|&nbsp;<span>${message.topic}</span><br />&nbsp;->&nbsp;${message.content}</li>
</#list>