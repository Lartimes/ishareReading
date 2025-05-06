package org.ishareReading.bankai.controller;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ishareReading.bankai.config.PromptConfig;
import org.ishareReading.bankai.model.Agents;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.util.SSEUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * 默认的聊天小助手
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final PromptConfig promptConfig;
    private final PgVectorStore vectorStore;
    private final DashScopeRerankModel rerankModel;

    @Autowired
    private ChatClient dashScopeChatClient;

    public ChatController(@Qualifier("dashscopeChatModel") ChatModel chatModel, PromptConfig promptConfig, PgVectorStore vectorStore, DashScopeRerankModel rerankModel) {

        this.promptConfig = promptConfig;
        this.vectorStore = vectorStore;
        this.rerankModel = rerankModel;
    }

    @GetMapping("/getAgents")
    public Response getAgents() {
        Agents agents = new Agents();
        agents.setName("聊天机器人");
        agents.setWelcomeMessage("我是ishareReading的解忧小助手哦哦😎😎😎");
        return Response.success(agents);
    }

    @GetMapping("/chatTest")
    public Flux<ServerSentEvent<String>> chatTest(@RequestParam(value = "sessionId") String chatId,
                                                  @RequestParam("content") String content,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        String advise = """
                Context information is below.
                ---------------------
                {question_answer_context}
                ---------------------
                """;
        response.setCharacterEncoding("UTF-8");
        SearchRequest searchRequest = SearchRequest.builder().query(content).build();
        Flux<ChatResponse> flux = dashScopeChatClient.prompt(promptConfig.systemPromptTemplate().getTemplate())
                .user(content)
                .advisors(new RetrievalRerankAdvisor(vectorStore, rerankModel, searchRequest, advise, 0.1),
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory(), chatId, 20))
                .stream().chatResponse();
        return SSEUtils.result(flux);
    }


}
