package org.ishareReading.bankai.service.memory;

import lombok.Builder;
import lombok.Data;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tokenizer.TokenCountEstimator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
public class MultiLevelMemory implements ChatMemory {
    // 用于存储会话的摘要
    private final Map<Long, String> sessionSummaries = new ConcurrentHashMap<>();
    private final PostgresChatMemory longTermMemory;    // 长期记忆（PostgreSQL）
    private final PostgresChatMemory shortTermMemory;   // 短期记忆（PostgreSQL）
    private final InMemoryChatMemory workingMemory;     // 工作记忆（内存）
    private final TokenCountEstimator tokenEstimator;   // Token计数器
    private final int maxTokens;                        // 最大Token数
    private final MemoryStrategy strategy;              // 记忆策略
    private ChatModel chatModel;                  // 用于生成摘要的模型

    @Override
    public void add(String conversationId, List<Message> messages) {
        Long sessionIdLong = Long.parseLong(conversationId);
        workingMemory.add(conversationId, messages);
        shortTermMemory.add(conversationId, messages);
        longTermMemory.add(conversationId, messages);
        handleTokenLimit(sessionIdLong);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        Long sessionIdLong = Long.parseLong(conversationId);
        List<Message> messages = new ArrayList<>();
        messages.addAll(workingMemory.get(conversationId, lastN));
        messages.addAll(shortTermMemory.get(conversationId, lastN));
        if (strategy == MemoryStrategy.SUMMARIZATION) {
            String summary = sessionSummaries.get(sessionIdLong);
            if (summary != null) {
                messages.add(new SystemMessage(summary));
            }
        } else {
            messages.addAll(longTermMemory.get(conversationId, lastN));
        }

        return messages;
    }

    @Override
    public void clear(String conversationId) {
        Long sessionIdLong = Long.parseLong(conversationId);
        workingMemory.clear(conversationId);
        shortTermMemory.clear(conversationId);
        longTermMemory.clear(conversationId);
        sessionSummaries.remove(sessionIdLong);
    }

    /**
     * 处理Token限制
     */
    private void handleTokenLimit(Long sessionId) {
        List<Message> allMessages = get(sessionId.toString(), 0);  // 0表示获取所有消息
        int totalTokens = estimateTokens(allMessages);

        if (totalTokens > maxTokens) {
            switch (strategy) {
                case SLIDING_WINDOW -> handleSlidingWindow(sessionId, allMessages);
                case SUMMARIZATION -> handleSummarization(sessionId, allMessages);
                case NONE -> {
                    // 不做任何处理
                }
            }
        }
    }

    /**
     * 处理滑动窗口策略
     */
    private void handleSlidingWindow(Long sessionId, List<Message> messages) {
        // 移除最旧的消息，直到Token数在限制内
        while (estimateTokens(messages) > maxTokens && !messages.isEmpty()) {
            messages.removeFirst();
        }
        // 更新记忆
        clear(sessionId.toString());
        add(sessionId.toString(), messages);
    }

    /**
     * 处理摘要策略
     */
    private void handleSummarization(Long sessionId, List<Message> messages) {
        // 生成摘要
        String summary = generateSummary(messages);
        sessionSummaries.put(sessionId, summary);

        // 清空旧消息
        clear(sessionId.toString());

        // 添加摘要作为系统消息
        add(sessionId.toString(), List.of(new SystemMessage(summary)));
    }

    /**
     * 生成摘要
     */
    private String generateSummary(List<Message> messages) {
        // 构建摘要提示
        String promptTemplate = """
                请对以下对话内容进行摘要，保留关键信息：
                {messages}
                """;

        SystemPromptTemplate template = new SystemPromptTemplate(promptTemplate);
        Prompt prompt = template.create(Map.of("messages", messages));
        // 使用AI模型生成摘要
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    /**
     * 估算Token数量
     */
    private int estimateTokens(List<Message> messages) {
        return messages.stream()
                .mapToInt(message -> tokenEstimator.estimate(message.getText()))
                .sum();
    }
} 