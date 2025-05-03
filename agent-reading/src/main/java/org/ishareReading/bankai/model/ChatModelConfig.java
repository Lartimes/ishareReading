package org.ishareReading.bankai.model;

import lombok.Data;
import org.ishareReading.bankai.service.factory.ChatModelFactory.ModelType;

@Data
public class ChatModelConfig {
    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 基础URL（仅OpenAI需要）
     */
    private String baseUrl;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 模型类型
     */
    private ModelType modelType;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 最大Token数
     */
    private Integer maxTokens;

    /**
     * Top-P参数
     */
    private Double topP;

    /**
     * 频率惩罚（仅OpenAI支持）
     */
    private Double frequencyPenalty;

    /**
     * 存在惩罚（仅OpenAI支持）
     */
    private Double presencePenalty;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 描述
     */
    private String description;
} 