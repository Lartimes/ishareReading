package org.ishareReading.bankai.controller;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import jakarta.servlet.http.HttpServletResponse;
import org.ishareReading.bankai.config.PromptConfig;
import org.ishareReading.bankai.util.SSEUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


/**
 * 默认的聊天小助手
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient client;
    private final PromptConfig promptConfig;
    private final PgVectorStore vectorStore;
    private final DashScopeRerankModel rerankModel;

    public ChatController(@Qualifier("dashscopeChatModel") ChatModel chatModel, PromptConfig promptConfig, PgVectorStore vectorStore, DashScopeRerankModel rerankModel) {

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.client = ChatClient.builder(chatModel)
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
        this.promptConfig = promptConfig;
        this.vectorStore = vectorStore;
        this.rerankModel = rerankModel;
    }

    @GetMapping("/chatTest")
    public Flux<ServerSentEvent<String>> chatTest(@RequestParam("sessionId") String chatId,
                                                  @RequestParam("content") String content,
                                                  HttpServletResponse response) {
        String advise = """
                Context information is below.
                ---------------------
                {question_answer_context}
                ---------------------
                """;
        response.setCharacterEncoding("UTF-8");
        SearchRequest searchRequest = SearchRequest.builder().query(content).build();
        Flux<ChatResponse> flux = client.prompt(promptConfig.systemPromptTemplate().getTemplate())
                .user(content)
                .advisors(new RetrievalRerankAdvisor(vectorStore, rerankModel, searchRequest, advise, 0.1),
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .advisors(a -> {
                    a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100);
                }).stream().chatResponse();
        return SSEUtils.result(flux);
    }
}
