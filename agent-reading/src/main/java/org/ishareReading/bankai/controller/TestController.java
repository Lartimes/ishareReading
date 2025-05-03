package org.ishareReading.bankai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.ishareReading.bankai.config.DBMemoryConfig;
import org.ishareReading.bankai.model.Agents;
import org.ishareReading.bankai.service.AgentsService;
import org.ishareReading.bankai.service.memory.MultiLevelMemory;
import org.ishareReading.bankai.service.memory.PostgresChatMemory;
import org.ishareReading.bankai.util.SSEUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource(name = "dashscopeChatModel")
    private ChatModel chatModel;

    @Resource(name = "openAiChatModel")
    private OpenAiChatModel openaiChatModel;
    @Autowired
    private DBMemoryConfig dbMemoryConfig;

    @Autowired
    private MultiLevelMemory multiLevelMemory;
    @Autowired
    private AgentsService agentsService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/test02")
    public Flux<ServerSentEvent<String>> test02() {

        ChatClient chatClient = ChatClient.builder(openaiChatModel)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(multiLevelMemory, "1", 20))
                .defaultOptions(OpenAiChatOptions.builder().temperature(0.7d).model("deepseek-chat").build())
                .build();
        Flux<ChatResponse> flux = chatClient.prompt()
                .user("我是张1😄")
                .stream()
                .chatResponse();
        return SSEUtils.result(flux);
    }

    @GetMapping("/test01")
    public void test01() {
        ChatClient chatClient = ChatClient.builder(openaiChatModel)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new PostgresChatMemory(dbMemoryConfig.getUser(),
                                dbMemoryConfig.getPassword(), dbMemoryConfig.getUrl())
                                , "1", 20))
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

    @GetMapping("/test03")
    public void test03() {
        Map<Agents, ChatModel> agentsByUserId = agentsService.getAgentsByUserId(1L);
//================================================================================
        ChatClient chatClient = ChatClient.builder(openaiChatModel)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new PostgresChatMemory(dbMemoryConfig.getUser(),
                                dbMemoryConfig.getPassword(), dbMemoryConfig.getUrl())
                                , "1", 20))
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

    @GetMapping("/test04")
    public SseEmitter test04() {
        String sessionId = "22";
        SseEmitter emitter = new SseEmitter();
        // 获取所有agents
        Map<Agents, ChatModel> agentsByUserId = agentsService.getAgentsByUserId(1L);
        // 创建并行流处理
        CompletableFuture.runAsync(() -> {
            try {
                // 为每个agent创建ChatClient
                List<CompletableFuture<String>> futures = agentsByUserId.entrySet().stream()
                        .map(entry -> CompletableFuture.supplyAsync(() -> {
                            try {
                                ChatClient chatClient = ChatClient.builder(entry.getValue())
                                        .defaultAdvisors(
                                                new MessageChatMemoryAdvisor(
                                                        multiLevelMemory,
                                                        sessionId,
                                                        20
                                                )
                                        )
                                        .build();

                                // 发送消息并获取响应
                                return Objects.requireNonNull(chatClient.prompt()
                                                .user("至正十一年(1351)，上天给元朝的最后一根稻草终于压了下来，元朝的末日到了。我们的谜底也揭开了。现在看来，脱脱坚决要求治黄河的愿望是好的，" +
                                                        "然而他不懂得那些反对的人的苦心，元朝那些腐败到极点的官吏也是他所不了解的。现在他终于要尝到苦果了。当元朝命令沿岸十七万劳工修河堤时，" +
                                                        "各级的官吏也异常兴奋。首先，皇帝拨给的修河工钱是可以克扣的，民工的口粮是可以克扣的" +
                                                        "，反正他们不吃不喝也事不关己，这就是一大笔收入；工程的费用也是可以克扣的，反正黄河泛滥也淹不死自己这些当官的。")
                                                .call()
                                                .chatResponse())
                                        .getResult()
                                        .toString();
                            } catch (Exception e) {
                                return "Error from " + entry.getKey().getName() + ": " + e.getMessage();
                            }
                        }))
                        .toList();

                // 等待所有响应完成
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenAccept(v -> {
                            try {
                                // 合并所有响应
                                String combinedResponse = futures.stream()
                                        .map(CompletableFuture::join)
                                        .collect(Collectors.joining("\n\n"));

                                // 发送合并后的响应
                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(combinedResponse));

                                emitter.complete();
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        System.out.println(emitter);

        return emitter;
    }

}
