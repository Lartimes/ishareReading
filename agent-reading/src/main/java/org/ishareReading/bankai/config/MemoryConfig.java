package org.ishareReading.bankai.config;

import org.ishareReading.bankai.service.memory.MemoryStrategy;
import org.ishareReading.bankai.service.memory.MultiLevelMemory;
import org.ishareReading.bankai.service.memory.PostgresChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {
    private final DBMemoryConfig dbMemoryConfig;
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public MemoryConfig(DBMemoryConfig dbMemoryConfig) {
        this.dbMemoryConfig = dbMemoryConfig;
    }

    @Bean
    public TokenCountEstimator tokenCountEstimator() {
        return new JTokkitTokenCountEstimator();
    }

    @Bean
    public MultiLevelMemory multiLevelMemory(
            TokenCountEstimator tokenEstimator) {

        return MultiLevelMemory.builder()
                .longTermMemory(longTermMemory(dbMemoryConfig))
                .shortTermMemory(shortTermMemory(dbMemoryConfig))
                .workingMemory(new InMemoryChatMemory())
                .tokenEstimator(tokenEstimator)
                .maxTokens(4000)  // 设置最大Token数
                .strategy(MemoryStrategy.SUMMARIZATION)  // 默认使用摘要策略
                .chatModel(summaryModel())
                .build();
    }

    @Bean
    public PostgresChatMemory longTermMemory(DBMemoryConfig dbConfig) {
        return new PostgresChatMemory(
                dbConfig.getUser(),
                dbConfig.getPassword(),
                dbConfig.getUrl(),
                "long_term_memory"
        );
    }

    @Bean
    public PostgresChatMemory shortTermMemory(DBMemoryConfig dbConfig) {
        return new PostgresChatMemory(
                dbConfig.getUser(),
                dbConfig.getPassword(),
                dbConfig.getUrl(),
                "short_term_memory"
        );
    }

    @Bean
    public ChatModel summaryModel() {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
//                .model("deepseek-reasoner") 必须是user messages
                .model("deepseek-chat")
                .temperature(1.3)
                .maxTokens(2000)
                .topP(0.8)
                .build();
        return OpenAiChatModel.builder().openAiApi(OpenAiApi.builder()
                .apiKey(apiKey).baseUrl(baseUrl).build()).defaultOptions(options).build();
    }

}