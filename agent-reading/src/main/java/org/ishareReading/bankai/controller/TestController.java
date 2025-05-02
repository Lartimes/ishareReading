package org.ishareReading.bankai.controller;

import jakarta.annotation.Resource;
import org.ishareReading.bankai.config.DBMemoryConfig;
import org.ishareReading.bankai.memory.PostgresChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource(name = "dashscopeChatModel")
    private ChatModel chatModel;

    @Resource(name = "openAiChatModel")
    private OpenAiChatModel openaiChatModel;
    @Autowired
    private DBMemoryConfig dbMemoryConfig;

    @GetMapping("/test01")
    public void test01() {
        ChatClient chatClient = ChatClient.builder(openaiChatModel)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new PostgresChatMemory(dbMemoryConfig.getUser(),
                                dbMemoryConfig.getPassword(), dbMemoryConfig.getUrl()), "1", 20))
                .defaultOptions(OpenAiChatOptions.builder().temperature(0.7d).model("deepseek-chat").build())
                .build();
        ChatResponse chatResponse = chatClient.prompt()
                .user("我是张三😄")
                .call()
                .chatResponse();
        String string = chatResponse.getResult().toString();
        System.out.println(string);
        String content2 = chatClient.prompt("你是一个聊天助手，情绪要积极")
                .user("我是谁")
                .call()
                .content();
        System.out.println(content2);
    }
}
