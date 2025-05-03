package org.ishareReading.bankai.service.factory;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Builder;
import lombok.Data;
import org.ishareReading.bankai.model.ChatModelConfig;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

@Data
@Builder
public class ChatModelFactory {
    private String apiKey;
    private String baseUrl;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private ModelType modelType;

    /**
     * 创建默认的OpenAI规范/DASHSCOPE模型
     */
    public static ChatModel createDefaultModel(ChatModelConfig chatModelConfig) {

        return ChatModelFactory.builder()
                .apiKey(chatModelConfig.getApiKey())
                .baseUrl(chatModelConfig.getBaseUrl())
                .model(chatModelConfig.getModel())
                .temperature(chatModelConfig.getTemperature())
                .maxTokens(chatModelConfig.getMaxTokens())
                .topP(chatModelConfig.getTopP())
                .frequencyPenalty(chatModelConfig.getFrequencyPenalty())
                .presencePenalty(chatModelConfig.getPresencePenalty())
                .modelType(ModelType.OPENAI)
                .build()
                .createChatModel();
    }

    /**
     * 创建ChatModel实例
     */
    public ChatModel createChatModel() {
        return switch (modelType) {
            case OPENAI -> createOpenAiModel();
            case DASHSCOPE -> createDashScopeModel();
        };
    }

    /**
     * 创建OpenAI模型
     */
    private ChatModel createOpenAiModel() {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .topP(topP)
                .frequencyPenalty(frequencyPenalty)
                .presencePenalty(presencePenalty)
                .build();
        return OpenAiChatModel.builder().openAiApi(OpenAiApi.builder()
                .apiKey(apiKey).baseUrl(baseUrl).build()).defaultOptions(options).build();
//        return new OpenAiChatModel(apiKey, baseUrl, options);
    }

    /**
     * 创建DashScope模型
     */
    private ChatModel createDashScopeModel() {
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel(model)
                .withTemperature(temperature)
                .withMaxToken(maxTokens)
                .withTopP(topP)
                .build();
        DashScopeApi dashScopeApi = new DashScopeApi(baseUrl, apiKey);
        return new DashScopeChatModel(dashScopeApi, options);
    }

    public enum ModelType {
        OPENAI,
        DASHSCOPE,
    }

}